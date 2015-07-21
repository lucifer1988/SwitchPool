package com.switchpool.detail;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.switchpool.detail.DetailContentFragment.DetailContentHandler;
import com.switchpool.model.Item;
import com.switchpool.model.Model;
import com.switchpool.model.SPFile;
import com.switchpool.model.User;
import com.switchpool.utility.ImageTools;
import com.switchpool.utility.MusicPlayer;
import com.switchpool.utility.NoContnetFragment;
import com.switchpool.utility.ToolBar;
import com.switchpool.utility.ToolBarCallBack;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import org.apache.http.Header; 
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class DetailActivity extends FragmentActivity implements DetailContentHandler, OnGestureListener  {

	static DetailActivity mContext;
	
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	
	public String poolId;
	public String subjectId;
	public Item item;
//	private String poolName;
	
	private Button summaryButton;
	private Button contentButton;
	private Button noteButton;
	private Button audioButton;
	private Button moreButton;
	private List<Button> topTabBtnArr;
	private int curTabIndex;
	
	public enum DeatilType { 
		DeatilTypeOrigin(0), DeatilTypeAudio(1), DeatilTypeSearch(2); 
	    private final int val; 
	 
	    private DeatilType(int value) { 
	        val = value; 
	    } 
	 
	    public int getValue() { 
	        return this.val; 
	    } 
	} 
	public DeatilType deatilType;
	public Boolean isForPlaying;
	
	//cache path
	private String verPath;
	private String modelCachePath;
	
	//Map
	private HashMap<String, String> resVerMap;
	private HashMap<String, HashMap<String, String>> allVerMap;
	private HashMap<String, Model> modelMap;
	private RequestParams params;
	
	public interface downloadCallBack {  
	   void downloadFinished(SPFile file, Throwable error);  
	} 
	
	private DetailSummaryFragment summaryFragment;
	private DetailContentFragment contentFragment;
	public DetailNoteFragment noteFragment;
	private DetailAudioFragment audioFragment;
	
	FragmentManager fManager;
	AsyncHttpClient downloadClient;
//	RequestHandle downloadRD;
	NoContnetFragment ncFragment;
	
	public MusicPlayer musicPlayer; 
	public static MusicPlayer staticMusicPlayer;
	private Boolean isItemChanged = false; 
	private Boolean isAudioDownloading = false; 
     
    //使用ServiceConnection来监听Service状态的变化  
    private ServiceConnection conn = new ServiceConnection() {  
          
        @Override  
        public void onServiceDisconnected(ComponentName name) {    
        	musicPlayer = null;  
        	staticMusicPlayer = null;
        }  
          
        @Override  
        public void onServiceConnected(ComponentName name, IBinder binder) {  
            //这里我们实例化audioService,通过binder来实现  
        	musicPlayer = ((MusicPlayer.AudioBinder)binder).getService();  
        	staticMusicPlayer = ((MusicPlayer.AudioBinder)binder).getService();
//        	if (deatilType == DeatilType.DeatilTypeAudio) {
        	if (isForPlaying) {
        		audioFragment.reload(null);
			}
        	else if (musicPlayer.player.isPlaying()) {
        		musicPlayer.player.stop();
			}
        }  
    };
    
    GestureDetector mGestureDetector; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		
		preferences = getSharedPreferences("switchpool", 0x0001);
		editor = preferences.edit();
		
		DisplayImageOptions defaultOptions = new DisplayImageOptions
				.Builder()
				.showImageForEmptyUri(null) 
				.showImageOnFail(null) 
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration
				.Builder(getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.discCacheSize(50 * 1024 * 1024)
				.discCacheFileCount(100)
				.writeDebugLogs()
				.build();
		ImageLoader.getInstance().init(config);
		
		mContext = this;
		fManager = getSupportFragmentManager();
		
		Intent intent = this.getIntent(); 
		item = (Item)intent.getSerializableExtra("item");
		poolId = intent.getStringExtra("poolId");
		subjectId = intent.getStringExtra("subjectId");
		deatilType = (DeatilType)intent.getSerializableExtra("type");
		isForPlaying = (Boolean)intent.getBooleanExtra("isForPlaying", false);
//		poolName = intent.getStringExtra("poolName");
		
		String splitArr[] = item.getCaption().split(" ");
		TextView textView = (TextView)findViewById(R.id.textView_detail_nav);
		textView.setText(getString(R.string.detail_nav_title, splitArr[0]));
		
		summaryButton = (Button)findViewById(R.id.button_detail_toptab_summary);
		contentButton = (Button)findViewById(R.id.button_detail_toptab_content);
		noteButton = (Button)findViewById(R.id.button_detail_toptab_note);
		audioButton = (Button)findViewById(R.id.button_detail_toptab_audio);
		moreButton = (Button)findViewById(R.id.button_detail_toptab_more);
		topTabBtnArr = new ArrayList<Button>();
		topTabBtnArr.add(summaryButton);
		topTabBtnArr.add(contentButton);
		topTabBtnArr.add(noteButton);
		topTabBtnArr.add(audioButton);
		topTabBtnArr.add(moreButton);
		
		summaryFragment = (DetailSummaryFragment)fManager.findFragmentById(R.id.detail_fragment_summary);
		contentFragment = (DetailContentFragment)fManager.findFragmentById(R.id.detail_fragment_content);
		noteFragment = (DetailNoteFragment)fManager.findFragmentById(R.id.detail_fragment_note);
		audioFragment = (DetailAudioFragment)fManager.findFragmentById(R.id.detail_fragment_audio);
		Button actionButton = (Button)findViewById(R.id.button_detail_note_action);  
		actionButton.setOnTouchListener((OnTouchListener)noteFragment);
		
		ncFragment = (NoContnetFragment)fManager.findFragmentById(R.id.detail_nocontent);
		ncFragment.initialize(getString(R.string.nocontenttip_detail_content));
		fManager.beginTransaction().hide(ncFragment).commit();
		
		//initial Model Version
		verPath = Utility.shareInstance().cachPoolDir(poolId, subjectId)+poolId;
		modelCachePath = Utility.shareInstance().cachPoolDir(poolId, subjectId)+item.getId();
		
		HashMap<String, HashMap<String, String>> map = (HashMap<String, HashMap<String, String>>)Utility.shareInstance().getObject(verPath);
		if (map == null || map.isEmpty()) {
			resVerMap = new HashMap<String, String>();
			String[] modelTypeStrings = new String[]{"10", "20", "21", "22", "40"};
			for (int i = 0; i < modelTypeStrings.length; i++) {
				resVerMap.put(modelTypeStrings[i], "0");
			}
			allVerMap = new HashMap<String, HashMap<String, String>>();
			allVerMap.put(item.getId(), resVerMap);
			Utility.shareInstance().saveObject(verPath, allVerMap);
		}
		else {
			allVerMap = map;
			HashMap<String, String> tempVerMap = map.get(item.getId());
			if (tempVerMap == null || tempVerMap.isEmpty()) {
				resVerMap = new HashMap<String, String>();
				String[] modelTypeStrings = new String[]{"10", "20", "21", "22", "40"};
				for (int i = 0; i < modelTypeStrings.length; i++) {
					resVerMap.put(modelTypeStrings[i], "0");
				}
				allVerMap.put(item.getId(), resVerMap);
				Utility.shareInstance().saveObject(verPath, allVerMap);
			}
			else {
				resVerMap = tempVerMap;
			}
			Log.v("sp", "resVerMap"+resVerMap);
		}
		
		HashMap<String, Model> tempModelMap = (HashMap<String, Model>)Utility.shareInstance().getObject(modelCachePath);
		if (tempModelMap != null && !tempModelMap.isEmpty()) {
			modelMap = tempModelMap;
		}
		else {
			modelMap = new HashMap<String, Model>();
		}
		
		//request param
		User userInfo = (User)Utility.shareInstance().getObject(Utility.shareInstance().userInfoFile());
		params = new RequestParams();
		params.put("uid", userInfo.getUid());
		params.put("token", userInfo.getToken());
		params.put("subjectid", subjectId);
		params.put("poolid", poolId);
		params.put("itemid", item.getId());
		
		//bottom tool bar
		ToolBar toolBar = (ToolBar)getSupportFragmentManager().findFragmentById(R.id.detail_toolbar);
		if (deatilType == DeatilType.DeatilTypeOrigin) {
			toolBar.button2.setImageResource(R.drawable.toolbar_tag);
		}
		else if (deatilType == DeatilType.DeatilTypeAudio) {
			toolBar.button2.setImageResource(R.drawable.toolbar_home);
		}
		else if (deatilType == DeatilType.DeatilTypeSearch) {
			toolBar.button2.setImageResource(R.drawable.toolbar_search);
		}
		toolBar.setCallBack(new ToolBarCallBack() {
			
			@Override
			public void tapButton6() {
				if (Utility.shareInstance().isFastDoubleClick()) {  
			        return;  
			    }  
				if (curTabIndex < topTabBtnArr.size()) {
					int index = curTabIndex+1;
					Button button = topTabBtnArr.get(index);
					tabTopBar(button);
				}
			}
			
			@Override
			public void tapButton5() {
				
			}
			
			@Override
			public void tapButton4() {
			}
			
			@Override
			public void tapButton3() {
			}
			
			@Override
			public void tapButton2() {
				if (Utility.shareInstance().isFastDoubleClick()) {  
			        return;  
			    } 
				stopDownload();
				
				finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			}
			
			@Override
			public void tapButton1() {
				if (Utility.shareInstance().isFastDoubleClick()) {  
			        return;  
			    } 
				if (curTabIndex > 0) {
					int index = curTabIndex-1;
					Button button = topTabBtnArr.get(index);
					tabTopBar(button);
				}
				else {
					if (!isForPlaying) {
						stopDownload();
						finish();
						overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
					}
				}
			}
		});
		
		if (staticMusicPlayer == null) {
			Intent playIntentntent = new Intent();  
			playIntentntent.setClass(this, MusicPlayer.class); 
		    startService(playIntentntent);  
	        bindService(playIntentntent, conn, Context.BIND_AUTO_CREATE); 
		}
		else {
			Intent playIntentntent = new Intent();  
			playIntentntent.setClass(this, MusicPlayer.class); 
	        bindService(playIntentntent, conn, Context.BIND_AUTO_CREATE);
		}
        
		//动态注册广播接收器  
        msgReceiver = new MsgReceiver();  
        IntentFilter intentFilter = new IntentFilter();  
        intentFilter.addAction("com.xiaoshuye.audioNoteFinished.broadcast");  
        registerReceiver(msgReceiver, intentFilter); 
        
        audioFinishedMsgReceiver = new AudioFinishedMsgReceiver();
        IntentFilter audioFinishedIntentFilter = new IntentFilter();  
        audioFinishedIntentFilter.addAction("com.xiaoshuye.audioFileFinished.broadcast");  
        registerReceiver(audioFinishedMsgReceiver, audioFinishedIntentFilter); 
        
        audioChangeReceiver = new AudioChangeMsgReceiver();
        IntentFilter audioChangeIntentFilter = new IntentFilter();  
        audioChangeIntentFilter.addAction("com.xiaoshuye.audioFileChanged.broadcast");  
        registerReceiver(audioChangeReceiver, audioChangeIntentFilter); 
        
		if (!isForPlaying) {
//			if (musicPlayer != null && musicPlayer.player.isPlaying()) {
//				musicPlayer.player.stop();
//			}
			
			summaryButton.setTextColor(Color.WHITE);
			summaryButton.setBackgroundResource(R.drawable.detailtab_bg_hig);
			Drawable summary_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_summary_hig);
			summaryButton.setCompoundDrawablesWithIntrinsicBounds(null, summary_top_drawable, null, null);
			FragmentTransaction transaction = fManager.beginTransaction();
			transaction.show(summaryFragment);
			transaction.hide(contentFragment);
			transaction.hide(noteFragment);
			transaction.hide(audioFragment);
			transaction.commit();
			requestModel("10", 0);
			curTabIndex = 0;
		}
		else {
			Utility.shareInstance().hideWaitingHUD();
			audioButton.setTextColor(Color.WHITE);
			audioButton.setBackgroundResource(R.drawable.detailtab_bg_hig);
			Drawable audio_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_audio_hig);
			audioButton.setCompoundDrawablesWithIntrinsicBounds(null, audio_top_drawable, null, null);
			FragmentTransaction transaction = fManager.beginTransaction();
			transaction.show(audioFragment);
			transaction.hide(contentFragment);
			transaction.hide(noteFragment);
			transaction.hide(summaryFragment);
			transaction.commit();
			curTabIndex = 3;
		}
		
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
	
	@Override
	public void onBackPressed() {
		stopDownload();
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	
	public void stopDownload() {
		if (downloadClient != null) {
			downloadClient.cancelAllRequests(true);
		}
	}
	
	public void showNoContent() {
        if (ncFragment != null) {  
        	fManager.beginTransaction().show(ncFragment).commit();
        }
	}
	
	public void hideNoContent() {
        if (ncFragment != null) {  
        	fManager.beginTransaction().hide(ncFragment).commit();
        }
	}
	
	public void tabTopBar(View sourceButton) {
		int index = topTabBtnArr.indexOf(sourceButton);
		if (index == curTabIndex) {
			return;
		}
		this.resetTopTab();
		curTabIndex = index;
		switch (sourceButton.getId()) {
		case R.id.button_detail_toptab_summary:{
			summaryButton.setTextColor(Color.WHITE);
			summaryButton.setBackgroundResource(R.drawable.detailtab_bg_hig);
			Drawable summary_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_summary_hig);
			summaryButton.setCompoundDrawablesWithIntrinsicBounds(null, summary_top_drawable, null, null);
			
			FragmentTransaction transaction = fManager.beginTransaction();
			transaction.show(summaryFragment);
			transaction.hide(contentFragment);
			transaction.hide(noteFragment);
			transaction.hide(audioFragment);
			transaction.commit();
			
			if (summaryFragment.getModel() == null || isItemChanged) {
				requestModel("10", index);
				isItemChanged = false;
			}
		}
			break;
		case R.id.button_detail_toptab_content:{
			contentButton.setTextColor(Color.WHITE);
			contentButton.setBackgroundResource(R.drawable.detailtab_bg_hig);
			Drawable content_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_content_hig);
			contentButton.setCompoundDrawablesWithIntrinsicBounds(null, content_top_drawable, null, null);
			
			FragmentTransaction transaction = fManager.beginTransaction();
			transaction.show(contentFragment);
			transaction.hide(summaryFragment);
			transaction.hide(noteFragment);
			transaction.hide(audioFragment);
			transaction.commit();
			
			if (contentFragment.content20Fragment.getModel() == null || isItemChanged) {
				requestModel("20", index);
				isItemChanged = false;
			}
		}
			break;
		case R.id.button_detail_toptab_note:{ 
			noteButton.setTextColor(Color.WHITE);
			noteButton.setBackgroundResource(R.drawable.detailtab_bg_hig);
			Drawable note_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_note_hig);
			noteButton.setCompoundDrawablesWithIntrinsicBounds(null, note_top_drawable, null, null);
			
			FragmentTransaction transaction = fManager.beginTransaction();
			transaction.show(noteFragment);
			transaction.hide(summaryFragment);
			transaction.hide(contentFragment);
			transaction.hide(audioFragment);
			transaction.commit();
			
			hideNoContent();
			
			if (noteFragment.noteTextFragment.getNote() == null || isItemChanged) {
				requestModel("30", index);
				isItemChanged = false;
			}
		}
			break;
		case R.id.button_detail_toptab_audio:{
			audioButton.setTextColor(Color.WHITE);
			audioButton.setBackgroundResource(R.drawable.detailtab_bg_hig);
			Drawable audio_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_audio_hig);
			audioButton.setCompoundDrawablesWithIntrinsicBounds(null, audio_top_drawable, null, null);
			
			FragmentTransaction transaction = fManager.beginTransaction();
			transaction.show(audioFragment);
			transaction.hide(summaryFragment);
			transaction.hide(contentFragment);
			transaction.hide(noteFragment);
			transaction.commit();
			
			hideNoContent();
			
			if ((audioFragment.model == null || isItemChanged) && !isAudioDownloading) {
				requestModel("40", index);
				isItemChanged = false;
			}
			else if (audioFragment.model != null && !this.musicPlayer.isItemAudio) {
				audioFragment.reload(audioFragment.model);
			}
		}
			break;
		case R.id.button_detail_toptab_more:{
			moreButton.setTextColor(Color.WHITE);
			moreButton.setBackgroundResource(R.drawable.detailtab_bg_hig);
			Drawable more_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_more_hig);
			moreButton.setCompoundDrawablesWithIntrinsicBounds(null, more_top_drawable, null, null);
		}
			break;

		default:
			break;
		}
	}
	
	public void resetTopTab() {
		summaryButton.setTextColor(Color.BLACK);
		summaryButton.setBackgroundColor(Color.TRANSPARENT);
		Drawable summary_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_summary_nor);
		summaryButton.setCompoundDrawablesWithIntrinsicBounds(null, summary_top_drawable, null, null);
		
		contentButton.setTextColor(Color.BLACK);
		contentButton.setBackgroundColor(Color.TRANSPARENT);
		Drawable content_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_content_nor);
		contentButton.setCompoundDrawablesWithIntrinsicBounds(null, content_top_drawable, null, null);
		
		noteButton.setTextColor(Color.BLACK);
		noteButton.setBackgroundColor(Color.TRANSPARENT);
		Drawable note_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_note_nor);
		noteButton.setCompoundDrawablesWithIntrinsicBounds(null, note_top_drawable, null, null);
		
		audioButton.setTextColor(Color.BLACK);
		audioButton.setBackgroundColor(Color.TRANSPARENT);
		Drawable audio_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_audio_nor);
		audioButton.setCompoundDrawablesWithIntrinsicBounds(null, audio_top_drawable, null, null);
		
		moreButton.setTextColor(Color.BLACK);
		moreButton.setBackgroundColor(Color.TRANSPARENT);
		Drawable more_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_more_nor);
		moreButton.setCompoundDrawablesWithIntrinsicBounds(null, more_top_drawable, null, null);
	}
	
	public void handleMessage(int index) {
		switch (index) {
		case 0:{
			requestModel("20", 1);
		}
			break;
		case 1:{
			requestModel("21", 1);
		}
			break;
		case 2:{
			requestModel("22", 1);
		}
			break;
		}
	}
	
	private void requestModel(final String modelType, final int index) {
		params.put("modetype", modelType);
		Log.v("sp", "ver"+resVerMap.get(modelType));
		if (resVerMap.get(modelType) == null) {
			resVerMap.put(modelType, "0");
		}
		params.put("localver", resVerMap.get(modelType));
//		params.put("localver", "0");//test
		if (modelType.equals("30")) {
			noteFragment.noteTextFragment.requestNoteText(params);
		}
		else {
			final Model cacheModel = modelMap.get(modelType);
			
			final String datePathString = Utility.shareInstance().resRootDir()+this.getString(R.string.SPPoolDateModelDict);
			Log.v("sp", "datePathString:"+datePathString);
			HashMap<String, Long> dateMap = (HashMap<String, Long>)Utility.shareInstance().getObject(datePathString);
			Log.v("sp", "dateMap:"+dateMap);
			Long lastDate = new Long(100);
			if (dateMap.size() > 0 && dateMap.get(item.getId()+modelType) != null) {
				lastDate = dateMap.get(item.getId()+modelType);
			}
			long gap = preferences.getLong("SPQueryGap", 0);
			
			if (Utility.shareInstance().isNetworkAvailable(this)) {
				
				if (modelType.equals("10") || modelType.equals("20") || modelType.equals("21") || modelType.equals("22")) {
					Utility.shareInstance().showWaitingHUD(this);
				}
				
				final long curDate = System.currentTimeMillis()/1000;
				Log.v("sp", "lastDate:"+lastDate);
				Log.v("sp", "curDate:"+curDate);
				Log.v("sp", "gap:"+gap);
				Log.v("sp", "cacheModel:"+cacheModel);
				if (lastDate != null && curDate-lastDate.longValue() < gap && cacheModel != null) {
					handelModelFiles(index, cacheModel, modelType);
					return;
				}
				
				AsyncHttpClient client = new AsyncHttpClient();
				String url = new String(this.getString(R.string.host) + "model/item");
				Log.v("sp", ""+url);
				Log.v("sp", ""+params);
				try {  
					client.post(url, params, new JsonHttpResponseHandler() {  

		                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {   
		                	Log.v("sp", "" + jsonObject); 
		                	if (statusCode == 200) {
		                		HashMap<String, Long> dateMap = (HashMap<String, Long>)Utility.shareInstance().getObject(datePathString);
		                		dateMap.put(item.getId()+modelType, new Long(curDate));
		                		Utility.shareInstance().saveObject(datePathString, dateMap);
		                		
		                		try {
		                			Model curModel;
		                			String curVer = jsonObject.getString("version");
		                			Log.v("sp", "resVerMap.get(modelType):"+resVerMap.get(modelType));
		                			Log.v("sp", "curVer:"+curVer);
		                			if (resVerMap.get(modelType).equals(curVer)) {
		                				if (cacheModel != null) {
		                					handelModelFiles(index, cacheModel, modelType);
										}
									}
		                			else {
		                				curModel = new Model();
		                				curModel.setItemid(jsonObject.getString("itemid"));
		                				curModel.setModetype(jsonObject.getString("modetype"));
		                				curModel.setVersion(jsonObject.getString("version"));
		                				JSONArray fileJsonArray = jsonObject.getJSONArray("models");
		                				List<SPFile> fileArr = new ArrayList<SPFile>();
		                				for (int i = 0; i < fileJsonArray.length(); i++) {
		                					JSONObject fileObject = (JSONObject) fileJsonArray.opt(i);
		                					SPFile file = new SPFile();
		                					file.setFid(fileObject.getString("fid"));
		                					file.setFtype(fileObject.getString("ftype"));
		                					file.setItemid(fileObject.getString("itemid"));
		                					file.setSeq(fileObject.getInt("seq"));
		                					fileArr.add(file);
										}
		                				curModel.setFileArr(fileArr);
		                				
		                				resVerMap.put(modelType, curVer);
		                				saveModelVer();
		                				handelModelFiles(index, curModel, modelType);
		                			}
								} catch (JSONException e) {
									handelModelFiles(index, cacheModel, modelType);
									Log.e("sp", "" + Log.getStackTraceString(e));
								}
							}
		                	else {
								handelModelFiles(index, cacheModel, modelType);
							}
		                }  
		                
		                public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
							handelModelFiles(index, cacheModel, modelType);
		                }
		                   
		            });  
				} catch (Exception e) {
					handelModelFiles(index, cacheModel, modelType);
					Log.e("sp", "" + Log.getStackTraceString(e));
				}
			}
			else {
				handelModelFiles(index, cacheModel, modelType);
			}
		}
	}
	
	private void handelModelFiles(final int index, final Model model, final String type) {
		if (type.equals("30")) {
		}
		else {
			if (model!=null) {
				final List<SPFile> modelFileArr = model.getFileArr();
				final List<SPFile> resultFileArrFiles = new ArrayList<SPFile>();
				if (!modelFileArr.isEmpty()) {
					modelMap.put(type, model);
					saveModel();
					
					int downloadCount = 0;
					for (int i = 0; i < modelFileArr.size(); i++) {
						SPFile file = modelFileArr.get(i);
						Log.v("sp", ""+"fileid:"+file.getFid());
						Log.v("sp", ""+"sq:"+file.getSeq());
						Log.v("sp", "file.getPath():"+file.getPath());
						downloadCount++;
						if (file.getPath() !=null && Utility.shareInstance().isFileExist(file.getPath())) {
							resultFileArrFiles.add(file);
							Log.v("sp", "downloadCount:"+downloadCount);
							if (downloadCount == modelFileArr.size()) {
								model.setFileArr(resultFileArrFiles);
								modelMap.put(type, model);
								saveModel();
								reloadChildFragment(index, model, type);
							}
						}
						else {
							final int finalDownloadCount = downloadCount;
							Log.v("sp", "finalDownloadCount:"+finalDownloadCount);
							try {
								downloadFile(type, file, new downloadCallBack(){
									public void downloadFinished(SPFile file, Throwable error) {
										if (error != null) {
											if (finalDownloadCount == modelFileArr.size()) {
												reloadChildFragment(index, null, type);
											}
										}
										else {
											resultFileArrFiles.add(file);
											Log.v("sp", "finalDownloadCount:"+finalDownloadCount);
											Log.v("sp", "modelFileArr.size:"+modelFileArr.size());
											if (finalDownloadCount == modelFileArr.size()) {
												model.setFileArr(resultFileArrFiles);
												modelMap.put(type, model);
												saveModel();
												reloadChildFragment(index, model, type);
											}
										}
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			else {
				reloadChildFragment(index, model, type);
			}
		}
	}
	
	private void reloadChildFragment(int index, Model model, String type) {
		switch (index) {
		case 0:{
			summaryFragment.reload(model);
		}
			break;
		case 1:{
			if (type.equals("20")) {
				contentFragment.content20Fragment.reload(model);
			}
			else if (type.equals("21")) {
				contentFragment.content21Fragment.reload(model);
			}
			else if (type.equals("22")) {
				contentFragment.content22Fragment.reload(model);
			}
		}
			break;	
		case 3:{ 
			String fidString = null;
			for (int i = 0; i < model.getFileArr().size(); i++) {
				SPFile file = model.getFileArr().get(i);
				if (file.getSeq() == 1) {
					fidString = file.getFid();
					break;
				}
			}
			musicPlayer.loadMusicList(poolId, subjectId, fidString);
			audioFragment.reload(model);
		}
			break;
		default:
			break;
		}
	}
	
	private void saveModelVer() {
		allVerMap.put(item.getId(), resVerMap);
		Utility.shareInstance().saveObject(verPath, allVerMap);
	}
	
	private void saveModel() {
		Utility.shareInstance().saveObject(modelCachePath, modelMap);
	}
	
	 /**
	 * @param url
	 * 要下载的文件URL
	 * @throws Exception
	 */
	 private void downloadFile(final String modelType,final SPFile file,final downloadCallBack callBack) throws Exception {
		if (downloadClient == null) {
			 downloadClient = new AsyncHttpClient();
		} 
		if (modelType.equals("40")) {
			audioFragment.seekBar.setProgress(0); 
			isAudioDownloading = true;
		}
		
		String paramString = String.format("model/getFile?poolid=%s&modetype=%s&fid=%s", poolId, modelType, file.getFid());
		String url = new String(this.getString(R.string.host) + paramString);
		// 获取二进制数据如图片和其他文件
		downloadClient.get(this, url, new BinaryHttpResponseHandler() {
	
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] binaryData) {
				// 下载成功后需要做的工作
//					progress.setProgress(0);
				Log.e("binaryData:", "共下载了：" + binaryData.length);
				String filePath = new String();
				if (modelType.equals("40")) {
					filePath = Utility.shareInstance().cachAudioDir(poolId, subjectId)+getString(R.string.SPAudioFilePrefix)+file.getFid();
					isAudioDownloading = false;
				}
				else {
					filePath = Utility.shareInstance().cachResPoolDir(poolId, subjectId, modelType)+file.getFid();
				}
				Log.v("sp", "file.getFid():"+file.getFid());
				if (file.getFid().endsWith("png")) {
					Utility.shareInstance().savePicFile(filePath, binaryData);
				}
				else {
					Utility.shareInstance().saveObject(filePath, binaryData);
				}
				SPFile tempFile = file;
				tempFile.setPath(filePath);
				callBack.downloadFinished(tempFile, null); 
			}
	
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] binaryData, Throwable error) {
				if (modelType.equals("40")) {
					isAudioDownloading = false;
				}
				callBack.downloadFinished(file, error); 
			}
	
			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				super.onProgress(bytesWritten, totalSize);
				if (modelType.equals("40") && !audioFragment.isHidden()) {
					int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
					audioFragment.seekBar.setMax(totalSize);
					audioFragment.seekBar.setProgress(bytesWritten); 
					audioFragment.refreshLoadProgress(count);
				}
				Log.e("下载 Progress>>>>>", bytesWritten + " / " + totalSize);
			}
	
			@Override
			public void onRetry(int retryNo) {
				super.onRetry(retryNo);
			}
			
			public void onCancel() {
				Log.e("sp", "CANCEL"+"");
			}
	
		});
	}

	public String getPoolId() {
		return poolId;
	}

	public void setPoolId(String poolId) {
		this.poolId = poolId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	//photo note
	private static final int TAKE_PICTURE = 100;
//	private static final int CHOOSE_PICTURE = 1;
	private static final int SCALE = 5;
	
	public void takePhoto() {
	    PackageManager packageManager = getPackageManager();
	    if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) ==false){
		    Toast.makeText(this, "请允许应用使用摄像头权限.", Toast.LENGTH_SHORT) .show();
		    return;
	    } 
		
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"image.jpg"));
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {			
			switch (requestCode) {
			case TAKE_PICTURE:	
				Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/image.jpg");
				Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
				bitmap.recycle();
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");  
		        String timeString=format.format(new Date());
		        Random md = new Random();
		        int randomInt = 100 + md.nextInt(900);
				String fileName = String.format("%s_%s_31_%s_%d.png", poolId, item.getId(), timeString, randomInt);
				String imgPath = Utility.shareInstance().cacheUserPhotoNote(poolId) + fileName;
				ImageTools.savePhotoToSDCard(newBitmap, Utility.shareInstance().cacheUserPhotoNote(poolId), fileName);
				
				noteFragment.notePhotoFragment.addNewPhoto(imgPath);
				
				break;

//			case CHOOSE_PICTURE:
//				ContentResolver resolver = getContentResolver();				
//				Uri originalUri = data.getData(); 
//	            try {
//					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
//					if (photo != null) {						
//						Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
//						photo.recycle();
//						
//						iv_image.setImageBitmap(smallBitmap);
//					}
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}  
//				break;
			
			default:
				break;
			}
		}
	} 
	
	public void deletePhotoItem(int index) {
		noteFragment.notePhotoFragment.deleteItem(index);
	}
	
	public void deleteAudioItem(int index) {
		noteFragment.noteAudioFragment.deleteItem(index);
	}
	
	//BroadcastReceiver  
    public class MsgReceiver extends BroadcastReceiver{  
  
        @Override  
        public void onReceive(Context context, Intent intent) {   
            if (intent.getBooleanExtra("isAudioNoteFinished", false)) {
            	noteFragment.noteAudioFragment.receiveNoteFinishPlayCast();
			}
        }  
          
    }  
    
	private MsgReceiver msgReceiver; 
	
	
    public class AudioFinishedMsgReceiver extends BroadcastReceiver{  
    	  
        @Override  
        public void onReceive(Context context, Intent intent) {   
            if (intent.getBooleanExtra("isAudioFileFinished", false)) {
            	audioFragment.receiveAudioFinishPlayCast();
			}
        }  
          
    }  
    
	private AudioFinishedMsgReceiver audioFinishedMsgReceiver;
	
	
    public class AudioChangeMsgReceiver extends BroadcastReceiver{  
    	  
        @Override  
        public void onReceive(Context context, Intent intent) {   
    		String curPoolid = intent.getStringExtra("poolid");
    		String curSubjectid = intent.getStringExtra("subjectid");
    		String curItemid = intent.getStringExtra("itemid");
    		subjectId = curSubjectid;
    		poolId = curPoolid;
    		item = Utility.shareInstance().findItem(curSubjectid, curSubjectid, curItemid, DetailActivity.this);
    		
    		String splitArr[] = item.getCaption().split(" ");
    		TextView textView = (TextView)findViewById(R.id.textView_detail_nav);
    		textView.setText(getString(R.string.detail_nav_title, splitArr[0]));
    		
    		params.put("subjectid", subjectId);
    		params.put("poolid", poolId);
    		params.put("itemid", curItemid);
    		
    		isItemChanged = true;
    		
    		audioFragment.reload(null);
        }  
          
    }  
    
	private AudioChangeMsgReceiver audioChangeReceiver;	
	
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
			if (curTabIndex < topTabBtnArr.size()) {
				int index = curTabIndex+1;
				Button button = topTabBtnArr.get(index);
				tabTopBar(button);
			}
//          Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();
        } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity && Math.abs(e1.getY() - e2.getY()) < horizonMinDistance) {
//          Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();
        	if (curTabIndex > 0) {
				int index = curTabIndex-1;
				Button button = topTabBtnArr.get(index);
				tabTopBar(button);
			}
			else {
				if (!isForPlaying) {
					stopDownload();
					finish();
					overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
				}
			}
        }

        return false;
    }
    
	@Override  
    protected void onDestroy() {   
        //注销广播  
        unregisterReceiver(msgReceiver);
        unregisterReceiver(audioFinishedMsgReceiver);
        unregisterReceiver(audioChangeReceiver);
        unbindService(conn);
        super.onDestroy();  
    }  
	//BroadcastReceiver 
	
    protected void onResume() {  
        super.onResume();  
    }
}
