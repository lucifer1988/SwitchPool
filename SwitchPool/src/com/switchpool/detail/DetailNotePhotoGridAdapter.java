package com.switchpool.detail;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.switchpool.model.Note;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

public class DetailNotePhotoGridAdapter extends BaseAdapter implements
		StickyGridHeadersSimpleAdapter {

	private List<Note> list;
	private LayoutInflater mInflater;
	private GridView mGridView;
	
	public DetailNotePhotoGridAdapter(Context context, List<Note> list,
			GridView mGridView) {
		this.list = list;
		mInflater = LayoutInflater.from(context);
		this.mGridView = mGridView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getHeaderId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
