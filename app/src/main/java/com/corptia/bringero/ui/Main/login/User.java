package com.corptia.bringero.ui.Main.login;

import android.text.TextUtils;
import android.util.Patterns;

public class User implements LoginContract.LoginModel {

    String password , phone;

    public User(String password, String phone) {
        this.password = password;
        this.phone = phone;
    }



    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public int isValidatieDate() {

        // Check is email empty
        // check if email matches pattern
        // check password lenght greter than 6
        if (TextUtils.isEmpty(getPhone()))
            return 0;
        else if (!Patterns.PHONE.matcher(getPhone()).matches())
            return 1;
        else if (getPassword().length()<6)
            return 2;
        else
            return -1;
    }
}
