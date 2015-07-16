package com.switchpool.detail;

import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DetailContentFragment extends Fragment implements OnClickListener {

	public interface DetailContentHandler   
    {  
        public void handleMessage(int index);   
    } 
	
	private Button tabButton, contentTitle;
	FragmentManager fManager;
	private int index;
	private DetailContentHandler handler;
	
	public DetailContent20Fragment content20Fragment;
	public DetailContent21Fragment content21Fragment;
	public DetailContent22Fragment content22Fragment;
	
	public DetailContentFragment() {
	}

	/** Fragment第一次附属于Activity时调用,在onCreate之前调用 */  
    @Override  
    public void onAttach(Activity activity) {  
        super.onAttach(activity);
        handler = (DetailContentHandler) activity;   
    } 
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_content, container,false);
        
        tabButton = (Button)view.findViewById(R.id.button_detail_content_tab);
        contentTitle = (Button)view.findViewById(R.id.button_detail_content_content1);
        tabButton.setOnClickListener(this);
        index = 0;
        
        fManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fManager.beginTransaction();
        content22Fragment = new DetailContent22Fragment();
        transaction.add(R.id.relativeLayout_detail_content20, content22Fragment);
        content21Fragment = new DetailContent21Fragment();
        transaction.add(R.id.relativeLayout_detail_content20, content21Fragment);
        content20Fragment = new DetailContent20Fragment();
        transaction.add(R.id.relativeLayout_detail_content20, content20Fragment);
        transaction.commit();
        
        return view;
    }
	
    @Override
    public void onClick(View v) {
		if (Utility.shareInstance().isFastDoubleClick()) {  
	        return;  
	    } 
    	index++;
    	if (index > 2) {
			index = 0;
		}
    	FragmentTransaction transaction = fManager.beginTransaction();
        switch (index) {
        case 0: {
        	transaction.hide(content21Fragment);
        	transaction.hide(content22Fragment);
        	transaction.show(content20Fragment);
        	contentTitle.setText(getActivity().getString(R.string.detail_content_content1));
        }
            break;
        case 1: {
        	transaction.hide(content20Fragment);
        	transaction.hide(content22Fragment);
        	transaction.show(content21Fragment);
        	contentTitle.setText(getActivity().getString(R.string.detail_content_content2));
        }
            break;
        case 2: {
        	transaction.hide(content20Fragment);
        	transaction.hide(content21Fragment);
        	transaction.show(content22Fragment);
        	contentTitle.setText(getActivity().getString(R.string.detail_content_content3));
        }
            break;
        }
        transaction.commit();
        handler.handleMessage(index); 
    }
}
