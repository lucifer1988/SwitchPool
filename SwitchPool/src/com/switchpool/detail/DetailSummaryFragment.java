package com.switchpool.detail;

import com.xiaoshuye.switchpool.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class DetailSummaryFragment extends Fragment implements OnClickListener {

	private WebView webView;
	
	public DetailSummaryFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_summary, container,false);
        webView = (WebView) view.findViewById(R.id.webView_detail_summary);
        webView.loadUrl("http://www.baidu.com");
        webView.setWebViewClient(new WebViewClient());
        
        Button backButton = (Button)view.findViewById(R.id.button_detail_summary_back);
        Button homeButton = (Button)view.findViewById(R.id.button_detail_summary_home);
        Button forwardButton = (Button)view.findViewById(R.id.button_detail_summary_forward);
        backButton.setOnClickListener(this); 
        homeButton.setOnClickListener(this); 
        forwardButton.setOnClickListener(this); 
        
        return view;
    }
	
    @Override
    public void onClick(View v) {
        int id  = v.getId();
        switch (id) {
        case R.id.button_detail_summary_back:
        	webView.goBack();
            break;
        case R.id.button_detail_summary_home:
        	webView.loadUrl("http://www.baidu.com");
            break;
        case R.id.button_detail_summary_forward:
        	webView.goForward();
            break;
        default:
            break;
        }
    }
}
