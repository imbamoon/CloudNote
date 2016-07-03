package com.example.administraor.cloudnote.activity.activity.controller;

import android.annotation.SuppressLint;
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
import com.example.administraor.cloudnote.activity.activity.model._User;

import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnRegisterBack, btnFinishRegister;
    private EditText inputRegisterId, inputRegisterPwd, inputRegisterRePwd;
    private String inputUsername,inputPwd,inputRePwd;
    public GateApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegisterBack = (Button) findViewById(R.id.register_back_button);
        btnFinishRegister = (Button) findViewById(R.id.finish_register_button);
        inputRegisterId = (EditText) findViewById(R.id.register_username);
        inputRegisterPwd = (EditText) findViewById(R.id.register_psword);
        inputRegisterRePwd = (EditText) findViewById(R.id.register_repsword);

        btnRegisterBack.setOnClickListener(this);
        btnFinishRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_back_button:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                break;

            case R.id.finish_register_button:
                inputUsername = inputRegisterId.getText().toString();
                inputPwd = inputRegisterPwd.getText().toString();
                inputRePwd = inputRegisterRePwd.getText().toString();
                if (inputUsername.equals("") || inputPwd.equals("") || inputRePwd.equals("")) {
                    new AlertDialog.Builder(RegisterActivity.this).setMessage("不能为空")
                            .setPositiveButton("确定", null)
                            .setCancelable(true)
                            .show();
                    break;
                } else if (!inputPwd.equals(inputRePwd)) {
                    new AlertDialog.Builder(RegisterActivity.this).setMessage("两次密码不一致")
                            .setPositiveButton("确定", null)
                            .setCancelable(true)
                            .show();
                    break;
                } else if (new NetworkState().isNetworkConnected(this)) {
                    signUp();
                } else {
                    Toast.makeText(this, "当前无网络", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 注册
     */
    @SuppressLint("UseValueOf")
    private void signUp(){
        final _User user = new _User();
        user.setUsername(inputUsername);
        user.setPassword(inputPwd);
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(RegisterActivity.this,"注册成功:" + user.getObjectId(),Toast.LENGTH_SHORT).show();
                app = (GateApplication)getApplication();
                app.setUsername(inputUsername);
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(RegisterActivity.this,"注册失败:" + s,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
