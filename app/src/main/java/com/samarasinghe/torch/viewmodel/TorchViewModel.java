package com.samarasinghe.torch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.samarasinghe.torch.model.TorchMode;
import com.samarasinghe.torch.model.TorchState;
import com.samarasinghe.torch.observer.TorchStateObserver;
import com.samarasinghe.torch.repository.TorchRepository;

/**
 * Jetpack ViewModel managing UI state for Torch application (MVVM Pattern).
 * Bridges domain layer repository and presentation UI views reactively via LiveData.
 */
public class TorchViewModel extends ViewModel {

    private final TorchRepository repository;
    private final MutableLiveData<TorchState> torchStateLiveData = new MutableLiveData<>();
    private final TorchStateObserver stateObserver;

    public TorchViewModel(TorchRepository repository) {
        this.repository = repository;
        this.stateObserver = torchStateLiveData::postValue;

        this.repository.addObserver(stateObserver);
    }

    public LiveData<TorchState> getTorchState() {
        return torchStateLiveData;
    }

    public void toggleTorch() {
        repository.toggleTorch();
    }

    public void toggleSos() {
        repository.toggleSos();
    }

    public void setBrightnessLevel(int level) {
        repository.setBrightnessLevel(level);
    }

    public void turnOffTorch() {
        repository.setTorchMode(TorchMode.OFF);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.removeObserver(stateObserver);
        repository.release();
    }
}
