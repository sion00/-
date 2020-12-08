package kpu.smartswitch;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
//import java.io.IOException;
import java.io.IOException;
import java.util.*;
import java.text.*;
import java.lang.*;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//import okhttp3.Response;

public class Settings extends AppCompatActivity {

    public String ip_address;

    public SharedPreferences ip_addr_data;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final TextView txt = (TextView)findViewById(R.id.textView_ServerTime);
        final TextView txt_ip = (TextView)findViewById(R.id.textView_ServerIP);
        final EditText ip_set = (EditText)findViewById(R.id.editText_ServerIP);

        ip_addr_data = getSharedPreferences("ip_addr_data",MODE_PRIVATE);
        ip_address = ip_addr_data.getString("IP_Address", "");

        txt_ip.setText("서버 IP : " + ip_address);

        Button b1 = (Button)findViewById(R.id.Settings_SetButton);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String today = new SimpleDateFormat("'time.cgi?Year='yyyy'&Month='MM'&Day='dd'&Hour='HH'&Minute='mm'&Second='ss").format(new Date());
                String serverAddress = ip_address; //아두이노 서버 주소 (포트:80)
                HttpRequestTask requestTask = new HttpRequestTask(serverAddress);
                requestTask.execute(today);

                File file = new File(getFilesDir()+"/mainstatus.txt");
                try{
                    file.createNewFile();
                }catch(Exception e){
                    e.printStackTrace();
                }

                URLConnector.URLConnectorGet url = new URLConnector.URLConnectorGet("http://" + ip_address + "/");
                url.start();

                try{
                    url.join();
                }
                catch(InterruptedException e){

                }

                String result = url.getResult();

                String str = "";   //readLine()수행하기 위한 변수
                String[] arr = null;

                try {

                    //파일 내의 스트링을 나누는 부분
                    while ((str = result) != null) {
                        arr = str.split("<br /><br />");
                        break;
                    }

                    //텍스트 뷰 텍스트 변경
                    txt.setText(arr[1]);
                    Toast.makeText(Settings.this, "시간 동기화 완료", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button b2 = (Button)findViewById(R.id.Settings_IP_Address_Set_Button);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ip_address = ip_set.getText().toString();

                // SharedPreferences 객체만으론 저장 불가능 Editor 사용
                SharedPreferences.Editor editor = ip_addr_data.edit();

                // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
                // 저장시킬 이름이 이미 존재하면 덮어씌움
                editor.putString("IP_Address", ip_set.getText().toString().trim());

                // apply, commit 을 안하면 변경된 내용이 저장되지 않음
                editor.apply();

                //텍스트 뷰 텍스트 변경
                txt_ip.setText("서버 IP : " + ip_address);
            }
        });

        Button b3 = (Button)findViewById(R.id.Settings_BackButton);
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
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