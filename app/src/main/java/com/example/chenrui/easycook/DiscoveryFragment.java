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
    private CustomAdaptor cAdaptor;
    private List<Integer> imageList;
    private List<String> nameList;
    private List<Integer> rateList;
    private List<Integer> likeNumList;
    private List<Boolean> favorList;
    private List<Integer> userImageList;
    private List<String> commentList;

    private View fragView;


    private String TAG = "YANG";
//    private ArrayList<Recipe> recipeList;
    private ArrayList<Recipe> RecylerRecipeList = new ArrayList<>();



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
                    refreshLayout.setRefreshing(true);
                    RecylerRecipeList.clear();
                    Utils.keyWordSearch(query, new AsyncData() {
                        @Override
                        public void onData(ArrayList<Recipe> recipeList) {
                            for(Recipe r : recipeList){
                                RecylerRecipeList.add(r);
                            }
                            cAdaptor.notifyDataSetChanged();
                            searchView.setQuery("",false);
                            refreshLayout.setRefreshing(false);
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
        fragView =view;
        refreshLayout = view.findViewById(R.id.SwipeRefView);
        refreshLayout.setColorSchemeResources(
                R.color.colorRed,
                R.color.colorYellow,
                R.color.colorGreen
        );
        recyclerView = view.findViewById(R.id.RecyView);
        linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        refreshLayout.setOnRefreshListener(this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new Decoration(getActivity()));
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        loadData();
//        cAdaptor = new CustomAdaptor(recipeList , getActivity(), view);
//
//        recyclerView.setAdapter(cAdaptor);


//        recyclerView.addOnScrollListener(new EndLessOnScrollListener(linearLayoutManager) {
//            @Override
//            public void onLoadMore(int currentPage) {
////                refreshLayout.setRefreshing(true);
////                loadData();
////                refreshLayout.setRefreshing(false);
////                cAdaptor.notifyDataSetChanged();
//
//            }
//        });

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

        fragView = view;
        return view;
    }

    private void loadData(){
        /** modify later**/
        refreshLayout.setRefreshing(true);
        Utils.randomSearch(new AsyncData() {
            @Override
            public void onData(ArrayList<Recipe> recipeList) {
                for(Recipe r : recipeList){
                    RecylerRecipeList.add(r);
                }
                cAdaptor = new CustomAdaptor(RecylerRecipeList, getActivity(), fragView);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new Decoration(getActivity()));
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

//    private void loadData(){
//        /** modify later**/
//        if(recipeList == null || recipeList.size()==0) {
//            recipeList = Utils.randomSearch();
//            Log.d("checkNUm1",recipeList.size()+"");
//        } else {
//            ArrayList<Recipe> newRecipes = Utils.randomSearch();
//            Log.d("checkNUm4",newRecipes.size()+"");
//            for (int i=0; i<newRecipes.size();i++) {
//                Log.d("checkNUm3",recipeList.size()+"");
//                recipeList.add(0,newRecipes.get(i));
//            }
//        }
//
//        Log.d("checkNUm2",recipeList.size()+"");
//    }

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
        RecylerRecipeList.clear();
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

//        Log.d("checkNUm9","REFRESH");
// //       new Handler().postDelayed(new Runnable() {
//
// //           @Override public void run() {
//                Log.d(TAG,"onRefresh happened");
//                loadData();
//                cAdaptor.notifyDataSetChanged();
//            //    cAdaptor = new CustomAdaptor(recipeList,getActivity(),fragView);
//            //    recyclerView.setAdapter(cAdaptor);
//                refreshLayout.setRefreshing(false);
//
// //           }
//
//  //      }, 500);


    }
}


