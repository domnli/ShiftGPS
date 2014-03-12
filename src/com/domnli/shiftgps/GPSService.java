package com.domnli.shiftgps;

import java.util.Iterator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSService extends Service implements Runnable {

	private LocationManager locationManager;
	private String mMockProviderName = "gps";
	private Location currentLocation;
	private Thread thd;

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
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		Bundle bundle = intent.getBundleExtra("bundle");
		currentLocation = new Location(mMockProviderName);
		currentLocation.setLatitude(Location.convert(bundle.getString("lat")));
		currentLocation.setLongitude(Location.convert(bundle.getString("lng")));
		currentLocation.setTime(System.currentTimeMillis());
		if (!thd.isAlive()) {
			thd.start();
		}
		Log.i("serviceonstart",
				bundle.getString("lat") + "," + bundle.getString("lng"));
	}

	public void onDestory() {
		Log.i("service", "onDestory");
		thd.interrupt();
		super.onDestroy();
	}

	@Override
	public void run() {
		while (true) {
			try {
				locationManager.addTestProvider(mMockProviderName, false,
						false, false, false, false, false, false, 1, 1);
				locationManager.setTestProviderEnabled(mMockProviderName, true);
			} catch (IllegalArgumentException localIllegalArgumentException) {
				while (true) {
					this.locationManager.removeTestProvider("gps");
					this.locationManager.addTestProvider("gps", false, false,
							false, false, false, false, false, 1, 1);
				}
			}
			currentLocation.setLatitude(currentLocation.getLatitude() + 0.5
					- Math.random());
			currentLocation.setLongitude(currentLocation.getLongitude() + 0.5
					- Math.random());
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
			locationManager.setTestProviderLocation(mMockProviderName,
					currentLocation);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}