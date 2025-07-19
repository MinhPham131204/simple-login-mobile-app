package com.example.application1.util.factory;

import android.util.Log;

import com.example.application1.checkEmulatorFactory.checkEmulator.CheckEmulator;
import com.example.application1.checkEmulatorFactory.checkEmulatorCreator.CheckCreator;
import com.example.application1.checkEmulatorFactory.checkEmulatorCreator.DeviceCheckCreator;
import com.example.application1.checkEmulatorFactory.checkEmulatorCreator.FingerprintCheckCreator;
import com.example.application1.checkEmulatorFactory.checkEmulatorCreator.HardwareCheckCreator;
import com.example.application1.checkEmulatorFactory.checkEmulatorCreator.ModelCheckCreator;
import com.example.application1.checkEmulatorFactory.checkEmulatorCreator.ProductCheckCreator;

public enum ClientFactory {
    // List all creator here
    DEVICE_CHECK(new DeviceCheckCreator()),
    MODEL_CHECK(new ModelCheckCreator()),
    HARDWARE_CHECK(new HardwareCheckCreator()),
    PRODUCT_CHECK(new ProductCheckCreator()),
    FINGERPRINT_CHECK(new FingerprintCheckCreator());

    // stores the Creator object corresponding to each check type.
    private final CheckCreator creator;

    // every check type call this constructor to assign their instance into field creator.
    ClientFactory(CheckCreator creator) {
        this.creator = creator;
    }

    public static void checkAllCreator(){
        for (ClientFactory type : ClientFactory.values()) {
            CheckEmulator product = type.creator.createCheck();
            Log.d("DEBUG", type.toString() + ": " +product.isEmulator());
        }
    }
}