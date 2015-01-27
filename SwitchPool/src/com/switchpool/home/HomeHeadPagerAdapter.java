package com.switchpool.home;

import java.util.ArrayList;
import java.util.List;

import com.switchpool.model.Subject;
import com.xiaoshuye.switchpool.R;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class HomeHeadPagerAdapter extends PagerAdapter {
	private ArrayList<View> mPageViewList;
	private List<Subject> mImageList;
	private LayoutInflater mInflater;
	
	public HomeHeadPagerAdapter(Context context, List<Subject> imageList) {
		mImageList = imageList;
		mPageViewList = new ArrayList<View>();
		if (mInflater == null) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		for (int i = 0; i < imageList.size(); i++) {
			View view = mInflater.inflate(R.layout.home_headitem, null);
			mPageViewList.add(view);
		}
	}

	@Override
	public int getCount() {
		return mPageViewList != null ? mPageViewList.size() : 0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(mPageViewList.get(arg1));
	}

	@Override
	public Object instantiateItem(View arg0, final int arg1) {
		((ViewPager) arg0).addView(mPageViewList.get(arg1));
		final ImageView ImageV = (ImageView) mPageViewList.get(arg1)
				.findViewById(R.id.imageView_home_head);
		ImageV.setImageResource(mImageList.get(arg1).getBgImage());
		ImageV.setScaleType(ScaleType.FIT_XY);
//		ImageV.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(mContext, "µã»÷Í¼Æ¬", Toast.LENGTH_LONG).show();
//			}
//		});
		return mPageViewList.get(arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		//
	}

	@Override
	public Parcelable saveState() {
		//
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		//
	}

	@Override
	public void finishUpdate(View arg0) {
		//
	}

}
