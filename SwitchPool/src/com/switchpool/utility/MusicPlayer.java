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
	
	/** 
     * 当Audio播放完的时候触发该动作 
     */  
    @Override  
    public void onCompletion(MediaPlayer player) {   
        next();  
    }  
      
    //在这里我们需要实例化MediaPlayer对象  
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
        if(start!=-1){  
            return pathandname.substring(start+1);    
        }else{  
            return null;  
        }
    } 
	
	public void prepareFile(SPFile file) {
		String fileParh = getString(R.string.SPAudioFilePrefix)+file.getFid();
		for (int i = 0; i < data.size(); i++) {
			String pathString = data.get(i);
			if (getFileName(pathString).equals(fileParh)) {
				curIndex = i;
		        try {   
		            player.reset();    
		            curPath = data.get(curIndex);
		            player.setDataSource(curPath);     
		            player.prepare();
		            return;
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        } 
			}
		}
	}
	
	public void playFile(SPFile file) {
		String fileParh = getString(R.string.SPAudioFilePrefix)+file.getFid();
		for (int i = 0; i < data.size(); i++) {
			String pathString = data.get(i);
			if (getFileName(pathString).equals(fileParh)) {
				curIndex = i;
				play();
				return;
			}
		}
	}
	
    private void play() {  
        try {  
            //重播   
            player.reset();  
            //获取歌曲路径   
            curPath = data.get(curIndex);
            player.setDataSource(curPath);  
            //缓冲   
            player.prepare();
            //开始播放   
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
	
	//为了和Activity交互，我们需要定义一个Binder对象  
    public class AudioBinder extends Binder{  
        //返回Service对象  
    	public MusicPlayer getService(){  
            return MusicPlayer.this;  
        }  
    }  
}
