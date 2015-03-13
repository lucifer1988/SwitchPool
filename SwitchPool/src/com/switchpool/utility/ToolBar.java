package com.switchpool.utility;

import com.xiaoshuye.switchpool.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class ToolBar extends Fragment implements OnClickListener {
	private ToolBarCallBack callBack;
	
	public ToolBar() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.toolbar, container,false);
        
        ImageButton button1 = (ImageButton) view.findViewById(R.id.imageButton_toolbar_1);
        ImageButton button2 = (ImageButton) view.findViewById(R.id.imageButton_toolbar_2);
        ImageButton button3 = (ImageButton) view.findViewById(R.id.imageButton_toolbar_3);
        ImageButton button4 = (ImageButton) view.findViewById(R.id.imageButton_toolbar_4);
        ImageButton button5 = (ImageButton) view.findViewById(R.id.imageButton_toolbar_5);
        ImageButton button6 = (ImageButton) view.findViewById(R.id.imageButton_toolbar_6);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        return view;
    }
	
	public void setCallBack(ToolBarCallBack callBack) {
		this.callBack = callBack;
	}
	
    @Override
    public void onClick(View v) {
        int id  = v.getId();
        switch (id) {
        case R.id.imageButton_toolbar_1:
        	callBack.tapButton1();
            break;
        case R.id.imageButton_toolbar_2:
        	callBack.tapButton2();
            break;
        case R.id.imageButton_toolbar_3:
        	callBack.tapButton3();
            break;
        case R.id.imageButton_toolbar_4:
        	callBack.tapButton4();
            break;
        case R.id.imageButton_toolbar_5:
        	callBack.tapButton5();
            break;
        case R.id.imageButton_toolbar_6:
        	callBack.tapButton6();
            break;
        default:
            break;
        }
    }
}
