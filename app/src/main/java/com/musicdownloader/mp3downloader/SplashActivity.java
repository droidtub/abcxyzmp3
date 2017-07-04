package com.musicdownloader.mp3downloader;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.musicdownloader.mp3downloader.entity.UpdateInfoEntity;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class SplashActivity extends AppCompatActivity {
    private String updateUrl = "https://footballcrazy255.files.wordpress.com/2017/07/mp3_config1.doc";
    private OkHttpClient okHttpClient;
    private SharedPreferences sharedPreferences;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onStart(){
        super.onStart();
        createOkHttpClient();
        loadUpdateInfo();
    }

    private OkHttpClient createOkHttpClient(){
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging);
        builder.connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(60 * 1000, TimeUnit.MILLISECONDS);

        okHttpClient = builder.build();
        return okHttpClient;
    }

    private void loadUpdateInfo(){
        final Request request = new Request.Builder()
                .url(updateUrl)
                .build();

        Observable.fromCallable(new Callable<UpdateInfoEntity>() {
            @Override
            public UpdateInfoEntity call() throws Exception {
                Gson gson = new GsonBuilder().create();
                Response response = okHttpClient.newCall(request).execute();
                InputStream in = response.body().byteStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String result, line = reader.readLine();
                result = line;
                while((line = reader.readLine()) != null){
                    result += line;
                }
                UpdateInfoEntity entity = gson.fromJson(result, UpdateInfoEntity.class);
                return entity;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateInfoEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull UpdateInfoEntity updateInfoEntity) {
                        saveAdsInfo(updateInfoEntity);
                        loadAds(updateInfoEntity.interstitial_ads);
                        if(updateInfoEntity.update == true) {
                            showUpdateDialog(updateInfoEntity);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void saveAdsInfo(UpdateInfoEntity entity){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.interstitial_id_key), entity.interstitial_ads);
        editor.putString(getString(R.string.banner_id_key), entity.banner_ads);
        editor.putString(getString(R.string.update_link_key), entity.update_link);
        editor.putString(getString(R.string.update_title_key), entity.update_title);
        editor.putString(getString(R.string.update_message_key), entity.update_message);
        editor.apply();
    }

    private void loadAds(String adsId){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(adsId);
        adRequest = new AdRequest.Builder()
                .addTestDevice("YOUR_DEVICE_HASH")
                .build();

        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                showSearchActivity();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                showSearchActivity();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }
        });
    }

    private void showSearchActivity(){
        startActivity(new Intent(this, SearchActivity.class));
        finish();
    }

    private void showUpdateDialog(final UpdateInfoEntity entity){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(entity.update_title)
                .setMessage(entity.update_message);

        if(entity.update_cancelable){
            builder.setPositiveButton(getResources().getText(R.string.action_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    showPlayStore(entity.update_link);
                }
            })
                    .setNegativeButton(getResources().getText(R.string.action_later), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    });
        } else {
            builder.setPositiveButton(getResources().getText(R.string.action_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showPlayStore(entity.update_link);
                }
            });
        }

        Dialog dialog = builder.create();
        dialog.show();
    }

    private void showPlayStore(String url){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (android.content.ActivityNotFoundException e){
            e.printStackTrace();
        }
    }
}
