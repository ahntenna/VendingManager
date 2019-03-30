package com.example.ahn.vendingmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    private FontSizeSetting fontSetting;

    private Button btFontDefault, btFontSmall, btFontBig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        fontSetting = new FontSizeSetting();

        btFontDefault = (Button)findViewById(R.id.bt_set_font_size_default);
        btFontSmall = (Button)findViewById(R.id.bt_set_font_size_small);
        btFontBig = (Button)findViewById(R.id.bt_set_font_size_big);

        btFontDefault.setOnClickListener(fontSetting);
        btFontSmall.setOnClickListener(fontSetting);
        btFontBig.setOnClickListener(fontSetting);
    }

    class FontSizeSetting implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Button bt = (Button)view;

            switch (view.getId()) {
                case R.id.bt_set_font_size_default :
                    Toast.makeText(getApplicationContext(), bt.getText().toString(), Toast.LENGTH_SHORT).show();

                    break;

                case R.id.bt_set_font_size_small :
                    Toast.makeText(getApplicationContext(), bt.getText().toString(), Toast.LENGTH_SHORT).show();

                    break;

                case R.id.bt_set_font_size_big :
                    Toast.makeText(getApplicationContext(), bt.getText().toString(), Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    }
}
