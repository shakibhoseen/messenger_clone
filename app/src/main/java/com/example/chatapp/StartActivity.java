package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
   private Button logIn , resister;

   private FirebaseUser firebaseUser;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startt);

        logIn = findViewById(R.id.btn_login);
        resister= findViewById(R.id.btn_register);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
         if(firebaseUser!= null){
              Intent intent = new Intent(StartActivity.this, MainActivity.class);
              startActivity(intent);
              finish();

         }


        logIn.setOnClickListener(this);
        resister.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (R.id.btn_login == view.getId()){
            startActivity(new Intent(StartActivity.this, LogIn.class));

        }
        if (R.id.btn_register == view.getId()){
            startActivity(new Intent(StartActivity.this ,RegisterActivity.class));

        }
    }
}
