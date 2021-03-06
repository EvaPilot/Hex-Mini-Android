
package com.hexairbot.hexmini;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
//import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.hexairbot.hexmini.R;
//import com.hexairbot.hexmini.drone.DroneConfig;
//import com.hexairbot.hexmini.drone.DroneConfig.EDroneVersion;
import com.hexairbot.hexmini.adapter.SettingsViewAdapter;
import com.hexairbot.hexmini.modal.ApplicationSettings;
//import  com.hexairbot.hexmini.ui.adapters.SettingsViewAdapter;
//import  com.hexairbot.hexmini.ui.controls.ViewPagerIndicator;
//import  com.hexairbot.hexmini.ui.filters.NetworkNameFilter;
//import  com.hexairbot.hexmini.ui.listeners.OnSeekChangedListener;
//import  com.hexairbot.hexmini.utils.FontUtils;
import com.hexairbot.hexmini.ui.control.ViewPagerIndicator;

public class SettingsViewController
        implements OnPageChangeListener,
        OnClickListener
{

    private static final String TAG = "SettingsViewController";

    private List<View> settingsViews;
    
    private TextView titleTextView;
    
    private ViewPager viewPager;
    private ImageButton preBtn;
    private ImageButton nextBtn;
    
    private Button defaultSettingsBtn;
    private Button accCalibrateBtn;
    private Button magCalibrateBtn;
    
    private CheckBox isLeftHandedCheckBox;
    
    private TextView interfaceOpacityTextView;
    private TextView takeOffThrottleTextView;
    private TextView aileronAndElevatorDeadBandTextView;
    private TextView rudderDeadBandTextView;
    
    private SeekBar interfaceOpacitySeekBar;
    private SeekBar takeOffThrottleSeekBar;
    private SeekBar aileronAndElevatorDeadBandSeekBar;
    private SeekBar rudderDeadBandSeekBar;
    
    private CheckBox[] checkBoxes;
    private View[] clickButtons;
    
    private CheckBox toggleJoypadMode;
    private CheckBox toggleAbsoluteControl;
    private CheckBox toggleLeftHanded;
    private CheckBox togglePairing;
    private CheckBox toggleVideoOnUsb;
    private CheckBox toggleLoopingEnabled;
    private CheckBox toggleOutdoorHull;
    private CheckBox toggleOutdoorFlight;

    private OnSeekBarChangeListener interfaceOpacitySeekBarListener;
    private OnSeekBarChangeListener takeOffThrottleSeekBarListener;
    private OnSeekBarChangeListener aileronAndElevatorDeadBandSeekBarListener;
    private OnSeekBarChangeListener rudderDeadBandSeekBarListener;

    private Resources res;

    private int[] titles;

    public SettingsViewController(Context context, LayoutInflater inflater, ViewGroup container)
    {
    	res = context.getResources();
    	
    	titleTextView = (TextView) container.findViewById(R.id.titleTextView);
    	
        preBtn = (ImageButton)container.findViewById(R.id.preBtn);
        preBtn.setOnClickListener(this);
        
        nextBtn = (ImageButton)container.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(this);
        
        defaultSettingsBtn = (Button)container.findViewById(R.id.defaultSettingsBtn);
        defaultSettingsBtn.setOnClickListener(this);
        
        accCalibrateBtn = (Button)container.findViewById(R.id.accCalibrateBtn);
        accCalibrateBtn.setOnClickListener(this);
        
        magCalibrateBtn = (Button)container.findViewById(R.id.magCalibrateBtn);
        magCalibrateBtn.setOnClickListener(this);

        titles = new int[] {
                R.string.settings_title_connection,
                R.string.settings_title_personal,
                R.string.settings_title_mode,
                R.string.settings_title_about
        };
    	
        clickButtons = new View[] {
        		container.findViewById(R.id.backBtn)
        };
        
        int[] pageIds = new int[]{
        		R.layout.settings_page_connection,
        		R.layout.settings_page_personal,
        		R.layout.settings_page_mode,
        		R.layout.settings_page_about
        };
        
        settingsViews = initPages(inflater, pageIds);
        
        viewPager = (ViewPager) container.findViewById(R.id.viewPager);
        viewPager.setAdapter(new SettingsViewAdapter(settingsViews));
        
        
        ViewPagerIndicator viewPagerIndicator = (ViewPagerIndicator) container.findViewById(R.id.pageIndicator);
        viewPagerIndicator.setViewPager(viewPager);
        viewPagerIndicator.setOnPageChangeListener(this);
        
        final int connectionPageIdx = 0;
        final int interfacePageIdx  = 1;
        final int modePageIdx       = 2;
        final int aboutPageIdx      = 3;

        isLeftHandedCheckBox = (CheckBox)settingsViews.get(interfacePageIdx).findViewById(R.id.isLeftHandedCheckBox);
        
        interfaceOpacityTextView =  (TextView)settingsViews.get(interfacePageIdx).findViewById(R.id.interfaceOpacityTextView);
        takeOffThrottleTextView = (TextView)settingsViews.get(modePageIdx).findViewById(R.id.takeOffThrottleTextView);
        aileronAndElevatorDeadBandTextView = (TextView)settingsViews.get(modePageIdx).findViewById(R.id.aileronAndElevatorDeadBandTextView);
        rudderDeadBandTextView = (TextView)settingsViews.get(modePageIdx).findViewById(R.id.rudderDeadBandTextView);
        
        interfaceOpacitySeekBar = (SeekBar)settingsViews.get(interfacePageIdx).findViewById(R.id.interfaceOpacitySeekBar);
        takeOffThrottleSeekBar = (SeekBar)settingsViews.get(modePageIdx).findViewById(R.id.takeOffThrottleSeekBar);
        aileronAndElevatorDeadBandSeekBar = (SeekBar)settingsViews.get(modePageIdx).findViewById(R.id.aileronAndElevatorDeadBandSeekBar);
        rudderDeadBandSeekBar = (SeekBar)settingsViews.get(modePageIdx).findViewById(R.id.rudderDeadBandSeekBar);
        
        interfaceOpacitySeekBar.setMax(100);
        takeOffThrottleSeekBar.setMax(1000);
        aileronAndElevatorDeadBandSeekBar.setMax(20);
        rudderDeadBandSeekBar.setMax(20);
        
        WebView aboutWebView = (WebView)settingsViews.get(aboutPageIdx).findViewById(R.id.aboutWebView);
        aboutWebView.getSettings().setJavaScriptEnabled(true);  
        aboutWebView.loadUrl("file:///android_asset/About.html");
        
 
        checkBoxes = new CheckBox[] {
        		isLeftHandedCheckBox
        };
        
        ApplicationSettings settings = HexMiniApplication.sharedApplicaion().getAppSettings();
        
        interfaceOpacitySeekBar.setProgress((int)(settings.getInterfaceOpacity() * 100));
        safeSetText(interfaceOpacityTextView, interfaceOpacitySeekBar.getProgress() + "%");
        
        takeOffThrottleSeekBar.setProgress((int)(settings.getTakeOffThrottle() - 1000));
        safeSetText(takeOffThrottleTextView, (takeOffThrottleSeekBar.getProgress() + 1000) + "us");
        
        aileronAndElevatorDeadBandSeekBar.setProgress((int)(settings.getAileronDeadBand() * 100));
        safeSetText(aileronAndElevatorDeadBandTextView, aileronAndElevatorDeadBandSeekBar.getProgress() + "%");
        
        rudderDeadBandSeekBar.setProgress((int)(settings.getRudderDeadBand() * 100));
        safeSetText(rudderDeadBandTextView, rudderDeadBandSeekBar.getProgress() + "%");
        
        
        initListeners();


        
    	/*
        res = context.getResources();
        inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        TILT_DIMENTION = " " + context.getString(R.string.degree_sign);
        YAW_SPEED_MAX_DIMENTION = " " + context.getString(R.string.deg_per_sec);
        VERT_SPEED_MAX_DIMENTION = " " + context.getString(R.string.mm_per_sec);
        ALTITUDE_DIMENSION = " " + context.getString(R.string.meters);

        int[] pageIds = null;
        int ardronePageIdx = -1, flightPageIdx = -1, devicePageIdx = -1, videoPageIdx = -1, aboutPageIdx = -1;

        switch (droneVersion) {
        case DRONE_1:

            titles = new int[] {
                    R.string.PERSONAL_SETTINGS,
                    R.string.FLIGHT_SETTINGS,
                    R.string.PILOTING_MODE,
                    R.string.VIDEO_SETTINGS,
                    R.string.STATUS
            };

            pageIds = new int[] {
                    R.layout.settings_page_ardrone,
                    R.layout.settings_page_flight,
                    R.layout.settings_page_device,
                    R.layout.settings_page_video,
                    R.layout.settings_page_about
            };

            ardronePageIdx = 0;
            flightPageIdx = 1;
            devicePageIdx = 2;
            videoPageIdx = 3;
            aboutPageIdx = 4;

            break;
        case DRONE_2:

            titles = new int[] {
                    R.string.PERSONAL_SETTINGS,
                    R.string.FLIGHT_SETTINGS,
                    R.string.PILOTING_MODE,
                    R.string.STATUS
            };

            pageIds = new int[] {
                    R.layout.ff2_settings_page_ardrone,
                    R.layout.settings_page_flight,
                    magnetoAvailable ? R.layout.ff2_settings_page_device : R.layout.settings_page_device,
                    R.layout.settings_page_about
            };

            ardronePageIdx = 0;
            flightPageIdx = 1;
            devicePageIdx = 2;
            aboutPageIdx = 3;
            break;
        default:
            throw new IllegalStateException("Can't determine drone version");
        }

        settingsViews = initializePages(inflater, pageIds, droneVersion);

        viewPager = (ViewPager) container.findViewById(R.id.viewPager);
        viewPager.setAdapter(new SettingsViewAdapter(settingsViews));

        ViewPagerIndicator viewPagerIndicator = (ViewPagerIndicator) container.findViewById(R.id.pageIndicator);
        viewPagerIndicator.setViewPager(viewPager);
        viewPagerIndicator.setOnPageChangeListener(this);

        TextView lblTiltMax = (TextView) settingsViews.get(flightPageIdx).findViewById(R.id.labelTiltMax);
        String tiltMaxText = lblTiltMax.getText().toString();
        if (tiltMaxText.indexOf("{device}") != -1) {
            tiltMaxText = tiltMaxText.replace("{device}", Build.MANUFACTURER.toUpperCase());
            lblTiltMax.setText(tiltMaxText);
        }

        // toggleAceMode = (ToggleButton)
        // dialog.findViewById(R.id.toggleAceMode);
        toggleJoypadMode = (CheckBox) settingsViews.get(devicePageIdx).findViewById(R.id.toggleAcceleroDisabled);
        toggleAbsoluteControl = (CheckBox) settingsViews.get(devicePageIdx).findViewById(R.id.toggleAbsoluteControl);
        toggleLeftHanded = (CheckBox) settingsViews.get(devicePageIdx).findViewById(R.id.toggleLeftHanded);
        toggleVideoOnUsb = (CheckBox) settingsViews.get(ardronePageIdx).findViewById(R.id.toggleUsbRecord);
        toggleLoopingEnabled = (CheckBox) settingsViews.get(ardronePageIdx).findViewById(R.id.toggleLoopingEnabled);
        // toggleAdaptiveVideo = (ToggleButton)
        // dialog.findViewById(R.id.toggleAdaptiveVideo);
        // toggleAltitudeLimited = (ToggleButton)
        // dialog.findViewById(R.id.toggleAltitudeLimited);
        toggleOutdoorFlight = (CheckBox) settingsViews.get(flightPageIdx).findViewById(R.id.toggleOutdoorFlight);
        toggleOutdoorHull = (CheckBox) settingsViews.get(flightPageIdx).findViewById(R.id.toggleOutdoorHull);
        togglePairing = (CheckBox) settingsViews.get(ardronePageIdx).findViewById(R.id.togglePairing);

        toggleButtons = new CheckBox[] {
                toggleJoypadMode, toggleAbsoluteControl, toggleLeftHanded,
                togglePairing, toggleVideoOnUsb, toggleLoopingEnabled,
                toggleOutdoorFlight, toggleOutdoorHull
        };

        btnScrollLeft = container.findViewById(R.id.btnPrev);
        btnScrollLeft.setOnClickListener(this);
        btnScrollRight = container.findViewById(R.id.btnNext);
        btnScrollRight.setOnClickListener(this);

        btnCalibrateMagneto = (Button) settingsViews.get(devicePageIdx).findViewById(R.id.btnCalibration);
        clickButtons = new View[] {
                btnDefaultSettings = container.findViewById(R.id.btnDefaultSettings),
                btnFlatTrim = container.findViewById(R.id.btnFlatTrim),
                container.findViewById(R.id.btnBack),
                btnCalibrateMagneto = settingsViews.get(devicePageIdx).findViewById(R.id.btnCalibration),
        };

        if (videoPageIdx != -1) {
            rgVideoCodec = (RadioGroup) settingsViews.get(videoPageIdx).findViewById(R.id.rgVideoCodec);
            rbVideoP264 = (RadioButton) settingsViews.get(videoPageIdx).findViewById(R.id.rbVideoP264);
            rbVideoVLIB = (RadioButton) settingsViews.get(videoPageIdx).findViewById(R.id.rbVideoVLIB);
        }

        txtTitle = (TextView) container.findViewById(R.id.txtTitle);
        txtDeviceTiltMaxValue = (TextView) settingsViews.get(devicePageIdx).findViewById(R.id.txtDeviceTiltMax);
        txtInterfaceOpacityValue = (TextView) settingsViews.get(ardronePageIdx).findViewById(
                R.id.textInterfaceOpacityValue);
        txtRotationSpeedMax = (TextView) settingsViews.get(flightPageIdx).findViewById(R.id.txtYawSpeedMax);
        txtVerticalSpeedMax = (TextView) settingsViews.get(flightPageIdx).findViewById(R.id.txtVerticalSpeedMax);
        txtTilt = (TextView) settingsViews.get(flightPageIdx).findViewById(R.id.txtTiltMax);
        txtAltitudeLimit = (TextView) settingsViews.get(flightPageIdx).findViewById(R.id.txtAltitudeLimit);

        txtDroneSoftVersion = (TextView) settingsViews.get(aboutPageIdx).findViewById(R.id.textDroneSoftwareVersion);
        txtDroneHardVersion = (TextView) settingsViews.get(aboutPageIdx).findViewById(R.id.textDroneHardwareVersion);
        txtInertialHardVersion = (TextView) settingsViews.get(aboutPageIdx).findViewById(R.id.textInertialHardware);
        txtInertialSoftVersion = (TextView) settingsViews.get(aboutPageIdx).findViewById(
                R.id.textInertialSoftwareVersion);

        motorType[0] = (TextView) settingsViews.get(aboutPageIdx).findViewById(R.id.textMotor1Type);
        motorType[1] = (TextView) settingsViews.get(aboutPageIdx).findViewById(R.id.textMotor2Type);
        motorType[2] = (TextView) settingsViews.get(aboutPageIdx).findViewById(R.id.textMotor3Type);
        motorType[3] = (TextView) settingsViews.get(aboutPageIdx).findViewById(R.id.textMotor4Type);

        motorSoftVersion[0] = (TextView) settingsViews.get(aboutPageIdx).findViewById(R.id.textMotor1Software);
        motorSoftVersion[1] = (TextView) settingsViews.get(aboutPageIdx).findViewById(R.id.textMotor2Software);
        motorSoftVersion[2] = (TextView) settingsViews.get(aboutPageIdx).findViewById(R.id.textMotor3Software);
        motorSoftVersion[3] = (TextView) settingsViews.get(aboutPageIdx).findViewById(R.id.textMotor4Software);

        motorHardVersion[0] = (TextView) settingsViews.get(aboutPageIdx).findViewById(R.id.textMotor1Hardware);
        motorHardVersion[1] = (TextView) settingsViews.get(aboutPageIdx).findViewById(R.id.textMotor2Hardware);
        motorHardVersion[2] = (TextView) settingsViews.get(aboutPageIdx).findViewById(R.id.textMotor3Hardware);
        motorHardVersion[3] = (TextView) settingsViews.get(aboutPageIdx).findViewById(R.id.textMotor4Hardware);

        editNetworkName = (EditText) settingsViews.get(ardronePageIdx).findViewById(R.id.editNetworkName);

        if (Build.MODEL.equals("Kindle Fire")) {
            // Workaround for Kindle Fire. For some unknown reason "done" button
            // that
            // should be displayed for IME_ACTION_DONE is not visible.
            // Just changing to IME_ACTION_GO
            editNetworkName.setImeOptions(EditorInfo.IME_ACTION_GO);
        }

        editNetworkName.setFilters(new InputFilter[] {
            new NetworkNameFilter()
        });

        seekDeviceTiltMax = (SeekBar) settingsViews.get(devicePageIdx).findViewById(R.id.seekDeviceTiltMax);
        seekInterfaceOpacity = (SeekBar) settingsViews.get(ardronePageIdx).findViewById(R.id.seekInterfaceOpacity);

        seekYawSpeedMax = (SeekBar) settingsViews.get(flightPageIdx).findViewById(R.id.seekYawSpeedMax);
        seekVertSpeedMax = (SeekBar) settingsViews.get(flightPageIdx).findViewById(R.id.seekVerticalSpeedMax);
        seekTilt = (SeekBar) settingsViews.get(flightPageIdx).findViewById(R.id.seekTiltMax);
        seekAltitudeLimit = (SeekBar) settingsViews.get(flightPageIdx).findViewById(R.id.seekAltitudeLimit);

        if (seekDeviceTiltMax != null) {
            seekDeviceTiltMax.setMax(DroneConfig.DEVICE_TILTMAX_MAX
                    - DroneConfig.DEVICE_TILTMAX_MIN);
        }

        if (seekInterfaceOpacity != null) {
            seekInterfaceOpacity
                    .setMax(ApplicationSettings.INTERFACE_OPACITY_MAX
                            - ApplicationSettings.INTERFACE_OPACITY_MIN);
        }

        if (seekYawSpeedMax != null) {
            seekYawSpeedMax.setMax(DroneConfig.YAW_MAX - DroneConfig.YAW_MIN);
        }

        if (seekVertSpeedMax != null) {
            seekVertSpeedMax.setMax(DroneConfig.VERT_SPEED_MAX
                    - DroneConfig.VERT_SPEED_MIN);
        }

        if (seekTilt != null) {
            seekTilt.setMax(DroneConfig.TILT_MAX - DroneConfig.TILT_MIN);
        }

        if (seekAltitudeLimit != null) {
            seekAltitudeLimit.setMax(DroneConfig.ALTITUDE_MAX - DroneConfig.ALTITUDE_MIN);
        }

        acceleroOnlyGroup = new View[] {
                toggleJoypadMode,
                seekDeviceTiltMax
        };

        magnetoOnlyGroup = new View[] {
                toggleAbsoluteControl,
        };

        flyingOnlyGroup = new View[] {
                btnCalibrateMagneto
        };

        landedOnlyGroup = new View[] {
                btnFlatTrim
        };

        connectedOnlyGroup = new View[] {
                btnDefaultSettings,
                btnFlatTrim,
                btnCalibrateMagneto,
                togglePairing,
                toggleVideoOnUsb,
                toggleOutdoorFlight,
                toggleOutdoorHull,
                seekAltitudeLimit,
                seekTilt,
                seekVertSpeedMax,
                seekYawSpeedMax
        };

        initListeners();
        */
    }


    private List<View> initPages(LayoutInflater inflater, int[] pageIds)
    {
        ArrayList<View> pageList = new ArrayList<View>(pageIds.length);

        for (int i = 0; i < pageIds.length; ++i) {
            View view = inflater.inflate(pageIds[i], null);
            //FontUtils.applyFont(inflater.getContext(), (ViewGroup) view);
            pageList.add(view);
        }

        return pageList;
    }


    private void initListeners()
    {
    	interfaceOpacitySeekBarListener = new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				ApplicationSettings settings = HexMiniApplication.sharedApplicaion().getAppSettings();
				settings.setInterfaceOpacity(seekBar.getProgress() / 100.0f);
				settings.save();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
              safeSetText(interfaceOpacityTextView, progress + "%");
			}
		};
		interfaceOpacitySeekBar.setOnSeekBarChangeListener(interfaceOpacitySeekBarListener);
    	
    	takeOffThrottleSeekBarListener = new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				ApplicationSettings settings = HexMiniApplication.sharedApplicaion().getAppSettings();
				settings.setTakeOffThrottle(seekBar.getProgress() + 1000);
				settings.save();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
              safeSetText(takeOffThrottleTextView, (progress + 1000) + "us");
			}
		};
		takeOffThrottleSeekBar.setOnSeekBarChangeListener(takeOffThrottleSeekBarListener);
		
    	aileronAndElevatorDeadBandSeekBarListener = new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				ApplicationSettings settings = HexMiniApplication.sharedApplicaion().getAppSettings();
				settings.setAileronDeadBand(seekBar.getProgress() / 100.f);
				settings.setElevatorDeadBand(settings.getAileronDeadBand());
				settings.save();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
              safeSetText(aileronAndElevatorDeadBandTextView, progress + "%");
			}
		};
		aileronAndElevatorDeadBandSeekBar.setOnSeekBarChangeListener(aileronAndElevatorDeadBandSeekBarListener);
		
    	rudderDeadBandSeekBarListener = new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				ApplicationSettings settings = HexMiniApplication.sharedApplicaion().getAppSettings();
				settings.setRudderDeadBand(seekBar.getProgress() / 100.f);
				settings.save();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
              safeSetText(rudderDeadBandTextView, progress + "%");
			}
		};
		rudderDeadBandSeekBar.setOnSeekBarChangeListener(rudderDeadBandSeekBarListener);
		
		
    	
		/*
        tiltMaxSeekListener = new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar)
            {}


            public void onStopTrackingTouch(SeekBar seekBar)
            {
                if (globalOnSeekChangedListener != null)
                    globalOnSeekChangedListener.onChanged(seekBar, seekBar.getProgress());
            }


            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser)
            {
                safeSetText(txtDeviceTiltMaxValue, "" + (progress
                        + DroneConfig.DEVICE_TILTMAX_MIN) + TILT_DIMENTION);
            }
        };

        interfaceOpacitySeekListener = new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar)
            {}


            public void onStopTrackingTouch(SeekBar seekBar)
            {
                if (globalOnSeekChangedListener != null)
                    globalOnSeekChangedListener.onChanged(seekBar, seekBar.getProgress());
            }


            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser)
            {
                safeSetText(txtInterfaceOpacityValue, "" + (progress
                        + ApplicationSettings.INTERFACE_OPACITY_MIN)
                        + INTERFACE_OPACITY_DIMENTION);
            }
        };

        yawSpeedMaxSeekListener = new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar)
            {}


            public void onStopTrackingTouch(SeekBar seekBar)
            {
                if (globalOnSeekChangedListener != null)
                    globalOnSeekChangedListener.onChanged(seekBar, seekBar.getProgress());
            }


            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser)
            {
                safeSetText(txtRotationSpeedMax, "" + (progress + DroneConfig.YAW_MIN)
                        + YAW_SPEED_MAX_DIMENTION);
            }
        };

        vertSpeedMaxSeekListener = new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar)
            {}


            public void onStopTrackingTouch(SeekBar seekBar)
            {
                if (globalOnSeekChangedListener != null)
                    globalOnSeekChangedListener.onChanged(seekBar, seekBar.getProgress());
            }


            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser)
            {
                safeSetText(txtVerticalSpeedMax, "" + (progress
                        + DroneConfig.VERT_SPEED_MIN) + VERT_SPEED_MAX_DIMENTION);
            }
        };

        tiltSeekListener = new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar)
            {}


            public void onStopTrackingTouch(SeekBar seekBar)
            {
                if (globalOnSeekChangedListener != null)
                    globalOnSeekChangedListener.onChanged(seekBar, seekBar.getProgress());
            }


            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser)
            {
                safeSetText(txtTilt, "" + (progress + DroneConfig.TILT_MIN)
                        + TILT_DIMENTION);
            }
        };

        altitudeLimitSeekListener = new OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar seekBar)
            {
                if (globalOnSeekChangedListener != null) {
                    globalOnSeekChangedListener.onChanged(seekBar, seekBar.getProgress());
                }
            }


            public void onStartTrackingTouch(SeekBar seekBar)
            {
                // TODO Auto-generated method stub

            }


            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser)
            {
                safeSetText(txtAltitudeLimit, "" + (progress + DroneConfig.ALTITUDE_MIN) + ALTITUDE_DIMENSION);
            }
        };
        */

    }


