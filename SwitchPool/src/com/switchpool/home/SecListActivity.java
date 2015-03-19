package com.switchpool.home;

import java.util.ArrayList;
import java.util.List;

import com.switchpool.home.TopListActivity.ExpandableListViewaAdapter;
import com.switchpool.model.Item;
import com.switchpool.utility.ToolBar;
import com.switchpool.utility.ToolBarCallBack;
import com.xiaoshuye.switchpool.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class SecListActivity extends FragmentActivity {

	public SecListActivity() {
		// TODO Auto-generated constructor stub
	}
	
	private SecListActivity ctx;
	
	List<Item> secListItemArr;
	private  ExpandableListView  secExpandableListView;
	private  ExpandableListViewaAdapter adapter;
	
	@Override	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seclist);
		
		Item secItem = new Item();
		ctx = this;
		
		ToolBar toolBar = (ToolBar)getSupportFragmentManager().findFragmentById(R.id.toplist_toolbar);
		toolBar.setCallBack(new ToolBarCallBack() {
			
			@Override
			public void tapButton6() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void tapButton5() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void tapButton4() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void tapButton3() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void tapButton2() {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void tapButton1() {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			}
		});
		
        /*初始化界面的list目录*/
		secExpandableListView = (ExpandableListView)findViewById(R.id.expandableListView_seclist_con);
		secExpandableListView.setGroupIndicator(null);
		
		Intent intent = this.getIntent(); 
		secItem=(Item)intent.getSerializableExtra("item");
		
		if(secItem!=null){
			secListItemArr = new ArrayList<Item>(secItem.getItemArr()); 
			adapter = new ExpandableListViewaAdapter(SecListActivity.this);
			secExpandableListView.setAdapter(adapter);
		}
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	
	class ExpandableListViewaAdapter extends BaseExpandableListAdapter {
        Activity activity;
         public  ExpandableListViewaAdapter(Activity a)  
            {  
                activity = a;  
            }  
       /*-----------------Child */
        @Override
        public Item getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return secListItemArr.get(groupPosition).getItemArr().get(childPosition);
        }
 
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childPosition;
        }
 
        @Override
        public View getChildView(int groupPosition, int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {
             
            Item item =secListItemArr.get(groupPosition).getItemArr().get(childPosition);
             
            return getGenericView(item);
        }
 
        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return secListItemArr.get(groupPosition).getItemArr().size();
        }
       /* ----------------------------Group */
        @Override
        public Item getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return getGroup(groupPosition);
        }
 
        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return secListItemArr.size();
        }
 
        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }
 
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent) {
             
           Item   item=secListItemArr.get(groupPosition);
           return getGenericView(item);
        }
 
        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return false;
        }
 
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) 
        {
            // TODO Auto-generated method stub
            return true;
        }
         
        private View  getGenericView(Item item ) 
        {
        	// Layout parameters for the ExpandableListView    
        	 AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                     ViewGroup.LayoutParams.MATCH_PARENT, 100);
             TextView textView = new TextView(activity);
             textView.setLayoutParams(lp);
             textView.setGravity(Gravity.CENTER_VERTICAL);
             textView.setTextSize(17);
             textView.setTextColor(Color.BLACK);
             textView.setText(item.getCaption());
             textView.setSingleLine(true);
             textView.setEllipsize(TruncateAt.END);
	         if (item.getItemArr() == null) {
	        	 textView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.toplist_childicon), null, null, null);
	        	 textView.setPadding(60, 0, 0, 0);
			 }
	         else {
	        	 textView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.toplist_parenticon), null, null, null);
	        	 textView.setPadding(30, 0, 0, 0);
			 }
             return textView;
         }		
		
    }

}
