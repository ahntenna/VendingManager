package com.example.ahn.vendingmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class StatusMonthActivity extends Activity {

    private LineChart lineChart;

    /*
     PHP 에서 JSON 으로 받아올 때 키워드 설정
     */
    private static String TAG = "phptest_MainActivity";
    private static final String TAG_JSON = "capstone";
    private static final String TAG_PRICE = "price"; // 금액
    private static final String TAG_DATE = "date"; // 날짜(년 월 일)
    String mJsonString = "";

    private static int datecount[] = new int[32];

    /*
     Intent 를통해 넘어온 값 저장 변수
     */
    Intent intent = null;
    int getyear, getmonth, getday;
    String modecheck; // 물품별, 금액별 판단
    String machine; // Vending Machine 판별

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_month);

        intent = getIntent();
        getyear = Integer.parseInt(intent.getStringExtra("Year")); // LastDayCheck 메소드에서 사용
        getmonth = Integer.parseInt(intent.getStringExtra("Month")); // LastDayCheck 메소드에서 사용
        getday = Integer.parseInt(intent.getStringExtra("Day")); // LastDayCheck 메소드에서 사용
        modecheck = intent.getStringExtra("Key");
        machine = intent.getStringExtra("Machine");
        Log.e("--- machine : ", machine);

        /*
         꺽은선 그래프 제목(년 월)
         */
        TextView txt = (TextView)findViewById(R.id.txt_date);
        txt.setText(intent.getStringExtra("Year") + "년 " + intent.getStringExtra("Month") + "월"); // 화면 상단 제목 TextView

        if(intent.getStringExtra("Key").equals("prod")){
            GetData task = new GetData();
            task.execute(MainActivity.DB_URL+"db_vending_prod_line.php?month=" + (intent.getStringExtra("Month")) + "&year=" + (intent.getStringExtra("Year")) + "&machine=" + machine); // 해당월 모든 물품 일별 판매량 Select
//            Toast.makeText(getApplicationContext(), ("http://"+MainActivity.DB_URL+"/db_vending_prod_line.php?month=" + (intent.getStringExtra("Month")) + "&year=" + (intent.getStringExtra("Year")) + "&machine=" + machine), Toast.LENGTH_SHORT).show();
        }else if(intent.getStringExtra("Key").equals("price")){
            GetData task = new GetData();
            task.execute(MainActivity.DB_URL+"db_vending_price_line.php?month=" + (intent.getStringExtra("Month")) + "&year=" + (intent.getStringExtra("Year")) + "&machine=" + machine); // 해당월 모든 물품 일별 판매금액 Select
        }

    }

    /*
    MyMarkerview 클래스
    꺾은선 그래프 해당 일의 원형 아이콘(꼭짓점) 클릭 이벤트
     */
    public class MyMarkerView extends MarkerView {

        private TextView tvContent;

        public MyMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);

            tvContent = (TextView)findViewById(R.id.tvContent);
        }

        @Override
        public void refreshContent (Entry e, Highlight highlight){

            tvContent.setText("" + Utils.formatNumber(e.getY(), 0, true));
            super.refreshContent(e, highlight);
        }


        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), -getHeight());
        }

    }

    /*
     GetData 클래스
     PHP에서 얻어온 값 관리 및 처리
     */
    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(StatusMonthActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response  - " + result);

            if (result == null){
            }
            else {

                mJsonString = result;
                showResult(); // 처리 메소드 실행
            }
        }

    }


    /*
     showResult 메소드
     데이터 처리 및 시각화 메소드
     */
    private void showResult(){

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            String prices = "0"; // 판매 금액 저장 변수
            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                String date = item.getString(TAG_DATE);
                String sbs = date.toString().substring(8,10);

                /*
                 물품별 : 해당 일의 배열 카운트 하나씩 증가
                 금액별 : 해당 일에 가격정보 저장
                 */
                if(modecheck.equals("prod")) {
                    datecount[Integer.parseInt(sbs)]++; // 해당일 배열 카운트 증가
                }else if(modecheck.equals("price")){
                    prices = item.getString(TAG_PRICE);
                    datecount[Integer.parseInt(sbs)] = Integer.parseInt(prices)/100; // 액수 단위(백원)
                }
            }

            ArrayList<Entry> entries = new ArrayList<>(); // ListChart에 적용할 엔트리를 위한 ArrayList
            for(int i=0; i<datecount.length; i++){
                entries.add(new Entry(i, datecount[i])); // ArrayList에 해당 엔트리(물품 개수 또는 물품 판매금액) 저장
            }

            /*
             LineChar 설정
             */
            lineChart = (LineChart)findViewById(R.id.chart);
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setAxisMinimum(1); // x 축 속성 최소값
            xAxis.setAxisMaximum(LastDayCheck()); // x 축 속성 최대값(일)
            xAxis.setLabelCount(LastDayCheck()); // x 축 속성 표시 갯수(일)
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextColor(Color.BLACK);
            xAxis.enableGridDashedLine(8, 24, 0);

            YAxis yLAxis = lineChart.getAxisLeft();
//            yLAxis.setValueFormatter(new IAxisValueFormatter() {
//                @Override
//                public String getFormattedValue(float value, AxisBase axis) {
//                    return Integer.toString((int)value);
//                }
//            });
            yLAxis.setTextColor(Color.BLACK);
            yLAxis.setAxisMinimum(0);
            yLAxis.setSpaceTop(10);
            YAxis yRAxis = lineChart.getAxisRight();
            yRAxis.setDrawLabels(false);
            yRAxis.setDrawAxisLine(false);
            yRAxis.setDrawGridLines(false);

            Description description = new Description();
            description.setText("");

            LineDataSet lineDataSet = null;

            /*
             그래프 단위설명 대체...
             */
            if(modecheck.equals("prod")) {
                lineDataSet = new LineDataSet(entries, "단위/개");
            }else if(modecheck.equals("price")){
                lineDataSet = new LineDataSet(entries, "단위/백원");
            }
            lineDataSet.setLineWidth(2);
            lineDataSet.setCircleRadius(6);
            lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
            lineDataSet.setCircleColorHole(Color.BLUE);
            lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
            lineDataSet.setDrawCircleHole(true);
            lineDataSet.setDrawCircles(true);
            lineDataSet.setDrawHorizontalHighlightIndicator(false);
            lineDataSet.setDrawValues(false);

            MyMarkerView marker = new MyMarkerView(this,R.layout.graph_layout);
            marker.setChartView(lineChart); // 마커뷰 설정

            LineData lineData = new LineData(lineDataSet);
            lineChart.setData(lineData);
            lineChart.setDoubleTapToZoomEnabled(false);
            lineChart.setDrawGridBackground(false);
            lineChart.setDescription(description);
            lineChart.animateY(1000, Easing.EasingOption.EaseInCubic); // 애니메이션 효과, 1초
            lineChart.invalidate();
            lineChart.setMarker(marker);

            /*
             초기화
             그래프 사용후 다음 그래프를 위한 초기화
             */
            for(int i=0; i<datecount.length; i++){
                datecount[i]=0;
            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


    /*
     LastDayCheck 메소드
     그래프의 x축(마지막 날) return
     */
    public int LastDayCheck(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();

        cal.set(getyear, getmonth-1, getday); //월은 -1해줘야 해당월로 인식

        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

}