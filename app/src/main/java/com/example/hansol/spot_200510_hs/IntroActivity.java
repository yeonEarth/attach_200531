package com.example.hansol.spot_200510_hs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import Page1.Page1;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class IntroActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceStare) {
        super.onCreate(savedInstanceStare);
        setContentView(R.layout.activity_intro);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), Page1.class);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        },1000);
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}