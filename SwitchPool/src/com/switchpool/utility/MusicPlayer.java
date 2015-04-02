package com.switchpool.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.switchpool.model.SPFile;
import com.xiaoshuye.switchpool.R;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MusicPlayer extends Service implements MediaPlayer.OnCompletionListener {

	private final IBinder binder = new AudioBinder();
	
	public MediaPlayer player;
	private List<String> data;
	private int curIndex;
	private Boolean isPause;
	public String curPath;
	
	public MusicPlayer() {
	}
	
//	public static MusicPlayer shareInstance(){
//    	if(singleton==null){
//        	synchronized(MusicPlayer.class){
//        	    if(singleton==null){
//        	    singleton=new MusicPlayer();
//        	    }
//    	    }
//    	}
//    	return singleton; 
//	}
	/** 
     * ��Audio�������ʱ�򴥷��ö��� 
     */  
    @Override  
    public void onCompletion(MediaPlayer player) {   
        next();  
    }  
      
    //������������Ҫʵ����MediaPlayer����  
    public void onCreate(){  
        super.onCreate();   
        player = new MediaPlayer();
        data = new ArrayList<String>();
        curIndex = 0;
        isPause = false;  
        player.setOnCompletionListener(this);  
    }  
     
    public int onStartCommand(Intent intent, int flags, int startId){   
        return START_STICKY;  
    }  
      
    public void onDestroy(){  
        //super.onDestroy();  
        if(player.isPlaying()){  
            player.stop();  
        }  
        player.release();  
    } 
    
	public void loadMusicList(String poolid, String subjectid) {
		String listPath = Utility.shareInstance().cachAudioDir(poolid, subjectid);
		Log.v("sp", ""+listPath);
		if (data.size() > 0) {
			data.clear();
		}
		File mfile = new File(listPath);
		File[] files = mfile.listFiles();
		for (int i = 0; i < files.length; i++) {
		  File file = files[i];
		  data.add(file.getPath());
		}
	}

	public String getFileName(String pathandname){  
        int start=pathandname.lastIndexOf("/");  
//        int end=pathandname.lastIndexOf(".");  
        if(start!=-1){  
            return pathandname.substring(start+1);    
        }else{  
            return null;  
        }
    } 
	
	public void playFile(SPFile file) {
		player.stop();
		String fileParh = getString(R.string.SPAudioFilePrefix)+file.getFid();
		for (int i = 0; i < data.size(); i++) {
			String pathString = data.get(i);
			if (getFileName(pathString).equals(fileParh)) {
				curIndex = i;
				play();
			}
		}
	}
	
    private void play() {  
        try {  
            //�ز�   
            player.reset();  
            //��ȡ����·��   
            curPath = data.get(curIndex);
            player.setDataSource(curPath);  
            //����   
            player.prepare();
            //��ʼ����   
            player.start(); 
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    
    public void resume() {  
        if (isPause) {  
            player.start();  
            isPause = false;  
        }  
    }
      
    public void pause() {  
        if (player != null && player.isPlaying()) {  
            player.pause();  
            isPause = true;  
        }  
    }
	
    public void previous() {  
    	curIndex = curIndex - 1 < 0 ? data.size() - 1 : curIndex - 1;  
        play();  
    }  
      
    public void next() {
    	curIndex = (curIndex + 1) % data.size();  
        play();  
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder; 
	}  
	
	//Ϊ�˺�Activity������������Ҫ����һ��Binder����  
    public class AudioBinder extends Binder{  
        //����Service����  
    	public MusicPlayer getService(){  
            return MusicPlayer.this;  
        }  
    }  
}
