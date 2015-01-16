package com.switchpool.login;

import java.io.UnsupportedEncodingException;

import com.switchpool.network.HTTPRequestManager;
import com.xiaoshuye.switchpool.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.*;

import com.loopj.android.http.JsonHttpResponseHandler; 
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
	
	public void loginPost(String userName, String userPass) { 
		try {
			RequestParams params = new RequestParams();  
			params.put("username", userName); // 设置请求的参数名和参数值  
			params.put("userpass", userPass);// 设置请求的参数名和参数 
			
			HTTPRequestManager.post("login/index", params, new JsonHttpResponseHandler() {
			    public void onSuccess(JSONObject jsonObject) {
			    	
			    }  
			});
		} catch (Exception e) {
			e.printStackTrace(); 
		}
        
    }  
}
