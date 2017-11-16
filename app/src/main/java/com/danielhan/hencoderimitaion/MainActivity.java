package com.danielhan.hencoderimitaion;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * @author DanielHan
 * @date 2017/11/16
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_jikezan;
    private Button btn_flipboard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_jikezan = (Button) findViewById(R.id.btn_jikezan);
        btn_flipboard = (Button) findViewById(R.id.btn_flipboard);

        btn_jikezan.setOnClickListener(this);
        btn_flipboard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_jikezan:
                startActivity(new Intent(this, JiKeZanActivity.class));
                break;
            case R.id.btn_flipboard:
                startActivity(new Intent(this, FlipboardActivity.class));
                break;
        }
    }
}
