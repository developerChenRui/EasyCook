package com.example.chenrui.easycook;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.willy.ratingbar.ScaleRatingBar;

//import com.willy.ratingbar.RotationRatingBar;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Created by Zhiyi Yang on 11/16/2018
 */
public class DiscoveryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private customAdaptor cAdaptor;
    private List<Integer> imageList;
    private List<String> nameList;
    private List<Integer> rateList;
    private List<Integer> likeNumList;
    private List<Boolean> favorList;
    private List<Integer> userImageList;
    private List<String> commentList;
    private String TAG = "YANG";

    public DiscoveryFragment() {

        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) searchView = (SearchView) searchItem.getActionView();
        if (searchView != null){
            searchView.setIconifiedByDefault(false);
            searchView.setQueryHint("search recipes ...");
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    /** implement later**/
                    Log.d(TAG, "onQueryTextSubmit: " + query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    /** implement later**/
                    Log.d(TAG, "onQueryTextChange: " + newText);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                /** implement later**/
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        imageList = new ArrayList<>();
        nameList = new ArrayList<>();
        rateList = new ArrayList<>();
        likeNumList = new ArrayList<>();
        userImageList = new ArrayList<>();
        favorList = new ArrayList<>();
        commentList = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        refreshLayout = view.findViewById(R.id.SwipeRefView);
        refreshLayout.setColorSchemeResources(
                R.color.colorRed,
                R.color.colorYellow,
                R.color.colorGreen
        );
        recyclerView = view.findViewById(R.id.RecyView);
        linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        loadData();
        cAdaptor = new customAdaptor(nameList, imageList,rateList,likeNumList, userImageList
                ,favorList, commentList , getActivity(), view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new Decoration(getActivity()));
         StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                 StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cAdaptor);
        refreshLayout.setOnRefreshListener(this);

        recyclerView.addOnScrollListener(new EndLessOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                refreshLayout.setRefreshing(true);
                loadData();
                refreshLayout.setRefreshing(false);
                cAdaptor.notifyDataSetChanged();

            }
        });
        return view;
    }

    private void loadData(){
        /** modify later**/
        for (int i = 0; i < 5; i++){
            imageList.add(R.drawable.hamburger);
            userImageList.add(R.drawable.superman);
            favorList.add((i % 2 == 1)? true : false);
            nameList.add("burger" + i);
            rateList.add(i);
            likeNumList.add(i*100);
            commentList.add("The burger is a kind of popular US fast food ....");
        }

    }

//    @Override
//    public void onClick(View v, int position) {
//        /** to do **/
//        Toast.makeText(getActivity(), "item " + position + " clicked", Toast.LENGTH_SHORT).show();
//        Log.d(TAG, "item " + position + " clicked");
//    }

    @Override
    public void onRefresh() {
        /** refresh new recipe **/
        Log.d(TAG,"onRefresh happened");
        refreshLayout.setRefreshing(false);
    }
}


/***********************************************************************************************************/
class customAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
//    private RecyclerViewClickListener clickListener;
    private List<String> nameList;
    private List<Integer> ImageList;
    private List<Integer> rateList;
    private List<Integer> likeNumList;
    private List<Integer> userImageList;
    private List<Boolean> favorList;
    private List<String> commentList;
    private Context context;
    private View v;


    public customAdaptor(List<String> nameList, List<Integer> ImageList, List<Integer> rateList, List<Integer> likeNumList,
                         List<Integer> userImageList, List<Boolean> favorList, List<String> commentList, Context context,
                          View v){
        this.nameList = nameList;
        this.ImageList = ImageList;
        this.context = context;
        this.rateList = rateList;
        this.likeNumList = likeNumList;
        this.commentList = commentList;
        this.context = context;
//        this.clickListener = clickListener;
        this.userImageList = userImageList;
        this.favorList = favorList;
        this.v = v;
    }

    @Override
    public int getItemViewType(int position) {
        return nameList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        /** modify latter**/
        /** setOnRatingBarChangeListener **/
        if (holder instanceof contentHolder){
            contentHolder cHolder = (contentHolder) holder;
            cHolder.dishRB.setRating(this.rateList.get(position).intValue());
            cHolder.dishImage.setImageResource(this.ImageList.get(position).intValue());
            cHolder.dishNameLabel.setText(this.nameList.get(position));
            cHolder.likeNumLabel.setText(String.valueOf(this.likeNumList.get(position)));
            cHolder.commentLabel.setText(commentList.get(position));
            cHolder.favBar.setChecked(favorList.get(position));
            cHolder.favBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /** modify later**/
                    if (cHolder.favBar.isChecked()) Toast.makeText(context,
                            "User likes recipe " + position, Toast.LENGTH_SHORT).show();
                    else Toast.makeText(context,
                            "User unlikes recipe " + position, Toast.LENGTH_SHORT).show();
                }
            });

            cHolder.userImage.setImageResource(this.userImageList.get(position).intValue());
            cHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO send the user information and the recipe info to the intent
                    //TODO : name / description / star rating / profile and name / ingredients / instruction / Reviews
                    Intent i = new Intent(context,DishItemActivity.class);
                    Recipe recipe = new Recipe();
                    i.putExtras(Utils.Recipe2Bundle(recipe));
                    context.startActivity(i);
                }
            });
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dish_panel, parent, false);
        return new customAdaptor.contentHolder(view);
    }



    @Override
    public int getItemCount() {
        return nameList.size();
    }

    class contentHolder extends RecyclerView.ViewHolder{
        protected TextView dishNameLabel;
        protected ImageView dishImage;
        protected ScaleRatingBar dishRB;
        private TextView likeNumLabel;
        private CheckBox favBar;
        private RoundImageView userImage;
        private TextView commentLabel;

        contentHolder(View itemView){
            super(itemView);
            dishNameLabel = itemView.findViewById(R.id.dishNameLabel);
            dishImage = itemView.findViewById(R.id.dishImage);
            dishRB = itemView.findViewById(R.id.rBar);
            likeNumLabel = itemView.findViewById(R.id.likeNumLabel);
            favBar = itemView.findViewById(R.id.userFavourite);
            userImage = itemView.findViewById(R.id.userImage);
            commentLabel = itemView.findViewById(R.id.comment);
        }

    }

}
