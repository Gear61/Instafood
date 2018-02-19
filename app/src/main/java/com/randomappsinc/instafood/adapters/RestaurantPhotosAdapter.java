package com.randomappsinc.instafood.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.instafood.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RestaurantPhotosAdapter extends RecyclerView.Adapter<RestaurantPhotosAdapter.RestaurantPhotoViewHolder> {

    public interface Listener {
        void onPhotoClicked(ArrayList<String> imageUrls, int position);
    }

    @NonNull private Listener listener;
    private Context context;
    private ArrayList<String> photoUrls;
    private Drawable defaultThumbnail;

    public RestaurantPhotosAdapter(Context context, @NonNull Listener listener) {
        this.listener = listener;
        this.context = context;
        this.photoUrls = new ArrayList<>();
        this.defaultThumbnail = new IconDrawable(this.context, IoniconsIcons.ion_image).colorRes(R.color.dark_gray);
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls.clear();
        this.photoUrls.addAll(photoUrls);
        notifyDataSetChanged();
    }

    @Override
    public RestaurantPhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.restaurant_photo_cell, parent, false);
        return new RestaurantPhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RestaurantPhotoViewHolder holder, int position) {
        holder.loadPhoto(position);
    }

    @Override
    public int getItemCount() {
        return photoUrls.isEmpty() ? 1 : photoUrls.size();
    }

    class RestaurantPhotoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.restaurant_photo) ImageView photoView;
        @BindView(R.id.no_photos) View noPhotos;

        RestaurantPhotoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadPhoto(int position) {
            if (photoUrls.isEmpty()) {
                photoView.setVisibility(View.GONE);
                noPhotos.setVisibility(View.VISIBLE);
            } else {
                noPhotos.setVisibility(View.GONE);
                photoView.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(photoUrls.get(position))
                        .error(defaultThumbnail)
                        .fit()
                        .centerCrop()
                        .into(photoView);
            }
        }

        @OnClick(R.id.parent)
        public void onPhotoClicked() {
            if (photoUrls.isEmpty()) {
                return;
            }
            listener.onPhotoClicked(photoUrls, getAdapterPosition());
        }
    }
}
