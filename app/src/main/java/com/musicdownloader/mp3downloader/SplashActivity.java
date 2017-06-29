package com.musicdownloader.mp3downloader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.musicdownloader.mp3downloader.entity.AdsInfoEntity;

import java.util.concurrent.Callable;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        loadAds();
    }

    private void loadAds(){
        Observable.fromCallable(new Callable<AdsInfoEntity>() {
            @Override
            public AdsInfoEntity call() throws Exception {
                Gson gson = new GsonBuilder().create();
                Response response = okHttpClient.newCall(request).execute();
                AdsInfoEntity entity = gson.fromJson(response.body().charStream(), AdsInfoEntity.class);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AdsInfoEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull AdsInfoEntity adsInfoEntity) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
