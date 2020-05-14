package kpu.smartswitch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;

public class SwitchControl extends AppCompatActivity {

    //private String htmlPageUrl = "192.168.4.1";     //아두이노 IP
    //private String htmlContent_light = "";
    //private String htmlContent_timer = "";
    //private String htmlContent_sensor = "";
    //private String htmlContent_security = "";

    private boolean saveLightStatusData;
    private boolean saveSensorStatusData;
    private boolean saveSecurityStatusData;
    private boolean saveTimerStatusData;

    private Switch light;
    private Switch sensor;
    private Switch security;
    private Switch timer;

    private SharedPreferences appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_control);
        /*
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();

        //전등 상태 확인
        if(htmlContent_light.lastIndexOf("0") != -1){
            saveLightStatusData = false;
        }
        else if(htmlContent_light.lastIndexOf("1") != -1){
            saveLightStatusData = true;
        }

        //타이머 상태 확인
        if(htmlContent_timer.lastIndexOf("0") != -1){
            saveTimerStatusData = false;
        }
        else if(htmlContent_light.lastIndexOf("1") != -1){
            saveTimerStatusData = true;
        }

        //센서 상태 확인
        if(htmlContent_sensor.lastIndexOf("0") != -1){
            saveSensorStatusData = false;
        }
        else if(htmlContent_sensor.lastIndexOf("1") != -1){
            saveSensorStatusData = true;
        }

        //방범 상태 확인
        if(htmlContent_security.lastIndexOf("0") != -1){
            saveSecurityStatusData = false;
        }
        else if(htmlContent_security.lastIndexOf("1") != -1){
            saveSecurityStatusData = true;
        }*/

        appData = getSharedPreferences("appData",MODE_PRIVATE);
        loadStatus();

        light = (Switch)findViewById(R.id.lightSwitch);
        sensor = (Switch)findViewById(R.id.sensorSwitch);
        security = (Switch)findViewById(R.id.securitySwitch);
        timer = (Switch)findViewById(R.id.timerSwitch);

        if(saveLightStatusData || saveSensorStatusData || saveSecurityStatusData || saveTimerStatusData){
            light.setChecked(saveLightStatusData);
            sensor.setChecked(saveSensorStatusData);
            security.setChecked(saveSecurityStatusData);
            timer.setChecked(saveTimerStatusData);
        }

        light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String lightStatus;
                if(light.isChecked()) {
                    lightStatus = "control.cgi?LightStatus=1";
                    Toast.makeText(SwitchControl.this, "전등 스위치 ON", Toast.LENGTH_SHORT).show();
                }else {
                    lightStatus = "control.cgi?LightStatus=0";
                    Toast.makeText(SwitchControl.this, "전등 스위치 OFF", Toast.LENGTH_SHORT).show();
                }
                saveStatus();

                String serverAddress = "192.168.4.1"; //아두이노 서버 주소 (포트:80)
                HttpRequestTask requestTask = new HttpRequestTask(serverAddress);
                requestTask.execute(lightStatus);
            }
        });

        sensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String sensorStatus;
                if(sensor.isChecked()) {
                    sensorStatus = "control.cgi?SensorStatus=1";
                    Toast.makeText(SwitchControl.this, "센서 스위치 ON", Toast.LENGTH_SHORT).show();
                }else {
                    sensorStatus = "control.cgi?SensorStatus=0";
                    Toast.makeText(SwitchControl.this, "센서 스위치 OFF", Toast.LENGTH_SHORT).show();
                }
                saveStatus();

                String serverAddress = "192.168.4.1"; //아두이노 서버 주소 (포트:80)
                HttpRequestTask requestTask = new HttpRequestTask(serverAddress);
                requestTask.execute(sensorStatus);
            }
        });

        security.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String securityStatus;
                if(security.isChecked()) {
                    securityStatus = "control.cgi?SecurityStatus=1";
                    Toast.makeText(SwitchControl.this, "방범 기능 ON", Toast.LENGTH_SHORT).show();
                }else {
                    securityStatus = "control.cgi?SecurityStatus=0";
                    Toast.makeText(SwitchControl.this, "방범 기능 OFF", Toast.LENGTH_SHORT).show();
                }
                saveStatus();

                String serverAddress = "192.168.4.1"; //아두이노 서버 주소 (포트:80)
                HttpRequestTask requestTask = new HttpRequestTask(serverAddress);
                requestTask.execute(securityStatus);
            }
        });

        timer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String timerStatus;
                if(timer.isChecked()) {
                    timerStatus = "timer.cgi?TimerStatus=1";
                    Toast.makeText(SwitchControl.this, "타이머 ON", Toast.LENGTH_SHORT).show();
                }else {
                    timerStatus = "timer.cgi?TimerStatus=0";
                    Toast.makeText(SwitchControl.this, "타이머 OFF", Toast.LENGTH_SHORT).show();
                }
                saveStatus();

                String serverAddress = "192.168.4.1"; //아두이노 서버 주소 (포트:80)
                HttpRequestTask requestTask = new HttpRequestTask(serverAddress);
                requestTask.execute(timerStatus);
            }
        });

        Button b1 = (Button)findViewById(R.id.SCtrl_SetTimerButton);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SwitchControl.this,SetTimer.class);
                startActivity(intent);
            }
        });

        Button b2 = (Button)findViewById(R.id.SCtrl_BackButton);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void saveStatus(){
        SharedPreferences.Editor editor = appData.edit();

        editor.putBoolean("lightStatus",light.isChecked());
        editor.putBoolean("sensorStatus",sensor.isChecked());
        editor.putBoolean("securityStatus",security.isChecked());
        editor.putBoolean("timerStatus",timer.isChecked());

        editor.apply();
    }

    public void loadStatus(){
        //저장 이력 없을 시 기본값
        saveLightStatusData = appData.getBoolean("lightStatus",false);
        saveSensorStatusData = appData.getBoolean("sensorStatus",false);
        saveSecurityStatusData = appData.getBoolean("securityStatus",false);
        saveTimerStatusData = appData.getBoolean("timerStatus",false);
    }
/*
    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params){
            try {
                Document doc = Jsoup.connect(htmlPageUrl).get();

                Elements light_e = doc.select(선택할 태그);
                Elements timer_e = doc.select(선택할 태그);
                Elements sensor_e = doc.select(선택할 태그);
                Elements security_e = doc.select(선택할 태그);

                for (Element e : light_e) {
                    htmlContent_light += e.text().trim();
                }
                for (Element e : timer_e) {
                    htmlContent_timer += e.text().trim();
                }
                for (Element e : sensor_e) {
                    htmlContent_sensor += e.text().trim();
                }
                for (Element e : security_e) {
                    htmlContent_sensor += e.text().trim();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
        }
    }
*/
    public class HttpRequestTask extends AsyncTask<String, Void, String> {
        private String serverAddress;

        public HttpRequestTask(String serverAddress) {
            this.serverAddress = serverAddress;
        }

        @Override
        protected String doInBackground(String... params) {
            String val = params[0];
            final String url = "http://"+serverAddress + "/" + val;

            //okHttp 라이브러리를 사용한다.
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                //Log.d(TAG, response.body().string());
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
