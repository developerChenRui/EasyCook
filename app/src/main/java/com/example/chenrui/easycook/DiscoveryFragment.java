package com.example.chenrui.easycook;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.Collections;
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
    public CustomAdaptor cAdaptor;
    private View fragView;
    private StaggeredGridLayoutManager layoutManager;

    private boolean firstLogin = true;


    private String TAG = "YANG";
    private ArrayList<Recipe> RecylerRecipeList = new ArrayList<>();



    public DiscoveryFragment() {
        setHasOptionsMenu(true);

        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
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
                    refreshLayout.setRefreshing(true);
                    RecylerRecipeList.clear();
                    Utils.generalKeyWordSearch(query, new FinalSync() {
                        @Override
                        public void onData(ArrayList<Recipe> recipeList) {
                            Collections.shuffle(recipeList);
                            for(Recipe r : recipeList){
                                RecylerRecipeList.add(r);
                            }
                            cAdaptor.notifyDataSetChanged();
                            searchView.setQuery("",false);
                            refreshLayout.setRefreshing(false);
                            recyclerView.clearOnScrollListeners();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            refreshLayout.setRefreshing(false);
                            Toast.makeText(getContext(),"There was error retriving data", Toast.LENGTH_LONG).show();
                        }
                    });

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    /** implement later**/
                    Log.d(TAG, "onQueryTextChange: " + newText);
                    return false;
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
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        fragView =view;
        refreshLayout = view.findViewById(R.id.SwipeRefView);
        refreshLayout.setColorSchemeResources(
                R.color.colorRed,
                R.color.colorYellow,
                R.color.colorGreen
        );
        recyclerView = view.findViewById(R.id.RecyView);
        refreshLayout.setOnRefreshListener(this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        if(firstLogin) {
            loadData();
            firstLogin = false;
        } else {
            cAdaptor = new CustomAdaptor(RecylerRecipeList, getActivity());
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(cAdaptor);
        }
        registerListener();



        fragView = view;
        return view;
    }

    public CustomAdaptor returnAdaptor() {
        return cAdaptor;
    }

    private void registerListener(){
        recyclerView.addOnScrollListener(new EndLessOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                Log.d(TAG, "onLoadMore: happened");
                refreshLayout.setRefreshing(true);
                Utils.randomSearch(new AsyncData() {
                    @Override
                    public void onData(ArrayList<Recipe> recipeList) {
                        for(Recipe r : recipeList){
                            RecylerRecipeList.add(r);
                        }
                        cAdaptor.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }
                    @Override
                    public void onError(String errorMessage) {
                        refreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(),"There was error retrieving data", Toast.LENGTH_LONG).show();

                    }
                });
            }
        });
    }
    private void loadData(){
        refreshLayout.setRefreshing(true);
        Utils.randomSearch(new AsyncData() {
            @Override
            public void onData(ArrayList<Recipe> recipeList) {
                for(Recipe r : recipeList){
                    RecylerRecipeList.add(r);
                }
                cAdaptor = new CustomAdaptor(RecylerRecipeList, getActivity());
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(cAdaptor);
                refreshLayout.setRefreshing(false);
            }
            @Override
            public void onError(String errorMessage) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(getContext(),"There was error retrieving data", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onRefresh() {
        /** refresh new recipe **/
        Log.d(TAG,"onRefresh happened");
        RecylerRecipeList.clear();
        Utils.randomSearch(new AsyncData() {
            @Override
            public void onData(ArrayList<Recipe> recipeList) {
                for(Recipe r : recipeList){
                    RecylerRecipeList.add(r);
                }
                cAdaptor.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
                registerListener();
            }

            @Override
            public void onError(String errorMessage) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(getContext(),"There was error retrieving data", Toast.LENGTH_LONG).show();
            }
        });

    }
}


