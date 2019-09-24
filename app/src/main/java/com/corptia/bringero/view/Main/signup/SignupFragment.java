package com.corptia.bringero.view.Main.signup;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.corptia.bringero.R;
import com.corptia.bringero.view.Main.otp.VerifyPhoneNumberActivity;
import com.google.android.material.textfield.TextInputLayout;

import es.dmoral.toasty.Toasty;

public class SignupFragment extends Fragment {


    TextInputLayout input_phone_number, input_password, input_confirm_password, input_firstName, input_lastName;
    public static String phone, password, firstName, lastName;
    private String confirm_password;
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
        input_password = view.findViewById(R.id.input_password);
        input_confirm_password = view.findViewById(R.id.input_confirm_password);
        input_firstName = view.findViewById(R.id.input_firstName);
        input_lastName = view.findViewById(R.id.input_lastName);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phone = input_phone_number.getEditText().getText().toString().trim();
                password = input_password.getEditText().getText().toString();
                confirm_password = input_confirm_password.getEditText().getText().toString();
                firstName = input_firstName.getEditText().getText().toString().trim();
                lastName = input_lastName.getEditText().getText().toString().trim();
                if (validateNonEmptyValues()) {
                    if (validateData()) {
                        startActivity(new Intent(getContext(), VerifyPhoneNumberActivity.class));
                        getActivity().finish();
                    }
                }
            }
        });
        return view;
    }

    private Boolean validateNonEmptyValues() {
        if (TextUtils.isEmpty(phone)) return false;
        if (TextUtils.isEmpty(password)) return false;
        if (TextUtils.isEmpty(confirm_password)) return false;
        if (TextUtils.isEmpty(firstName)) return false;
        return true;
    }

    private Boolean validateData() {
        if (password.length() < 8) {
            Toasty.error(getContext(), "Password must be at least \"8\" characters").show();
            return false;
        }
        if (!password.equals(confirm_password)) {
            Toasty.error(getContext(), "Password and confirmation don't match!").show();
            return false;
        }
        if (!phone.startsWith("01")) {
            Toasty.error(getContext(), "Invalid phone! \n It must start with \"01\".").show();
            return false;
        }
        if (phone.length() != 11) {
            Toasty.error(getContext(), "Invalid phone number! \n It must be \"11\" character length.").show();
            return false;
        }

        return true;
    }
}
