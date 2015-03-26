package com.switchpool.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.xiaoshuye.switchpool.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class Utility extends Activity {
	
	private volatile static Utility singleton;
	  
	private Utility(){}
	  
	public static Utility shareInstance(){
    	if(singleton==null){
        	synchronized(Utility.class){
        	    if(singleton==null){
        	    singleton=new Utility();
        		// ��ȡֻ�ܱ���Ӧ�ó������д��SharedPreferences����
        	    }
    	    }
    	}
    	return singleton; 
	}
	
	private ProgressDialog pd;
	
	public void showWaitingHUD(Context ctx) {
		pd = new ProgressDialog(ctx);
		pd.setTitle(ctx.getString(R.string.loading));
		pd.setMessage(ctx.getString(R.string.loadingandwait));
		pd.setCancelable(false);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setIndeterminate(true);
		pd.show();
	}
	
	public void hideWaitingHUD() {
		if (pd!=null) {
			pd.dismiss();
			pd = null;
		}
	}
	
	 /**
     * ��鵱ǰ�����Ƿ����
     * 
     * @param context
     * @return
     */
    
    public boolean isNetworkAvailable(Activity activity)
    {
        Context context = activity.getApplicationContext();
        // ��ȡ�ֻ��������ӹ�����󣨰�����wi-fi,net�����ӵĹ���
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // ��ȡNetworkInfo����
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            
            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    System.out.println(i + "===״̬===" + networkInfo[i].getState());
                    System.out.println(i + "===����===" + networkInfo[i].getTypeName());
                    // �жϵ�ǰ����״̬�Ƿ�Ϊ����״̬
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
    		if (file.mkdir()) {
    			return true;
    		}
    		else {
    			return false;
    		}
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
	        File file = new File(name);
			if (file.exists()) {
		        try {  
		            fis = new FileInputStream(file.toString());  
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
			else {
				return null;
			}
			
	    }
	
	//bulid Directory
	/*��Ŀ¼*/
	public String rootDir() {
		return Environment.getExternalStorageDirectory().toString()
	             + File.separator +"SwitchPoolCache"+File.separator;
	}
	/*�û���Ϣ��Ŀ¼*/
	public String userRootDir() {
		return this.rootDir() + "ikuser"+ File.separator;
	}
	/*ϵͳ��Ϣ��Ŀ¼*/
	public String sysRootDir() {
		return this.rootDir() + "iksys"+ File.separator;
	}
	/*��Դ��Ŀ¼*/	
	public String resRootDir() {
		return this.sysRootDir() + "ikres"+ File.separator;
	}
	/*���Ի��û�Ŀ¼*/
	public String userInfoFile() {
		return this.userRootDir() + "sp_user.dat";
	}
	/*��subject�ĸ�Ŀ¼*/
	public String resSubjectListFile() {
		return this.resRootDir() + "sp_subjectlist.dat";
	}
	public String cachPoolDir(String poolId,String subjectId) {
		return this.resRootDir() + subjectId +File.separator + poolId + File.separator;
	}
	public String cachResPoolDir(String poolId,String subjectId,String resId) {
		return this.resRootDir() + subjectId +File.separator + poolId + File.separator + resId + File.separator;
	}
	public String cachAudioDir(String poolId,String subjectId) {
		return this.resRootDir() + "A40" +File.separator + subjectId + File.separator + poolId + File.separator;
	}
	
}
