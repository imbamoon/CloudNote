package com.example.administraor.cloudnote.activity.activity.controller;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administraor.cloudnote.R;
import com.example.administraor.cloudnote.activity.activity.Utils.NetworkState;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private String username;
    private EditText inputUsername, inputPwd;
    private Button btnLogin, btnRegister;
    public GateApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        app = (GateApplication) getApplication();

        btnLogin = (Button) findViewById(R.id.login_button);
        btnRegister = (Button) findViewById(R.id.register_button);
        inputUsername = (EditText) findViewById(R.id.login_username);
        inputPwd = (EditText) findViewById(R.id.login_psword);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                if (new NetworkState().isNetworkConnected(LoginActivity.this)){
                    final String Susername = inputUsername.getText().toString();
                    String Spwd = inputPwd.getText().toString();

                    if (Susername.equals("") || Spwd.equals("")) {
                        new AlertDialog.Builder(LoginActivity.this).setMessage("不能为空")
                                .setPositiveButton("确定", null)
                                .setCancelable(true)
                                .show();
                        break;
                    }

                    BmobUser user = new BmobUser();
                    user.setUsername(Susername);
                    user.setPassword(Spwd);
                    user.login(this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            app.setUsername(Susername);
                            Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(LoginActivity.this, "登陆失败：" + s, Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(LoginActivity.this, "当前无网络", Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.register_button:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        }
    }


}
