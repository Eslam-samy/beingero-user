package com.corptia.bringero.ui.Main.login;


public interface OnLoginFinishedListener {

    void onUsernameError();

    void onPasswordError();

    void onSuccess();
}
