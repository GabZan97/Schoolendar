package com.gabrielezanelli.schoolendar.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.gabrielezanelli.schoolendar.StoreManager;
import com.gabrielezanelli.schoolendar.spaggiari.ClassevivaEvent;
import com.gabrielezanelli.schoolendar.spaggiari.SpaggiariClient;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

public class SpaggiariDownloaderService extends Service {
    private StoreManager storeManager;
    private String timeStart = "0";
    private String timeEnd = ""+(System.currentTimeMillis()/1000);

    public SpaggiariDownloaderService() {
        storeManager = StoreManager.getInstance();

        SpaggiariClient.getIstance().getEvents(timeStart, timeEnd, new SpaggiariClient.SpaggiariGetEventsListener() {
            @Override
            public void onGetEventsSuccess(String response) {
                try {
                    //Log.d("Response", response);
                    Log.d("Spaggiari", "Events retrieve succeeded");

                    Gson gson = new Gson();

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        storeManager.addClassevivaEvent(gson.fromJson(jsonArray.get(i).toString(), ClassevivaEvent.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onGetEventsFailure(String response) {

            }
        });

    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
