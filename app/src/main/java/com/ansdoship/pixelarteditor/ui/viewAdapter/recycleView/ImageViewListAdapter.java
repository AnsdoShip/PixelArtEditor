package com.ansdoship.pixelarteditor.ui.viewAdapter.recycleView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ansdoship.pixelarteditor.R;
import com.ansdoship.pixelarteditor.ui.view.CheckedImageView;

import java.util.List;

public class ImageViewListAdapter extends RecyclerView.Adapter<ImageViewListAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Drawable> mImages;
    private OnItemClickListener mOnItemClickListener;
    private final int mCheckedPosition;

    public ImageViewListAdapter(@NonNull Context context, @NonNull List<Drawable> images) {
        this (context, images, -1);
    }

    public ImageViewListAdapter(@NonNull Context context, @NonNull List<Drawable> images, int checkedPosition) {
        mContext = context;
        mImages = images;
        mCheckedPosition = checkedPosition;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (position == mCheckedPosition) {
            holder.imgItem.setChecked(true);
            holder.imgItem.setColorFilter(ContextCompat.getColor(mContext, R.color.colorTheme));
        }
        holder.imgItem.setImageDrawable(mImages.get(position));
        if (mOnItemClickListener != null) {
            holder.imgItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CheckedImageView imgItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img_adapter_item);
        }

    }

}
