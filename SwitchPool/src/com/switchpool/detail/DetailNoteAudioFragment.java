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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class DetailNoteAudioFragment extends Fragment {

	private DetailActivity ctx;
	private DetailNoteAudioGridAdapter audioGridAdapter;
	private String noteAudioCachePath;
	private List<Note> noteAudioArr;
	private GridView noteGridView;
	private static int section = 1;
	private int curPlayIndex;
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
		// 添加列表项被单击的监听器
        noteGridView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Log.v("sp", ""+position);
				Note note = noteAudioArr.get(position);
				note.setIsPlaying(!note.getIsPlaying());
				if (curPlayIndex < noteAudioArr.size() && curPlayIndex != position) {
					Note curNotenote = noteAudioArr.get(curPlayIndex);
					curNotenote.setIsPlaying(!curNotenote.getIsPlaying());
				}
				
				audioGridAdapter.notifyDataSetChanged();
				
				if (note.getIsPlaying()) {
					ctx.musicPlayer.playAudioNote(note.getPath());
					curPlayIndex = position;
				}
				else {
					ctx.musicPlayer.stop();
					curPlayIndex = noteAudioArr.size();
				}
				
			}
		});
        
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
			}
		}
		audioGridAdapter = new DetailNoteAudioGridAdapter(getActivity(), noteAudioArr, noteGridView);
		noteGridView.setAdapter(audioGridAdapter);
		curPlayIndex = noteAudioArr.size();
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
		
//		noteGridView.setAdapter(new DetailNoteAudioGridAdapter(getActivity(), noteAudioArr, noteGridView));
		audioGridAdapter.notifyDataSetChanged();
	}
	
	public void deleteItem(int index) {
		noteAudioArr.remove(index);
		Utility.shareInstance().saveObject(noteAudioCachePath, noteAudioArr);
		audioGridAdapter.notifyDataSetChanged();
//		noteGridView.setAdapter(new DetailNotePhotoGridAdapter(getActivity(), noteAudioArr, noteGridView));
	}
	
	public void receiveNoteFinishPlayCast() {
		Note note = noteAudioArr.get(curPlayIndex);
		note.setIsPlaying(false);
		audioGridAdapter.notifyDataSetChanged();
		curPlayIndex = noteAudioArr.size();
//		noteGridView.setAdapter(new DetailNotePhotoGridAdapter(getActivity(), noteAudioArr, noteGridView));
	}
	
	public void edit() {
		for (int i = 0; i < noteAudioArr.size(); i++) {
			Note note = noteAudioArr.get(i);
			note.setCanBeDeleted(true);
		}
		audioGridAdapter.notifyDataSetChanged();
//		noteGridView.setAdapter(new DetailNotePhotoGridAdapter(getActivity(), noteAudioArr, noteGridView));
	}

	public void complete() {
		for (int i = 0; i < noteAudioArr.size(); i++) {
			Note note = noteAudioArr.get(i);
			note.setCanBeDeleted(false);
		}
		audioGridAdapter.notifyDataSetChanged();
//		noteGridView.setAdapter(new DetailNotePhotoGridAdapter(getActivity(), noteAudioArr, noteGridView));
	}
}
