/**
 * 
 */
package com.hexairbot.hexmini;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author koupoo
 *
 */
public class SettingsActivity extends Activity {
	private Button backBtn;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		backBtn = (Button)findViewById(R.id.backBtn);
		
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO go back to Hud Activity, destroy Settings Activity
			}
		});
	}
}
