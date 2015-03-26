package com.switchpool.utility;

import com.xiaoshuye.switchpool.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NoContnetFragment extends Fragment {

	private String tipString;
	
	public NoContnetFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nocontent, container,false);
        TextView textView = (TextView) view.findViewById(R.id.textView_nocontent);
        textView.setText(tipString);
        
        return view;
    }
	
	public void initialize(String string) {
		tipString = string;
	}

}
