package com.test.swivl.storage;

import com.test.swivl.main.TestsHelper;
import com.test.swivl.pojo.UserBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeansFactory {
    public static final String DUMMY_LOGIN = "dummy";
    public static final String DUMMY_HTML_URL = "http://dummy.com/dummy/";
    public static final int RANDOM_RANGE = 10000;
    private Set<String> usedIds;

    public BeansFactory() {
        usedIds = new HashSet<String>();
    }

    public UserBean makeUser(int id) {
        usedIds.add(String.valueOf(id));
        UserBean user = new UserBean();
        user.setId(id);
        user.setLogin(DUMMY_LOGIN + id);
        user.setHtml_url(DUMMY_HTML_URL + id );
        return user;
    }

    public UserBean makeUser() {
        int id = (int) (RANDOM_RANGE * Math.random() + 1);
        while(usedIds.contains(Integer.toString(id))) {
            id = (int) (RANDOM_RANGE * Math.random() + 1);
        }
        return makeUser(id);
    }

    public List<UserBean> makeSizedListOfUsers(int size) {
        List<UserBean> list = new ArrayList<UserBean>();
        for (int i = 0; i < size; i++) {
            list.add(makeUser());
        }
        return list;
    }

    public List<UserBean> makeListOfUsers() {
        return makeSizedListOfUsers(TestsHelper.COUNT_OF_USERS);
    }
}