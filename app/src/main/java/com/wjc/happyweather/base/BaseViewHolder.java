package com.wjc.happyweather.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Package_NAME : com.wjc.happyweather.base
 * File_NAME : BaseViewHolder
 * Created by WJC on 2017/12/14 14:02
 * Describe : TODO
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView){
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    protected abstract void bind(T t);
}
