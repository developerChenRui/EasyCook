package com.example.chenrui.easycook;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private BottomNavigationView bnvMenu;
    private TextView txtUser;
    private ImageView imgUser;

    UserProfileListener UPListener;


    public UserProfile() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        bnvMenu = view.findViewById(R.id.bnvMenu);
        txtUser = view.findViewById(R.id.txtUser);
        imgUser = view.findViewById(R.id.imgUser);

        bnvMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.favorites:
                        UPListener.pickRecipeList(R.id.favorite);
                    case R.id.myrecipes:
                        UPListener.pickRecipeList(R.id.myrecipes);
                }
                return false;
            }
        });
        return view;
    }

    public interface UserProfileListener {
        public void pickRecipeList(int i);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        UPListener = (UserProfileListener) context;
    }
}
