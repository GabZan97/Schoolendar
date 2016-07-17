package com.gabrielezanelli.schoolendar.spaggiari;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SpaggiariClient {

    private static final String loginUrl = "https://web.spaggiari.eu/home/app/default/login.php";
    private static final String agendaUrl = "https://web.spaggiari.eu/cvv/app/default/agenda_studenti.php";

    private static SpaggiariClient spaggiariClient;
    private final LoopjHTTPHelper loopjHTTPHelper;

    private SpaggiariClient() {
        this.loopjHTTPHelper = new LoopjHTTPHelper();
    }

    public static SpaggiariClient getIstance(){
        if(spaggiariClient==null)
            spaggiariClient = new SpaggiariClient();
        return spaggiariClient;
    }

    public void login(String schoolCode, String userCode, String password, final SpaggiariAuthStateListener listener) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("custcode", schoolCode);
            params.put("login", userCode);
            params.put("password", password);
            params.put("mode", "custcode");

            loopjHTTPHelper.get(new URL(loginUrl), params, new LoopjSpaggiariHelper.SpaggiariHelperResponseListener() {
                @Override
                public void onSuccess(String response) {
                    Log.d("Spaggiari", "Login succeeded");
                    listener.onLoginSuccess(response);
                }

                @Override
                public void onFailure(String response) {
                    Log.d("Spaggiari", "Login failed");
                    listener.onLoginFailure(response);
                }
            });
        } catch (MalformedURLException ex) {
            listener.onLoginFailure("");
        }
    }

    public void getEvents(String timeStart, String timeEnd, final SpaggiariGetEventsListener listener) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("ope", "get_events");
            params.put("start", timeStart);
            params.put("end", timeEnd);

            loopjHTTPHelper.get(new URL(agendaUrl), params, new LoopjSpaggiariHelper.SpaggiariHelperResponseListener() {
                @Override
                public void onSuccess(String response) {
                    Log.d("Spaggiari", "Events retrieve succeeded");
                    Log.d("Spaggiari response",response);
                    listener.onGetEventsSuccess(response);
                }

                @Override
                public void onFailure(String response) {
                    Log.d("Spaggiari", "Events retrieve failed");
                    listener.onGetEventsFailure(response);
                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public interface SpaggiariAuthStateListener {
        void onLoginSuccess(String response);

        void onLoginFailure(String response);
    }

    public interface SpaggiariGetEventsListener {
        void onGetEventsSuccess(String response);

        void onGetEventsFailure(String response);
    }
}