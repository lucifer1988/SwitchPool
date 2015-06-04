package com.switchpool.detail;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.switchpool.utility.AudioRecorder;
import com.switchpool.utility.Utility;

import com.xiaoshuye.switchpool.R;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DetailNoteFragment extends Fragment implements OnClickListener, OnTouchListener {

	private Button tabButton;
	private Button editButton;
	public Button actionButton;
	
	FragmentManager fManager;
	private int index;
	private DetailActivity ctx;
	
	public DetailNoteTextFragment noteTextFragment;
	public DetailNotePhotoFragment notePhotoFragment;
	public DetailNoteAudioFragment noteAudioFragment;
	
    private static final int MIN_RECORD_TIME = 1; // 最短录制时间，单位秒，0为无时间限制

    private static final int RECORD_OFF = 0; // 不在录音
    private static final int RECORD_ON = 1; // 正在录音
    private String curRecordPath; // 录音文件名
	
    private Dialog mRecordDialog;
    private AudioRecorder mAudioRecorder;
    private Thread mRecordThread;
    private TextView mTvRecordDialogTxt;
    private ImageView mIvRecPhone;
    private ImageView mIvRecVolume;
    private ImageView mIvRecTip;

    private int recordState = 0; // 录音状态
    private float recodeTime = 0.0f; // 录音时长
    private double voiceValue = 0.0; // 录音的音量值
    private boolean moveState = false; // 手指是否移动

    private float downY;
    
    public String textNoteDate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_note, container,false);
        ctx = (DetailActivity) getActivity();
        textNoteDate = "";
        
        tabButton = (Button)view.findViewById(R.id.button_detail_note_tab);
        editButton = (Button)view.findViewById(R.id.button_detail_note_edit);
        actionButton = (Button)view.findViewById(R.id.button_detail_note_action);
        tabButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        actionButton.setBackgroundResource(0);
        index = 0;
        
        fManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fManager.beginTransaction();
        noteAudioFragment = new DetailNoteAudioFragment();
        transaction.add(R.id.relativeLayout_detail_note, noteAudioFragment);
        notePhotoFragment = new DetailNotePhotoFragment();
        transaction.add(R.id.relativeLayout_detail_note, notePhotoFragment);
        noteTextFragment = new DetailNoteTextFragment();
        transaction.add(R.id.relativeLayout_detail_note, noteTextFragment);
        transaction.commit();
        
        return view;
    }
	
	  @Override
	  public boolean onTouch(View v, MotionEvent event) {
		  if (index == 1) {
		      switch (event.getAction()) {
		      case MotionEvent.ACTION_DOWN: // 按下按钮
		          downY = event.getY();
		      break;
		      case MotionEvent.ACTION_MOVE: // 滑动手指
		      float moveY = event.getY();
		      if (moveY - downY > 50) {
		          moveState = true;
		      }
		      if (moveY - downY < 20) {
		          moveState = false;
		      }
		      break;
		     case MotionEvent.ACTION_CANCEL:
		     case MotionEvent.ACTION_UP: // 松开手指
			      if (!moveState){
			    	  notePhotoFragment.takePhoto();
			      }
			      moveState = false;
		          break;
		          }
		      }
		  else if(index == 2) {
			      switch (event.getAction()) {
			      case MotionEvent.ACTION_DOWN: // 按下按钮
			      if (recordState != RECORD_ON) {
			          downY = event.getY();
			          
					 SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");  
					 String timeString=format.format(new Date());
					 Random md = new Random();
					 int randomInt = 100 + md.nextInt(900);
					 String fileName = String.format("%s_%s_31_%s_%d.amr", ctx.getPoolId(), ctx.getItem().getId(), timeString, randomInt);
			          curRecordPath = Utility.shareInstance().cacheUserAudioNote(ctx.getPoolId()) + fileName;
			          
			          mAudioRecorder = new AudioRecorder(curRecordPath);
			          recordState = RECORD_ON;
			          try {
			              mAudioRecorder.start();
			              recordTimethread();
			              showVoiceDialog(0);
			          } catch (IOException e) {
			              e.printStackTrace();
			          }
			      }
			      break;
				  case MotionEvent.ACTION_MOVE: // 滑动手指
				      float moveY = event.getY();
				      Log.v("sp", "downY:"+ downY);
				      Log.v("sp", "moveY:"+ moveY);
				      if (downY - moveY > 50) {
				          moveState = true;
				          showVoiceDialog(1);
				      }
				      if (downY - moveY < 20) {
				          moveState = false;
				          showVoiceDialog(0);
				      }
				      break;
				  case MotionEvent.ACTION_CANCEL:
				  case MotionEvent.ACTION_UP: // 松开手指
				  if (recordState == RECORD_ON) {
				      recordState = RECORD_OFF;
				      if (mRecordDialog.isShowing()) {
				          mRecordDialog.dismiss();
				      }
				      try {
				          mAudioRecorder.stop();
				          mRecordThread.interrupt();
				          voiceValue = 0.0;
				      } catch (IOException e) {
				          e.printStackTrace();
				      }
				
				      if (!moveState) {
				          if (recodeTime < MIN_RECORD_TIME) {
				              showWarnToast("时间太短，录音失败");
				          } 
					      else {
					    	  noteAudioFragment.addNewAudio(curRecordPath, (int) recodeTime);
					      }
				      }
				      moveState = false;
				  }
				  break;
		    }
		}
		return false;
	  }
	    
	    // 录音时显示Dialog
		void showVoiceDialog(int flag) {
		    if (mRecordDialog == null) {
		        mRecordDialog = new Dialog(getActivity(), R.style.DialogStyle);
		        mRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		        mRecordDialog.getWindow().setFlags(
		                WindowManager.LayoutParams.FLAG_FULLSCREEN,
		                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		        mRecordDialog.setContentView(R.layout.detail_note_audio_dialog);
		        mIvRecPhone = (ImageView) mRecordDialog.findViewById(R.id.imageView1_detail_note_audio_dialog);
		        mIvRecVolume = (ImageView) mRecordDialog.findViewById(R.id.imageView2_detail_note_audio_dialog);
		        mTvRecordDialogTxt = (TextView) mRecordDialog.findViewById(R.id.textView_detail_note_audio_dialog);
		        mIvRecTip = (ImageView) mRecordDialog.findViewById(R.id.imageView3_detail_note_audio_dialog);
		    }
		    switch (flag) {
		    case 1:
		    	mIvRecPhone.setVisibility(View.INVISIBLE);
		    	mIvRecVolume.setVisibility(View.INVISIBLE);
		    	mIvRecTip.setImageResource(R.drawable.recordcancel);
		    	mIvRecTip.setVisibility(View.VISIBLE);
		        mTvRecordDialogTxt.setText("松开手指，取消录音");
		    break;
		
		    default:
		    	mIvRecPhone.setVisibility(View.VISIBLE);
		    	mIvRecVolume.setVisibility(View.VISIBLE);
		    	mIvRecTip.setVisibility(View.INVISIBLE);
		        mTvRecordDialogTxt.setText("向上滑动，取消录音");
		        break;
		    }
		    mTvRecordDialogTxt.setTextSize(14);
		    mRecordDialog.show();
		}
	
	// 录音时间太短时Toast显示
	void showWarnToast(String toastText) {
	    Toast toast = new Toast(getActivity());
	    LinearLayout linearLayout = new LinearLayout(getActivity());
	    linearLayout.setOrientation(LinearLayout.VERTICAL);
	    linearLayout.setPadding(30, 30, 30, 30);
	
		// 定义一个ImageView
		ImageView imageView = new ImageView(getActivity());
		imageView.setImageResource(R.drawable.messagetooshort); // 图标
		imageView.setPadding(10, 10, 10, 0);
		
		TextView mTv = new TextView(getActivity());
		mTv.setText(toastText);
		mTv.setTextSize(13);
		mTv.setTextColor(Color.WHITE);// 字体颜色
		mTv.setPadding(10, 0, 10, 10);
		
		// 将ImageView和ToastView合并到Layout中
		linearLayout.addView(imageView);
		linearLayout.addView(mTv);
		linearLayout.setGravity(Gravity.CENTER);// 内容居中
		linearLayout.setBackgroundResource(R.drawable.voice_rcd_hint_bg);// 设置自定义toast的背景
		
		toast.setView(linearLayout);
		toast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间
		toast.show();
	}
	
	// 录音计时线程
	void recordTimethread() {
	    mRecordThread = new Thread(recordThread);
	    mRecordThread.start();
	}
	
	// 录音Dialog图片随声音大小切换
	void setDialogImage() {
	    if (voiceValue < 600.0) {
	        mIvRecVolume.setImageResource(R.drawable.recordingsignal001);
	    } else if (voiceValue > 600.0 && voiceValue < 1000.0) {
	        mIvRecVolume.setImageResource(R.drawable.recordingsignal001);
	    } else if (voiceValue > 1000.0 && voiceValue < 1200.0) {
	        mIvRecVolume.setImageResource(R.drawable.recordingsignal002);
	    } else if (voiceValue > 1200.0 && voiceValue < 1400.0) {
	        mIvRecVolume.setImageResource(R.drawable.recordingsignal002);
	    } else if (voiceValue > 1400.0 && voiceValue < 1600.0) {
	        mIvRecVolume.setImageResource(R.drawable.recordingsignal003);
	    } else if (voiceValue > 1600.0 && voiceValue < 1800.0) {
	        mIvRecVolume.setImageResource(R.drawable.recordingsignal003);
	    } else if (voiceValue > 1800.0 && voiceValue < 2000.0) {
	        mIvRecVolume.setImageResource(R.drawable.recordingsignal004);
	    } else if (voiceValue > 2000.0 && voiceValue < 3000.0) {
	        mIvRecVolume.setImageResource(R.drawable.recordingsignal004);
	    } else if (voiceValue > 3000.0 && voiceValue < 4000.0) {
	        mIvRecVolume.setImageResource(R.drawable.recordingsignal005);
	    } else if (voiceValue > 4000.0 && voiceValue < 6000.0) {
	        mIvRecVolume.setImageResource(R.drawable.recordingsignal005);
	    } else if (voiceValue > 6000.0 && voiceValue < 8000.0) {
	        mIvRecVolume.setImageResource(R.drawable.recordingsignal006);
	    } else if (voiceValue > 8000.0 && voiceValue < 10000.0) {
	        mIvRecVolume.setImageResource(R.drawable.recordingsignal006);
	    } else if (voiceValue > 10000.0 && voiceValue < 12000.0) {
	        mIvRecVolume.setImageResource(R.drawable.recordingsignal007);
	    } else if (voiceValue > 12000.0) {
	        mIvRecVolume.setImageResource(R.drawable.recordingsignal008);
	    }
	}
	
	// 录音线程
	private Runnable recordThread = new Runnable() {
	
	    @Override
	    public void run() {
	        recodeTime = 0.0f;
	        while (recordState == RECORD_ON) {
	            // 限制录音时长
	    // if (recodeTime >= MAX_RECORD_TIME && MAX_RECORD_TIME != 0) {
	    // imgHandle.sendEmptyMessage(0);
	    // } else
	    {
	        try {
	            Thread.sleep(150);
	            recodeTime += 0.15;
	            // 获取音量，更新dialog
	                    if (!moveState) {
	                        voiceValue = mAudioRecorder.getAmplitude();
	                        recordHandler.sendEmptyMessage(1);
	                    }
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	};
	
	public Handler recordHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        setDialogImage();
	    }
	};
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_detail_note_tab) {
	    	index++;
	    	if (index > 2) {
				index = 0;
			}
	    	FragmentTransaction transaction = fManager.beginTransaction();
	        switch (index) {
	        case 0: {
	        	transaction.hide(notePhotoFragment);
	        	transaction.hide(noteAudioFragment);
	        	transaction.show(noteTextFragment);
//	        	actionButton.setVisibility(View.INVISIBLE);
	        }
	            break;
	        case 1: {
	        	transaction.hide(noteTextFragment);
	        	transaction.hide(noteAudioFragment);
	        	transaction.show(notePhotoFragment);
//	        	actionButton.setImageResource(R.drawable.detail_note_camera);
//	        	actionButton.setVisibility(View.VISIBLE);
	        	notePhotoFragment.reload();
	        }
	            break;
	        case 2: {
	        	transaction.hide(noteTextFragment);
	        	transaction.hide(notePhotoFragment);
	        	transaction.show(noteAudioFragment);
//	        	actionButton.setImageResource(R.drawable.detail_note_record);
//	        	actionButton.setVisibility(View.VISIBLE);
	        	noteAudioFragment.reload();
	        }
	            break;
	        }
	        transaction.commit();
	        
	        refreshActionButton();
		}
//		else if (v.getId() == R.id.button_detail_note_action) {
//			switch (index) {
//			case 1:{
//				notePhotoFragment.takePhoto();
//			}
//				break;
//			case 2:{
//				noteAudioFragment.startRecord();
//			}
//				break;
//			default:
//				break;
//			}
//		}
		else if (v.getId() == R.id.button_detail_note_edit) {
			if (editButton.getText().equals(getActivity().getString(R.string.detail_note_edit))) {
				editButton.setText(getActivity().getString(R.string.detail_note_complete));
				switch (index) {
				case 0:{
					noteTextFragment.edit();
				}
					break;
				case 1:{
					notePhotoFragment.edit();
				}
					break;
				case 2:{
					noteAudioFragment.edit();
				}
					break;
				}
			}
			else {
				editButton.setText(getActivity().getString(R.string.detail_note_edit));
				switch (index) {
				case 0:{
					noteTextFragment.complete();
				}
					break;
				case 1:{
					notePhotoFragment.complete();
				}
					break;
				case 2:{
					noteAudioFragment.complete();
				}
					break;
				}
			}
		}

	}
	
	public void refreshActionButton() {
		switch (index) {
		case 0:{
			actionButton.setBackgroundResource(0);
			Log.v("sp", "textNoteDate:"+textNoteDate);
			actionButton.setTextColor(R.color.grey);
			actionButton.setText(textNoteDate);
		}
			break;

		case 1:{
			actionButton.setBackgroundResource(R.drawable.detail_note_camera);
			actionButton.setTextColor(R.color.white);
			actionButton.setText(R.string.camera);
		}
			break;
			
		case 2:{
			actionButton.setBackgroundResource(R.drawable.detail_note_record);
			actionButton.setTextColor(R.color.white);
			actionButton.setText(R.string.record);
		}
			break;
		}
	}

}
