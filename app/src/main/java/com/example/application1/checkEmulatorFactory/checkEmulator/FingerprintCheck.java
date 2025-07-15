package com.example.application1.checkEmulatorFactory.checkEmulator;

import android.os.Build;

public class FingerprintCheck implements CheckEmulator {
    @Override
    public boolean isEmulator() {
        return Build.FINGERPRINT.contains("sdk") || Build.FINGERPRINT.contains("emu");
    }
}