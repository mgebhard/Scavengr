package org.teamscavengr.scavengr;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
 * Created by zrneely on 4/4/15.
 */
public class NetworkHelper {

    public static JSONObject doRequest(URL url, String type, Map<String, String> values) throws
            IOException,
            JSONException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(1000);
        conn.setConnectTimeout(1000);
        conn.setRequestMethod(type);
        conn.setDoOutput(true);
        conn.setDoInput(true);

        List<NameValuePair> params = new ArrayList<>();
        for (String key : values.keySet()) {
            params.add(new BasicNameValuePair(key, values.get(key)));
        }

        OutputStream out = conn.getOutputStream();
        InputStream in = conn.getInputStream();

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
        bw.write(getQuery(params));
        bw.flush();

        conn.connect();

        // Get output
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }

        in.close();
        out.close();

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
