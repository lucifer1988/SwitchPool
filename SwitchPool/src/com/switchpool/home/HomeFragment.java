package com.switchpool.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.switchpool.detail.DetailActivity;
import com.switchpool.detail.DetailActivity.DeatilType;
import com.switchpool.model.Subject;
import com.switchpool.search.SearchActivity;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class HomeFragment extends Fragment implements OnPageChangeListener {
	
	GridView grid;
	int[] normalImageIds = new int[]
	{
		R.drawable.home_grid01_normal , R.drawable.home_grid02_normal , R.drawable.home_grid03_normal
		, R.drawable.home_grid04_normal , R.drawable.home_grid05_normal , R.drawable.home_grid06_normal
		, R.drawable.home_grid07_normal , R.drawable.home_grid08_normal
	};
	int[] highlightImageIds = new int[]
	{
		R.drawable.home_grid01_highlight , R.drawable.home_grid02_highlight , R.drawable.home_grid03_highlight
		, R.drawable.home_grid04_highlight , R.drawable.home_grid05_highlight , R.drawable.home_grid06_highlight
		, R.drawable.home_grid07_highlight , R.drawable.home_grid08_highlight
	};
	int[] itemNameIds = new int[]
	{
		R.string.home_item_grid1 , R.string.home_item_grid2 , R.string.home_item_grid3
		, R.string.home_item_grid4 , R.string.home_item_grid5 , R.string.home_item_grid6
		, R.string.home_item_grid7 , R.string.home_item_grid8
	};
	List<Subject> subjectArr ;
	
	private ViewPager homeHeadViewPager;
	private HomeHeadPagerAdapter homeHeaderAdapter;
	private TextView homeHeadTextView;
	private ImageView placeholderImageView;
	private RelativeLayout headerRelativeLayout;
	
	private Subject curSubject;
	
	public HomeFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container,false);
        
        homeHeadViewPager = (ViewPager) view.findViewById(R.id.viewpager_home_header);
        homeHeadTextView = (TextView)view.findViewById(R.id.textView_home_header);
        placeholderImageView = (ImageView)view.findViewById(R.id.imageView_home_header_placeholder);
        headerRelativeLayout = (RelativeLayout)view.findViewById(R.id.relativeLayout_home_header);
