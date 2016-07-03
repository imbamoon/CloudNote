package com.example.administraor.cloudnote.activity.activity.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administraor.cloudnote.R;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private String username;
    private TextView tvUsername;
    private Button btnLogOut;
    private Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        tvUsername = (TextView)findViewById(R.id.username);
        btnLogOut = (Button)findViewById(R.id.log_out);
        btnBack = (Button)findViewById(R.id.back);
        username = getIntent().getExtras().getString("username");
        tvUsername.setText("用户名："+username);
        btnLogOut.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.log_out:
                GateApplication app = (GateApplication)getApplication();
                app.setUsername("");
                startActivity(new Intent(UserActivity.this,MainActivity.class));
                break;
            case R.id.back:
                startActivity(new Intent(UserActivity.this,MainActivity.class));
                break;
        }

    }
}
