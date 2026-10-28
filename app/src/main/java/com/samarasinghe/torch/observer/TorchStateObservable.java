package com.samarasinghe.torch.observer;

/**
 * Observable interface for managing TorchState observers (Observer Pattern).
 */
public interface TorchStateObservable {
    void addObserver(TorchStateObserver observer);
    void removeObserver(TorchStateObserver observer);
    void notifyObservers();
}
