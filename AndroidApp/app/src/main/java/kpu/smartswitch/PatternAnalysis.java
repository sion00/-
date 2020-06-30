package kpu.smartswitch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.io.*;

public class PatternAnalysis extends AppCompatActivity {

    private LineChart lineChart;
    FileReader fr = null;

    public String ip_address;
    public SharedPreferences ip_addr_data;

    //StringBuilder sendResult = new StringBuilder("");    //db로 보내기 위한 값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_analysis);

        ip_addr_data = getSharedPreferences("ip_addr_data",MODE_PRIVATE);
        ip_address = ip_addr_data.getString("IP_Address","");

        File file = new File(getFilesDir()+"/log.txt");

        //html에서 로그를 읽어와 로컬 txt 파일로 쓰기

        try{
            file.createNewFile();
        }catch(Exception e){
            e.printStackTrace();
        }

        URLConnector url = new URLConnector("http://" + ip_address + "/timelog");

        url.start();

        try{
            url.join();
        }
        catch(InterruptedException e){
        }

        //String result = url.getResult();
        String result = "2020062223\n" +
                "2020062223\n"+
                "2020062300\n"+
                "2020062301\n"+
                "2020062301\n"+
                "2020062303\n" +
                "2020062303\n" +
                "2020062303\n" +
                "2020062303\n"+
                "2020062305\n"+
                "2020062305";

