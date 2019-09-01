package com.corptia.bringero.view.Main.signup;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.telephony.PhoneNumberUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.view.Main.MainActivity;
import com.corptia.bringero.view.Main.otp.VerifyPhoneNumberActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class SignupFragment extends Fragment {


    TextInputLayout input_phone_number;
    Button btn_signup;

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        btn_signup = view.findViewById(R.id.btn_signup);
        input_phone_number = view.findViewById(R.id.input_phone_number);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Common.CURRENT_NUMBER = input_phone_number.getEditText().getText().toString();
                startActivity(new Intent(getContext() , VerifyPhoneNumberActivity.class));

            }
        });

        return view;
    }

}
