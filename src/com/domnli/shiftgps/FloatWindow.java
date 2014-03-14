package com.domnli.shiftgps;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

public class FloatWindow {
	Context context;
	WindowManager wm;
	LayoutParams params;
	Button masterButton;
	Button[] arrowButtons;
	boolean isOpen = false;
	
	public FloatWindow(Context mcontext){
		context = mcontext;
		wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		initLayoutParams();
		initButton();
	}
	
	public void initLayoutParams(){
		LayoutParams param = new LayoutParams();
		param.type = LayoutParams.TYPE_SYSTEM_ALERT;
		param.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
		param.width = 100;
		param.height = 100;
		param.alpha = 0.5f;
		setParams(param);
	}
	
	public void setParams(LayoutParams param){
		this.params = param;
	}
	
	private void initButton(){
		Button btn = new Button(context);
		btn.setText("展开");
		this.masterButton = btn;
	}
	
	public void showFloatWindow(){
		addListener();
		wm.addView(masterButton, params);
	}
	private void addListener(){
		masterButton.setOnTouchListener(new View.OnTouchListener() {
			int lastX, lastY;  
	        int paramX, paramY;
	        
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {  
	            case MotionEvent.ACTION_DOWN:  
	                lastX = (int) event.getRawX();  
	                lastY = (int) event.getRawY();  
	                paramX = params.x;  
	                paramY = params.y;  
	                break;  
	            case MotionEvent.ACTION_MOVE:  
	                int dx = (int) event.getRawX() - lastX;  
	                int dy = (int) event.getRawY() - lastY;  
	                params.x = paramX + dx;  
	                params.y = paramY + dy;  
	                // 更新悬浮窗位置  
	                wm.updateViewLayout(masterButton, params);  
	                break;  
	            }  
	            return true;  
			}
		});
		masterButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isOpen){
					
				}else{
					
				}
			}
		});
	}
	public void removeFloatWindow(){
		wm.removeView(masterButton);
	}
}
