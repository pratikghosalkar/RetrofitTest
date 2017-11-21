package com.microtelecom.retrofittest;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fabio on 30/01/2016.
 */
public class WallPaperChangerService extends Service {
    public int counter = 0;

    SharedPref sharedPref;

    public WallPaperChangerService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public WallPaperChangerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        sharedPref = new SharedPref(getApplicationContext());
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("com.microtelecom.retrofittest.RestartService");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 360000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                String sharedPrefDate = sharedPref.getDate();
                if (sharedPrefDate == null || sharedPrefDate.equals("")) {
                    changeWallPaper();
                } else {
                    String current_date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                    Log.i("sharedpref " + sharedPrefDate, "current " + current_date);
                    if (!sharedPrefDate.equals(current_date)) {
                        clearDiskCache();
                        changeWallPaper();
                    }

                }
            }
        };
    }

    private void changeWallPaper() {

        if (isNetworkConnected()) {

            try {

                Bitmap myBitmap = GlideApp.with(getApplicationContext())
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("in lis hei", "" + resource.getHeight());

                                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                                try {
                                    wallpaperManager.setBitmap(resource);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                sharedPref.saveDate("" + new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                                return false;
                            }
                        })
                        .load("https://source.unsplash.com/daily")
                        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void clearDiskCache() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Glide.get(getApplicationContext()).clearDiskCache();
                Glide.get(getApplicationContext()).clearMemory();
                return null;
            }
        };
    }
}