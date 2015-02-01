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
import com.switchpool.model.Subject;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import com.switchpool.model.Item;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TopListActivity extends Activity {

	public TopListActivity() {
		// TODO Auto-generated constructor stub
	}
	private String poolId;
	List<Item> topListItemArr;

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	private  ExpandableListView  topExpandableListView;
	
	@Override	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.toplist);
		
        /*初始化界面的list目录*/
		topExpandableListView = (ExpandableListView)findViewById(R.id.expandableListView_toplist_con);
		
		String version;
		Intent intent = getIntent();
		poolId=intent.getStringExtra("poolId");
		
		preferences = getSharedPreferences("switchpool", MODE_WORLD_READABLE);
		editor = preferences.edit();
		
		// 读取字符串数据
		version = preferences.getString(poolId, null);
		if (version == null) {
			version = "0";
			editor.putString(poolId, version);
		}
		/*获得初始化数据*/
		poolItemRequstPost(poolId,version);
		
	}
	
	private void poolItemRequstPost(String poolid, String version) {
		AsyncHttpClient client = new AsyncHttpClient();
		String url = new String(this.getString(R.string.host) + "file/getSource");
		Log.v("sp", "" + url);
		
		RequestParams params = new RequestParams(); 
		params.put("poolid", poolid);
		params.put("version", version);
		
		try {  
			client.get(url, params, new JsonHttpResponseHandler() {  

                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {   
                	Log.v("sp", "" + jsonObject); 
                	if (statusCode == 200) {
                		try {
                			editor.putString(poolId, jsonObject.getString("version"));
                			List<Item> topItemArr = new ArrayList<Item>();
                			if (jsonObject.getInt("isUpdate") == 0 ) {
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
                				topExpandableListView.setAdapter(new ExpandableListViewaAdapter(TopListActivity.this));
							} else {
								//TODO :更新树 --lxl
								
							}
                			

							
						} catch (JSONException e) {
							Log.e("sp", "" + Log.getStackTraceString(e));
						}
					}
                }  
                   
            });  
		} catch (Exception e) {
			Log.e("sp", "" + Log.getStackTraceString(e));
			Toast.makeText(this, "登录失败", Toast.LENGTH_LONG).show(); 
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
        public Object getChild(int groupPosition, int childPosition) {
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
        public Object getGroup(int groupPosition) {
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
                    ViewGroup.LayoutParams.MATCH_PARENT, 40);  
            lp.height = 44;           
            TextView text = new TextView(TopListActivity.this);    
            text.setLayoutParams(lp);    
            // Center the text vertically    
            text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);    
            // Set the text starting position    
            text.setPadding(36, 0, 0, 0);    
                
            text.setText(item.getCaption());    
            return text;
            
//        	LinearLayout linearLayout = new LinearLayout(TopListActivity.this);
//        	linearLayout.setOrientation(0);
//            ImageView generallogo = new ImageView(TopListActivity.this);
//           // generallogo.setImageResource();
//            linearLayout.addView(generallogo);           
//            AbsListView.LayoutParams layoutParams =new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
//            TextView  textView =new TextView(activity);               
//            textView.setGravity(Gravity.CENTER_VERTICAL |Gravity.LEFT);
//            textView.setPadding(40, 0, 0, 0);
//            textView.setText(item.getCaption());
//            linearLayout.addView(textView);
//            return linearLayout;
         }		
		
    }

}
