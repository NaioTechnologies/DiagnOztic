package com.naio.diagnostic.activities;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import org.osmdroid.ResourceProxy.bitmap;

import com.naio.diagnostic.R;

import com.naio.diagnostic.opengl.TextureRenderer;
import com.naio.diagnostic.packet.OdometryPacket;
import com.naio.diagnostic.threads.BitmapThread;
import com.naio.diagnostic.threads.ReadSocketThread;

import com.naio.diagnostic.trames.LogTrame;
import com.naio.diagnostic.trames.OdoTrame;
import com.naio.diagnostic.trames.TrameDecoder;
import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.DataManager;
import com.naio.diagnostic.utils.NewMemoryBuffer;
import com.naio.diagnostic.utils.TextureHelper;
import com.naio.opengl.CircleMesh;
import com.naio.opengl.OpenGLRenderer;
import com.naio.opengl.SimplePlane;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Activity that displays the camera of Oz send in the Log socket. It also
 * displays points send in the same socket
 * 
 * @author bodereau
 * 
 */
public class CameraActivity extends FragmentActivity {
	private static final long MILLISECONDS_RUNNABLE = 64; // 64 for 15fps

	private TrameDecoder trameDecoder;

	private Handler handler = new Handler();

	Runnable runnable = new Runnable() {
		public void run() {
			read_the_queue();
		}
	};

	private NewMemoryBuffer memoryBufferLog;
	private ReadSocketThread readSocketThreadLog;
	private ImageView imageview;
	private int nbrImage;
	private ImageView imageview_r;
	private NewMemoryBuffer memoryBufferOdo;
	private ReadSocketThread readSocketThreadOdo;
	private TextView odo_display;
	private ArrayList<float[]> arrayPoints = new ArrayList<float[]>();
	private static float scaleX = 3.5f;
	private static float scaleY = 2.5f;
	private float rapScaleX;
	private float rapScaleY;

	private SimplePlane plane;

	private TextureRenderer renderer;

	private ArrayList<float[]> arrayPoints3d = new ArrayList<float[]>();

	private TextView txt_opengl;

	private boolean stop_the_handler;

	private OdometryPacket ancienPacket = null;

	private byte[] oldPollFifo = null;

	private BitmapThread bitmapThread;

