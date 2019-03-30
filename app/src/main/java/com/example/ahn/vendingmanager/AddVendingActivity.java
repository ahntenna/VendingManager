package com.example.ahn.vendingmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.security.acl.Owner;

public class AddVendingActivity extends AppCompatActivity {

    private Button btAdd, btCancel;
    private EditText editName, editAddress, editOwnerPhone;
    private RadioGroup radioGroup;

    private String radioStatus;
    private String LOGIN_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vending);
        setTitle("자판기 추가");

        Intent intent = getIntent();
        LOGIN_ID = intent.getStringExtra("LOGIN_ID");

        editName = (EditText)findViewById(R.id.edit_add_name);
        editAddress = (EditText)findViewById(R.id.edit_add_address);
        editOwnerPhone = (EditText)findViewById(R.id.edit_add_owner_phone);

        radioGroup = (RadioGroup)findViewById(R.id.rg_radio);

        btAdd = (Button)findViewById(R.id.bt_add);
        btCancel = (Button)findViewById(R.id.bt_cancel);

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton rb = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());

                if(rb.getText().toString().equals("식품")) {
                    radioStatus = "식품";
                } else if(rb.getText().toString().equals("생필품")) {
                    radioStatus = "생필품";
                } else if(rb.getText().toString().equals("기타")) {
                    radioStatus = "기타";
                }

                Intent intent = new Intent(AddVendingActivity.this, ManageActivity.class);
                intent.putExtra("ADD_NAME", editName.getText().toString());
                intent.putExtra("ADD_ADDRESS", editAddress.getText().toString());
                intent.putExtra("ADD_TYPE", radioStatus);
                intent.putExtra("ADD_OWNER_PHONE", editOwnerPhone.getText().toString());
                intent.putExtra("ADD_ERROR", "0");
                intent.putExtra("ADD_OWNER", LOGIN_ID);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
