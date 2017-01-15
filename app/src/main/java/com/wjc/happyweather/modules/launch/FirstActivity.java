package com.wjc.happyweather.modules.launch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wjc.happyweather.modules.main.ui.MainActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class FirstActivity extends AppCompatActivity {
    private static final String TAG = FirstActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
        Observable.timer(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    MainActivity.launch(this);
                    finish();
                });
    }
}
