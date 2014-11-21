package com.test.swivl.main;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.ViewAsserts;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.test.swivl.R;

public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {
    private MainActivity mMainActivity;
    private Button mReloadButton;
    private ListView mUsersListView;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent launchIntent = new Intent(getInstrumentation().getTargetContext(),
                MainActivity.class);
        startActivity(launchIntent, null, null);

        mMainActivity = getActivity();
        mReloadButton = (Button) mMainActivity.findViewById(R.id.main_top_reload_button);
        mUsersListView = (ListView) mMainActivity.findViewById(R.id.main_listview);
    }

    public void testPreconditions() {
        assertNotNull("mMainActivity is null", mMainActivity);
        assertNotNull("mReloadButton is null", mReloadButton);
        assertNotNull("mUsersListView is null", mUsersListView);
    }

    public void testReloadButton_layout() {
        final View decorView = mMainActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, mReloadButton);

        final ViewGroup.LayoutParams layoutParams = mReloadButton.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void testReloadButton_labelText() {
        final String expectedRefreshButtonText = mMainActivity.getString(R.string.reload_button_text);
        final String actualRefreshButtonText = mReloadButton.getText().toString();
        assertEquals(expectedRefreshButtonText, actualRefreshButtonText);
    }

    public void testUsersListView_layout() {
        final View decorView = mMainActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, mUsersListView);

        final ViewGroup.LayoutParams layoutParams = mUsersListView.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
