package com.wjc.happyweather.modules.main.ui;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wjc.happyweather.R;
import com.wjc.happyweather.base.BaseFragment;
import com.wjc.happyweather.common.utils.RxUtil;
import com.wjc.happyweather.common.utils.SharedPreferenceUtil;
import com.wjc.happyweather.common.utils.ToastUtil;
import com.wjc.happyweather.common.utils.VersionUtil;
import com.wjc.happyweather.component.NotificationHelper;
import com.wjc.happyweather.component.RetrofitSingleton;
import com.wjc.happyweather.component.RxBus;
import com.wjc.happyweather.modules.main.adapter.WeatherAdapter;
import com.wjc.happyweather.modules.main.domain.ChangeCityEvent;
import com.wjc.happyweather.modules.main.domain.Weather;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Package_NAME : com.wjc.happyweather.modules.main.ui
 * File_NAME : MainFragment
 * Created by WJC on 2017/12/14 19:05
 * Describe : TODO
 */

public class MainFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiprefresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.iv_erro)
    ImageView mIvError;

    private static Weather mWeather = new Weather();
    private WeatherAdapter mAdapter;

    public AMapLocationClient mLocationClient;
    public AMapLocationClientOption mLocationOption;

    private View view;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.content_main, container, false);
            ButterKnife.bind(this, view);
        }
        mTsCreateView = true;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        new RxPermissions(getActivity())
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .doOnNext(o -> mRefreshLayout.setRefreshing(true))
                .doOnNext(granted -> {
//                    if (granted) {
//                        location();
//                    } else {
//                        load();
//                    }
                    load();
                    VersionUtil.checkVersion(getActivity());
                })
                .subscribe();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.getDefault()
                .toObservable(ChangeCityEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(event -> isVisible())
                .doOnNext(event -> {
                    mRefreshLayout.setRefreshing(true);
                    load();
                })
                .subscribe();
    }

    private void initView() {
        if (mRefreshLayout != null) {
            mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
            mRefreshLayout.setOnRefreshListener(
                    () -> mRefreshLayout.postDelayed(this::load, 1000));

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mAdapter = new WeatherAdapter(mWeather);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private void load() {
        fetchDataByNetWork()
                .doOnSubscribe(aLong->mRefreshLayout.setRefreshing(true))
                .doOnError(throwable -> {
                   mIvError.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    SharedPreferenceUtil.getInstance().setCityName("九江");
                    safeSetTitle("找不到城市呢");
                })
                .doOnNext(weather -> {
                   mIvError.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);

                    mWeather.status = weather.status;
                    mWeather.aqi = weather.aqi;
                    mWeather.basic = weather.basic;
                    mWeather.suggestion = weather.suggestion;
                    mWeather.now = weather.now;
                    mWeather.dailyForecast = weather.dailyForecast;
                    mWeather.hourlyForecast = weather.hourlyForecast;
                    safeSetTitle(weather.basic.city);
                    mAdapter.notifyDataSetChanged();
                    NotificationHelper.showWeatherNotification(getActivity(),weather);
                })
                .doOnComplete(()->{
                    mRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                    ToastUtil.showShort(getString(R.string.complete));
                })
                .subscribe();
    }

    /**
     * 从网络获取
     */
    private Observable<Weather> fetchDataByNetWork(){
        String cityName = SharedPreferenceUtil.getInstance().getCityName();
        return RetrofitSingleton.getInstance()
                .fetchWeather(cityName)
                .compose(RxUtil.fragmentLifecycle(this));
    }

    /**
     * 高德定位
     */
    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity());
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setWifiActiveScan(false);
        //设置定位间隔 单位为毫秒
        int autoUpdateTime = SharedPreferenceUtil.getInstance().getAutoUpdate();
        mLocationOption.setInterval((autoUpdateTime == 0 ? 100 : autoUpdateTime) * SharedPreferenceUtil.ONE_HOUR);
        mLocationClient.setLocationListener(aMapLocation -> {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    aMapLocation.getLocationType();
                    SharedPreferenceUtil.getInstance();
                } else {
                    if (isAdded()){
                        ToastUtil.showShort(getString(R.string.errorLocation));
                    }
                }
                load();
            }
        });
        mLocationClient.startLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();
    }

    /**
     * 懒加载 , 在视图创建前进行数据的加载操作
     */
    @Override
    protected void lazyLoad() {

    }

    public Weather getmWeather(){
        return mWeather;
    }
}
