package com.switchpool.detail;

import com.switchpool.model.Model;
import com.switchpool.model.SPFile;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class DetailContent22Fragment extends Fragment {

	private ListView listView; 
	private Content22ListAdapter adapter;
	public Model model;
	String[] filePaths;
	
	public DetailContent22Fragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_content22, container,false);
        listView = (ListView) view.findViewById(R.id.listView_detail_content22); 
        listView.setOnItemClickListener(new OnItemClickListener() {
        	@Override  
    	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		imageBrower(position, filePaths);
    	    } 
		});
        
        return view;
    }
	
	public void reload(Model resModel) {
		Utility.shareInstance().hideWaitingHUD();
		model = resModel;
		if (model != null) {
			// 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。  
	        // LruCache通过构造函数传入缓存值，以KB为单位。  
	        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);  
	        // 使用最大可用内存值的1/8作为缓存的大小。  
	        int cacheSize = maxMemory / 8; 
	        Log.v("sp", "cacheSize: " + cacheSize); 
	        LruMemoryCache mMemoryCache = new LruMemoryCache(cacheSize); 
	        Log.v("sp", "memoryCache: " + mMemoryCache.size() / 1024); 
	        filePaths = new String[model.getFileArr().size()];
	        for (int i = 0; i < model.getFileArr().size(); i++) {
	        	SPFile file = model.getFileArr().get(i);
	        	filePaths[i] = file.getPath();
			}
	        Log.v("sp", "filePaths:"+filePaths);
	        
			adapter = new Content22ListAdapter(getActivity(), mMemoryCache);
			listView.setAdapter(adapter);
		}
	}
	
	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	
	private void imageBrower(int position, String[] urls) {
		Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		getActivity().startActivity(intent);
	}
	
	class Content22ListAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater listContainer; 
		private LruMemoryCache mMemoryCache = null; 
		
		public final class ListItemView{                //自定义控件集合     
            public ImageView image;     
            public LinearLayout linearLayout;          
     } 
		
		public Content22ListAdapter (Context ctx, LruMemoryCache memoryCacher) {
			context = ctx;
			listContainer = LayoutInflater.from(context);
			mMemoryCache = memoryCacher; 
		}
		
		@Override
		public int getCount() {      
	        return model.getFileArr().size();   
	    }   
	  
	    public Object getItem(int arg0) {      
	        return null;   
	    }   
	  
	    public long getItemId(int arg0) {  
	        return 0;   
	    } 
	    
	    /**  
	     * ListView Item设置  
	     */  
	    public View getView(int position, View convertView, ViewGroup parent) {       
	        //自定义视图   
	        ListItemView  listItemView = null;  
	        SPFile file = model.getFileArr().get(position);
	        if (convertView == null) {   
	            listItemView = new ListItemView();    
	            //获取list_item布局文件的视图   
	            convertView = listContainer.inflate(R.layout.detail_content22_item, null);  
	            listItemView.image = (ImageView)convertView.findViewById(R.id.imageView_detail_content22_item);   
	            listItemView.linearLayout = (LinearLayout)convertView.findViewById(R.id.linearLayout_detail_content22_item);    
	            convertView.setTag(listItemView);   
	        }else {   
	            listItemView = (ListItemView)convertView.getTag();   
	        }
	        loadBitmap(file.getPath(), listItemView.image);
//	        LayoutParams linearParams = (LayoutParams)listItemView.linearLayout.getLayoutParams();
//            linearParams.height = curImgHeight+10;
//            listItemView.linearLayout.setLayoutParams(linearParams);
	           
	        return convertView;   
	    } 
	    
	    public void loadBitmap(String url, ImageView imageView) {  
	        final String imageKey = url;  
	        final Bitmap bitmap = mMemoryCache.getBitmapFromMemCache(imageKey);  
	        if (bitmap != null) {  
	            imageView.setImageBitmap(bitmap);  
	        } else {   
	            BitmapWorkerTask task = new BitmapWorkerTask(imageView);  
	            task.execute(url);  
	        }  
	    }  
	  
	    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {  
	        // 源图片的高度和宽度  
	        final int height = options.outHeight;  
	        final int width = options.outWidth; 
//	        Log.v("sp", "height:"+height);
//	        Log.v("sp", "reqWidth:"+reqWidth);
//	        Log.v("sp", "width:"+width);
	        int inSampleSize = 1;  
	        if (height > reqHeight || width > reqWidth) {  
	            // 计算出实际宽高和目标宽高的比率   
	            final int widthRatio = Math.round((float) width / (float) reqWidth);    
	            inSampleSize = widthRatio;  
	        }  
	        return inSampleSize;  
	    }  
	  
	    public Bitmap decodeSampledBitmapFromResource(String url, int reqWidth, int reqHeight) {  
	        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小  
	    	Log.v("sp", "url:"+url);
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds =  true;
			BitmapFactory.decodeFile(url, options);  
			
	        // 调用上面定义的方法计算inSampleSize值  
	        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);  
	        // 使用获取到的inSampleSize值再次解析图片  
	        options.inJustDecodeBounds = false;  
	        options.inPreferredConfig = Bitmap.Config.RGB_565;
	        options.inPurgeable = true;
	        options.inInputShareable = true;
	        
	        return BitmapFactory.decodeFile(url, options); 
	    }  
	  
	    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {  
	        ImageView mImageView;  
	  
	        public BitmapWorkerTask(ImageView imageView) {  
	            mImageView = imageView;  
	        }  
	  
	        // 在后台加载图片。  
	        @Override  
	        protected Bitmap doInBackground(String... params) {
	        	DisplayMetrics dm2 = getResources().getDisplayMetrics();
	            final Bitmap bitmap = decodeSampledBitmapFromResource(params[0], dm2.widthPixels-10, 10000);  
	            mMemoryCache.addBitmapToMemoryCache(params[0], bitmap);    
	            return bitmap;  
	        }  
	  
	        @Override  
	        protected void onPostExecute(Bitmap result) { 
	        	
	            mImageView.setImageBitmap(result);  
	            super.onPostExecute(result);  
	        }  
	    }  
	}
}
