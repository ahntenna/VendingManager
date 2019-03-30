package com.example.ahn.vendingmanager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends Activity {

    private EditText editId, editPw;
    private Button btLogin, btSign;
    private TextView textURL;

    private DatabaseManager dbManager;

    private String LOGIN_ID = null;
//    public static String DB_URL = "http://10.0.2.2:80/vending/";
    public final static String DB_URL = "http://1.245.49.29:80/vending/";
    public static String PHP_FILENAME = "";
    public static int AVAILABLE_TABLES = 0;
    public static String GET_TOKEN = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("notice");
        FirebaseInstanceId.getInstance().getToken();

        dbManager = new DatabaseManager();
        dbManager.execute();

        GET_TOKEN = FirebaseInstanceId.getInstance().getToken();
//        Log.e("--- TOKEN", GET_TOKEN);

        editId = (EditText)findViewById(R.id.txt_getId);
        editPw = (EditText)findViewById(R.id.txt_getPwd);
        btLogin = (Button)findViewById(R.id.bt_login);
        btSign = (Button)findViewById(R.id.bt_sign);

        textURL = (TextView)findViewById(R.id.text_url);
        textURL.setText(Html.fromHtml("<a href=\"" +
                "http://1.245.49.22:8181/index.jsp" +
                "\">모바일 홈페이지</a>"));
        textURL.setLinkTextColor(Color.BLACK);
        textURL.setMovementMethod(LinkMovementMethod.getInstance());

        // 영문 자판 기본 설정
        editId.setPrivateImeOptions("defaultInputmode=english;");
        editPw.setPrivateImeOptions("defaultInputmode=english;");

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbManager = new DatabaseManager(editId.getText().toString(), editPw.getText().toString());
                dbManager.execute();
            }
        });

        btSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivityForResult(intent, 1111);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 회원가입
        if(requestCode == 1111 & resultCode == RESULT_OK) {
            String getId = data.getStringExtra("SIGN_ID").toString();
            String getPw = data.getStringExtra("SIGN_PW").toString();
            String getName = data.getStringExtra("SIGN_NAME").toString();

            dbManager = new DatabaseManager(getId, getPw, getName, GET_TOKEN);
            dbManager.execute();
        }
    }

    class DatabaseManager extends AsyncTask<Void, Integer, Void> {

        private String param = null;
        private String data = null;

        // DB 접속 생성자
        public DatabaseManager() {
            PHP_FILENAME = "db_connection.php";
            param = "";
        }

        // 로그인 생성자
        public DatabaseManager(String getId, String getPw) {
            PHP_FILENAME = "db_login.php";
            param = "getId=" + getId + "&getPw=" + getPw + "";
        }

        // 로그인 회원 구별 생성자
        public DatabaseManager(String getId) {
            PHP_FILENAME = "db_login_typecheck.php";
            param = "getId=" + getId + "";
        }

        // 회원가입 생성자
        public DatabaseManager(String getId, String getPw, String getName, String getToken) {
            PHP_FILENAME = "db_sign.php";
            param = "getId=" + getId + "&getPw=" + getPw + "&getName=" + getName + "&getToken=" + getToken + "";
        }

        @Override
        protected Void doInBackground(Void... unused) {
            try {
                /* 서버연결 */
                URL url = new URL(DB_URL + PHP_FILENAME);             // it is worked
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

            if(PHP_FILENAME.equals("db_connection.php")) {
                StringTokenizer token = new StringTokenizer(data);
                String data = token.nextToken(",");
                AVAILABLE_TABLES = Integer.parseInt(data);
                Log.e("--- Main Tables ", String.valueOf(AVAILABLE_TABLES));
            }
            if(PHP_FILENAME.equals("db_login.php")) {
                if((data.substring(2)).equals("Login Success")) {
                    dbManager = new DatabaseManager(editId.getText().toString());
                    dbManager.execute();
                }
            }
            if((data.substring(2)).equals("Admin")) {
                Intent intent = new Intent(MainActivity.this, ManageActivity.class);
                LOGIN_ID = editId.getText().toString();
                intent.putExtra("LOGIN_ID", LOGIN_ID);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "[Admin] 로그인 성공.\n" + editId.getText().toString() + "님 반갑습니다.", Toast.LENGTH_SHORT).show();
            }
            if((data.substring(2)).equals("User")) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                LOGIN_ID = editId.getText().toString();
                intent.putExtra("LOGIN_ID", LOGIN_ID);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "[User] 로그인 성공.\n" + editId.getText().toString() + "님 반갑습니다.", Toast.LENGTH_SHORT).show();
            }
            if((data.substring(2)).equals("Sign Success")) {
                Toast.makeText(getApplicationContext(), "회원가입 성공.\n스마트 벤딩 매니저에 오신 것을 환영합니다..", Toast.LENGTH_SHORT).show();
            }
            if((data.substring(2)).equals("mysql query error")) {
                Toast.makeText(getApplicationContext(), "회원가입 실패.\n아이디를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
            if((data.substring(2)).equals("No Query")) {
                Toast.makeText(getApplicationContext(), "로그인 실패.\n계정 또는 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
            if((data.substring(2)).equals("Password Incorrect")) {
                Toast.makeText(getApplicationContext(), "로그인 실패.\n비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}