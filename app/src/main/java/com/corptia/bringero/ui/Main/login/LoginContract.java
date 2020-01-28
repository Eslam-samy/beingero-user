package com.corptia.bringero.ui.Main.login;

import com.corptia.bringero.base.BaseView;

public class LoginContract {

    public interface LoginView extends BaseView {

        //void login(String username, String password, OnLoginFinishedListener listener);
        void onSuccessLoginToMap();
        void onErrorRole(String role);
    }

    public interface LoginPresenter{

        void onLogin(String phone , String password); //validateCredentials
    }

    public interface LoginModel{

        String getPhone();
        String getPassword();
        int isValidatieDate();

    }

}
