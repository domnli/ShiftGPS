package com.domnli.shiftgps;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;

public class GPSService extends Service implements Runnable {

	private LocationManager locationManager;
	private Location currentLocation;
	private Thread thd;
	private BroadcastReceiver mBroadcastReceiver;
	private FloatWindow floatWindow;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		thd = new Thread(this);
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		currentLocation = new Location("gps");
		floatWindow = new FloatWindow(getApplicationContext(), new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				String tag = v.getTag().toString();
				if(tag == "up"){
					currentLocation.setLatitude(currentLocation.getLatitude() + 0.001);
				}
				if(tag == "down"){
					currentLocation.setLatitude(currentLocation.getLatitude() - 0.001);
				}
				if(tag == "left"){
					currentLocation.setLongitude(currentLocation.getLongitude() - 0.001);
				}
				if(tag == "right"){
					currentLocation.setLongitude(currentLocation.getLongitude() + 0.001);
				}
				return false;
			}
		});
		floatWindow.showFloatWindow();
		mBroadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				
			}

		};
		IntentFilter intentFilter = new IntentFilter(MainActivity.class
				.getPackage().getName() + ".NOTIFY");
		this.registerReceiver(mBroadcastReceiver, intentFilter);
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Bundle bundle = intent.getBundleExtra("bundle");
		currentLocation.setLatitude(Location.convert(bundle.getString("lat")));
		currentLocation.setLongitude(Location.convert(bundle.getString("lng")));
		currentLocation.setTime(System.currentTimeMillis());
		currentLocation.setAccuracy(1);
		currentLocation.setAltitude(65);
		if (!thd.isAlive()) {
			thd.start();
		}
	}

	public void onDestory() {
		thd.interrupt();
		unregisterReceiver(mBroadcastReceiver);
		String lat = currentLocation.getLatitude() + "";
		String lng = currentLocation.getLongitude() + "";
		SharedPreferences.Editor lastCoorEdit =  getSharedPreferences("data", 0).edit();
		lastCoorEdit.putString("lastCoor", lat+","+lng);
		lastCoorEdit.commit();
		super.onDestroy();
	}

	@Override
	public void run() {
		while (true) {
			try {
				locationManager.addTestProvider("gps", false,
						false, false, false, false, false, false, 1, 1);
				locationManager.setTestProviderEnabled("gps", true);
				locationManager.addTestProvider("network", false,
						false, false, false, false, false, false, 1, 1);
				locationManager.setTestProviderEnabled("network", true);
			} catch (IllegalArgumentException localIllegalArgumentException) {
				while (true) {
					this.locationManager.removeTestProvider("gps");
					this.locationManager.removeTestProvider("network");
					this.locationManager.addTestProvider("gps", false, false,
							false, false, false, false, false, 1, 1);
					this.locationManager.addTestProvider("network", false, false,
							false, false, false, false, false, 1, 1);
				}
			}
		/*	currentLocation.setLatitude(currentLocation.getLatitude() + 0.5
					- Math.random());
			currentLocation.setLongitude(currentLocation.getLongitude() + 0.5
					- Math.random());*/
			Intent intent = new Intent(MainActivity.class.getPackage()
					.getName() + ".NOTIFY");
			intent.putExtra("lat",
					Location.convert(currentLocation.getLatitude(), 0));
			intent.putExtra("lng",
					Location.convert(currentLocation.getLongitude(), 0));
			this.sendBroadcast(intent);
			locationManager.setTestProviderLocation("gps",
					currentLocation);
			locationManager.setTestProviderLocation("network",
					currentLocation);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}