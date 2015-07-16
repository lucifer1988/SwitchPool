package com.switchpool.home;

import java.util.ArrayList;
import java.util.List;

import com.switchpool.detail.DetailActivity;
import com.switchpool.detail.DetailActivity.DeatilType;
import com.switchpool.model.Item;
import com.switchpool.utility.NoContnetFragment;
import com.switchpool.utility.ToolBar;
import com.switchpool.utility.ToolBarCallBack;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils.TruncateAt;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.OnGestureListener;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;

public class SecListActivity extends FragmentActivity implements OnGestureListener {

	public SecListActivity() {
	}
	
	private String poolId;
	private String subjectId;
	private String poolName;
	
	List<Item> secListItemArr;
	private  ExpandableListView  secExpandableListView;
	private  ExpandableListViewaAdapter adapter;
	
	FragmentManager fManager;
	NoContnetFragment ncFragment;
	
	GestureDetector mGestureDetector; 
	
	@Override	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seclist);
		
		Item secItem = new Item();
		fManager = getSupportFragmentManager();
		
		ToolBar toolBar = (ToolBar)getSupportFragmentManager().findFragmentById(R.id.toplist_toolbar);
		toolBar.button2.setImageResource(R.drawable.toolbar_tag);
		toolBar.setCallBack(new ToolBarCallBack() {
			
			@Override
			public void tapButton6() {
				Item item = Utility.shareInstance().secSelectItem(subjectId, poolId, SecListActivity.this);
				if (item != null) {
	            	Intent intent=new Intent();
	            	intent.setClass(SecListActivity.this, DetailActivity.class);
	            	Bundle bundle = new Bundle();
	            	bundle.putSerializable("item", item);
	            	bundle.putSerializable("type", DeatilType.DeatilTypeOrigin);
	            	intent.putExtras(bundle);
	            	intent.putExtra("poolId", poolId);
	            	intent.putExtra("subjectId", subjectId);
	            	intent.putExtra("poolName", poolName);
	            	startActivityForResult(intent, 0); 
	            	overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				}
			}
			
			@Override
			public void tapButton5() {
//				if (DetailActivity.staticMusicPlayer != null && DetailActivity.staticMusicPlayer.player.isPlaying()) {
//					String curSubjectid = DetailActivity.staticMusicPlayer.curSubjectid();
//					String curPoolid = DetailActivity.staticMusicPlayer.curPoolid();
//					String curItemid = DetailActivity.staticMusicPlayer.curItemid();
//					
//	            	Intent intent=new Intent();
//	            	intent.setClass(SecListActivity.this, DetailActivity.class);
//	            	Bundle bundle = new Bundle();
//	            	bundle.putSerializable("item", Utility.shareInstance().findItem(curSubjectid, curPoolid, curItemid, SecListActivity.this));
//	            	bundle.putSerializable("type", DeatilType.DeatilTypeAudio);
//	            	intent.putExtras(bundle);
//	            	intent.putExtra("poolId", curPoolid);
//	            	intent.putExtra("subjectId", curSubjectid);
//	            	intent.putExtra("poolName", poolName);
//	            	startActivity(intent); 
//	            	overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//				}
			}
			
			@Override
			public void tapButton4() {
//				Intent onItemClickIntent = new Intent();
//				onItemClickIntent.putExtra("poolId", poolId);
//				onItemClickIntent.putExtra("subjectId", subjectId);
//				onItemClickIntent.putExtra("poolName", poolName);
//				onItemClickIntent.setClass(SecListActivity.this, TopListActivity.class);
//				startActivity(onItemClickIntent);
//				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
			
			@Override
			public void tapButton3() {
//				Intent onItemClickIntent = new Intent();
//				onItemClickIntent.putExtra("subjectId", subjectId);
//				onItemClickIntent.setClass(SecListActivity.this, SearchActivity.class);
//				startActivity(onItemClickIntent);
//				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
			
			@Override
			public void tapButton2() {
//	            Intent myIntent = new Intent();
//	            myIntent.setClass(SecListActivity.this, MainActivity.class);
//	            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//	            startActivity(myIntent);
//	            SecListActivity.this.finish();
//	            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
				finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			}
			
			@Override
			public void tapButton1() {
				finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//				Intent onItemClickIntent = new Intent();
//				onItemClickIntent.putExtra("poolId", poolId);
//				onItemClickIntent.putExtra("subjectId", subjectId);
//				onItemClickIntent.putExtra("poolName", poolName);
//				onItemClickIntent.setClass(SecListActivity.this, TopListActivity.class);
//				startActivity(onItemClickIntent);
//				finish();
//				overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
			}
		});
		
        /*初始化界面的list目录*/
		secExpandableListView = (ExpandableListView)findViewById(R.id.expandableListView_seclist_con);
		secExpandableListView.setGroupIndicator(null);
		
		Intent intent = this.getIntent(); 
		secItem=(Item)intent.getSerializableExtra("item");
		poolId=intent.getStringExtra("poolId");
		subjectId=intent.getStringExtra("subjectId");
		poolName = intent.getStringExtra("poolName");
		
		TextView textView = (TextView)findViewById(R.id.textView_seclist_nav);
		textView.setText(getString(R.string.seclist_nav_title, poolName, secItem.getOrder()));
		
		if(secItem == null || secItem.getItemArr().size() == 0) {
			showNoContent();
		}
		else {
			secListItemArr = new ArrayList<Item>(secItem.getItemArr()); 
			initialTrack();
			adapter = new ExpandableListViewaAdapter(SecListActivity.this);
			secExpandableListView.setAdapter(adapter);
			expendAllGroup();
		}
		
		//设置item点击的监听器
		secExpandableListView.setOnChildClickListener(new OnChildClickListener() {
			
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
            	Item item = adapter.getChild(groupPosition, childPosition);
            	Utility.shareInstance().setSecSelectItem(subjectId, poolId, item, SecListActivity.this);
            	
            	Intent intent=new Intent();
            	intent.setClass(SecListActivity.this, DetailActivity.class);
            	Bundle bundle = new Bundle();
            	bundle.putSerializable("item", item);
            	bundle.putSerializable("type", DeatilType.DeatilTypeOrigin);
            	intent.putExtras(bundle);
            	intent.putExtra("poolId", poolId);
            	intent.putExtra("subjectId", subjectId);
            	intent.putExtra("poolName", poolName);
            	startActivityForResult(intent, 0);  
            	overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
        });
		
		mGestureDetector = new GestureDetector(this, (OnGestureListener)this); 
	}
	
	@Override  
	public boolean dispatchTouchEvent(MotionEvent ev) {  
		if (mGestureDetector.onTouchEvent(ev)) {
			return mGestureDetector.onTouchEvent(ev);
		}  
	    return super.dispatchTouchEvent(ev);  
	} 
	
	public boolean onTouch(MotionEvent event) {  
	    return mGestureDetector.onTouchEvent(event);  
	} 
	
	private void initialTrack() {
		if (Utility.shareInstance().secSelectItem(subjectId, poolId, SecListActivity.this) == null) {
			if (secListItemArr.size() > 0) {
				Item item = secListItemArr.get(0);
				if (item.getItemArr().size() > 0) {
					Utility.shareInstance().setSecSelectItem(subjectId, poolId, item.getItemArr().get(0), SecListActivity.this);
				}
			}
		}
	}
	
	private void expendAllGroup() {
		if (secListItemArr.size() > 0) {
			for (int i = 0; i < secListItemArr.size(); i++) {
				secExpandableListView.expandGroup(i);
			}
		}
	}
	
	private void showNoContent() {
        if (ncFragment == null) {  
    		ncFragment = new NoContnetFragment();
    		ncFragment.initialize(getString(R.string.nocontenttip_list));
    		FragmentTransaction transaction = fManager.beginTransaction();
    		transaction.add(R.id.relativeLayout_toplist_nocontent, ncFragment).commit();
        }
        fManager.beginTransaction().show(ncFragment);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		adapter.notifyDataSetChanged();
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
            View resultView = getGenericView(item);
        	Item selectedItem = Utility.shareInstance().secSelectItem(subjectId, poolId, SecListActivity.this);
        	
			if (selectedItem !=null && item.getId().equals(selectedItem.getId())) {
				resultView.setBackgroundResource(R.color.expandableList_hig);
			}
			else {
				resultView.setBackgroundResource(R.color.expandableList_nor);
			} 
			
            return resultView;
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
           Item item=secListItemArr.get(groupPosition);
           View resultView = getGenericView(item);
           resultView.setBackgroundResource(R.color.expandableList_nor);
           
           return resultView;
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

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

    private int verticalMinDistance = 50;
    private int minVelocity         = 50;
    private int horizonMinDistance  = 100;

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity && Math.abs(e1.getY() - e2.getY()) < horizonMinDistance) {
			Item item = Utility.shareInstance().secSelectItem(subjectId, poolId, SecListActivity.this);
			if (item != null) {
            	Intent intent=new Intent();
            	intent.setClass(SecListActivity.this, DetailActivity.class);
            	Bundle bundle = new Bundle();
            	bundle.putSerializable("item", item);
            	bundle.putSerializable("type", DeatilType.DeatilTypeOrigin);
            	intent.putExtras(bundle);
            	intent.putExtra("poolId", poolId);
            	intent.putExtra("subjectId", subjectId);
            	intent.putExtra("poolName", poolName);
            	startActivityForResult(intent, 0); 
            	overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
//          Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();
        } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity && Math.abs(e1.getY() - e2.getY()) < horizonMinDistance) {
//          Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();
			finish();
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        return true;
    }

}
