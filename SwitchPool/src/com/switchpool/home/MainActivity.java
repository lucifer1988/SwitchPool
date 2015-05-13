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
import com.switchpool.model.User;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.Toast;

@SuppressLint("ResourceAsColor")
public class MainActivity extends FragmentActivity implements OnClickListener {
	
	private MainActivity ctx;
	
	private HomeFragment homeFragment;
	private HomeFragment newsFragment;
	private HomeFragment settingFragment;
	private HomeFragment moreFragment;
	
	private RadioButton homeButton;
	private RadioButton newsButton;
	private RadioButton settingButton;
	private RadioButton moreButton;
	
	int[] normalTabItemIds = new int[] {
			R.drawable.tabbar_home_normal , R.drawable.tabbar_news_normal , 
			R.drawable.tabbar_setting_normal , R.drawable.tabbar_more_normal
	};
	
	int[] highlightTabItemIds = new int[] {
			R.drawable.tabbar_home_highlight , R.drawable.tabbar_news_highlight , 
			R.drawable.tabbar_setting_highlight , R.drawable.tabbar_more_highlight
	};
	
	FragmentManager fManager;
	
	public static Boolean isLogin = false;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        fManager = getSupportFragmentManager();
        
        ctx = this;
        
        homeButton = (RadioButton)findViewById(R.id.radio_home_home);
        newsButton = (RadioButton)findViewById(R.id.radio_home_news);
        settingButton = (RadioButton)findViewById(R.id.radio_home_setting);
        moreButton = (RadioButton)findViewById(R.id.radio_home_more);
        
        homeButton.setOnClickListener(this);
        newsButton.setOnClickListener(this);
        settingButton.setOnClickListener(this);
        moreButton.setOnClickListener(this);
        