//    public void setCheckBoxesCheckedListener(OnCheckedChangeListener listener)
//    {
//        this.globalOnCheckedChangeListener = listener;
//
//        for (int i = 0; i < checkBoxes.length; ++i) {
//            CheckBox button = checkBoxes[i];
//
//            if (button != null)
//                button.setOnCheckedChangeListener(globalOnCheckedChangeListener);
//        }
//    }


//    public void setSeekBarsOnChangeListener(OnSeekChangedListener listener)
//    {
//        this.globalOnSeekChangedListener = listener;
//    }


    public void setAcceleroDisabledChecked(boolean checked)
    {
        if (toggleJoypadMode != null) {
            toggleJoypadMode.setChecked(checked);
        }
    }


    public void setAcceleroDisabledEnabled(boolean enabled)
    {
        toggleJoypadMode.setEnabled(enabled);
    }


    public boolean isAcceleroDisabledChecked()
    {
        if (toggleJoypadMode != null)
            return toggleJoypadMode.isChecked();

        Log.w(TAG, "Toggle button toggleAccelero is null");
        return false;
    }


    public void setAbsoluteControlChecked(boolean checked)
    {
        if (toggleAbsoluteControl != null) {
            toggleAbsoluteControl.setChecked(checked);
        }
    }


    public boolean isAbsoluteControlChecked()
    {
        return toggleAbsoluteControl.isChecked();
    }


    public void setLeftHandedChecked(boolean checked)
    {
        if (toggleLeftHanded != null) {
            toggleLeftHanded.setChecked(checked);
        }
    }


    public boolean isLeftHandedChecked()
    {
        if (toggleLeftHanded != null) { return toggleLeftHanded.isChecked(); }

        Log.w(TAG, "Toggle button toggleLeftHanded is null");
        return false;
    }

