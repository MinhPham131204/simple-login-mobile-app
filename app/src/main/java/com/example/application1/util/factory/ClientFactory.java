package com.example.application1.util.factory;

import com.example.application1.checkEmulatorFactory.checkEmulator.CheckEmulator;
import com.example.application1.checkEmulatorFactory.checkEmulatorCreator.ABICheckCreator;
import com.example.application1.checkEmulatorFactory.checkEmulatorCreator.CheckCreator;
import com.example.application1.checkEmulatorFactory.checkEmulatorCreator.ModelCheckCreator;

public enum ClientFactory {
    // List all creator here
    ABI_CHECK(new ABICheckCreator());

    // stores the Creator object corresponding to each check type.
    private final CheckCreator creator;

    // every check type call this constructor to assign their instance into field creator.
    ClientFactory(CheckCreator creator) {
        this.creator = creator;
    }

    public static String checkAllCreator(){
        ClientFactory type = ClientFactory.values()[0];
        CheckEmulator product = type.creator.createCheck();

        if(product.isEmulator()) {
            return "App run on emulator";
        }

        else{
            return "App run on real device";
        }
    }
}