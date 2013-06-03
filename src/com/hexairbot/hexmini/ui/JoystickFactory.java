/*
 * JoystickFactory
 *
 *  Created on: May 26, 2011
 *      Author: Dmytro Baryskyy
 */

package com.hexairbot.hexmini.ui;


import com.hexairbot.hexmini.ui.Sprite.Align;

import android.content.Context;


public class JoystickFactory 
{
	public static JoystickBase createAnalogueJoystick(Context context, boolean absolute,
															JoystickListener analogueListener)
	{
		AnalogueJoystick joy = new AnalogueJoystick(context, Align.NO_ALIGN, absolute);
		joy.setOnAnalogueChangedListener(analogueListener);
		
		return joy;
	}
	
	public static JoystickBase createCombinedJoystick(Context context, 
															boolean absolute,
															JoystickListener analogueListener,
															JoystickListener acceleroListener)
	{
		JoystickBase joy = new AnalogueJoystick(context, Align.NO_ALIGN, absolute);
		joy.setOnAnalogueChangedListener(analogueListener);
		
		return joy;
	}
}