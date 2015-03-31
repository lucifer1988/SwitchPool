package com.switchpool.detail;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xiaoshuye.switchpool.R;

import com.switchpool.model.Note;
import com.switchpool.utility.Utility;
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
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder;
		if (convertView == null) {
			mViewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.detail_note_gridcell, parent, false);
			mViewHolder.mImageView = (ImageView) convertView
					.findViewById(R.id.imageView_detail_note_gridcell);
			mViewHolder.mTextView1 = (TextView) convertView.findViewById(R.id.textView1_detail_note_gridcell);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		Note note = list.get(position);
		String path = list.get(position).getPath();
		mViewHolder.mImageView.setTag(path);
		 
		mViewHolder.mImageView.setImageResource(R.drawable.nocontent_tip);
		mViewHolder.mTextView1.setText(Utility.shareInstance().paserTimeToHM(note.getTime()));
		
		return convertView;
	}
	

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder mHeaderHolder;
		if (convertView == null) {
			mHeaderHolder = new HeaderViewHolder();
			convertView = mInflater.inflate(R.layout.detail_note_gridheader, parent, false);
			mHeaderHolder.mTextView = (TextView) convertView
					.findViewById(R.id.TextView_detail_note_gridheader);
			mHeaderHolder.line = (LinearLayout)convertView.findViewById(R.id.linearLayout_detail_note_gridheader);
			convertView.setTag(mHeaderHolder);
		} else {
			mHeaderHolder = (HeaderViewHolder) convertView.getTag();
		}

		mHeaderHolder.mTextView.setText(Utility.shareInstance().paserTimeToYM(list.get(position).getTime()));

		return convertView;
	}

	public static class ViewHolder {
		public ImageView mImageView;
		public TextView mTextView1;
	}

	public static class HeaderViewHolder {
		public TextView mTextView;
		public LinearLayout line;
	}

	@Override
	public long getHeaderId(int position) {
		return list.get(position).getSection();
	}

}
