package com.wjc.happyweather.modules.city.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wjc.happyweather.R;
import com.wjc.happyweather.base.BaseViewHolder;
import com.wjc.happyweather.component.AnimRecyclerViewAdapter;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Package_NAME : com.wjc.happyweather.modules.city.adapter
 * File_NAME : CityAdapter
 * Created by WJC on 2017/12/18 18:18
 * Describe : TODO
 */

public class CityAdapter extends AnimRecyclerViewAdapter<CityAdapter.CityViewHolder>{


    private Context mContext;
    private ArrayList<String> mDataList;
    private OnRecyclerViewItemClickListener mOnItemClickListener;

    public CityAdapter(Context mContext, ArrayList<String> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int pos);
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CityViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_city,parent,false));
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        holder.bind(mDataList.get(position));
        holder.mCardView.setOnClickListener(v -> mOnItemClickListener.onItemClick(v,position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void setmOnItemClickListener(OnRecyclerViewItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    class CityViewHolder extends BaseViewHolder<String>{
        @BindView(R.id.item_city)
        TextView mItemCity;
        @BindView(R.id.cardView)
        CardView mCardView;

        public CityViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bind(String s) {
            mItemCity.setText(s);
        }
    }
}
