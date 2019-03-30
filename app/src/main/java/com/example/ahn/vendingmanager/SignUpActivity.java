package com.example.ahn.vendingmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class SignUpActivity extends AppCompatActivity {

    private EditText editSignId, editSignPw1, editSignPw2, editSignName;
    private Button btOk, btCancel, btIdCheck;

    private ButtonEvent buttonEvent;
    private AlertDialog.Builder dialog;

    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("스마트 벤딩 매니저 회원가입");

        buttonEvent = new ButtonEvent();
        dialog = new AlertDialog.Builder(this);

        editSignId = (EditText)findViewById(R.id.edit_sign_id);
        editSignPw1 = (EditText)findViewById(R.id.edit_sign_pw1);
        editSignPw2 = (EditText)findViewById(R.id.edit_sign_pw2);
        editSignName = (EditText)findViewById(R.id.edit_sign_name);

        // 영문 자판 기본 설정
        editSignId.setPrivateImeOptions("defaultInputmode=english;");
        editSignPw1.setPrivateImeOptions("defaultInputmode=english;");
        editSignPw2.setPrivateImeOptions("defaultInputmode=english;");

        btOk = (Button)findViewById(R.id.bt_sign_ok);
        btCancel = (Button)findViewById(R.id.bt_sign_cancel);
        btIdCheck = (Button)findViewById(R.id.bt_id_check);

        btOk.setOnClickListener(buttonEvent);
        btCancel.setOnClickListener(buttonEvent);
        btIdCheck.setOnClickListener(buttonEvent);
    }

    class ButtonEvent implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_id_check :
                    if(!(editSignId.getText().toString().replace(" ", "").equals(""))) {
                        dbManager = new DatabaseManager(editSignId.getText().toString());
                        dbManager.execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case R.id.bt_sign_ok :
                    if(btIdCheck.isEnabled() == false) {
                        // 공백 체크
                        if (!(editSignId.getText().toString().replace(" ", "").equals(""))
                                && !(editSignPw1.getText().toString().replace(" ", "").equals(""))
                                && !(editSignPw2.getText().toString().replace(" ", "").equals(""))
                                && !(editSignName.getText().toString().replace(" ", "").equals(""))) {

                            dialog.setTitle("회원가입")
                                    .setMessage("입력하신 내용이 정확합니까?")
                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //
                                        }
                                    })
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (editSignPw1.getText().toString().equals(editSignPw2.getText().toString())) {
                                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                intent.putExtra("SIGN_ID", editSignId.getText().toString());
                                                intent.putExtra("SIGN_PW", editSignPw1.getText().toString());
                                                intent.putExtra("SIGN_NAME", editSignName.getText().toString());

                                                setResult(RESULT_OK, intent);
                                                finish();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .show();
                        } else {
                            Log.e("--- id", editSignId.getText().toString());
                            Log.e("--- pw1", editSignPw1.getText().toString());
                            Log.e("--- pw2", editSignPw2.getText().toString());
                            Log.e("--- name", editSignName.getText().toString());
                            Toast.makeText(getApplicationContext(), "빈 공간이 있습니다.\n다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "중복체크를 해주세요.", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case R.id.bt_sign_cancel :
                    setResult(RESULT_CANCELED);

                    finish();

                    break;
            }
        }
    }

    class DatabaseManager extends AsyncTask<Void, Integer, Void> {

        private String param = null;
        private String data = null;

        // DB 접속 생성자
        public DatabaseManager(String getId) {
            MainActivity.PHP_FILENAME = "db_id_check.php";
            param = "getId=" + getId + "";
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
                StringTokenizer token = new StringTokenizer(data);
                String temp = token.nextToken(",");
                data = token.nextToken();
                Log.e("--- getCheck", data);
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

            if(data.equals("ID Available")) {
                dialog.setTitle("아이디 중복체크")
                        .setMessage("해당 계정을 사용하시겠습니까?")
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editSignId.setEnabled(false);
                                btIdCheck.setEnabled(false);
                            }
                        })
                        .show();
            } else if(data.equals("ID Unavailable")) {
                Toast.makeText(getApplicationContext(), "아이디가 중복됩니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