//		String subjectPathString = Utility.shareInstance().resSubjectListFile();
//        List<Subject> subjectArr = (List<Subject>) Utility.shareInstance().getObject(subjectPathString);
//        homeHeaderAdapter = new HomeHeadPagerAdapter(this.getActivity(), subjectArr);
//        homeHeadViewPager.setAdapter(homeHeaderAdapter);
		
		// 创建一个List对象，List对象的元素是Map
		List<Map<String, Object>> listItems = 
				new ArrayList<Map<String, Object>>();
		for (int i = 0; i < normalImageIds.length; i++)
		{
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", normalImageIds[i]);
			listItem.put("title", getActivity().getString(itemNameIds[i]));
			listItems.add(listItem);
		}
		// 创建一个SimpleAdapter
		SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),
				listItems
				// 使用/layout/cell.xml文件作为界面布局
				, R.layout.home_gridcell, new String[] { "image" , "title" },
				new int[] { R.id.imageView_home_gridcell , R.id.textView_home_gridcell});
		grid = (GridView) view.findViewById(R.id.gridView_home);
		// 为GridView设置Adapter
		grid.setAdapter(simpleAdapter);
		// 添加列表项被选中的监听器
		grid.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id)
			{
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
		// 添加列表项被单击的监听器
		grid.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
//				Log.v("sp", ""+position);
//				Log.v("sp", ""+curSubject.subjectid+"x"+(position+1));
				
				if (curSubject.getHasRight() == 0) {
					showAD(101, getString(R.string.nocontenttip_noRight));
				}
				
				else if (position < 4) {
					Intent onItemClickIntent = new Intent();
					onItemClickIntent.putExtra("poolId", curSubject.subjectid+"x"+(position+1));
					onItemClickIntent.putExtra("subjectId", curSubject.subjectid);
					onItemClickIntent.putExtra("poolName", getActivity().getString(itemNameIds[position]));
					onItemClickIntent.setClass(getActivity(), TopListActivity.class);
					getActivity().startActivity(onItemClickIntent);
					getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				}
				else if (position == 5) {
					Intent onItemClickIntent = new Intent();
					onItemClickIntent.putExtra("subjectId", curSubject.subjectid);
					onItemClickIntent.setClass(getActivity(), SearchActivity.class);
					getActivity().startActivity(onItemClickIntent);
					getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				}
				else if (position == 6) {
					if (DetailActivity.staticMusicPlayer != null && DetailActivity.staticMusicPlayer.player.isPlaying()) {
						String curSubjectid = DetailActivity.staticMusicPlayer.curSubjectid();
						String curPoolid = DetailActivity.staticMusicPlayer.curPoolid();
						String curItemid = DetailActivity.staticMusicPlayer.curItemid();
						
		            	Intent intent=new Intent();
		            	intent.setClass(getActivity(), DetailActivity.class);
		            	Bundle bundle = new Bundle();
		            	bundle.putSerializable("item", Utility.shareInstance().findItem(curSubjectid, curPoolid, curItemid, getActivity()));
		            	bundle.putSerializable("type", DeatilType.DeatilTypeAudio);
		            	intent.putExtras(bundle);
		            	intent.putExtra("poolId", curPoolid);
		            	intent.putExtra("subjectId", curSubjectid);
		            	getActivity().startActivity(intent); 
		            	getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
					}
					else {
						showAD(100, getString(R.string.nocontenttip_audioplay));
					}
				}
			}
		});
        
        return view;
    }
	
	public void refreshHeader() {
		headerRelativeLayout.removeView(placeholderImageView);
		String subjectPathString = Utility.shareInstance().resSubjectListFile();
        subjectArr = (List<Subject>) Utility.shareInstance().getObject(subjectPathString);
        Log.v("sp", ""+ subjectArr);
        homeHeaderAdapter = new HomeHeadPagerAdapter(this.getActivity(), subjectArr);
        homeHeadViewPager.setAdapter(homeHeaderAdapter);
        homeHeadViewPager.setOnPageChangeListener(this);
        curSubject = subjectArr.get(0);
        homeHeadTextView.setText(curSubject.title);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		Log.v("sp", ""+arg0);
        curSubject = subjectArr.get(arg0);
        homeHeadTextView.setText(curSubject.title);
	}
	
	//选择对话框
	public void showAD(int flag, String title) {
		AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity()).setTitle(title);
		setPositiveButton(builder, flag);
		setNegativeButton(builder, flag)
		.create()
		.show();
	}
	
	private AlertDialog.Builder setPositiveButton(
			AlertDialog.Builder builder, final int flag)
	{
		// 调用setPositiveButton方法添加确定按钮
		return builder.setPositiveButton(getString(R.string.OK), new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (flag == 100) {
					Intent onItemClickIntent = new Intent();
					onItemClickIntent.putExtra("poolId", curSubject.subjectid+"x"+1);
					onItemClickIntent.putExtra("subjectId", curSubject.subjectid);
					onItemClickIntent.putExtra("poolName", getActivity().getString(itemNameIds[1]));
					onItemClickIntent.setClass(getActivity(), TopListActivity.class);
					getActivity().startActivity(onItemClickIntent);
					getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				}
				else if (flag == 101) {
					//跳往开通页面
				}
			}
		});
	}
	
	private AlertDialog.Builder setNegativeButton(
			AlertDialog.Builder builder, final int flag)
	{
		// 调用setNegativeButton方法添加取消按钮
		return builder.setNegativeButton(getString(R.string.cancel), new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				return ;
			}
		});
	}
}
