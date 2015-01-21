package com.switchpool.home;

import com.xiaoshuye.switchpool.R;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

@SuppressLint("ResourceAsColor")
public class MainActivity extends FragmentActivity implements OnClickListener {
	
	private HomeFragment homeFragment;
	private HomeFragment newsFragment;
	private HomeFragment settingFragment;
	private HomeFragment moreFragment;
	
	private RelativeLayout flayout;
	
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

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        fManager = getSupportFragmentManager();
        
        homeButton = (RadioButton)findViewById(R.id.radio_home_home);
        newsButton = (RadioButton)findViewById(R.id.radio_home_news);
        settingButton = (RadioButton)findViewById(R.id.radio_home_setting);
        moreButton = (RadioButton)findViewById(R.id.radio_home_more);
        
        homeButton.setOnClickListener(this);
        newsButton.setOnClickListener(this);
        settingButton.setOnClickListener(this);
        moreButton.setOnClickListener(this);
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
            homeButton.setTextColor(R.color.tab_highlight_color);
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
            newsButton.setTextColor(R.color.tab_highlight_color);
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
            settingButton.setTextColor(R.color.tab_highlight_color);
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
             moreButton.setTextColor(R.color.tab_highlight_color);
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
}
