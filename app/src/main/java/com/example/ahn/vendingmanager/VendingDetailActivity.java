package com.example.ahn.vendingmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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

public class VendingDetailActivity extends AppCompatActivity {

    private ListView listView;
    private Button btCall, btMap, btProd, btPrice;
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
    private String oldProdName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vending_detail);

        ListViewAdapter.TAG = "VendingDetailActivity";

        btCall = (Button)findViewById(R.id.bt_call);
        btMap = (Button)findViewById(R.id.bt_map);
        btProd = (Button)findViewById(R.id.bt_prod);
        btPrice = (Button)findViewById(R.id.bt_price);

        getIntent = getIntent();
        setTitle(getIntent.getStringExtra("NAME").toString());

        adapter = new ListViewAdapter("VendingDetailActivity");
        dialog = new AlertDialog.Builder(this);

        initList();
        listView = (ListView)findViewById(R.id.list_view_stock);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, final int position, long id) {
                // get item
                ListViewItem item = (ListViewItem)parent.getItemAtPosition(position);

                String prodName = item.getProdName();
                oldProdName = prodName;
                String prodPrice = item.getProdPrice();
                String prodStock = item.getProdStock();

                editName.setText(prodName.toString());
                editPrice.setText(prodPrice.toString());
                editStock.setText(prodStock.toString());

                // Dialog
                try {
                    if(MODE_TAG.equals("EDIT")) {
                        showDialog(layout, (position+1));
                    } else if(MODE_TAG.equals("DELETE")) {
                        showDialog(layout, 0);
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "모드 선택을 해주세요.", Toast.LENGTH_SHORT).show();
                }
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
                Intent intent = new Intent(VendingDetailActivity.this, MapActivity.class);
                intent.putExtra("NAME", getIntent.getStringExtra("NAME"));
                // address => lat, lng 로 수정 필요
                intent.putExtra("ADDRESS", getIntent.getStringExtra("ADDRESS"));

                if(intent != null) {
                    startActivity(intent);
                }
            }
        });

        btProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VendingDetailActivity.this, StatusActivity.class);

                if(intent != null) {
                    intent.putExtra("MACHINE_ID", getIntent.getStringExtra("MACHINE_ID"));
                    intent.putExtra("Key", "prod"); // 물품별 키워드 전송
                    intent.putExtra("Machine", "vending_" + getIntent.getStringExtra("MACHINE_ID")); // 클릭된 자판기에대한 정보 전송(동적으로 수정)
                    startActivity(intent);
                }
            }
        });

        btPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VendingDetailActivity.this, StatusActivity.class);

                if(intent != null) {
                    intent.putExtra("MACHINE_ID", getIntent.getStringExtra("MACHINE_ID"));
                    intent.putExtra("Key", "price"); // 금액별 키워드 전송
                    intent.putExtra("Machine", "vending_" + getIntent.getStringExtra("MACHINE_ID")); // 클릭된 자판기에대한 정보 전송(동적으로 수정)
                    startActivity(intent);
                }
            }
        });
    }

    // Back Key 눌렀을 경우
    @Override
    public void onBackPressed() {
        ListViewAdapter.TAG = "ManageActivity";

        super.onBackPressed();
    }

    // Option Menu Create
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stock, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // If Select Option Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add :
                MODE_TAG = "ADD";

                editName.setText("");
                editPrice.setText("");
                editStock.setText("");

                editName.setEnabled(true);
                editPrice.setEnabled(true);
                editStock.setEnabled(true);

                showDialog(layout, 0);

                break;

            case R.id.item_edit :
                MODE_TAG = "EDIT";

                editName.setEnabled(true);
                editPrice.setEnabled(true);
                editStock.setEnabled(true);

                Toast.makeText(getApplicationContext(), "품목을 눌러 수정해주세요.", Toast.LENGTH_SHORT).show();

                break;

            case R.id.item_del :
                MODE_TAG = "DELETE";

                editName.setEnabled(false);
                editPrice.setEnabled(false);
                editStock.setEnabled(false);

                Toast.makeText(getApplicationContext(), "품목을 눌러 삭제 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog(View layout, final int position) {
        String msg = "";

        if(MODE_TAG.equals("ADD")) {
            msg = "물품 추가";
        } else if(MODE_TAG.equals("EDIT")) {
            msg = "수량 변경";
        } else if(MODE_TAG.equals("DELETE")) {
            msg = "물품 삭제";
        }

        if(this.layout.getParent() != null) {
            Log.e("--- removeView", "start");

            ViewGroup vg = (ViewGroup)this.layout.getParent();
            vg.removeView(this.layout);
        }

        dialog.setTitle("재고 관리")
                .setView(layout)
                .setMessage(msg)
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (MODE_TAG.equals("ADD")) {
                            dbManager = new DatabaseManager(getIntent.getStringExtra("MACHINE_ID").toString(),
                                    getIntent.getStringExtra("OWNER").toString(), editName.getText().toString(),
                                    editPrice.getText().toString(), editStock.getText().toString());
                            dbManager.execute();
                        } else if(MODE_TAG.equals("EDIT")) {
                            dbManager = new DatabaseManager(getIntent.getStringExtra("MACHINE_ID").toString(), oldProdName,
                                    editName.getText().toString(), Integer.parseInt(editPrice.getText().toString()), Integer.parseInt(editStock.getText().toString()));
                            dbManager.execute();
                        } else if(MODE_TAG.equals("DELETE")) {
                            dbManager = new DatabaseManager(getIntent.getStringExtra("MACHINE_ID").toString(),
                                    editName.getText().toString());
                            dbManager.execute();
                        }
                    }
                })
                .show();
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

        // 물품 추가 생성자
        public DatabaseManager(String vending, String owner, String prodName, String prodPrice, String prodStock) {
            MainActivity.PHP_FILENAME = "add_prod.php";
            param = "getVending=vending_" + vending + "&getOwner=" + owner +  "&getProdName=" + prodName + "&getProdPrice=" + prodPrice + "&getProdStock=" + prodStock + "";
        }

        // 물품 편집 생성자
        public DatabaseManager(String vending, String oldProdName, String prodName, int prodPrice, int prodStock) {
            MainActivity.PHP_FILENAME = "edit_prod.php";
            param = "getVending=vending_" + vending + "&getOldProdName=" + oldProdName + "&getProdName=" + prodName + "&getProdPrice=" + prodPrice + "&getProdStock=" + prodStock + "";
        }

        // 물품 삭제 생성자
        public DatabaseManager(String vending, String prodName) {
            MainActivity.PHP_FILENAME = "del_prod.php";
            param = "getVending=vending_" + vending +  "&getProdName=" + prodName +  "";
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
            } else if(MainActivity.PHP_FILENAME.equals("add_prod.php")) {
                Toast.makeText(getApplicationContext(), "품목이 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                adapter.clear();
                initList();
                MODE_TAG = null;
            } else if(MainActivity.PHP_FILENAME.equals("edit_prod.php")) {
                Toast.makeText(getApplicationContext(), "품목이 수정 되었습니다.", Toast.LENGTH_SHORT).show();
                adapter.clear();
                initList();
                MODE_TAG = null;
            } else if(MainActivity.PHP_FILENAME.equals("del_prod.php")) {
                Toast.makeText(getApplicationContext(), "품목이 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                adapter.clear();
                initList();
                MODE_TAG = null;
            }
        }
    }
}
