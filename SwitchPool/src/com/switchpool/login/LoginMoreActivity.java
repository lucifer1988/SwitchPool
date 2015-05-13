package com.switchpool.login;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaoshuye.switchpool.R;

public class LoginMoreActivity extends Activity {

	private List<String> itemList; 
	private GroupListAdapter adapter;
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_more);
		
		itemList = new ArrayList<String>(){
			{
				add("blank");
				add(getString(R.string.login_more_list1));
				add("blank");
				add(getString(R.string.login_more_list2));
				add("blank");
				add(getString(R.string.login_more_list3));
				add("blank");
			}
		};
		adapter = new GroupListAdapter(this, itemList);
		listView = (ListView)findViewById(R.id.listView_loginmore);
		listView.setAdapter(adapter);
	}
	
	public void back(View sourceView) {
		finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	
	public void register(View sourceView) {
		Intent intent = new Intent(this, RegisterActivity.class);    	
		this.startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	private static class GroupListAdapter extends ArrayAdapter<String>{
	    public GroupListAdapter(Context context, List<String> objects) {
	        super(context, 0, objects);
	    }
	     
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View view = convertView;
	        //根据标签类型加载不通的布局模板
	        if(getItem(position).equals("blank")) {
	            //如果是标签项
	            view = LayoutInflater.from(getContext()).inflate(R.layout.login_more_listblank, parent, false);
	        }
	        else {             
	            //否则就是数据项了     
	            view = LayoutInflater.from(getContext()).inflate(R.layout.login_more_listitem, parent, false);
		        //显示名称
		        TextView textView1 = (TextView) view.findViewById(R.id.textView1_loginmore_listitem);
		        TextView textView2 = (TextView) view.findViewById(R.id.textView2_loginmore_listitem);
		        ImageView imageView1 = (ImageView) view.findViewById(R.id.imageView1_loginmore_listitem);
		        if (position == 1) {
		        	imageView1.setImageResource(R.drawable.login_username);
				}
		        else if (position == 3) {
		        	textView2.setText("中文");
				}
		        else if (position == 5) {
		        	textView2.setText("www.xxxx.com");
				}
		        textView1.setText(getItem(position));
	        }
	        
	        return view;
	    }
	}
}
