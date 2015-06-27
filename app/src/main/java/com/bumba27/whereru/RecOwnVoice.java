package com.bumba27.whereru;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumba27.utils.ReusableClass;


public class RecOwnVoice extends Activity {

    TextView textViewResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_own_voice);

        textViewResult = (TextView)findViewById(R.id.textViewResult);
        ((TextView)findViewById(R.id.textViewTitle)).setTypeface(ReusableClass.getFontGenericaBoldStyle(this));
        textViewResult.setTypeface(ReusableClass.getFontGenericaBoldStyle(this));
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, SettingsActivity.class);
        finish();
        startActivity(i);
    }

    public void openingGoogleVoice(View view) {
        textViewResult.setText("");
        /* Call Activity for Voice Input */
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        try {
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(RecOwnVoice.this, getString(R.string.device_do_not_support_text_to_speech), Toast.LENGTH_SHORT).show();
        }
    }

    /* When Mic activity close */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    String yourResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
                    textViewResult.setText(yourResult);
                }
                break;
            }
        }
    }

    public void saveRecordedText(View view) {

        if(textViewResult.getText().toString().length()>0) {
            ReusableClass.saveInPreference("RecordedText", textViewResult.getText().toString(), RecOwnVoice.this);
            Toast.makeText(this, getString(R.string.thanks_message_on_saving_own_word), Toast.LENGTH_LONG).show();

            Intent i = new Intent(this, SettingsActivity.class);
            finish();
            startActivity(i);
        }
        else
            Toast.makeText(this, getString(R.string.no_word_error) , Toast.LENGTH_LONG).show();
    }
}
