package com.domnli.shiftgps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class FloatWindow {
	Context context;
	WindowManager wm;
	MasterButton masterButton;
	FrameLayout optionPanel;
	
	public FloatWindow(Context mcontext){
		context = mcontext;
		wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		masterButton = new MasterButton();
		initOptionPanel();
		
	}
	
	private void initOptionPanel() {
		// TODO Auto-generated method stub
		
	}
	
	public void showFloatWindow(){
		wm.addView(masterButton.getButton(), masterButton.getLayoutParams());
	}
	
	
	public void removeFloatWindow(){
		wm.removeView(masterButton.getButton());
	}
	
	/**
	 *
	 * 按钮、布局参数 包装 
	 */
	private class MasterButton{
		private Button btn;
		private LayoutParams params;
		private boolean isOpen;
		
		public MasterButton(){
			btn = new Button(context);
			params = new LayoutParams();
			isOpen = false;
			
			params.type = LayoutParams.TYPE_SYSTEM_ALERT;
			params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
			params.width = 100;
			params.height = 100;
			
			btn.setText("展开");
			
			addMasterButtonListener();
		}
		public LayoutParams getLayoutParams() {
			return params;
		}
		public Button getButton() {
			return this.btn;
		}
		private void addMasterButtonListener(){
			btn.setOnTouchListener(new View.OnTouchListener() {
				int lastX, lastY;  
		        int paramX, paramY;
		        
				@SuppressLint("NewApi")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch(event.getAction()) {  
		            case MotionEvent.ACTION_DOWN:  
		                lastX = (int) event.getRawX();  
		                lastY = (int) event.getRawY();  
		                paramX = params.x;  
		                paramY = params.y;  
		                Log.i("down",(int)event.getRawX()+","+(int)event.getRawY());
		                break;  
		            case MotionEvent.ACTION_MOVE:  
		                int dx = (int) event.getRawX() - lastX;  
		                int dy = (int) event.getRawY() - lastY;  
		                params.x = paramX + dx;  
		                params.y = paramY + dy;  
		                // 更新悬浮窗位置  
		                wm.updateViewLayout(btn, params);
		                Log.i("move",(int)event.getRawX()+","+(int)event.getRawY());
		                break;
		            case MotionEvent.ACTION_UP:
		            	int ux = (int) event.getRawX() - lastX;  
		                int uy = (int) event.getRawY() - lastY;
		                Log.i("up",(int)event.getRawX()+","+(int)event.getRawY());
		                if(ux < 1 & uy < 1){
		                	if(isOpen){
		    					Toast.makeText(context, "正在关闭", Toast.LENGTH_LONG).show();
		    					isOpen = false;
		    				}else{
		    					Toast.makeText(context, "正在展开", Toast.LENGTH_LONG).show();
		    					isOpen = true;
		    				}
		                }
		            	break;
		            }
					
		            return true;  
				}
			});
		}
	}
}
