package kpu.smartswitch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b1 = (Button)findViewById(R.id.Main_SCtrlButton);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SwitchControl.class);
                startActivity(intent);
            }
        });

        Button b3 = (Button)findViewById(R.id.Main_SleepManageButton);
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SleepManage.class);
                startActivity(intent);
            }
        });

        Button b4 = (Button)findViewById(R.id.Main_SettingsButton);
        b4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Settings.class);
                startActivity(intent);
            }
        });

        Button b5 = (Button)findViewById(R.id.Main_HelpButton);
        b5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Help.class);
                startActivity(intent);
            }
        });

        Button b6 = (Button)findViewById(R.id.Main_ExitButton);
        b6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
