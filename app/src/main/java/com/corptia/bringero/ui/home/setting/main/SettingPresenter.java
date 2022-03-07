package com.corptia.bringero.ui.home.setting.main;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.LogoutMutation;
import com.corptia.bringero.type.UserDeviceInput;

import org.jetbrains.annotations.NotNull;

public class SettingPresenter {

    public SettingView view;

    public SettingPresenter(SettingView view) {
        this.view = view;
    }

    public void logOut(String tokenFirebase){

        view.showProgressBar();

        MyApolloClient.
                getApollowClientAuthorization()
                .mutate(LogoutMutation.builder().device(UserDeviceInput.builder().token(tokenFirebase).build()).build())
                .enqueue(new ApolloCall.Callback<LogoutMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<LogoutMutation.Data> response) {

                        view.hideProgressBar();

                        if (response.data().UserMutation().logout().status() == 200){
                            view.OnSuccessLogOut();
                        }
                        else
                        {
                            view.OnFailedLogOut();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        view.OnFailedLogOut();
                        view.hideProgressBar();

                    }
                });


    }

}
