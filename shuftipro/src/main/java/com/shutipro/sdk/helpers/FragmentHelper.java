package com.shutipro.sdk.helpers;

import android.support.v7.app.AppCompatActivity;

public class FragmentHelper {

    public static void addFragment(AppCompatActivity activity, android.support.v4.app.Fragment fragment, int containerId, String fragmentTag){

        android.support.v4.app.FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerId,fragment,fragmentTag);
        fragmentTransaction.commit();

    }

    public static boolean removeFragment(AppCompatActivity activity,String tag){

        android.support.v4.app.Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment != null) {
            activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            return true;
        }

        return false;
    }
}
