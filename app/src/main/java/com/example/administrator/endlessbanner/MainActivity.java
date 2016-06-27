package com.example.administrator.endlessbanner;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Banner banner = (Banner) findViewById(R.id.banner);
        banner.setData(R.drawable.shaoshao, R.drawable.shaoyu, R.drawable.tianming,
                R.drawable.shilan, R.drawable.dashu, R.drawable.ronger);
    }
}
