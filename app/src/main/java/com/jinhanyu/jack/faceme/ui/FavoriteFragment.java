package com.jinhanyu.jack.faceme.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.adapter.FavoritePagerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by anzhuo on 2016/10/18.
 */
public class FavoriteFragment extends Fragment{
    FavoriteFragment1 child_one;
    FavoriteFragment1 child_two;
    List<Fragment> fragments;
    String[] titles = {"Following","Me"};
    FragmentManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorite_fragment, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        fragments = new ArrayList<>();
        child_one = new FavoriteFragment1();
        Bundle bundle = new Bundle();
        bundle.putInt("location",0);
        child_one.setArguments(bundle);
        fragments.add(child_one);
        child_two=new FavoriteFragment1();
        Bundle bundle2=new Bundle();
        bundle2.putInt("location",1);
        child_two.setArguments(bundle2);
        fragments.add(child_two);
        manager=getActivity().getSupportFragmentManager();
        FavoritePagerAdapter favoritePagerAdapter = new FavoritePagerAdapter(getFragmentManager(),fragments,titles);
        viewPager.setAdapter(favoritePagerAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);



    }


}
