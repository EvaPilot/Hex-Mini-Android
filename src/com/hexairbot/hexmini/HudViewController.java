package com.hexairbot.hexmini;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

//import com.parrot.freeflight.gestures.EnhancedGestureDetector;
import com.hexairbot.hexmini.gestures.EnhancedGestureDetector;
import com.hexairbot.hexmini.ui.Button;
import com.hexairbot.hexmini.ui.Image;
import com.hexairbot.hexmini.ui.Indicator;
import com.hexairbot.hexmini.ui.JoystickBase;
import com.hexairbot.hexmini.ui.Sprite;
import com.hexairbot.hexmini.ui.Text;
import com.hexairbot.hexmini.ui.ToggleButton;
import com.hexairbot.hexmini.ui.VideoStageRenderer;
import com.hexairbot.hexmini.ui.Image.SizeParams;
import com.hexairbot.hexmini.ui.Sprite.Align;
//import com.parrot.freeflight.utils.FontUtils.TYPEFACE;
//import com.parrot.freeflight.video.VideoStageRenderer;
//import com.parrot.freeflight.video.VideoStageView;

public class HudViewController 
	implements OnTouchListener,
			   OnGestureListener
{
	public enum JoystickType {
		NONE,
		ANALOGUE,
		ACCELERO,
		COMBINED,
	}

	private static final String TAG = "HudViewController";

//	private static final int JOY_ID_LEFT = 1;
//	private static final int JOY_ID_RIGHT = 2;
//	private static final int ALERT_ID = 3;
//	private static final int TAKE_OFF_ID = 4;
//	private static final int TOP_BAR_ID = 5;
//	private static final int BOTTOM_BAR_ID = 6;
//	private static final int CAMERA_ID = 7;
//	private static final int RECORD_ID = 8;
//	private static final int PHOTO_ID = 9;
//	private static final int SETTINGS_ID = 10;
//	private static final int BATTERY_INDICATOR_ID = 11;
//	private static final int WIFI_INDICATOR_ID = 12;
//	private static final int EMERGENCY_LABEL_ID = 13;
//	private static final int BATTERY_STATUS_LABEL_ID = 14;
//	private static final int RECORD_LABEL_ID = 15;
//	private static final int USB_INDICATOR_ID = 16;
//	private static final int USB_INDICATOR_TEXT_ID = 17;
//	private static final int BACK_BTN_ID = 18;
//	private static final int LAND_ID = 19;
	
	private Image bottomBarBg;
//	
//	private Button btnSettings;
//	private Button btnTakeOff;
//	private Button btnLand;
//	private Button btnEmergency;
//	private Button btnCameraSwitch;
//	private Button btnPhoto;
//	private Button btnBack;
//	private ToggleButton btnRecord;
	
	private static final int JOY_ID_LEFT     = 1;
	private static final int JOY_ID_RIGHT    = 2;
	private static final int TOP_BAR_ID      = 4;
	private static final int BOTTOM_BAR_ID   = 5;
	private static final int TAKE_OFF_BTN_ID = 6;
	private static final int STOP_BTN_ID     = 7;
	private static final int SETTINGS_BTN_ID = 8;

	
	private Button stopBtn;
	private Button takeOffBtn;
	private Button settingsBtn;
	
	private Button[] buttons;
	
	private Indicator batteryIndicator;
	private Indicator wifiIndicator;
	private Image  usbIndicator;
//	private TextView txtVideoFps;
	private TextView txtSceneFps;
	
	private Text txtBatteryStatus;
	private Text txtAlert;
	private Text txtRecord;
	private Text txtUsbRemaining;
	
	private GLSurfaceView glView;
	
	private JoystickBase[] joysticks;
	private float joypadOpacity;
	private GestureDetector gestureDetector;
	
	private VideoStageRenderer renderer;
	private Activity context;
	
	private boolean useSoftwareRendering;
	private int prevRemainingTime;

	private SparseIntArray emergencyStringMap;

	public HudViewController(Activity context, boolean useSoftwareRendering)
	{
	    joypadOpacity = 1f;
		this.context = context;
		this.useSoftwareRendering = useSoftwareRendering;
		gestureDetector = new EnhancedGestureDetector(context, this);
		
		//canvasView = null;
		joysticks = new JoystickBase[2];

		glView = new GLSurfaceView(context);
		glView.setEGLContextClientVersion(2);
		
		context.setContentView(glView);
		
		renderer = new VideoStageRenderer(context, null);
		
		if (useSoftwareRendering){
			// Replacing OpneGl based view with Canvas based one
//			RelativeLayout root = (RelativeLayout) context.findViewById(R.id.controllerRootLayout);
//			root.removeView(glView);
			glView = null;
			
//			canvasView = new VideoStageView(context);
//			canvasView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//			root.addView(canvasView, 0);
		}
	
		initGLSurfaceView();

		Resources res = context.getResources();

//		btnSettings = new Button(res, R.drawable.btn_settings, R.drawable.btn_settings_pressed, Align.TOP_LEFT);
//		btnSettings.setMargin(0, 0, 0, (int)res.getDimension(R.dimen.hud_btn_settings_margin_left));
//		
//		
//		btnTakeOff = new Button(res, R.drawable.btn_take_off_normal, R.drawable.btn_take_off_pressed, Align.BOTTOM_CENTER);		
//		btnLand = new Button(res, R.drawable.btn_landing, R.drawable.btn_landing_pressed, Align.BOTTOM_CENTER);      
//		btnLand.setVisible(false);
//		
//		Image topBarBg = new Image(res, R.drawable.barre_haut, Align.TOP_CENTER);
//		topBarBg.setSizeParams(SizeParams.FILL_SCREEN, SizeParams.NONE);
//		topBarBg.setAlphaEnabled(false);
//		
//		bottomBarBg = new Image(res, R.drawable.barre_bas, Align.BOTTOM_CENTER);
//		bottomBarBg.setSizeParams(SizeParams.FILL_SCREEN, SizeParams.NONE);
//		bottomBarBg.setAlphaEnabled(false);
//		
//
//	    btnPhoto = new Button(res, R.drawable.btn_photo, R.drawable.btn_photo_pressed, Align.TOP_RIGHT);
//		btnRecord = new ToggleButton(res, R.drawable.btn_record, R.drawable.btn_record_pressed, 
//		                                    R.drawable.btn_record1, R.drawable.btn_record1_pressed,
//		                                    R.drawable.btn_record2, Align.TOP_RIGHT);
//		btnRecord.setMargin(0, res.getDimensionPixelOffset(R.dimen.hud_btn_rec_margin_right), 0, 0);
//		
//		txtRecord = new Text(context, "REC", Align.TOP_RIGHT);
//		txtRecord.setMargin((int)res.getDimension(R.dimen.hud_rec_text_margin_top), (int)res.getDimension(R.dimen.hud_rec_text_margin_right), 0, 0);
//		txtRecord.setTextColor(Color.WHITE);
//		txtRecord.setTypeface(TYPEFACE.Helvetica(context));
//		txtRecord.setTextSize(res.getDimensionPixelSize(R.dimen.hud_rec_text_size));
//		
//		usbIndicator = new Image(res, R.drawable.picto_usb_actif, Align.TOP_RIGHT);
//		usbIndicator.setMargin(0, res.getDimensionPixelOffset(R.dimen.hud_usb_indicator_margin_right), 0, 0);
//		
//		prevRemainingTime = -1;
//		txtUsbRemaining = new Text(context, "KO", Align.TOP_RIGHT);
//		txtUsbRemaining.setMargin(res.getDimensionPixelOffset(R.dimen.hud_usb_indicator_text_margin_top), 
//									res.getDimensionPixelOffset(R.dimen.hud_usb_indicator_text_margin_right), 0, 0);
//		txtUsbRemaining.setTypeface(TYPEFACE.Helvetica(context));
//		txtUsbRemaining.setTextSize(res.getDimensionPixelSize(R.dimen.hud_usb_indicator_text_size));
//		
//		btnCameraSwitch = new Button(res, R.drawable.btn_camera, R.drawable.btn_camera_pressed, Align.TOP_RIGHT);
//		btnCameraSwitch.setMargin(0, res.getDimensionPixelOffset(R.dimen.hud_btn_camera_switch_margin_right), 0, 0);
//		
//		int batteryIndicatorRes[] = {R.drawable.btn_battery_0,
//									R.drawable.btn_battery_1,
//									R.drawable.btn_battery_2,
//									R.drawable.btn_battery_3
//		};
//		
//		batteryIndicator = new Indicator(res, batteryIndicatorRes, Align.TOP_LEFT);
//		batteryIndicator.setMargin(0, 0, 0, (int)res.getDimension(R.dimen.hud_battery_indicator_margin_left));
//		
//		txtBatteryStatus = new Text(context, "0%", Align.TOP_LEFT);
//		txtBatteryStatus.setMargin((int)res.getDimension(R.dimen.hud_battery_text_margin_top),0,0, 			
//									(int)res.getDimension(R.dimen.hud_battery_indicator_margin_left) + batteryIndicator.getWidth());
//		txtBatteryStatus.setTextColor(Color.WHITE);
//		txtBatteryStatus.setTypeface(TYPEFACE.Helvetica(context));
//		txtBatteryStatus.setTextSize((int)res.getDimension(R.dimen.hud_battery_text_size));
//		
//
//		int wifiIndicatorRes[] = {
//			R.drawable.btn_wifi_0,
//			R.drawable.btn_wifi_1,
//			R.drawable.btn_wifi_2,
//			R.drawable.btn_wifi_3
//		};
//		
//		wifiIndicator = new Indicator(res, wifiIndicatorRes, Align.TOP_LEFT);
//		wifiIndicator.setMargin(0, 0, 0, (int)res.getDimension(R.dimen.hud_wifi_indicator_margin_left));

		Image topBarBg = new Image(res, R.drawable.bar_top, Align.TOP_CENTER);
		topBarBg.setSizeParams(SizeParams.FILL_SCREEN, SizeParams.NONE);
		topBarBg.setAlphaEnabled(false);
		
		bottomBarBg = new Image(res, R.drawable.bar_bottom, Align.BOTTOM_CENTER);
		bottomBarBg.setSizeParams(SizeParams.FILL_SCREEN, SizeParams.NONE);
		bottomBarBg.setAlphaEnabled(false);
		
		settingsBtn = new Button(res, R.drawable.btn_settings_normal, R.drawable.btn_settings_hl, Align.TOP_RIGHT);
		//settingsBtn.setMargin(0, 0, 0, (int)res.getDimension(R.dimen.hud_btn_settings_margin_left));
			
		takeOffBtn = new Button(res, R.drawable.btn_take_off_normal, R.drawable.btn_take_off_hl, Align.BOTTOM_CENTER);		
		stopBtn = new Button(res, R.drawable.btn_stop_normal, R.drawable.btn_stop_hl, Align.TOP_CENTER);

		buttons = new Button[3];
		buttons[0] = settingsBtn;
		buttons[1] = takeOffBtn;
		buttons[2] = stopBtn;

		renderer.addSprite(TOP_BAR_ID, topBarBg);
		renderer.addSprite(BOTTOM_BAR_ID, bottomBarBg);
		renderer.addSprite(TAKE_OFF_BTN_ID, takeOffBtn);
		renderer.addSprite(STOP_BTN_ID, stopBtn);
		renderer.addSprite(SETTINGS_BTN_ID, settingsBtn);
//		renderer.addSprite(BACK_BTN_ID, btnBack);
//		renderer.addSprite(PHOTO_ID, btnPhoto);
//		renderer.addSprite(RECORD_ID, btnRecord);
//		renderer.addSprite(CAMERA_ID, btnCameraSwitch);
//		renderer.addSprite(ALERT_ID, btnEmergency);
//		renderer.addSprite(TAKE_OFF_ID, btnTakeOff);
//		renderer.addSprite(LAND_ID, btnLand);
//		renderer.addSprite(BATTERY_INDICATOR_ID, batteryIndicator);
//		renderer.addSprite(WIFI_INDICATOR_ID, wifiIndicator);
//		renderer.addSprite(EMERGENCY_LABEL_ID, txtAlert);
//		renderer.addSprite(BATTERY_STATUS_LABEL_ID, txtBatteryStatus);
//		renderer.addSprite(RECORD_LABEL_ID, txtRecord);
//		renderer.addSprite(USB_INDICATOR_ID, usbIndicator);
//		renderer.addSprite(USB_INDICATOR_TEXT_ID, txtUsbRemaining);
	}
	

	
//	private void initCanvasSurfaceView()
//	{
//		if (canvasView != null) {
//			canvasView.setRenderer(renderer);
//			canvasView.setOnTouchListener(this);
//		}
//	}
	
	
	private void initGLSurfaceView() {
		if (glView != null) {
			glView.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
			glView.setRenderer(renderer);
			glView.setOnTouchListener(this);
		}
	}
	
	
	public void setJoysticks(JoystickBase left, JoystickBase right)
	{
		joysticks[0] = left;
		if (left != null)   {
		    joysticks[0].setAlign(Align.BOTTOM_LEFT);
		    joysticks[0].setAlpha(joypadOpacity);
		}
		joysticks[1] = right;
		if (right != null)	{
		    joysticks[1].setAlign(Align.BOTTOM_RIGHT);
		    joysticks[1].setAlpha(joypadOpacity);
		}
	
		for (int i=0; i<joysticks.length; ++i) {
		    JoystickBase joystick = joysticks[i];
		    
			if (joystick != null) {
				if (!useSoftwareRendering) {
					joystick.setInverseYWhenDraw(true);
				} else {
					joystick.setInverseYWhenDraw(false);
				}
				
				int margin = context.getResources().getDimensionPixelSize(R.dimen.hud_joy_margin);
				
				joystick.setMargin(0, margin, bottomBarBg.getHeight() + margin, margin);
			}
		}
		
		renderer.removeSprite(JOY_ID_LEFT);
		renderer.removeSprite(JOY_ID_RIGHT);

		if (left != null) {
			renderer.addSprite(JOY_ID_LEFT, left);
		}
		
		if (right != null) {
			renderer.addSprite(JOY_ID_RIGHT, right);
		}
	}
	
	
	public JoystickBase getJoystickLeft()
	{
	    return joysticks[0];
	}
	
	
	public JoystickBase getJoystickRight()
	{
	    return joysticks[1];
	}
	

	public void setInterfaceOpacity(float opacity)
	{
		if (opacity < 0 || opacity > 100.0f) {
			Log.w(TAG, "Can't set interface opacity. Invalid value: " + opacity);
			return;
		}
		
		joypadOpacity = opacity / 100f;
		
		Sprite joystick = renderer.getSprite(JOY_ID_LEFT);
		joystick.setAlpha(joypadOpacity);
		
		joystick = renderer.getSprite(JOY_ID_RIGHT);
		joystick.setAlpha(joypadOpacity);
	}
	
	
//	public void setIsFlying(final boolean isFlying)
//	{
//		if (isFlying) {
//			btnTakeOff.setVisible(false);
//			btnLand.setVisible(true);
//		} else {
//	         btnTakeOff.setVisible(true);
//	         btnLand.setVisible(false);
//		}
//	}

	
	public void setBatteryValue(final int percent)
	{
		if (percent > 100 || percent < 0) {
			Log.w(TAG, "Can't set battery value. Invalid value " + percent);
			return;
		}
				
		int imgNum = Math.round((float) percent / 100.0f * 3.0f);

		txtBatteryStatus.setText(percent + "%");
		
		if (imgNum < 0)
			imgNum = 0;
		
		if (imgNum > 3) 
			imgNum = 3;

		if (batteryIndicator != null) {
			batteryIndicator.setValue(imgNum);
		}
	}
	
	public void setWifiValue(final int theNum)
	{
		if (wifiIndicator != null) {
			wifiIndicator.setValue(theNum);
		}
	}
	
	public void setSettingsButtonEnabled(boolean enabled)
	{
		settingsBtn.setEnabled(enabled);
	}
	
	public void setTakeOffBtnClickListener(OnClickListener listener)
	{
		this.takeOffBtn.setOnClickListener(listener);
	}
	

	public void setStopBtnClickListener(OnClickListener listener) 
	{
		this.stopBtn.setOnClickListener(listener);		
	}

	public void setSettingsBtnClickListener(OnClickListener listener) 
	{
		this.settingsBtn.setOnClickListener(listener);		
	}
	
	public void setDoubleTapClickListener(OnDoubleTapListener listener) 
	{
		gestureDetector.setOnDoubleTapListener(listener);	
	}
	
	
	public void onPause()
	{
		if (glView != null) {
			glView.onPause();
		}
	}
	
	
	public void onResume()
	{
		if (glView != null) {
			glView.onResume();
		}
//		
//		if (canvasView != null) {
//			canvasView.onStart();
//		}
	}


	public boolean onTouch(View v, MotionEvent event)
	{
		boolean result = false;
		
		for (int i=0; i<buttons.length; ++i) {
			if (buttons[i].processTouch(v, event)) {
				result = true;
				break;
			}
		}
		
		if (result != true) {	
			gestureDetector.onTouchEvent(event);
			
			for (int i=0; i<joysticks.length; ++i) {
				JoystickBase joy = joysticks[i];
				if (joy != null) {
					if (joy.processTouch(v, event)) {
						
						result = true;
					}
				}
			}
		}
		
			
//		if (event.getAction() == MotionEvent.ACTION_MOVE) {
//		    // Trying to avoid flooding of ACTION_MOVE events
//			try {
//				Thread.sleep(33);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
		return result;
	}

	
	public void onDestroy()
	{
	    renderer.clearSprites();
		
//		if (canvasView != null) {
//			canvasView.onStop();
//		}
	}


	public boolean onDown(MotionEvent e) 
	{
		return false;
	}


	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) 
	{
		return false;
	}


	public void onLongPress(MotionEvent e) 
	{
    	// Left unimplemented	
	}


	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) 
	{
		return false;
	}


	public void onShowPress(MotionEvent e) 
	{
	    // Left unimplemented
	}


	public boolean onSingleTapUp(MotionEvent e) 
	{
		return false;
	}
	
	public View getRootView()
	{
	    if (glView != null) {
	        return glView;
	    }
//	    else if (canvasView != null) {
//	        return canvasView;
//	    }
	    
	    Log.w(TAG, "Can't find root view");
	    return null;
	}
}
