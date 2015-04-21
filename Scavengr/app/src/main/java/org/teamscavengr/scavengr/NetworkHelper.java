package org.teamscavengr.scavengr;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A nice friendly network helper.
 * Created by zrneely on 4/4/15.
 */
public class NetworkHelper {

    public static JSONObject doRequest(URL url, String type) throws IOException, JSONException {
        return doRequest(url, type, false, new HashMap<String, String>());
    }

    public static JSONObject doRequest(URL url, String type, boolean output, Map<String, String> values) throws
            IOException,
            JSONException {
        System.setProperty("http.keepAlive", "false");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(10000);
        conn.setRequestMethod(type);
        conn.setDoOutput(output);
        conn.setRequestProperty("Accept-Encoding", "");
        if (output)
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoInput(true);
        //if (output)
        //    conn.setRequestProperty("Content-Type","application/json");


        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //JSONObject jsonParams = new JSONObject();
        for (String key : values.keySet()) {
            params.add(new BasicNameValuePair(key, values.get(key)));
            //jsonParams.put(key, values.get(key));
        }

        conn.connect();

        if (output) {
            DataOutputStream printout = new DataOutputStream(conn.getOutputStream());

            printout.writeBytes(getQuery(params));

            printout.flush();
            printout.close();
        }

        // Get output
        InputStream in = conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }

        in.close();
        try {
            Log.d("JSONObject", sb.toString());
            return new JSONObject(sb.toString());
        } catch (JSONException e) {
            Log.d("JSONParseError", sb.toString());
            return new JSONObject();
        }
    }

    private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(NameValuePair p : params) {
            if(first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(p.getName(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(p.getValue(), "UTF-8"));
        }
        return sb.toString();
    }
}
