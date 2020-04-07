package kpu.smartswitch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SleepManage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_manage);

        Button b1 = (Button)findViewById(R.id.SleepManage_PatternAnalysisButton);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SleepManage.this,PatternAnalysis.class);
                startActivity(intent);
            }
        });

        Button b2 = (Button)findViewById(R.id.SleepManage_SleepingTipButton);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SleepManage.this,SleepingTip.class);
                startActivity(intent);
            }
        });

        Button b3 = (Button)findViewById(R.id.SleepManage_BackButton);
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
