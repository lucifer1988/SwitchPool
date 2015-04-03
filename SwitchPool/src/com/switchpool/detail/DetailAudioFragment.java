package com.switchpool.detail;

import com.switchpool.model.Model;
import com.switchpool.model.SPFile;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class DetailAudioFragment extends Fragment implements OnClickListener {

	private DetailActivity ctx;
	
	public Model model;
	private SPFile file;
	
	ImageButton playbButton, forwardButton, nextButton, volumeupButton, volumedownButton;
	TextView curTimeTextView, totalTimeTextView, downloadProgressTextView, titleTextView, slashTextView;
	public SeekBar seekBar;
	
	private int current;    
    private boolean isPause;  
	
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
        
        slashTextView.setVisibility(View.INVISIBLE);
        curTimeTextView.setVisibility(View.INVISIBLE);
        totalTimeTextView.setVisibility(View.INVISIBLE);
        downloadProgressTextView.setVisibility(View.VISIBLE);
          
        //进度条监听器   
//        seekBar.setOnSeekBarChangeListener(new MySeekBarListener());   
        
        return view;
    }

	public void reload(Model resModel) {
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
				}
			}
		}
	}
	
	public void refreshLoadProgress(int count) {
		downloadProgressTextView.setText(ctx.getString(R.string.detail_note_audio_cacheProgress, count));
		if (count == 100) {
	        seekBar.setProgress(1);
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imageButton_detail_audio_play) {
			playOrPause();
		}
	}
	
//	private final class MySeekBarListener implements OnSeekBarChangeListener {  
//        //移动触发   
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {  
//        }  
//  
//        //起始触发   
//        public void onStartTrackingTouch(SeekBar seekBar) {  
//            isStartTrackingTouch = true;  
//        }  
//  
//        //结束触发   
//        public void onStopTrackingTouch(SeekBar seekBar) {  
//            player.seekTo(seekBar.getProgress());  
//            isStartTrackingTouch = false;  
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
    
//	 private void resume() {  
//        if (isPause) {  
//            player.start();  
//            isPause = false;  
//        }  
//	 }  
//	      
//	 private void pause() {  
//	     if (player != null && player.isPlaying()) {  
//	         player.pause();  
//	         isPause = true;  
//	     }  
//     } 
}
