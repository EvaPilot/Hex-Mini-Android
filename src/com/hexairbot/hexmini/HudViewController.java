package com.hexairbot.hexmini;

import android.app.Activity;
import android.content.res.Resources;
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
import com.hexairbot.hexmini.gestures.EnhancedGestureDetector;
import com.hexairbot.hexmini.ui.Button;
import com.hexairbot.hexmini.ui.Image;
import com.hexairbot.hexmini.ui.Indicator;
import com.hexairbot.hexmini.ui.Sprite;
import com.hexairbot.hexmini.ui.Text;
import com.hexairbot.hexmini.ui.ToggleButton;
import com.hexairbot.hexmini.ui.UIRenderer;
import com.hexairbot.hexmini.ui.Image.SizeParams;
import com.hexairbot.hexmini.ui.Sprite.Align;
import com.hexairbot.hexmini.ui.joystick.JoystickBase;

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
	
	private static final int JOY_ID_LEFT     = 1;
	private static final int JOY_ID_RIGHT    = 2;
	private static final int MIDLLE_BG_ID    = 3;
	private static final int TOP_BAR_ID      = 4;
	private static final int BOTTOM_BAR_ID   = 5;
	private static final int TAKE_OFF_BTN_ID = 6;
	private static final int STOP_BTN_ID     = 7;
	private static final int SETTINGS_BTN_ID = 8;
	
	private Image bottomBarBg;

	private Button stopBtn;
	private Button takeOffBtn;
	private Button settingsBtn;
	
	private Button[] buttons;
	
	private Indicator batteryIndicator;
	private Image  usbIndicator;
//	private TextView txtVideoFps;
	private TextView txtSceneFps;
	
	private Text txtBatteryStatus;
	
	private GLSurfaceView glView;
	
	private JoystickBase[] joysticks;
	private float joypadOpacity;
	private GestureDetector gestureDetector;
	
	private UIRenderer renderer;
	private Activity context;

	public HudViewController(Activity context)
	{
	    joypadOpacity = 1f;
		this.context = context;
		gestureDetector = new EnhancedGestureDetector(context, this);
		
		joysticks = new JoystickBase[2];

		glView = new GLSurfaceView(context);
		glView.setEGLContextClientVersion(2);
		
		context.setContentView(glView);
		
		renderer = new UIRenderer(context, null);
	
		initGLSurfaceView();

		Resources res = context.getResources();

		Image topBarBg = new Image(res, R.drawable.bar_top, Align.TOP_CENTER);
		topBarBg.setSizeParams(SizeParams.FILL_SCREEN, SizeParams.NONE);  //Width水平伸缩至全屏，height保持不边
		topBarBg.setAlphaEnabled(false);
		
		bottomBarBg = new Image(res, R.drawable.bar_bottom, Align.BOTTOM_CENTER);
		bottomBarBg.setSizeParams(SizeParams.FILL_SCREEN, SizeParams.NONE);
		bottomBarBg.setAlphaEnabled(false);
		
		Image middleBg = new Image(res, R.drawable.middle_bg, Align.CENTER);
		middleBg.setSizeParams(SizeParams.FILL_SCREEN, SizeParams.FILL_SCREEN);  //Width水平伸缩至全屏，height保持不边
		middleBg.setAlphaEnabled(false);
		
		settingsBtn = new Button(res, R.drawable.btn_settings_normal, R.drawable.btn_settings_hl, Align.TOP_RIGHT);
		//settingsBtn.setMargin(0, 0, 0, (int)res.getDimension(R.dimen.hud_btn_settings_margin_left));
			
		takeOffBtn = new Button(res, R.drawable.btn_take_off_normal, R.drawable.btn_take_off_hl, Align.BOTTOM_CENTER);		
		stopBtn = new Button(res, R.drawable.btn_stop_normal, R.drawable.btn_stop_hl, Align.TOP_CENTER);

		buttons = new Button[3];
		buttons[0] = settingsBtn;
		buttons[1] = takeOffBtn;
		buttons[2] = stopBtn;

		renderer.addSprite(MIDLLE_BG_ID, middleBg);
		renderer.addSprite(TOP_BAR_ID, topBarBg);
		renderer.addSprite(BOTTOM_BAR_ID, bottomBarBg);
		renderer.addSprite(TAKE_OFF_BTN_ID, takeOffBtn);
		renderer.addSprite(STOP_BTN_ID, stopBtn);
		renderer.addSprite(SETTINGS_BTN_ID, settingsBtn);
	}
	
	
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
				joystick.setInverseYWhenDraw(true);

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
	}

    //glView onTouch Event handler
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
		
		return result;
	}

	
	public void onDestroy()
	{
	    renderer.clearSprites();
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
	    
	    Log.w(TAG, "Can't find root view");
	    return null;
	}
}
