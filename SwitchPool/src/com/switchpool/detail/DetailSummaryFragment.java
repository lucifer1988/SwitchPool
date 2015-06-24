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
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class DetailSummaryFragment extends Fragment implements OnClickListener {

	private DetailActivity ctx;
	
	private WebView webView;
	public Model model;
	private SPFile file;
	private String url;
	
	public DetailSummaryFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		ctx = (DetailActivity)getActivity();
		
        View view = inflater.inflate(R.layout.detail_summary, container,false);
        webView = (WebView) view.findViewById(R.id.webView_detail_summary);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setUseWideViewPort(true); 
        webView.getSettings().setLoadWithOverviewMode(true);
        
        Button backButton = (Button)view.findViewById(R.id.button_detail_summary_back);
        Button homeButton = (Button)view.findViewById(R.id.button_detail_summary_home);
        Button forwardButton = (Button)view.findViewById(R.id.button_detail_summary_forward);
        backButton.setOnClickListener(this); 
        homeButton.setOnClickListener(this); 
        forwardButton.setOnClickListener(this); 
        
        return view;
    }
	
	public void reload(Model resModel) {
		model = resModel;
		Utility.shareInstance().hideWaitingHUD();
		if (model != null) {
			Log.v("sp", "model"+model.getFileArr());
			for (int i = 0; i < model.getFileArr().size(); i++) {
				SPFile curFile = model.getFileArr().get(i);
				Log.v("sp", "curFile:"+curFile.getSeq());
				if (curFile.getSeq() == 1) {
					file = curFile;
					url = "file://"+file.getPath();
					Log.v("sp", ""+url);
					webView.loadUrl(url);
					ctx.hideNoContent();
					return ;
				}
			}
		}
		ctx.showNoContent();
	}
	
    @Override
    public void onClick(View v) {
        int id  = v.getId();
        switch (id) {
        case R.id.button_detail_summary_back:
        	webView.goBack();
            break;
        case R.id.button_detail_summary_home:
        	if (url != null) {
        		webView.loadUrl(url);
			}
            break;
        case R.id.button_detail_summary_forward:
        	webView.goForward();
            break;
        default:
            break;
        }
    }

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
    
    
}
