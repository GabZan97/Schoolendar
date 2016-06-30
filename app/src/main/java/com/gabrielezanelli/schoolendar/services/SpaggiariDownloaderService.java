package com.gabrielezanelli.schoolendar.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.gabrielezanelli.schoolendar.EventManager;
import com.gabrielezanelli.schoolendar.spaggiari.ClassevivaEvent;
import com.gabrielezanelli.schoolendar.spaggiari.SpaggiariClient;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.SQLException;
import java.util.Calendar;

public class SpaggiariDownloaderService extends Service {
    private EventManager eventManager;
    private String timeStart = "0";
    private String timeEnd = ""+(System.currentTimeMillis()/1000);

    public SpaggiariDownloaderService(Context context) {
        eventManager = EventManager.getInstance(context);

        SpaggiariClient.getIstance().getEvents(timeStart, timeEnd, new SpaggiariClient.SpaggiariGetEventsListener() {
            @Override
            public void onGetEventsSuccess(String response) {
                try {
                    //Log.d("Response", response);
                    Log.d("Spaggiari", "Events retrieve succeeded");

                    Gson gson = new Gson();

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++)
                        eventManager.addClassevivaEvent((gson.fromJson(jsonArray.get(i).toString(), ClassevivaEvent.class)));
                } catch (JSONException | SQLException e) {
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
