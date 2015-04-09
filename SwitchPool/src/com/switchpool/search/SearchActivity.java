package com.switchpool.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.loopj.android.http.RequestParams;
import com.switchpool.home.MainActivity;
import com.switchpool.model.Item;
import com.switchpool.model.SearchKey;
import com.switchpool.model.User;
import com.switchpool.utility.NoContnetFragment;
import com.switchpool.utility.ToolBar;
import com.switchpool.utility.ToolBarCallBack;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends FragmentActivity implements OnClickListener {

	FragmentManager fManager;
	NoContnetFragment ncFragment;
	TextView tipTextView;
	EditText searchField;
	LinearLayout toolbarLayout;
	private  ExpandableListView  searchExpandableListView;
	private  ExpandableListViewaAdapter adapter;
	
	String subjectid;
	int curIndex;
	
	private RequestParams params;
	List<String> poolidArr;
	HashMap<String, String> searchVerDict;
	List<Item> resultArr;
	String curPoolid;
	String curSearchStr;
	List<List<SearchKey>> searchKeyList;
	
	//History
	List<String> searchHistoryArr;
	String searchHistoryCachePath;
	String searchVerCachePath;
	int searchHistoryIndex;
	
	public SearchActivity() {
	}
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		fManager = getSupportFragmentManager();
		tipTextView = (TextView)findViewById(R.id.textView_search_tip);
		searchField = (EditText)findViewById(R.id.editText_search_field);
		toolbarLayout = (LinearLayout)findViewById(R.id.linearLayout_search_toolbar);
		searchExpandableListView = (ExpandableListView)findViewById(R.id.expandableListView_search);
		
		Button searchButton = (Button)findViewById(R.id.button_search_field);
		searchButton.setOnClickListener(this);
		findViewById(R.id.button1_search_toolbar).setOnClickListener(this);
		findViewById(R.id.button2_search_toolbar).setOnClickListener(this);
		findViewById(R.id.button3_search_toolbar).setOnClickListener(this);
		findViewById(R.id.button4_search_toolbar).setOnClickListener(this);
		findViewById(R.id.relativeLayout_search).setOnClickListener(this);
		
		Intent intent = getIntent();
		subjectid=intent.getStringExtra("subjectId");
		
		searchHistoryCachePath = Utility.shareInstance().cacheUserHistory()+subjectid;
		List<String> tempHistoryList = (List<String>)Utility.shareInstance().getObject(searchHistoryCachePath);
		if (tempHistoryList == null) {
			searchHistoryArr = new ArrayList<String>();
		}
		else {
			searchHistoryArr = tempHistoryList;
			searchField.setText(searchHistoryArr.get(0));
			searchHistoryIndex = 0;
		}
		
		poolidArr = new ArrayList<String>();
		for (int i = 1; i < 5; i++) {
			poolidArr.add(String.format("%sx%d", subjectid, i));
		}
		searchVerCachePath = Utility.shareInstance().cacheUserHistory()+getString(R.string.SPPoolSearchDict);
		searchVerDict = (HashMap<String, String>)Utility.shareInstance().getObject(searchVerCachePath);
		if (!searchVerDict.containsKey(poolidArr.get(0))) {
			searchVerDict.put(poolidArr.get(0), "0");
			Utility.shareInstance().saveObject(searchVerCachePath, searchVerDict);
		}
		curPoolid = poolidArr.get(0);
		//request params
		User userInfo = (User)Utility.shareInstance().getObject(Utility.shareInstance().userInfoFile());
		params = new RequestParams();
		params.put("uid", userInfo.getUid());
		params.put("token", userInfo.getToken());
		params.put("subjectid", subjectid);
		params.put("poolid", curPoolid);
		params.put("version", "0");
		
		searchKeyList = new ArrayList<List<SearchKey>>();
		
		toolbarLayout.setVisibility(View.INVISIBLE);
		searchExpandableListView.setVisibility(View.INVISIBLE);
		
		ToolBar toolBar = (ToolBar)getSupportFragmentManager().findFragmentById(R.id.search_toolbar);
		toolBar.setCallBack(new ToolBarCallBack() {
			
			@Override
			public void tapButton6() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void tapButton5() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void tapButton4() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void tapButton3() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void tapButton2() {
	            Intent myIntent = new Intent();
	            myIntent.setClass(SearchActivity.this, MainActivity.class);
	            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(myIntent);
	            SearchActivity.this.finish();
	            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			}
			
			@Override
			public void tapButton1() {
				// TODO Auto-generated method stub
			}
		});
		
		if (!TextUtils.isEmpty(searchField.getText().toString())) {
			search();
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent myIntent = new Intent();
            myIntent.setClass(SearchActivity.this, MainActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent);
            SearchActivity.this.finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        return super.onKeyDown(keyCode, event);
    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.relativeLayout_search) {
	         InputMethodManager imm = (InputMethodManager)  
             getSystemService(Context.INPUT_METHOD_SERVICE);  
             imm.hideSoftInputFromWindow(v.getWindowToken(), 0); 
		}
	}
	
	private void search() {
		String searchString = searchField.getText().toString().trim();  
        if (TextUtils.isEmpty(searchString)) {  
            Toast.makeText(this, "搜索关键词不能为空", Toast.LENGTH_LONG).show(); 
            return;
        }
		startSearch(searchString);
        curSearchStr = searchString;
        addHistoryKey(searchString);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0); 
        toolbarLayout.setVisibility(View.VISIBLE);
	}
	
	private void startSearch(String searchString) {
		List<SearchKey> curKeyList = searchKeyList.get(curIndex);
		if (curKeyList == null || curKeyList.size() == 0) {
			
		}
	}
	
	private void addHistoryKey(String searchString) {
		if (searchHistoryArr != null) {
			if (searchHistoryArr.size() > 0) {
				for (int i = 0; i < searchHistoryArr.size(); i++) {
					String hisKry = searchHistoryArr.get(i);
					if (hisKry.equals(searchString)) {
						searchHistoryArr.remove(i);
						break;
					}
				}
			}
			searchHistoryArr.add(0, searchString);
			Utility.shareInstance().saveObject(searchHistoryCachePath, searchHistoryArr);
		}
	}

	class ExpandableListViewaAdapter extends BaseExpandableListAdapter {
        Activity activity;
         public  ExpandableListViewaAdapter(Activity a)  
            {  
                activity = a;  
            }  
       /*-----------------Child */
        @Override
        public Item getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return resultArr.get(groupPosition).getItemArr().get(childPosition);
        }
 
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childPosition;
        }
 
        @Override
        public View getChildView(int groupPosition, int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {
             
            Item item =resultArr.get(groupPosition).getItemArr().get(childPosition);
             
            return getGenericView(item);
        }
 
        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return resultArr.get(groupPosition).getItemArr().size();
        }
       /* ----------------------------Group */
        @Override
        public Item getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return getGroup(groupPosition);
        }
 
        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return resultArr.size();
        }
 
        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }
 
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent) {
             
           Item item=resultArr.get(groupPosition);
           return getGenericView(item);
        }
 
        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return false;
        }
 
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) 
        {
            // TODO Auto-generated method stub
            return true;
        }
         
        private View  getGenericView(Item item ) 
        {
        	// Layout parameters for the ExpandableListView    
        	 AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
             TextView textView = new TextView(activity);
             textView.setLayoutParams(lp);
             textView.setGravity(Gravity.CENTER_VERTICAL);
             textView.setTextSize(17);
             textView.setTextColor(Color.BLACK);
             textView.setText(item.getCaption());
	         if (item.getParentid() == null) {
	        	 textView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.toplist_parenticon), null, null, null);
	        	 textView.setPadding(30, 0, 0, 0);
			 }
	         else {
	        	 textView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.toplist_childicon), null, null, null);
	        	 textView.setPadding(60, 0, 0, 0);
			 }
             return textView;
         }		
		
    }
}
