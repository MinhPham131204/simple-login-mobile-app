package com.example.application1.checkEmulatorFactory.checkEmulatorCreator;

import com.example.application1.checkEmulatorFactory.checkEmulator.CheckEmulator;
import com.example.application1.checkEmulatorFactory.checkEmulator.ModelCheck;

public class ModelCheckCreator implements CheckCreator{
    @Override
    public CheckEmulator createCheck() {
        return new ModelCheck();
    }
}
