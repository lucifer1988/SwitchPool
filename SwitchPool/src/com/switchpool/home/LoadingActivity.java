package com.switchpool.home;

import com.switchpool.login.LoginActivity;
import com.xiaoshuye.switchpool.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoadingActivity extends Activity {

	public LoadingActivity() {
		// TODO Auto-generated constructor stub
	}
	
	@Override	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		
		new Handler().postDelayed(new Runnable() {
            public void run() {
                /* Create an Intent that will start the Main WordPress Activity. */
                Intent loadingIntent = new Intent(LoadingActivity.this, LoginActivity.class);
                LoadingActivity.this.startActivity(loadingIntent);
                LoadingActivity.this.finish();
            }
        }, 2000);
		
	}
	

}
