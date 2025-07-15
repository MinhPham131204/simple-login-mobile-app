package com.example.application1.checkEmulatorFactory.checkEmulatorCreator;

import com.example.application1.checkEmulatorFactory.checkEmulator.CheckEmulator;
import com.example.application1.checkEmulatorFactory.checkEmulator.DeviceCheck;

public class DeviceCheckCreator implements CheckCreator {
    @Override
    public CheckEmulator createCheck() {
        return new DeviceCheck();
    }
}