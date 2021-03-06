package com.switchpool.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.switchpool.detail.DetailActivity;
import com.switchpool.detail.DetailActivity.DeatilType;
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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.ExpandableListView.OnChildClickListener;

public class SearchActivity extends FragmentActivity implements OnClickListener {

	FragmentManager fManager;
	NoContnetFragment ncFragment;
	TextView tipTextView;
	EditText searchField;
	Button tabButton1;
	Button tabButton2;
	Button tabButton3;
	Button tabButton4;
	
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
	
	//searchKeyCache
	List<SearchKey> curSearchKeyCacheList;
	String curSearchKeyCachePath;
	
	//History
	List<String> searchHistoryArr;
	String searchHistoryCachePath;
	String searchVerCachePath;
	int searchHistoryIndex;
	
	int[] itemNameIds = new int[]
	{
		R.string.home_item_grid1 , R.string.home_item_grid2 , R.string.home_item_grid3
		, R.string.home_item_grid4 , R.string.home_item_grid5 , R.string.home_item_grid6
		, R.string.home_item_grid7 , R.string.home_item_grid8
	};
	
	SharedPreferences preferences;
	
	public SearchActivity() {
	}
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		preferences = getSharedPreferences("switchpool", 0x0001);
		
		fManager = getSupportFragmentManager();
		tipTextView = (TextView)findViewById(R.id.textView_search_tip);
		searchField = (EditText)findViewById(R.id.editText_search_field);
		toolbarLayout = (LinearLayout)findViewById(R.id.linearLayout_search_toolbar);
		searchExpandableListView = (ExpandableListView)findViewById(R.id.expandableListView_search);
		searchExpandableListView.setGroupIndicator(null);
		
