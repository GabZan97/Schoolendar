package com.gabrielezanelli.schoolendar.spaggiari;

import java.net.URL;
import java.util.Map;

/**
 * Created by Gabriele Zanelli on 24/06/2016.
 */
public interface LoopjSpaggiariHelper {

    void get(URL url, Map<String, String> arguments, SpaggiariHelperResponseListener listener);

    interface SpaggiariHelperResponseListener {
        void onSuccess(String data);

        void onFailure(String data);
    }
}