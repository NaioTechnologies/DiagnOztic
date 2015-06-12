package com.naio.diagnostic.activities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.naio.diagnostic.R;
import com.naio.diagnostic.opengl.MyGLSurfaceView;
import com.naio.diagnostic.opengl.OpenGLES20Fragment;
import com.naio.diagnostic.threads.SelectorThread;
import com.naio.diagnostic.threads.SendSocketThread;
import com.naio.diagnostic.trames.GPSTrame;
import com.naio.diagnostic.trames.LidarTrame;
import com.naio.diagnostic.trames.LogTrame;
import com.naio.diagnostic.trames.StringTrame;
import com.naio.diagnostic.trames.TrameDecoder;
import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.DataManager;
import com.naio.diagnostic.utils.NewMemoryBuffer;
import com.naio.diagnostic.views.AnalogueView;
import com.naio.diagnostic.views.MyMoveListenerForAnalogueView;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Activity that displays the lidar, the map of the position of oz and a pad to
 * control the motors of oz
 * 
 * @author bodereau
 * 
 */
public class LidarGPSMotorsActivity extends FragmentActivity {
	private static final int MILLISECONDS_RUNNABLE = 64; // 64 for 15fps

	private OpenGLES20Fragment openglfragment;
	private TrameDecoder trameDecoder;
	private NewMemoryBuffer memoryBufferLidar;
	private Handler handler = new Handler();
	private GoogleMap map;
	private MapView maporg;
	private SelectorThread selectorThread;
	private NewMemoryBuffer memoryBufferMap;
	private boolean firstTimeDisplayTheMap;
	private List<LatLng> listPointMap;
	Runnable runnable = new Runnable() {
		public void run() {
			display_txt_handler();
		}
	};

	private NewMemoryBuffer memoryBufferLog;
	private SelectorThread readSocketThreadLog;
	private MapView mapView;
	private MyLocationNewOverlay mMyLocationOverlay;
	private ItemizedIconOverlay<OverlayItem> currentLocationOverlay;
	private ResourceProxyImpl resProxyImpl;
	private ArrayList<GeoPoint> listPointMapView;
	private int idxActuator;
	private int idxMotor;

	private TextView txtLidar1;
	private TextView txtLidar2;
	private TextView txtLidar3;
	private TextView txtLidar4;
	private TextView txtLidar5;
	private TextView txtLidar6;
	private TextView txtLidar7;
	private TextView txtLidar8;
	private TextView txtLidar9;
	private TextView txtLidar10;
	private TextView txtLidar11;
	private TextView txtLidar12;

