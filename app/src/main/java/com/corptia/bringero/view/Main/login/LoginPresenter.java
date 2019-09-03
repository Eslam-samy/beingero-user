package com.corptia.bringero.view.Main.login;

public class LoginPresenter implements LoginContract.LoginPresenter {

    private LoginContract.LoginView loginView;

    public LoginPresenter(LoginContract.LoginView loginView) {
        this.loginView = loginView;
    }

    @Override
    public void onLogin(String phone, String password) {

        User user = new User(password, phone);
        int isSuccess = user.isValidatieDate();
        if (isSuccess == 0) {
            loginView.onLoginError("phone number is Empty");
        } else if (isSuccess == 1)
            loginView.onLoginError("phone not matches");

        else if (isSuccess == 2)
            loginView.onLoginError("password is too short");

        else {
            loginView.onLoginSuccess("login success");
        }

    }
}
