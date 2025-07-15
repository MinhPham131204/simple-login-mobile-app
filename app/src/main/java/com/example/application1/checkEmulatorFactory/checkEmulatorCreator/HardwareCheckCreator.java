package com.example.application1.checkEmulatorFactory.checkEmulatorCreator;

import com.example.application1.checkEmulatorFactory.checkEmulator.CheckEmulator;
import com.example.application1.checkEmulatorFactory.checkEmulator.HardwareCheck;

public class HardwareCheckCreator implements CheckCreator {
    public CheckEmulator createCheck() {
        return new HardwareCheck();
    }
}
