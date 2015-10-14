package fiskinfoo.no.sintef.fiskinfoo;

import android.app.Application;

import com.rey.material.app.ThemeManager;

public class FiskInfo extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ThemeManager.init(this, 1, 0, null);
    }
}
