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
import android.widget.Toast;

public class TopListActivity extends Activity {

	public TopListActivity() {
		// TODO Auto-generated constructor stub
	}
	private String poolId;
	List<Item> topListItemArr = new ArrayList<Item>();
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	
	@Override	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.toplist);
		
		String version;
		Intent intent = getIntent();
		poolId=intent.getStringExtra("poolId");
		
		preferences = getSharedPreferences("switchpool", MODE_WORLD_READABLE);
		editor = preferences.edit();
		
		// ¶ÁÈ¡×Ö·û´®Êý¾Ý
		version = preferences.getString(poolId, null);
		if (version == null) {
			version = "0";
			editor.putString(poolId, version);
		}	
		
	}
	
	private void poolItemRequstPost(String poolid, String version) {
		AsyncHttpClient client = new AsyncHttpClient();
		String url = new String(this.getString(R.string.host) + "/file/getSource");
		Log.v("sp", "" + url);
		
		List<JSONArray> topJsonArray = new ArrayList<JSONArray>();
		
		
		RequestParams params = new RequestParams(); 
		params.put("poolid", poolid);
		params.put("version", version);
		
		try {  
			client.post(url, params, new JsonHttpResponseHandler() {  

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
                        					for(int iFor =0; iFor < secChildJsonArray.length(); iFor++){
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
                			topItemArr = new ArrayList<Item>(topItemArr); 
							} else {
								//TODO :¸üÐÂÊ÷ --lxl
							}
                			

							
						} catch (JSONException e) {
							Log.e("sp", "" + Log.getStackTraceString(e));
						}
					}
                }  
                  
            });  
		} catch (Exception e) {
			Log.e("sp", "" + Log.getStackTraceString(e));
			Toast.makeText(this, "µÇÂ¼Ê§°Ü", Toast.LENGTH_LONG).show(); 
		}
		
	}
	
	
	

}
