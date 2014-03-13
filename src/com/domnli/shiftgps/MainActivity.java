package com.domnli.shiftgps;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init("zDFbauXsup4nGdgdz9erEwuF", null);
		// 注意：请在试用setContentView前初始化BMapManager对象，否则会报错
		setContentView(R.layout.activity_main);
		mMapView = (MapView) findViewById(R.id.bmapsView);
		txtCoords = (EditText) findViewById(R.id.txtCoords);
		btnStart = (Button) findViewById(R.id.btnStart);
		btnStop = (Button) findViewById(R.id.btnStop);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// 设置启用内置的缩放控件
		mMapView.setBuiltInZoomControls(true);
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		mMapController = mMapView.getController();
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
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
				String coordsStr = txtCoords.getText().toString();
				updateLocation(coordsStr);
			}
		});

		btnStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		
	}

	private String coorFormat(GeoPoint point) {
		String lat; // 纬度
		String lon; // 经度
		lat = point.getLatitudeE6() / 1E6 + "";
		lon = point.getLongitudeE6() / 1E6 + "";
		return lat + "," + lon;
	}

	private void updateLocation(String coordsStr) {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
		super.onResume();
	}

}
