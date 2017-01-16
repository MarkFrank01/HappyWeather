package com.wjc.happyweather.modules.city.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wjc.happyweather.R;
import com.wjc.happyweather.base.ToolbarActivity;
import com.wjc.happyweather.common.C;
import com.wjc.happyweather.common.Irrelevant;
import com.wjc.happyweather.common.utils.RxUtil;
import com.wjc.happyweather.common.utils.SharedPreferenceUtil;
import com.wjc.happyweather.component.OrmLite;
import com.wjc.happyweather.component.RxBus;
import com.wjc.happyweather.modules.city.adapter.CityAdapter;
import com.wjc.happyweather.modules.city.db.DBManager;
import com.wjc.happyweather.modules.city.db.WeatherDB;
import com.wjc.happyweather.modules.city.domain.City;
import com.wjc.happyweather.modules.city.domain.Province;
import com.wjc.happyweather.modules.main.domain.ChangeCityEvent;
import com.wjc.happyweather.modules.main.domain.CityORM;
import com.wjc.happyweather.modules.main.domain.MultiUpdateEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Package_NAME : com.wjc.happyweather.modules.city.ui
 * File_NAME : ChoiceCityActivity
 * Created by WJC on 2017/12/18 17:38
 * Describe : TODO
 */

public class ChoiceCityActivity extends ToolbarActivity {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private ArrayList<String> dataList = new ArrayList<>();
    private Province selectedProvince;
    private List<Province> provincesList = new ArrayList<>();
    private List<City> cityList;
    private CityAdapter mCityAdapter;

    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    private int currentLevel;

    private boolean isChecked = false;

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_choice_city;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (mProgressBar != null) {
//            mProgressBar.setVisibility(View.VISIBLE);
//        }

        Observable.create(emitter -> {
            DBManager.getInstance().openDatabase();
            emitter.onNext(Irrelevant.INSTANCE);
            emitter.onComplete();
        })
                .compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .doOnNext(o -> {
                    initRecycleView();
                    queryProvinces();
                })
                .doOnError(o-> Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show())
                .subscribe();

        Intent intent = getIntent();
        isChecked = intent.getBooleanExtra(C.MULTI_CHECK, false);
        if (isChecked && SharedPreferenceUtil.getInstance().getBoolean("Tips", true)) {
            showTips();
        }
    }

    private void initRecycleView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mCityAdapter = new CityAdapter(this, dataList);
        mRecyclerView.setAdapter(mCityAdapter);

        mCityAdapter.setmOnItemClickListener((view, pos) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provincesList.get(pos);
                mRecyclerView.smoothScrollToPosition(0);
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                String city = SharedPreferenceUtil.getInstance().getCityName();
                if (isChecked) {
                    OrmLite.getInstance().save(new CityORM(city));
                    RxBus.getDefault().post(new MultiUpdateEvent());
                } else {
                    SharedPreferenceUtil.getInstance().setCityName(city);
                    RxBus.getDefault().post(new ChangeCityEvent());
                }
                quit();
            }
        });
    }

    /**
     * 查询全国所有的省，从数据库查询
     */
    private void queryProvinces() {
        getToolbar().setTitle("选择省份");
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<String> emitter) throws Exception {
                if (provincesList.isEmpty()) {
                    provincesList.addAll(WeatherDB.loadProvinces(DBManager.getInstance().getDatabase()));
                }
                dataList.clear();
                for (Province province : provincesList) {
                    emitter.onNext(province.mProName);
                }
                emitter.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .compose(RxUtil.ioF())
                .compose(RxUtil.activityLifecycleF(this))
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String proName) throws Exception {
                        dataList.add(proName);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        mProgressBar.setVisibility(View.GONE);
                        currentLevel = LEVEL_PROVINCE;
                        mCityAdapter.notifyDataSetChanged();
                    }
                })
                .subscribe();
    }

    /**
     * 从选中的省份中查询城市
     */
    private void queryCities() {
        getToolbar().setTitle("选择城市");
        dataList.clear();
        mCityAdapter.notifyDataSetChanged();

        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<String> e) throws Exception {
                cityList = WeatherDB.loadCities(DBManager.getInstance().getDatabase(), selectedProvince.mProSort);
                for (City city : cityList) {
                    e.onNext(city.mCityName);
                }
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .compose(RxUtil.ioF())
                .compose(RxUtil.activityLifecycleF(this))
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        dataList.add(s);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        currentLevel = LEVEL_CITY;
                        mCityAdapter.notifyDataSetChanged();
                        mRecyclerView.smoothScrollToPosition(0);
                    }
                })
                .subscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.multi_city_menu, menu);
        menu.getItem(0).setChecked(isChecked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.multi_check) {
            if (isChecked) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
            }
            isChecked = item.isChecked();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_PROVINCE) {
            quit();
        } else {
            queryProvinces();
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBManager.getInstance().closeDatabase();
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, ChoiceCityActivity.class));
    }

    private void showTips() {
        new AlertDialog.Builder(this)
                .setTitle("多城市管理模式")
                .setMessage("您现在是多城市管理模式,直接点击即可新增城市.如果暂时不需要添加,"
                        + "在右上选项中关闭即可像往常一样操作.\n因为 api 次数限制的影响,多城市列表最多三个城市.(๑′ᴗ‵๑)")
                .setPositiveButton("我知道啦", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("不要你提醒我，哼", (dialog, which) -> SharedPreferenceUtil.getInstance().putBoolean("Tips", false))
                .show();
    }

    private void quit() {
        ChoiceCityActivity.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
