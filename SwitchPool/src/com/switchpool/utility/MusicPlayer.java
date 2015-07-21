package com.switchpool.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.switchpool.model.SPFile;
import com.xiaoshuye.switchpool.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
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
	public Boolean isItemAudio;
	
	private int maxVolume = 50; // 最大音量值  
    private int curVolume = 20; // 当前音量值  
    private int stepVolume = 0; // 每次调整的音量幅度
    private AudioManager audioMgr = null; // Audio管理器，用了控制音量 
	
	public MusicPlayer() {
	}
	
	/** 
     * 当Audio播放完的时候触发该动作 
     */  
    @Override  
    public void onCompletion(MediaPlayer player) { 
    	if (isItemAudio) {
    		next(); 
		} 
    	else {
    		isItemAudio = true;
    		Intent intent = new Intent("com.xiaoshuye.audioNoteFinished.broadcast");
    		//要发送的内容
    		intent.putExtra("isAudioNoteFinished", true);
    		//发送 一个无序广播
    		sendBroadcast(intent);
		}
    }  
    
    private void postAudioFinishIntent() {
		Intent intent = new Intent("com.xiaoshuye.audioFileFinished.broadcast");
		//要发送的内容
		intent.putExtra("isAudioFileFinished", true);
		//发送 一个无序广播
		sendBroadcast(intent);
	}
    
    private void postAudioChangeIntent() {
		String fileName = getFileName(curPath);
		String curPoolid = fileName.split("_")[0];
		String curSubjectid = curPoolid.substring(0, 6);
		String curItemid = fileName.split("_")[1];
		
		Intent intent = new Intent("com.xiaoshuye.audioFileChanged.broadcast");
		//要发送的内容
		intent.putExtra("subjectid", curSubjectid);
		intent.putExtra("poolid", curPoolid);
		intent.putExtra("itemid", curItemid);
		//发送 一个无序广播
		sendBroadcast(intent);
	}
      
    //在这里我们需要实例化MediaPlayer对象  
    public void onCreate(){  
        super.onCreate();   
        player = new MediaPlayer();
        data = new ArrayList<String>();
        curIndex = 0;
        isPause = false;  
        isItemAudio = true;
        player.setOnCompletionListener(this);  
        
        audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);  
        // 获取最大音乐音量  
        maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);  
        // 初始化音量大概为最大音量的1/2  
        curVolume = maxVolume / 2;  
        // 每次调整的音量大概为最大音量的1/6  
        stepVolume = maxVolume / 6;
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
    
	public void loadMusicList(String poolid, String subjectid, String fid) {
		String listPath = Utility.shareInstance().cachAudioDir(poolid, subjectid);
		Log.v("sp", ""+listPath);
		if (data.size() > 0) {
			data.clear();
		}
		File mfile = new File(listPath);
		File[] files = mfile.listFiles();
		for (int i = 0; i < files.length; i++) {
		  File file = files[i];
		  if (fid != null && file.getPath().endsWith(fid)) {
			  data.add(file.getPath());
		  }
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
		if (!isItemAudio && player.isPlaying()) {
			player.stop();
    		Intent intent = new Intent("com.xiaoshuye.audioNoteFinished.broadcast");
    		intent.putExtra("isAudioNoteFinished", true);
    		sendBroadcast(intent);
		}
		
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
	
	public void stop() {
		player.stop();
		curIndex = 0;
		if (isItemAudio) {
    		isItemAudio = true;
    		Intent intent = new Intent("com.xiaoshuye.audioNoteFinished.broadcast");
    		//要发送的内容
    		intent.putExtra("isAudioNoteFinished", true);
    		//发送 一个无序广播
    		sendBroadcast(intent);
		}
	}
	
	public void playAudioNote(String audioNotePath) {
		if (isItemAudio && player.isPlaying()) {
			postAudioFinishIntent();
		}
		
		player.stop();
		isItemAudio = false;
        try {  
            //重播   
            player.reset();  
            player.setDataSource(audioNotePath);  
            //缓冲   
            player.prepare();
            //开始播放   
            player.start(); 
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
	
	public void playFile(SPFile file) {
		if (!isItemAudio && player.isPlaying()) {
    		Intent intent = new Intent("com.xiaoshuye.audioNoteFinished.broadcast");
    		intent.putExtra("isAudioNoteFinished", true);
    		sendBroadcast(intent);
		}
		
		String fileParh = getString(R.string.SPAudioFilePrefix)+file.getFid();
		isItemAudio = true;
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
    	if (curIndex - 1 < 0 ) {
//			player.stop();
    		curIndex = 0;
    		play();
    		postAudioChangeIntent();
		}
    	else {
    		curIndex = curIndex - 1;
    		play(); 
    		postAudioChangeIntent();
		} 
    }  
      
    public void next() {
    	if (curIndex + 1 > data.size()) {
//    		player.stop();
//    		postAudioFinishIntent();
    		curIndex = 0;
    		play();
//    		postAudioChangeIntent();
		}
    	else {
    		curIndex = curIndex + 1;
    		play();
//    		postAudioChangeIntent();
		} 
    }
    
    public void volumeUp() {
    	curVolume += stepVolume;  
        if (curVolume >= maxVolume) {  
            curVolume = maxVolume;  
        } 
        adjustVolume(); 
	}
    
    public void volumeDown() {
    	curVolume -= stepVolume;  
        if (curVolume <= 0) {  
            curVolume = 0;  
        } 
        adjustVolume(); 
	}
    
    /** 
     * 调整音量 
     */  
    private void adjustVolume() {  
        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, AudioManager.FLAG_PLAY_SOUND);  
    } 

    public String curSubjectid() {
    	if (curPath !=null) {
    		String fileName = getFileName(curPath);
    		String curSubjectid = fileName.split("_")[0];
    		curSubjectid = curSubjectid.substring(0, 6);
    		return curSubjectid;
		}
    	return null;
	}
    
    public String curPoolid() {
    	if (curPath !=null) {
    		String fileName = getFileName(curPath);
    		String curPoolid = fileName.split("_")[0];
    		return curPoolid;
		}
    	return null;
	}
    
    public String curItemid() {
    	if (curPath !=null) {
    		String fileName = getFileName(curPath);
    		String curItemid = fileName.split("_")[1];
    		return curItemid;
		}
    	return null;
	}
    
	@Override
	public IBinder onBind(Intent intent) {
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
