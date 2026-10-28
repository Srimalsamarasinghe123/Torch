package com.samarasinghe.torch.strategy;

import com.samarasinghe.torch.model.TorchMode;

/**
 * Factory class for creating {@link TorchModeStrategy} instances (Factory Pattern).
 */
public class TorchStrategyFactory {

    public static TorchModeStrategy getStrategy(TorchMode mode) {
        if (mode == null) {
            return new SteadyTorchStrategy();
        }
        switch (mode) {
            case SOS:
                return new SosTorchStrategy();
            case STEADY:
            case OFF:
            default:
                return new SteadyTorchStrategy();
        }
    }
}
