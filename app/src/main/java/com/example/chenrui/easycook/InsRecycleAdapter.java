package com.example.chenrui.easycook;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.chenrui.easycook.InsRecycleAdapter.InsViewHolder.delImg;

public class InsRecycleAdapter extends RecyclerView.Adapter<InsRecycleAdapter.InsViewHolder> {

    public OnBindCallback onBind;
    private Context context;
    private List<Integer> list = new ArrayList<>();
    private LayoutInflater mInflater;
    private List<LocalMedia> medialist = new ArrayList<>();
    private int selectMax = 1;

    private onAddPicClickListener mOnAddPicClickListener;

    public void setList(List<LocalMedia> selectList) {
        medialist = selectList;
        //show pic

        if(!medialist.isEmpty()) {
//            InsViewHolder.delImg.setVisibility(View.INVISIBLE);
            LocalMedia media = medialist.get(medialist.size() - 1);
            String path = "";
            if (media.isCut() && !media.isCompressed()) {
                path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                path = media.getCompressPath();
            } else {
                path = media.getPath();
            }
            // image
            if (media.isCompressed()) {
                Log.i("compress image result:", new File(media.getCompressPath()).length() / 1024 + "k");
                Log.i("compress path:", media.getCompressPath());
            }

            Log.i("origin path::", media.getPath());
            int pictureType = PictureMimeType.isPictureType(media.getPictureType());
            if (media.isCut()) {
                Log.i("crop path::", media.getCutPath());
            }

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.color.color_f6);
            Glide.with(context)
                    .load(path)
                    .apply(options)
                    .into(InsViewHolder.upload);

            delImg.setVisibility(View.VISIBLE);
        }
    }

    public void setSelectMax(int i) {
        this.selectMax = i;
    }


    public interface onAddPicClickListener {
        void onAddPicClick();
    }

    public InsRecycleAdapter(Context context, List<Integer> list, InsRecycleAdapter.onAddPicClickListener mOnAddPicClickListener) {
        this.context = context;
        this.list = list;
        this.mOnAddPicClickListener = mOnAddPicClickListener;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public InsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        InsViewHolder holder = new InsViewHolder(LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.instruction_layout, viewGroup,false));
        return holder;
    }

    @Override
    public int getItemCount() {
        Log.e("add", list.toString());
        return list.size();
    }


    @Override
    public void onBindViewHolder(InsViewHolder myViewHolder, int i) {
        if (onBind != null) {
            onBind.onViewBound(myViewHolder, i);
        }

        myViewHolder.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnAddPicClickListener.onAddPicClick();
            }
        });

        myViewHolder.delImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                medialist.remove(medialist.size() - 1);
                myViewHolder.upload.setImageResource(R.drawable.addimg_1x);
                delImg.setVisibility(View.INVISIBLE);
            }
        });

        myViewHolder.title.setText("Step " + (i + 1));


        myViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.size() == 1) {
                    Snackbar.make(view, "can't be deleted", Snackbar.LENGTH_SHORT).show();
                } else {
                    removeData(i);
                    medialist.remove(medialist.size() - 1);
                }
            }
        });
    }


    public void addData(int position) {
        list.add(position, R.layout.instruction_layout);
        Toast.makeText(context, "Data added" + list.size(), Toast.LENGTH_SHORT).show();
        notifyItemInserted(position);
        notifyItemRangeChanged(position,list.size() - position);
    }

    public void removeData(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    static class InsViewHolder extends RecyclerView.ViewHolder {
       static ImageView delete;
       static TextView title;
       static ImageButton upload;
       static ImageView delImg;
        public InsViewHolder(View view) {
            super(view);
            delete = (ImageView)view.findViewById(R.id.delete);
            title = (TextView)view.findViewById(R.id.title);
            upload = (ImageButton) view.findViewById(R.id.pic);
            delImg = (ImageView)view.findViewById(R.id.iv_del);
        }
    }

}