/*
    public void setTiltMax(int tiltMax)
    {
        if (tiltMax < DroneConfig.DEVICE_TILTMAX_MIN
                || tiltMax > DroneConfig.DEVICE_TILTMAX_MAX) {
            // throw new IllegalArgumentException("tilt: " + tiltMax);

        }

        if (txtDeviceTiltMaxValue != null) {
            txtDeviceTiltMaxValue.setText("" + tiltMax + TILT_DIMENTION);
        }

        if (seekDeviceTiltMax != null) {
            seekDeviceTiltMax.setOnSeekBarChangeListener(null);
            seekDeviceTiltMax.setProgress(tiltMax - DroneConfig.DEVICE_TILTMAX_MIN);
            seekDeviceTiltMax.setOnSeekBarChangeListener(tiltMaxSeekListener);
        }
    }


    public int getTiltMax()
    {
        return seekDeviceTiltMax.getProgress() + DroneConfig.DEVICE_TILTMAX_MIN;
    }


    public void setAltitudeLimit(int altitude)
    {
        if (altitude < DroneConfig.ALTITUDE_MIN
                || altitude > DroneConfig.ALTITUDE_MAX) { throw new IllegalArgumentException("Wrong altitude"); }

        if (txtAltitudeLimit != null) {
            txtAltitudeLimit.setText("" + altitude + ALTITUDE_DIMENSION);
        }

        if (seekAltitudeLimit != null) {
            seekAltitudeLimit.setOnSeekBarChangeListener(null);
            seekAltitudeLimit.setProgress(altitude - DroneConfig.ALTITUDE_MIN);
            seekAltitudeLimit.setOnSeekBarChangeListener(altitudeLimitSeekListener);
        }
    }


    public int getAltitudeLimit()
    {
        return seekAltitudeLimit.getProgress() + DroneConfig.ALTITUDE_MIN;
    }
*/
/*
    public void setInterfaceOpacity(int opacity)
    {
        if (opacity < ApplicationSettings.INTERFACE_OPACITY_MIN
                || opacity > ApplicationSettings.INTERFACE_OPACITY_MAX) { throw new IllegalArgumentException(); }

        if (txtInterfaceOpacityValue != null) {
            txtInterfaceOpacityValue.setText("" + opacity
                    + INTERFACE_OPACITY_DIMENTION);
        }

        if (seekInterfaceOpacity != null) {
            seekInterfaceOpacity.setOnSeekBarChangeListener(null);
            seekInterfaceOpacity.setProgress(opacity
                    - ApplicationSettings.INTERFACE_OPACITY_MIN);
            seekInterfaceOpacity
                    .setOnSeekBarChangeListener(interfaceOpacitySeekListener);
        }
    }


    public int getInterfaceOpacity()
    {
        return seekInterfaceOpacity.getProgress() + ApplicationSettings.INTERFACE_OPACITY_MIN;
    }


    public void setYawSpeedMax(int speed)
    {
        if (speed < DroneConfig.YAW_MIN || speed > DroneConfig.YAW_MAX) {
            Log.e(TAG, "Yaw exceeds bounds. Yaw: " + speed);
            // throw new IllegalArgumentException();
            if (speed > DroneConfig.YAW_MAX) {
                speed = DroneConfig.YAW_MAX;
            } else {
                speed = DroneConfig.YAW_MIN;
            }
        }

        safeSetText(txtRotationSpeedMax, "" + speed + YAW_SPEED_MAX_DIMENTION);

        if (seekYawSpeedMax != null) {
            seekYawSpeedMax.setOnSeekBarChangeListener(null);
            seekYawSpeedMax.setProgress(speed - DroneConfig.YAW_MIN);
            seekYawSpeedMax.setOnSeekBarChangeListener(yawSpeedMaxSeekListener);
        }
    }


    public int getYawSpeedMax()
    {
        if (seekYawSpeedMax != null)
            return seekYawSpeedMax.getProgress() + DroneConfig.YAW_MIN;

        return DroneConfig.YAW_MIN;
    }


    public void setVerticalSpeedMax(int speed)
    {
        if (speed < DroneConfig.VERT_SPEED_MIN
                || speed > DroneConfig.VERT_SPEED_MAX) { throw new IllegalArgumentException(); }

        safeSetText(txtVerticalSpeedMax, "" + speed + VERT_SPEED_MAX_DIMENTION);

        if (seekVertSpeedMax != null) {
            seekVertSpeedMax.setOnSeekBarChangeListener(null);
            seekVertSpeedMax.setProgress(speed - DroneConfig.VERT_SPEED_MIN);
            seekVertSpeedMax
                    .setOnSeekBarChangeListener(vertSpeedMaxSeekListener);
        }
    }


    public int getVerticalSpeedMax()
    {
        if (seekVertSpeedMax != null)
            return seekVertSpeedMax.getProgress() + DroneConfig.VERT_SPEED_MIN;

        return DroneConfig.VERT_SPEED_MIN;
    }


    public void setTilt(int tilt)
    {
        if (tilt < DroneConfig.TILT_MIN || tilt > DroneConfig.TILT_MAX) {
            if (tilt < DroneConfig.TILT_MIN) {
                tilt = DroneConfig.TILT_MIN;
            } else {
                tilt = DroneConfig.TILT_MAX;
            }
        }

        safeSetText(txtTilt, "" + tilt + TILT_DIMENTION);

        if (seekTilt != null) {
            seekTilt.setOnSeekBarChangeListener(null);
            seekTilt.setProgress(tilt - DroneConfig.TILT_MIN);
            seekTilt.setOnSeekBarChangeListener(tiltSeekListener);
        }
    }


    public int getTilt()
    {
        if (seekTilt != null)
            return seekTilt.getProgress() + DroneConfig.TILT_MIN;

        return DroneConfig.TILT_MIN;
    }
*/


    public void setPairing(boolean checked)
    {
        togglePairing.setChecked(checked);
    }


    public boolean isPairingChecked()
    {
        return togglePairing.isChecked();
    }


    public void setAceMode(boolean checked)
    {
        // if (toggleAceMode != null) {
        // toggleAceMode.setChecked(checked);
        // }
    }


    public void setAceModeEnabled(boolean enabled)
    {
        // if (toggleAceMode != null) {
        // toggleAceMode.setEnabled(enabled);
        // }
    }


    public boolean isAceModeChecked()
    {
        // if (toggleAceMode != null) {
        // return toggleAceMode.isChecked();
        // }

        return false;
    }


    public void setAdaptiveVideo(boolean checked)
    {
        // toggleAdaptiveVideo.setChecked(checked);
    }


    public boolean isAdapriveVideoChecked()
    {
        // return toggleAdaptiveVideo.isChecked();
        return false;
    }


    public void setOutdoorHull(boolean checked)
    {
        toggleOutdoorHull.setChecked(checked);
    }


    public boolean isOutdoorHullChecked()
    {
        return toggleOutdoorHull.isChecked();
    }


    public void setOutdoorFlight(boolean checked)
    {
        toggleOutdoorFlight.setChecked(checked);
    }


    public boolean isOutdoorFlightChecked()
    {
        return toggleOutdoorFlight.isChecked();
    }


