package com.example.application1.checkEmulatorFactory.checkEmulatorCreator;

import com.example.application1.checkEmulatorFactory.checkEmulator.ABICheck;
import com.example.application1.checkEmulatorFactory.checkEmulator.CheckEmulator;

public class ABICheckCreator implements CheckCreator{
    public CheckEmulator createCheck() {
        return new ABICheck();
    }
}
