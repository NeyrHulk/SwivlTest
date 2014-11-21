package com.test.swivl.main;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.test.swivl.R;
import com.test.swivl.http.URLConnectionHelper;
import com.test.swivl.pojo.UserBean;

import android.widget.Toast;

import com.test.swivl.storage.BeansDBAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity {
    public static final int UPDATE_AVATAR_IMAGEVIEW = 10;
    public static final int SMALL_AVATAR_SIZE = 100;
    public static final int BIG_AVATAR_SIZE = 400;

    private Handler mHandler;
    private ListView mUsersListView;
    private Button mReloadButton;
    private BeansDBAdapter mDBAdapter;
    private Map<String, Bitmap> mCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        createHandler();
        initListView();
        initButton();

        mCache = new HashMap<String, Bitmap>();
    }

    private void initListView() {
        mUsersListView = (ListView) findViewById(R.id.main_listview);
        BeanCursorAdapter adapter = new BeanCursorAdapter(this, null);
        mUsersListView.setAdapter(adapter);
    }

    private void initButton() {
        mReloadButton = (Button) findViewById(R.id.main_top_reload_button);
        mReloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReloadButton.setEnabled(false);
                downloadUsers();
            }
        });
    }

    private void createHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.what) {
                    case UPDATE_AVATAR_IMAGEVIEW:
                        if (inputMessage.obj != null) {
                            new StoreDownloadedAvatarTask().execute(inputMessage.arg1,
                                    inputMessage.obj);
                        }
                        break;
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        };
    }

    void downloadImageInBackground(final int id) {
        new Thread(new Runnable() {
            public void run() {
                // send message to the handler to update UI
                Messenger messenger = new Messenger(mHandler);
                Message message = Message.obtain();
                message.what = MainActivity.UPDATE_AVATAR_IMAGEVIEW;
                message.obj = URLConnectionHelper.downloadAvatarById(id);
                message.arg1 = id;

                try {
                    messenger.send(message);
                } catch (RemoteException e) {
                    Log.w(MainActivity.class.getSimpleName(),
                            "Error sending message to activity handler");
                }
            }
        }).start();
    }

    void showAlertDialog(String title, boolean showOkButton) {
        // using the same alert dialog for showing progress as well as errors so that there are
        // some parameters here
        DialogFragment newFragment = AlertDialogFragment.newInstance(
                getString(R.string.dialog_fragment_title_key), title,
                getString(R.string.dialog_fragment_show_ok_button_key), showOkButton);
        newFragment.show(getSupportFragmentManager(), getString(R.string.alert_dialog_fragment_tag_key));
    }

    void dismissAlertDialog() {
        DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.alert_dialog_fragment_tag_key));
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }

    void handleError(String errorMessage) {
        showAlertDialog(errorMessage, true);
    }

    Map<String, Bitmap> getCache() {
        return mCache;
    }

    void showLargeImage(int id) {
        new FetchUserByIdTask().execute(id);
    }

    private void downloadUsers() {
        if (!isNetworkConnectionAlive(getBaseContext())) {
            Toast.makeText(getBaseContext(), getString(R.string.no_network_connection_toast_text),
                    Toast.LENGTH_SHORT).show();
        } else {
            mCache.clear();
            new DownloadUsersTask(this).execute();
        }
    }

    private boolean isNetworkConnectionAlive(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onStart() {
        super.onStart();
        openDBAdapter();
        new FetchUsersTask().execute();
    }

    @Override
    protected void onStop() {
        closeCursor(((CursorAdapter) mUsersListView.getAdapter()).getCursor());
        closeDBAdapter();
        super.onStop();
    }

    private void openDBAdapter() {
        if (mDBAdapter == null) {
            mDBAdapter = new BeansDBAdapter(getBaseContext());
            mDBAdapter.open();
        } else if (!mDBAdapter.isOpen()) {
            mDBAdapter.open();
        }
    }

    private void closeDBAdapter() {
        if (mDBAdapter != null) {
            mDBAdapter.close();
        }
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Task to fetch all users from DB and set up {@link ListView} and adapter
     */
    private class FetchUsersTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected void onPreExecute() {
            closeCursor(((CursorAdapter) mUsersListView.getAdapter()).getCursor());
            openDBAdapter();
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            return mDBAdapter.fetchAllUsersSortedById();
        }

        @Override
        protected void onPostExecute(Cursor result) {
            if (result != null && result.getCount() > 0) {
                mUsersListView.setAdapter(new BeanCursorAdapter(MainActivity.this, result));
                mUsersListView.invalidateViews();
            } else {
                downloadUsers();
            }
            mReloadButton.setEnabled(true);
        }
    }

    /**
     * Task to store users to the DB
     */
    class StoreBeansTask extends AsyncTask<List<UserBean>, Void, Void> {

        @Override
        protected void onPreExecute() {
            openDBAdapter();
        }

        @Override
        protected Void doInBackground(List<UserBean>... params) {
            mDBAdapter.putBeans(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            new FetchUsersTask().execute();
        }
    }

    /**
     * Task to store downloaded avatar
     */
    class StoreDownloadedAvatarTask extends AsyncTask<Object, Void, Object[]> {

        @Override
        protected void onPreExecute() {
            openDBAdapter();
        }

        @Override
        protected Object[] doInBackground(Object... params) {
            if (params.length == 2) {
                mDBAdapter.update((Integer) params[0], (byte[]) params[1]);
            }
            return params;
        }

        @Override
        protected void onPostExecute(Object[] result) {
            ((BeanCursorAdapter) mUsersListView.getAdapter())
                    .updateDownloadedAvatar((Integer) result[0], (byte[]) result[1]);
        }
    }

    private class FetchUserByIdTask extends AsyncTask<Integer, Void, Cursor> {

        @Override
        protected void onPreExecute() {
            openDBAdapter();
        }

        @Override
        protected Cursor doInBackground(Integer... params) {
            return mDBAdapter.fetchUserById(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            String userLogin = "";
            byte[] imageBytes = null;

            if (result != null && result.moveToFirst()) {
                userLogin = result.getString(BeansDBAdapter.LOGIN_COLUMN_INDEX);
                if (result.getBlob(BeansDBAdapter.AVATAR_COLUMN_INDEX) != null) {
                    imageBytes = result.getBlob(BeansDBAdapter.AVATAR_COLUMN_INDEX);
                } else {
                    downloadImageInBackground(result.getInt(BeansDBAdapter.ID_COLUMN_INDEX));
                }
            }

            DialogFragment newFragment = LargeImageDialogFragment.newInstance(
                    getString(R.string.dialog_fragment_title_key), userLogin,
                    getString(R.string.dialog_fragment_image_bytes_key), imageBytes);
            newFragment.show(getSupportFragmentManager(),
                    getString(R.string.large_image_dialog_fragment_tag_key));
        }
    }
}