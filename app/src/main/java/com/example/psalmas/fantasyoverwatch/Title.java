package com.example.psalmas.fantasyoverwatch;

/**
 * Created by psalmas on 9/10/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


public class Title extends Activity {

    Button btLogin;
    TextView registerLink, freeAgentLink;
    EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);


        btLogin = (Button) findViewById(R.id.btLogin);
        registerLink = (TextView) findViewById(R.id.tvRegister);
        freeAgentLink = (TextView) findViewById(R.id.tvFreeAgentListing);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

        Intent freeAgentListingIntent = new Intent(Title.this, FreeAgentListing.class);
//        Title.this.startActivity(freeAgentListingIntent);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(Title.this, Register.class);
                Title.this.startActivity(registerIntent);

            }
        });

        freeAgentLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent freeAgentIntent = new Intent(Title.this, FreeAgentListing.class);
                Title.this.startActivity(freeAgentIntent);


            }
        });

    }

    public void OnLogin(View view) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String type = "login";

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, username, password);
    }
}
