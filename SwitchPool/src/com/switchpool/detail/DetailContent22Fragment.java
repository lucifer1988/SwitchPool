package com.switchpool.detail;

import com.switchpool.model.Model;
import com.switchpool.model.SPFile;
import com.switchpool.utility.Utility;
import com.xiaoshuye.switchpool.R;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class DetailContent22Fragment extends Fragment {

	private ImageView imageView;
	public Model model;
	private SPFile file;
	private String url;
	
	public DetailContent22Fragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_content22, container,false);
        imageView = (ImageView) view.findViewById(R.id.imageView_detail_content22); 
        
        if (url != null) {
            Bitmap bitmap = Utility.shareInstance().getLoacalBitmap(url);
            imageView .setImageBitmap(bitmap); 
		}
        
        return view;
    }
	
	public void reload(Model resModel) {
		model = resModel;
		if (model != null) {
			for (int i = 0; i < model.getFileArr().size(); i++) {
				SPFile curFile = model.getFileArr().get(i);
				if (curFile.getSeq() == 1) {
					file = curFile;
					url = file.getPath();
					Log.v("sp", ""+url);
		            Bitmap bitmap = Utility.shareInstance().getLoacalBitmap(url);
		            imageView .setImageBitmap(bitmap); 
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
