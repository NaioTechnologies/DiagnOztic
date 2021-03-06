/**
 * Created the 23 janv. 2015 at 15:25:23
 * by bodereau
 * 
 */
package com.naio.diagnostic.utils;

/**
 * @author bodereau
 *
 */
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter {

	private final List fragments;

	//On fournit à l'adapter la liste des fragments à afficher
	public MyPagerAdapter(FragmentManager fm, List fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return (Fragment) this.fragments.get(position);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}
}