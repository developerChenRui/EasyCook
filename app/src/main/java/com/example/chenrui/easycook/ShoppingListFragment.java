package com.example.chenrui.easycook;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingListFragment extends Fragment {

    ImageButton btnDelete_shoplist;
    List<Item> items;
    ListView listView;
    ItemsListAdapter myItemsListAdapter;

    ImageButton btnSupermarket;


    public ShoppingListFragment() {
        // Required empty public constructor
    }

    static class ViewHolder {
        CheckBox checkBox;
        TextView text;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        listView = (ListView)view.findViewById(R.id.listview);
        btnDelete_shoplist = (ImageButton)view.findViewById(R.id.btnDelete_shoplist);
        btnSupermarket = (ImageButton)view.findViewById(R.id.btnStore);

        initItems();
        myItemsListAdapter = new ItemsListAdapter(getActivity(), items);
        listView.setAdapter(myItemsListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                Toast.makeText(MainActivity.this,
//                        ((Item)(parent.getItemAtPosition(position))).ItemString,
//                        Toast.LENGTH_LONG).show();
            }});

        btnDelete_shoplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str = "Check items:\n";

                for (int i=0; i<items.size(); i++){
                    if (items.get(i).isChecked()){
                        items.remove(i);
                    }
                    myItemsListAdapter.notifyDataSetChanged();
                }

               // Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();

            }
        });

        btnSupermarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent appInfo = new Intent(getActivity(), MapsActivity.class);
                startActivity(appInfo);

            }
        });
        return view;
    }

    private void initItems(){
        items = new ArrayList<Item>();

        // TypedArray arrayDrawable = getResources().obtainTypedArray(R.array.resicon);
        TypedArray arrayText = getResources().obtainTypedArray(R.array.restext);

        for(int i=0; i<arrayText.length(); i++){
            String s = arrayText.getString(i);
            boolean b = false;
            Item item = new Item(s, b);
            items.add(item);
        }

        //arrayDrawable.recycle();
        arrayText.recycle();
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
            View rowView = convertView;

            ViewHolder viewHolder = new ViewHolder();
            if (rowView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                rowView = inflater.inflate(R.layout.shopping_list_item, null);

                viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.rowCheckBox);
                viewHolder.text = (TextView) rowView.findViewById(R.id.rowTextView);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }

            viewHolder.checkBox.setChecked(list.get(position).checked);

            final String itemStr = list.get(position).ItemString;
            viewHolder.text.setText(itemStr);

            viewHolder.checkBox.setTag(position);
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean newState = !list.get(position).isChecked();
                    list.get(position).checked = newState;
//                    Toast.makeText(getApplicationContext(),
//                            itemStr + "setOnClickListener\nchecked: " + newState,
//                            Toast.LENGTH_LONG).show();
                }
            });

            viewHolder.checkBox.setChecked(isChecked(position));

            return rowView;
        }
    }

}
