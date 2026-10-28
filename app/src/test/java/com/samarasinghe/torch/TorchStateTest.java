package com.samarasinghe.torch;

import com.samarasinghe.torch.model.TorchMode;
import com.samarasinghe.torch.model.TorchState;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TorchStateTest {

    @Test
    public void defaultTorchState_isOffAndAvailable() {
        TorchState state = new TorchState.Builder().build();

        assertEquals(TorchMode.OFF, state.getMode());
        assertEquals(1, state.getBrightnessLevel());
        assertEquals(1, state.getMaxBrightnessLevel());
        assertTrue(state.isAvailable());
        assertFalse(state.isTorchOn());
        assertFalse(state.isSosActive());
        assertFalse(state.hasBrightnessControl());
    }

    @Test
    public void steadyState_withBrightnessControl() {
        TorchState state = new TorchState.Builder()
                .setMode(TorchMode.STEADY)
                .setBrightnessLevel(3)
                .setMaxBrightnessLevel(5)
                .setAvailable(true)
                .build();

        assertEquals(TorchMode.STEADY, state.getMode());
        assertEquals(3, state.getBrightnessLevel());
        assertEquals(5, state.getMaxBrightnessLevel());
        assertTrue(state.isTorchOn());
        assertFalse(state.isSosActive());
        assertTrue(state.hasBrightnessControl());
    }

    @Test
    public void sosState_isSosActiveAndTorchOn() {
        TorchState state = new TorchState.Builder()
                .setMode(TorchMode.SOS)
                .build();

        assertEquals(TorchMode.SOS, state.getMode());
        assertTrue(state.isTorchOn());
        assertTrue(state.isSosActive());
    }

    @Test
    public void brightnessLevel_clampedToMax() {
        TorchState state = new TorchState.Builder()
                .setBrightnessLevel(10)
                .setMaxBrightnessLevel(4)
                .build();

        assertEquals(4, state.getBrightnessLevel());
    }
}