//    public void setButtonsEnabled(boolean enabled)
//    {
//        editNetworkName.setEnabled(enabled);
//
//        for (View btn : clickButtons) {
//            if (btn != null) {
//                if (btn.getId() != R.id.btnCalibration) {
//                    btn.setEnabled(enabled);
//                }
//            }
//        }
//    }


    /*
     * Private
     */

    private void safeSetText(final TextView view, final String text)
    {
        if (view != null) {
            view.setText(text);
        }
    }


    public void setButtonsOnClickListener(OnClickListener listener)
    {
        for (View button : clickButtons) {
            if (button != null) {
                button.setOnClickListener(listener);
            }
        }
    }


    public void onPageScrollStateChanged(int state)
    {
        // Left unimplemented
    }


    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
        // Left unimplemented
    }


    public void onPageSelected(int position)
    {
        if (position == 0 && preBtn.getVisibility() != View.INVISIBLE) {
            preBtn.setVisibility(View.INVISIBLE);
        } else if (preBtn.getVisibility() != View.VISIBLE) {
            preBtn.setVisibility(View.VISIBLE);
        }

        if (nextBtn.getVisibility() != View.INVISIBLE && position == (viewPager.getAdapter().getCount() - 1)) {
            nextBtn.setVisibility(View.INVISIBLE);
        } else if (nextBtn.getVisibility() != View.VISIBLE) {
            nextBtn.setVisibility(View.VISIBLE);
        }

        if(titleTextView == null){
        	Log.d("Debug", "titleTextView is null");
        }
        
        
        titleTextView.setText(res.getString(titles[position]));
    }


    public void onClick(View v)
    {	
        switch (v.getId()) {
        case R.id.preBtn:
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
            break;
        case R.id.nextBtn:
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            break;
        case R.id.defaultSettingsBtn:
        	//to do reset to default
        	break;
        case R.id.accCalibrateBtn:
        	//to do calibrate acc
        	break;
        case R.id.magCalibrateBtn:
        	//to do calibrate mag
        	break;
        }
    }


    public void setRecordOnUsb(boolean recordOnUsb)
    {
        if (toggleVideoOnUsb != null) {
            toggleVideoOnUsb.setChecked(recordOnUsb);
        }
    }


    public void setLoopingEnabled(boolean loopingEnabled)
    {
        if (toggleLoopingEnabled != null) {
            toggleLoopingEnabled.setChecked(loopingEnabled);
        }

    }
}
