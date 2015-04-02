package com.switchpool.detail;

import java.util.List;

import com.switchpool.model.Model;
import com.switchpool.model.SPFile;
import com.switchpool.utility.MusicPlayer;
import com.xiaoshuye.switchpool.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class DetailAudioFragment extends Fragment implements OnClickListener {

	private DetailActivity ctx;
	
	public Model model;
	private SPFile file;
	
	ImageButton playbButton, forwardButton, nextButton, volumeupButton, volumedownButton;
	TextView curTimeTextView, totalTimeTextView, downloadProgressTextView, titleTextView, slashTextView;
	SeekBar seekBar;
	
	private int current;  
    private MediaPlayer player;  
    private Handler handler = new Handler(); 
    private boolean isPause;  
    private boolean isStartTrackingTouch; 
	
	public DetailAudioFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_audio, container,false);
        ctx = (DetailActivity) getActivity();
        
        playbButton = (ImageButton)view.findViewById(R.id.imageButton_detail_audio_play);
        forwardButton = (ImageButton)view.findViewById(R.id.imageButton_detail_audio_forward);
        nextButton = (ImageButton)view.findViewById(R.id.imageButton_detail_audio_next);
        volumeupButton = (ImageButton)view.findViewById(R.id.imageButton_detail_audio_volumeup);
        volumedownButton = (ImageButton)view.findViewById(R.id.imageButton_detail_audio_volumedown);
        seekBar = (SeekBar)view.findViewById(R.id.seekBar_detail_audio);
        playbButton.setOnClickListener(this); 
        forwardButton.setOnClickListener(this); 
        nextButton.setOnClickListener(this); 
        volumeupButton.setOnClickListener(this);
        volumedownButton.setOnClickListener(this);
        
        slashTextView = (TextView)view.findViewById(R.id.textView1_detail_audio);
        curTimeTextView = (TextView)view.findViewById(R.id.textView2_detail_audio);
        totalTimeTextView = (TextView)view.findViewById(R.id.textView3_detail_audio);
        downloadProgressTextView = (TextView)view.findViewById(R.id.textView4_detail_audio);
        titleTextView = (TextView)view.findViewById(R.id.textView5_detail_audio);
        
//        //创建一个音乐播放器   
//        player = new MediaPlayer();   
//        //进度条监听器   
//        seekBar.setOnSeekBarChangeListener(new MySeekBarListener());  
//        //播放器监听器   
//        player.setOnCompletionListener(new MyPlayerListener());
//        //意图过滤器   
//        IntentFilter filter = new IntentFilter();   
//        //播出电话暂停音乐播放   
//        filter.addAction("Android.intent.action.NEW_OUTGOING_CALL");  
//        ctx.registerReceiver(new PhoneListener(), filter);
//        //创建一个电话服务   
//        TelephonyManager manager = (TelephonyManager) ctx.getSystemService(ctx.TELEPHONY_SERVICE);
//        //监听电话状态，接电话时停止播放   
//        manager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);  
        
        return view;
    }

	public void reload(Model resModel) {
		model = resModel;
		if (model != null) {
			for (int i = 0; i < model.getFileArr().size(); i++) {
				SPFile curFile = model.getFileArr().get(i);
				if (curFile.getSeq() == 1) {
					file = curFile;
//					url = "file://"+file.getPath();
//					Log.v("sp", ""+url);
//					webView.loadUrl(url);
				}
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imageButton_detail_audio_play) {
			playOrPause();
		}
	}
	
	private final class MySeekBarListener implements OnSeekBarChangeListener {  
        //移动触发   
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {  
        }  
  
        //起始触发   
        public void onStartTrackingTouch(SeekBar seekBar) {  
            isStartTrackingTouch = true;  
        }  
  
        //结束触发   
        public void onStopTrackingTouch(SeekBar seekBar) {  
            player.seekTo(seekBar.getProgress());  
            isStartTrackingTouch = false;  
        }  
    }
	
//	private final class MyPlayerListener implements OnCompletionListener {  
//        //歌曲播放完后自动播放下一首歌曲   
//        public void onCompletion(MediaPlayer mp) {  
//            next();  
//        }  
//    }
	
//	private final class PhoneListener extends BroadcastReceiver {  
//        public void onReceive(Context context, Intent intent) {  
//            pause();  
//        }  
//    } 
//
//	private final class MyPhoneStateListener extends PhoneStateListener {  
//        public void onCallStateChanged(int state, String incomingNumber) {  
//            pause();  
//        }  
//    } 
	
//	private void previous() {  
//        current = current - 1 < 0 ? data.size() - 1 : current - 1;  
//        play();  
//    }  
//  
//      
//    private void next() {  
//        current = (current + 1) % data.size();  
//        play();  
//    } 
    
    private void playOrPause() {  
    	ctx.musicPlayer.playFile(file);
    }
    
	 private void resume() {  
        if (isPause) {  
            player.start();  
            isPause = false;  
        }  
	 }  
	  
	      
	 private void pause() {  
	     if (player != null && player.isPlaying()) {  
	         player.pause();  
	         isPause = true;  
	     }  
     } 
}
