package com.corptia.bringero.ui.setting.contactUs;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.GeneralOptionAllQuery;
import com.corptia.bringero.graphql.MessageMutation;
import com.corptia.bringero.type.CreateMessage;
import com.corptia.bringero.type.MessageUserType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ContactusPresenter {


    ContactusView view;

    public ContactusPresenter(ContactusView view) {
        this.view = view;
    }

    public void sendMessage(String title , String message){


                CreateMessage createMessage = CreateMessage.builder()
                .message(message)
                .title(title)
                .userId(Common.CURRENT_USER.getId())
                .userType(MessageUserType.CUSTOMER).build();

        view.showProgressBar();

        MyApolloClient
                .getApollowClientAuthorization()
                .mutate(MessageMutation.builder().data(createMessage).build())
                .enqueue(new ApolloCall.Callback<MessageMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<MessageMutation.Data> response) {

                        if (response.data().MessageMutation().create().status() == 200 ){

                            view.onSuccessMessage("");
                            view.hideProgressBar();
                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        view.hideProgressBar();

                    }
                });

    }


    public void getAllOption(){

        view.showProgressBar();

        MyApolloClient.getApollowClientAuthorization().query(GeneralOptionAllQuery.builder().build()).enqueue(new ApolloCall.Callback<GeneralOptionAllQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GeneralOptionAllQuery.Data> response) {

                view.hideProgressBar();

                GeneralOptionAllQuery.@Nullable GetAll optionResponse = response.data().GeneralOptionQuery().getAll();

                if (optionResponse.status() == 200 ){

                    view.setSocialMedia(optionResponse.data());
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

                view.hideProgressBar();
            }
        });

    }


}
