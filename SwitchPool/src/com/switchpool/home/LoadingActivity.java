package com.switchpool.home;

import com.switchpool.login.LoginActivity;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoadingActivity extends Activity {

	private String cache_user_filenameString;
	public LoadingActivity() {
		// TODO Auto-generated constructor stub
	}
		
	@Override	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		cache_user_filenameString = this.getString(R.string.ser_user);
		
		new Handler().postDelayed(new Runnable() {
            public void run() {
                /* Create an Intent that will start the Main WordPress Activity. */
            	Intent loadingIntent=new Intent();
            	if(Utility.shareInstance().getObject(cache_user_filenameString) == null) {
            		loadingIntent.setClass(LoadingActivity.this, LoginActivity.class);
            	}
            	else {
            		loadingIntent.putExtra("isFromLoadingActivity", "true");
            		loadingIntent.setClass(LoadingActivity.this, MainActivity.class);
            	}            	
                LoadingActivity.this.startActivity(loadingIntent);
                LoadingActivity.this.finish();
            }
        }, 2000);
		
	}
	

}
