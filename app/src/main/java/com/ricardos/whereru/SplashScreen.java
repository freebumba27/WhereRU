package com.ricardos.whereru;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ricardos.utils.ReusableClass;

import inapputils.IabHelper;
import inapputils.IabResult;
import inapputils.Inventory;
import inapputils.Purchase;


public class SplashScreen extends Activity {

    //-----------------------------------------------------------------------------
    //In App Purchase
    //-----------------------------------------------------------------------------

    IabHelper mHelper;
    static final String ITEM_SKU = "full_version_001";

    //-----------------------------------------------------------------------------
    //In App Purchase
    //-----------------------------------------------------------------------------


    LinearLayout logo_layout;
    boolean backPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spash_screen);

        //-------------------------------------------------------------------------
        // In App Purchase
        //-------------------------------------------------------------------------

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAigzTq89WPE7xJHy0d+VKrMCx3lelvBMiP67V4KjpKPnD+Tt1eqtZPzvuERF0bC4xJDmHgUTYc3vt729CxE71h8R22N/y1N0Nh3mCPR4XLOHas0eq9bjArk1MkMPFwgkE23MmmLlfDQun3j3KbFRDfxMbFRYsavNEIdFlTnk0B4CnPi0Lr/RgoaCZLZzFfwFN76qZGFiB2/gG2BcopfpaMwbO5ZRVAtSJLugJx2ysakQ+vcD+QtbsxiWe71sr1lw2zrQ94VN+9GowmTCxGvw3ESMsXf4ZkdysJDf0EdlxiGNERhTOcktBlLOfKBCWpSJZb4o0Qkd/uydn1x6kAfoIRQIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {

                if (!result.isSuccess()) {
                    Log.i("TAG", "Problem setting up in-app billing: " + result);
                    return;
                } else {
                    Log.d("TAG", "In-app Billing is set up OK");
                }
            }
        });

        //-------------------------------------------------------------------------
        // In App Purchase
        //-------------------------------------------------------------------------

        if (ReusableClass.getFromPreference("appStartingDate", this).equalsIgnoreCase("")) {
            ReusableClass.saveInPreference("appStartingDate", System.currentTimeMillis() + "", this);
        }


        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        logo_layout = (LinearLayout) findViewById(R.id.linearLayoutLogoLayout);
        logo_layout.setAnimation(slideUp);



        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - Long.parseLong(ReusableClass.getFromPreference("appStartingDate", SplashScreen.this));
        Log.v("TAG", "firstTime: " + Long.parseLong(ReusableClass.getFromPreference("appStartingDate", SplashScreen.this)));
        Log.v("TAG", "currentTime: " + currentTime);
        Log.v("TAG", "Time Diff: " + timeDiff);

        //Starting date ~ 1424165427370 = 17-02-2015 15:00:00 India Time
        //Trial Period  ~ 1000*60*60*24*4 = 345600000 mili sec (4 days)

        if (timeDiff > 1000 * 60 * 60 * 24 * 60) {
//                if (timeDiff > 1000 * 60 * 1) {
            if (ReusableClass.getFromPreference("FullVersionPurchased", SplashScreen.this).equalsIgnoreCase("yes")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!backPressed) {
                            Intent myIntent = new Intent(SplashScreen.this, MainActivity.class);
                            finish();
                            startActivity(myIntent);
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        }
                    }
                }, 3500);
            } else {
                new AlertDialog.Builder(new ContextThemeWrapper(SplashScreen.this, android.R.style.Theme_Holo))
                        .setTitle(getString(R.string.purchase_full_ver_title))
                        .setMessage(getString(R.string.trial_period_expaired))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mHelper.launchPurchaseFlow(SplashScreen.this, ITEM_SKU, 10001, mPurchaseFinishedListener, "mypurchasetoken");
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        } else {
            //Do the normal things

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!backPressed) {
                        Intent myIntent = new Intent(SplashScreen.this, MainActivity.class);
                        finish();
                        startActivity(myIntent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                }
            }, 3500);
        }
    }

    @Override
    public void onBackPressed() {
        backPressed = true;
        finish();
    }

    //----------------------------------------------------------------
    //In App Purchase
    //----------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                return;
            } else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();
            }
        }
    };

    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU), mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {

            //alert("Purchase Result: " + purchase);
            if (result.isSuccess()) {
                Toast.makeText(SplashScreen.this, getString(R.string.payment_successful), Toast.LENGTH_SHORT).show();
                ReusableClass.saveInPreference("FullVersionPurchased", "yes", SplashScreen.this);
            } else {
                Toast.makeText(SplashScreen.this, getString(R.string.payment_unsuccessful), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

//    void alert(String message) {
//        AlertDialog.Builder bld = new AlertDialog.Builder(this);
//        bld.setMessage(message);
//        bld.setNeutralButton("OK", null);
//        bld.create().show();
//    }

    // ----------------------------------------------------------------
    //In App Purchase
    //----------------------------------------------------------------
}
