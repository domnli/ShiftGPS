package com.domnli.shiftgps;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

public class FloatWindow {
	Context context;
	WindowManager wm;
	LayoutParams param;
	View view;
	
	public FloatWindow(Context mcontext){
		context = mcontext;
		wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		initLayoutParams();
	}
	
	public void initLayoutParams(){
		LayoutParams param = new LayoutParams();
		param.type = LayoutParams.TYPE_SYSTEM_ALERT;
		param.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
		param.width = 100;
		param.height = 100;
		
		setParams(param);
	}
	
	public void setParams(LayoutParams param){
		this.param = param;
	}
	
	public View initButton(){
		Button btn = new Button(context);
		btn.setText("Ðü¸¡´°");
		return btn;
	}
	
	public void createFloatWindow(){
		wm.addView(view, param);
	}
	
	public void removeFloatWindow(){
		wm.removeView(view);
	}
}
