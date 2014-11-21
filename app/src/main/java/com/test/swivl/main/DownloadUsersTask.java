package com.test.swivl.main;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.jr.ob.JSON;
import com.test.swivl.R;
import com.test.swivl.http.URLConnectionHelper;
import com.test.swivl.pojo.UserBean;
import com.test.swivl.http.ServerResponse;

import java.io.IOException;
import java.util.List;

public class DownloadUsersTask extends AsyncTask<Void, Void, List<UserBean>> {
    public static final int FINISHED_SUCCESSFULLY = 0;
    public static final int CONNECTION_ERROR = 1;
    public static final int PARSING_JSON_ERROR = 2;

    private MainActivity mMainActivity;
    private int mResponseStatus;
    private String mErrorMessage;

    DownloadUsersTask(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {
        mMainActivity.showAlertDialog(mMainActivity
                .getString(R.string.loading_progress_dialog_message_text), false);
    }

    @Override
    protected List<UserBean> doInBackground(Void... params) {
        List<UserBean> userBeans;
        ServerResponse response;
        try {
            response = URLConnectionHelper.executeRequestToAPI();
            if (response == null) {
                mResponseStatus = CONNECTION_ERROR;
                mErrorMessage = mMainActivity.getString(R.string.connection_error_message_text);
                return null;
            }

            if (response.getInputStream() != null) {
                userBeans = JSON.std.with(JSON.Feature.FAIL_ON_UNKNOWN_BEAN_PROPERTY)
                        .listOfFrom(UserBean.class, response.getInputStream());
                mResponseStatus = FINISHED_SUCCESSFULLY;
                return userBeans;
            } else if (response.getErrorStream() != null) {
                mResponseStatus = CONNECTION_ERROR;
                mErrorMessage = response.getErrorStream();
            }
        } catch (IOException ex) {
            Log.w(DownloadUsersTask.class.getSimpleName(), "I/O issue during parsing JSON", ex);
            mResponseStatus = PARSING_JSON_ERROR;
            mErrorMessage = ex.getMessage();

        }
        return null;
    }

    @Override
    protected void onPostExecute(List<UserBean> result) {
        switch (mResponseStatus) {
            case FINISHED_SUCCESSFULLY:
                mMainActivity.dismissAlertDialog();
                mMainActivity.new StoreBeansTask().execute(result);
                break;
            case CONNECTION_ERROR:
            case PARSING_JSON_ERROR:
            default:
                mMainActivity.dismissAlertDialog();
                mMainActivity.handleError(mErrorMessage);
                break;
        }
    }
}