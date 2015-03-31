package com.switchpool.detail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

public class DetailNoteAudioFragment extends Fragment {

	private DetailActivity ctx;
	private String noteAudioCachePath;
	private List<Note> noteAudioArr;
	private GridView noteGridView;
	private static int section = 1;
	private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
	
	public DetailNoteAudioFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_note_audio, container,false);
        noteGridView = (GridView)view.findViewById(R.id.gridView_detail_note_audio);
        
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
		noteAudioCachePath = Utility.shareInstance().cacheUserAudioNote(ctx.getPoolId()) + ctx.getItem().getId();
		Log.v("sp", ""+noteAudioCachePath);
		noteAudioArr = (List<Note>)Utility.shareInstance().getObject(noteAudioCachePath);
		if (noteAudioArr == null) {
			noteAudioArr = new ArrayList<Note>();
		}

		if (noteAudioArr.size() > 0) {
			Collections.sort(noteAudioArr, new DetailNoteComparator());
			
			for(ListIterator<Note> it = noteAudioArr.listIterator(); it.hasNext();){
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
		}
		noteGridView.setAdapter(new DetailNoteAudioGridAdapter(getActivity(), noteAudioArr, noteGridView));
	}
	
	public void addNewAudio(String path, int length) {
		Log.v("sp", ""+path);
		Note note = new Note();
		
		note.setPath(path);
		note.setPoolid(ctx.getPoolId());
		note.setItemid(ctx.getItem().getId());
		note.setTime(System.currentTimeMillis()/1000);
		note.setSize(length);
		
		note.setCanBeDeleted(false);
		note.setIsPlaying(false);
		String ym = Utility.shareInstance().paserTimeToYM(note.getTime());
		if(!sectionMap.containsKey(ym)){
			note.setSection(section);
			sectionMap.put(ym, section);
			section ++;
		}else{
			note.setSection(sectionMap.get(ym));
		}
		noteAudioArr.add(note);
		Utility.shareInstance().saveObject(noteAudioCachePath, noteAudioArr);
		
		noteGridView.setAdapter(new DetailNoteAudioGridAdapter(getActivity(), noteAudioArr, noteGridView));
	}
	
	public void edit() {
		// TODO Auto-generated method stub
		
	}

	public void complete() {
		// TODO Auto-generated method stub
		
	}

	public void startRecord() {
		// TODO Auto-generated method stub
		
	}

}
