package com.example.chenrui.easycook;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingListFragment extends Fragment {

    ImageButton btnDelete_shoplist;
    List<Item> items= new ArrayList<Item>();
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        listView = (ListView)view.findViewById(R.id.listview);
        btnDelete_shoplist = (ImageButton)view.findViewById(R.id.btnDelete_shoplist);
        btnSupermarket = (ImageButton)view.findViewById(R.id.btnStore);
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
        return view;
    }

    public void initItems(ArrayList<String> shoppinglist){
        if(items == null) {
            items = new ArrayList<Item>();
        }
        for(String s:shoppinglist) {
            items.add(new Item(s,false));
        }
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
                }
            });

            viewHolder.checkBox.setChecked(isChecked(position));

            return rowView;
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
            case R.id.btnDelete_shoplist:

                /** implement later**/
                String str = "Check items:\n";
                for (int i=0; i<items.size(); i++){
                    if (items.get(i).isChecked()){
                        items.remove(i);
                    }
                    myItemsListAdapter.notifyDataSetChanged();
                }
                return true;

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
