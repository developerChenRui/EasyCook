package com.example.chenrui.easycook;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.chenrui.easycook.InsRecycleAdapter.InsViewHolder.detail;


/***
 * IngRecycleAdapter
 *
 * Displays the ingredients in the CreateActivity
 ***/
public class IngRecycleAdapter extends RecyclerView.Adapter<IngRecycleAdapter.MyViewHolder> {

    private Context context;
    private List<Integer> list = new ArrayList<Integer>();

    private ArrayList<String> amounts = new ArrayList<>();
    private ArrayList<String> units = new ArrayList<>();
    private ArrayList<String> ingres = new ArrayList<>();

    public EditText amount;
    public EditText unit;
    public EditText ingredient;


    public ArrayList<String> getAmounts() {
        return amounts;
    }

    public ArrayList<String> getUnits() {
        return units;
    }

    public ArrayList<String> getIngredients() {
        return ingres;
    }

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
        System.out.format("MyViewHolder.amount = %s%n",myViewHolder.amount);

        myViewHolder.amount.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(i > amounts.size()-1) {
                    amounts.add(myViewHolder.amount.getText().toString());
                } else {
                    amounts.set(i, myViewHolder.amount.getText().toString());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        myViewHolder.unit.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(i > units.size()-1) {
                    units.add(myViewHolder.unit.getText().toString());
                } else {
                    units.set(i, myViewHolder.unit.getText().toString());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        myViewHolder.ingredient.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(i > ingres.size()-1) {
                    ingres.add(myViewHolder.ingredient.getText().toString());
                } else {
                    ingres.set(i, myViewHolder.ingredient.getText().toString());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

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
        EditText amount;
        EditText unit;
        EditText ingredient;
        public MyViewHolder(View view) {
            super(view);
            System.out.format("Setting MyViewHolder properties %n");
            tv_delete = (ImageView)view.findViewById(R.id.delete);
            amount = (EditText)view.findViewById(R.id.amout);
            unit = (EditText)view.findViewById(R.id.unit);
            ingredient = (EditText)view.findViewById(R.id.ingredient);
            System.out.format("Bound MyViewHolder.amount to %s%n",view.findViewById(R.id.amount));
        }
    }
}
