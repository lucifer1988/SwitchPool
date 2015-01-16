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
        String userName = et_name.getText().toString();// �û���  
        String userPass = et_pass.getText().toString();// �û�����  
        //�ж��û����������Ƿ�Ϊ��  
        if (TextUtils.isEmpty(userName.trim())  
                || TextUtils.isEmpty(userPass.trim())) {  
            Toast.makeText(this, "�û����������벻��Ϊ��", Toast.LENGTH_LONG).show();  
        } else {  
            // ���������������  
            //���ã�loginByAsyncHttpClientPost(userName, userPass);  
            loginPost(userName, userPass);  
        }  
	}
	
	public void loginPost(String userName, String userPass) { 
		try {
			RequestParams params = new RequestParams();  
			params.put("username", userName); // ��������Ĳ������Ͳ���ֵ  
			params.put("userpass", userPass);// ��������Ĳ������Ͳ��� 
			
			HTTPRequestManager.post("login/index", params, new JsonHttpResponseHandler() {
			    public void onSuccess(JSONObject jsonObject) {
			    	
			    }  
			});
		} catch (Exception e) {
			e.printStackTrace(); 
		}
        
    }  
}
