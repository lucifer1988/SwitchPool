package com.switchpool.home;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.switchpool.search.SearchActivity;
import com.switchpool.utility.NoContnetFragment;
import com.switchpool.utility.ToolBar;
import com.switchpool.utility.ToolBarCallBack;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.switchpool.model.Item;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class TopListActivity extends FragmentActivity {

	public TopListActivity() {
	}
	private String poolId, subjectId, poolName;
	List<Item> topListItemArr;

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	private  ExpandableListView  topExpandableListView;
	private  ExpandableListViewaAdapter adapter;
	
	FragmentManager fManager;
	NoContnetFragment ncFragment;
	
	@Override	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.toplist);
		
		fManager = getSupportFragmentManager();
		
        /*初始化界面的list目录*/
		topExpandableListView = (ExpandableListView)findViewById(R.id.expandableListView_toplist_con);
		topExpandableListView.setGroupIndicator(null);
		String version;
		Intent intent = getIntent();
		poolId=intent.getStringExtra("poolId");
		subjectId=intent.getStringExtra("subjectId");
		poolName = intent.getStringExtra("poolName");
		
		TextView textView = (TextView)findViewById(R.id.textView_toplist_nav);
		textView.setText(getString(R.string.toplist_nav_title, poolName));
		
		ToolBar toolBar = (ToolBar)getSupportFragmentManager().findFragmentById(R.id.toplist_toolbar);
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
				Intent onItemClickIntent = new Intent();
				onItemClickIntent.putExtra("subjectId", subjectId);
				onItemClickIntent.setClass(TopListActivity.this, SearchActivity.class);
				TopListActivity.this.startActivity(onItemClickIntent);
				TopListActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
			
			@Override
			public void tapButton2() {
	            Intent myIntent = new Intent();
	            myIntent.setClass(TopListActivity.this, MainActivity.class);
	            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(myIntent);
	            TopListActivity.this.finish();
	            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			}
			
			@Override
			public void tapButton1() {
				// TODO Auto-generated method stub
			}
		});
		
		preferences = getSharedPreferences("switchpool", 0x0001);
		editor = preferences.edit();
		
		// 读取字符串数据
		version = preferences.getString(poolId, null);
		if (version == null) {
			version = "0";
			editor.putString(poolId, version);
		}
		/*获得初始化数据*/
		poolItemRequstPost(poolId,version);
		
		//设置item点击的监听器
		topExpandableListView.setOnChildClickListener(new OnChildClickListener() {
			
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
            	
            	Intent intent=new Intent();
            	intent.setClass(TopListActivity.this, SecListActivity.class);
            	Bundle bundle = new Bundle();
            	bundle.putSerializable("item", adapter.getChild(groupPosition, childPosition));
            	intent.putExtras(bundle);
            	intent.putExtra("poolId", poolId);
            	intent.putExtra("subjectId", subjectId);
            	intent.putExtra("poolName", poolName);
            	startActivity(intent); 
            	overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
        });
		
	}
	
	private void showNoContent() {
        if (ncFragment == null) {  
    		ncFragment = new NoContnetFragment();
    		ncFragment.initialize(getString(R.string.nocontenttip_list));
    		FragmentTransaction transaction = fManager.beginTransaction();
    		transaction.add(R.id.relativeLayout_toplist_nocontent, ncFragment).commit();
        }
        fManager.beginTransaction().show(ncFragment).commit();
	}
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	 }
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent myIntent = new Intent();
            myIntent.setClass(TopListActivity.this, MainActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent);
            TopListActivity.this.finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        return super.onKeyDown(keyCode, event);
    }
	
	private void poolItemRequstPost(String poolid, String version) {
		final String cachePathString = Utility.shareInstance().cachPoolDir(poolId, subjectId)+this.getString(R.string.SPItemList);
		final List<Item> cacheArr = (List<Item>) Utility.shareInstance().getObject(cachePathString);
		
		if (Utility.shareInstance().isNetworkAvailable(this)) {
			Utility.shareInstance().showWaitingHUD(this);
			AsyncHttpClient client = new AsyncHttpClient();
			String url = new String(this.getString(R.string.host) + "file/getSource");
			Log.v("sp", "" + url);
			
			RequestParams params = new RequestParams(); 
			params.put("poolid", poolid);
			params.put("version", version);
			try {  
				client.get(url, params, new JsonHttpResponseHandler() {  
	                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
	                	Utility.shareInstance().hideWaitingHUD();
	                	Log.v("sp", "" + jsonObject); 
	                	if (statusCode == 200) {
	                		try {
	                			editor.putString(poolId, jsonObject.getString("version"));
	                			List<Item> topItemArr = new ArrayList<Item>();
	                			if ((jsonObject.getInt("isUpdate") == 0 ) ) {/*在服务器端返回的数据没有更新没有更新的情况*/
	                				if (cacheArr != null) {
	                					topListItemArr = new ArrayList<Item>(cacheArr); 
	                					adapter = new ExpandableListViewaAdapter(TopListActivity.this);
	                					topExpandableListView.setAdapter(adapter);
	                				}
	                				else {
		                				JSONArray poolResultJsonArray = jsonObject.getJSONArray("item");
		                				for(int iTop = 0; iTop<poolResultJsonArray.length(); iTop++){
		                					JSONObject topJsonObject = (JSONObject)poolResultJsonArray.opt(iTop);
		                					Item topItem = new Item();
		                					topItem.setCaption(topJsonObject.getString("caption"));
		                					topItem.setId(topJsonObject.getString("id"));
		                					topItem.setOrder(topJsonObject.getInt("order"));
		                					List<Item> secItemArr = new ArrayList<Item>();
		                					JSONArray topChildJsonArray = topJsonObject.getJSONArray("children");
		                					for(int iSec =0; iSec < topChildJsonArray.length(); iSec++){
		                						JSONObject secJsonObject = (JSONObject)topChildJsonArray.opt(iSec);
		                    					Item secItem = new Item();
		                    					secItem.setCaption(secJsonObject.getString("caption"));
		                    					secItem.setId(secJsonObject.getString("id"));
		                    					secItem.setOrder(secJsonObject.getInt("order"));
		                    					secItem.setParentid(secJsonObject.getString("parentid"));
		                    					List<Item> thrItemArr = new ArrayList<Item>();
		                    					JSONArray secChildJsonArray = secJsonObject.getJSONArray("children");
		                    					for(int iThr =0; iThr < secChildJsonArray.length(); iThr++){
		                    						JSONObject thrJsonObject = (JSONObject)secChildJsonArray.opt(iThr);
		                        					Item thrItem = new Item();
		                        					thrItem.setCaption(thrJsonObject.getString("caption"));
		                        					thrItem.setId(thrJsonObject.getString("id"));
		                        					thrItem.setOrder(thrJsonObject.getInt("order"));
		                        					thrItem.setParentid(thrJsonObject.getString("parentid"));
		                        					List<Item> forItemArr = new ArrayList<Item>();
		                        					JSONArray thrChildJsonArray = thrJsonObject.getJSONArray("children");
		                        					for(int iFor =0; iFor < thrChildJsonArray.length(); iFor++){
		                        						JSONObject forJsonObject = (JSONObject)thrChildJsonArray.opt(iFor);
		                            					Item forItem = new Item();
		                            					forItem.setCaption(forJsonObject.getString("caption"));
		                            					forItem.setId(forJsonObject.getString("id"));
		                            					forItem.setOrder(forJsonObject.getInt("order"));
		                            					forItem.setParentid(forJsonObject.getString("parentid"));                           					
		                            					forItemArr.add(forItem);
		                        					}
		                        					thrItem.setItemArr(forItemArr);
		                        					thrItemArr.add(thrItem);
		                    					}
		                    					secItem.setItemArr(thrItemArr);
		                    					secItemArr.add(secItem);
		                					}
		                					topItem.setItemArr(secItemArr);
		                					topItemArr.add(topItem);                					
		                				}
		                				topListItemArr = new ArrayList<Item>(topItemArr); 
		                				Utility.shareInstance().saveObject(cachePathString, topItemArr);
		                				adapter = new ExpandableListViewaAdapter(TopListActivity.this);
		                				topExpandableListView.setAdapter(adapter);
	                				}
								} else {
									if (cacheArr != null) {
										topListItemArr = new ArrayList<Item>(updatePoolCache(cacheArr, jsonObject.getJSONObject("dynamic"), cachePathString));
		                				adapter = new ExpandableListViewaAdapter(TopListActivity.this);
		                				topExpandableListView.setAdapter(adapter);
									}
									else {
										showNoContent();
										return;
									}
								}	
							} catch (JSONException e) {
								if (cacheArr == null) {
									showNoContent();
									return;
								}
								else {
									topListItemArr = new ArrayList<Item>(cacheArr); 
									adapter = new ExpandableListViewaAdapter(TopListActivity.this);
									topExpandableListView.setAdapter(adapter);
								}
								Log.e("sp", "" + Log.getStackTraceString(e));
							}
						}
	                	else {
							if (cacheArr == null) {
								showNoContent();
								return;
							}
							else {
								topListItemArr = new ArrayList<Item>(cacheArr); 
								adapter = new ExpandableListViewaAdapter(TopListActivity.this);
								topExpandableListView.setAdapter(adapter);
							}
						}
	                }
	                
	                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable){
	                	Utility.shareInstance().hideWaitingHUD();
	                	if (cacheArr == null) {
							showNoContent();
							return;
						}
						else {
							topListItemArr = new ArrayList<Item>(cacheArr); 
							adapter = new ExpandableListViewaAdapter(TopListActivity.this);
							topExpandableListView.setAdapter(adapter);
						}
	                }
	            });  
			} catch (Exception e) {
				Utility.shareInstance().hideWaitingHUD();
				if (cacheArr == null) {
					showNoContent();
					return;
				}
				else {
					topListItemArr = new ArrayList<Item>(cacheArr); 
					adapter = new ExpandableListViewaAdapter(TopListActivity.this);
					topExpandableListView.setAdapter(adapter);
				}
				Log.e("sp", "" + Log.getStackTraceString(e));
			}
		}
		else {
			if (cacheArr == null) {
				showNoContent();
				return;
			}
			else {
				topListItemArr = new ArrayList<Item>(cacheArr); 
				adapter = new ExpandableListViewaAdapter(TopListActivity.this);
				topExpandableListView.setAdapter(adapter);
			}
		}	
	}
	
	private List<Item> updatePoolCache(List<Item> cacheArr, JSONObject updateInfo, String cachePath) {
		try {
			JSONArray topInfoArray = updateInfo.getJSONArray("top");
			JSONArray firstInfoArray = updateInfo.getJSONArray("firstInfos");
			JSONArray secondInfoArray = updateInfo.getJSONArray("secondInfos");
			JSONArray itemInfoArray = updateInfo.getJSONArray("itemInfos");
			
			String kSPItemOptype = "optype";
			String kSPItemOptypeAdd = "A";
			String kSPItemOptypeDelete = "D";
			String kSPItemOptypeUpdate = "U";
			String kSPItemParent = "parent";
			
			for (int i = 0; i < topInfoArray.length(); i++) {
				JSONObject topInfo = topInfoArray.getJSONObject(i);
				if (topInfo.getString(kSPItemOptype).equals(kSPItemOptypeAdd)) {
					Item aTopItem = new Item();
					aTopItem.setCaption(topInfo.getString("caption"));
					aTopItem.setId(topInfo.getString("id"));
					aTopItem.setOrder(topInfo.getInt("order"));                           					
					cacheArr.add(aTopItem);
				}
				else {
					for (int j = 0; j < cacheArr.size(); j++) {
						Item topItem = (Item) cacheArr.get(j);
						if (topItem.getId().equals(topInfo.getString("id"))) {
							if (topInfo.getString(kSPItemOptype).equals(kSPItemOptypeDelete)) {
								cacheArr.remove(topItem);
							}
							else if (topInfo.getString(kSPItemOptype).equals(kSPItemOptypeUpdate)) {
								topItem.setCaption(topInfo.getString("caption"));
								topItem.setOrder(topInfo.getInt("order"));
							}
						}
					}
				}
			}
			
			for (int i = 0; i < firstInfoArray.length(); i++) {
				JSONObject firInfo = firstInfoArray.getJSONObject(i);
				for (int j = 0; j < cacheArr.size(); j++) {
					Item topItem = (Item) cacheArr.get(j);
					if (topItem.getId().equals(firInfo.getJSONObject(kSPItemParent).getString("id"))) {
						List<Item> topChildArr = new ArrayList<Item>(topItem.getItemArr());
						if (firInfo.getString(kSPItemOptype).equals(kSPItemOptypeAdd)) {
							Item aFirItem = new Item();
							aFirItem.setCaption(firInfo.getString("caption"));
							aFirItem.setId(firInfo.getString("id"));
							aFirItem.setOrder(firInfo.getInt("order"));                           					
							topChildArr.add(aFirItem);
						}
						else {
							for (int k = 0; k < topChildArr.size(); k++) {
								Item firItem = (Item) topChildArr.get(k);
								if (firItem.getId().equals(firInfo.getString("id"))) {
									if (firInfo.getString(kSPItemOptype).equals(kSPItemOptypeDelete)) {
										topChildArr.remove(topItem);
									}
									else if (firInfo.getString(kSPItemOptype).equals(kSPItemOptypeUpdate)) {
										firItem.setCaption(firInfo.getString("caption"));
										firItem.setOrder(firInfo.getInt("order"));
									}
								}
							}
						}
						topItem.setItemArr(topChildArr);
					}
				}
			}
			
			for (int i = 0; i < secondInfoArray.length(); i++) {
				JSONObject secInfo = secondInfoArray.getJSONObject(i);
				for (int j = 0; j < cacheArr.size(); j++) {
					Item topItem = (Item) cacheArr.get(j);
					if (topItem.getId().equals(secInfo.getJSONObject(kSPItemParent).getJSONObject(kSPItemParent).getString("id"))) {
						List<Item> topChildArr = new ArrayList<Item>(topItem.getItemArr());
						for (int k = 0; k < topChildArr.size(); k++) {
							Item firItem = (Item) topChildArr.get(j);
							if (firItem.getId().equals(secInfo.getJSONObject(kSPItemParent).getString("id"))) {
								List<Item> firChildArr = new ArrayList<Item>(firItem.getItemArr());
								if (secInfo.getString(kSPItemOptype).equals(kSPItemOptypeAdd)) {
									Item aSecItem = new Item();
									aSecItem.setCaption(secInfo.getString("caption"));
									aSecItem.setId(secInfo.getString("id"));
									aSecItem.setOrder(secInfo.getInt("order"));                           					
									firChildArr.add(aSecItem);
								}
								else {
									for (int l = 0; l < firChildArr.size(); l++) {
										Item secItem = (Item) firChildArr.get(l);
										if (secItem.getId().equals(secInfo.getString("id"))) {
											if (secInfo.getString(kSPItemOptype).equals(kSPItemOptypeDelete)) {
												firChildArr.remove(secItem);
											}
											else if (secInfo.getString(kSPItemOptype).equals(kSPItemOptypeUpdate)) {
												secItem.setCaption(secInfo.getString("caption"));
												secItem.setOrder(secInfo.getInt("order"));
											}
										}
									}
								}
								firItem.setItemArr(firChildArr);
							}
						}
						topItem.setItemArr(topChildArr);
					}
				}
			}
			
			for (int i = 0; i < itemInfoArray.length(); i++) {
				JSONObject itemInfo = itemInfoArray.getJSONObject(i);
				for (int j = 0; j < cacheArr.size(); j++) {
					Item topItem = (Item) cacheArr.get(j);
					if (topItem.getId().equals(itemInfo.getJSONObject(kSPItemParent).getJSONObject(kSPItemParent).getJSONObject(kSPItemParent).getString("id"))) {
						List<Item> topChildArr = new ArrayList<Item>(topItem.getItemArr());
						for (int k = 0; k < topChildArr.size(); k++) {
							Item firItem = (Item) topChildArr.get(j);
							if (firItem.getId().equals(itemInfo.getJSONObject(kSPItemParent).getJSONObject(kSPItemParent).getString("id"))) {
								List<Item> firChildArr = new ArrayList<Item>(firItem.getItemArr());
								for (int l = 0; l < firChildArr.size(); l++) {
									Item secItem = (Item) firChildArr.get(j);
									if (secItem.getId().equals(itemInfo.getJSONObject(kSPItemParent).getString("id"))) {
										List<Item> secChildArr = new ArrayList<Item>(secItem.getItemArr());
										if (itemInfo.getString(kSPItemOptype).equals(kSPItemOptypeAdd)) {
											Item aItemItem = new Item();
											aItemItem.setCaption(itemInfo.getString("caption"));
											aItemItem.setId(itemInfo.getString("id"));
											aItemItem.setOrder(itemInfo.getInt("order"));                           					
											firChildArr.add(aItemItem);
										}
										else {
											for (int m = 0; m < firChildArr.size(); m++) {
												Item itemItem = (Item) firChildArr.get(m);
												if (itemItem.getId().equals(itemInfo.getString("id"))) {
													if (itemInfo.getString(kSPItemOptype).equals(kSPItemOptypeDelete)) {
														firChildArr.remove(itemItem);
													}
													else if (itemInfo.getString(kSPItemOptype).equals(kSPItemOptypeUpdate)) {
														itemItem.setCaption(itemInfo.getString("caption"));
														itemItem.setOrder(itemInfo.getInt("order"));
													}
												}
											}
										}
										secItem.setItemArr(secChildArr);
									}
								}
								firItem.setItemArr(firChildArr);
							}
						}
						topItem.setItemArr(topChildArr);
					}
				}
			}
			
			Utility.shareInstance().saveObject(cachePath, cacheArr);
		} catch (Exception e) {
			return cacheArr;
		}
		return cacheArr;
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
            return topListItemArr.get(groupPosition).getItemArr().get(childPosition);
        }
 
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childPosition;
        }
 
        @Override
        public View getChildView(int groupPosition, int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {
             
            Item item =topListItemArr.get(groupPosition).getItemArr().get(childPosition);
             
            return getGenericView(item);
        }
 
        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return topListItemArr.get(groupPosition).getItemArr().size();
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
            return topListItemArr.size();
        }
 
        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }
 
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent) {
             
           Item   item=topListItemArr.get(groupPosition);
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
        	 AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                     ViewGroup.LayoutParams.MATCH_PARENT, 100);
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
