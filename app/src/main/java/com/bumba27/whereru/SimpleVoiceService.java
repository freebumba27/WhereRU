package com.bumba27.whereru;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.bumba27.utils.ReusableClass;

import java.util.ArrayList;

public class SimpleVoiceService extends Service implements RecognitionListener {

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    Context con;
    boolean matched = false;

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

        mute();
        speech.startListening(recognizerIntent);
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
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!matched) {
                    mute();
                    speech.startListening(recognizerIntent);
                }
            }
        }, 800);
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
                if(ReusableClass.getFromPreference("onOffFlag", SimpleVoiceService.this).equalsIgnoreCase("on"))
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
        String savedText = ReusableClass.getFromPreference("RecordedText", SimpleVoiceService.this);


        //Toast.makeText(con, "Word captured: " + text, Toast.LENGTH_LONG).show();
        if(text.matches("(.*)android phone(.*)") || (text.matches("(.*)" + savedText + "(.*)") && !savedText.equalsIgnoreCase("")))
        {
            try
            {
                Log.i(LOG_TAG, "Matched");
                matched = true;

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {1000, 2000, 3000, 4000, 1000, 2000, 3000, 4000, 1000, 2000, 3000, 4000, 1000, 2000, 3000, 4000, 1000, 2000, 3000, 4000, 1000, 2000, 3000, 4000};
                v.vibrate(pattern, -1);

                Uri alert = null;
                if (ReusableClass.getFromPreference("ring_tone_uri", SimpleVoiceService.this).equalsIgnoreCase("")) {
                    alert = Uri.parse("android.resource://" + con.getPackageName() + "/" + R.raw.i_am_here);
                } else {
                    alert = Uri.parse(ReusableClass.getFromPreference("ring_tone_uri", SimpleVoiceService.this));
                }


                try
                {
                    AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                    int maxVolumeMusic = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolumeMusic,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                final MediaPlayer mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(this, alert);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();

                        mute();
                        speech.startListening(recognizerIntent);
                        matched = false;
                    }
                }, 1000*60);
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

    public void mute() {
        //mute audio
        Log.d("TAG", "Mute");
        AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
    }

    public void unMute() {
        //unmute audio
        Log.d("TAG", "UnMute");
        AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
    }
}