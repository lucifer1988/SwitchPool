package com.switchpool.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.xiaoshuye.switchpool.R;

import android.app.Activity;
import android.os.Environment;

public class Utility extends Activity {
	
	private volatile static Utility singleton;
	  
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
	
	public String cache_user_filenameString;
	
	public String getCache_user_filenameString() {
		if (cache_user_filenameString == null) {
			cache_user_filenameString = this.getString(R.string.ser_user);
		}
		return cache_user_filenameString;
	}

	public void saveObject(String name, Object object){  
        FileOutputStream fos = null;  
        ObjectOutputStream oos = null;  
        
        File file = new File(Environment.getExternalStorageDirectory().toString()
	             + File.separator +"SwitchPoolCache"+File.separator +"ikuser"+ File.separator + name);
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
//            fos = this.openFileOutput(name, MODE_PRIVATE); 
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
	
	public Object getObject(String name){  
	        FileInputStream fis = null;  
	        ObjectInputStream ois = null;  
	        File file = new File(Environment.getExternalStorageDirectory().toString()
		             + File.separator +"SwitchPoolCache"+File.separator +"ikuser"+ File.separator + name);
	        
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
	 
}
