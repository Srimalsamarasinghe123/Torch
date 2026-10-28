package com.samarasinghe.torch.model;

import java.util.Objects;

/**
 * Immutable representation of the torch application state.
 * Implements the Builder Pattern for construction and immutability.
 */
public final class TorchState {

    private final TorchMode mode;
    private final int brightnessLevel;
    private final int maxBrightnessLevel;
    private final boolean isAvailable;
    private final String errorMessage;

    private TorchState(Builder builder) {
        this.mode = builder.mode;
        this.brightnessLevel = builder.brightnessLevel;
        this.maxBrightnessLevel = builder.maxBrightnessLevel;
        this.isAvailable = builder.isAvailable;
        this.errorMessage = builder.errorMessage;
    }

    public TorchMode getMode() {
        return mode;
    }

    public int getBrightnessLevel() {
        return brightnessLevel;
    }

    public int getMaxBrightnessLevel() {
        return maxBrightnessLevel;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean isTorchOn() {
        return mode == TorchMode.STEADY || mode == TorchMode.SOS;
    }

    public boolean isSosActive() {
        return mode == TorchMode.SOS;
    }

    public boolean hasBrightnessControl() {
        return maxBrightnessLevel > 1;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Builder toBuilder() {
        return new Builder()
                .setMode(this.mode)
                .setBrightnessLevel(this.brightnessLevel)
                .setMaxBrightnessLevel(this.maxBrightnessLevel)
                .setAvailable(this.isAvailable)
                .setErrorMessage(this.errorMessage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TorchState that = (TorchState) o;
        return brightnessLevel == that.brightnessLevel &&
                maxBrightnessLevel == that.maxBrightnessLevel &&
                isAvailable == that.isAvailable &&
                mode == that.mode &&
                Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode, brightnessLevel, maxBrightnessLevel, isAvailable, errorMessage);
    }

    @Override
    public String toString() {
        return "TorchState{" +
                "mode=" + mode +
                ", brightnessLevel=" + brightnessLevel +
                ", maxBrightnessLevel=" + maxBrightnessLevel +
                ", isAvailable=" + isAvailable +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

    /**
     * Builder for {@link TorchState}.
     */
    public static class Builder {
        private TorchMode mode = TorchMode.OFF;
        private int brightnessLevel = 1;
        private int maxBrightnessLevel = 1;
        private boolean isAvailable = true;
        private String errorMessage = null;

        public Builder() {}

        public Builder setMode(TorchMode mode) {
            this.mode = mode != null ? mode : TorchMode.OFF;
            return this;
        }

        public Builder setBrightnessLevel(int brightnessLevel) {
            this.brightnessLevel = Math.max(1, brightnessLevel);
            return this;
        }

        public Builder setMaxBrightnessLevel(int maxBrightnessLevel) {
            this.maxBrightnessLevel = Math.max(1, maxBrightnessLevel);
            return this;
        }

        public Builder setAvailable(boolean available) {
            isAvailable = available;
            return this;
        }

        public Builder setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public TorchState build() {
            if (this.brightnessLevel > this.maxBrightnessLevel) {
                this.brightnessLevel = this.maxBrightnessLevel;
            }
            return new TorchState(this);
        }
    }
}