	private GLSurfaceView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.form));
		arrayPoints = new ArrayList<float[]>();
		arrayPoints3d = new ArrayList<float[]>();
		// Remove the title bar from the window.

		// Make the windows into full screen mode.
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// setContentView(R.layout.camera_activity);
		// Create a OpenGL view.
		// GLSurfaceView view = (GLSurfaceView) findViewById(R.id.opengl_view);
		setContentView(R.layout.camera_activity);
		view = (GLSurfaceView) findViewById(R.id.opengl_view);// new
																// GLSurfaceView(this);

		// Creating and attaching the renderer.
		renderer = new TextureRenderer(this);
		view.setEGLContextClientVersion(2);
		view.setRenderer(renderer);
		txt_opengl = (TextView) findViewById(R.id.text_image);
		stop_the_handler = false;
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		final float density = displayMetrics.density;
		view.setOnTouchListener(new OnTouchListener() {
			private float mPreviousX;
			private float mPreviousY;
			private float mDensity = density;

			// These matrices will be used to move and zoom image
			Matrix matrix = new Matrix();
			Matrix savedMatrix = new Matrix();

			// We can be in one of these 3 states
			static final int NONE = 0;
			static final int DRAG = 1;
			static final int ZOOM = 2;
			static final int DRAW = 3;
			int mode = NONE;

			// Remember some things for zooming
			PointF start = new PointF();
			PointF mid = new PointF();
			float oldDist = 1f;

			// Limit zoomable/pannable image
			private float[] matrixValues = new float[9];
			private float maxZoom;
			private float minZoom;
			private float height;
			private float width;
			private RectF viewRect;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event != null) {
					float x = event.getX();
					float y = event.getY();

					/*
					 * if (event.getAction() == MotionEvent.ACTION_MOVE) { if
					 * (renderer != null) { float deltaY = (x - mPreviousX) /
					 * mDensity / 2f; float deltaX = (y - mPreviousY) / mDensity
					 * / 2f;
					 * 
					 * renderer.mDeltaX += deltaX; renderer.mDeltaY += deltaY; }
					 * }
					 */

					switch (event.getAction() & MotionEvent.ACTION_MASK) {
					case MotionEvent.ACTION_DOWN:
						start.set(event.getX(), event.getY());
						mode = DRAG;
						break;
					case MotionEvent.ACTION_POINTER_DOWN:
						oldDist = spacing(event);
						if (oldDist > 10f) {
							midPoint(mid, event);
							mode = ZOOM;
						}
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
						mode = NONE;

						break;
					case MotionEvent.ACTION_MOVE:
						if (mode == DRAW) {
							onTouchEvent(event);
						}
						if (mode == DRAG) {
							if (renderer != null) {
								float deltaY = (x - mPreviousX) / mDensity / 2f;
								float deltaX = (y - mPreviousY) / mDensity / 2f;

								renderer.mDeltaX += deltaX;
								renderer.mDeltaY += deltaY;
							}
						} else if (mode == ZOOM) {
							float newDist = spacing(event);
							Log.e("agabddb", "--" + newDist);
							if (newDist > 10f) {
								float scale = newDist / oldDist;
								Log.e("agabb", "--" + scale);
								renderer.scale += (scale - 1) / 10;
								if (renderer.scale >= 2.2f) {
									renderer.scale = 2.2f;
								}
							}

						}
						break;
					}
					mPreviousX = x;
					mPreviousY = y;
					return true;
				} else {
					return false;
				}
			}

			// *******************Determine the space between the first two
			// fingers
			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return FloatMath.sqrt(x * x + y * y);
			}

			// ************* Calculate the mid point of the first two fingers
			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}
		});
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
			/*
			 * nbrImage = 0; odo_display = (TextView)
			 * findViewById(R.id.odo_text); imageview = (ImageView)
			 * findViewById(R.id.imageview); imageview_r = (ImageView)
			 * findViewById(R.id.imageview_r);
			 */
			/*
			 * imageview.setOnClickListener(new OnClickListener() { boolean
			 * fullsize = false;
			 * 
			 * @Override public void onClick(View v) {
			 * 
			 * if (!fullsize) { v.startAnimation(AnimationUtils.loadAnimation(
			 * v.getContext(), R.animator.animation_imageview)); imageview
			 * .setLayoutParams(new LinearLayout.LayoutParams(
			 * LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			 * 
			 * } else { v.startAnimation(AnimationUtils.loadAnimation(
			 * v.getContext(), R.animator.animation_imageview_return));
			 * imageview .setLayoutParams(new LinearLayout.LayoutParams( 320,
			 * 240));
			 * 
			 * } fullsize = !fullsize; } });
			 * 
			 * imageview_r.setOnClickListener(new OnClickListener() { boolean
			 * fullsize = false;
			 * 
			 * @Override public void onClick(View v) {
			 * 
			 * if (!fullsize) { v.startAnimation(AnimationUtils.loadAnimation(
			 * v.getContext(), R.animator.animation_imageview)); imageview_r
			 * .setLayoutParams(new LinearLayout.LayoutParams(
			 * LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			 * 
			 * } else { v.startAnimation(AnimationUtils.loadAnimation(
			 * v.getContext(), R.animator.animation_imageview_return));
			 * imageview_r .setLayoutParams(new LinearLayout.LayoutParams( 320,
			 * 240));
			 * 
			 * } fullsize = !fullsize; } });
			 */
			trameDecoder = new TrameDecoder();

			memoryBufferLog = new NewMemoryBuffer();

			// memoryBufferOdo = new NewMemoryBuffer();
			readSocketThreadLog = new ReadSocketThread(memoryBufferLog,
					Config.PORT_LOG);

			bitmapThread = new BitmapThread(memoryBufferLog);

			/*
			 * readSocketThreadOdo = new ReadSocketThread(memoryBufferOdo,
			 * Config.PORT_ODO);
			 */

			DataManager.getInstance().setPoints_position_oz("");
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			readSocketThreadLog.start();
			bitmapThread.start();
			// readSocketThreadOdo.start();

			handler.postDelayed(runnable, MILLISECONDS_RUNNABLE);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// DataManager.getInstance().write_in_file(this);
		readSocketThreadLog.setStop(false);
		bitmapThread.quit();
		// renderer = null;
		view.onPause();

		// readSocketThreadOdo.setStop(false);
		handler.removeCallbacks(runnable);
		stop_the_handler = true;

	}

	private void read_the_queue() {
		display_image();
		// display_odo();
		if (!stop_the_handler)
			handler.postDelayed(runnable, MILLISECONDS_RUNNABLE);
	}

	private void display_odo() {
		OdoTrame odo = (OdoTrame) trameDecoder.decode(memoryBufferOdo
				.getPollFifo());
		if (odo != null) {
			odo_display.setText(odo.show());
		}

	}

	private void display_image() {

		String test = DataManager.getInstance().getPollFifoStringOdoPacket();
		txt_opengl.setText(test);

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
}
