package com.microtelecom.retrofittest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.microtelecom.retrofittest.Model.Hit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by pratik on 11/14/2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Hit> data = new ArrayList<>();

    public class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView mImg;

        public MyItemHolder(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.item_img);

            mImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("images", (Serializable) data);
                    bundle.putInt("position", getAdapterPosition());

                    FragmentTransaction ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "slideshow");
                }
            });
        }

    }

    public GalleryAdapter(Context context, List<Hit> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item, parent, false);
        return new MyItemHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((MyItemHolder) holder).mImg.layout(0, 0, 0, 0);
        GlideApp.with(context).load(data.get(position).getPreviewURL())
                .thumbnail(0.5f)
                .override(200, 200)
                .transition(withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(((MyItemHolder) holder).mImg);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}
