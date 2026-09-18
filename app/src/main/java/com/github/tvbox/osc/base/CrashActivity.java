package com.github.tvbox.osc.base;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

public class CrashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.BLACK);

        TextView tv = new TextView(this);
        tv.setTextColor(Color.RED);
        tv.setTextSize(13);
        tv.setPadding(25, 25, 25, 25);

        String error = getIntent().getStringExtra("error");
        tv.setText("【TVBox 崩溃排查诊断 - Android 4.2.2 x86】\n\n" + (error != null ? error : "未知错误"));

        scrollView.addView(tv);
        setContentView(scrollView);
    }
}
