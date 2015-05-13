package com.switchpool.detail;

import com.switchpool.model.Model;
import com.switchpool.model.SPFile;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
	public SeekBar seekBar;
	    
    private boolean isPause; 
    private boolean isStartTrackingTouch;
	
    //处理进度条更新
    Handler mHandler = new Handler(){
      @Override  
	    public void handleMessage(Message msg){  
	        switch (msg.what){
	        case 0: 
	        	if (!isStartTrackingTouch) {
	                seekBar.setProgress(ctx.musicPlayer.player.getCurrentPosition());  
	                curTimeTextView.setText(Utility.shareInstance().paserTimeToHMS(ctx.musicPlayer.player.getCurrentPosition()));
				} 
	          break;
	      default:
	          break;
	        }
	    }  
    };
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_audio, container,false);
        ctx = (DetailActivity) getActivity();
        
        playbButton = (ImageButton)view.findViewById(R.id.imageButton_detail_audio_play);
//        forwardButton = (ImageButton)view.findViewById(R.id.imageButton_detail_audio_forward);
//        nextButton = (ImageButton)view.findViewById(R.id.imageButton_detail_audio_next);
        volumeupButton = (ImageButton)view.findViewById(R.id.imageButton_detail_audio_volumeup);
        volumedownButton = (ImageButton)view.findViewById(R.id.imageButton_detail_audio_volumedown);
        seekBar = (SeekBar)view.findViewById(R.id.seekBar_detail_audio);
        playbButton.setOnClickListener(this); 
//        forwardButton.setOnClickListener(this); 
//        nextButton.setOnClickListener(this); 
        volumeupButton.setOnClickListener(this);
        volumedownButton.setOnClickListener(this);
        
        slashTextView = (TextView)view.findViewById(R.id.textView1_detail_audio);
        curTimeTextView = (TextView)view.findViewById(R.id.textView2_detail_audio);
        totalTimeTextView = (TextView)view.findViewById(R.id.textView3_detail_audio);
        downloadProgressTextView = (TextView)view.findViewById(R.id.textView4_detail_audio);
        titleTextView = (TextView)view.findViewById(R.id.textView5_detail_audio);
        
        slashTextView.setVisibility(View.INVISIBLE);
        curTimeTextView.setVisibility(View.INVISIBLE);
        totalTimeTextView.setVisibility(View.INVISIBLE);
        downloadProgressTextView.setVisibility(View.VISIBLE);
          
        //进度条监听器   
        seekBar.setOnSeekBarChangeListener(new MySeekBarListener());   
        
        isStartTrackingTouch = false;
        isPause = false;
        
        return view;
    }

	public void reload(Model resModel) {
		if (resModel != null) {
			model = resModel;
			if (model != null) {
				for (int i = 0; i < model.getFileArr().size(); i++) {
					SPFile curFile = model.getFileArr().get(i);
					if (curFile.getSeq() == 1) {
						file = curFile;
						titleTextView.setText(ctx.getItem().getCaption());
				        slashTextView.setVisibility(View.VISIBLE);
				        curTimeTextView.setVisibility(View.VISIBLE);
				        totalTimeTextView.setVisibility(View.VISIBLE);
				        downloadProgressTextView.setVisibility(View.INVISIBLE);
						ctx.musicPlayer.prepareFile(file);
						curTimeTextView.setText(Utility.shareInstance().paserTimeToHMS(ctx.musicPlayer.player.getCurrentPosition()));
						totalTimeTextView.setText(Utility.shareInstance().paserTimeToHMS(ctx.musicPlayer.player.getDuration()));
						seekBar.setMax(ctx.musicPlayer.player.getDuration());
						
						final int milliseconds = 1000;
					      new Thread(){
					        @Override
					        public void run(){
					          while(true){  
						        try {  
						            sleep(milliseconds);  
						        } catch (InterruptedException e) {  
						            e.printStackTrace();  
						        }
						        mHandler.sendEmptyMessage(0);  
					          }  
					        }
					      }.start();
					}
				}
			}
		}
		else {
			if (ctx.musicPlayer.player.isPlaying() && ctx.musicPlayer.isItemAudio) {
				titleTextView.setText(ctx.getItem().getCaption());
		        slashTextView.setVisibility(View.VISIBLE);
		        curTimeTextView.setVisibility(View.VISIBLE);
		        totalTimeTextView.setVisibility(View.VISIBLE);
		        downloadProgressTextView.setVisibility(View.INVISIBLE);
				curTimeTextView.setText(Utility.shareInstance().paserTimeToHMS(ctx.musicPlayer.player.getCurrentPosition()));
				totalTimeTextView.setText(Utility.shareInstance().paserTimeToHMS(ctx.musicPlayer.player.getDuration()));
				seekBar.setMax(ctx.musicPlayer.player.getDuration());
                seekBar.setProgress(ctx.musicPlayer.player.getCurrentPosition());  
                curTimeTextView.setText(Utility.shareInstance().paserTimeToHMS(ctx.musicPlayer.player.getCurrentPosition()));
                playbButton.setImageResource(R.drawable.detail_audio_pause_selector);
                
				final int milliseconds = 100;
			      new Thread(){
			        @Override
			        public void run(){
			          while(true){  
				        try {  
				            sleep(milliseconds);  
				        } catch (InterruptedException e) {  
				            e.printStackTrace();  
				        }
				        mHandler.sendEmptyMessage(0);  
			          }  
			        }
			      }.start();
			}
		}
	}
	
	public void refreshLoadProgress(int count) {
		downloadProgressTextView.setText(ctx.getString(R.string.detail_note_audio_cacheProgress, count));
		if (count == 100) {
	        seekBar.setProgress(1);
		}
	}
	
	public void receiveAudioFinishPlayCast() {
		seekBar.setProgress(0); 
        slashTextView.setVisibility(View.VISIBLE);
        curTimeTextView.setVisibility(View.VISIBLE);
        totalTimeTextView.setVisibility(View.VISIBLE);
        downloadProgressTextView.setVisibility(View.INVISIBLE);
        playbButton.setImageResource(R.drawable.detail_audio_play_selector);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imageButton_detail_audio_play) {
			playOrPause();
		}
//		else if (v.getId() == R.id.imageButton_detail_audio_forward) {
//			ctx.musicPlayer.previous();
//		}
//		else if (v.getId() == R.id.imageButton_detail_audio_next) {
//			ctx.musicPlayer.next();
//		}
		else if (v.getId() == R.id.imageButton_detail_audio_volumeup) {
			ctx.musicPlayer.volumeUp();
		}
		else if (v.getId() == R.id.imageButton_detail_audio_volumedown) {
			ctx.musicPlayer.volumeDown();
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
            ctx.musicPlayer.player.seekTo(seekBar.getProgress());  
            isStartTrackingTouch = false;  
        }  
    }
    
    private void playOrPause() { 
    	if (ctx.musicPlayer.player.isPlaying()) {
    		ctx.musicPlayer.pause();  
	         isPause = true; 
	         playbButton.setImageResource(R.drawable.detail_audio_play_selector);
		}
    	else {
    		if (file != null) {
    			if (isPause) {
    				ctx.musicPlayer.resume();
    				isPause = false;
    				playbButton.setImageResource(R.drawable.detail_audio_pause_selector);
    			}
    			else {
    				ctx.musicPlayer.playFile(file);
    				playbButton.setImageResource(R.drawable.detail_audio_pause_selector);
    			}
			}
		}
    }
}
