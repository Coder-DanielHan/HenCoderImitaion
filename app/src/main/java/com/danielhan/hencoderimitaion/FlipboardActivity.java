package com.danielhan.hencoderimitaion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.danielhan.hencoderimitaion.flipboard.FlipboardView;

public class FlipboardActivity extends AppCompatActivity {

    private FlipboardView flipboardview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flipboard);
        flipboardview = (FlipboardView) findViewById(R.id.flipboardview);
    }
}
