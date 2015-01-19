package com.switchpool.login;

import com.xiaoshuye.switchpool.R;
import com.switchpool.model.User;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.*;
import org.apache.http.Header; 

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler; 
import com.loopj.android.http.AsyncHttpResponseHandler; 
import com.loopj.android.http.RequestParams; 

public class LoginActivity extends Activity {
	
	private EditText et_name, et_pass;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		et_name = (EditText) findViewById(R.id.editText_login_username);
		et_pass = (EditText) findViewById(R.id.editText_login_password);
	}
	
	public void login(View sourceView) {
//        String userName = et_name.getText().toString();// 用户名  
//        String userPass = et_pass.getText().toString();// 用户密码  
      String userName = "13501279170";// 用户名  
      String userPass = "3001000";// 用户密码 
		
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
	
	public void loginPost(String userName, String userPass) { 
		AsyncHttpClient client = new AsyncHttpClient();
		String url = new String(this.getString(R.string.host) + "login/index");
		Log.v("switchpool", "loginURL" + url);
		
		User user = new User();
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
		
		Log.v("sp", "params" + params);
		
		client.post(url, params, new AsyncHttpResponseHandler() {  
            /** 
             * 成功处理的方法 
             * statusCode:响应的状态码; headers:相应的头信息 比如 响应的时间，响应的服务器 ; 
             * responseBody:响应内容的字节 
             */  
            @Override  
            public void onSuccess(int statusCode, Header[] headers,  
                    byte[] responseBody) {  
                if (statusCode == 200) {
                	Log.v("sp", "loginresult" + responseBody); 
                }  
            }  
  
            /** 
             * 失败处理的方法 
             * error：响应失败的错误信息封装到这个异常对象中 
             */  
            @Override  
            public void onFailure(int statusCode, Header[] headers,  
                    byte[] responseBody, Throwable error) {  
                error.printStackTrace();// 把错误信息打印出轨迹来
                Log.e("sp", "Exception: " + Log.getStackTraceString(error));
            }  
        });
    }  
}
