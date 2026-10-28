package com.samarasinghe.torch.repository;

import com.samarasinghe.torch.hardware.TorchHardwareController;
import com.samarasinghe.torch.model.TorchMode;
import com.samarasinghe.torch.model.TorchState;
import com.samarasinghe.torch.observer.TorchStateObserver;
import com.samarasinghe.torch.strategy.TorchModeStrategy;
import com.samarasinghe.torch.strategy.TorchStrategyFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Concrete implementation of {@link TorchRepository} (Facade Pattern & Observer Pattern).
 */
public class TorchRepositoryImpl implements TorchRepository {

    private final TorchHardwareController hardwareController;
    private final List<TorchStateObserver> observers = new CopyOnWriteArrayList<>();

    private TorchState currentState;
    private TorchModeStrategy currentStrategy;

    public TorchRepositoryImpl(TorchHardwareController hardwareController) {
        this.hardwareController = hardwareController;

        boolean available = hardwareController.isHardwareAvailable();
        int maxBrightness = hardwareController.getMaxBrightnessLevel();

        this.currentState = new TorchState.Builder()
                .setMode(TorchMode.OFF)
                .setBrightnessLevel(maxBrightness)
                .setMaxBrightnessLevel(maxBrightness)
                .setAvailable(available)
                .build();

        this.hardwareController.registerAvailabilityCallback(available1 -> {
            updateState(currentState.toBuilder().setAvailable(available1).build());
            if (!available1 && currentState.isTorchOn()) {
                setTorchMode(TorchMode.OFF);
            }
        });
    }

    @Override
    public synchronized TorchState getCurrentState() {
        return currentState;
    }

    @Override
    public synchronized void toggleTorch() {
        if (!currentState.isAvailable()) return;

        if (currentState.isTorchOn()) {
            setTorchMode(TorchMode.OFF);
        } else {
            setTorchMode(TorchMode.STEADY);
        }
    }

    @Override
    public synchronized void setTorchMode(TorchMode targetMode) {
        TorchMode mode = (targetMode != null) ? targetMode : TorchMode.OFF;

        if (!currentState.isAvailable() && mode != TorchMode.OFF) {
            return;
        }

        // Stop current active strategy if running
        if (currentStrategy != null) {
            currentStrategy.stop(hardwareController);
            currentStrategy = null;
        }

        if (mode == TorchMode.OFF) {
            updateState(currentState.toBuilder().setMode(TorchMode.OFF).build());
        } else {
            currentStrategy = TorchStrategyFactory.getStrategy(mode);
            currentStrategy.start(hardwareController, currentState.getBrightnessLevel());
            updateState(currentState.toBuilder().setMode(mode).build());
        }
    }

    @Override
    public synchronized void setBrightnessLevel(int level) {
        int validLevel = Math.max(1, Math.min(level, currentState.getMaxBrightnessLevel()));
        updateState(currentState.toBuilder().setBrightnessLevel(validLevel).build());

        // If torch is currently active in STEADY mode, apply new brightness immediately
        if (currentState.getMode() == TorchMode.STEADY && currentStrategy != null) {
            currentStrategy.start(hardwareController, validLevel);
        }
    }

    @Override
    public synchronized void toggleSos() {
        if (!currentState.isAvailable()) return;

        if (currentState.isSosActive()) {
            setTorchMode(TorchMode.OFF);
        } else {
            setTorchMode(TorchMode.SOS);
        }
    }

    @Override
    public void addObserver(TorchStateObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
            observer.onTorchStateChanged(getCurrentState());
        }
    }

    @Override
    public void removeObserver(TorchStateObserver observer) {
        if (observer != null) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers() {
        TorchState snapshot = getCurrentState();
        for (TorchStateObserver observer : observers) {
            observer.onTorchStateChanged(snapshot);
        }
    }

    private void updateState(TorchState newState) {
        if (!Objects.equals(this.currentState, newState)) {
            this.currentState = newState;
            notifyObservers();
        }
    }

    @Override
    public synchronized void release() {
        if (currentStrategy != null) {
            currentStrategy.stop(hardwareController);
            currentStrategy = null;
        }
        hardwareController.release();
        observers.clear();
    }
}
