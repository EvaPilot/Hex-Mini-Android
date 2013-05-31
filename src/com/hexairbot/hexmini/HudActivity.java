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

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
@SuppressLint("NewApi")
public class HudActivity extends FragmentActivity implements SettingsDialogDelegate, OnTouchListener{
	private Button stopBtn;
	private Button takeOffBtn;
	private Button settingsBtn;
	
	private View joystickLeftView;
	private View joystickRightView;
	private ImageView joystickLeftThumbImageView;
	private ImageView joystickRightThumbImageView;
	private ImageView joystickLeftBgImageView;
	private ImageView joystickRightBgImageView;
	
    private SettingsDialog settingsDialog;
    
	protected int fingerId;
	protected boolean isPressed;
	
	protected boolean rightIsPressed;
	protected boolean leftIsPressed;
	
	
	private Point joystickLeftInitialPosition;
	private Point joystickRightInitialPosition;
	
	private Point joystickLeftCurrentPosition;
	private Point joystickRightCurrentPosition;
	
	private Point joystickLeftCenter;
	private Point joystickRightCenter;
	
	private boolean isLeftHanded;
	
    private int rightJoyStickOperableRadius;
    private int leftJoyStickOperableRadius;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  
		setContentView(R.layout.activity_hud);
		
		stopBtn     = (Button)findViewById(R.id.stopBtn);
		takeOffBtn  = (Button)findViewById(R.id.takeOffBtn);
		settingsBtn = (Button)findViewById(R.id.settingsBtn);
		
		joystickLeftView  = (View)findViewById(R.id.joystickLeftView);
		joystickRightView = (View)findViewById(R.id.joystickRightView);
		
		joystickLeftThumbImageView  = (ImageView)findViewById(R.id.joystickLeftThumbImageView);
		joystickRightThumbImageView = (ImageView)findViewById(R.id.joystickRightThumbImageView);
		joystickLeftBgImageView  = (ImageView)findViewById(R.id.joystickLeftBgImageView);
		joystickRightBgImageView = (ImageView)findViewById(R.id.joystickRightBgImageView);
		
		joystickLeftView.setOnTouchListener(this);
		joystickRightView.setOnTouchListener(this);
		
	    settingsDialog = new SettingsDialog(this, this);
	    //settingsDialog

		settingsBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO go to Settings Activity， and keep Hud Activity alive.
				
