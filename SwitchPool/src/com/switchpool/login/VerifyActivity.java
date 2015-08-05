package com.switchpool.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.switchpool.home.MainActivity;
import com.switchpool.model.Subject;
import com.switchpool.model.User;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class VerifyActivity extends Activity {
	private String phoneString;
	private String verifyString;
	private String uid;
	
	private TextView phoneTextView;
	private EditText codEditText;
	private TextView timeTextView;
	private Button notRecButton;
	
	private int totalWaitTime = 60;
	private Thread timeThread;
	private Boolean isRunning = true;
	
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	
    //处理进度条更新
    Handler mHandler = new Handler(){
      @Override  
	    public void handleMessage(Message msg){  
	        switch (msg.what){
	        case 0: 
	        	if (totalWaitTime == 0) {
	        		timeTextView.setVisibility(View.INVISIBLE);
	        		notRecButton.setVisibility(View.VISIBLE);
	        		isRunning = false;
				} 
	        	else {
	        		timeTextView.setText(getString(R.string.login_verify_time, totalWaitTime));
	        		totalWaitTime--;
	        	}
	          break;
	      default:
	          break;
	        }
	    }  
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_verify);
		
		codEditText = (EditText)findViewById(R.id.editText1_verify);
		phoneTextView = (TextView)findViewById(R.id.textView2_verify);
		timeTextView = (TextView)findViewById(R.id.textView3_verify);
		notRecButton = (Button)findViewById(R.id.button1_verify);
		
		Intent intent = this.getIntent();
		phoneString=intent.getStringExtra("phone");
		verifyString=intent.getStringExtra("code");
		
		phoneTextView.setText(phoneString);
		codEditText.setText(verifyString);
		
		preferences = getSharedPreferences("switchpool", 0x0001);
		editor = preferences.edit();
		
		final int milliseconds = 1000;
		timeThread = new Thread(){
	        @Override
	        public void run(){
	          while(isRunning){  
		        try {  
		            sleep(milliseconds);  
		        } catch (InterruptedException e) {  
		            e.printStackTrace();  
		        }
		        mHandler.sendEmptyMessage(0);  
	          }  
	        }
	      };
	     timeThread.start();
	}
	
	public void back(View sourceView) {
		if (timeTextView.getVisibility() == View.VISIBLE) {
			showAD(102, getString(R.string.login_verify_during));
		}
		else {
			finish();
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		}
	}
	
	public void notReceive(View sourceView) {
		showAD(100, getString(R.string.login_verify_resend));
	}
	
	public void submit(View sourceView) {
        final String code = codEditText.getText().toString();
        //判断是否为空  
        if (TextUtils.isEmpty(code.trim())) {  
        	showAD(101, getString(R.string.login_verify_fail)); 
        	return;
        }
        
		AsyncHttpClient client = new AsyncHttpClient();
		String url = new String(this.getString(R.string.host) + "reg/index");
		Log.v("sp", "" + url);
		
		final User user = new User();
		user.setCellphone(phoneString);
		user.setPassword(null);
		user.setBrand(null);
		user.setIp(null);
		user.setMod(null);
		user.setOs(null);
		user.setOsver(null);
		user.setPrefix(null);
		user.setToken(null);
		user.setUeid(null);
		user.setUename(null);
		user.setUetype(null);
		user.setUid(null);
		user.setVender(null);
		user.setVer(null);
		
		RequestParams params = new RequestParams();  
		params.put("cellphone", phoneString);  
		params.put("passwd", user.getPassword()); 
		params.put("code", code); 
		params.put("ver", user.getVer());
		params.put("prefix", user.getPrefix());
		params.put("uetype", user.getUetype());
		params.put("ueid", user.getUeid());
		params.put("uename", user.getUename());
		params.put("os", user.getOs());
		params.put("osver", user.getOsver());
		params.put("vender", user.getVender());
		params.put("brand", user.getBrand());
		params.put("ip", user.getIp());
		params.put("mod", user.getMod());
		params.put("token", user.getToken());
		
		Log.v("sp", "" + params);
		
		try {  
			client.post(url, params, new JsonHttpResponseHandler() {  

                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                	Log.v("sp", "" + jsonObject); 
                	if (statusCode == 200) {
                		try {
                			if (jsonObject.getString("uid") != null) {
								uid = jsonObject.getString("uid");
								showAD(104, getString(R.string.login_verify_succeed, uid));
							}
						} catch (JSONException e) {
							Log.e("sp", "" + Log.getStackTraceString(e));
							showAD(101, getString(R.string.login_verify_fail)); 
						}
					}
                }  
                
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
					showAD(101, getString(R.string.login_verify_fail)); 
				}
                  
            });  
		} catch (Exception e) {
			Log.e("sp", "" + Log.getStackTraceString(e));
			showAD(101, getString(R.string.login_verify_fail)); 
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	
	public void showAD(int flag, String title) {
		if (flag == 101 || flag == 104) {
			AlertDialog.Builder builder  = new AlertDialog.Builder(this).setTitle(title);
			setPositiveButton(builder, flag)
			.create()
			.show();
		}
		else {
			AlertDialog.Builder builder  = new AlertDialog.Builder(this).setTitle(title);
			setPositiveButton(builder, flag);
			setNegativeButton(builder, flag)
			.create()
			.show();
		}
	}
	
	private AlertDialog.Builder setPositiveButton(
			AlertDialog.Builder builder, final int flag)
	{
		return builder.setPositiveButton(getString(R.string.OK), new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (flag == 102) {
					finish();
					overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
				}
				else if (flag == 100) {    
		    		AsyncHttpClient client = new AsyncHttpClient();
		    		String url = new String(VerifyActivity.this.getString(R.string.host) + "sms/code");
		    		Log.v("sp", "" + url);
		    		
		    		RequestParams params = new RequestParams();  
		    		params.put("cellphone", phoneString); 
		    		Log.v("sp", "" + params);
		    		
		    		try {  
		    			client.post(url, params, new JsonHttpResponseHandler() {  

		                    public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
		                    	Utility.shareInstance().hideWaitingHUD();
		                    	Log.v("sp", "" + jsonObject); 
		                    	if (statusCode == 200) {
		                    		try {
		                    			if (jsonObject.getString("flag") != null && jsonObject.getString("flag").equals("0")) {
		                	        		totalWaitTime = 60;
		                    				timeTextView.setVisibility(View.VISIBLE);
		                	        		notRecButton.setVisibility(View.INVISIBLE);
		                	        		isRunning = true;
		                	        		codEditText.setText(jsonObject.getString("code"));
										}
		    						} catch (JSONException e) {
		    							Log.e("sp", "" + Log.getStackTraceString(e));
		    							Toast.makeText(VerifyActivity.this, "发送验证失败", Toast.LENGTH_LONG).show(); 
		    						}
		    					}
		                    }  
		                    
		    				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
		    					Toast.makeText(VerifyActivity.this, "发送验证失败", Toast.LENGTH_LONG).show(); 
		    				}
		                      
		                });  
		    		} catch (Exception e) {
		    			Log.e("sp", "" + Log.getStackTraceString(e));
		    			Toast.makeText(VerifyActivity.this, "发送验证失败", Toast.LENGTH_LONG).show(); 
		    		}
		        }
				else if (flag == 101) {
					return;
				}
				else if (flag == 104) {
					AsyncHttpClient client = new AsyncHttpClient();
					String url = new String(VerifyActivity.this.getString(R.string.host) + "login/index");
					Log.v("sp", "" + url);
					
					final User user = new User();
					user.setCellphone("0");
					user.setPassword(null);
					user.setBrand(null);
					user.setIp(null);
					user.setMod(null);
					user.setOs(null);
					user.setOsver(null);
					user.setPrefix(null);
					user.setToken(null);
					user.setUeid(null);
					user.setUename(null);
					user.setUetype(null);
					user.setUid(uid);
					user.setVender(null);
					user.setVer(null);
					
					RequestParams params = new RequestParams();  
					params.put("cellphone", user.getCellphone());  
					params.put("passwd", user.getPassword()); 
					params.put("ver", user.getVer());
					params.put("prefix", user.getPrefix());
					params.put("uetype", user.getUetype());
					params.put("ueid", user.getUeid());
					params.put("uename", user.getUename());
					params.put("os", user.getOs());
					params.put("osver", user.getOsver());
					params.put("vender", user.getVender());
					params.put("brand", user.getBrand());
					params.put("ip", user.getIp());
					params.put("mod", user.getMod());
					params.put("token", user.getToken());
					params.put("uid", user.getUid());
					
					Log.v("sp", "" + params);
					
					
					try {  
						client.post(url, params, new JsonHttpResponseHandler() {  

			                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
			                	Utility.shareInstance().hideWaitingHUD();
			                	Log.v("sp", "" + jsonObject); 
			                	if (statusCode == 200) {
			                		try {
			                			user.setCellphone(jsonObject.getString("cellphone"));
										user.setUid(jsonObject.getString("uid"));
										user.setToken(jsonObject.getString("token"));
										user.setBuglog(jsonObject.getInt("buglog"));
										user.setChannel(jsonObject.getInt("channel"));
										user.setInreg(jsonObject.getInt("inreg"));
										user.setTopic(jsonObject.getInt("topic"));  
										
		    							editor.putLong(getString(R.string.SPQueryGap), (long)jsonObject.getInt("querygap"));
		    							editor.commit();
		    							
										Utility.shareInstance().saveObject(Utility.shareInstance().userInfoFile(), user);
										
										JSONArray resultSubjectList = jsonObject.getJSONArray("subjectList");
										List<Subject> subjectArr = new ArrayList<Subject>();
										 for(int i=0; i<resultSubjectList.length(); i++){ 
						                        JSONObject subjectJsonObject = (JSONObject)resultSubjectList.opt(i); 
						                        Subject subject = new Subject(); 
						                        subject.setTitle(subjectJsonObject.getString("title"));
						                        subject.setSubjectid(subjectJsonObject.getString("subjectid"));
						                        subject.setDesc(subjectJsonObject.getString("desc"));
						                        subject.setSeq(subjectJsonObject.getString("seq"));
						                        subject.setType(subjectJsonObject.getString("type"));
						                        subject.setBgImage(R.drawable.home_bg_header);
						                        subjectArr.add(subject); 
						                    }
										 Utility.shareInstance().saveObject(Utility.shareInstance().resSubjectListFile(), subjectArr);
										 
										 HashMap<String, Long> poolDateMap = new HashMap<String, Long>();
										 HashMap<String, Long> modelDateMap = new HashMap<String, Long>();
										 HashMap<String, Long> searchDateMap = new HashMap<String, Long>();
										 final String poolDatePath = Utility.shareInstance().resRootDir()+VerifyActivity.this.getString(R.string.SPPoolDateDict);
										 final String modelDatePath = Utility.shareInstance().resRootDir()+VerifyActivity.this.getString(R.string.SPPoolDateModelDict);
										 final String searchDatePath = Utility.shareInstance().resRootDir()+VerifyActivity.this.getString(R.string.SPPoolDateSearchDict);
										 for (int i=0; i<subjectArr.size(); i++) {
											 Subject subject = subjectArr.get(i);
											 for (int j = 1; j < 9; j++) {
												 String poolID = subject.getSubjectid() + "x" + String.valueOf(j);
												 editor.putString(poolID, "0");
												 poolDateMap.put(poolID, new Long(100));
												 searchDateMap.put(poolID, new Long(100));
											}
										 }
										 editor.commit();
										 Log.v("sp", "dateMap:"+poolDateMap);
										 Utility.shareInstance().saveObject(poolDatePath, poolDateMap);
										 Utility.shareInstance().saveObject(modelDatePath, modelDateMap);
										 Utility.shareInstance().saveObject(searchDatePath, searchDateMap);
										
										Intent intent = new Intent(VerifyActivity.this, MainActivity.class);    	
										VerifyActivity.this.startActivity(intent);
									} catch (JSONException e) {
										Log.e("sp", "" + Log.getStackTraceString(e));
									}
								}
			                }  
			                
							public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
								Utility.shareInstance().hideWaitingHUD();
							}
			                  
			            });  
					} catch (Exception e) {
						Utility.shareInstance().hideWaitingHUD();
						Log.e("sp", "" + Log.getStackTraceString(e));
						Toast.makeText(VerifyActivity.this, "登录失败", Toast.LENGTH_LONG).show(); 
					}
				}
			}
		});
	}
	
	private AlertDialog.Builder setNegativeButton(
			AlertDialog.Builder builder, int flag)
	{
		if (flag == 102) {
			return builder.setNegativeButton(getString(R.string.wait), new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					return;
				}
			});
		}
		else {
			return builder.setNegativeButton(getString(R.string.cancel), new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					return;
				}
			});
		}
	}
}
