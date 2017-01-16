package com.wjc.happyweather.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;

import com.wjc.happyweather.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Package_NAME : com.wjc.happyweather.base
 * File_NAME : ToolbarActivity
 * Created by WJC on 2017/1/10 14:09
 * Describe : TODO
 */

public abstract class ToolbarActivity extends BaseActivity {

    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    public void onToolbarClick() {
    }

//    protected AppBarLayout mAppBar;
//    protected Toolbar mToolbar;
    protected boolean mIsHidden = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mAppBar = (AppBarLayout) findViewById(R.id.appbar_layout);
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        ButterKnife.bind(this);
        if (mToolbar == null || mAppBar == null) {
            throw new IllegalArgumentException(
                    "The subclass of ToolbarActivity must contain a toolbar.");
        }
        mToolbar.setOnClickListener(v->onToolbarClick());
        setSupportActionBar(mToolbar);
        if (canBack()) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            mAppBar.setElevation(10.6f);
        }
    }

    public boolean canBack(){
        return false;
    }

    protected void setAppBarAlpha(float alpha){
        mAppBar.setAlpha(alpha);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    protected void hideOrShowToolbar() {
        mAppBar.animate()
                .translationY(mIsHidden ? 0 : -mAppBar.getHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        mIsHidden = !mIsHidden;
    }

    protected void safeSetTitle(String title) {
        ActionBar appBarLayout = getSupportActionBar();
        if (appBarLayout != null) {
            appBarLayout.setTitle(title);
        }
    }
}
