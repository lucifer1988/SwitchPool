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
import com.switchpool.home.MainActivity;
import com.switchpool.model.Item;
import com.switchpool.model.Model;
import com.switchpool.model.SPFile;
import com.switchpool.model.User;
import com.switchpool.utility.ImageTools;
import com.switchpool.utility.MusicPlayer;
import com.switchpool.utility.ToolBar;
import com.switchpool.utility.ToolBarCallBack;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import org.apache.http.Header; 
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends FragmentActivity implements DetailContentHandler  {

	static DetailActivity mContext;
	
	public String poolId;
	public String subjectId;
	public Item item;
	
	private Button summaryButton;
	private Button contentButton;
	private Button noteButton;
	private Button audioButton;
	private Button moreButton;
	private List<Button> topTabBtnArr;
	private int curTabIndex;
	
	public enum DeatilType { 
		DeatilTypeOrigin(0), DeatilTypeAudio(1); 
	    private final int val; 
	 
	    private DeatilType(int value) { 
	        val = value; 
	    } 
	 
	    public int getValue() { 
	        return this.val; 
	    } 
	} 
	public DeatilType deatilType;
	
	//cache path
	private String verPath;
	private String modelCachePath;
	
	//Map
	private HashMap<String, String> resVerMap;
	private HashMap<String, HashMap<String, String>> allVerMap;
	private HashMap<String, Model> modelMap;
	private RequestParams params;
	
	public interface downloadCallBack {  
	   void downloadFinished(SPFile file);  
	} 
	
	private DetailSummaryFragment summaryFragment;
	private DetailContentFragment contentFragment;
	private DetailNoteFragment noteFragment;
	private DetailAudioFragment audioFragment;
	
	FragmentManager fManager;
	AsyncHttpClient client;
	
	 public MusicPlayer musicPlayer;  
     
    //使用ServiceConnection来监听Service状态的变化  
    private ServiceConnection conn = new ServiceConnection() {  
          
        @Override  
        public void onServiceDisconnected(ComponentName name) {  
            // TODO Auto-generated method stub  
        	musicPlayer = null;  
        }  
          
        @Override  
        public void onServiceConnected(ComponentName name, IBinder binder) {  
            //这里我们实例化audioService,通过binder来实现  
        	musicPlayer = ((MusicPlayer.AudioBinder)binder).getService();  
              
        }  
    }; 
	    
	public DetailActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		
		mContext = this;
		fManager = getSupportFragmentManager();
		
		Intent intent = this.getIntent(); 
		item = (Item)intent.getSerializableExtra("item");
		poolId = intent.getStringExtra("poolId");
		subjectId = intent.getStringExtra("subjectId");
		deatilType = (DeatilType)intent.getSerializableExtra("type");
		
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
		ImageButton actionButton = (ImageButton)findViewById(R.id.button_detail_note_action);  
		actionButton.setOnTouchListener((OnTouchListener)noteFragment);
		
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
			resVerMap = map.get(item.getId());
		}
		
		HashMap<String, Model> tempModelMap = (HashMap<String, Model>)Utility.shareInstance().getObject(modelCachePath);
		if (tempModelMap != null && !tempModelMap.isEmpty()) {
			modelMap = tempModelMap;
		}
		else {
			modelMap = new HashMap<String, Model>();
		}
		
		//request params
		User userInfo = (User)Utility.shareInstance().getObject(Utility.shareInstance().userInfoFile());
		params = new RequestParams();
		params.put("uid", userInfo.getUid());
		params.put("token", userInfo.getToken());
		params.put("subjectid", subjectId);
		params.put("poolid", poolId);
		params.put("itemid", item.getId());
		
		//buttom toolbar
		ToolBar toolBar = (ToolBar)getSupportFragmentManager().findFragmentById(R.id.detail_toolbar);
		toolBar.setCallBack(new ToolBarCallBack() {
			
			@Override
			public void tapButton6() {
				
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
	            Intent myIntent = new Intent();
	            myIntent.setClass(DetailActivity.this, MainActivity.class);
	            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(myIntent);
	            DetailActivity.this.finish();
	            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			}
			
			@Override
			public void tapButton1() {
				finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			}
		});
		
		if (deatilType == DeatilType.DeatilTypeOrigin) {
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
			requestModel("40", 3);
			curTabIndex = 3;
		}
		
		Intent playIntentntent = new Intent();  
		playIntentntent.setClass(this, MusicPlayer.class); 
	    startService(playIntentntent);  
        bindService(playIntentntent, conn, Context.BIND_AUTO_CREATE); 
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
			
			if (summaryFragment.getModel() == null) {
				requestModel("10", index);
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
			
			if (contentFragment.content20Fragment.getModel() == null) {
				requestModel("20", index);
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
			
			if (noteFragment.noteTextFragment.getNote() == null) {
				requestModel("30", index);
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
			
			if (audioFragment.model == null) {
				requestModel("40", index);
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
		if (resVerMap.get(modelType) == null) {
			resVerMap.put(modelType, "0");
		}
//		params.put("localver", resVerMap.get(modelType));
		params.put("localver", "0");//test
		if (modelType.equals("30")) {
			noteFragment.noteTextFragment.requestNoteText(params);
		}
		else {
			final Model cacheModel = modelMap.get(modelType);
			if (Utility.shareInstance().isNetworkAvailable(this)) {
				AsyncHttpClient client = new AsyncHttpClient();
				String url = new String(this.getString(R.string.host) + "model/item");
				Log.v("sp", ""+url);
				Log.v("sp", ""+params);
				try {  
					client.post(url, params, new JsonHttpResponseHandler() {  

		                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {   
		                	Log.v("sp", "" + jsonObject); 
		                	if (statusCode == 200) {
		                		try {
		                			Model curModel;
		                			String curVer = jsonObject.getString("version");
		                			if (resVerMap.get(modelType).equals(curVer) && cacheModel != null) {
		                				handelModelFiles(index, cacheModel, modelType);
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
									if (cacheModel != null) {
										handelModelFiles(index, cacheModel, modelType);
									}
									Log.e("sp", "" + Log.getStackTraceString(e));
								}
							}
		                	else {
								if (cacheModel != null) {
									handelModelFiles(index, cacheModel, modelType);
								}
							}
		                }  
		                
		                public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
							if (cacheModel != null) {
								handelModelFiles(index, cacheModel, modelType);
							}
		                }
		                   
		            });  
				} catch (Exception e) {
					if (cacheModel != null) {
						handelModelFiles(index, cacheModel, modelType);
					}
					Log.e("sp", "" + Log.getStackTraceString(e));
				}
			}
			else {
				if (cacheModel != null) {
					handelModelFiles(index, cacheModel, modelType);
				}
			}
		}
	}
	
	private void handelModelFiles(final int index, final Model model, final String type) {
		if (type.equals("30")) {
			// TODO:文字笔记下载
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
									public void downloadFinished(SPFile file) {
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
								});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
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
	         
			musicPlayer.loadMusicList(poolId, subjectId);
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
		 if (client == null) {
			 client = new AsyncHttpClient();
		} 
		new String();
		String paramString = String.format("model/getFile?poolid=%s&modetype=%s&fid=%s", poolId, modelType, file.getFid());
		String url = new String(this.getString(R.string.host) + paramString);
		// 获取二进制数据如图片和其他文件
		client.get(url, new BinaryHttpResponseHandler() {
	
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] binaryData) {
				// 下载成功后需要做的工作
//					progress.setProgress(0);
				Log.e("binaryData:", "共下载了：" + binaryData.length);
				String filePath = new String();
				if (modelType.equals("40")) {
					filePath = Utility.shareInstance().cachAudioDir(poolId, subjectId)+getString(R.string.SPAudioFilePrefix)+file.getFid();
				}
				else {
					filePath = Utility.shareInstance().cachResPoolDir(poolId, subjectId, modelType)+file.getFid();
				}
				Utility.shareInstance().saveObject(filePath, binaryData);
				SPFile tempFile = file;
				tempFile.setPath(filePath);
				callBack.downloadFinished(tempFile); 
			}
	
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] binaryData, Throwable error) {
				callBack.downloadFinished(file); 
			}
	
			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				super.onProgress(bytesWritten, totalSize);
				int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
				// 下载进度显示
//					progress.setProgress(count);
				Log.e("下载 Progress>>>>>", bytesWritten + " / " + totalSize);
	
			}
	
			@Override
			public void onRetry(int retryNo) {
				super.onRetry(retryNo);
				// 返回重试次数
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
				String fileName = String.format("%s_%s_31_%s_%d", poolId, item.getId(), timeString, randomInt);
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
	    
    protected void onResume() {  
        super.onResume();  
//        resume();  
    } 
}
