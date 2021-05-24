package com.ansdoship.pixelarteditor.viewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ansdoship.pixelarteditor.R;

import java.util.List;

public class PaletteListAdapter extends RecyclerView.Adapter<PaletteListAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mInternalPalettes;
    private List<String> mExternalPalettes;
    private OnItemClickListener mOnItemClickListener;
    private int mCheckedPosition;

    public PaletteListAdapter(Context context, List<String> internalPalettes,List<String> externalPalettes) {
        this(context, internalPalettes, externalPalettes, -1);
    }

    public PaletteListAdapter(Context context, List<String> internalPalettes,List<String> externalPalettes, int checkedPosition) {
        mContext = context;
        mInternalPalettes = internalPalettes;
        mExternalPalettes = externalPalettes;
        mCheckedPosition = checkedPosition;
    }

    public interface OnItemClickListener {
        void onInternalPaletteClick(int position);
        void onExternalPaletteClick(int posotion);
        void onResetClick(int position);
        void onRenameClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_item_palette, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (position == mCheckedPosition) {
            holder.tvPalette.setTextColor(ContextCompat.getColor(mContext, R.color.colorTheme));
        }
        if (position < mInternalPalettes.size()) {
            holder.tvPalette.setText(mInternalPalettes.get(position));
            holder.tvPalette.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            holder.imgRename.setVisibility(View.GONE);
            holder.imgDelete.setVisibility(View.GONE);
            if (mOnItemClickListener != null) {
                holder.tvPalette.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onInternalPaletteClick(position);
                    }
                });
                holder.imgReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onResetClick(position);
                    }
                });
            }
        }
        else {
            final int position2 = position - mInternalPalettes.size();
            holder.tvPalette.setText(mExternalPalettes.get(position2));
            holder.imgReset.setVisibility(View.GONE);
            if (mOnItemClickListener != null) {
                holder.tvPalette.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onExternalPaletteClick(position2);
                    }
                });
                holder.imgRename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onRenameClick(position2);
                    }
                });
                holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onDeleteClick(position2);
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return mInternalPalettes.size() + mExternalPalettes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvPalette;
        public ImageButton imgReset;
        public ImageButton imgRename;
        public ImageButton imgDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPalette = itemView.findViewById(R.id.tv_adapter_palette);
            imgReset = itemView.findViewById(R.id.img_adapter_reset);
            imgRename = itemView.findViewById(R.id.img_adapter_rename);
            imgDelete = itemView.findViewById(R.id.img_adapter_delete);
        }

    }

}
