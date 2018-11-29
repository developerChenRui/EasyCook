package com.example.chenrui.easycook;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class InsRecycleAdapter extends RecyclerView.Adapter<InsRecycleAdapter.InsViewHolder> {

    public OnBindCallback onBind;
    private Context context;
    private List<Integer> list;

    private static final int PHOTO_REQUEST_CAREMA = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;

    public InsRecycleAdapter(Context context, List<Integer> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public InsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        InsViewHolder holder = new InsViewHolder(LayoutInflater.from(
                context).inflate(R.layout.instruction_layout, viewGroup,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(InsViewHolder myViewHolder, int i) {
        if (onBind != null) {
            onBind.onViewBound(myViewHolder, i);
        }

        myViewHolder.title.setText("Step " + (i + 1));
        myViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() == 1) {
                    Snackbar.make(v, "can't be deleted", Snackbar.LENGTH_SHORT).show();
                } else {
                    removeData(i);
                }
            }
        });
    }

    private void startActivityForResult(Intent intent, int photoRequestGallery) {
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addData(int position) {
        list.add(position, R.layout.instruction_layout);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    class InsViewHolder extends RecyclerView.ViewHolder {
        ImageView delete;
        TextView title;
        ImageButton upload;

        public InsViewHolder(View view) {
            super(view);
            delete = (ImageView)view.findViewById(R.id.delete);
            title = (TextView)view.findViewById(R.id.title);
            upload = (ImageButton) view.findViewById(R.id.pic);
        }
    }
}
