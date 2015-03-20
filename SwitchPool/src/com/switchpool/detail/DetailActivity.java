package com.switchpool.detail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.switchpool.utility.ToolBar;
import com.switchpool.utility.ToolBarCallBack;
import com.xiaoshuye.switchpool.R;

import org.apache.http.Header; 

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DetailActivity extends FragmentActivity {

	static DetailActivity mContext;
	
	public DetailActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		
		mContext = this;
		
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
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			}
		});
	}
	
	public void tabTopBar(Button sourceButton) {
		switch (sourceButton.getId()) {
		case R.id.button_detail_toptab_summary:
			
			break;

		default:
			break;
		}
	}
	
	 /**
	 * @param url
	 * 要下载的文件URL
	 * @throws Exception
	 */
	 public static void downloadFile(String url) throws Exception {
	
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
