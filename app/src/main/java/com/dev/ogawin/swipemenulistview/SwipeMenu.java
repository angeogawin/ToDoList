package com.dev.ogawin.swipemenulistview;

import android.content.Context;

import com.dev.ogawin.swipemenulistview.SwipeMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author baoyz
 * @date 2014-8-23
 * 
 */
public class SwipeMenu {

	private Context mContext;
	private List<com.dev.ogawin.swipemenulistview.SwipeMenuItem> mItems;
	private int mViewType;

	public SwipeMenu(Context context) {
		mContext = context;
		mItems = new ArrayList<com.dev.ogawin.swipemenulistview.SwipeMenuItem>();
	}

	public Context getContext() {
		return mContext;
	}

	public void addMenuItem(com.dev.ogawin.swipemenulistview.SwipeMenuItem item) {
		mItems.add(item);
	}

	public void removeMenuItem(com.dev.ogawin.swipemenulistview.SwipeMenuItem item) {
		mItems.remove(item);
	}

	public List<com.dev.ogawin.swipemenulistview.SwipeMenuItem> getMenuItems() {
		return mItems;
	}

	public SwipeMenuItem getMenuItem(int index) {
		return mItems.get(index);
	}

	public int getViewType() {
		return mViewType;
	}

	public void setViewType(int viewType) {
		this.mViewType = viewType;
	}

}
