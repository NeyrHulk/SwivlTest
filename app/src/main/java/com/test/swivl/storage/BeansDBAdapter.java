package com.test.swivl.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.test.swivl.pojo.UserBean;

import java.util.List;

public class BeansDBAdapter {
    public static final String ID_COLUMN_NAME = "_id";
    public static final String LOGIN_COLUMN_NAME = "login";
    public static final String HTML_URL_COLUMN_NAME = "html_url";
    public static final String AVATAR_COLUMN_NAME = "avatar";
    public static final int BEANS_TABLE_COLUMN_COUNT = 4;
    public static final int ID_COLUMN_INDEX = 0;
    public static final int LOGIN_COLUMN_INDEX = 1;
    public static final int HTML_URL_COLUMN_INDEX = 2;
    public static final int AVATAR_COLUMN_INDEX = 3;
    public static final int RESULT_IF_QUERRY_FAIL = -1;
    public static final String ASC_ORDERING = "ASC";

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private BeansDBOpenHelper mDBOpenHelper;
    private final String[] mAllColumnsProjection;

    public BeansDBAdapter(Context context) {
        this.mContext = context;
        mAllColumnsProjection = getAllColumnsProjection();
    }

    private String[] getAllColumnsProjection() {
        String[] allColumnsProjectionResult =  new String[BEANS_TABLE_COLUMN_COUNT];
        allColumnsProjectionResult[ID_COLUMN_INDEX] = ID_COLUMN_NAME;
        allColumnsProjectionResult[LOGIN_COLUMN_INDEX] = LOGIN_COLUMN_NAME;
        allColumnsProjectionResult[HTML_URL_COLUMN_INDEX] = HTML_URL_COLUMN_NAME;
        allColumnsProjectionResult[AVATAR_COLUMN_INDEX] = AVATAR_COLUMN_NAME;
        return allColumnsProjectionResult;
    }

    public BeansDBAdapter open() throws SQLException {
        mDBOpenHelper = new BeansDBOpenHelper(mContext);
        mDatabase = mDBOpenHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDBOpenHelper.close();
    }

    public boolean isOpen() {
        return mDatabase != null && mDatabase.isOpen();
    }

    public long insert(int id, String login, String html_url, byte[] avatarBytes) {
        ContentValues contentValues = createContentValues(id, login, html_url, avatarBytes);
        long result;

        try {
            mDatabase.beginTransaction();
            result = mDatabase.insert(BeansDBOpenHelper.BEANS_TABLE_NAME, null, contentValues);
            mDatabase.setTransactionSuccessful();
        } catch (Throwable t) {
            return RESULT_IF_QUERRY_FAIL;
        } finally {
            if (isOpen()) {
                mDatabase.endTransaction();
            }
        }

        return result;
    }

    public long update(int id, byte[] avatarBytes) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AVATAR_COLUMN_NAME, avatarBytes);
        long result;

        try {
            mDatabase.beginTransaction();
            result = mDatabase.update(BeansDBOpenHelper.BEANS_TABLE_NAME, contentValues,
                    ID_COLUMN_NAME + "=" + id, null);
            mDatabase.setTransactionSuccessful();
        } catch (Throwable t) {
            return RESULT_IF_QUERRY_FAIL;
        } finally {
            if (isOpen()) {
                mDatabase.endTransaction();
            }
        }

        return result;
    }

    public int deleteAll() {
        int result;

        try {
            mDatabase.beginTransaction();
            result = mDatabase.delete(BeansDBOpenHelper.BEANS_TABLE_NAME, null, null);
            mDatabase.setTransactionSuccessful();
        } catch (Throwable t) {
            return RESULT_IF_QUERRY_FAIL;
        } finally {
            if (isOpen()) {
                mDatabase.endTransaction();
            }
        }

        return result;
    }

    public Cursor fetchAllUsersSortedById()
            throws SQLException {
        Cursor result;

        result = mDatabase.query(false,
                BeansDBOpenHelper.BEANS_TABLE_NAME, mAllColumnsProjection,
                null, null, null, null, ID_COLUMN_NAME + " " + ASC_ORDERING, null);

        return result;
    }

    public void putBeans(List<UserBean> userBeans) {
        if (deleteAll() > RESULT_IF_QUERRY_FAIL) {
            for(UserBean userBean : userBeans) {
                insert(userBean.getId(), userBean.getLogin(), userBean.getHtml_url(), null);
            }
        }
    }

    private ContentValues createContentValues(int id, String login, String html_url,
                                              byte[] avatarBytes) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN_NAME, id);
        values.put(LOGIN_COLUMN_NAME, login);
        values.put(HTML_URL_COLUMN_NAME, html_url);
        values.put(AVATAR_COLUMN_NAME, avatarBytes);
        return values;
    }
}