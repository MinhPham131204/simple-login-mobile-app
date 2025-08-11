package com.example.application1.checkEmulatorFactory.checkEmulator;

import android.os.Build;

public class ABICheck implements CheckEmulator{
    public boolean isEmulator() {
        return !Build.SUPPORTED_ABIS[0].contains("arm");
    }
}
