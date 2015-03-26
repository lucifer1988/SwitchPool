package com.switchpool.detail;

import com.xiaoshuye.switchpool.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class DetailContentFragment extends Fragment {

	private Button tabButton, contentTitle;
	FragmentManager fManager;
	
	public DetailContent20Fragment content20Fragment;
	
	public DetailContentFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_content, container,false);
        
        tabButton = (Button)view.findViewById(R.id.button_detail_content_tab);
        contentTitle = (Button)view.findViewById(R.id.button_detail_content_content1);
        
        fManager = getActivity().getSupportFragmentManager();
        
        content20Fragment = new DetailContent20Fragment();
		FragmentTransaction transaction = fManager.beginTransaction();
		transaction.add(R.id.relativeLayout_detail_content, content20Fragment).commit();
        
        return view;
    }
	
}
