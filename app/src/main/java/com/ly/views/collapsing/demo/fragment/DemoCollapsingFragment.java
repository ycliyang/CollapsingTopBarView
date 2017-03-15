package com.ly.views.collapsing.demo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ly.views.collapsing.R;
import com.ly.views.collapsing.demo.fragment.dummy.DummyContent;

import java.util.ArrayList;

/**
 * Created by liyang on 2017/3/10.
 */
public class DemoCollapsingFragment extends Fragment {
    ViewPager mPager;

    TextView toolbar1, toolbar2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.collapsing_dome_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPager = (ViewPager) view.findViewById(R.id.ViewPager);
        toolbar1 = (TextView) view.findViewById(R.id.toolbar1);
        toolbar2 = (TextView) view.findViewById(R.id.toolbar2);
        ArrayList fragmentList = new ArrayList<Fragment>();
        MyListFragment secondFragment = MyListFragment.newInstance(1);
        MyListFragment thirdFragment = MyListFragment.newInstance(1);
        MyListFragment fourthFragment = MyListFragment.newInstance(1);
        fragmentList.add(secondFragment);
        fragmentList.add(thirdFragment);
        fragmentList.add(fourthFragment);

        //给ViewPager设置适配器
        mPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
        mPager.setCurrentItem(0);//设置当前显示标签页为第一页

    }



    static class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

    }



    public static class MyListFragment extends Fragment {

        // TODO: Customize parameters

        private static final String ARG_COLUMN_COUNT = "column-count";
        // TODO: Customize parameters
        private int mColumnCount = 1;

        // TODO: Customize parameter initialization
        @SuppressWarnings("unused")
        public static MyListFragment newInstance(int columnCount) {
            MyListFragment fragment = new MyListFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_COLUMN_COUNT, columnCount);
            fragment.setArguments(args);
            return fragment;
        }


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.collapsing_demo_content, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            // Set the adapter
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                if (mColumnCount <= 1) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                }
                recyclerView.setAdapter(new DemoCollapsingContentRecyclerViewAdapter(DummyContent.ITEMS));
            }
        }
    }
}
