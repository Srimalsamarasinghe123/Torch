package com.samarasinghe.torch.strategy;

import android.util.Log;
import com.samarasinghe.torch.hardware.TorchHardwareController;
import com.samarasinghe.torch.model.TorchMode;

/**
 * Strategy implementation for continuous steady torch light.
 */
public class SteadyTorchStrategy implements TorchModeStrategy {

    private static final String TAG = "SteadyTorchStrategy";

    @Override
    public void start(TorchHardwareController controller, int level) {
        if (controller == null) return;
        try {
            controller.setTorchMode(true, level);
        } catch (Exception e) {
            Log.e(TAG, "Failed to start steady torch strategy", e);
        }
    }

    @Override
    public void stop(TorchHardwareController controller) {
        if (controller == null) return;
        try {
            controller.setTorchMode(false, 1);
        } catch (Exception e) {
            Log.e(TAG, "Failed to stop steady torch strategy", e);
        }
    }

    @Override
    public TorchMode getMode() {
        return TorchMode.STEADY;
    }
}
