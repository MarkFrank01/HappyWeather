package com.wjc.happyweather.modules.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.wjc.happyweather.common.utils.SharedPreferenceUtil;
import com.wjc.happyweather.component.NotificationHelper;
import com.wjc.happyweather.component.RetrofitSingleton;
import com.wjc.happyweather.modules.main.domain.Weather;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class AutoUpdateService extends Service {

    private Disposable mDisposable;
    private boolean mIsUnSubscribed = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        synchronized (this) {
            unSubscribed();
            if (mIsUnSubscribed) {
                unSubscribed();
                if (SharedPreferenceUtil.getInstance().getAutoUpdate() != 0) {
                    mDisposable = Observable.interval(SharedPreferenceUtil.getInstance().getAutoUpdate(), TimeUnit.HOURS)
                            .doOnNext(new Consumer<Long>() {
                                @Override
                                public void accept(@NonNull Long aLong) throws Exception {
                                    mIsUnSubscribed = false;
                                    AutoUpdateService.this.fetchDataByNetWork();
                                }
                            })
                            .subscribe();
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    private void unSubscribed() {
        mIsUnSubscribed = true;
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    private void fetchDataByNetWork() {
        String cityName = SharedPreferenceUtil.getInstance().getCityName();
        RetrofitSingleton.getInstance()
                .fetchWeather(cityName)
                .subscribe(new Consumer<Weather>() {
                    @Override
                    public void accept(@NonNull Weather weather) throws Exception {
                        NotificationHelper.showWeatherNotification(AutoUpdateService.this, weather);
                    }
                });
    }
}
