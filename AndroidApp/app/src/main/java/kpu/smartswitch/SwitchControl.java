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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SwitchControl extends AppCompatActivity {

    private boolean saveLightStatusData;
    private boolean saveSensorStatusData;
    private boolean saveSecurityStatusData;
    private boolean saveTimerStatusData;

    private Switch light;
    private Switch sensor;
    private Switch security;
    private Switch timer;

    public String ip_address;
    public SharedPreferences ip_addr_data;

    private SharedPreferences appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_control);

        //모든 스위치 상태 확인

        appData = getSharedPreferences("appData",MODE_PRIVATE);
        loadStatus();

        ip_addr_data = getSharedPreferences("ip_addr_data",MODE_PRIVATE);
        ip_address = ip_addr_data.getString("IP_Address","");

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

        if(ip_address.equals("")){
            Toast.makeText(SwitchControl.this, "오류: IP 주소가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
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

                String serverAddress = ip_address; //아두이노 서버 주소 (포트:80)
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

                String serverAddress = ip_address; //아두이노 서버 주소 (포트:80)
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

                String serverAddress = ip_address; //아두이노 서버 주소 (포트:80)
                HttpRequestTask requestTask = new HttpRequestTask(serverAddress);
                requestTask.execute(securityStatus);
            }
        });

        timer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String timerStatus;
                if(timer.isChecked()) {
                    timerStatus = "control.cgi?TimerStatus=1";
                    Toast.makeText(SwitchControl.this, "타이머 ON", Toast.LENGTH_SHORT).show();
                }else {
                    timerStatus = "control.cgi?TimerStatus=0";
                    Toast.makeText(SwitchControl.this, "타이머 OFF", Toast.LENGTH_SHORT).show();
                }
                saveStatus();

                String serverAddress = ip_address; //아두이노 서버 주소 (포트:80)
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