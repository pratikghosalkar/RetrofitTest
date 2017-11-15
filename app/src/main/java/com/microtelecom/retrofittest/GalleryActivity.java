package com.microtelecom.retrofittest;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.microtelecom.retrofittest.Model.Hit;
import com.microtelecom.retrofittest.Model.PixaBayModel;
import com.microtelecom.retrofittest.retrofit.ImageAPIClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    GalleryAdapter galleryAdapter;
    int current_page = 1;
    PixaBayModel pixaBayModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        initViews();
        getAllImages();
    }

    private void initViews() {
        mRecyclerView = findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true); // Helps improve performance
        SpacesItemDecoration decoration = new SpacesItemDecoration(8);
        mRecyclerView.addItemDecoration(decoration);


        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void getAllImages() {
//        image_position = 1;
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting images...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ImageAPIClient imageAPIClient = ServiceGenerator.createService(ImageAPIClient.class);

        Map<String, String> data = new HashMap<>();
        data.put("key", "7000436-d37f3e4f959fd31258c65e047");
        data.put("image_type", "photo");
        data.put("page", String.valueOf(current_page));
        data.put("per_page", "50");

        Call<PixaBayModel> pixaBayModelCall = imageAPIClient.getImagesByPage(data);

        pixaBayModelCall.enqueue(new Callback<PixaBayModel>() {
            @Override
            public void onResponse(Call<PixaBayModel> call, Response<PixaBayModel> response) {
                progressDialog.dismiss();
                pixaBayModel = response.body();
                loadImage(pixaBayModel.getHits());
            }

            @Override
            public void onFailure(Call<PixaBayModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(GalleryActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImage(List<Hit> data) {
        galleryAdapter = new GalleryAdapter(GalleryActivity.this, data);
        mRecyclerView.setAdapter(galleryAdapter);
    }
}
