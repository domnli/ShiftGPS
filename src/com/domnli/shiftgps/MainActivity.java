package com.domnli.shiftgps;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

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
		// ע�⣺��������setContentViewǰ��ʼ��BMapManager���󣬷���ᱨ��
		setContentView(R.layout.activity_main);
		mMapView = (MapView) findViewById(R.id.bmapsView);
		txtCoords = (EditText) findViewById(R.id.txtCoords);
		btnStart = (Button) findViewById(R.id.btnStart);
		btnStop = (Button) findViewById(R.id.btnStop);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// �����������õ����ſؼ�
		mMapView.setBuiltInZoomControls(true);
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		mMapController = mMapView.getController();
		// �ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
		GeoPoint point = new GeoPoint((int) (39.915 * 1E6),
				(int) (116.404 * 1E6));
		mMapController.setCenter(point);// ���õ�ͼ���ĵ�
		mMapController.setZoom(12);// ���õ�ͼzoom����
		txtCoords.setText(coorFormat(point));
		// mMapView.setSatellite(true); //����ͼ

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
				String[] coor = coordsStr.split(",");
				Log.i("click", coor[0] + "," + coor[1]);
				updateLocation(coor);
			}
		});

		btnStop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		mBroadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String lat = intent.getStringExtra("lat");
				String lng = intent.getStringExtra("lng");
				GeoPoint point = new GeoPoint(
						(int) (Double.valueOf(lat) * 1E6),
						(int) (Double.valueOf(lng) * 1E6));
				txtCoords.setText(lat + "," + lng);
				mMapController.setCenter(point);
			}

		};
		IntentFilter intentFilter = new IntentFilter(MainActivity.class
				.getPackage().getName() + ".NOTIFY");
		this.registerReceiver(mBroadcastReceiver, intentFilter);
	}

	private String coorFormat(GeoPoint point) {
		String lat; // γ��
		String lon; // ����
		lat = point.getLatitudeE6() / 1E6 + "";
		lon = point.getLongitudeE6() / 1E6 + "";
		return lat + "," + lon;
	}

	private void updateLocation(String[] coor) {
		String latLng;
		if (coor.length == 2) {
			latLng = "Latitude:" + coor[0] + "  Longitude:" + coor[1];
			Bundle bundle = new Bundle();
			bundle.putString("lat", coor[0]);
			bundle.putString("lng", coor[1]);
			Intent service = new Intent(MainActivity.this, GPSService.class);
			service.putExtra("bundle", bundle);
			startService(service);
		} else {
			latLng = "Can't access your location";
		}
		Log.i("updateLocation", "The location has changed..");
		Log.i("updateLocation", "Your Location:" + latLng);

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
		unregisterReceiver(mBroadcastReceiver);
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
