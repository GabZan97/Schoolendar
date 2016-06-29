package com.gabrielezanelli.schoolendar.spaggiari;

import android.content.Context;
import android.util.Log;

import com.gabrielezanelli.schoolendar.ClassevivaEvent;
import com.gabrielezanelli.schoolendar.EventManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SpaggiariClient {

    private static final String loginUrl = "https://web.spaggiari.eu/home/app/default/login.php";
    private static final String agendaUrl = "https://web.spaggiari.eu/cvv/app/default/agenda_studenti.php";

    private final LoopjHTTPHelper loopjHTTPHelper;

    public SpaggiariClient() {
        this.loopjHTTPHelper = new LoopjHTTPHelper();
    }

    public void login(final Context context, String schoolCode, String userCode, String password, final SpaggiariAuthStateListener listener) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("custcode", schoolCode);
            params.put("login", userCode);
            params.put("password", password);
            params.put("mode", "custcode");

            loopjHTTPHelper.get(new URL(loginUrl), params, new LoopjSpaggiariHelper.SpaggiariHelperResponseListener() {
                @Override
                public void onSuccess(String response) {
                    listener.onLoginSuccess();
                    Log.d("Spaggiari", "Login succeeded");
                    getEvents(context);
                }

                @Override
                public void onFailure(String response) {
                    listener.onLoginFailure();
                    Log.d("Spaggiari", "Login failed");
                }
            });
        } catch (MalformedURLException ex) {
            listener.onLoginFailure();
        }
    }

    public void getEvents(final Context context) {
        try {
            String timeStart = "1464599200";
            String timeEnd = "1465164000";
            Map<String, String> params = new HashMap<>();
            params.put("ope", "get_events");
            params.put("start", timeStart);
            params.put("end", timeEnd);

            loopjHTTPHelper.get(new URL(agendaUrl), params, new LoopjSpaggiariHelper.SpaggiariHelperResponseListener() {
                @Override
                public void onSuccess(String response) {
                    try {
                        Log.d("Response", response);
                        Log.d("Spaggiari", "Events retrieve succeeded");

                        Gson gson = new Gson();
                        EventManager eventManager = EventManager.getInstance(context);

                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = 0; i < jsonArray.length(); i++)
                            eventManager.addClassevivaEvent((gson.fromJson(jsonArray.get(i).toString(), ClassevivaEvent.class)));
                    } catch (JSONException | SQLException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String response) {
                    Log.d("Spaggiari", "Events retrieve failed");
                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public interface SpaggiariAuthStateListener {
        void onLoginSuccess();

        void onLoginFailure();
    }
}