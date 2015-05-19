package com.bumba27.whereru;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumba27.utils.ReuseableClass;

import java.net.URI;


public class SettingsActivity extends Activity {

    URI ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    public void openingRingToneLists(View v) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.select_your_ring_tone));
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        this.startActivityForResult(intent, 5);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                Toast.makeText(getBaseContext(), getString(R.string.thanks_for_saving_ringtone), Toast.LENGTH_SHORT).show();
                ReuseableClass.saveInPreference("ring_tone_uri", uri.toString(), SettingsActivity.this);
            }
            else
            {
                Toast.makeText(getBaseContext(), getString(R.string.ring_tone_selection_error), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
