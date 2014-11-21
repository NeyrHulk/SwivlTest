package com.test.swivl.main;

import android.app.Instrumentation;
    import android.test.ActivityInstrumentationTestCase2;
    import android.test.ViewAsserts;
    import android.widget.Button;
    import android.widget.ListView;

    import com.test.swivl.R;

    public class MainActivityInstrumentationTest extends ActivityInstrumentationTestCase2<MainActivity> {
        private MainActivity mMainActivity;
        private Button mReloadButton;
        private ListView mUsersListView;

    public MainActivityInstrumentationTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);

        mMainActivity = getActivity();
        mReloadButton = (Button) mMainActivity.findViewById(R.id.main_top_reload_button);
        mUsersListView = (ListView) mMainActivity.findViewById(R.id.main_listview);
    }

    public void testPreconditions() {
        assertNotNull("mMainActivity is null", mMainActivity);
        assertNotNull("mReloadButton is null", mReloadButton);
        assertNotNull("mUsersListView is null", mUsersListView);
    }

    public void testLoad() throws Exception {
        checkMainActivityAndComponents();
    }

    public void testReload() throws Exception {
        checkMainActivityAndComponents();
        assertTrue(TestsHelper.tryClickView(this, mReloadButton));
        checkMainActivityAndComponents();
    }

    private void checkMainActivityAndComponents() {
        ViewAsserts.assertOnScreen(mMainActivity.getWindow().getDecorView(), mUsersListView);
        assertEquals(TestsHelper.COUNT_OF_USERS, mUsersListView.getCount());
    }
}
