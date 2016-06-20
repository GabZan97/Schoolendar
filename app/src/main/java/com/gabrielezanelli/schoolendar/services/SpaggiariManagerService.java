package com.gabrielezanelli.schoolendar.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SpaggiariManagerService extends Service {
    public SpaggiariManagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
