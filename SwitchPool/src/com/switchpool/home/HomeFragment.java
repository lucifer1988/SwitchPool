package com.switchpool.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiaoshuye.switchpool.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class HomeFragment extends Fragment {
	
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
	
	public HomeFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container,false);
        
		// 创建一个List对象，List对象的元素是Map
		List<Map<String, Object>> listItems = 
				new ArrayList<Map<String, Object>>();
		for (int i = 0; i < normalImageIds.length; i++)
		{
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", normalImageIds[i]);
			listItem.put("title", itemNameIds[i]);
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
			}
		});
        
        return view;
    }
}
