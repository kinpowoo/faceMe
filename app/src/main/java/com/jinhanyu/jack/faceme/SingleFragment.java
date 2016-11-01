package com.jinhanyu.jack.faceme;


import com.jinhanyu.jack.faceme.ui.FavoriteFragment;
import com.jinhanyu.jack.faceme.ui.FlowFragment;
import com.jinhanyu.jack.faceme.ui.MainFragment;
import com.jinhanyu.jack.faceme.ui.PostActivity;
import com.jinhanyu.jack.faceme.ui.UserFragment;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class SingleFragment {
    private static MainFragment mainFragment;
    private static FlowFragment flowFragment;
    private static PostActivity postFragment;
    private static FavoriteFragment favoriteFragment;
    private static UserFragment userFragment;

    public static MainFragment getMainFragment(){
        if(mainFragment==null){
            mainFragment=new MainFragment();
        }
        return mainFragment;
    }

    public static FlowFragment getFlowFragment(){
        if(flowFragment==null){
            flowFragment=new FlowFragment();
        }
        return flowFragment;
    }

    public static PostActivity getPostFragment(){
        if(postFragment==null){
            postFragment=new PostActivity();
        }
        return postFragment;
    }

    public static FavoriteFragment getFavoriteFragment(){
        if(favoriteFragment==null){
            favoriteFragment=new FavoriteFragment();
        }
        return favoriteFragment;
    }

    public static UserFragment getUserFragment(){
        if(userFragment==null){
            userFragment=new UserFragment();
        }
        return userFragment;
    }
}
