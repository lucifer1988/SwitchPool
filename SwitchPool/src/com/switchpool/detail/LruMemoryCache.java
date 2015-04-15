package com.switchpool.detail;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class LruMemoryCache extends LruCache<String, Bitmap> {

	public LruMemoryCache(int maxSize) {  
        super(maxSize);  
    }  
	      
	@Override  
	protected int sizeOf(String key, Bitmap bitmap) {  
	    // ��д�˷���������ÿ��ͼƬ�Ĵ�С��Ĭ�Ϸ���ͼƬ������  
	    return bitmap.getRowBytes()*bitmap.getHeight()/1024;
	}  
	  
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {     
	    if (getBitmapFromMemCache(key) == null) {   
	        put(key, bitmap);  
	    }     
	}     
	   
	public Bitmap getBitmapFromMemCache(String key) {     
	    return get(key);     
	}  
}
