package com.switchpool.detail;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.switchpool.model.Note;
import com.switchpool.model.User;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class DetailNoteTextFragment extends Fragment {

	public Note note;
	private String textNoteCachePath;
	private HashMap<String, Note> textNoteCacheMap;
	private DetailActivity ctx;
	
	private EditText textView;
	
	public DetailNoteTextFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_note_text, container,false);
        textView = (EditText) view.findViewById(R.id.editText__detail_note_text);
        ctx = (DetailActivity) getActivity();
        textNoteCachePath = Utility.shareInstance().cacheUserTextNote(ctx.getPoolId());
        if (Utility.shareInstance().isFileExist(textNoteCachePath)) {
        	textNoteCacheMap = (HashMap<String, Note>) Utility.shareInstance().getObject(textNoteCachePath);
		}
        else {
			textNoteCacheMap = new HashMap<String, Note>();
		}
        
        return view;
    }

	public void edit() { 
//		textView.setEnabled(true);
//		textView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		textView.setFocusable(true);
		textView.requestFocus();
		textView.setFocusableInTouchMode(true);
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//		imm.showSoftInput(textView, InputMethodManager.SHOW_FORCED);
	}

	public void complete() {
//		textView.setEnabled(false);
//		textView.setInputType(InputType.TYPE_NULL);
		textView.setFocusable(false);
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
		
		if (note == null) {
			note = new Note();
		}
		
		note.setContent(textView.getText().toString());
		note.setTime(System.currentTimeMillis()/1000);
		note.setSize(note.getContent().length());
		
		User userInfo = (User)Utility.shareInstance().getObject(Utility.shareInstance().userInfoFile());
		RequestParams params = new RequestParams();
		params.put("uid", userInfo.getUid());
		params.put("poolid", ctx.getPoolId());
		params.put("itemid", ctx.getItem().getId());
		params.put("size", note.getSize());
		params.put("content", note.getContent());
		
		saveTextNote(params);
	}

	public void requestNoteText(RequestParams params) {
		Log.v("sp", "param:"+params);
		final Note cacheNote = textNoteCacheMap.get(ctx.getItem().getId());
		
		if (Utility.shareInstance().isNetworkAvailable(ctx)) {
			AsyncHttpClient client = new AsyncHttpClient();
			String url = new String(this.getString(R.string.host) + "model/getNote");
			Log.v("sp", ""+url);
			Log.v("sp", ""+params);
			try {
				client.get(url, params, new JsonHttpResponseHandler() {
					public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
						Log.v("sp", "" + jsonObject);
						if (statusCode == 200) {
							try {
								Note curNote;
								JSONArray fileJsonArray = jsonObject.getJSONArray("notes");
								if (fileJsonArray.length() > 0) {
									JSONObject dict = (JSONObject) fileJsonArray.opt(0);
									curNote = new Note();
									curNote.setContent(dict.getString("content"));
									curNote.setItemid(dict.getString("itemid"));
									curNote.setPoolid(dict.getString("poolid"));
									curNote.setSize(dict.getInt("size"));
									curNote.setTime(dict.getLong("time"));
									note = curNote;
									reload();
									
									textNoteCacheMap.put(ctx.getItem().getId(), note);
									Utility.shareInstance().saveObject(textNoteCachePath, textNoteCacheMap);
								    if (curNote.getContent() != null) {
								    	ctx.noteFragment.textNoteDate = Utility.shareInstance().paserTimeToYMDHMS(System.currentTimeMillis()/1000);
								    	ctx.noteFragment.refreshActionButton();
								    }
								}
								else {
									if (cacheNote != null) {
										note = cacheNote;
										reload();
									}
								}
							} catch (Exception e) {
								Log.e("sp", "" + Log.getStackTraceString(e));
								if (cacheNote != null) {
									note = cacheNote;
									reload();
								}
							}
						}
						else {
							if (cacheNote != null) {
								note = cacheNote;
								reload();
							}
						}
					}
					
					public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
						if (cacheNote != null) {
							note = cacheNote;
							reload();
						}
					}
				});
			} catch (Exception e) {
				Log.e("sp", "" + Log.getStackTraceString(e));
				if (cacheNote != null) {
					note = cacheNote;
					reload();
				}
			}
		}
		else {
			if (cacheNote != null) {
				note = cacheNote;
				reload();
			}
		}
	}
	
	public void saveTextNote(RequestParams params) {
		textNoteCacheMap.put(ctx.getItem().getId(), note);
		Utility.shareInstance().saveObject(textNoteCachePath, textNoteCacheMap);
		
		if (Utility.shareInstance().isNetworkAvailable(ctx)) {
			AsyncHttpClient client = new AsyncHttpClient();
			String url = new String(this.getString(R.string.host) + "model/uploadNote");
			Log.v("sp", ""+url);
			Log.v("sp", ""+params);
			try {
				client.post(url, params, new JsonHttpResponseHandler() {
					public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
						Log.v("sp", "" + jsonObject);
					}
					
					public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
					}
				});
			} catch (Exception e) {
				Log.e("sp", "" + Log.getStackTraceString(e));
			}
		}
	}
	
	public void reload() {
		if (note != null) {
			textView.setText(note.getContent());
		}
	}
	
	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}
}
