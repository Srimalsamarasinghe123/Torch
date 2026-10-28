package com.samarasinghe.torch.strategy;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.samarasinghe.torch.hardware.TorchHardwareController;
import com.samarasinghe.torch.model.TorchMode;

/**
 * Strategy implementation for emergency SOS Morse code signaling (... --- ...).
 * Encapsulates timing patterns and async loop execution cleanly away from UI components.
 */
public class SosTorchStrategy implements TorchModeStrategy {

    private static final String TAG = "SosTorchStrategy";

    // SOS pattern delays: 3 short (200ms), 3 long (600ms), 3 short (200ms)
    private static final long[] PATTERN = {
        200, 200, 200, 200, 200, 200, // S: ...
        600, 200, 600, 200, 600, 200, // O: ---
        200, 200, 200, 200, 200, 1000 // S: ...
    };

    private final Handler handler;
    private boolean isRunning = false;
    private int stepIndex = 0;
    private Runnable sosRunnable;

    public SosTorchStrategy() {
        this(null);
    }

    public SosTorchStrategy(Handler handler) {
        this.handler = handler;
    }

    private Handler getHandler() {
        if (handler != null) {
            return handler;
        }
        try {
            return new Handler(Looper.getMainLooper());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void start(final TorchHardwareController controller, final int level) {
        stop(controller); // Ensure previous instance is stopped

        if (controller == null) return;

        isRunning = true;
        stepIndex = 0;

        final Handler activeHandler = getHandler();

        sosRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isRunning) return;

                boolean torchOn = (stepIndex % 2 == 0);
                try {
                    controller.setTorchMode(torchOn, level);
                } catch (Exception e) {
                    Log.e(TAG, "Error toggling torch state during SOS pattern", e);
                }

                long delay = PATTERN[stepIndex];
                stepIndex = (stepIndex + 1) % PATTERN.length;

                if (activeHandler != null) {
                    activeHandler.postDelayed(this, delay);
                }
            }
        };

        if (activeHandler != null) {
            activeHandler.post(sosRunnable);
        } else {
            // Fallback for non-Android unit testing environment
            sosRunnable.run();
        }
    }

    @Override
    public void stop(TorchHardwareController controller) {
        isRunning = false;
        Handler activeHandler = getHandler();
        if (sosRunnable != null && activeHandler != null) {
            activeHandler.removeCallbacks(sosRunnable);
            sosRunnable = null;
        }
        if (controller != null) {
            try {
                controller.setTorchMode(false, 1);
            } catch (Exception e) {
                Log.e(TAG, "Error turning off torch on SOS stop", e);
            }
        }
    }

    @Override
    public TorchMode getMode() {
        return TorchMode.SOS;
    }
}
