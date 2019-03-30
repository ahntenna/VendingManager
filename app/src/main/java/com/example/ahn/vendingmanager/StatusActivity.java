package com.example.ahn.vendingmanager;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class StatusActivity extends Activity {

    private BarChart barChart;
    private DatePicker datePicker;

    /*
     PHP 에서 JSON 으로 받아올 때 키워드 설정
     */
    private static String TAG = "phptest_MainActivity";
    private static final String TAG_JSON = "capstone"; // JSON 최상위 태그
    private static final String TAG_PRODUCT = "prod"; // 물품
    private static final String TAG_COUNT = "count"; // 개수 또는 금액
    String mJsonString ="";

    private static int datecount[] = new int[32]; // 일별 그래프 표시를 위한 배열

    /*
     Intent 를통해 넘어온 값 저장 변수
     */
    Intent intent = null;
    int getyear, getmonth, getday;
    String modecheck = ""; // 물품별, 금액별 판단
    String machine = ""; // Vending Machine 판별

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        intent = getIntent();
        modecheck = intent.getStringExtra("Key");
        machine = intent.getStringExtra("Machine");
        Log.e("--- machine : ", machine);

        /*
         DatePicker(달력) 초기 설정
         */
        datePicker = (DatePicker)findViewById(R.id.datepicker);
        getyear = datePicker.getYear();
        getmonth = datePicker.getMonth() + 1;
        getday = datePicker.getDayOfMonth();
        datePicker.init(datePicker.getYear(), datePicker.getMonth(),datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener(){
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth){

                getyear = year;
                getmonth = monthOfYear + 1;
                getday = dayOfMonth;

                for (int i = 0; i < datecount.length; i++) {
                    datecount[i] = 0;
                }

                String strMonth = "";
                String strDay = "";

                if (monthOfYear < 10) {
                    strMonth = String.valueOf("0" + (monthOfYear + 1));
                } else {
                    strMonth = String.valueOf((monthOfYear + 1));
                }

                if (dayOfMonth < 10) {
                    strDay = String.valueOf("0" + (dayOfMonth));
                } else {
                    strDay = String.valueOf((dayOfMonth));
                }

                /*
                 이전 엑티비티에서 클릭한 버튼에 따른 Parameter 전송
                 */
                if(modecheck.equals("prod")){
                    GetData task = new GetData();
                    task.execute(MainActivity.DB_URL+"db_vending_prod_stick.php?month=" + (strMonth) + "&year=" + year + "&day=" + strDay + "&machine=" + machine); // 해당 일에대한 물품 판매량 Select
                }else if(modecheck.equals("price")){
                    GetData task = new GetData();
                    task.execute(MainActivity.DB_URL+"db_vending_price_stick.php?month=" + (strMonth) + "&year=" + year + "&day=" + strDay + "&machine=" + machine); // 해당 일에대한 물품 판매금액 Select
                }
            }
        });

        /*
         해당 월전체(물품,금액) 확인 View Listener
         */
        View.OnClickListener listener = new View.OnClickListener(){
            Intent intent2 = new Intent(StatusActivity.this, StatusMonthActivity.class);
            @Override
            public void onClick(View v) {
                String Month;
                String Day;
                if (getmonth < 10) {
                    Month = String.valueOf("0" + (getmonth));
                } else {
                    Month = String.valueOf((getmonth));
                }

                if (getday < 10) {
                    Day = String.valueOf("0" + (getday));
                } else {
                    Day = String.valueOf((getday));
                }

                intent2.putExtra("Month", Month);
                intent2.putExtra("Day", Day); // 해당 월의 마지막날을 체크하기위한 Day 전송
                intent2.putExtra("Year", Integer.toString(getyear));
                intent2.putExtra("Machine", machine); // 자판기 정보 전송

                if (intent.getStringExtra("Key").equals("prod")){ // 물품별일 경우
                    intent2.putExtra("Key", "prod");
                }else if(intent.getStringExtra("Key").equals("price")){
                    intent2.putExtra("Key", "price");
                }
                v.getContext().startActivity(intent2);
            }
        };

        Button btn = (Button) findViewById(R.id.bt_month);
        btn.setOnClickListener(listener);

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

            progressDialog = ProgressDialog.show(StatusActivity.this,
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


        String Xvalues[]; // 막대그래프의 x 축 좌표의 수를 결정하기 위한 배열

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            Xvalues = new String[jsonArray.length()+1]; // 데이터의 길이(개수) +1 만큼 배열 할당
            ArrayList<BarEntry> entries = new ArrayList(); // BarChart에 적용할 엔트리를 위한 ArrayList

            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                String product = item.getString(TAG_PRODUCT);
                String count = "";
                if(modecheck.equals("prod")) {
                    count = item.getString(TAG_COUNT); // 개수
                }else if(modecheck.equals("price")) {
                    count = Integer.toString(Integer.parseInt(item.getString(TAG_COUNT))/100); // 액수 단위(백원)
                }
                Xvalues[i] = product; // 물품명 저장
                entries.add(new BarEntry(i, Integer.parseInt(count))); // ArrayList에 해당 엔트리(물품 개수 또는 물품 판매금액) 저장
            }

            /*
             BarChar 설정
             */
            barChart = (BarChart) findViewById(R.id.chart);
            barChart.getXAxis().setValueFormatter(new MyXAxisValueFormatter(Xvalues)); // x축 설정
            XAxis xAxis = barChart.getXAxis();
            xAxis.setDrawLabels(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);

            YAxis yLAxis = barChart.getAxisLeft();
            yLAxis.setAxisMinimum(0);
            yLAxis.setSpaceTop(10);
            YAxis yRAxis = barChart.getAxisRight();
            yRAxis.setDrawLabels(false);
            yRAxis.setDrawAxisLine(false);
            yRAxis.setDrawGridLines(false);

            Description description = new Description();
            description.setText("");

            BarDataSet bardataset = null;

            /*
             그래프 단위설명 대체...
             */
            if(modecheck.equals("prod")) {
                bardataset = new BarDataSet(entries, "단위/개");
            }else if(modecheck.equals("price")) {
                bardataset = new BarDataSet(entries, "단위/백원");
            }

            BarData data = new BarData(bardataset);
            barChart.setData(data);
            barChart.setDoubleTapToZoomEnabled(false);
            barChart.setDrawGridBackground(false);
            barChart.setDescription(description);
            barChart.animateY(1000, Easing.EasingOption.EaseInCubic); // 애니메이션 효과, 1초
            barChart.invalidate();

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

}