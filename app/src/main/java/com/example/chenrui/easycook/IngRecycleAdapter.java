package com.example.chenrui.easycook;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class IngRecycleAdapter extends RecyclerView.Adapter<IngRecycleAdapter.MyViewHolder> {

    private Context context;
    private List<Integer> list = new ArrayList<Integer>();


    public EditText amount;
    public EditText unit;
    public EditText ingredient;
    public IngRecycleAdapter(Context context, List<Integer> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.ingredients_layout, viewGroup,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(IngRecycleAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addData(int position) {
        list.add(position, R.layout.item_layout);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public Float getAmount() {
        return Float.parseFloat(amount.getText().toString());
    }

    public String getUnit() {
        return unit.getText().toString();
    }

    public String getIngredient() {
        return ingredient.getText().toString();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView tv_delete;
        public MyViewHolder(View view) {
            super(view);
            tv_delete = (ImageView)view.findViewById(R.id.delete);
            amount = (EditText)view.findViewById(R.id.amount);
            unit = (EditText)view.findViewById(R.id.unit);
            ingredient = (EditText)view.findViewById(R.id.ingredient);
        }
    }
}
