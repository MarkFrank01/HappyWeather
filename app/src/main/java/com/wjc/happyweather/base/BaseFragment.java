package com.wjc.happyweather.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.trello.rxlifecycle2.components.support.RxFragment;

/**
 * Package_NAME : com.wjc.happyweather.base
 * File_NAME : BaseFragment
 * Created by WJC on 2017/1/7 13:53
 * Describe : TODO
 */

public abstract class BaseFragment extends RxFragment {

    protected boolean mTsCreateView = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mTsCreateView) {
            lazyLoad();
        }
    }

    /**
     * 懒加载 , 在视图创建前进行数据的加载操作
     */
    protected abstract void lazyLoad();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()) {
            lazyLoad();
        }
    }

    protected void safeSetTitle(String title) {
        ActionBar appBarLayout = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (appBarLayout != null) {
            appBarLayout.setTitle(title);
        }
    }
}
