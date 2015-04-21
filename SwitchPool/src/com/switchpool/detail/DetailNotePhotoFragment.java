package com.switchpool.detail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.switchpool.model.Note;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class DetailNotePhotoFragment extends Fragment {

	private DetailActivity ctx;
	private String notePhotoCachePath;
	private List<Note> notePhotoArr;
	private GridView noteGridView;
	private static int section = 1;
	private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
	List<String> filePaths;
			
	public DetailNotePhotoFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_note_photo, container,false);
        noteGridView = (GridView)view.findViewById(R.id.gridView_detail_note_photo);
        
        ctx = (DetailActivity) getActivity();
		// 添加列表项被单击的监听器
        noteGridView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Log.v("sp", ""+position);
				imageBrower(position);
			}
		});
        
        filePaths = new ArrayList<String>();
        
        return view;
    }
	
	private void imageBrower(int position) {
		if (filePaths != null && filePaths.size() > 0) {
			String[] urls = new String[filePaths.size()];
			for (int i = 0; i < filePaths.size(); i++) {
				urls[i] = filePaths.get(i);
			}
			Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
			getActivity().startActivity(intent);
		}
	}
	
	public void reload() {
		notePhotoCachePath = Utility.shareInstance().cacheUserPhotoNote(ctx.getPoolId()) + ctx.getItem().getId();
		Log.v("sp", ""+notePhotoCachePath);
		notePhotoArr = (List<Note>)Utility.shareInstance().getObject(notePhotoCachePath);
		if (notePhotoArr == null) {
			notePhotoArr = new ArrayList<Note>();
		}

		if (notePhotoArr.size() > 0) {
			Collections.sort(notePhotoArr, new DetailNoteComparator());
	        for (int i = 0; i < notePhotoArr.size(); i++) {
	        	Note note = notePhotoArr.get(i);
				note.setCanBeDeleted(false);
				String ym = Utility.shareInstance().paserTimeToYM(note.getTime());
				
				filePaths.add(note.getPath());
				
				if(!sectionMap.containsKey(ym)){
					note.setSection(section);
					sectionMap.put(ym, section);
					section ++;
				}else{
					note.setSection(sectionMap.get(ym));
				}
			}
		}
		noteGridView.setAdapter(new DetailNotePhotoGridAdapter(getActivity(), notePhotoArr, noteGridView));
	}
	
	public void addNewPhoto(String path) {
		Log.v("sp", ""+path);
		Note note = new Note();
		
		note.setPath(path);
		note.setPoolid(ctx.getPoolId());
		note.setItemid(ctx.getItem().getId());
		note.setTime(System.currentTimeMillis()/1000);
		
		File dF = new File(path); 
		FileInputStream fis;
		try {
			fis = new FileInputStream(dF);
			note.setSize(fis.available());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
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
		notePhotoArr.add(note);
		filePaths.add(note.getPath());
		Utility.shareInstance().saveObject(notePhotoCachePath, notePhotoArr);
		
		noteGridView.setAdapter(new DetailNotePhotoGridAdapter(getActivity(), notePhotoArr, noteGridView));
	}
	
	public void deleteItem(int index) {
		notePhotoArr.remove(index);
		filePaths.remove(index);
		Utility.shareInstance().saveObject(notePhotoCachePath, notePhotoArr);
		noteGridView.setAdapter(new DetailNotePhotoGridAdapter(getActivity(), notePhotoArr, noteGridView));
	}
	
	public void takePhoto() {
		ctx.takePhoto();
	}

	public void edit() {
		for (int i = 0; i < notePhotoArr.size(); i++) {
			Note note = notePhotoArr.get(i);
			note.setCanBeDeleted(true);
		}
		noteGridView.setAdapter(new DetailNotePhotoGridAdapter(getActivity(), notePhotoArr, noteGridView));
	}

	public void complete() {
		for (int i = 0; i < notePhotoArr.size(); i++) {
			Note note = notePhotoArr.get(i);
			note.setCanBeDeleted(false);
		}
		noteGridView.setAdapter(new DetailNotePhotoGridAdapter(getActivity(), notePhotoArr, noteGridView));
	}
}
