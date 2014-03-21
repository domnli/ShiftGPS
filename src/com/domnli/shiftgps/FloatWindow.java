package com.domnli.shiftgps;

import java.util.HashMap;

import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class FloatWindow {
	Context context;
	WindowManager wm;
	MasterButton masterButton;
	OptionPanel optionPanel;
	
	public FloatWindow(Context mcontext){
		context = mcontext;
		wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		masterButton = new MasterButton();
		optionPanel = new OptionPanel();
		masterButton.addMasterButtonListener(optionPanel);
		
	}
	
	public FloatWindow(Context mcontext,OnTouchListener onTouchListener){
		context = mcontext;
		wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		masterButton = new MasterButton();
		optionPanel = new OptionPanel(onTouchListener);
		masterButton.addMasterButtonListener(optionPanel);
		
	}
	
	public void showFloatWindow(){
		masterButton.show();
	}
	
	
	public void removeFloatWindow(){
		wm.removeView(masterButton.getButton());
	}
	
	/**
	 *
	 * 按钮、布局参数 包装 
	 */
	private class MasterButton{
		private FrameLayout view;
		private ImageButton btn;
		private LayoutParams viewParams;
		private boolean isOpen = false;
		
		public MasterButton(){
			view = new FrameLayout(context);
			btn = new ImageButton(context);
			viewParams = new LayoutParams();
			FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(62,62);
			isOpen = false;
			
			viewParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
			viewParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
			viewParams.width = 64;
			viewParams.height = 64;
			viewParams.x = 0;
			viewParams.y = 0;
			viewParams.format = android.graphics.PixelFormat.RGBA_8888;//背景透明
			
			btn.setBackgroundColor(android.graphics.Color.TRANSPARENT);
			btn.setImageResource(R.drawable.ic_floaticon);
			view.addView(btn, btnParams);
			
			//addMasterButtonListener();
		}
		public void show() {
			wm.addView(view, viewParams);
			
		}
		public boolean isOpen(){
			return isOpen;
		}
		public LayoutParams getLayoutParams() {
			return viewParams;
		}
		public ImageButton getButton() {
			return this.btn;
		}
		public void addMasterButtonListener(final OptionPanel panel){
			btn.setOnTouchListener(new View.OnTouchListener() {
				int lastX, lastY;  
		        int paramX, paramY;
		        
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch(event.getAction()) {  
		            case MotionEvent.ACTION_DOWN:  
		                lastX = (int) event.getRawX();  
		                lastY = (int) event.getRawY();  
		                paramX = viewParams.x;  
		                paramY = viewParams.y;  
		                Log.i("down",(int)event.getRawX()+","+(int)event.getRawY());
		                break;  
		            case MotionEvent.ACTION_MOVE:  
		                int dx = (int) event.getRawX() - lastX;  
		                int dy = (int) event.getRawY() - lastY;  
		                viewParams.x = paramX + dx;  
		                viewParams.y = paramY + dy;  
		                // 更新悬浮窗位置  
		                wm.updateViewLayout(view, viewParams);
		                Log.i("move",(int)event.getRawX()+","+(int)event.getRawY());
		                break;
		            case MotionEvent.ACTION_UP:
		            	int ux = (int) event.getRawX() - lastX;  
		                int uy = (int) event.getRawY() - lastY;
		                Log.i("up",(int)event.getRawX()+","+(int)event.getRawY());
		                if(Math.abs(ux) < 1 & Math.abs(uy) < 1){
		                	if(isOpen){
		    					isOpen = false;
		    					panel.close();
		    				}else{
		    					isOpen = true;
		    					panel.open();
		    				}
		                }
		            	break;
		            }
					return true;
				}
			});
		}
	}
	
	public class OptionPanel{
		
		private static final int ARROW_UP = 0;
		private static final int ARROW_DOWN = 1;
		private static final int ARROW_LEFT = 2;
		private static final int ARROW_RIGHT = 3;

		private FrameLayout layout;
		private LayoutParams wmParams;
		private HashMap<String,ArrowButton> buttons;
		private OnTouchListener arrowTouchListener;
		
		public OptionPanel(){
			init();
		}
		
		public OptionPanel(OnTouchListener l){
			arrowTouchListener = l;
			init();
		}

		public void init(){
			layout = new FrameLayout(context);
			wmParams = new LayoutParams();
			layout.setBackgroundColor(android.graphics.Color.WHITE);
			wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT - 1;
			wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
			wmParams.width = 300;
			wmParams.height = 300;
			wmParams.alpha = 0.8f;
			
			buttons = new HashMap<String, ArrowButton>();
			initButtons();
			
			if(arrowTouchListener == null){
				arrowTouchListener = new OnTouchListener(){

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						return false;
					}
					
				};
			}
		}
		
		private void initButtons(){
			createArrowButton(OptionPanel.ARROW_UP);
			createArrowButton(OptionPanel.ARROW_DOWN);
			createArrowButton(OptionPanel.ARROW_LEFT);
			createArrowButton(OptionPanel.ARROW_RIGHT);
		}
		
/*		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					Log.i("touch","down");
					mPress.pressDown();
					break;
				case MotionEvent.ACTION_UP:
					Log.i("touch","up");
					mPress.pressUp();
					break;
			}
			return true;
		}*/
		
		private void createArrowButton(int key){
			int panelWidth = wmParams.width;
			int panelHeight = wmParams.height;
			int btnWidth = 100;
			int btnHeight = 100;
			String keyStr = null;
			ImageButton btnArrow = new ImageButton(context);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(btnWidth,btnHeight);
			switch(key){
				case OptionPanel.ARROW_UP:
					btnArrow.setImageResource(R.drawable.up);
					//params.setMargins(left, top, right, bottom)
					params.setMargins(panelWidth/2-btnWidth/2, 10, 0, 0);
					keyStr = "up";
					break;
				case OptionPanel.ARROW_DOWN:
					btnArrow.setImageResource(R.drawable.down);
					params.setMargins(panelWidth/2-btnWidth/2, panelHeight-btnHeight-10, 0, 0);
					keyStr = "down";
					break;
				case OptionPanel.ARROW_LEFT:
					btnArrow.setImageResource(R.drawable.left);
					params.setMargins(10, panelHeight/2-btnHeight/2, 0, 0);
					keyStr = "left";
					break;
				case OptionPanel.ARROW_RIGHT:
					btnArrow.setImageResource(R.drawable.right);
					params.setMargins(panelWidth-btnWidth-10, panelHeight/2-btnHeight/2, 0, 0);
					keyStr = "right";
					break;
			}
			btnArrow.setTag(keyStr);
			btnArrow.setOnTouchListener(this.arrowTouchListener);
			
			ArrowButton arrowButton = new ArrowButton();
			arrowButton.setButton(btnArrow);
			arrowButton.setParams(params);
			buttons.put(keyStr, arrowButton);
			layout.addView(btnArrow, params);
			
		}
		public void close() {
			wm.removeView(layout);
		}

		public void open() {
			wm.addView(layout, wmParams);
		}
		
		public class ArrowButton{
			private ImageButton button;
			private FrameLayout.LayoutParams params;
			public ImageButton getButton() {
				return button;
			}
			public void setButton(ImageButton button) {
				this.button = button;
			}
			public FrameLayout.LayoutParams getParams() {
				return params;
			}
			public void setParams(FrameLayout.LayoutParams params) {
				this.params = params;
			}
		}
		
	}
}
