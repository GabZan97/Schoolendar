package com.gabrielezanelli.schoolendar.spaggiari;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.net.URL;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class LoopjHTTPHelper implements LoopjSpaggiariHelper {

    private final AsyncHttpClient client;

    public LoopjHTTPHelper() {
        client = new AsyncHttpClient();
    }

    @Override
    public void get(URL url, Map<String, String> arguments, final SpaggiariHelperResponseListener listener) {
        String urlString = url.toString();

        if (arguments.size() > 0) {

            boolean isFirstArg = true;
            for (Map.Entry<String, String> entry : arguments.entrySet()) {
                if (isFirstArg)
                    urlString += "?";
                else
                    urlString += "&";

                urlString += entry.getKey() + "=" + entry.getValue();
                isFirstArg = false;
            }

        }
        Log.d("Requesting for: ", urlString);

        client.get(urlString, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                for (int i = 0; i < headers.length; i++)
                    Log.d("Header n" + i, headers[i].toString());
                listener.onSuccess(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFailure(responseString);
            }

        });
    }

}
