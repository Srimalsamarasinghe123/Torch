package com.samarasinghe.torch.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import com.samarasinghe.torch.R;
import com.samarasinghe.torch.hardware.TorchHardwareController;
import com.samarasinghe.torch.hardware.TorchHardwareControllerFactory;
import com.samarasinghe.torch.model.TorchMode;
import com.samarasinghe.torch.model.TorchState;
import com.samarasinghe.torch.repository.TorchRepository;
import com.samarasinghe.torch.repository.TorchRepositoryImpl;
import com.samarasinghe.torch.viewmodel.TorchViewModel;
import com.samarasinghe.torch.viewmodel.TorchViewModelFactory;

/**
 * Main Activity UI View (MVVM View / Single Responsibility Principle).
 * Responsibilities are strictly limited to UI inflation, view binding, and delegating user input events to TorchViewModel.
 */
public class MainActivity extends AppCompatActivity {

    private ToggleButton toggleButton;
    private Button btnSOS;
    private SeekBar seekBarBrightness;

    private TorchViewModel viewModel;
    private boolean isUpdatingUiFromState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle splash screen initialization
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupViewModel();
        observeViewModel();
        setupListeners();
    }

    private void initViews() {
        toggleButton = findViewById(R.id.btnFlashlight);
        btnSOS = findViewById(R.id.btnSOS);
        seekBarBrightness = findViewById(R.id.seekBarBrightness);
    }

    private void setupViewModel() {
        TorchHardwareController hardwareController = TorchHardwareControllerFactory.createHardwareController(this);
        TorchRepository repository = new TorchRepositoryImpl(hardwareController);
        TorchViewModelFactory factory = new TorchViewModelFactory(repository);

        viewModel = new ViewModelProvider(this, factory).get(TorchViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getTorchState().observe(this, this::renderState);
    }

    private void setupListeners() {
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUpdatingUiFromState) return;
            viewModel.toggleTorch();
        });

        btnSOS.setOnClickListener(v -> viewModel.toggleSos());

        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && !isUpdatingUiFromState) {
                    viewModel.setBrightnessLevel(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    /**
     * Renders the current {@link TorchState} reactively.
     */
    private void renderState(TorchState state) {
        if (state == null) return;

        isUpdatingUiFromState = true;

        try {
            if (!state.isAvailable()) {
                toggleButton.setEnabled(false);
                btnSOS.setEnabled(false);
                seekBarBrightness.setEnabled(false);
                Toast.makeText(this, R.string.camera_not_accessible, Toast.LENGTH_SHORT).show();
                return;
            }

            toggleButton.setEnabled(true);
            btnSOS.setEnabled(true);

            // Toggle button reflects state (ON in STEADY mode, OFF during SOS or OFF mode)
            toggleButton.setChecked(state.getMode() == TorchMode.STEADY);

            // SOS button UI text
            if (state.isSosActive()) {
                btnSOS.setText(R.string.stop_sos);
            } else {
                btnSOS.setText(R.string.sos_pattern);
            }

            // SeekBar Brightness setup
            if (state.hasBrightnessControl()) {
                seekBarBrightness.setEnabled(true);
                seekBarBrightness.setMax(state.getMaxBrightnessLevel());
                if (seekBarBrightness.getProgress() != state.getBrightnessLevel()) {
                    seekBarBrightness.setProgress(state.getBrightnessLevel());
                }
            } else {
                seekBarBrightness.setEnabled(false);
            }

            if (state.getErrorMessage() != null) {
                Toast.makeText(this, state.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        } finally {
            isUpdatingUiFromState = false;
        }
    }
}