				HudActivity.this.showSettingsDialog();
			}
		});
		
		
		joystickLeftInitialPosition  = new Point(0, 0);
		joystickRightInitialPosition = new Point(0, 0);
		
		joystickLeftCenter  = new Point(0, 0);
		joystickRightCenter = new Point(0, 0);
		
		joystickLeftCurrentPosition  = new Point(0, 0);
		joystickRightCurrentPosition = new Point(0, 0);

		joystickLeftThumbImageView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				joystickLeftCenter.set((int)(joystickLeftThumbImageView.getLeft() + joystickLeftThumbImageView.getWidth() / 2)
						, (int)(joystickLeftThumbImageView.getTop() + joystickLeftThumbImageView.getHeight() / 2));
				joystickLeftInitialPosition.set(joystickLeftCenter.x - joystickLeftBgImageView.getWidth() / 2
						, joystickLeftCenter.y - (joystickLeftBgImageView.getWidth() / 2));
				
				joystickLeftCurrentPosition.set(joystickLeftInitialPosition.x, joystickLeftInitialPosition.y);
				
				System.out.println("joystickLeftCenter x:" + joystickLeftCenter.x + ";" + "joystickLeftCenter y:" + joystickLeftCenter.y + ";");
				System.out.println("joystickLeftInitialPosition x:" + joystickLeftInitialPosition.x + ";" + "joystickLeftInitialPosition y:" + joystickLeftInitialPosition.y + ";");
			}
		});
		
		joystickRightThumbImageView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				joystickRightCenter.set((int)(joystickRightThumbImageView.getLeft() + joystickRightThumbImageView.getWidth() / 2)
						, (int)(joystickRightThumbImageView.getTop() + joystickRightThumbImageView.getHeight() / 2));
				joystickRightInitialPosition.set(joystickRightCenter.x - joystickRightBgImageView.getWidth() / 2
						, joystickRightCenter.y - (joystickRightBgImageView.getWidth() / 2));
				
				joystickRightCurrentPosition.set(joystickRightInitialPosition.x, joystickRightInitialPosition.y);
				
				
				
				refreshJoystickRight();
				
				System.out.println("joystickRightCenter x:" + joystickRightCenter.x + ";" + "joystickRightCenter y:" + joystickRightCenter.y + ";");
				System.out.println("joystickRightInitialPosition x:" + joystickRightInitialPosition.x + ";" + "joystickRightInitialPosition y:" + joystickRightInitialPosition.y + ";");
			}
		});
		
		joystickLeftBgImageView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				leftJoyStickOperableRadius  = joystickLeftBgImageView.getWidth() / 2;
				joystickLeftInitialPosition.set(joystickLeftCenter.x - joystickLeftBgImageView.getWidth() / 2
						, joystickLeftCenter.y - (joystickLeftBgImageView.getWidth() / 2));
				
				joystickLeftCurrentPosition.set(joystickLeftInitialPosition.x, joystickLeftInitialPosition.y);
				
				System.out.println("leftJoyStickOperableRadius:" + leftJoyStickOperableRadius);
				System.out.println("joystickLeftInitialPosition x:" + joystickLeftInitialPosition.x + ";" + "joystickLeftInitialPosition y:" + joystickLeftInitialPosition.y + ";");
			}
		});
		
		joystickRightBgImageView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				rightJoyStickOperableRadius = joystickRightBgImageView.getWidth() / 2;
				joystickRightInitialPosition.set(joystickRightCenter.x - joystickRightBgImageView.getWidth() / 2
						, joystickRightCenter.y - (joystickRightBgImageView.getWidth() / 2));
				
				joystickRightCurrentPosition.set(joystickRightInitialPosition.x, joystickRightInitialPosition.y);

				refreshJoystickRight();
				
				System.out.println("rightJoyStickOperableRadius:" + rightJoyStickOperableRadius);
				System.out.println("joystickRightInitialPosition x:" + joystickRightInitialPosition.x + ";" + "joystickRightInitialPosition y:" + joystickRightInitialPosition.y + ";");
			
			}
		});
		


		fingerId = -1;
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
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		int pointerIdx = action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

		switch (actionCode) {
			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_DOWN:
				
				if (fingerId == -1) {
					fingerId = event.getPointerId(pointerIdx);
					isPressed = true;
					onActionDown(v, event.getX(pointerIdx), event.getY(pointerIdx));
					return true;
				}

				return false;
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_UP:
			{	
				if (fingerId == -1)
					return false;
				
				if (event.getPointerId(pointerIdx) != fingerId)
					return false;
						
				fingerId = -1;
				onActionUp(v, event.getX(pointerIdx),  event.getY(pointerIdx));
				isPressed = false;
				return true;
			}
			case MotionEvent.ACTION_MOVE: 
			{
				if (fingerId == -1)
					return false;
				
				for (int i=0; i<event.getPointerCount(); ++i)  {
					if (event.getPointerId(i) == fingerId) {
						onActionMove(v, event.getX(i), event.getY(i));		
						return true;
					}
				}

				return false;
			}
			default:
				return false;
		}
	}
	
    protected void onActionDown(View view, float x, float y)
    {
    	if(view == joystickRightView){
    		//System.out.print("Touch down x:" + x + "; y:" + y);
    		
    		Log.d("Touch down", "x:" + x + "; y:" + y);
    		
    		rightIsPressed = true;
 //   		joystickRightBgImageView.setAlpha((float)1.0);
//    		joystickRightBgImageView.setX(x - joystickRightBgImageView.getWidth() / 2);
    		
    		joystickRightCurrentPosition.x = (int)(x - joystickRightBgImageView.getWidth() / 2);
    		
    		
    		int thumbCurrentX = 0;
    		int thumbCurrentY = 0;
    		
    		
    		if(isLeftHanded){
    			
    			
    		}
    		else{
    			float throttleValue = 1.0f;

                joystickRightCurrentPosition.y = (int) (y - (joystickRightBgImageView.getHeight() / 2) + throttleValue * rightJoyStickOperableRadius);
    			
                refreshJoystickRight();
                
                joystickRightCenter.set((int)(joystickRightBgImageView.getLeft() + joystickRightBgImageView.getWidth() / 2)
                		, (int)(joystickRightBgImageView.getTop() + joystickRightBgImageView.getHeight() / 2));
    		
                thumbCurrentX = joystickRightCenter.x;
                thumbCurrentY = (int) y;
    		}
    		
    		updateVelocity(thumbCurrentX, thumbCurrentY, true);
    		
    	}
    	
    	/*
    	if(view == joystickLeftView){
    		leftIsPressed = true;
    		joystickLeftBgImageView.setAlpha((float)1.0);
    		
    		
    	}*/
    }
    
    protected void onActionUp(View view, float x, float y)
    {
    	if(view == joystickRightView){
    		rightIsPressed = false;
    		joystickRightCurrentPosition.set(joystickRightInitialPosition.x, joystickRightInitialPosition.y);
    		
    		//joystickRightBackgroundImageView.alpha = joystickRightThumbImageView.alpha = joystickAlpha;
    		
    		refreshJoystickRight();
    		
    		if(isLeftHanded){
    			
    			
    		}
    		else{
    			float throttleValue = 0;
    			
    			joystickRightCenter.set((int)(joystickRightBgImageView.getLeft() + joystickRightBgImageView.getWidth() / 2), 
    					(int)(joystickRightBgImageView.getTop() + (joystickRightBgImageView.getHeight() / 2 - throttleValue * rightJoyStickOperableRadius)));
    		}
    		
    		updateVelocity(joystickRightCenter.x, joystickRightCenter.y, true);
    	}
    	
    	
    }
    
    protected void onActionMove(View view, float x, float y)
    {
    	
    	
    }
    
    private void refreshJoystickLeft()
    {
    	joystickLeftBgImageView.setLeft(joystickLeftCurrentPosition.x);
    	joystickLeftBgImageView.setTop(joystickLeftCurrentPosition.y);
    	
    }
    
    private void refreshJoystickRight()
    {
    	joystickRightBgImageView.setLeft(joystickRightCurrentPosition.x);
    	joystickRightBgImageView.setTop(joystickRightCurrentPosition.y);
    }    
    
  //更新摇杆点（joystickRightThumbImageView或joystickLeftThumbImageView）的位置，point是当前触摸点的位置
    private void updateVelocity(int x, int y, boolean isRight)
    {
//        float leftThumbWidth = 0.0f;
//        float rightThumbWidth = 0.0f;
//        float leftThumbHeight = 0.0f;
//        float rightThumbHeight = 0.0f;
//        float leftRadius = 0.0f;
//        float rightRadius = 0.0f;
         
//        float leftThumbWidth = joystickLeftThumbImageView.getWidth();
//        float rightThumbWidth = joystickRightThumbImageView.getWidth();
//        float leftThumbHeight = joystickLeftThumbImageView.getHeight();
//        float rightThumbHeight = joystickRightThumbImageView.getHeight();
//        float leftRadius = joystickLeftBgImageView.getWidth() / 2.0f;
//        float  rightRadius = joystickRightBgImageView.getWidth() / 2.0f;

     	Point nextpoint = new Point(x, y);
     	Point center = (isRight ? joystickRightCenter : joystickLeftCenter);
     	ImageView thumbImage = (isRight ? joystickRightThumbImageView : joystickLeftThumbImageView);
     	
     	float dx = nextpoint.x - center.x;
     	float dy = nextpoint.y - center.y;
         
         float thumb_radius = isRight ? rightJoyStickOperableRadius : leftJoyStickOperableRadius;
     	
         if(Math.abs(dx) > thumb_radius){
             if (dx > 0) {
                 nextpoint.x = center.x + rightJoyStickOperableRadius;
             }
             else {
                 nextpoint.x = center.x - rightJoyStickOperableRadius;
             }
         }
         
         if(Math.abs(dy) > thumb_radius){
             if(dy > 0){
                 nextpoint.y = center.y + rightJoyStickOperableRadius;
             }
             else {
                 nextpoint.y = center.y - rightJoyStickOperableRadius;
             }
         }
     	
     	thumbImage.setX(nextpoint.x - thumbImage.getWidth() / 2);
     	thumbImage.setY(nextpoint.y - thumbImage.getHeight() / 2);
     }
    
    private void updateJoystickCenter()
    {
    	joystickLeftCenter.set(joystickLeftInitialPosition.x + (joystickLeftBgImageView.getWidth() / 2), joystickLeftInitialPosition.y +  (joystickLeftBgImageView.getHeight() / 2));
        joystickRightCenter.set(joystickRightInitialPosition.x + (joystickRightBgImageView.getWidth() / 2), joystickRightInitialPosition.y +  (joystickRightBgImageView.getHeight() / 2));
        
        if(isLeftHanded){
            //joystickLeftThumbImageView.center = CGPointMake(leftCenter.x, leftCenter.y - _throttleChannel.value * leftJoyStickOperableRadius);
            joystickLeftThumbImageView.setLeft(joystickLeftCenter.x - joystickLeftThumbImageView.getWidth() / 2);
            joystickLeftThumbImageView.setTop(joystickLeftCenter.x - joystickLeftThumbImageView.getWidth() / 2 - 0 * leftJoyStickOperableRadius);
        }
        else{
            //joystickRightThumbImageView.center = CGPointMake(rightCenter.x, rightCenter.y - _throttleChannel.value * rightJoyStickOperableRadius);
        	joystickRightThumbImageView.setLeft(joystickRightCenter.x - joystickRightThumbImageView.getWidth() / 2);
        	joystickRightThumbImageView.setTop(joystickRightCenter.x - joystickRightThumbImageView.getWidth() / 2 - 0 * rightJoyStickOperableRadius);
        }
    }
}
