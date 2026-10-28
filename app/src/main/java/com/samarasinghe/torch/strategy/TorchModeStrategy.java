package com.samarasinghe.torch.strategy;

import com.samarasinghe.torch.hardware.TorchHardwareController;
import com.samarasinghe.torch.model.TorchMode;

/**
 * Strategy interface defining how the flashlight output is driven (Strategy Pattern).
 * Concrete implementations define behavior for steady light, SOS signaling, strobing, etc.
 */
public interface TorchModeStrategy {

    /**
     * Executes the strategy with the provided controller and brightness level.
     *
     * @param controller Hardware controller driving the physical torch.
     * @param level Brightness level requested.
     */
    void start(TorchHardwareController controller, int level);

    /**
     * Stops the strategy execution and turns off hardware torch.
     *
     * @param controller Hardware controller driving the physical torch.
     */
    void stop(TorchHardwareController controller);

    /**
     * Returns the {@link TorchMode} associated with this strategy.
     */
    TorchMode getMode();
}
