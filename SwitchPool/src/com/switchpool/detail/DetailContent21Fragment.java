package com.switchpool.detail;

import com.switchpool.model.Model;
import com.switchpool.model.SPFile;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DetailContent21Fragment extends Fragment {

	private WebView webView;
	public Model model;
	private SPFile file;
	private String url;
	
	public DetailContent21Fragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_content21, container,false);
        webView = (WebView) view.findViewById(R.id.webView_detail_content21);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setUseWideViewPort(true); 
        webView.getSettings().setLoadWithOverviewMode(true); 
        
        if (url != null) {
			webView.loadUrl(url);
		}
        
        Utility.shareInstance().showWaitingHUD(getActivity());
        
        return view;
    }
	
	public void reload(Model resModel) {
		Utility.shareInstance().hideWaitingHUD();
		model = resModel;
		if (model != null) {
			for (int i = 0; i < model.getFileArr().size(); i++) {
				SPFile curFile = model.getFileArr().get(i);
				if (curFile.getSeq() == 1) {
					file = curFile;
					url = "file://"+file.getPath();
					Log.v("sp", ""+url);
					webView.loadUrl(url);
				}
			}
		}
	}
	
	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
}
