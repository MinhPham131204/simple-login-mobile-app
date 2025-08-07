package com.example.application1.checkEmulatorFactory.checkEmulator;

import android.os.Build;

public class ModelCheck implements CheckEmulator{
    @Override
    public boolean isEmulator() {
        return Build.MODEL.contains("x86");
    }
}
