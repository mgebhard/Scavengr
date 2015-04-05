package org.teamscavengr.scavengr;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A nice friendly network helper.
 * Created by zrneely on 4/4/15.
 */
public class NetworkHelper {

    public static JSONObject doRequest(URL url, String type, boolean output, Map<String, String> values) throws
            IOException,
            JSONException {
        System.setProperty("http.keepAlive", "false");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(10000);
        conn.setRequestMethod(type);
        conn.setDoOutput(output);
        if(output)
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

        OutputStream out = null;
        //if(output)
        //    out = conn.getOutputStream();
        Log.d("EVER", "Output is " + output);
        if(output) {
            Log.d("EVER", "Sent request to server");
            Log.d("EVER", URLEncoder.encode(params.toString(), "UTF-8"));
            DataOutputStream printout = new DataOutputStream(conn.getOutputStream());

            //String encoded = URLEncoder.encode(jsonParams.toString(),"UTF-8");

            printout.writeBytes(URLEncoder.encode(getQuery(params), "UTF-8"));

            printout.flush();
            printout.close();



            /*BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            bw.write(getQuery(params));
            bw.flush();*/
        }

        // Get output
        InputStream in = conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }

        in.close();

        return new JSONObject(sb.toString());
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
