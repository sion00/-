package kpu.smartswitch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SleepingTip extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleeping_tip);

        TextView textView = (TextView)findViewById(R.id.textView2);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());

        Button b1 = (Button)findViewById(R.id.SleepingTip_BackButton);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
