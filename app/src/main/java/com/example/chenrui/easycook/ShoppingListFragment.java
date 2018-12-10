package com.example.chenrui.easycook;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingListFragment extends Fragment {



    List<Item> items= new ArrayList<Item>();
    ListView listView;
    JSONArray ingArr;


    ItemsListAdapter myItemsListAdapter;
    private FloatingActionButton btnAdd;

    ImageButton btnSupermarket;


    public ShoppingListFragment() {
        // Required empty public constructor
    }

    static class ViewHolder {
        CheckBox checkBox;
        TextView text;
        ImageButton btndelete;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            myItemsListAdapter.notifyDataSetChanged();
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        listView = (ListView)view.findViewById(R.id.listview);
        btnSupermarket = (ImageButton)view.findViewById(R.id.btnStore);
        myItemsListAdapter = new ItemsListAdapter(getActivity(), items);
        btnAdd = (FloatingActionButton) view.findViewById(R.id.btnAdd);
        TextInputLayout addIngredient = (TextInputLayout)view.findViewById(R.id.layout_ing);
        listView.setAdapter(myItemsListAdapter);
        TextView text = (TextView)view.findViewById(R.id.rowTextView);
        TextView addIngredients = (TextView) view.findViewById(R.id.addingredient);




        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab(btnAdd, false, 200);
                AlertDialog.Builder customizeDialog =
                        new AlertDialog.Builder(getContext());
                final View dialogView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.add_shoppinglist_dialog, null);

                TextInputLayout addIngredient = (TextInputLayout)dialogView.findViewById(R.id.layout_ing);
                TextView addIngredients = (TextView) dialogView.findViewById(R.id.addingredient);
                TextView title = new TextView(getActivity());
                title.setTextSize(20);
                title.setPadding(0, 20, 0, 20);

                title.setText("Add Ingredients");
                title.setGravity(Gravity.CENTER);
                customizeDialog.setCustomTitle(title);

                customizeDialog.setView(dialogView);
                customizeDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(addIngredients.getText().toString().trim().length() == 0){
                                    Toasty.error(getContext(),"Please enter valid input",Toast.LENGTH_SHORT, true).show();
                                }
                                else {
                                    items.add(new Item(((TextView) dialogView.findViewById(R.id.addingredient)).getText().toString(), false));

                                    myItemsListAdapter.notifyDataSetChanged();
                                    animateFab(btnAdd, true, 200);
                                }
                            }
                        });
                customizeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(final DialogInterface arg0) {
                        animateFab(btnAdd, true, 200);
                    }
                });
                customizeDialog.show();

            }

        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long viewId = view.getId();
                if (viewId == R.id.rowCheckBox) {
                    if(items.get(position).isChecked()){
                        ((TextView)view).setText("GOT!") ;
                        ((TextView)view).setTextColor(getResources().getColor(R.color.welcome_screen_4));}
                    else{
                        ((TextView)view).setText("");}
                    myItemsListAdapter.notifyDataSetChanged();
                }
            }
        });
        return view;
    }

    public void initItems(ArrayList<String> shoppinglist){
        if(items == null) {
            items = new ArrayList<Item>();
        }
        ingArr = Utils.user.getShoppingList();
        if (ingArr == null) ingArr = new JSONArray();
        try{
            for (int i = 0; i < ingArr.length(); i++){
                items.add((Item)ingArr.get(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        for(String s:shoppinglist) {
            Item item = new Item(s,false);
            items.add(item);
            ingArr.put(item);
        }
        Utils.user.setShoppingList(ingArr);


    }

    public void animateFab(View fab, boolean scaleFabUp, int duration){
        fab.animate()
                .scaleX(scaleFabUp ? 1.0f : 0.0f)
                .scaleY(scaleFabUp ? 1.0f : 0.0f)
                .alpha(scaleFabUp ? 1.0f : 0.0f)
                .setDuration(duration)
                .start();
    }

    public class Item {
        boolean checked;
        String ItemString;
        Item(String t, boolean b){
            ItemString = t;
            checked = b;
        }

        public boolean isChecked(){
            return checked;
        }
    }


    public class ItemsListAdapter extends BaseAdapter {

        private Context context;
        private List<Item> list;

        HashMap<Integer, View> lmap = new HashMap<Integer, View>();



        ItemsListAdapter(Context c, List<Item> l) {
            context = c;
            list = l;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public boolean isChecked(int position) {
            return list.get(position).checked;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (lmap.get(position) == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.shopping_list_item, null);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);
                lmap.put(position,convertView);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.rowCheckBox);
            viewHolder.text = (TextView) convertView.findViewById(R.id.rowTextView);
            viewHolder.btndelete = (ImageButton)convertView.findViewById(R.id.rowdeletebtn);

            viewHolder.checkBox.setChecked(list.get(position).checked);

            final String itemStr = list.get(position).ItemString;
            viewHolder.text.setText(itemStr);
            viewHolder.text.setTextSize(15);

            viewHolder.btndelete.setImageResource(R.drawable.delete_sl);
            viewHolder.checkBox.setTag(position);

            StateListDrawable stateList = new StateListDrawable();
            int statePressed = android.R.attr.state_pressed;
            int stateChecked = android.R.attr.state_checked;
            stateList.addState(new int[] {-stateChecked}, new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.rect)));
            stateList.addState(new int[] {stateChecked}, new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.successadd)));
            stateList.addState(new int[] {statePressed}, new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.rect)));

            viewHolder.checkBox.setButtonDrawable(stateList);
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    list.get(position).checked = !list.get(position).isChecked();
                    ((ListView) parent).performItemClick(view, position, 0);
                }
            });
            viewHolder.btndelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    items.remove(position);
                    myItemsListAdapter.notifyDataSetChanged();
                    JSONArray newIngArr = new JSONArray();
                    for (Item i : items){
                        newIngArr.put(i);
                    }
                    Utils.user.setShoppingList(newIngArr);
                }

            });


            return convertView;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.shopping_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.btnStore:
                Intent appInfo = new Intent(getActivity(), MapsActivity.class);
                startActivity(appInfo);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }


}
