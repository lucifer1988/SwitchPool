package com.switchpool.utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;

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
	
	public void saveObject(String name, Object object){  
        FileOutputStream fos = null;  
        ObjectOutputStream oos = null;  
        try {  
            fos = this.openFileOutput(name, MODE_PRIVATE);  
            oos = new ObjectOutputStream(fos);  
            oos.writeObject(object);  
        } catch (Exception e) {  
            e.printStackTrace();  
            //�����Ǳ����ļ������쳣  
        } finally {  
            if (fos != null){  
                try {  
                    fos.close();  
                } catch (IOException e) {  
                    //fos���ر��쳣  
                    e.printStackTrace();  
                }  
            }  
            if (oos != null){  
                try {  
                    oos.close();  
                } catch (IOException e) {  
                    //oos���ر��쳣  
                    e.printStackTrace();  
                }  
            }  
        }  
    } 
	
	public Object getObject(String name){  
	        FileInputStream fis = null;  
	        ObjectInputStream ois = null;  
	        try {  
	            fis = this.openFileInput(name);  
	            ois = new ObjectInputStream(fis);  
	            return ois.readObject();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	            //�����Ƕ�ȡ�ļ������쳣  
	        } finally {  
	            if (fis != null){  
	                try {  
	                    fis.close();  
	                } catch (IOException e) {  
	                    //fis���ر��쳣  
	                    e.printStackTrace();  
	                }  
	            }  
	            if (ois != null){  
	                try {  
	                    ois.close();  
	                } catch (IOException e) {  
	                    //ois���ر��쳣  
	                    e.printStackTrace();  
	                }  
	            }  
	        }  
	        //��ȡ�����쳣������null  
	        return null;  
	    }  
	 
}