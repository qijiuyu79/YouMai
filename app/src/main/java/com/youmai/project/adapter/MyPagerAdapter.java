package com.youmai.project.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/11/3 0003.
 */

public class MyPagerAdapter extends PagerAdapter {
    private List<View> list = null;
    public MyPagerAdapter(List<View> list) {
        this.list = list;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(list.get(position));
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getCount() {
        return list==null ? 0 : list.size();
    }
    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        view.addView(list.get(position));
        return list.get(position);
    }

    public void restoreState(Parcelable arg0, ClassLoader arg1) {

    }

    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }

}