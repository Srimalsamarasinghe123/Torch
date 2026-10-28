package com.samarasinghe.torch.hardware;

/**
 * Interface representing the low-level Hardware Access layer for the device Torch/Flashlight.
 * Abstraction/Adapter Pattern: Decouples domain logic from Android CameraManager framework APIs.
 */
public interface TorchHardwareController {

    interface AvailabilityCallback {
        void onTorchAvailabilityChanged(boolean available);
    }

    /**
     * Checks if a flashlight/torch feature is available on the current device.
     */
    boolean isHardwareAvailable();

    /**
     * Gets the maximum brightness level supported by the torch hardware.
     * Returns 1 if multi-level brightness control is not supported.
     */
    int getMaxBrightnessLevel();

    /**
     * Enables or disables the torch with the specified brightness level.
     *
     * @param enabled True to turn ON, false to turn OFF.
     * @param level Strength level (1 to max level).
     * @throws Exception If hardware access fails.
     */
    void setTorchMode(boolean enabled, int level) throws Exception;

    /**
     * Registers a callback listener for torch availability events.
     */
    void registerAvailabilityCallback(AvailabilityCallback callback);

    /**
     * Unregisters the torch availability callback listener.
     */
    void unregisterAvailabilityCallback();

    /**
     * Releases any hardware resources held by the controller.
     */
    void release();
}
