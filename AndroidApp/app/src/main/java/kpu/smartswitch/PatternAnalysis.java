package kpu.smartswitch;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.io.*;
import java.util.Map;

import static java.lang.String.*;

public class PatternAnalysis extends AppCompatActivity {

    private LineChart lineChart;
    FileReader fr = null;

    public String ip_address;
    public SharedPreferences ip_addr_data;

    public boolean isGraphCreated = false;

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

        URLConnector.URLConnectorGet url = new URLConnector.URLConnectorGet("http://" + ip_address + "/timelog");

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
                "2020062305";    // 테스트용 예제 데이터

        List<Entry> entries = new ArrayList<>();
        final List<Entry> entriesAvg = new ArrayList<>();         //평균값
        final ArrayList<String> labels = new ArrayList<String>();
        final ArrayList<String> labelsAvg = new ArrayList<String>();

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
            }

            /*List<Entry> values = new ArrayList<>();

            int k = 0;
            List<Entry> entries0 = new ArrayList<>();
            for (k=0;k==entries.getXIndex()){
                if (entries.contains(mXIndex)){
                    entries0.add(new Entry(0, k));
                }
                k += 1;
            }
            getXIndex()*/

            for (; ; ) {
                /*if(dat.substring(8).equals("00")){
                    labels.add(dat.substring(4));
                }
                else {
                    labels.add(dat.substring(8));
                }*/ // 끄적인 코드

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

        String example = url.getResult(); // 본방용 코드

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
        //LineDataSet lineDataSet = new LineDataSet(entries, "시간 당 뒤척인 횟수");
        LineDataSet lineDataSet = new LineDataSet(entries, "예시");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        //LineDataSet lineDataSetExample = new LineDataSet(entriesexample, "예시");  // 본방용 코드
        LineDataSet lineDataSetExample = new LineDataSet(entriesexample, "시간 당 뒤척인 횟수");
        lineDataSetExample.setLineWidth(2);
        lineDataSetExample.setCircleRadius(6);
        lineDataSetExample.setCircleColor(Color.parseColor("RED"));
        lineDataSetExample.setColor(Color.parseColor("RED"));
        lineDataSetExample.setDrawCircleHole(true);
        lineDataSetExample.setDrawCircles(true);
        lineDataSetExample.setDrawHorizontalHighlightIndicator(false);
        lineDataSetExample.setDrawHighlightIndicators(false);
        lineDataSetExample.setDrawValues(false);

        final LineData lineData = new LineData(labels, lineDataSet);

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
                return valueOf((int) value);
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


        Button b1 = (Button) findViewById(R.id.PatternAnalysis_ServerConnectButton);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(isGraphCreated){
                    Toast.makeText(PatternAnalysis.this, "평균 그래프를 이미 생성하였습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                isGraphCreated = true;
                //public String ip_address;
                //public SharedPreferences ip_addr_data;

                //@Override
                //protected void onCreate(Bundle savedInstanceState) {
                //    super.onCreate(savedInstanceState);
                 //   setContentView(R.layout.activity_pattern_analysis);

                 //   ip_addr_data = getSharedPreferences("ip_addr_data",MODE_PRIVATE);
                 //   ip_address = ip_addr_data.getString("IP_Address","");

                    String adu_Host = ip_address;
                    //String adu_Port = "8080";

                    String host = "52.79.198.32"; //"192.168.0.1"; //웹서버 주소 - 54.236.44.216
                    String port = "3000";

                    String getResultInfo = "http://" + adu_Host + "/timelog";
                    URLConnector.URLConnectorGet getResultUrl = new URLConnector.URLConnectorGet(getResultInfo);

                    getResultUrl.start();

                    String result = "";
                /*String result = "2020062223\n" +
                        "2020062223\n" +
                        "2020062300\n" +
                        "2020062301\n" +
                        "2020062301\n" +
                        "2020062303\n" +
                        "2020062303\n" +
                        "2020062303\n" +
                        "2020062303\n" +
                        "2020062305\n" +
                        "2020062305";*/

                    try {
                        getResultUrl.join();
                        result = getResultUrl.getResult();
                    } catch (InterruptedException e) {
                    }

                    // 데이터 없는 경우
                    if (result.equals("")) {
                        return;
                    }


                    // 1. log 데이터 가져온거 데이터 분리
                    String[] timeData = result.split("\n");

                    Map<String, Integer> patternData = new HashMap<String, Integer>();

                    for (int i = 0; i < timeData.length; i++) {
                        String key = timeData[i].substring(8);

                        if (patternData.containsKey(key)) {
                            int data = patternData.get(key) + 1;
                            patternData.put(key, data);

                        } else {
                            patternData.put(key, 1);
                        }
                    }

                    // 2. 시간, 횟수, 표본 데이터 생성
                    JSONObject insertListInfo = new JSONObject();
                    JSONArray insertList = new JSONArray();
                    try {
                        for (Map.Entry<String, Integer> item : patternData.entrySet()) {

                            JSONObject info = new JSONObject();


                            info.put("mTime", item.getKey());
                            info.put("mMoveTime", item.getValue());

                            insertList.put(info);

                        }
                        insertListInfo.put("PatternInfoList", insertList);
                    } catch (JSONException e) {

                    }

                    // 3. 웹서버로 데이터 전송
                    String insertPatternInfoUrl = "http://" + host + ":" + port + "/insertPatternInfo";
                    URLConnector.URLConnectorPost postUrl = new URLConnector.URLConnectorPost(insertPatternInfoUrl, insertListInfo);
                    postUrl.start();

                    try{

                        postUrl.join();

                        String resultInfo = postUrl.getResult();

                        // 4. 웹서버 데이터 수신
                        // 5. 웹서버에서 수신된 데이터 db로 저장
                        if(resultInfo.contains("true") == false){
                            return;
                        }

                    }catch (InterruptedException e) {
                        Log.e("insertPatternInfoUrl", e.toString());
                    }

                    // 6. 평균 데이터 DB에서 안드로이드로 가져옴
                    String getPatternInfoUrl = "http://" + host + ":" + port + "/getPatternInfo";

                    URLConnector.URLConnectorGet getUrl = new URLConnector.URLConnectorGet(getPatternInfoUrl);
                    getUrl.start();

                    try {
                        getUrl.join();

                        String resultInfo = getUrl.getResult();

                        PatternDataModel.PatternInfoList patternInfoList = new Gson().fromJson(resultInfo, PatternDataModel.PatternInfoList.class);
                        for (PatternDataModel.PatternInfo item : patternInfoList.PatternInfoList) {

                            /* 이 부분이 DB로 가져온 데이터 */
                            @SuppressLint("DefaultLocale") String d = format("time : %s, moveTime : %.2f, sample : %d", item.time, item.moveTime, item.sample);

                            Log.d("PatternInfo", d);
                            /* 이 부분이 DB로 가져온 데이터 */

                            //그래프 엔트리 추가
                            entriesAvg.add(new Entry((float)item.moveTime, Integer.parseInt(item.time)));
                            labelsAvg.add(item.time);
                        }

                    } catch (InterruptedException e) {
                        Log.e("getPatternInfoUrl", e.toString());
                    }

                    LineDataSet lineDataSetAvg = new LineDataSet(entriesAvg, "평균");
                    lineDataSetAvg.setLineWidth(2);
                    lineDataSetAvg.setCircleRadius(6);
                    lineDataSetAvg.setCircleColor(Color.RED);
                    lineDataSetAvg.setColor(Color.RED);
                    lineDataSetAvg.setDrawCircleHole(true);
                    lineDataSetAvg.setDrawCircles(true);
                    lineDataSetAvg.setDrawHorizontalHighlightIndicator(false);
                    lineDataSetAvg.setDrawHighlightIndicators(false);
                    lineDataSetAvg.setDrawValues(false);

                    LineData lineDataAvg = new LineData(labelsAvg, lineDataSetAvg);

                    lineChart.setData(lineDataAvg);

                    lineData.addDataSet(lineDataSetAvg);

                    //차트 그리기
                    lineChart.setDoubleTapToZoomEnabled(false);
                    lineChart.setDrawGridBackground(false);
                    lineChart.invalidate();
                }
            });

            Button b2 = (Button)findViewById(R.id.PatternAnalysis_BackButton);
        b2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
