package com.bumba27.whereru;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class SimpleVoiceService extends Service implements RecognitionListener {

	private SpeechRecognizer speech = null;
	private Intent recognizerIntent;
	private String LOG_TAG = "VoiceRecognitionActivity";
	Context con;
	private AudioManager mAudioManager;
	private int mStreamVolume = 0; 

	@Override
	public void onCreate() {
		super.onCreate();

		con = SimpleVoiceService.this;
		speech = SpeechRecognizer.createSpeechRecognizer(this);
		speech.setRecognitionListener(this);

		recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en");
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,this.getPackageName());
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

		speech.startListening(recognizerIntent);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // getting system volume into var for later un-muting 
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0); // setting system volume to zero, muting
		//speech.stopListening();
	}



	@Override
	public void onBeginningOfSpeech() 
	{
		Log.i(LOG_TAG, "onBeginningOfSpeech");
	}

	@Override
	public void onBufferReceived(byte[] buffer) 
	{
		Log.i(LOG_TAG, "onBufferReceived: " + buffer);
	}

	@Override
	public void onEndOfSpeech() 
	{
		Log.i(LOG_TAG, "onEndOfSpeech");
		speech.startListening(recognizerIntent);
		//stopSelf();
	}

	@Override
	public void onError(int errorCode) 
	{
		String errorMessage = getErrorText(errorCode);
		Log.d(LOG_TAG, "FAILED " + errorMessage);
		
		
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() 
			{
				if(getFromPreference("onOffFlag", SimpleVoiceService.this).equalsIgnoreCase("on"))
				{
					speech.startListening(recognizerIntent);
				}
				else
				{
					//speech.stopListening();
					return;
				}
			}
		}, 800);
		//stopSelf();
	}

	@Override
	public void onEvent(int arg0, Bundle arg1) {
		Log.i(LOG_TAG, "onEvent");
	}

	@Override
	public void onPartialResults(Bundle arg0) {
		Log.i(LOG_TAG, "onPartialResults");
	}

	@Override
	public void onReadyForSpeech(Bundle arg0) {
	    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mStreamVolume, 0); // again setting the system volume back to the original, un-mutting
		Log.i(LOG_TAG, "onReadyForSpeech");
	}

	@Override
	public void onResults(Bundle results) {
		Log.i(LOG_TAG, "onResults");
		ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		String text = "";
		for (String result : matches)
			text += result + " ";

		Log.i(LOG_TAG, "Result: " + text);
		Toast.makeText(con, "Word captured: " + text, Toast.LENGTH_LONG).show();
		if(text.contains("android phone"))
		{
			try
			{
				Log.i(LOG_TAG, "Matched");
				
//				MediaPlayer mediaPlayer = MediaPlayer.create(con, R.raw.i_am_here);
//				mediaPlayer.start();
//				Vibrator v = (Vibrator) this.con.getSystemService(Context.VIBRATOR_SERVICE);
//				v.vibrate(50);
				
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
				v.vibrate(pattern, -1);

		        Uri alert = Uri.parse("android.resource://" + con.getPackageName() + "/" + R.raw.i_am_here);
		        final MediaPlayer mMediaPlayer = new MediaPlayer();

		        mMediaPlayer.setDataSource(this, alert);

		        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		        try 
		        {
		        	mMediaPlayer.setVolume(Float.parseFloat(Double.toString(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) / 7.0)),
		                                Float.parseFloat(Double.toString(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) / 7.0)));
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		        
		        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) 
		        {
		            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		            mMediaPlayer.setLooping(true);
		            mMediaPlayer.prepare();
		            mMediaPlayer.start();
		        }
		        
		        final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() 
					{
						mMediaPlayer.stop();
						mMediaPlayer.release();
					}
				}, 1000*30);
			} 
			catch(Exception e)
			{
				// handle error here.. 
			}

		}
		//stopSelf();
	}

	@Override
	public void onRmsChanged(float rmsdB) 
	{
		Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
	}

	public static String getErrorText(int errorCode) {
		String message;
		switch (errorCode) {
		case SpeechRecognizer.ERROR_AUDIO:
			message = "Audio recording error";
			break;
		case SpeechRecognizer.ERROR_CLIENT:
			message = "Client side error";
			break;
		case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
			message = "Insufficient permissions";
			break;
		case SpeechRecognizer.ERROR_NETWORK:
			message = "Network error";
			break;
		case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
			message = "Network timeout";
			break;
		case SpeechRecognizer.ERROR_NO_MATCH:
			message = "No match";
			break;
		case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
			message = "RecognitionService busy";
			break;
		case SpeechRecognizer.ERROR_SERVER:
			message = "error from server";
			break;
		case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
			message = "No speech input";
			break;
		default:
			message = "Didn't understand, please try again.";
			break;
		}
		return message;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//speech.stopListening();
		//sendBroadcast(new Intent("YouWillNeverKillMe"));
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	//===================================================================================================================================
	//Preference variable
	//===================================================================================================================================

	//--------------------------------------------
	// method to save variable in preference
	//--------------------------------------------
	public static void saveInPreference(String name, String content, Activity myActivity) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(myActivity);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(name, content);
		editor.commit();
	}

	//--------------------------------------------
	// getting content from preferences
	//--------------------------------------------
	public static String getFromPreference(String variable_name, Context con) {
		String preference_return;
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(con);
		preference_return = preferences.getString(variable_name, "");

		return preference_return;
	}

	//===================================================================================================================================
	//Preference variable
	//===================================================================================================================================

}