package com.corptia.bringero.view.Main.login;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.corptia.bringero.R;
import com.corptia.bringero.view.home.StoreTypesActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    @BindView(R.id.btn_login)
    Button btn_login;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this,view);

        btn_login.setOnClickListener(view1 -> {

            //StoreTypesActivity .navController .navigate(R.id.action_loginFragment_to_nav_home2);
        });

        return view;
    }

}
