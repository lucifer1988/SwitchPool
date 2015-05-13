package com.switchpool.login;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private EditText phoneEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		phoneEditText = (EditText)findViewById(R.id.editText_register_phoneNumber);
	}
	
	public void back(View sourceView) {
		finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	
	public void verify(View sourceView) {
        final String userName = phoneEditText.getText().toString();
        //判断是否为空  
        if (TextUtils.isEmpty(userName.trim())) {  
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_LONG).show();  
        } else {    
    		AsyncHttpClient client = new AsyncHttpClient();
    		String url = new String(this.getString(R.string.host) + "sms/code");
    		Log.v("sp", "" + url);
    		
    		RequestParams params = new RequestParams();  
    		params.put("cellphone", userName); 
    		Log.v("sp", "" + params);
    		
    		try {  
    			client.post(url, params, new JsonHttpResponseHandler() {  

                    public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    	Utility.shareInstance().hideWaitingHUD();
                    	Log.v("sp", "" + jsonObject); 
                    	if (statusCode == 200) {
                    		try {
                    			if (jsonObject.getString("flag") != null && jsonObject.getString("flag").equals("0")) {
                        			Intent intent = new Intent(RegisterActivity.this, VerifyActivity.class);    	
                        			intent.putExtra("phone", userName);
                        			intent.putExtra("code", jsonObject.getString("code"));
                        			RegisterActivity.this.startActivity(intent);
                        			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
								}
    						} catch (JSONException e) {
    							Log.e("sp", "" + Log.getStackTraceString(e));
    							Toast.makeText(RegisterActivity.this, "发送验证失败", Toast.LENGTH_LONG).show(); 
    						}
    					}
                    }  
                    
    				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
    					Toast.makeText(RegisterActivity.this, "发送验证失败", Toast.LENGTH_LONG).show(); 
    				}
                      
                });  
    		} catch (Exception e) {
    			Log.e("sp", "" + Log.getStackTraceString(e));
    			Toast.makeText(this, "发送验证失败", Toast.LENGTH_LONG).show(); 
    		}
        } 
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	
}
