package com.test.swivl.storage;

import java.lang.reflect.Field;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.test.swivl.main.TestsHelper;
import com.test.swivl.pojo.UserBean;

public class BeansDBAdapterTest extends AndroidTestCase {
    private static final String PREFIX = "test_";
    private static final String DATABASE_FIELD_NAME = "mDatabase";
    private BeansDBAdapter mDBAdapter;
    private SQLiteDatabase mDatabase;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        prepareMockedDbAdapterAndDatabase();
        cleanUpTable();
    }

    private void prepareMockedDbAdapterAndDatabase() throws NoSuchFieldException, IllegalAccessException {
        RenamingDelegatingContext mockContext = new RenamingDelegatingContext(getContext(), PREFIX);
        mockContext.makeExistingFilesAndDbsAccessible();
        mDBAdapter = new BeansDBAdapter(mockContext);
        mDBAdapter.open();

        // made to obtain mocked database from DB adapter
        Field dbField = mDBAdapter.getClass().getDeclaredField(DATABASE_FIELD_NAME);
        dbField.setAccessible(true);
        mDatabase = (SQLiteDatabase) dbField.get(mDBAdapter);
    }

    private void cleanUpTable() {
        mDatabase.delete(BeansDBOpenHelper.BEANS_TABLE_NAME, null, null);
        TestsHelper.sleepThreadToWairForAction(TestsHelper.TIME_DELAY_TO_VERIFY_ACTION / 8);
        assertEquals(0, countRowsInDBTable());
    }

    private int countRowsInDBTable() {
        Cursor cursor = null;
        int rowsCount = -1;
        try {
            cursor = mDatabase.query(BeansDBOpenHelper.BEANS_TABLE_NAME, null, null, null, null,
                    null, null);
            rowsCount = cursor.getCount();
        } finally {
            cursor.close();
        }
        return rowsCount;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mDBAdapter.close();
    }

    public void testDeleteAll() {
        assertEquals(0, countRowsInDBTable());
        mDBAdapter.putBeans(new BeansFactory().makeListOfUsers());
        TestsHelper.sleepThreadToWairForAction(TestsHelper.TIME_DELAY_TO_VERIFY_ACTION / 4);
        mDBAdapter.deleteAll();
        TestsHelper.sleepThreadToWairForAction(TestsHelper.TIME_DELAY_TO_VERIFY_ACTION / 8);
        assertEquals(0, countRowsInDBTable());
    }

    public void testPutBeans() {
        BeansFactory beansFactory = new BeansFactory();

        // put some data to check if table clears with deleteAll() before putting new data
        assertEquals(0, countRowsInDBTable());
        mDBAdapter.putBeans(beansFactory.makeListOfUsers());
        List<UserBean> users = beansFactory.makeListOfUsers();
        mDBAdapter.putBeans(users);
        TestsHelper.sleepThreadToWairForAction(TestsHelper.TIME_DELAY_TO_VERIFY_ACTION / 4);
        assertEquals(users.size(),  countRowsInDBTable());
    }

    public void testFetchAllUsersSortedById() {
        assertEquals(0, countRowsInDBTable());

        BeansFactory beansFactory = new BeansFactory();
        UserBean user1 = beansFactory.makeUser(0);
        UserBean user2 = beansFactory.makeUser(1);
        UserBean user3 = beansFactory.makeUser(2);
        UserBean user4 = beansFactory.makeUser(3);
        UserBean user5 = beansFactory.makeUser(4);
        UserBean user6 = beansFactory.makeUser(5);

        // we create them separately in order to provide non trivial order of rows in DB table
        insertUser(user4);
        insertUser(user6);
        insertUser(user3);
        insertUser(user2);
        insertUser(user5);
        insertUser(user1);
        TestsHelper.sleepThreadToWairForAction(TestsHelper.TIME_DELAY_TO_VERIFY_ACTION / 8);

        Cursor cursor = mDBAdapter.fetchAllUsersSortedById();
        assertNotNull(cursor);
        assertEquals(6, cursor.getCount());

        List<UserBean> users = TestsHelper.createListOfSortedUserBeans(user1, user2, user3, user4,
                user5, user6);

        // ensure order of the items of Cursor. It should be user1 .. user6
        cursor.moveToFirst();
        for (int i = 0; !cursor.isAfterLast(); ++i) {
            UserBean user = users.get(i);
            assertEquals(user.getId().intValue(), cursor.getInt(BeansDBAdapter.ID_COLUMN_INDEX));
            assertEquals(user.getLogin(), cursor.getString(BeansDBAdapter.LOGIN_COLUMN_INDEX));
            assertEquals(user.getHtml_url(), cursor.getString(BeansDBAdapter.HTML_URL_COLUMN_INDEX));
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void insertUser(UserBean user) {
        mDBAdapter.insert(user.getId(), user.getLogin(), user.getHtml_url(), null);
    }
}
