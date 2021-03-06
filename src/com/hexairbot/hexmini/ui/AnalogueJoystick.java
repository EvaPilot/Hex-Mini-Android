/*
 * AnalogueJoystick
 *
 *  Created on: May 26, 2011
 *      Author: Dmytro Baryskyy
 */

package com.hexairbot.hexmini.ui;

import com.hexairbot.hexmini.R;

import android.content.Context;


public class AnalogueJoystick 
	extends JoystickBase
{

	public AnalogueJoystick(Context context, Align align, boolean absolute) 
	{
		super(context, align, absolute);
	}
 
	@Override
	protected int getBackgroundDrawableId() 
	{
		return R.drawable.joystick_bg;
	}

	
	@Override
	protected int getTumbDrawableId() 
	{
		return R.drawable.joystick_rudder_throttle;
	}
}
