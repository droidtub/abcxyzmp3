package com.musicdownloader.mp3downloader;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Hanh Nguyen on 6/30/2017.
 */

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.search_btn) TextView searchBtn;
    @BindView(R.id.adView) FrameLayout bannerAds;
    private SharedPreferences sharedPreferences;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onStart(){
        super.onStart();

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(sharedPreferences.getString(getString(R.string.banner_id_key), ""));
        bannerAds.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        loadAds(sharedPreferences.getString(getString(R.string.interstitial_id_key), ""));
    }

    @OnClick(R.id.search_btn)
    public void searchSong(){
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
        showLoadingDialog();
        loadAds(sharedPreferences.getString(getString(R.string.interstitial_id_key), ""));
    }

    private void loadAds(String adsId){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(adsId);
        adRequest = new AdRequest.Builder()
                .addTestDevice("YOUR_DEVICE_HASH")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void showLoadingDialog(){
        new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();
    }
}
