package com.hexairbot.hexmini;

import com.hexairbot.hexmini.SettingsDialog;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import com.hexairbot.hexmini.ui.*;
import com.hexairbot.hexmini.ui.joystick.AnalogueJoystick;
import com.hexairbot.hexmini.ui.joystick.JoystickBase;
import com.hexairbot.hexmini.ui.joystick.JoystickFactory;
import com.hexairbot.hexmini.ui.joystick.JoystickListener;
import com.hexairbot.hexmini.HudViewController.JoystickType;
import com.hexairbot.hexmini.modal.ApplicationSettings;


@SuppressLint("NewApi")
public class HudActivity extends FragmentActivity implements SettingsDialogDelegate, OnTouchListener{
	private static final String TAG = HudActivity.class.getSimpleName();
	
	private SettingsDialog settingsDialog;
    private HudViewController hudVC;
	
    private boolean isLeftHanded;
    private JoystickListener rollPitchListener;
    private JoystickListener rudderThrottleListener;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
		
		hudVC = new HudViewController(this);
		
		initButtonListeners();
		initJoystickListeners();
		
		initJoysticks(JoystickType.ANALOGUE, JoystickType.ANALOGUE, isLeftHanded);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}
	
    protected void showSettingsDialog()
    {        
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        
        if(settingsDialog == null)
        	settingsDialog = new SettingsDialog(this, this);
        
        settingsDialog.show(ft, "settings");
    }


	@Override
	public void prepareDialog(SettingsDialog dialog) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDismissed(SettingsDialog settingsDialog) {
		// TODO Auto-generated method stub
		
	}


//	@Override
//	public void onOptionChangedApp(SettingsDialog dialog,
//			EAppSettingProperty property, Object value) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void initButtonListeners(){
		hudVC.setSettingsBtnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HudActivity.this.showSettingsDialog();
			}
		});
		
		hudVC.setTakeOffBtnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		hudVC.setStopBtnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
    
    private void initJoysticks(JoystickType leftType, JoystickType rightType, boolean isLeftHanded)
    {
        JoystickBase joystickLeft = (!isLeftHanded ? hudVC.getJoystickLeft() : hudVC.getJoystickRight());
        JoystickBase joystickRight = (!isLeftHanded ? hudVC.getJoystickRight() : hudVC.getJoystickLeft());

        ApplicationSettings settings = getSettings();

        if (joystickLeft == null || !(joystickLeft instanceof AnalogueJoystick)) {
        	joystickLeft = JoystickFactory.createAnalogueJoystick(this, false, rollPitchListener);
        } 
        else {
        	joystickLeft.setOnAnalogueChangedListener(rollPitchListener);
        	joystickRight.setAbsolute(false);
        }

        if (joystickRight == null || !(joystickRight instanceof AnalogueJoystick)) {
        	joystickRight = JoystickFactory.createAnalogueJoystick(this, false, rudderThrottleListener);
        } 
        else {
        	joystickRight.setOnAnalogueChangedListener(rudderThrottleListener);
        	joystickRight.setAbsolute(false);
        }

        if (!isLeftHanded) {
        	hudVC.setJoysticks(joystickLeft, joystickRight);
        } 
        else {
        	hudVC.setJoysticks(joystickRight, joystickLeft);
        }
    }
    
    private ApplicationSettings getSettings()
    {
        return ((HexMiniApplication) getApplication()).getAppSettings();
    }
    
    private void initJoystickListeners()
    {
        rollPitchListener = new JoystickListener()
        {
            public void onChanged(JoystickBase joy, float x, float y)
            {
        		Log.d(TAG, "rollPitchListener onChanged x:" + x + "y:" + y);
            }

            @Override
            public void onPressed(JoystickBase joy)
            {
            }

            @Override
            public void onReleased(JoystickBase joy)
            {

            }
        };

        rudderThrottleListener = new JoystickListener()
        {
            public void onChanged(JoystickBase joy, float x, float y)
            {
        		Log.d(TAG, "rudderThrottleListener onChanged x:" + x + "y:" + y);
            }

            @Override
            public void onPressed(JoystickBase joy)
            {
            }

            @Override
            public void onReleased(JoystickBase joy)
            {
            }
        };
    }

}
