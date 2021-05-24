package com.ansdoship.pixelarteditor.viewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ansdoship.pixelarteditor.R;

import java.util.List;

public class TextViewListAdapter extends RecyclerView.Adapter<TextViewListAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mData;
    private OnItemClickListener mOnItemClickListener;
    private Drawable mDrawable;

    public TextViewListAdapter (Context context, List<String> data, Drawable drawable) {
        mContext = context;
        mData = data;
        mDrawable = drawable;
        mDrawable.setBounds(0, 0, mDrawable.getMinimumWidth(), mDrawable.getMinimumHeight());
    }

    public interface OnItemClickListener {
        void onClick (int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_item_text, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvItem.setText(mData.get(position));
        holder.tvItem.setCompoundDrawables(mDrawable, null, null, null);
        if (mOnItemClickListener != null) {
            holder.tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tv_adapter_item);
        }

    }

}
