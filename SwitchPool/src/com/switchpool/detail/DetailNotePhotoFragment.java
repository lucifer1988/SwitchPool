package com.switchpool.detail;

import com.xiaoshuye.switchpool.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DetailNotePhotoFragment extends Fragment {

	private DetailActivity ctx;
			
	public DetailNotePhotoFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_note_photo, container,false);
        
        ctx = (DetailActivity) getActivity();
//        textNoteCachePath = Utility.shareInstance().cacheUserTextNote(ctx.getPoolId());
//        if (Utility.shareInstance().isFileExist(textNoteCachePath)) {
//        	textNoteCacheMap = (HashMap<String, Note>) Utility.shareInstance().getObject(textNoteCachePath);
//		}
//        else {
//			textNoteCacheMap = new HashMap<String, Note>();
//		}
        
        return view;
    }
	
	public void addNewPhoto(String path) {
		Log.v("sp", ""+path);
	}
	
	public void takePhoto() {
		ctx.takePhoto();
	}

	public void edit() {
		// TODO Auto-generated method stub
		
	}

	public void complete() {
		// TODO Auto-generated method stub
		
	}

}
