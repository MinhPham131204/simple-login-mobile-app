package com.example.application1.checkEmulatorFactory.checkEmulator;

import android.os.Build;

public class HardwareCheck implements CheckEmulator{
    public boolean isEmulator() {
        return Build.HARDWARE.equals("ranchu");
    }
}