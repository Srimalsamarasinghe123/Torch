package com.samarasinghe.torch;

import com.samarasinghe.torch.hardware.TorchHardwareController;
import com.samarasinghe.torch.model.TorchMode;
import com.samarasinghe.torch.model.TorchState;
import com.samarasinghe.torch.observer.TorchStateObserver;
import com.samarasinghe.torch.repository.TorchRepositoryImpl;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TorchRepositoryTest {

    private FakeTorchHardwareController fakeHardware;
    private TorchRepositoryImpl repository;
    private List<TorchState> observedStates;

    @Before
    public void setUp() {
        fakeHardware = new FakeTorchHardwareController(true, 5);
        repository = new TorchRepositoryImpl(fakeHardware);
        observedStates = new ArrayList<>();

        repository.addObserver(new TorchStateObserver() {
            @Override
            public void onTorchStateChanged(TorchState state) {
                observedStates.add(state);
            }
        });
    }

    @Test
    public void initialState_isOffWithCorrectMaxBrightness() {
        TorchState state = repository.getCurrentState();
        assertEquals(TorchMode.OFF, state.getMode());
        assertEquals(5, state.getMaxBrightnessLevel());
        assertTrue(state.isAvailable());
    }

    @Test
    public void toggleTorch_switchesBetweenOffAndSteady() {
        repository.toggleTorch();
        assertEquals(TorchMode.STEADY, repository.getCurrentState().getMode());
        assertTrue(fakeHardware.isTorchEnabled());

        repository.toggleTorch();
        assertEquals(TorchMode.OFF, repository.getCurrentState().getMode());
        assertFalse(fakeHardware.isTorchEnabled());
    }

    @Test
    public void toggleSos_switchesToSosMode() {
        repository.toggleSos();
        assertEquals(TorchMode.SOS, repository.getCurrentState().getMode());

        repository.toggleSos();
        assertEquals(TorchMode.OFF, repository.getCurrentState().getMode());
    }

    @Test
    public void setBrightnessLevel_updatesStateAndHardware() {
        repository.setTorchMode(TorchMode.STEADY);
        repository.setBrightnessLevel(3);

        assertEquals(3, repository.getCurrentState().getBrightnessLevel());
        assertEquals(3, fakeHardware.getCurrentLevel());
    }

    private static class FakeTorchHardwareController implements TorchHardwareController {
        private boolean available;
        private int maxBrightness;
        private boolean torchEnabled = false;
        private int currentLevel = 1;

        public FakeTorchHardwareController(boolean available, int maxBrightness) {
            this.available = available;
            this.maxBrightness = maxBrightness;
        }

        @Override
        public boolean isHardwareAvailable() {
            return available;
        }

        @Override
        public int getMaxBrightnessLevel() {
            return maxBrightness;
        }

        @Override
        public void setTorchMode(boolean enabled, int level) {
            this.torchEnabled = enabled;
            this.currentLevel = level;
        }

        @Override
        public void registerAvailabilityCallback(AvailabilityCallback callback) {}

        @Override
        public void unregisterAvailabilityCallback() {}

        @Override
        public void release() {}

        public boolean isTorchEnabled() {
            return torchEnabled;
        }

        public int getCurrentLevel() {
            return currentLevel;
        }
    }
}
