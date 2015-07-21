package com.switchpool.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import com.switchpool.model.Item;
import com.walnutlabs.android.ProgressHUD;
import com.xiaoshuye.switchpool.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utility extends Activity {
	
	private volatile static Utility singleton;
	private String appRootPath;
	private Utility(){}
	  
	public static Utility shareInstance(){
    	if(singleton==null){
        	synchronized(Utility.class){
        	    if(singleton==null){
        	    singleton=new Utility();
        	    }
    	    }
    	}
    	return singleton; 
	}
	
//	private ProgressDialog pd;
	private ProgressHUD mProgressHUD; 
	
	public void showWaitingHUD(Context ctx) {
		mProgressHUD = ProgressHUD.show(ctx,"加载中", false, false, null);
		
//		pd = new ProgressDialog(ctx);
//		pd.setTitle(ctx.getString(R.string.loading));
//		pd.setMessage(ctx.getString(R.string.loadingandwait));
//		pd.setCancelable(true);
//		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		pd.setIndeterminate(true);
//		pd.show();
	}
	
	public void hideWaitingHUD() {
		if (mProgressHUD != null) {
			mProgressHUD.dismiss();
		}
//		if (pd!=null) {
//			pd.dismiss();
//			pd = null;
//		}
	}
	
	
	private static long lastClickTime;  
    public boolean isFastDoubleClick() {  
        long time = System.currentTimeMillis();  
        long timeD = time - lastClickTime;  
        if ( 0 < timeD && timeD < 1000) {     
            return true;     
        }     
        lastClickTime = time;     
        return false;     
    } 
    
	 /**
     * 检查当前网络是否可用
     * 
     * @param context
     * @return
     */
    
    public boolean isNetworkAvailable(Activity activity)
    {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wifi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            
            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isFileExist(String path) {
    	File file = new File(path);
    	if (!file.exists()) {
			return false;
    	}
    	else {
    		return true;
		}
    }
    
	public void saveObject(String name, Object object){  
        FileOutputStream fos = null;  
        ObjectOutputStream oos = null;  
        
        File file = new File(name);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		

        try {   
        	fos = new FileOutputStream(file.toString());
            oos = new ObjectOutputStream(fos);  
            oos.writeObject(object);  
        } catch (Exception e) {  
            e.printStackTrace();  
            //这里是保存文件产生异常  
        } finally {  
            if (fos != null){  
                try {  
                    fos.close();  
                } catch (IOException e) {  
                    //fos流关闭异常  
                    e.printStackTrace();  
                }  
            }  
            if (oos != null){  
                try {  
                    oos.close();  
                } catch (IOException e) {  
                    //oos流关闭异常  
                    e.printStackTrace();  
                }  
            }  
        }  
    } 
	
	public void savePicFile(String name, byte[] data) {
		Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length); 
        File bitmapFile = new File(name);  
        FileOutputStream bitmapWtriter = null;
		if (!bitmapFile.getParentFile().exists()) {
			bitmapFile.getParentFile().mkdirs();
		}
        try {
        	bitmapWtriter = new FileOutputStream(bitmapFile);
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bitmapWtriter);  
	}
	
	public Object getObject(String name){  
	        FileInputStream fis = null;  
	        ObjectInputStream ois = null;  
	        File file = new File(name);
			if (file.exists()) {
		        try {  
		            fis = new FileInputStream(file.toString());  
		            ois = new ObjectInputStream(fis);  
		            return ois.readObject();  
		        } catch (Exception e) {  
		            e.printStackTrace();  
		            //这里是读取文件产生异常  
		        } finally {  
		            if (fis != null){  
		                try {  
		                    fis.close();  
		                } catch (IOException e) {  
		                    //fis流关闭异常  
		                    e.printStackTrace();  
		                }  
		            }  
		            if (ois != null){  
		                try {  
		                    ois.close();  
		                } catch (IOException e) {  
		                    //ois流关闭异常  
		                    e.printStackTrace();  
		                }  
		            }  
		        }  
		        //读取产生异常，返回null  
		        return null;
			}
			else {
				return null;
			}
			
	    }
	
   /**
    * 加载本地图片
    * @param url
    * @return
    */
    public Bitmap getLoacalBitmap(String url) {
         try {
        	 Log.v("sp", "url:"+url);
              FileInputStream fis = new FileInputStream(url);
              return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片        
              
           } catch (FileNotFoundException e) {
              e.printStackTrace();
              return null;
         }
    }
    
	public String paserTimeToYM(long time) {
		System.setProperty("user.timezone", "Asia/Shanghai");
		TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		TimeZone.setDefault(tz);
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		return format.format(new Date(time * 1000L));
	}
	
	public String paserTimeToYMDHMS(long time) {
		System.setProperty("user.timezone", "Asia/Shanghai");
		TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		TimeZone.setDefault(tz);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return format.format(new Date(time * 1000L));
	}
	
	public String paserTimeToHM(long time) {
		System.setProperty("user.timezone", "Asia/Shanghai");
		TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		TimeZone.setDefault(tz);
		SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
		return format.format(new Date(time * 1000L));
	}
	
	public String paserTimeToHMS(int time) {
		String result = new String();
		time = time/1000;
		int hour = time/3600;
		int min = (time-hour*3600)/60;
		int sec = (time-hour*3600-min*60);
		result = String.format("%s%d:%s%d:%s%d", (hour<10?"0":""), hour, (min<10?"0":""), min, (sec<10?"0":""), sec);
		if (hour == 0) {
			result = result.substring(3);
		}
		return result;
	}
	
	static HashMap<String, HashMap<String, HashMap<String, Item>>> itemSelectHistoryMap = new HashMap<String, HashMap<String, HashMap<String, Item>>>();
	
	public void setTopSelectItem(String subjectid, String poolid, Item item, Context ctx) {
		HashMap<String, HashMap<String, Item>> subHashMap = null;
		HashMap<String, Item> poolHashMap = null;
		if (itemSelectHistoryMap != null && itemSelectHistoryMap.get(subjectid) != null) {
			subHashMap = itemSelectHistoryMap.get(subjectid);
			if (subHashMap.get(poolid) != null) {
				poolHashMap = subHashMap.get(poolid);
			}
			else {
				poolHashMap = new HashMap<String, Item>();
			}
		}
		else {
			subHashMap = new HashMap<String, HashMap<String, Item>>();
			poolHashMap = new HashMap<String, Item>();
		}
		poolHashMap.put(ctx.getString(R.string.SPItemSelectHistoryTop), item);
		subHashMap.put(poolid, poolHashMap);
		itemSelectHistoryMap.put(subjectid, subHashMap);
	}
	
	public Item topSelectItem(String subjectid, String poolid, Context ctx) {
		if (itemSelectHistoryMap != null && itemSelectHistoryMap.get(subjectid) != null) {
			HashMap<String, HashMap<String, Item>> subHashMap = itemSelectHistoryMap.get(subjectid);
			if (subHashMap.get(poolid) != null) {
				HashMap<String, Item> poolHashMap = subHashMap.get(poolid);
				if (poolHashMap.containsKey(ctx.getString(R.string.SPItemSelectHistoryTop))) {
					return poolHashMap.get(ctx.getString(R.string.SPItemSelectHistoryTop));
				}
			}
		}
		return null;
	}
	
	public void setSecSelectItem(String subjectid, String poolid, Item item, Context ctx) {
		HashMap<String, HashMap<String, Item>> subHashMap = null;
		HashMap<String, Item> poolHashMap = null;
		if (itemSelectHistoryMap != null && itemSelectHistoryMap.get(subjectid) != null) {
			subHashMap = itemSelectHistoryMap.get(subjectid);
			if (subHashMap.get(poolid) != null) {
				poolHashMap = subHashMap.get(poolid);
			}
			else {
				poolHashMap = new HashMap<String, Item>();
				poolHashMap = new HashMap<String, Item>();
			}
		}
		else {
			subHashMap = new HashMap<String, HashMap<String, Item>>();
		}
		poolHashMap.put(ctx.getString(R.string.SPItemSelectHistorySec), item);
		subHashMap.put(poolid, poolHashMap);
		itemSelectHistoryMap.put(subjectid, subHashMap);
	}
	
	public Item secSelectItem(String subjectid, String poolid, Context ctx) {
		if (itemSelectHistoryMap != null && itemSelectHistoryMap.get(subjectid) != null) {
			HashMap<String, HashMap<String, Item>> subHashMap = itemSelectHistoryMap.get(subjectid);
			if (subHashMap.get(poolid) != null) {
				HashMap<String, Item> poolHashMap = subHashMap.get(poolid);
				if (poolHashMap.containsKey(ctx.getString(R.string.SPItemSelectHistorySec))) {
					return poolHashMap.get(ctx.getString(R.string.SPItemSelectHistorySec));
				}
			}
		}
		return null;
	}
	
	public Item findItem(String subjectid, String poolid, String itemid, Context ctx) {
		Log.v("sp", "subjectid"+subjectid);
		Log.v("sp", "poolid"+poolid);
		Log.v("sp", "itemid"+itemid);
		Item resultItem = null;
		String cachePathString = cachPoolDir(poolid, subjectid)+ ctx.getString(R.string.SPItemList);
		List<Item> cacheArr = (List<Item>)getObject(cachePathString);
		Log.v("sp", "cacheArr:"+ cacheArr);
		if (cacheArr != null && cacheArr.size() > 0) {
		  loop:for (int i = 0; i < cacheArr.size(); i++) {
				Item topItem = cacheArr.get(i);
					for (int j = 0; j < topItem.getItemArr().size(); j++) {
						Item secItem = topItem.getItemArr().get(j);
							for (int k = 0; k < secItem.getItemArr().size(); k++) {
								Item thrItem = secItem.getItemArr().get(k);
									for (int l = 0; l < thrItem.getItemArr().size(); l++) {
										Log.v("sp", "thrItem.getItemArr():"+thrItem.getItemArr());
										Item forItem = thrItem.getItemArr().get(l);
										if (forItem.getId().equals(itemid)) {
											resultItem = forItem;
											Log.v("sp", "forItem.getId():"+forItem.getId());
											break loop;
										}
										else {
											continue;
										}
									}
							}
					}
			}
		}
		return resultItem;
	}
	
	public Item findSecItem(String subjectid, String poolid, String itemid, Context ctx) {
		Log.v("sp", "subjectid"+subjectid);
		Log.v("sp", "poolid"+poolid);
		Log.v("sp", "itemid"+itemid);
		String cachePathString = cachPoolDir(poolid, subjectid)+ ctx.getString(R.string.SPItemList);
		List<Item> cacheArr = (List<Item>)getObject(cachePathString);
		Item curSecItem = null;
		Log.v("sp", "cacheArr:"+ cacheArr);
		if (cacheArr != null && cacheArr.size() > 0) {
		  loop:for (int i = 0; i < cacheArr.size(); i++) {
				Item topItem = cacheArr.get(i);
					for (int j = 0; j < topItem.getItemArr().size(); j++) {
						Item secItem = topItem.getItemArr().get(j);
							for (int k = 0; k < secItem.getItemArr().size(); k++) {
								Item thrItem = secItem.getItemArr().get(k);
									for (int l = 0; l < thrItem.getItemArr().size(); l++) {
										Log.v("sp", "thrItem.getItemArr():"+thrItem.getItemArr());
										Item forItem = thrItem.getItemArr().get(l);
										if (forItem.getId().equals(itemid)) {
											curSecItem = secItem;
											break loop;
										}
										else {
											continue;
										}
									}
							}
					}
			}
		}
		return curSecItem;
	}
	
	//bulid Directory
	/*根目录*/
	public String getAppRootPath() {
		return appRootPath;
	}

	public void setAppRootPath(String appRootPath) {
		this.appRootPath = appRootPath;
	}
	
	public String rootDir() {
		String rootPathString = getAppRootPath()+File.separator+"SwitchPoolCache"+File.separator;		
		Log.v("sp", ""+ rootPathString);
		return rootPathString;
	}

	/*用户信息根目录*/
	public String userRootDir() {
		return this.rootDir() + "ikuser"+ File.separator;
	}
	/*系统信息根目录*/
	public String sysRootDir() {
		return this.rootDir() + "iksys"+ File.separator;
	}
	/*资源根目录*/	
	public String resRootDir() {
		return this.sysRootDir() + "ikres"+ File.separator;
	}
	/*存subject的根目录*/
	public String resSubjectListFile() {
		return this.resRootDir() + "sp_subjectlist";
	}
	public String cachPoolDir(String poolId,String subjectId) {
		return this.resRootDir() + subjectId +File.separator + poolId + File.separator;
	}
	public String cachResPoolDir(String poolId,String subjectId,String resId) {
		return this.resRootDir() + subjectId +File.separator + poolId + File.separator + resId + File.separator;
	}
	public String cachAudioDir(String poolId,String subjectId) {
		return this.resRootDir() + "A40" +File.separator + subjectId.substring(3) + File.separator + poolId.substring(6) + File.separator;
	}
	/*个性化用户目录*/
	public String userInfoFile() {
		return this.userRootDir() + "sp_user";
	}
	public String cacheUserTextNote(String poolid) {
		return this.userRootDir() + "tnote" + File.separator + poolid + File.separator + "kSPTextNoteDict";
	}
	public String cacheUserPhotoNote(String poolid) {
		return this.userRootDir() + "inote" + File.separator + poolid + File.separator;
	}
	public String cacheUserAudioNote(String poolid) {
		return this.userRootDir() + "anote" + File.separator + poolid + File.separator;
	}
	public String cacheUserHistory() {
		return this.userRootDir() + "searchhistory" + File.separator;
	}
}
