package com.switchpool.detail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.xiaoshuye.switchpool.R;

import org.apache.http.Header; 

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
		setContentView(R.layout.login);
		
		mContext = this;
	}
	
	 /**
	 * @param url
	 * Ҫ���ص��ļ�URL
	 * @throws Exception
	 */
	 public static void downloadFile(String url) throws Exception {
	
		AsyncHttpClient client = new AsyncHttpClient();
		// ָ���ļ�����
		String[] allowedContentTypes = new String[] {"image/png", "image/jpeg" };
		// ��ȡ������������ͼƬ�������ļ�
		client.get(url, new BinaryHttpResponseHandler(allowedContentTypes) {
	
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] binaryData) {
//				String tempPath = Environment.getExternalStorageDirectory().getPath() + "/temp.jpg";
				// TODO Auto-generated method stub
				// ���سɹ�����Ҫ���Ĺ���
//				progress.setProgress(0);
				//
				Log.e("binaryData:", "�������ˣ�" + binaryData.length);
				//
				Bitmap bmp = BitmapFactory.decodeByteArray(binaryData, 0,
						binaryData.length);
	
//				File file = new File(tempPath);
				// ѹ����ʽ
				CompressFormat format = Bitmap.CompressFormat.JPEG;
				// ѹ������
				int quality = 100;
//				try {
//					// ��������ɾ��
//					if (file.exists())
//						file.delete();
//					// �����ļ�
//					file.createNewFile();
//					//
//					OutputStream stream = new FileOutputStream(file);
//					// ѹ�����
//					bmp.compress(format, quality, stream);
					// �ر�
//					stream.close();
					//
//					Toast.makeText(mContext, "���سɹ�\n" + tempPath, Toast.LENGTH_LONG).show();
	
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
	
			}
	
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] binaryData, Throwable error) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, "����ʧ��", Toast.LENGTH_LONG).show();
			}
	
			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				// TODO Auto-generated method stub
				super.onProgress(bytesWritten, totalSize);
				int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
				// ���ؽ�����ʾ
//				progress.setProgress(count);
				Log.e("���� Progress>>>>>", bytesWritten + " / " + totalSize);
	
			}
	
			@Override
			public void onRetry(int retryNo) {
				// TODO Auto-generated method stub
				super.onRetry(retryNo);
				// �������Դ���
			}
	
		});
	}
}