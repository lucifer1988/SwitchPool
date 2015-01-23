package com.switchpool.login;

import com.xiaoshuye.switchpool.R;
import com.switchpool.home.MainActivity;
import com.switchpool.model.User;
import com.switchpool.utility.Utility;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.*;
import org.apache.http.Header; 

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler; 
import com.loopj.android.http.RequestParams; 

public class LoginActivity extends Activity {
	
	private EditText et_name, et_pass;
	private String cache_user_filenameString;
	private LoginActivity ctx;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		ctx = this;
		
		et_name = (EditText) findViewById(R.id.editText_login_username);
		et_pass = (EditText) findViewById(R.id.editText_login_password);
		cache_user_filenameString = this.getString(R.string.ser_user);
	}
	
	public void login(View sourceView) {
        String userName = et_name.getText().toString();// 用户名  
        String userPass = et_pass.getText().toString();// 用户密码  
		
        //判断用户名和密码是否为空  
        if (TextUtils.isEmpty(userName.trim())  
                || TextUtils.isEmpty(userPass.trim())) {  
            Toast.makeText(this, "用户名或者密码不能为空", Toast.LENGTH_LONG).show();  
        } else {  
            // 发送请求给服务器  
            //调用：loginByAsyncHttpClientPost(userName, userPass);  
            loginPost(userName, userPass);  
        }  
	}
	
	public void more(View sourceView) {
		Intent intent = new Intent(this, MainActivity.class);    	
		this.startActivity(intent);
//		User loginUser = (User)Utility.shareInstance().getObject(cache_user_filenameString);
//		Toast.makeText(this, "用户名"+loginUser.getCellphone(), Toast.LENGTH_LONG).show();
	}
	
	public void loginPost(String userName, String userPass) { 
		AsyncHttpClient client = new AsyncHttpClient();
		String url = new String(this.getString(R.string.host) + "login/index");
		Log.v("sp", "" + url);
		
		final User user = new User();
		user.setCellphone(userName);
		user.setPassword(userPass);
		user.setBrand(null);
		user.setBuglog(true);
		user.setChannel(true);
		user.setInreg(true);
		user.setIp(null);
		user.setMod(null);
		user.setOs(null);
		user.setOsver(null);
		user.setPrefix(null);
		user.setToken(null);
		user.setTopic(true);
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
		params.put("buglog", user.getBuglog());
		params.put("channel", user.getChannel());
		params.put("inreg", user.getInreg());
		params.put("topic", user.getTopic());
		params.put("token", user.getToken());
		
		Log.v("sp", "" + params);
		
		
		try {  
			client.post(url, params, new JsonHttpResponseHandler() {  

                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {   
                	Log.v("sp", "" + jsonObject); 
                	if (statusCode == 200) {
                		try {
							user.setUid(jsonObject.getString("uid"));
							user.setToken(jsonObject.getString("token"));
							user.setBuglog(jsonObject.getBoolean("buglog"));
							user.setChannel(jsonObject.getBoolean("channel"));
							user.setInreg(jsonObject.getBoolean("inreg"));
							user.setTopic(jsonObject.getBoolean("topic"));  
							
							Utility.shareInstance().saveObject(cache_user_filenameString, user);
							
							Intent intent = new Intent(ctx, MainActivity.class);    	
							ctx.startActivity(intent);
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
	
}
