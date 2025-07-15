package com.example.application1.checkEmulatorFactory.checkEmulator;

import android.os.Build;

public class ProductCheck implements CheckEmulator{
    public boolean isEmulator() {
        return Build.PRODUCT.contains("sdk");
    }
}
