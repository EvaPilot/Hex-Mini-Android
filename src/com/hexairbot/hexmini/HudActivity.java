package com.hexairbot.hexmini;

import com.hexairbot.hexmini.modal.ApplicationSettings.EAppSettingProperty;
import com.hexairbot.hexmini.util.SystemUiHider;
import com.hexairbot.hexmini.SettingsDialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class HudActivity extends FragmentActivity implements SettingsDialogDelegate{
	private Button stopBtn;
	private Button takeOffBtn;
	private Button settingsBtn;
	
    private SettingsDialog settingsDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  
		setContentView(R.layout.activity_hud);

		stopBtn     = (Button)findViewById(R.id.stopBtn);
		takeOffBtn  = (Button)findViewById(R.id.takeOffBtn);
		settingsBtn = (Button)findViewById(R.id.settingsBtn);
		
	    settingsDialog = new SettingsDialog(this, this);
	    //settingsDialog

		settingsBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO go to Settings Activity£¬ and keep Hud Activity alive.
				
				HudActivity.this.showSettingsDialog();
			}
		});
	}
	
	
    protected void showSettingsDialog()
    {        
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
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
}
