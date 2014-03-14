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
import android.util.Log;

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
		Log.i("service", "onCreate");
		thd = new Thread(this);
		Log.i("service", thd.getName());
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		floatWindow = new FloatWindow(getApplicationContext());
		floatWindow.showFloatWindow();
		mBroadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String lat = intent.getStringExtra("lat");
				String lng = intent.getStringExtra("lng");
				SharedPreferences.Editor lastCoorEdit =  getSharedPreferences("data", 0).edit();
				lastCoorEdit.putString("lastCoor", lat+","+lng);
				lastCoorEdit.commit();
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
		currentLocation = new Location("gps");
		currentLocation.setLatitude(Location.convert(bundle.getString("lat")));
		currentLocation.setLongitude(Location.convert(bundle.getString("lng")));
		currentLocation.setTime(System.currentTimeMillis());
		currentLocation.setAccuracy(1);
		currentLocation.setAltitude(65);
		if (!thd.isAlive()) {
			thd.start();
		}
		Log.i("serviceonstart",
				bundle.getString("lat") + "," + bundle.getString("lng"));
	}

	public void onDestory() {
		Log.i("service", "onDestory");
		thd.interrupt();
		unregisterReceiver(mBroadcastReceiver);
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
			Log.i("coor",
					currentLocation.getLatitude() + ","
							+ currentLocation.getLongitude());
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
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}