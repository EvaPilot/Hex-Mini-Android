package com.hexairbot.hexmini;

import com.hexairbot.hexmini.modal.ApplicationSettings.EAppSettingProperty;
import com.hexairbot.hexmini.util.SystemUiHider;
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
import com.hexairbot.hexmini.HudViewController.JoystickType;
import com.hexairbot.hexmini.modal.ApplicationSettings;
import com.hexairbot.hexmini.modal.ApplicationSettings.ControlMode;;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
@SuppressLint("NewApi")
public class HudActivity extends FragmentActivity implements SettingsDialogDelegate, OnTouchListener{
    private SettingsDialog settingsDialog;
    private HudViewController hudVC;
	
    private boolean isLeftHanded;
    private boolean acceleroEnabled;
    private JoystickListener rollPitchListener;
    private JoystickListener gazYawListener;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
		
		hudVC = new HudViewController(this, false);
		
		initListeners();
		
		applyJoypadConfig(ControlMode.NORMAL_MODE, false);
		
        //if (!isLeftHanded) {

       // } else {
//        	hudVC.setJoysticks(joystickRight, joystickLeft);
//        }

		//setContentView(R.layout.activity_hud);
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


	@Override
	public void onOptionChangedApp(SettingsDialog dialog,
			EAppSettingProperty property, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void initListeners(){
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
	
 
    private void applyJoypadConfig(ControlMode controlMode, boolean isLeftHanded)
    {
        switch (controlMode) {
        case NORMAL_MODE:
            initVirtualJoysticks(JoystickType.ANALOGUE, JoystickType.ANALOGUE, isLeftHanded);
            acceleroEnabled = false;
            break;
        case ACCELERO_MODE:
            initVirtualJoysticks(JoystickType.ACCELERO, JoystickType.ANALOGUE, isLeftHanded);
            acceleroEnabled = true;
            break;
        case ACE_MODE:
            initVirtualJoysticks(JoystickType.NONE, JoystickType.COMBINED, isLeftHanded);
            acceleroEnabled = true;
            break;
        }
    }
    
    private void initVirtualJoysticks(JoystickType leftType, JoystickType rightType, boolean isLeftHanded)
    {
        JoystickBase joystickLeft = (!isLeftHanded ? hudVC.getJoystickLeft() : hudVC.getJoystickRight());
        JoystickBase joystickRight = (!isLeftHanded ? hudVC.getJoystickRight() : hudVC.getJoystickLeft());

        ApplicationSettings settings = getSettings();

//        if (leftType == JoystickType.ANALOGUE) {
            if (joystickLeft == null || !(joystickLeft instanceof AnalogueJoystick) || joystickLeft.isAbsoluteControl() != settings.isAbsoluteControlEnabled()) {
                joystickLeft = JoystickFactory.createAnalogueJoystick(this, settings.isAbsoluteControlEnabled(), rollPitchListener);
            } 
            else {
                joystickLeft.setOnAnalogueChangedListener(rollPitchListener);
                joystickRight.setAbsolute(settings.isAbsoluteControlEnabled());
            }
//        } else if (leftType == JoystickType.ACCELERO) {
//            if (joystickLeft == null || !(joystickLeft instanceof AcceleroJoystick) || joystickLeft.isAbsoluteControl() != settings.isAbsoluteControlEnabled()) {
//                joystickLeft = JoystickFactory.createAcceleroJoystick(this, settings.isAbsoluteControlEnabled(), rollPitchListener);
//            } else {
//                joystickLeft.setOnAnalogueChangedListener(rollPitchListener);
//                joystickRight.setAbsolute(settings.isAbsoluteControlEnabled());
//            }
//        }
//
//        if (rightType == JoystickType.ANALOGUE) {
//            if (joystickRight == null || !(joystickRight instanceof AnalogueJoystick) || joystickRight.isAbsoluteControl() != settings.isAbsoluteControlEnabled()) {
//                joystickRight = JoystickFactory.createAnalogueJoystick(this, false, gazYawListener);
//            } else {
//                joystickRight.setOnAnalogueChangedListener(gazYawListener);
//                joystickRight.setAbsolute(false);
//            }
//        } else if (rightType == JoystickType.ACCELERO) {
//            if (joystickRight == null || !(joystickRight instanceof AcceleroJoystick) || joystickRight.isAbsoluteControl() != settings.isAbsoluteControlEnabled()) {
//                joystickRight = JoystickFactory.createAcceleroJoystick(this, false, gazYawListener);
//            } else {
//                joystickRight.setOnAnalogueChangedListener(gazYawListener);
//                joystickRight.setAbsolute(false);
//            }
//        }

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
}
