package com.switchpool.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.switchpool.home.MainActivity;
import com.switchpool.model.Item;
import com.switchpool.model.Model;
import com.switchpool.model.SPFile;
import com.switchpool.model.User;
import com.switchpool.utility.ToolBar;
import com.switchpool.utility.ToolBarCallBack;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import org.apache.http.Header; 
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends FragmentActivity {

	static DetailActivity mContext;
	
	private String poolId;
	private String subjectId;
	private Item item;
	
	private Button summaryButton;
	private Button contentButton;
	private Button noteButton;
	private Button audioButton;
	private Button moreButton;
	
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
	
	public DetailActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		
		mContext = this;
		
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
		
		summaryFragment = (DetailSummaryFragment)getSupportFragmentManager().findFragmentById(R.id.detail_fragment_summary);
		
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
			requestModel("10", 0);
		}
		else {
			audioButton.setTextColor(Color.WHITE);
			audioButton.setBackgroundResource(R.drawable.detailtab_bg_hig);
			Drawable audio_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_audio_hig);
			audioButton.setCompoundDrawablesWithIntrinsicBounds(null, audio_top_drawable, null, null);
			requestModel("40", 3);
		}
	}
	
	public void tabTopBar(View sourceButton) {
		this.resetTopTab();
		switch (sourceButton.getId()) {
		case R.id.button_detail_toptab_summary:{
			summaryButton.setTextColor(Color.WHITE);
			summaryButton.setBackgroundResource(R.drawable.detailtab_bg_hig);
			Drawable summary_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_summary_hig);
			summaryButton.setCompoundDrawablesWithIntrinsicBounds(null, summary_top_drawable, null, null);
		}
			break;
		case R.id.button_detail_toptab_content:{
			contentButton.setTextColor(Color.WHITE);
			contentButton.setBackgroundResource(R.drawable.detailtab_bg_hig);
			Drawable content_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_content_hig);
			contentButton.setCompoundDrawablesWithIntrinsicBounds(null, content_top_drawable, null, null);
		}
			break;
		case R.id.button_detail_toptab_note:{
			noteButton.setTextColor(Color.WHITE);
			noteButton.setBackgroundResource(R.drawable.detailtab_bg_hig);
			Drawable note_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_note_hig);
			noteButton.setCompoundDrawablesWithIntrinsicBounds(null, note_top_drawable, null, null);
		}
			break;
		case R.id.button_detail_toptab_audio:{
			audioButton.setTextColor(Color.WHITE);
			audioButton.setBackgroundResource(R.drawable.detailtab_bg_hig);
			Drawable audio_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_audio_hig);
			audioButton.setCompoundDrawablesWithIntrinsicBounds(null, audio_top_drawable, null, null);
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
	
	
	
	private void requestModel(final String modelType, final int index) {
		params.put("modetype", modelType);
		if (resVerMap.get(modelType) == null) {
			resVerMap.put(modelType, "0");
		}
//		params.put("localver", resVerMap.get(modelType));
		params.put("localver", "0");//test
		if (modelType.equals("30")) {
			// TODO:文字笔记下载
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
						downloadCount++;
						if (file.getPath() !=null && Utility.shareInstance().isFileExist(file.getPath())) {
							resultFileArrFiles.add(file);
							if (downloadCount == modelFileArr.size()) {
								model.setFileArr(resultFileArrFiles);
								modelMap.put(type, model);
								saveModel();
								reloadChildFragment(index, model, type);
							}
						}
						else {
							final int finalDownloadCount = downloadCount;
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
		AsyncHttpClient client = new AsyncHttpClient();
		new String();
		String paramString = String.format("model/getFile?poolid=%s&modetype=%s&fid=%s", poolId, modelType, file.getFid());
		String url = new String(this.getString(R.string.host) + paramString);
		// 获取二进制数据如图片和其他文件
		client.get(url, new BinaryHttpResponseHandler() {
	
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] binaryData) {
				// 下载成功后需要做的工作
//				progress.setProgress(0);
				Log.e("binaryData:", "共下载了：" + binaryData.length);
				String filePath = new String();
				if (modelType.equals("40")) {
					filePath = Utility.shareInstance().cachAudioDir(poolId, subjectId)+getString(R.string.SPAudioFilePrefix)+file.getFid();
				}
				else {
					filePath = Utility.shareInstance().cachResPoolDir(poolId, subjectId, modelType)+file.getFid();
				}
				Utility.shareInstance().saveObject(filePath, binaryData);
				file.setPath(filePath);
				callBack.downloadFinished(file);
			}
	
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] binaryData, Throwable error) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, "下载失败", Toast.LENGTH_LONG).show();
			}
	
			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				// TODO Auto-generated method stub
				super.onProgress(bytesWritten, totalSize);
				int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
				// 下载进度显示
//				progress.setProgress(count);
				Log.e("下载 Progress>>>>>", bytesWritten + " / " + totalSize);
	
			}
	
			@Override
			public void onRetry(int retryNo) {
				// TODO Auto-generated method stub
				super.onRetry(retryNo);
				// 返回重试次数
			}
	
		});
	}
}
