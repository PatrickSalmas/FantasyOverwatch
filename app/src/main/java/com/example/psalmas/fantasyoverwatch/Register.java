package com.example.psalmas.fantasyoverwatch;

/**
 * Created by psalmas on 9/10/2017.
 */
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//import com.android.volley.Response;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.locks.ReentrantLock;

public class Register extends AppCompatActivity {
    EditText name, age, username, password, confirm_password;
    Button register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.etName);
        age = (EditText) findViewById(R.id.etAge);
        username = (EditText) findViewById(R.id.etUsername);
        password = (EditText) findViewById(R.id.etPassword);
        confirm_password = (EditText) findViewById(R.id.etConfirmPassword);
        register = (Button) findViewById(R.id.btRegister);

    }

    public void OnReg(View view) {
        String str_name = name.getText().toString();
        String str_age = age.getText().toString();
        String str_username = username.getText().toString();
        String str_password = password.getText().toString();
        String type = "register";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, str_name,str_age,str_username,str_password);




    }
}
