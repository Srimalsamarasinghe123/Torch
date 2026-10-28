package com.samarasinghe.torch;

import com.samarasinghe.torch.model.TorchMode;
import com.samarasinghe.torch.strategy.SosTorchStrategy;
import com.samarasinghe.torch.strategy.SteadyTorchStrategy;
import com.samarasinghe.torch.strategy.TorchModeStrategy;
import com.samarasinghe.torch.strategy.TorchStrategyFactory;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TorchStrategyFactoryTest {

    @Test
    public void getStrategy_returnsSteadyStrategyForOffAndSteady() {
        TorchModeStrategy offStrategy = TorchStrategyFactory.getStrategy(TorchMode.OFF);
        assertTrue(offStrategy instanceof SteadyTorchStrategy);

        TorchModeStrategy steadyStrategy = TorchStrategyFactory.getStrategy(TorchMode.STEADY);
        assertTrue(steadyStrategy instanceof SteadyTorchStrategy);
    }

    @Test
    public void getStrategy_returnsSosStrategyForSosMode() {
        TorchModeStrategy sosStrategy = TorchStrategyFactory.getStrategy(TorchMode.SOS);
        assertTrue(sosStrategy instanceof SosTorchStrategy);
    }
}
