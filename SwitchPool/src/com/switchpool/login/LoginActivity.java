package com.switchpool.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.xiaoshuye.switchpool.R;
import com.switchpool.home.MainActivity;
import com.switchpool.model.Subject;
import com.switchpool.model.User;
import com.switchpool.utility.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.*;
import org.apache.http.Header; 

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler; 
import com.loopj.android.http.RequestParams; 

public class LoginActivity extends Activity {
	
	private EditText uid_name, uid_passwd, phone_name, phone_passwd;
	private LoginActivity ctx;
	
	private Button uidTabBtn, phoneTabBtn;
	private RelativeLayout phoneBgLayout;
	private LinearLayout uidBgLayout;
	
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		ctx = this;
		
		preferences = getSharedPreferences("switchpool", 0x0001);
		editor = preferences.edit();
		if (preferences.getLong("SPQueryGap", 0) == 0) {
			editor.putLong(getString(R.string.SPQueryGap), 0);
			editor.commit();
		}
		
		uid_name = (EditText) findViewById(R.id.editText_login_username);
		uid_passwd = (EditText) findViewById(R.id.editText_login_password);
		phone_name = (EditText) findViewById(R.id.editText_login_phoneNumber);
		phone_passwd = (EditText) findViewById(R.id.editText1_login_passsword);
		uidTabBtn = (Button)findViewById(R.id.button_tab_uidlogin);
		phoneTabBtn = (Button)findViewById(R.id.button_tab_phonelogin);
		phoneBgLayout = (RelativeLayout)findViewById(R.id.relativeLayout_login_field);
		uidBgLayout = (LinearLayout)findViewById(R.id.linearLayout_login_field);
		
	}
	
	public void login(View sourceView) { 
        if (uidBgLayout.getVisibility() == View.VISIBLE) {
            String userName = uid_name.getText().toString();// 用户名  
            String userPass = uid_passwd.getText().toString();// 用户密码 
            //判断用户名和密码是否为空  
            if (TextUtils.isEmpty(userName.trim())  
                    || TextUtils.isEmpty(userPass.trim())) {  
                Toast.makeText(this, "用户号或者密码不能为空", Toast.LENGTH_LONG).show();  
            } else {    
                loginPostForUid(userName, userPass);
            } 
		} 
        else {
            String userName = phone_name.getText().toString();// 用户名  
            String userPass = phone_passwd.getText().toString();// 用户密码 
            //判断用户名和密码是否为空  
            if (TextUtils.isEmpty(userName.trim())  
                    || TextUtils.isEmpty(userPass.trim())) {  
                Toast.makeText(this, "手机号或者密码不能为空", Toast.LENGTH_LONG).show();  
            } else {    
                loginPost(userName, userPass);  
            } 
		}
	}
	
	public void more(View sourceView) {
		Intent intent = new Intent(this, LoginMoreActivity.class);    	
		this.startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	public void tab(View sourceView) {
		if (sourceView.getId() == R.id.button_tab_uidlogin) {
			uidTabBtn.setTextColor(this.getResources().getColor(R.color.lightgrey_hearder));
			phoneTabBtn.setTextColor(this.getResources().getColor(R.color.white));
			uidBgLayout.setVisibility(View.VISIBLE);
			phoneBgLayout.setVisibility(View.INVISIBLE);
		}
		else {
			uidTabBtn.setTextColor(this.getResources().getColor(R.color.white));
			phoneTabBtn.setTextColor(this.getResources().getColor(R.color.lightgrey_hearder));
			uidBgLayout.setVisibility(View.INVISIBLE);
			phoneBgLayout.setVisibility(View.VISIBLE);
		}
	}
	
	public void loginPost(String userName, String userPass) { 
		
		if (Utility.shareInstance().isNetworkAvailable(this)) {
			Utility.shareInstance().showWaitingHUD(this);
			
			AsyncHttpClient client = new AsyncHttpClient();
			String url = new String(this.getString(R.string.host) + "login/index");
			Log.v("sp", "" + url);
			
			final User user = new User();
			user.setCellphone(userName);
			user.setPassword(userPass);
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
			params.put("cellphone", userName);  
			params.put("passwd", userPass); 
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
	                	Utility.shareInstance().hideWaitingHUD();
	                	Log.v("sp", "" + jsonObject); 
	                	if (statusCode == 200) {
	                		try {
								user.setUid(jsonObject.getString("uid"));
								user.setToken(jsonObject.getString("token"));
								user.setBuglog(jsonObject.getInt("buglog"));
								user.setChannel(jsonObject.getInt("channel"));
								user.setInreg(jsonObject.getInt("inreg"));
								user.setTopic(jsonObject.getInt("topic"));  
								
								editor.putLong(getString(R.string.SPQueryGap), (long)jsonObject.getInt("querygap"));
								
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
				                        subject.setHasRight(subjectJsonObject.getInt("hasRight"));
				                        subjectArr.add(subject); 
				                    }
								 Utility.shareInstance().saveObject(Utility.shareInstance().resSubjectListFile(), subjectArr);
								 
								 HashMap<String, Long> poolDateMap = new HashMap<String, Long>();
								 HashMap<String, Long> modelDateMap = new HashMap<String, Long>();
								 HashMap<String, Long> searchDateMap = new HashMap<String, Long>();
								 final String poolDatePath = Utility.shareInstance().resRootDir()+ctx.getString(R.string.SPPoolDateDict);
								 final String modelDatePath = Utility.shareInstance().resRootDir()+ctx.getString(R.string.SPPoolDateModelDict);
								 final String searchDatePath = Utility.shareInstance().resRootDir()+ctx.getString(R.string.SPPoolDateSearchDict);
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
								
								Intent intent = new Intent(ctx, MainActivity.class);    	
								ctx.startActivity(intent);
							} catch (JSONException e) {
								Log.e("sp", "" + Log.getStackTraceString(e));
							}
						}
	                }  
	                
	                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
						ctx.hideKeyboard();
	                	Utility.shareInstance().hideWaitingHUD();
						Log.e("onFailureonResponse", responseString.toString());
						Toast.makeText(ctx, "登录失败", Toast.LENGTH_LONG).show();
	                } 
	            });  
			} catch (Exception e) {
				ctx.hideKeyboard();
				Utility.shareInstance().hideWaitingHUD();
				Log.e("sp", "" + Log.getStackTraceString(e));
				Toast.makeText(this, "登录失败", Toast.LENGTH_LONG).show(); 
			}
		}
		else {
			ctx.hideKeyboard();
			Toast.makeText(this, "手机网络已关闭，无法登录", Toast.LENGTH_LONG).show(); 
		}
		

    }
	
	public void loginPostForUid(String userName, String userPass) { 
		if (Utility.shareInstance().isNetworkAvailable(this)) {
			Utility.shareInstance().showWaitingHUD(this);
			
			AsyncHttpClient client = new AsyncHttpClient();
			String url = new String(this.getString(R.string.host) + "login/index");
			Log.v("sp", "" + url);
			
			final User user = new User();
			user.setCellphone("0");
			user.setPassword(userPass);
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
			user.setUid(userName);
			user.setVender(null);
			user.setVer(null);
			
			RequestParams params = new RequestParams();  
			params.put("cellphone", "0");  
			params.put("passwd", userPass); 
			params.put("uid", userName);
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
	                	Utility.shareInstance().hideWaitingHUD();
	                	Log.v("sp", "" + jsonObject); 
	                	if (statusCode == 200) {
	                		try {
	                			user.setCellphone(jsonObject.getString("phone"));
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
				                        subject.setHasRight(subjectJsonObject.getInt("hasRight"));
				                        subjectArr.add(subject); 
				                    }
								 Utility.shareInstance().saveObject(Utility.shareInstance().resSubjectListFile(), subjectArr);
								 
								 HashMap<String, Long> poolDateMap = new HashMap<String, Long>();
								 HashMap<String, Long> modelDateMap = new HashMap<String, Long>();
								 HashMap<String, Long> searchDateMap = new HashMap<String, Long>();
								 final String poolDatePath = Utility.shareInstance().resRootDir()+ctx.getString(R.string.SPPoolDateDict);
								 final String modelDatePath = Utility.shareInstance().resRootDir()+ctx.getString(R.string.SPPoolDateModelDict);
								 final String searchDatePath = Utility.shareInstance().resRootDir()+ctx.getString(R.string.SPPoolDateSearchDict);
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
								
								Intent intent = new Intent(ctx, MainActivity.class);    	
								ctx.startActivity(intent);
							} catch (JSONException e) {
								Log.e("sp", "" + Log.getStackTraceString(e));
							}
						}
	                }  
	                
	                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
						ctx.hideKeyboard();
	                	Utility.shareInstance().hideWaitingHUD();
						Log.e("onFailureonResponse", responseString.toString());
						Toast.makeText(ctx, "登录失败", Toast.LENGTH_LONG).show(); 
	                } 
	                  
	            });  
			} catch (Exception e) {
				ctx.hideKeyboard();
				Utility.shareInstance().hideWaitingHUD();
				Log.e("sp", "" + Log.getStackTraceString(e));
				Toast.makeText(this, "登录失败", Toast.LENGTH_LONG).show(); 
			}
		}
		else {
			ctx.hideKeyboard();
			Toast.makeText(this, "手机网络已关闭，无法登录", Toast.LENGTH_LONG).show(); 
		}
    }
	
	private void hideKeyboard() {
		InputMethodManager imm =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);   
		if(imm != null) {   
			imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),    
		                       0);   
		} 
	}
}
