package com.domnli.shiftgps;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MKMapStatusChangeListener;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MainActivity extends Activity {
	BMapManager mBMapMan = null;
	MapController mMapController = null;
	LocationManager locationManager = null;
	GPSService service = null;
	MapView mMapView = null;
	EditText txtCoords = null;
	Button btnStart = null;
	Button btnStop = null;
	BroadcastReceiver mBroadcastReceiver = null;
	boolean mockOpen = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init("zDFbauXsup4nGdgdz9erEwuF", null);// 使用setContentView前初始化BMapManager对象，否则会报错

		setContentView(R.layout.activity_main);
		mMapView = (MapView) findViewById(R.id.bmapsView);
		txtCoords = (EditText) findViewById(R.id.txtCoords);
		btnStart = (Button) findViewById(R.id.btnStart);
		btnStop = (Button) findViewById(R.id.btnStop);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		mMapView.setBuiltInZoomControls(true);// 设置启用内置的缩放控件
		mMapController = mMapView.getController();// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		loadLastCoorFromData();//加载最后一次位置
		
		// mMapView.setSatellite(true); //卫星图
		
		mMapView.regMapStatusChangeListener(new MKMapStatusChangeListener() {

			@Override
			public void onMapStatusChange(MKMapStatus arg0) {
				txtCoords.setText(coorFormat(mMapView.getMapCenter()));

			}

		});
		btnStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateLocation();
			}
		});

		btnStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onStart(){
		super.onStart();
	}
	@Override
	protected void onDestroy() {
		mMapView.destroy();
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		if (mBMapMan != null) {
			mBMapMan.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		if (mBMapMan != null) {
			mBMapMan.start();
		}
		setMockEnable();
		loadLastCoorFromData();
		super.onResume();
	}
	
	private String coorFormat(GeoPoint point) {
		String lat; // 纬度
		String lon; // 经度
		lat = point.getLatitudeE6() / 1E6 + "";
		lon = point.getLongitudeE6() / 1E6 + "";
		return lat + "," + lon;
	}

	private void updateLocation() {
		String coordsStr = txtCoords.getText().toString();
		String[] coor = coordsStr.split(",");
		if (coor.length == 2) {
			Bundle bundle = new Bundle();
			bundle.putString("lat", coor[0]);
			bundle.putString("lng", coor[1]);
			Intent service = new Intent(MainActivity.this, GPSService.class);
			service.putExtra("bundle", bundle);
			startService(service);
		}

	}
	
	private void loadLastCoorFromData(){
		SharedPreferences lastCoor = getSharedPreferences("data", 0);
		String coorStr = lastCoor.getString("lastCoor","39.915,116.404");
		int zoom = lastCoor.getInt("lastZoom", 12); 
		String[] coor = coorStr.split(",");
		GeoPoint point = new GeoPoint(
				(int) (Double.valueOf(coor[0]) * 1E6),
				(int) (Double.valueOf(coor[1]) * 1E6));
		mMapController.setCenter(point);// 设置地图中心点
		mMapController.setZoom(zoom);// 设置地图zoom级别
		txtCoords.setText(coorFormat(point));
	}
	
	private void setMockEnable(){
		 ContentResolver cv = this.getContentResolver();
		 int status = 0;
		 try {
			Settings.Secure.putInt(cv, "mock_location", 1);
		 } catch (Exception e) {
			e.printStackTrace();
		 }
		 try {
			status = Settings.Secure.getInt(cv, "mock_location");
		 } catch (SettingNotFoundException e) {
			e.printStackTrace();
		 }
		 if(status == 0) {
			 Toast.makeText(getApplicationContext(), "请打开允许模拟位置选项", Toast.LENGTH_LONG).show();
			 startActivity(new Intent().setClassName("com.android.settings", "com.android.settings.DevelopmentSettings"));
		 }else if(status == 1){
			 mockOpen = true;
		 }
		 Log.i("canMock",status+"");
	}

}
