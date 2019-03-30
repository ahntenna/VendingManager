package com.example.ahn.vendingmanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

public class UserVendingDetailActivity extends AppCompatActivity {

    private ListView listView;
    private Button btCall, btMap;
    private Intent getIntent;
    private AlertDialog.Builder dialog;

    private DatabaseManager dbManager;
    private ListViewAdapter adapter;

    // for Dialog
    private String MODE_TAG = null;
    private Context mContext;
    private LayoutInflater inflater;
    private View layout;
    private EditText editName, editPrice, editStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_vending_detail);

        ListViewAdapter.TAG = "UserVendingDetailActivity";

        btCall = (Button)findViewById(R.id.bt_call);
        btMap = (Button)findViewById(R.id.bt_map);

        getIntent = getIntent();
        setTitle(getIntent.getStringExtra("NAME").toString());

        adapter = new ListViewAdapter("UserVendingDetailActivity");
        dialog = new AlertDialog.Builder(this);

        initList();
        listView = (ListView)findViewById(R.id.list_view_stock_user);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, final int position, long id) {
                // get item
                ListViewItem item = (ListViewItem)parent.getItemAtPosition(position);

                String prodName = item.getProdName();
                String prodPrice = item.getProdPrice();
                String prodStock = item.getProdStock();
            }
        });

        btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getIntent.getStringExtra("PHONE")));

                if(intent != null) {
                    startActivity(intent);
                }
            }
        });

        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserVendingDetailActivity.this, MapActivity.class);
                intent.putExtra("NAME", getIntent.getStringExtra("NAME"));
                intent.putExtra("ADDRESS", getIntent.getStringExtra("ADDRESS"));

                if(intent != null) {
                    startActivity(intent);
                }
            }
        });
    }

    // Back Key 눌렀을 경우
    @Override
    public void onBackPressed() {
        ListViewAdapter.TAG = "UserActivity";

        super.onBackPressed();
    }

    private void initList() {
        mContext = getApplicationContext();
        inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.dialog_stock, (ViewGroup)findViewById(R.id.layout_dialog));

        editName = (EditText)layout.findViewById(R.id.edit_dialog_prod_name);
        editPrice = (EditText)layout.findViewById(R.id.edit_dialog_prod_price);
        editStock = (EditText)layout.findViewById(R.id.edit_dialog_prod_stock);

        dbManager = new DatabaseManager();
        dbManager.execute();
    }

    public void addToArrayList(String name, String stock, String price, String sold, String error) {
        Integer imgWarning;

        if(error.equals("1")) {
            imgWarning = R.drawable.warning;
        } else {
            imgWarning = R.drawable.blanc;
        }

        adapter.addItem(ContextCompat.getDrawable(this, imgWarning), name, stock, price, sold, "VendingDetailActivity");
        adapter.notifyDataSetChanged();
    }

    private void parsingString(String getListData) {
        if(!getListData.equals("]")) {
            StringTokenizer token = new StringTokenizer(getListData);
            String tables = token.nextToken(",");

            // String parsing
            String cnt = token.nextToken("[");
            cnt = cnt.substring(1);

            if(!(cnt.equals("]"))) {
                Log.e("--- cnt", cnt);

                for(int i=0; i<Integer.parseInt(cnt); i++) {
                    String temp = token.nextToken("<");
                    String itemID = token.nextToken(",");
                    itemID = itemID.substring(1);
                    Log.e("--- itemID", itemID);

                    String name = token.nextToken(",");
                    String stock = token.nextToken(",");
                    String price = token.nextToken(",");
                    String sold = token.nextToken(",");
                    String error = token.nextToken(",");

                    if (i < Integer.parseInt(cnt) - 1) {
                        error = error.substring(0, error.length() - 1);
                    } else if (i < Integer.parseInt(cnt)) {
                        error = error.substring(0, error.length() - 2);
                    }

                    addToArrayList(name, stock, price, sold, error);
                }
            }
        }
    }

    class DatabaseManager extends AsyncTask<Void, Integer, Void> {

        private String param = null;
        private String data = null;

        // 자판기 재고 목록 생성자
        public DatabaseManager() {
            MainActivity.PHP_FILENAME = "select_vending.php";
            param = "getVendingName=" + getIntent.getStringExtra("NAME").toString() +  "";
        }

        @Override
        protected Void doInBackground(Void... unused) {
            try {
                /* 서버연결 */
                URL url = new URL(MainActivity.DB_URL + MainActivity.PHP_FILENAME);             // it is worked
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream inputStream = null;
                BufferedReader bufferedReader = null;
                data = "";

                inputStream = conn.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);

                String line = null;
                StringBuffer buff = new StringBuffer();

                while((line = bufferedReader.readLine()) != null) {
                    buff.append(line + "\n");
                }

                data = buff.toString().trim();
                Log.e("--- Select DATA ", data);

            } catch (MalformedURLException me) {
                me.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        // doInBackground가 끝난 후 실행
        // 서버에서의 echo 값에 따른 행동 설정
        // * data는 php에서의 결과값
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(MainActivity.PHP_FILENAME.equals("select_vending.php")) {
                parsingString(data);
            }
        }
    }
}
