package com.danielhan.hencoderimitaion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.danielhan.hencoderimitaion.jikezan.JiKeZanView;

public class MainActivity extends AppCompatActivity {

    private JiKeZanView zanview;
    private EditText et;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zanview = (JiKeZanView) findViewById(R.id.zanview);
        et = (EditText) findViewById(R.id.et);
        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zanview.setCount(Integer.parseInt(et.getText().toString()));
            }
        });
    }
}