        List<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        if(result.equals("")){
            Toast.makeText(PatternAnalysis.this, "오류: 데이터를 읽어오지 못하였습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        //로컬 txt 파일에서 앱으로 데이터 읽어오기
        try {
            String[] arr = result.split("\n");
            //파일에서 로그 값 읽어서 엔트리 채우기

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH");
            Date d0 = formatter.parse(arr[0]);
            Date d = d0;
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String dat = new SimpleDateFormat("yyyyMMddHH").format(d);
            String last = arr[arr.length - 1];

            Date x0 = formatter.parse(arr[0]);

            for (int i = 0, y = 1; i <= (arr.length - 1); i++) {
                if (i > 0) {
                    //연속된 두 줄의 문자열이 같으면 ++y
                    if (arr[i].equals(arr[i - 1])) {
                        ++y;
                        if(i==(arr.length-1)){
                                Date x = formatter.parse(arr[i]);
                                long diff = (x.getTime() - x0.getTime()) / (60 * 60 * 1000);
                                entries.add(new Entry(y, (int) diff));
                        }
                    } else {
                        Date x = formatter.parse(arr[i - 1]);
                        long diff = (x.getTime() - x0.getTime()) / (60 * 60 * 1000);
                        entries.add(new Entry(y, (int) diff));

                        y = 1;

                        //sendResult.append(Integer.toString((int) diff) + " " + Integer.toString(y) + "\n");
                        if (i==(arr.length-1)) {
                            x = formatter.parse(arr[i]);
                            diff = (x.getTime() - x0.getTime()) / (60 * 60 * 1000);
                            entries.add(new Entry(y, (int) diff));
                        }
                    }
                }
            }       //참고자료 확인할 것... 배열 대신 calendar쓰는 이중 for문으로 바꿔볼 것..

            for (; ; ) {
                /*if(dat.substring(8).equals("00")){
                    labels.add(dat.substring(4));
                }
                else {
                    labels.add(dat.substring(8));
                }*/

                labels.add(dat.substring(8));
                if (dat.equals(last)) {
                    break;
                }
                cal.add(Calendar.HOUR, 1);
                dat = formatter.format(cal.getTime());
            }
        }
            catch(Exception e){
                e.printStackTrace();
            }

        String example = url.getResult();

        List<Entry> entriesexample = new ArrayList<>();

        try {
            String[] arr = example.split("\n");
            //파일에서 로그 값 읽어서 엔트리 채우기

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH");
            Date d0 = formatter.parse(arr[0]);
            Date d = d0;
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String dat = new SimpleDateFormat("yyyyMMddHH").format(d);
            String last = arr[arr.length - 1];

            Date x0 = formatter.parse(arr[0]);

            for (int i = 0, y = 1; i <= (arr.length - 1); i++) {
                if (i > 0) {
                    //연속된 두 줄의 문자열이 같으면 ++y
                    if (arr[i].equals(arr[i - 1])) {
                        ++y;
                        if (i == (arr.length - 1)) {
                            Date x = formatter.parse(arr[i]);
                            long diff = (x.getTime() - x0.getTime()) / (60 * 60 * 1000);
                            entriesexample.add(new Entry(y, (int) diff));
                        }
                    } else {
                        Date x = formatter.parse(arr[i - 1]);
                        long diff = (x.getTime() - x0.getTime()) / (60 * 60 * 1000);
                        entriesexample.add(new Entry(y, (int) diff));

                        y = 1;

                        //sendResult.append(Integer.toString((int) diff) + " " + Integer.toString(y) + "\n");
                        if (i == (arr.length - 1)) {
                            x = formatter.parse(arr[i]);
                            diff = (x.getTime() - x0.getTime()) / (60 * 60 * 1000);
                            entriesexample.add(new Entry(y, (int) diff));
                        }
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

            //----로그 파일 취급 부분의 끝----//

            //차트 작성
            lineChart = (LineChart)findViewById(R.id.line_chart);

            //차트 데이터 집합 설정 및 그래프 설정
            LineDataSet lineDataSet = new LineDataSet(entries, "시간 당 뒤척인 횟수");
            lineDataSet.setLineWidth(2);
            lineDataSet.setCircleRadius(6);
            lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
            lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
            lineDataSet.setDrawCircleHole(true);
            lineDataSet.setDrawCircles(true);
            lineDataSet.setDrawHorizontalHighlightIndicator(false);
            lineDataSet.setDrawHighlightIndicators(false);
            lineDataSet.setDrawValues(false);
            /*lineDataSet.setFillFormatter(new FillFormatter()
            {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    float fillMin = 0f;
                    float chartMaxY = dataProvider.getYChartMax();
                    float chartMinY = dataProvider.getYChartMin();

                    LineData data = dataProvider.getLineData();

                    if (dataSet.getYMax() > 0 && dataSet.getYMin() < 0) {
                        fillMin = 0f;
                    } else {

                        float max, min;

                        if (data.getYMax() > 0)
                            max = 0f;
                        else
                            max = chartMaxY;
                        if (data.getYMin() < 0)
                            min = 0f;
                        else
                            min = chartMinY;

                        fillMin = dataSet.getYMin() >= 0 ? min : max;
                    }

                    return fillMin;
                }
            });*/

        LineDataSet lineDataSetExample = new LineDataSet(entriesexample, "예시");
        lineDataSetExample.setLineWidth(2);
        lineDataSetExample.setCircleRadius(6);
        lineDataSetExample.setCircleColor(Color.parseColor("RED"));
        lineDataSetExample.setColor(Color.parseColor("RED"));
        lineDataSetExample.setDrawCircleHole(true);
        lineDataSetExample.setDrawCircles(true);
        lineDataSetExample.setDrawHorizontalHighlightIndicator(false);
        lineDataSetExample.setDrawHighlightIndicators(false);
        lineDataSetExample.setDrawValues(false);

            LineData lineData = new LineData(labels, lineDataSet);

            lineData.addDataSet(lineDataSetExample);

            lineChart.setDescription("시간(시)");
            lineChart.setData(lineData);

            //축 설정
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextColor(Color.BLACK);
            xAxis.enableGridDashedLine(8, 24, 0);


            YAxis yLAxis = lineChart.getAxisLeft();
            yLAxis.setTextColor(Color.BLACK);
            yLAxis.setAxisMinValue(0);
            //yLAxis.setAxisMaxValue(10)
            yLAxis.setGranularity(1f);
            yLAxis.setValueFormatter(new YAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, YAxis yAxis) {
                    return String.valueOf((int) value);
                }
            });

            YAxis yRAxis = lineChart.getAxisRight();
            yRAxis.setDrawLabels(false);
            yRAxis.setDrawAxisLine(false);
            yRAxis.setDrawGridLines(false);

            //차트 그리기
            lineChart.setDoubleTapToZoomEnabled(false);
            lineChart.setDrawGridBackground(false);
            lineChart.invalidate();

        /*Button b1 = (Button)findViewById(R.id.PatternAnalysis_ServerConnectButton);
        b1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String sendmsg = "vision_write";
                //String sendResult = ""; //자신이 보내고싶은 값을 보내시면됩니다
                try{
                    String rst = new Task(sendmsg).execute(sendResult.toString(),"vision_write").get();
                }catch (Exception e){
                    e.printStackTrace();
                }

                String sendmsg2 = "vision_list";
                String result; //전체출력 result;
                String[] oj;
                try{
                    List<Entry> entries = new ArrayList<>();

                    result  = new Task(sendmsg2).execute("vision_list").get();//디비값을 가져오기

                    int time, toss_turn_time_avg;

                    time = Integer.parseInt(result.substring(0,1));
                    toss_turn_time_avg = Integer.parseInt(result.substring(2));

                    entries.add(new Entry(toss_turn_time_avg, time));

                    LineDataSet lineDataSet = new LineDataSet(entries, "평균 뒤척인 횟수");

                    lineChart.invalidate();
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });*/

            Button b2 = (Button)findViewById(R.id.PatternAnalysis_BackButton);
            b2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

/*
x, y값을 string바꿔주고, 이 중 연월일을 잘라내고 몇 시 대에 뒤척였는지만 DB에 반영되도록 만들어야 함.

* */