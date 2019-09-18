package com.corptia.bringero.view.Main.login;

public class LoginContract {

    public interface LoginView{

        void showProgress();
        void hideProgress();
        void onLoginSuccess(String message);
        void onLoginError(String message);

        //void login(String username, String password, OnLoginFinishedListener listener);

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