	private LidarThread lidarThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// change the color of the action bar
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.form));
		setContentView(R.layout.lidar_gps_motors_activity);

		getSupportFragmentManager().addOnBackStackChangedListener(
				new OnBackStackChangedListener() {
					public void onBackStackChanged() {
						int backCount = getSupportFragmentManager()
								.getBackStackEntryCount();
						if (backCount == 0) {
							finish();
						}
					}
				});

		if (savedInstanceState == null) {
			trameDecoder = new TrameDecoder();
			trameDecoder.avoidOdoPacket();
			memoryBufferLidar = new NewMemoryBuffer();
			memoryBufferLog = new NewMemoryBuffer();
			memoryBufferMap = new NewMemoryBuffer();
			listPointMap = new ArrayList<LatLng>();
			listPointMapView = new ArrayList<GeoPoint>();
			firstTimeDisplayTheMap = true;
			txtLidar1 = (TextView) findViewById(R.id.txt_lidar_1);
			txtLidar2 = (TextView) findViewById(R.id.txt_lidar_2);
			txtLidar3 = (TextView) findViewById(R.id.txt_lidar_3);
			txtLidar4 = (TextView) findViewById(R.id.txt_lidar_4);
			txtLidar5 = (TextView) findViewById(R.id.txt_lidar_5);
			txtLidar6 = (TextView) findViewById(R.id.txt_lidar_6);
			txtLidar7 = (TextView) findViewById(R.id.txt_lidar_7);
			txtLidar8 = (TextView) findViewById(R.id.txt_lidar_8);
			txtLidar9 = (TextView) findViewById(R.id.txt_lidar_9);
			txtLidar10 = (TextView) findViewById(R.id.txt_lidar_10);
			txtLidar11 = (TextView) findViewById(R.id.txt_lidar_11);
			txtLidar12 = (TextView) findViewById(R.id.txt_lidar_12);
			
			selectorThread = new SelectorThread(memoryBufferMap,Config.PORT_GPS,SelectorThread.READ);
			selectorThread.addSocket(memoryBufferLidar, Config.PORT_LIDAR,SelectorThread.READ);
			selectorThread.addSocket(memoryBufferLog, Config.PORT_LOG,SelectorThread.READ);
			//TODO : change for this :
			//selectorThread.addSocket(memoryBufferLog, Config.PORT_LOG,SelectorThread.READ);
			 
			idxMotor = selectorThread.addSocket(null, Config.PORT_MOTORS, SelectorThread.WRITE);
			idxActuator = selectorThread.addSocket(null, Config.PORT_ACTUATOR, SelectorThread.WRITE);
			/*readSocketThreadLidar = new ReadSocketThread(memoryBufferLidar,	Config.PORT_LIDAR);
			
			readSocketThreadLog = new ReadSocketThread(memoryBufferLog,	Config.PORT_LOG);
			
			sendSocketThreadMotors = new SendSocketThread(Config.PORT_MOTORS);
			
			sendSocketThreadActuators = new SendSocketThread(Config.PORT_ACTUATOR);*/
			
			DataManager.getInstance().setPoints_position_oz("");
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			// initialize the fragments
			openglfragment = new OpenGLES20Fragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.list, openglfragment).addToBackStack(null)
					.commit();
			resProxyImpl = new ResourceProxyImpl(this);
			mapView = (MapView) findViewById(R.id.map_osm);

			// enable zoom controls
			mapView.setBuiltInZoomControls(true);

			// enable multitouch
			mapView.setMultiTouchControls(true);
			// mapView.setTileSource(TileSourceFactory.MAPNIK);
			/*String m_locale = Locale.getDefault().getISO3Language() + "-"
					+ Locale.getDefault().getISO3Language();
			// String m_locale = Locale.getDefault().getDisplayName();
			BingMapTileSource.retrieveBingKey(this);
			BingMapTileSource bing = new BingMapTileSource(m_locale);

			bing.setStyle(BingMapTileSource.IMAGERYSET_AERIAL);
			mapView.setTileSource(bing);

			GpsMyLocationProvider imlp = new GpsMyLocationProvider(
					this.getBaseContext());
			// minimum distance for update
			imlp.setLocationUpdateMinDistance(1000);
			// minimum time for update
			imlp.setLocationUpdateMinTime(60000);

			mapView.getController().setZoom(18);
			mapView.getController()
					.setCenter(new GeoPoint(43.560597, 1.491833));
			OverlayItem myLocationOverlayItem = new OverlayItem("Here",
					"Naio ", new GeoPoint(43.560597, 1.491833));*/
			/*
			 * Drawable myCurrentLocationMarker =
			 * this.getResources().getDrawable(R.drawable.map_marker_small);
			 * myLocationOverlayItem.setMarker(myCurrentLocationMarker);
			 */

			/*final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
			items.add(myLocationOverlayItem);
			currentLocationOverlay = new ItemizedIconOverlay<OverlayItem>(
					items,
					new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
						public boolean onItemSingleTapUp(final int index,
								final OverlayItem item) {
							return true;
						}

						public boolean onItemLongPress(final int index,
								final OverlayItem item) {
							return true;
						}
					}, resProxyImpl);
			mapView.getOverlays().add(currentLocationOverlay);*/

			set_the_analogueView();
			set_the_dpadView();
			set_the_actuator_button();
			Button changeDisplay = (Button) findViewById(R.id.changePad);
			changeDisplay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AnalogueView analView = (AnalogueView) findViewById(R.id.analogueView1);
					LinearLayout dpadview = (LinearLayout) findViewById(R.id.dpadview);
					if (analView.getVisibility() == View.GONE) {
						analView.setVisibility(View.VISIBLE);
						dpadview.setVisibility(View.GONE);
					} else {
						analView.setVisibility(View.GONE);
						dpadview.setVisibility(View.VISIBLE);
					}

				}
			});

			// start the threads
			//readSocketThreadLidar.start();
			selectorThread.start();
			//readSocketThreadLog.start();
	//		sendSocketThreadMotors.start();
