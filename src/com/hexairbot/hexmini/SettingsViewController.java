
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
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

public class SettingsViewController
        implements OnPageChangeListener,
        OnClickListener
{

    private static final String TAG = "SettingsViewController";

    private List<View> settingsViews;
    
    private Button defaultSettingsBtn;
    private Button accCalibrateBtn;
    private Button magCalibrateBtn;

    private View btnCalibrateMagneto;
    private View btnFlatTrim;
    private View btnDefaultSettings;

    // private CheckBox toggleAceMode;
    private CheckBox toggleJoypadMode;
    private CheckBox toggleAbsoluteControl;
    private CheckBox toggleLeftHanded;
    private CheckBox togglePairing;
    private CheckBox toggleVideoOnUsb;
    private CheckBox toggleLoopingEnabled;
    // private CheckBox toggleAltitudeLimited;
    // private CheckBox toggleAdaptiveVideo;
    private CheckBox toggleOutdoorHull;
    private CheckBox toggleOutdoorFlight;

    private CheckBox[] toggleButtons;
    private View[] clickButtons;

    private TextView titleTextView;
    private TextView txtDeviceTiltMaxValue;
    private TextView txtInterfaceOpacityValue;
    private TextView txtRotationSpeedMax;
    private TextView txtVerticalSpeedMax;
    private TextView txtTilt;
    private TextView txtDroneSoftVersion;
    private TextView txtDroneHardVersion;
    private TextView txtInertialHardVersion;
    private TextView txtInertialSoftVersion;
    private TextView txtAltitudeLimit;

    private TextView[] motorType = {
            null, null, null, null
    };
    private TextView[] motorHardVersion = {
            null, null, null, null
    };
    private TextView[] motorSoftVersion = {
            null, null, null, null
    };

    private EditText editNetworkName;

    private RadioGroup rgVideoCodec;
    // private RadioGroup rgVideoCodec2;
    private RadioButton rbVideoVLIB;
    private RadioButton rbVideoP264;
    // private RadioButton rbMPEG4_720p;
    // private RadioButton rbH264_360p;
    // private RadioButton rbH264_720p;

    private SeekBar seekDeviceTiltMax;
    private SeekBar seekInterfaceOpacity;
    private SeekBar seekYawSpeedMax;
    private SeekBar seekVertSpeedMax;
    private SeekBar seekTilt;
    private SeekBar seekAltitudeLimit;

    private ViewPager viewPager;
    private ImageButton preBtn;
    private ImageButton nextBtn;

    private OnSeekBarChangeListener tiltMaxSeekListener;
    private OnSeekBarChangeListener interfaceOpacitySeekListener;
    private OnSeekBarChangeListener yawSpeedMaxSeekListener;
    private OnSeekBarChangeListener vertSpeedMaxSeekListener;
    private OnSeekBarChangeListener tiltSeekListener;
    private OnSeekBarChangeListener altitudeLimitSeekListener;

    private OnCheckedChangeListener globalOnCheckedChangeListener;
    //private OnSeekChangedListener globalOnSeekChangedListener;

    private String TILT_DIMENTION = " \u00B0";
    private String INTERFACE_OPACITY_DIMENTION = " %";
    private String YAW_SPEED_MAX_DIMENTION = " \u00B0/s";
    private String VERT_SPEED_MAX_DIMENTION = " mm/s";
    private String ALTITUDE_DIMENSION = " m";

    private Resources res;
    private InputMethodManager inputManager;

    private int[] titles;

    // Groups of controls that will be enabled/disabled depending on the
    // conditions.
    private View[] acceleroOnlyGroup;
    private View[] magnetoOnlyGroup;
    private View[] flyingOnlyGroup;
    private View[] landedOnlyGroup;
    private View[] connectedOnlyGroup;

    private boolean accelAvailable;
    private boolean magnetoAvailable;
    private boolean connected;
    private boolean flying;

    private OnEditorActionListener editNetworkNameActionListener;


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


    public void setNetworkNameOnEditorActionListener(OnEditorActionListener listener)
    {
        this.editNetworkNameActionListener = listener;
        
        editNetworkName.setOnEditorActionListener(new OnEditorActionListener() {
            
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {   
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Avoid the focus to be put in the field automatically
                setNetworkNameFocusable(false);

                if (editNetworkNameActionListener != null) {
                    return editNetworkNameActionListener.onEditorAction(v, actionId, event);
                } else {
                    return false;
                }
            }
        });
        
        editNetworkName.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setFocusableInTouchMode(true);
                }

                return false;
            }
        });
    }


    public void setToggleButtonsCheckedListener(OnCheckedChangeListener listener)
    {
        this.globalOnCheckedChangeListener = listener;

        for (int i = 0; i < toggleButtons.length; ++i) {
            CheckBox button = toggleButtons[i];

            if (button != null)
                button.setOnCheckedChangeListener(globalOnCheckedChangeListener);
        }
    }


    public void setRadioButtonsCheckedListener(android.widget.RadioGroup.OnCheckedChangeListener listener)
    {
        if (rgVideoCodec != null) {
            rgVideoCodec.setOnCheckedChangeListener(listener);

            // if (rgVideoCodec2 != null) {
            // rgVideoCodec2.setOnCheckedChangeListener(listener);
            // }
        }
    }


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

    public void setDroneVersion(String hardwareVersion, String softwareVersion)
    {
        safeSetText(txtDroneHardVersion, hardwareVersion);
        safeSetText(txtDroneSoftVersion, softwareVersion);
    }


    public void setInertialVersion(String inertialHardwareVersion, String inertialSoftwareVersion)
    {
        if (inertialHardwareVersion != null) {
            safeSetText(txtInertialHardVersion, inertialHardwareVersion.length() > 0 ? inertialHardwareVersion : "0.0");
        } else {
            txtInertialHardVersion.setText("0.0");
        }

        if (inertialSoftwareVersion != null) {
            safeSetText(txtInertialSoftVersion, inertialSoftwareVersion.length() > 0 ? inertialSoftwareVersion : "0.0");
        } else {
            txtInertialHardVersion.setText("0.0");
        }
    }


    public void setMotorVersion(int motorIdx, String type, String hardVersion, String softVersion)
    {
        safeSetText(motorType[motorIdx], type);
        safeSetText(motorHardVersion[motorIdx], hardVersion);
        safeSetText(motorSoftVersion[motorIdx], softVersion);
    }


    public void setNetworkName(String networkName)
    {
        editNetworkName.setText(networkName);
    }


    public void setNetworkNameFocusable(boolean b)
    {
        editNetworkName.setFocusableInTouchMode(b);

        if (!b) {
            editNetworkName.clearFocus();
        }
    }


    public String getNetworkName()
    {
        return editNetworkName.getText().toString();
    }


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


    public void setVideoP264Checked(boolean b)
    {
        if (rbVideoP264 != null) {
            rbVideoP264.setChecked(b);
        }
    }


    public void setVideoVLIBChecked(boolean b)
    {
        if (rbVideoVLIB != null) {
            rbVideoVLIB.setChecked(b);
        }
    }


    public void setOutdoorFlight(boolean checked)
    {
        toggleOutdoorFlight.setChecked(checked);
    }


    public boolean isOutdoorFlightChecked()
    {
        return toggleOutdoorFlight.isChecked();
    }


    public void setOutdoorFlightControlsEnabled(boolean enabled)
    {
        if (connected) {
            toggleOutdoorFlight.setEnabled(enabled);
            seekYawSpeedMax.setEnabled(enabled);
            seekVertSpeedMax.setEnabled(enabled);
            seekTilt.setEnabled(enabled);
        }
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


    public void setConnected(boolean connected)
    {
        this.connected = connected;
    }


    public void setAcceleroAvailable(boolean available)
    {
        this.accelAvailable = available;
    }


    public void setMagnetoAvailable(boolean available)
    {
        this.magnetoAvailable = available;
    }


    public void setFlying(boolean flying)
    {
        this.flying = flying;
    }


    public void enableAvailableSettings()
    {
        setGroupEnabled(connectedOnlyGroup, connected, false);
        setGroupEnabled(landedOnlyGroup, !flying, true);
        setGroupEnabled(flyingOnlyGroup, flying, true);
        setGroupEnabled(acceleroOnlyGroup, accelAvailable, true);
        setGroupEnabled(magnetoOnlyGroup, magnetoAvailable, true);
        setGroupVisible(magnetoOnlyGroup, magnetoAvailable);
    }


    public void disableControlsThatRequireDroneConnection()
    {
        setGroupEnabled(connectedOnlyGroup, false, false);
    }


    private void setGroupEnabled(View[] group, boolean enabled, boolean disableOnly)
    {
        for (int i = 0; i < group.length; ++i) {
            View v = group[i];

            if (v != null) {
                if (disableOnly && !enabled && v.isEnabled() == true) {
                    v.setEnabled(enabled);
                } else if (!disableOnly) {
                    v.setEnabled(enabled);
                }
            }
        }
    }


    private void setGroupVisible(View[] group, boolean visible)
    {
        for (int i = 0; i < group.length; ++i) {
            View v = group[i];

            if (v != null) {
                v.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }
    }
}