        initialize();
    }
	 
	@Override
	protected void onStart() {
		super.onStart();
		if (isLogin) {
			return ;
		}
		Intent intent = getIntent();
		if (intent != null && intent.getStringExtra("isFromLoadingActivity") != null) {
	        Log.v("sp", ""+intent.getStringExtra("isFromLoadingActivity"));
	        if (intent.getStringExtra("isFromLoadingActivity").equals("true")) {
	        	
	        	if (Utility.shareInstance().isNetworkAvailable(this)) {
	        		Utility.shareInstance().showWaitingHUD(this);
		    		AsyncHttpClient client = new AsyncHttpClient();
		    		String url = new String(this.getString(R.string.host) + "login/index");
		    		Log.v("sp", "" + url);
		    		
		    		final User user = (User) Utility.shareInstance().getObject(Utility.shareInstance().userInfoFile());
		    		
		    		RequestParams params = new RequestParams();  
		    		params.put("cellphone", user.cellphone);  
		    		params.put("passwd", user.password); 
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
		    							 Log.v("sp", "" + Utility.shareInstance().resSubjectListFile());
		    							 Utility.shareInstance().saveObject(Utility.shareInstance().resSubjectListFile(), subjectArr);
		    							 ctx.homeFragment.refreshHeader();
		    						} catch (JSONException e) {
		    							Log.e("sp", "" + Log.getStackTraceString(e));
		    							ctx.homeFragment.refreshHeader();
		    						}
		    					}
		                    }  
		                    
							public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
								Utility.shareInstance().hideWaitingHUD();
								ctx.homeFragment.refreshHeader();
							}
		                });  
		    		} catch (Exception e) {
		    			Utility.shareInstance().hideWaitingHUD();
		    			Log.e("sp", "" + Log.getStackTraceString(e));
		    			Toast.makeText(this, "登录失败", Toast.LENGTH_LONG).show();
		    			ctx.homeFragment.refreshHeader();
		    		}
				}
	        	else {
	        		Toast.makeText(this, "登录失败", Toast.LENGTH_LONG).show();
	        		ctx.homeFragment.refreshHeader();
				}
			}
	        else {
	        	ctx.homeFragment.refreshHeader();
			}
		}
		else {
			ctx.homeFragment.refreshHeader();
		}
		isLogin = true;
	}
	
	private void initialize() {
    	Drawable highlight_drawable0 = this.getResources().getDrawable(highlightTabItemIds[0]); 
        homeButton.setCompoundDrawablesWithIntrinsicBounds(null, highlight_drawable0, null, null);  
        homeButton.setTextColor(this.getResources().getColor(R.color.tab_highlight_color));
        homeFragment = new HomeFragment(); 
        fManager.beginTransaction().add(R.id.relativeLayout_home_container, homeFragment).commit(); 
	}
	
	@Override
	public void onClick(View view) {
		 switch (view.getId()) {
	        case R.id.radio_home_home:
	            setChioceItem(0);
	            break;
	        case R.id.radio_home_news:
	            setChioceItem(1);
	            break;
	        case R.id.radio_home_setting:
	            setChioceItem(2);
	            break;
	        case R.id.radio_home_more:
	            setChioceItem(3);
	            break;
	        default:
	            break;
	        }
	}

    //定义一个选中一个item后的处理
    @SuppressLint("ResourceAsColor")
	public void setChioceItem(int index)
    {
        //重置选项+隐藏所有Fragment
        FragmentTransaction transaction = fManager.beginTransaction();  
        clearChioce();
        hideFragments(transaction);
        switch (index) {
        case 0:
        	Drawable highlight_drawable0 = this.getResources().getDrawable(highlightTabItemIds[0]); 
            homeButton.setCompoundDrawablesWithIntrinsicBounds(null, highlight_drawable0, null, null);  
            homeButton.setTextColor(this.getResources().getColor(R.color.tab_highlight_color));
            if (homeFragment == null) {  
                // 如果homeFragment为空，则创建一个并添加到界面上  
                homeFragment = new HomeFragment();  
                transaction.add(R.id.relativeLayout_home_container, homeFragment);  
            } else {  
                // 如果MessageFragment不为空，则直接将它显示出来  
                transaction.show(homeFragment);  
            }  
            break;  
 
        case 1:
        	Drawable highlight_drawable1 = this.getResources().getDrawable(highlightTabItemIds[1]); 
            newsButton.setCompoundDrawablesWithIntrinsicBounds(null, highlight_drawable1, null, null);  
            newsButton.setTextColor(this.getResources().getColor(R.color.tab_highlight_color));
            if (newsFragment == null) {   
            	newsFragment = new HomeFragment();  
                transaction.add(R.id.relativeLayout_home_container, newsFragment);  
            } else {    
                transaction.show(newsFragment);  
            }  
            break;      
         
         case 2:
         	Drawable highlight_drawable2 = this.getResources().getDrawable(highlightTabItemIds[2]); 
            settingButton.setCompoundDrawablesWithIntrinsicBounds(null, highlight_drawable2, null, null);  
            settingButton.setTextColor(this.getResources().getColor(R.color.tab_highlight_color));
            if (settingFragment == null) {   
            	settingFragment = new HomeFragment();  
                transaction.add(R.id.relativeLayout_home_container, settingFragment);  
            } else {    
                transaction.show(settingFragment);  
            }  
            break; 
            
         case 3:
          	Drawable highlight_drawable3 = this.getResources().getDrawable(highlightTabItemIds[3]); 
             moreButton.setCompoundDrawablesWithIntrinsicBounds(null, highlight_drawable3, null, null);  
             moreButton.setTextColor(this.getResources().getColor(R.color.tab_highlight_color));
             if (moreFragment == null) {   
            	 moreFragment = new HomeFragment();  
                 transaction.add(R.id.relativeLayout_home_container, moreFragment);  
             } else {    
                 transaction.show(moreFragment);  
             }  
             break;
        }
        transaction.commit();
    }
    
    //隐藏所有的Fragment,避免fragment混乱
    private void hideFragments(FragmentTransaction transaction) {  
        if (homeFragment != null) {  
            transaction.hide(homeFragment);  
        }  
        if (newsFragment != null) {  
            transaction.hide(newsFragment);  
        }  
        if (settingFragment != null) {  
            transaction.hide(settingFragment);  
        }  
        if (moreFragment != null) {  
            transaction.hide(moreFragment);  
        } 
    } 
    
    //定义一个重置所有选项的方法
    public void clearChioce()
    {
    	Drawable normal_drawable0 = this.getResources().getDrawable(normalTabItemIds[0]); 
        homeButton.setCompoundDrawablesWithIntrinsicBounds(null, normal_drawable0, null, null);
        homeButton.setTextColor(this.getResources().getColor(R.color.tab_normal_color));
        
    	Drawable normal_drawable1 = this.getResources().getDrawable(normalTabItemIds[1]); 
        newsButton.setCompoundDrawablesWithIntrinsicBounds(null, normal_drawable1, null, null);  
        newsButton.setTextColor(this.getResources().getColor(R.color.tab_normal_color));
        
    	Drawable normal_drawable2 = this.getResources().getDrawable(normalTabItemIds[2]); 
        settingButton.setCompoundDrawablesWithIntrinsicBounds(null, normal_drawable2, null, null);  
        settingButton.setTextColor(this.getResources().getColor(R.color.tab_normal_color));
        
    	Drawable normal_drawable3 = this.getResources().getDrawable(normalTabItemIds[3]); 
        moreButton.setCompoundDrawablesWithIntrinsicBounds(null, normal_drawable3, null, null);  
        moreButton.setTextColor(this.getResources().getColor(R.color.tab_normal_color));
    }
    
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	 }
}
