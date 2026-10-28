package com.samarasinghe.torch.observer;

import com.samarasinghe.torch.model.TorchState;

/**
 * Observer interface for receiving updates on TorchState changes (Observer Pattern).
 */
public interface TorchStateObserver {
    void onTorchStateChanged(TorchState state);
}
