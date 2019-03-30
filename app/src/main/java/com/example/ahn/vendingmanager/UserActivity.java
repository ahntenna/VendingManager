package com.example.ahn.vendingmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

public class UserActivity extends Activity {

    private ListView listView;
    private ListViewAdapter adapter;
    private EditText editFilter;

    private DatabaseManager dbManager;

    private Intent intent;
    private String LOGIN_ID;

    private int imageNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("자판기 목록");

        intent = getIntent();
        LOGIN_ID = intent.getStringExtra("LOGIN_ID").toString();

        dbManager = new DatabaseManager(LOGIN_ID);
        dbManager.execute();

        ListViewAdapter.TAG = "UserActivity";
        adapter = new ListViewAdapter("UserActivity");
        listView = (ListView)findViewById(R.id.list_view_user);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListViewItem item = (ListViewItem)parent.getItemAtPosition(position);

                String vendingNames = item.getVendingName();
                String vendingOwner = item.getVendingOwnerPhone();
                String vendingAddress = item.getVendingLocation();

                Intent intent = new Intent(UserActivity.this, UserVendingDetailActivity.class);
                intent.putExtra("MACHINE_ID", String.valueOf(position+1));
                intent.putExtra("OWNER", LOGIN_ID);
                intent.putExtra("NAME", vendingNames);
                intent.putExtra("PHONE", vendingOwner);
                intent.putExtra("ADDRESS", vendingAddress);
                startActivity(intent);
            }
        });

        editFilter = (EditText)findViewById(R.id.edit_filter_user);
        editFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edit) {
                String filterText = edit.toString();
                ((ListViewAdapter)listView.getAdapter()).getFilter().filter(filterText);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addToArrayList(String name, String type, String address, String phone, String error) {
        Integer imgWarning;
        Integer imgVending;

        if(error.equals("1")) {
            imgWarning = R.drawable.warning;
        } else {
            imgWarning = R.drawable.blanc;
        }

        // 자판기 이미지 순환 추가
        if(imageNum == 0) {
            imgVending = R.drawable.vending_black;
        } else if(imageNum == 1) {
            imgVending = R.drawable.vending_red;
        } else if(imageNum == 2) {
            imgVending = R.drawable.vending_blue;
        } else if(imageNum == 3) {
            imgVending = R.drawable.vending_color;
        } else {
            imgVending = R.drawable.vending_logo;
        }

        imageNum++;

        if(imageNum == 4) {
            imageNum = 0;
        }

        adapter.addItem(ContextCompat.getDrawable(this, imgVending), ContextCompat.getDrawable(this, imgWarning), name, type, address, phone, "UserActivity");
        adapter.notifyDataSetChanged();
    }

    private void parsingString(String getListData) {
        if(!getListData.equals("]")) {
            StringTokenizer token = new StringTokenizer(getListData);
            String tables = token.nextToken(",");

            // String parsing
            String cnt = token.nextToken("[");
            cnt = cnt.substring(1);

            try {
                for (int i = 0; i < Integer.parseInt(cnt); i++) {
                    String temp = token.nextToken("<");
                    String machineId = token.nextToken(",");
                    machineId = machineId.substring(1);

                    String name = token.nextToken(",");
                    String type = token.nextToken(",");
                    String address = token.nextToken(",");
                    String phone = token.nextToken(",");
                    String error = token.nextToken(",");

                    if (i < Integer.parseInt(cnt) - 1) {
                        error = error.substring(0, error.length() - 1);
                    } else if (i < Integer.parseInt(cnt)) {
                        error = error.substring(0, error.length() - 2);
                    }

                    addToArrayList(name, type, address, phone, error);
                }
            } catch(NumberFormatException ne) {
                Toast.makeText(getApplicationContext(), "자판기를 생성해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class DatabaseManager extends AsyncTask<Void, Integer, Void> {

        private String param = null;
        private String data = null;

        public DatabaseManager(String getUser) {
            MainActivity.PHP_FILENAME = "db_list_machine_user.php";
            param = "getUser=" + getUser + "";
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
                Log.e("--- Manage DATA ", data);

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

            if(MainActivity.PHP_FILENAME.equals("db_list_machine_user.php")) {
                parsingString(data);
            }
        }
    }
}