		findViewById(R.id.button_search_field).setOnClickListener(this);
		tabButton1 = (Button)findViewById(R.id.button1_search_toolbar);
		tabButton1.setOnClickListener(this);
		tabButton2 = (Button)findViewById(R.id.button2_search_toolbar);
		tabButton2.setOnClickListener(this);
		tabButton3 = (Button)findViewById(R.id.button3_search_toolbar);
		tabButton3.setOnClickListener(this);
		tabButton4 = (Button)findViewById(R.id.button4_search_toolbar);
		tabButton4.setOnClickListener(this);
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
		if (searchVerDict == null) {
			searchVerDict = new HashMap<String, String>();
			searchVerDict.put(poolidArr.get(0), "0");
			Utility.shareInstance().saveObject(searchVerCachePath, searchVerDict);
		}
		else if (!searchVerDict.containsKey(poolidArr.get(0))) {
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
//		params.put("version", "0");
		params.put("version", searchVerDict.get(curPoolid));
		
		searchKeyList = new ArrayList<List<SearchKey>>();
		for (int i = 0; i < 4; i++) {
			List<SearchKey> list = new ArrayList<SearchKey>();
			searchKeyList.add(list);
		}
		
		toolbarLayout.setVisibility(View.INVISIBLE);
		searchExpandableListView.setVisibility(View.INVISIBLE);
		
		ToolBar toolBar = (ToolBar)getSupportFragmentManager().findFragmentById(R.id.search_toolbar);
		toolBar.setCallBack(new ToolBarCallBack() {
			
			@Override
			public void tapButton6() {
				if (searchHistoryArr.size() > 0) {
					if (searchHistoryIndex < searchHistoryArr.size()-1) {
						searchHistoryIndex++;
						searchField.setText(searchHistoryArr.get(searchHistoryIndex));
					}
				}
			}
			
			@Override
			public void tapButton5() {
			}
			
			@Override
			public void tapButton4() {
			}
			
			@Override
			public void tapButton3() {
			}
			
			@Override
			public void tapButton2() {
	            SearchActivity.this.finish();
	            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			}
			
			@Override
			public void tapButton1() {
				if (searchHistoryArr.size() > 0) {
					if (searchHistoryIndex > 0) {
						searchHistoryIndex--;
						searchField.setText(searchHistoryArr.get(searchHistoryIndex));
					}
				}
			}
		});
		
		//����item����ļ�����
		searchExpandableListView.setOnChildClickListener(new OnChildClickListener() {
			
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
            	Item curItem = adapter.getChild(groupPosition, childPosition);
            	
            	Intent intent=new Intent();
            	intent.setClass(SearchActivity.this, DetailActivity.class);
            	Bundle bundle = new Bundle();
            	bundle.putSerializable("item", curItem);
            	bundle.putSerializable("type", DeatilType.DeatilTypeSearch);
            	intent.putExtras(bundle);
            	intent.putExtra("poolId", curPoolid);
            	intent.putExtra("subjectId", subjectid);
            	
            	if (DetailActivity.staticMusicPlayer != null && DetailActivity.staticMusicPlayer.player.isPlaying() && DetailActivity.staticMusicPlayer.isItemAudio && DetailActivity.staticMusicPlayer.curPoolid().equals(curPoolid) && DetailActivity.staticMusicPlayer.curItemid().equals(curItem.getId())) {
            		intent.putExtra("isForPlaying", true);
				}
            	else {
            		intent.putExtra("isForPlaying", false);
				}
            	
            	startActivity(intent); 
            	overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
        });
		
		if (!TextUtils.isEmpty(searchField.getText().toString())) {
			curIndex = 0;
			tabButton1.setTextColor(getResources().getColor(R.color.detail_toolbutton_color_hig));
			search();
		}
	}
	
//	@Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            Intent myIntent = new Intent();
//            myIntent.setClass(SearchActivity.this, MainActivity.class);
//            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(myIntent);
//            SearchActivity.this.finish();
//            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//        }
//        return super.onKeyDown(keyCode, event);
//    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.relativeLayout_search) {
	         InputMethodManager imm = (InputMethodManager)  
             getSystemService(Context.INPUT_METHOD_SERVICE);  
             imm.hideSoftInputFromWindow(v.getWindowToken(), 0); 
		}
		else if (v.getId() == R.id.button1_search_toolbar) {
			curIndex = 0;
			refreshTabButtons();
			tabButton1.setTextColor(getResources().getColor(R.color.detail_toolbutton_color_hig));
			reloadParam();
			if (curSearchStr != null) {
				startSearch(curSearchStr);
			}
		}
		else if (v.getId() == R.id.button2_search_toolbar) {
			curIndex = 1;
			refreshTabButtons();
			tabButton2.setTextColor(getResources().getColor(R.color.detail_toolbutton_color_hig));
			reloadParam();
			if (curSearchStr != null) {
				startSearch(curSearchStr);
			}
		}
		else if (v.getId() == R.id.button3_search_toolbar) {
			curIndex = 2;
			refreshTabButtons();
			tabButton3.setTextColor(getResources().getColor(R.color.detail_toolbutton_color_hig));
			reloadParam();
			if (curSearchStr != null) {
				startSearch(curSearchStr);
			}
		}
		else if (v.getId() == R.id.button4_search_toolbar) {
			curIndex = 3;
			refreshTabButtons();
			tabButton4.setTextColor(getResources().getColor(R.color.detail_toolbutton_color_hig));
			reloadParam();
			if (curSearchStr != null) {
				startSearch(curSearchStr);
			}
		}
		else if (v.getId() == R.id.button_search_field) {
			search();
		}
	}
	
	private void refreshTabButtons() {
		tabButton1.setTextColor(getResources().getColor(R.color.detail_toolbutton_color_nor));
		tabButton2.setTextColor(getResources().getColor(R.color.detail_toolbutton_color_nor));
		tabButton3.setTextColor(getResources().getColor(R.color.detail_toolbutton_color_nor));
		tabButton4.setTextColor(getResources().getColor(R.color.detail_toolbutton_color_nor));
	}
	
	private void reloadParam() {
		curPoolid = poolidArr.get(curIndex);
		if (!searchVerDict.containsKey(curPoolid)) {
			searchVerDict.put(curPoolid, "0");
			Utility.shareInstance().saveObject(searchVerCachePath, searchVerDict);
		}
		params.put("poolid", curPoolid);
		params.put("version", searchVerDict.get(curPoolid));
	}
	
	private void noResultTip() {
		searchExpandableListView.setVisibility(View.INVISIBLE);
		tipTextView.setText(getString(R.string.search_nocontent_tip));
	}
	
	private void search() {
		String searchString = searchField.getText().toString().trim();  
        if (TextUtils.isEmpty(searchString)) {  
            Toast.makeText(this, "�����ؼ��ʲ���Ϊ��", Toast.LENGTH_LONG).show(); 
            return;
        }
		startSearch(searchString);
        curSearchStr = searchString;
        addHistoryKey(searchString);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0); 
        toolbarLayout.setVisibility(View.VISIBLE);
	}
	
	private void startSearch(final String searchString) {
		List<SearchKey> curKeyList = searchKeyList.get(curIndex);
		if (curKeyList == null || curKeyList.size() == 0) {
			curSearchKeyCachePath = Utility.shareInstance().cachPoolDir(curPoolid, subjectid)+getString(R.string.SPSearchKeyList);
			curSearchKeyCacheList = (List<SearchKey>)Utility.shareInstance().getObject(curSearchKeyCachePath);
			if (curSearchKeyCacheList != null) {
				searchKeyList.get(curIndex).addAll(curSearchKeyCacheList);
			}
			
			final String datePathString = Utility.shareInstance().resRootDir()+this.getString(R.string.SPPoolDateSearchDict);
			Log.v("sp", "datePathString:"+datePathString);
			HashMap<String, Long> dateMap = (HashMap<String, Long>)Utility.shareInstance().getObject(datePathString);
			Log.v("sp", "dateMap:"+dateMap);
			Long lastDate = dateMap.get(curPoolid);
			long gap = preferences.getLong("SPQueryGap", 0);
			
			if (Utility.shareInstance().isNetworkAvailable(this)) {
				final long curDate = System.currentTimeMillis()/1000;
				Log.v("sp", "poolid:"+curPoolid);
				Log.v("sp", "lastDate:"+lastDate);
				Log.v("sp", "curDate:"+curDate);
				Log.v("sp", "gap:"+gap);
				if (lastDate != null && curDate-lastDate.longValue() < gap && curSearchKeyCacheList.size() > 0) {
					searchResult(searchString, curSearchKeyCacheList);
					return;
				}
				
				AsyncHttpClient client = new AsyncHttpClient();
				String url = new String(this.getString(R.string.host) + "search/keywd");
				Log.v("sp", ""+url);
				Log.v("sp", ""+params);
				try {  
					client.post(url, params, new JsonHttpResponseHandler() {  

		                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {   
		                	Log.v("sp", "" + jsonObject); 
		                	if (statusCode == 200) {
		                		HashMap<String, Long> dateMap = (HashMap<String, Long>)Utility.shareInstance().getObject(datePathString);
		                		dateMap.put(curPoolid, new Long(curDate));
		                		Utility.shareInstance().saveObject(datePathString, dateMap);
		                		
		                		try {
		                			searchVerDict.put(curPoolid, jsonObject.getString("version"));
		                			Utility.shareInstance().saveObject(searchVerCachePath, searchVerDict);
		                			List<SearchKey> resultArrKeys = new ArrayList<SearchKey>();
		                			if (jsonObject.getInt("isUpdate") == 0 ) {
	                					if (curSearchKeyCacheList != null) {
	                						searchResult(searchString, curSearchKeyCacheList);
	                					}
	                					else {
											JSONArray itemArr = jsonObject.getJSONArray("item");
											if (itemArr != null && itemArr.length() > 0) {
												for (int i = 0; i < itemArr.length(); i++) {
													JSONObject item = (JSONObject) itemArr.opt(i);
													SearchKey searchKey = new SearchKey();
													searchKey.setItemid(item.getString("itemid"));
													searchKey.setKeywords(item.getString("keyword").split(";"));
													resultArrKeys.add(searchKey);
												}
												searchKeyList.get(curIndex).clear();
												searchKeyList.get(curIndex).addAll(resultArrKeys);
												Utility.shareInstance().saveObject(curSearchKeyCachePath, resultArrKeys);
												searchResult(searchString, resultArrKeys);
											}
										}
									}
		                			else {
		                				if (curSearchKeyCacheList != null) {
											resultArrKeys = new ArrayList<SearchKey>(updateSearchKeyCache(curSearchKeyCacheList, jsonObject.getJSONArray("dynamic"), curSearchKeyCachePath));
											searchKeyList.get(curIndex).clear();
											searchKeyList.get(curIndex).addAll(resultArrKeys);
											searchResult(searchString, resultArrKeys);
										}
		    		    				else {
		    		    					noResultTip();
		    							}
		                			}
								} catch (JSONException e) {
									if (curSearchKeyCacheList != null) {
										searchResult(searchString, curSearchKeyCacheList);
									}
									Log.e("sp", "" + Log.getStackTraceString(e));
								}
							}
		                	else {
		        				if (curSearchKeyCacheList != null) {
		        					searchResult(searchString, curSearchKeyCacheList);
		        				}
							}
		                }  
		                
		                public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
		    				if (curSearchKeyCacheList != null) {
		    					searchResult(searchString, curSearchKeyCacheList);
		    				}
		    				else {
		    					noResultTip();
							}
		                }
		                   
		            });  
				} catch (Exception e) {
					if (curSearchKeyCacheList != null) {
						searchResult(searchString, curSearchKeyCacheList);
					}
					Log.e("sp", "" + Log.getStackTraceString(e));
				}
			}
			else {
				if (curSearchKeyCacheList != null) {
					searchResult(searchString, curSearchKeyCacheList);
				}
			}
		}
		else {
			searchResult(searchString, curKeyList);
		}
	}
	
	private List<SearchKey> updateSearchKeyCache(List<SearchKey> cacheArr, JSONArray updateArr, String cachePath) {
		try {
			String kSPItemOptype = "optype";
			String kSPItemOptypeAdd = "A";
			String kSPItemOptypeDelete = "D";
			String kSPItemOptypeUpdate = "U";
			
			if (updateArr != null && updateArr.length() > 0) {
				for (int i = 0; i < updateArr.length(); i++) {
					JSONObject topInfo = updateArr.getJSONObject(i);
					if (topInfo.getString(kSPItemOptype).equals(kSPItemOptypeAdd)) {
						SearchKey aSearchKey = new SearchKey();
						aSearchKey.setItemid(topInfo.getString("itemid"));
						aSearchKey.setKeywords(topInfo.getString("keyword").split(";"));
						cacheArr.add(aSearchKey);
					}
					else {
						for (int j = 0; j < cacheArr.size(); j++) {
							SearchKey searchKey = cacheArr.get(j);
							if (searchKey.getItemid().equals(topInfo.getString("itemid"))) {
								if (topInfo.getString(kSPItemOptype).equals(kSPItemOptypeDelete)) {
									cacheArr.remove(j);
								}
								else if (topInfo.getString(kSPItemOptype).equals(kSPItemOptypeUpdate)) {
									searchKey.setKeywords(topInfo.getString("keyword").split(";"));
								}
							}
						}
					}
				}
			}
			Utility.shareInstance().saveObject(cachePath, cacheArr);
			
		} catch (Exception e) {
			return cacheArr;
		}
		return cacheArr;
	}
	
	private void searchResult(String searchStr, List<SearchKey> keyList) {
		String cachePathString = Utility.shareInstance().cachPoolDir(curPoolid, subjectid)+ getString(R.string.SPItemList);
		List<Item> poolCacheArr = (List<Item>)Utility.shareInstance().getObject(cachePathString);
		List<String> itemidResultList = new ArrayList<String>();
		List<Item> resultItemList = new ArrayList<Item>();
		for (int i = 0; i < keyList.size(); i++) {
			SearchKey searchKey = keyList.get(i);
			for (int j = 0; j < searchKey.getKeywords().length; j++) {
				String key = searchKey.getKeywords()[j];
				if (key.equals(searchStr)) {
					itemidResultList.add(searchKey.getItemid());
					break;
				}
			}
		}
		
		if (poolCacheArr != null && poolCacheArr.size() > 0) {
			for (int i = 0; i < poolCacheArr.size(); i++) {
				Item topItem = poolCacheArr.get(i);
				if (topItem.getItemArr().size() > 0) {
					for (int j = 0; j < topItem.getItemArr().size(); j++) {
						Item secItem = topItem.getItemArr().get(j);
						if (secItem.getItemArr().size() > 0) {
							for (int k = 0; k < secItem.getItemArr().size(); k++) {
								Item resultItem = secItem.getItemArr().get(k);
								List<Item> tempItemArr = resultItem.getItemArr();
								
								Iterator<Item> it = tempItemArr.iterator();
								while (it.hasNext()) {
									Item forItem = it.next(); 
									if (!itemidResultList.contains(forItem.getId())) {
										it.remove();
									}
								}
								
								Log.v("sp", "tempItemArr.size()"+tempItemArr.size());
								if (tempItemArr.size() > 0) {
									resultItem.setItemArr(tempItemArr);
									resultItemList.add(resultItem);
								}
							}
						}
					}
				}
			}
		}
		
		if (resultItemList.size() > 0) {
			if (resultArr != null && resultArr.size() > 0) {
				resultArr.clear();
			}
			resultArr = new ArrayList<Item>(resultItemList);
			adapter = new ExpandableListViewaAdapter(SearchActivity.this);
			searchExpandableListView.setAdapter(adapter);
			expendAllGroup();
			searchExpandableListView.setVisibility(View.VISIBLE);
		}
		else {
			noResultTip();
		}
	}
	
	private void expendAllGroup() {
		if (resultArr.size() > 0) {
			for (int i = 0; i < resultArr.size(); i++) {
				searchExpandableListView.expandGroup(i);
			}
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
             textView.setSingleLine(true);
             textView.setEllipsize(TruncateAt.END);
             textView.setTextSize(17);
             textView.setTextColor(Color.BLACK);
             textView.setText(item.getCaption());
	         if (item.getItemArr() != null) {
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
