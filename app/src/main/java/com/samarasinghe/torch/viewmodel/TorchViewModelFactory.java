package com.samarasinghe.torch.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.samarasinghe.torch.repository.TorchRepository;

/**
 * Factory class for instantiating {@link TorchViewModel} with dependencies (Factory Pattern).
 */
public class TorchViewModelFactory implements ViewModelProvider.Factory {

    private final TorchRepository repository;

    public TorchViewModelFactory(TorchRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TorchViewModel.class)) {
            return (T) new TorchViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
