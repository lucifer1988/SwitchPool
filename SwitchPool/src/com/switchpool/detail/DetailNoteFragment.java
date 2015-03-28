package com.switchpool.detail;

import com.xiaoshuye.switchpool.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class DetailNoteFragment extends Fragment implements OnClickListener {

	private Button tabButton;
	private Button editButton;
	private ImageButton actionButton;
	
	FragmentManager fManager;
	private int index;
	
	public DetailNoteTextFragment noteTextFragment;
	public DetailNotePhotoFragment notePhotoFragment;
	public DetailNoteAudioFragment noteAudioFragment;
	
	public DetailNoteFragment() {
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_note, container,false);
        
        tabButton = (Button)view.findViewById(R.id.button_detail_note_tab);
        editButton = (Button)view.findViewById(R.id.button_detail_note_edit);
        actionButton = (ImageButton)view.findViewById(R.id.button_detail_note_action);
        actionButton.setVisibility(View.INVISIBLE);
        tabButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        actionButton.setOnClickListener(this);
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
	        	actionButton.setVisibility(View.INVISIBLE);
	        }
	            break;
	        case 1: {
	        	transaction.hide(noteTextFragment);
	        	transaction.hide(noteAudioFragment);
	        	transaction.show(notePhotoFragment);
	        	actionButton.setImageResource(R.drawable.detail_note_camera);
	        	actionButton.setVisibility(View.VISIBLE);
	        }
	            break;
	        case 2: {
	        	transaction.hide(noteTextFragment);
	        	transaction.hide(notePhotoFragment);
	        	transaction.show(noteAudioFragment);
	        	actionButton.setImageResource(R.drawable.detail_note_record);
	        	actionButton.setVisibility(View.VISIBLE);
	        }
	            break;
	        }
	        transaction.commit();
		}
		else if (v.getId() == R.id.button_detail_note_action) {
			switch (index) {
			case 1:{
				notePhotoFragment.takePhoto();
			}
				break;
			case 2:{
				noteAudioFragment.startRecord();
			}
				break;
			default:
				break;
			}
		}
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

}
