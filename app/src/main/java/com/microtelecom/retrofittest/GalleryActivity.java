package com.microtelecom.retrofittest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.microtelecom.retrofittest.Model.Hit;
import com.microtelecom.retrofittest.Model.PixaBayModel;
import com.microtelecom.retrofittest.retrofit.ImageAPIClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryActivity extends AppCompatActivity {
    private TextView textViewPrevious, textViewNext, textViewGO;
    private EditText editTextPageNumber;
    private RecyclerView mRecyclerView;
    private GalleryAdapter galleryAdapter;
    private PixaBayModel pixaBayModel;
    private Intent mServiceIntent;
    private WallPaperChangerService mWallPaperChangerService;
    private Context ctx;
    private int current_page = 1, previous_page = 1;

    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ctx = this;

        startService();

        initViews();

        getAllImages();

//        Button crashButton = new Button(this);
//        crashButton.setText("Crash!");
//        crashButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                Crashlytics.getInstance().crash(); // Force a crash
//            }
//        });
//        addContentView(crashButton,
//                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void initViews() {
        mRecyclerView = findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true); // Helps improve performance
        SpacesItemDecoration decoration = new SpacesItemDecoration(8);
        mRecyclerView.addItemDecoration(decoration);

        textViewPrevious = findViewById(R.id.textViewPrevious);
        textViewNext = findViewById(R.id.textViewNext);
        textViewGO = findViewById(R.id.textViewGO);
        editTextPageNumber = findViewById(R.id.editTextPageNumber);

        editTextPageNumber.setText("" + current_page);
        textViewPrevious.setVisibility(View.INVISIBLE);

        editTextPageNumber.setOnFocusChangeListener((view, b) -> {
            try {
                setButtonsVisibilityAccordingToPageNo();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        });

        editTextPageNumber.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                getNewImages();
                return true;
            }
            return false;
        });

        textViewGO.setOnClickListener(view -> getNewImages());

        textViewPrevious.setOnClickListener(view -> {
            int no = Integer.parseInt(editTextPageNumber.getText().toString().trim());
            if (no > 1) {
                editTextPageNumber.setText((no - 1) + "");
                getNewImages();
            }
        });

        textViewNext.setOnClickListener(view -> {
            int no = Integer.parseInt(editTextPageNumber.getText().toString().trim());
            if (no < 11) {
                editTextPageNumber.setText((no + 1) + "");
                getNewImages();
            }
        });
    }

    private void getNewImages() {
        hideKeyBoard();
        int no = Integer.parseInt(editTextPageNumber.getText().toString().trim());
        if (no == current_page) {
            Toast.makeText(this, "Currently on same page", Toast.LENGTH_SHORT).show();
        } else {
            if (no > 0 && no < 11) {
                previous_page = current_page;
                current_page = no;
                getAllImages();
            } else
                Toast.makeText(GalleryActivity.this, "Enter correct page number between 1 to 10", Toast.LENGTH_SHORT).show();
        }
        editTextPageNumber.clearFocus();

        setButtonsVisibilityAccordingToPageNo();
    }

    private void getAllImages() {
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
                if (response.body() == null && response.errorBody() != null) {
                    String body = null;
                    try {
                        body = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (body.equals("[ERROR 400] \"page\" is out of valid range.")) {
                        Toast.makeText(GalleryActivity.this, "Page is out of valid range.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(GalleryActivity.this, "Some error occurred.", Toast.LENGTH_SHORT).show();
                    }
                    current_page = previous_page;
                    setButtonsVisibilityAccordingToPageNo();
                    editTextPageNumber.setText("" + current_page);
                } else {
                    pixaBayModel = response.body();
                    loadImage(pixaBayModel.getHits());
                }
            }

            @Override
            public void onFailure(Call<PixaBayModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(GalleryActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                current_page = previous_page;
                setButtonsVisibilityAccordingToPageNo();
                editTextPageNumber.setText("" + current_page);
            }
        });
    }

    private void loadImage(List<Hit> data) {
        galleryAdapter = new GalleryAdapter(GalleryActivity.this, data);
        mRecyclerView.setAdapter(galleryAdapter);
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextPageNumber.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void setButtonsVisibilityAccordingToPageNo() {
        if (current_page == 1)
            textViewPrevious.setVisibility(View.INVISIBLE);
        else textViewPrevious.setVisibility(View.VISIBLE);

        if (current_page == 10)
            textViewNext.setVisibility(View.INVISIBLE);
        else textViewNext.setVisibility(View.VISIBLE);
    }

    private void startService() {
        mWallPaperChangerService = new WallPaperChangerService(getCtx());
        mServiceIntent = new Intent(getCtx(), mWallPaperChangerService.getClass());
        if (!isMyServiceRunning(mWallPaperChangerService.getClass())) {
            startService(mServiceIntent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }
}
