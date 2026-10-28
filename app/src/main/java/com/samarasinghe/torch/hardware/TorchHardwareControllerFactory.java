package com.samarasinghe.torch.hardware;

import android.content.Context;

/**
 * Factory class for creating instances of {@link TorchHardwareController} (Factory Pattern).
 * Enables easy substitution of fake/mock hardware controllers during testing or future device variants.
 */
public class TorchHardwareControllerFactory {

    public static TorchHardwareController createHardwareController(Context context) {
        return new Camera2TorchHardwareController(context);
    }
}
