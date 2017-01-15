package com.wjc.happyweather.modules.main.ui;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wjc.happyweather.R;
import com.wjc.happyweather.base.ToolbarActivity;
import com.wjc.happyweather.common.IntentKey;
import com.wjc.happyweather.modules.main.adapter.WeatherAdapter;
import com.wjc.happyweather.modules.main.domain.Weather;

import butterknife.BindView;

public class DetailCityActivity extends ToolbarActivity {

    @BindView(R.id.recyclerview)
    RecyclerView mRecycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewWithData();
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_detail;
    }

    @Override
    public boolean canBack() {
        return true;
    }

    private void initViewWithData() {
        Intent intent = getIntent();
        Weather weather = (Weather) intent.getSerializableExtra(IntentKey.WEATHER);
        if (weather == null) {
            finish();
        }
        safeSetTitle(weather.basic.city);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        WeatherAdapter weatherAdapter = new WeatherAdapter(weather);
        mRecycleView.setAdapter(weatherAdapter);
    }

    public static void launch(Context context, Weather weather) {
        Intent intent = new Intent(context, DetailCityActivity.class);
        intent.putExtra(IntentKey.WEATHER,weather);
        context.startActivity(intent);
    }

}