//			sendSocketThreadActuators.start();
			lidarThread = new LidarThread();
			lidarThread.start();
			handler.postDelayed(runnable, MILLISECONDS_RUNNABLE);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		DataManager.getInstance().write_in_file(this);
		//readSocketThreadLidar.setStop(false);
		selectorThread.setStop(false);
		//readSocketThreadLog.setStop(false);
		//sendSocketThreadMotors.setStop(false);
		//sendSocketThreadActuators.setStop(false);
		handler.removeCallbacks(runnable);
		lidarThread.finish();
	}

	private void read_the_queue() {
		display_lidar_info();
		display_gps_info();
		display_lidar_lines();
		//display_txt();
		//handler.postDelayed(runnable, MILLISECONDS_RUNNABLE);
	}
	
	private void display_txt_handler(){
		display_txt();
				handler.postDelayed(runnable, MILLISECONDS_RUNNABLE);
	}

	private void display_txt() {
		trameDecoder.decode(memoryBufferLog.getPollFifo());
		StringTrame[] strTr = trameDecoder.getStringPacket().getStringtrames();
		if(strTr[0] != null)
			txtLidar1.setText(strTr[0].getText());
		if(strTr[1] != null)
			txtLidar2.setText(strTr[1].getText());
		if(strTr[2] != null)
			txtLidar3.setText(strTr[2].getText());
		if(strTr[3] != null)
			txtLidar4.setText(strTr[3].getText());
		if(strTr[4] != null)
			txtLidar5.setText(strTr[4].getText());
		if(strTr[5] != null)
			txtLidar6.setText(strTr[5].getText());
		if(strTr[6] != null)
			txtLidar7.setText(strTr[6].getText());
		if(strTr[7] != null)
			txtLidar8.setText(strTr[7].getText());
		if(strTr[8] != null)
			txtLidar9.setText(strTr[8].getText());
		if(strTr[9] != null)
			txtLidar10.setText(strTr[9].getText());
		if(strTr[10] != null)
			txtLidar11.setText(strTr[10].getText());
		if(strTr[11] != null)
			txtLidar12.setText(strTr[11].getText());
		
	}

	private void display_gps_info() {
		/*GPSTrame gps = (GPSTrame) trameDecoder.decode(memoryBufferMap
				.getPollFifo());*/
		//TODO: change for this :
		trameDecoder.decode(memoryBufferLog.getPollFifo());
		if( trameDecoder.getGpsPacket() != null){
		Log.e("gps","gps values :" + trameDecoder.getGpsPacket().getLat()+"   "+ trameDecoder.getGpsPacket().getLon() +"   "+ trameDecoder.getGpsPacket().getAlt());}

		/*if (gps != null) {
			DecimalFormat df = new DecimalFormat("####.##");
			TextView altitude = (TextView) findViewById(R.id.textview_altitude);
			altitude.setText("Altitude:" + df.format(gps.getAlt()) + " m");
			TextView vitesse = (TextView) findViewById(R.id.textview_groundspeed);
			vitesse.setText("Vitesse:" + df.format(gps.getGroundSpeed())
					+ " km/h");
			DataManager.getInstance().write_in_log(
					"alt and vitesse : " + gps.getAlt() + "---"
							+ gps.getGroundSpeed() + "\n");*/
			/*
			 * map.clear(); LatLng latlng = new LatLng(gps.getLat(),
			 * gps.getLon()); PolylineOptions option = new
			 * PolylineOptions().width(5)
			 * .color(Color.BLUE).addAll(listPointMap); map.addPolyline(option);
			 * 
			 * listPointMap.add(latlng);
			 * DataManager.getInstance().addPoints_position_oz( latlng.latitude
			 * + "#" + latlng.longitude + "%"); map.addMarker(new
			 * MarkerOptions().position(latlng).title("Oz")); if
			 * (firstTimeDisplayTheMap) {
			 * map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18));
			 * firstTimeDisplayTheMap = false; }
			 */
			/*GeoPoint latlng = new GeoPoint(gps.getLat(), gps.getLon());
			OverlayItem myLocationOverlayItem = new OverlayItem("Here", "Oz",
					latlng);

			mapView.getOverlays().clear();
			// listPointMapView.add(latlng);
			RoadManager roadManager = new OSRMRoadManager();
			ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
			items.add(myLocationOverlayItem);
			currentLocationOverlay = new ItemizedIconOverlay<OverlayItem>(
					items,
					new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
						public boolean onItemSingleTapUp(final int index,
								final OverlayItem item) {
							return true;
						}

						public boolean onItemLongPress(final int index,
								final OverlayItem item) {
							return true;
						}
					}, resProxyImpl);
			mapView.getOverlays().add(currentLocationOverlay);
			if (firstTimeDisplayTheMap) {
				mapView.getController().setZoom(18);
				mapView.getController().setCenter(latlng);
				firstTimeDisplayTheMap = false;
			}*/
			// TODO : on affiche vraiment la road ? Plus ça avance et plus elle
			// devient longue à afficher et ça fait tout ramer
			/*
			 * Road road = roadManager.getRoad(listPointMapView); Polyline
			 * roadOverlay = RoadManager.buildRoadOverlay(road, this);
			 * mapView.getOverlays().add(roadOverlay);
			 */
			//mapView.invalidate();
		//}
	}

	private void display_lidar_info() {
		
		//LidarTrame lidar = (LidarTrame) trameDecoder.decode(memoryBufferLidar
				//.getPollFifo());
		
		//TODO : change for this :
		 trameDecoder.decode(memoryBufferLog.getPollFifo());
		
		//if (lidar != null) {
			//TODO : change for this :
			if( trameDecoder.getLidarPacket().getPointtrame() != null){
				if(!trameDecoder.getLidarPacket().getPointtrame().getArrayListPoints1DUInt16().isEmpty()){
			// replace lidar.data_uint16()
			((MyGLSurfaceView) openglfragment.getView())
					.update_with_uint16( DataManager.convertUint16Array(trameDecoder.getLidarPacket().getPointtrame().getArrayListPoints1DUInt16()));
				}
			}
		//}
		
	}

	private void display_lidar_lines() {

		//LogTrame log = (LogTrame) trameDecoder.decode(memoryBufferLog.getPollFifo());
		//TODO : change for this :
		 trameDecoder.decode(memoryBufferLog.getPollFifo());
		/*if (log != null) {
			if (log.getType() == 1)
				((MyGLSurfaceView) openglfragment.getView()).update_line();
		}*/
	}

	private void set_the_analogueView() {
		AnalogueView analView = (AnalogueView) findViewById(R.id.analogueView1);
		int heightTab = getApplicationContext().getResources()
				.getDisplayMetrics().heightPixels;
		analView.setLayoutParams(new LayoutParams(heightTab / 3, heightTab / 3,
				Gravity.CENTER));
		analView.setRADIUS(heightTab / 12);
		analView.setOnMoveListener(new MyMoveListenerForAnalogueView(
				selectorThread,idxMotor));
	}

	private void set_the_dpadView() {
		LinearLayout layoutdpad = (LinearLayout) findViewById(R.id.dpadview);
		int heightTab = getApplicationContext().getResources()
				.getDisplayMetrics().heightPixels;
		layoutdpad.setLayoutParams(new LayoutParams((int) (heightTab / 2.8),
				(int) (heightTab / 2.8), Gravity.CENTER));
		ImageView dpaddown = (ImageView) findViewById(R.id.dpad_down);
		ImageView dpadup = (ImageView) findViewById(R.id.dpad_up);
		ImageView dpadleft = (ImageView) findViewById(R.id.dpad_left);
		ImageView dpadright = (ImageView) findViewById(R.id.dpad_right);
		dpaddown.setOnTouchListener(new View.OnTouchListener() {

			private Handler mHandler;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (mHandler != null)
						return true;
					mHandler = new Handler();
					mHandler.postDelayed(mAction, 20);
					break;
				case MotionEvent.ACTION_UP:
					if (mHandler == null)
						return true;
					mHandler.removeCallbacks(mAction);
					mHandler = null;
					break;
				}
				return false;
			}

			Runnable mAction = new Runnable() {
				@Override
				public void run() {
					byte[] b = new byte[] { 
							78, 65, 73, 79, 48, 49,
							1, 0, 0, 0, 2, 
							0, -100, 
							0, 0, 0, 0 };
					selectorThread.setBytesToWriteForThread(b, idxMotor);
					mHandler.postDelayed(this, 20);
				}
			};
		});

		dpadup.setOnTouchListener(new View.OnTouchListener() {

			private Handler mHandler;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (mHandler != null)
						return true;
					mHandler = new Handler();
					mHandler.postDelayed(mAction, 20);
					break;
				case MotionEvent.ACTION_UP:
					if (mHandler == null)
						return true;
					mHandler.removeCallbacks(mAction);
					mHandler = null;
					break;
				}
				return false;
			}

			Runnable mAction = new Runnable() {
				@Override
				public void run() {
					byte[] b = new byte[] { 
							78, 65, 73, 79, 48, 49,
							1, 0, 0, 0,	2, 
							0, 100, 
							0, 0, 0, 0 };
					selectorThread.setBytesToWriteForThread(b, idxMotor);
					mHandler.postDelayed(this, 20);
				}
			};
		});

		dpadleft.setOnTouchListener(new View.OnTouchListener() {

			private Handler mHandler;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (mHandler != null)
						return true;
					mHandler = new Handler();
					mHandler.postDelayed(mAction, 20);
					break;
				case MotionEvent.ACTION_UP:
					if (mHandler == null)
						return true;
					mHandler.removeCallbacks(mAction);
					mHandler = null;
					break;
				}
				return false;
			}

			Runnable mAction = new Runnable() {
				@Override
				public void run() {
					byte[] b = new byte[] {
							78, 65, 73, 79, 48, 49, 
							1, 0, 0, 0,	2,
							-100, 100,
							0, 0, 0, 0 };
					selectorThread.setBytesToWriteForThread(b, idxMotor);
					mHandler.postDelayed(this, 20);
				}
			};
		});

		dpadright.setOnTouchListener(new View.OnTouchListener() {

			private Handler mHandler;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (mHandler != null)
						return true;
					mHandler = new Handler();
					mHandler.postDelayed(mAction, 20);
					break;
				case MotionEvent.ACTION_UP:
					if (mHandler == null)
						return true;
					mHandler.removeCallbacks(mAction);
					mHandler = null;
					break;
				}
				return false;
			}

			Runnable mAction = new Runnable() {
				@Override
				public void run() {
					byte[] b = new byte[] {
							78, 65, 73, 79, 48, 49,
							1, 0, 0, 0,	2, 
							100, 100, 
							0, 0, 0, 0 };
					selectorThread.setBytesToWriteForThread(b, idxMotor);
					mHandler.postDelayed(this, 20);
				}
			};
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.animator.animation_end2,
				R.animator.animation_end1);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void set_the_actuator_button() {
		Button btn = (Button) findViewById(R.id.actuator_down);
		Button btn2 = (Button) findViewById(R.id.actuator_up);
		btn.setOnTouchListener(new OnTouchListener() {
			byte[] byteDown = new byte[] { 
					78, 65, 73, 79, 48, 49,
					0xf, 1, 0,0, 0, 
					2,
					0, 0, 0, 0 };

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					selectorThread.setBytesToWriteForThread(byteDown, idxActuator);
					break;
				case MotionEvent.ACTION_UP:
					break;
				}
				return false;
			}

		});

		btn2.setOnTouchListener(new OnTouchListener() {

			byte[] byteDown = new byte[] { 
					78, 65, 73, 79, 48, 49,
					0xf, 1, 0,0, 0, 
					1, 
					0, 0, 0, 0 };
			private Handler mHandler;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					selectorThread.setBytesToWriteForThread(byteDown, idxActuator);
					break;
				case MotionEvent.ACTION_UP:
					break;
				}
				return false;
			}

		});
	}
	
	private class LidarThread extends Thread {

		private boolean over = false;

		public void run() {
			while(!over){
				synchronized (memoryBufferLog.lock) {
					try {
						memoryBufferLog.lock.wait(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					read_the_queue();
				}
			}
		}

		public void finish() {
			 over = true;
		}
	}
}
