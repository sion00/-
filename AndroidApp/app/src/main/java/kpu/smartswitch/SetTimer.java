package kpu.smartswitch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SetTimer extends AppCompatActivity {
    public String timeSecond;

    public String ip_address;
    public SharedPreferences ip_addr_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_timer);

        ip_addr_data = getSharedPreferences("ip_addr_data",MODE_PRIVATE);
        ip_address = ip_addr_data.getString("IP_Address","");

        final EditText timer_set = (EditText)findViewById(R.id.editText_OperatingTimeSecond);

        Button b1 = (Button)findViewById(R.id.SetTimer_SetButton);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timeSecond = timer_set.getText().toString();

                HttpRequestTask requestTask = new SetTimer.HttpRequestTask(ip_address);
                requestTask.execute("setting.cgi?timer=" + timeSecond);

                Toast.makeText(SetTimer.this, "타이머 설정 완료", Toast.LENGTH_SHORT).show();
            }
        });

        Button b2 = (Button)findViewById(R.id.SetTimer_BackButton);
        b2.setOnClickListener(new View.OnClickListener() {
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
