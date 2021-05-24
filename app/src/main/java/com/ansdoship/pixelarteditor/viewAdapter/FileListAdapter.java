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
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.ansdoship.pixelarteditor.R;

import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mDirs;
    private List<String> mFiles;
    private OnItemClickListener mOnItemClickListener;
    private Drawable mFolderDrawable;
    private Drawable mFileDrawable;

    public FileListAdapter(Context context, List<String> dirs, List<String> files, Drawable fileIcon) {
        mContext = context;
        mDirs = dirs;
        mFiles = files;
        mFolderDrawable = VectorDrawableCompat.create(mContext.getResources(),
                R.drawable.ic_baseline_folder_24, mContext.getTheme());
        if (mFolderDrawable != null) {
            mFolderDrawable.setBounds(0, 0,
                    mFolderDrawable.getMinimumWidth(), mFolderDrawable.getMinimumHeight());
        }
        if (fileIcon == null) {
            mFileDrawable = VectorDrawableCompat.create(mContext.getResources(),
                    R.drawable.ic_baseline_file_24, mContext.getTheme());
            if (mFileDrawable != null) {
                mFileDrawable.setBounds(0, 0,
                        mFileDrawable.getMinimumWidth(), mFileDrawable.getMinimumHeight());
            }
        }
        else {
            mFileDrawable = fileIcon;
            mFileDrawable.setBounds(0, 0,
                    mFileDrawable.getMinimumWidth(), mFileDrawable.getMinimumHeight());
        }
    }

    public interface OnItemClickListener {
        void onDirectoryClick(String name, int position);
        void onFileClick(String name, int position);
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
        if (position < mDirs.size()) {
            holder.tvItem.setText(mDirs.get(position));
            holder.tvItem.setCompoundDrawables(mFolderDrawable, null, null, null);
            if (mOnItemClickListener != null) {
                holder.tvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onDirectoryClick(mDirs.get(position), position);
                    }
                });
            }
        }
        else {
            final int position2 = position - mDirs.size();
            holder.tvItem.setText(mFiles.get(position2));
            holder.tvItem.setCompoundDrawables(mFileDrawable, null, null, null);
            if (mOnItemClickListener != null) {
                holder.tvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onFileClick(mFiles.get(position2), position2);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDirs.size() + mFiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tv_adapter_item);
        }

    }

}
