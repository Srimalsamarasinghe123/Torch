package com.samarasinghe.torch.repository;

import com.samarasinghe.torch.model.TorchMode;
import com.samarasinghe.torch.model.TorchState;
import com.samarasinghe.torch.observer.TorchStateObservable;

/**
 * Repository interface acting as a Facade for the Torch system domain (Facade Pattern).
 * Combines hardware state, strategy execution, and state observations into a clean unified API.
 */
public interface TorchRepository extends TorchStateObservable {

    /**
     * Gets the current immutable state of the torch.
     */
    TorchState getCurrentState();

    /**
     * Toggles steady torch light ON or OFF.
     */
    void toggleTorch();

    /**
     * Sets a specific torch mode.
     */
    void setTorchMode(TorchMode mode);

    /**
     * Sets the torch brightness level.
     *
     * @param level Brightness level between 1 and max level.
     */
    void setBrightnessLevel(int level);

    /**
     * Toggles emergency SOS mode ON or OFF.
     */
    void toggleSos();

    /**
     * Releases hardware and background resources held by the repository.
     */
    void release();
}
