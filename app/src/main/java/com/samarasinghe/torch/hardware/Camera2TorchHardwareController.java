package com.samarasinghe.torch.hardware;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Android Camera2 API implementation of {@link TorchHardwareController}.
 * Encapsulates Android-specific Camera hardware details and API level checks.
 */
public class Camera2TorchHardwareController implements TorchHardwareController {

    private static final String TAG = "Camera2TorchController";

    private final CameraManager cameraManager;
    private String cameraId;
    private int maxBrightnessLevel = 1;
    private boolean isAvailable = false;
    private AvailabilityCallback availabilityCallback;
    private CameraManager.TorchCallback torchCallback;

    public Camera2TorchHardwareController(Context context) {
        this.cameraManager = (CameraManager) context.getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
        initCamera();
    }

    private void initCamera() {
        if (cameraManager == null) {
            Log.e(TAG, "CameraManager service is unavailable on this device.");
            return;
        }

        try {
            String[] cameraIds = cameraManager.getCameraIdList();
            if (cameraIds.length > 0) {
                this.cameraId = cameraIds[0];
                this.isAvailable = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                    Integer maxLevel = characteristics.get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL);
                    if (maxLevel != null && maxLevel > 1) {
                        this.maxBrightnessLevel = maxLevel;
                    }
                }
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Failed to initialize camera for torch hardware controller", e);
            this.isAvailable = false;
        }
    }

    @Override
    public boolean isHardwareAvailable() {
        return isAvailable && cameraId != null;
    }

    @Override
    public int getMaxBrightnessLevel() {
        return maxBrightnessLevel;
    }

    @Override
    public void setTorchMode(boolean enabled, int level) throws CameraAccessException {
        if (cameraId == null) {
            throw new CameraAccessException(CameraAccessException.CAMERA_DISCONNECTED, "No accessible camera found");
        }

        if (enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && maxBrightnessLevel > 1) {
                int validLevel = Math.min(Math.max(1, level), maxBrightnessLevel);
                cameraManager.turnOnTorchWithStrengthLevel(cameraId, validLevel);
            } else {
                cameraManager.setTorchMode(cameraId, true);
            }
        } else {
            cameraManager.setTorchMode(cameraId, false);
        }
    }

    @Override
    public void registerAvailabilityCallback(AvailabilityCallback callback) {
        this.availabilityCallback = callback;
        if (cameraManager != null && torchCallback == null) {
            torchCallback = new CameraManager.TorchCallback() {
                @Override
                public void onTorchModeUnavailable(String id) {
                    if (id.equals(cameraId)) {
                        isAvailable = false;
                        if (availabilityCallback != null) {
                            availabilityCallback.onTorchAvailabilityChanged(false);
                        }
                    }
                }

                @Override
                public void onTorchModeChanged(String id, boolean enabled) {
                    if (id.equals(cameraId)) {
                        isAvailable = true;
                        if (availabilityCallback != null) {
                            availabilityCallback.onTorchAvailabilityChanged(true);
                        }
                    }
                }
            };
            cameraManager.registerTorchCallback(torchCallback, new Handler(Looper.getMainLooper()));
        }
    }

    @Override
    public void unregisterAvailabilityCallback() {
        if (cameraManager != null && torchCallback != null) {
            cameraManager.unregisterTorchCallback(torchCallback);
            torchCallback = null;
        }
        this.availabilityCallback = null;
    }

    @Override
    public void release() {
        unregisterAvailabilityCallback();
    }
}
