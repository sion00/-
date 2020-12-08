package kpu.smartswitch;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class URLConnector {

    public static class URLConnectorGet extends Thread {

        private String result;
        private String URL;

        public URLConnectorGet(String url) {
            URL = url;
        }

        @Override
        public void run() {
            final String output = request(URL);
            result = output;
        }

        public String getResult() {
            return result;
        }

        private String request(String urlStr) {
            StringBuilder output = new StringBuilder();
            try {
                java.net.URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    //conn.setDoInput(true);
                    //conn.setDoOutput(true);

                    int resCode = conn.getResponseCode();
                    if (resCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = null;
                        while (true) {
                            line = reader.readLine();
                            if (line == null) {
                                break;
                            }
                            output.append(line + "\n");
                        }

                        reader.close();
                        conn.disconnect();
                    }
                }
            } catch (Exception ex) {
                //Log.e("SampleHTTP", "Exception in processing response.", ex);
                ex.printStackTrace();
            }

            return output.toString();
        }

    }

    public static class URLConnectorPost extends Thread {

        private String result;
        private String URL;
        private JSONObject Data;

        public URLConnectorPost(String url, JSONObject data) {
            URL = url;
            Data = data;
        }

        @Override
        public void run() {
            final String output = request(URL, Data);
            result = output;
        }

        public String getResult() {
            return result;
        }

        private String request(String urlStr, JSONObject param) {
            StringBuilder output = new StringBuilder();
            try {
                java.net.URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {
                    conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(onConvertJsonToString(param));

                    writer.flush();
                    writer.close();
                    os.close();

                    int resCode = conn.getResponseCode();
                    if (resCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = null;
                        while (true) {
                            line = reader.readLine();
                            if (line == null) {
                                break;
                            }
                            output.append(line + "\n");
                        }

                        reader.close();
                        conn.disconnect();
                    }
                }
            } catch (Exception ex) {
                //Log.e("SampleHTTP", "Exception in processing response.", ex);
                ex.printStackTrace();
            }

            return output.toString();
        }

        public static String onConvertJsonToString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while (itr.hasNext()) {

                String key = itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }

    }
}
