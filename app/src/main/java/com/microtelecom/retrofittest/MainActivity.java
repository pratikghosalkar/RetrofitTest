package com.microtelecom.retrofittest;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.microtelecom.retrofittest.Model.PixaBayModel;
import com.microtelecom.retrofittest.retrofit.ImageAPIClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.progressmanager.body.ProgressInfo;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button button, buttonSetWallpaper;
    Context context;
    OkHttpClient OkHttpClient;
    private ProgressBar mGlideProgress;
    int current_page = 1;

    int image_position = 1;
    PixaBayModel pixaBayModel;


    WallpaperManager wallpaperManager;
    Bitmap bitmap1, bitmap2;
    DisplayMetrics displayMetrics; //to find screen dimensions
    int width, height; //to find screen dimensions
    BitmapDrawable bitmapDrawable;
    Button downloadButton;
    String url = "your wallpaper url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // When building OkHttpClient, the OkHttpClient.Builder() is passed to the with() method to initialize the configuration
        OkHttpClient = ProgressManager.getInstance().with(new OkHttpClient.Builder())
                .build();
        getAllImages();

        mGlideProgress = findViewById(R.id.glide_progress);
        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        buttonSetWallpaper = findViewById(R.id.button2);
        context = MainActivity.this;
//        loadImage();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                clearDiskCache();
                mGlideProgress.setProgress(2);
                if (image_position == 20) {
                    current_page = current_page + 1;
                    getAllImages();
                } else
                    loadImage();
            }
        });

        buttonSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        wallpaperManager = WallpaperManager.getInstance(getApplication());
                        bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                        bitmap1 = bitmapDrawable.getBitmap();
                        GetScreenWidthHeight();
                        bitmap2 = Bitmap.createScaledBitmap(bitmap1, width, height, false);
                        wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                        try {
                            wallpaperManager.setBitmap(bitmap1);
//                            wallpaperManager.suggestDesiredDimensions(width, height);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Snackbar snackbar = Snackbar
                        .make(imageView, "Wallpaper Set", Snackbar.LENGTH_SHORT);
                snackbar.show();

            }
        });

    }

    private int getImagePosition() {

        if (image_position == 1) {
            return image_position;
        } else
            return image_position + 1;
    }

    private void loadImage() {
//        String url = "https://source.unsplash.com/random/" + getRandomNo();

        RequestListener<Bitmap> requestListener = new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                // todo log exception to central service or something like that

                // important to return false so the error placeholder can be placed
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                // everything worked out, so probably nothing to do
                return false;
            }
        };


        String url = pixaBayModel.getHits().get(image_position).getWebformatURL();

        ProgressManager.getInstance().addResponseListener(url, getGlideListener());
        String s = "" + (System.currentTimeMillis() + 1);
        Log.d("random", UUID.randomUUID().toString());
        GlideApp.with(context)
                .asBitmap()
                .listener(requestListener)
                .load(url)
                .into(imageView);
        image_position++;

    }

    private void clearDiskCache() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Glide.get(MainActivity.this).clearDiskCache();
                Glide.get(MainActivity.this).clearMemory();
                return null;
            }
        };
    }

    private int getRandomNo() {
        Random r = new Random();
        int Low = 10;
        int High = 100;
        int Result = r.nextInt(High - Low) + Low;
        return Result;
    }

    @NonNull
    private ProgressListener getGlideListener() {
        final String TAG = "GLIDE";
        return new ProgressListener() {
            @Override
            public void onProgress(ProgressInfo progressInfo) {
                int progress = progressInfo.getPercent();
                mGlideProgress.setProgress(progress);
//                mGlideProgressText.setText(progress + "%");
                Log.d(TAG, "--Glide-- " + progress + " %  " + progressInfo.getSpeed() + " byte/s  " + progressInfo.toString());
                if (progressInfo.isFinish()) {
                    //说明已经加载完成
                    Log.d(TAG, "--Glide-- finish");
                }
            }

            @Override
            public void onError(long id, Exception e) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mGlideProgress.setProgress(0);
//                        mGlideProgressText.setText("error");
                    }
                });
            }
        };
    }

    private void getAllImages() {
        image_position = 1;
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting images...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ImageAPIClient imageAPIClient = ServiceGenerator.createService(ImageAPIClient.class);

        Map<String, String> data = new HashMap<>();
        data.put("key", "7000436-d37f3e4f959fd31258c65e047");
        data.put("image_type", "photo");
        data.put("page", String.valueOf(current_page));

        Call<PixaBayModel> pixaBayModelCall = imageAPIClient.getImagesByPage(data);

        pixaBayModelCall.enqueue(new Callback<PixaBayModel>() {
            @Override
            public void onResponse(Call<PixaBayModel> call, Response<PixaBayModel> response) {
                progressDialog.dismiss();
                pixaBayModel = response.body();
                loadImage();
            }

            @Override
            public void onFailure(Call<PixaBayModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void GetScreenWidthHeight() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
    }

}
