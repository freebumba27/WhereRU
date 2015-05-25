package com.bumba27.whereru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumba27.utils.ReusableClass;

public class MainActivity extends Activity{

	ImageView imageViewOnOffButton;
	TextView TextViewMessage;
	private static final int TIME_INTERVAL = 2000;
	private long mBackPressed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		imageViewOnOffButton = (ImageView)findViewById(R.id.imageViewOnOffButton);
		TextViewMessage		 = (TextView)findViewById(R.id.TextViewMessage);
		TextViewMessage.setTypeface(ReusableClass.getFontGillSansMTProStyle(this));

		if(ReusableClass.getFromPreference("onOffFlag", MainActivity.this).equalsIgnoreCase("off") || ReusableClass.getFromPreference("onOffFlag", MainActivity.this).equalsIgnoreCase(""))
		{
			imageViewOnOffButton.setImageResource(R.drawable.off_btn);
			TextViewMessage.setText(getString(R.string.stop_listening));
		}
		else if(ReusableClass.getFromPreference("onOffFlag", MainActivity.this).equalsIgnoreCase("on"))
		{
			imageViewOnOffButton.setImageResource(R.drawable.on_btn);
			TextViewMessage.setText(getString(R.string.start_listening));
		}
	}

	//Your phone will  when you will call him as "android phone" loudly 
	public void OnOffClicked(View v) 
	{
		if(ReusableClass.getFromPreference("onOffFlag", MainActivity.this).equalsIgnoreCase("off") || ReusableClass.getFromPreference("onOffFlag", MainActivity.this).equalsIgnoreCase(""))
		{
			imageViewOnOffButton.setImageResource(R.drawable.on_btn);
			TextViewMessage.setText(R.string.start_listening);
			ReusableClass.saveInPreference("onOffFlag", "on", MainActivity.this);
			startService(new Intent(this, SimpleVoiceService.class));
		}
		else if(ReusableClass.getFromPreference("onOffFlag", MainActivity.this).equalsIgnoreCase("on"))
		{
			imageViewOnOffButton.setImageResource(R.drawable.off_btn);
			//Your phone is not listening to you !!
			TextViewMessage.setText(R.string.stop_listening);
			ReusableClass.saveInPreference("onOffFlag", "off", MainActivity.this);
			stopService(new Intent(this, SimpleVoiceService.class));
		}
	}

	//Settings Button Click
	public void goingToSettings(View v)
	{
		Intent myIntent = new Intent(this, SettingsActivity.class);
		finish();
		startActivity(myIntent);
	}
	
	@Override
	public void onBackPressed()
	{
		if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
		{
			super.onBackPressed();
			return;
		}
		else { Toast.makeText(getBaseContext(), getString(R.string.double_back_press_message), Toast.LENGTH_SHORT).show(); }

		mBackPressed = System.currentTimeMillis();
	}
}
