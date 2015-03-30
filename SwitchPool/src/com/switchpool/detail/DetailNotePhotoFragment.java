package com.switchpool.detail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.switchpool.model.Note;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class DetailNotePhotoFragment extends Fragment {

	private DetailActivity ctx;
	private String notePhotoCachePath;
	private List<Note> notePhotoArr;
	private GridView noteGridView;
	private static int section = 1;
	private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
			
	public DetailNotePhotoFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_note_photo, container,false);
        noteGridView = (GridView)view.findViewById(R.id.gridView_detail_note_photo);
        
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
	
	public void reload() {
		notePhotoCachePath = Utility.shareInstance().cacheUserPhotoNote(ctx.getPoolId()) + ctx.getItem().getId();
		if (Utility.shareInstance().isFileExist(notePhotoCachePath)) {
			notePhotoArr = (List<Note>)Utility.shareInstance().getObject(notePhotoCachePath);
		}
		else {
			notePhotoArr = new ArrayList<Note>();
		}
		if (notePhotoArr.size() > 0) {
			Collections.sort(notePhotoArr, new DetailNoteComparator());
			
			for(ListIterator<Note> it = notePhotoArr.listIterator(); it.hasNext();){
				Note note = it.next();
				String ym = Utility.shareInstance().paserTimeToYM(note.getTime());
				if(!sectionMap.containsKey(ym)){
					note.setSection(section);
					sectionMap.put(ym, section);
					section ++;
				}else{
					note.setSection(sectionMap.get(ym));
				}
			}
			
			noteGridView.setAdapter(new DetailNotePhotoGridAdapter(getActivity(), notePhotoArr, noteGridView));
		}
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
