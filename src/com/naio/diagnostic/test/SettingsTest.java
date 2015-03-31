package com.naio.diagnostic.test;

import com.naio.diagnostic.R;
import com.naio.diagnostic.activities.HubActivity;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

public class SettingsTest extends ActivityInstrumentationTestCase2<HubActivity> {



	private HubActivity mActivity;

	public SettingsTest() {
		super( HubActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
	}
	
	public void testPreconditions() {
		assertNotNull("mActivity is null", mActivity);
	}
	
	public void testMenuSettings(){
		Solo han = new Solo(getInstrumentation(),mActivity);
		han.clickOnActionBarItem(R.id.action_settings);
		assertTrue("Could not find the activity!", han.searchText("Developer"));	
		han.goBack();
	}
	
	public void testMenuSettingsPorts(){
		Solo han = new Solo(getInstrumentation(),mActivity);
		han.clickOnActionBarItem(R.id.action_settings);
		getInstrumentation().waitForIdleSync();
		han.clickOnText("Developer Mode");
		getInstrumentation().waitForIdleSync();
		han.clickOnText("Ports");
		assertTrue("Could not find the activity!", han.searchText("Motors"));	
		han.goBack();
	}
}
