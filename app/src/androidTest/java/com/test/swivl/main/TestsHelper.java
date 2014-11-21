package com.test.swivl.main;

import android.test.InstrumentationTestCase;
import android.test.TouchUtils;
import android.util.Log;
import android.view.View;

import com.test.swivl.pojo.UserBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestsHelper {
    public static final int COUNT_OF_USERS = 100;
    public static final int TIME_DELAY_TO_VERIFY_ACTION = 8000;
    public static final int TRIES_TO_CLICK_VIEW = 5;

    public static List<UserBean> createListOfSortedUserBeans(UserBean... beansArgs) {
        List<UserBean> beans = new ArrayList<UserBean>();
        for (int i = 0; beansArgs != null && i < beansArgs.length; ++i) {
            beans.add(beansArgs[i]);
        }
        Collections.sort(beans, new Comparator<UserBean>() {
            @Override
            public int compare(UserBean lhs, UserBean rhs) {
                return lhs.getId() - rhs.getId();
            }
        });
        return beans;
    }

    /**
     * Safe method to click a view. Gives a couple of tries to perform a click.
     */
    public static boolean tryClickView(InstrumentationTestCase test, View view) {
        boolean result = false;

        for(int i = 0; i < TRIES_TO_CLICK_VIEW && !result; ++i) {
            try {
                TouchUtils.clickView(test, view);
                Thread.sleep(TIME_DELAY_TO_VERIFY_ACTION / 4);
                result = true;
            } catch (Exception ex) {
                Log.w(MainActivityInstrumentationTest.class.getSimpleName(), "Unsuccessful attempt " +
                        "no. " + (i + 1) + " to click " + view);
            }
        }
        return result;
    }

    /**
     * Sleeps thread for some period of time
     */
    public static void sleepThreadToWairForAction(int period) {
        try {
            Thread.sleep(period);
        } catch (InterruptedException e) {
            Log.w(TestsHelper.class.getSimpleName(), "Thread was interrupted: ", e);
        }
    }
}
