package com.switchpool.detail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Dictionary;
import java.util.HashMap;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.switchpool.model.Item;
import com.switchpool.model.Model;
import com.switchpool.model.User;
import com.switchpool.utility.ToolBar;
import com.switchpool.utility.ToolBarCallBack;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import org.apache.http.Header; 

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
		
		if (deatilType == DeatilType.DeatilTypeOrigin) {
			summaryButton.setTextColor(Color.WHITE);
			summaryButton.setBackgroundResource(R.drawable.detailtab_bg_hig);
			Drawable summary_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_summary_hig);
			summaryButton.setCompoundDrawablesWithIntrinsicBounds(null, summary_top_drawable, null, null);
		}
		else {
			audioButton.setTextColor(Color.WHITE);
			audioButton.setBackgroundResource(R.drawable.detailtab_bg_hig);
			Drawable audio_top_drawable = this.getResources().getDrawable(R.drawable.detailtab_audio_hig);
			audioButton.setCompoundDrawablesWithIntrinsicBounds(null, audio_top_drawable, null, null);
		}
		
		//initial Model Version
		verPath = Utility.shareInstance().cachPoolDir(poolId, subjectId)+poolId;
		modelCachePath = Utility.shareInstance().cachPoolDir(poolId, subjectId)+item.getId();
		
		HashMap<String, HashMap<String, String>> map = (HashMap<String, HashMap<String, String>>)Utility.shareInstance().getObject(verPath);
		if (map.isEmpty()) {
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
		if (!tempModelMap.isEmpty()) {
			modelMap = tempModelMap;
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
				finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			}
		});
		
		
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
	
	
	
	private void requestModel(String modelType, int index) {
		params.put("modetype", modelType);
		if (resVerMap.get(modelType) == null) {
			resVerMap.put(modelType, "0");
		}
		params.put("localver", resVerMap.get(modelType));
		if (modelType == "30") {
			// TODO:文字笔记下载
		}
		else {
			3
		}
	}
	
	 /**
	 * @param url
	 * 要下载的文件URL
	 * @throws Exception
	 */
	 public void downloadFile(String url) throws Exception {
		AsyncHttpClient client = new AsyncHttpClient();
		// 指定文件类型
		String[] allowedContentTypes = new String[] {"image/png", "image/jpeg" };
		// 获取二进制数据如图片和其他文件
		client.get(url, new BinaryHttpResponseHandler(allowedContentTypes) {
	
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] binaryData) {
//				String tempPath = Environment.getExternalStorageDirectory().getPath() + "/temp.jpg";
				// TODO Auto-generated method stub
				// 下载成功后需要做的工作
//				progress.setProgress(0);
				//
				Log.e("binaryData:", "共下载了：" + binaryData.length);
				//
				Bitmap bmp = BitmapFactory.decodeByteArray(binaryData, 0,
						binaryData.length);
	
//				File file = new File(tempPath);
				// 压缩格式
				CompressFormat format = Bitmap.CompressFormat.JPEG;
				// 压缩比例
				int quality = 100;
//				try {
//					// 若存在则删除
//					if (file.exists())
//						file.delete();
//					// 创建文件
//					file.createNewFile();
//					//
//					OutputStream stream = new FileOutputStream(file);
//					// 压缩输出
//					bmp.compress(format, quality, stream);
					// 关闭
//					stream.close();
					//
//					Toast.makeText(mContext, "下载成功\n" + tempPath, Toast.LENGTH_LONG).show();
	
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
	
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
