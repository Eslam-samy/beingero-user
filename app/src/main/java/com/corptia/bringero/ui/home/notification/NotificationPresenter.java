package com.corptia.bringero.ui.home.notification;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.NotificationQuery;
import com.corptia.bringero.type.NotificationFilterInput;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.corptia.bringero.utils.recyclerview.PaginationListener.PAGE_SIZE;


public class NotificationPresenter {

    NotificationView view;

    public NotificationPresenter(NotificationView view) {
        this.view = view;
    }

    public void getNotification(int currentPage) {


        NotificationFilterInput notificationFilterInput = NotificationFilterInput.builder().build();
        MyApolloClient.getApollowClientAuthorization()
                .query(NotificationQuery.builder()
                        .filter(notificationFilterInput)
                        .limit(PAGE_SIZE)
                        .page(currentPage)
                        .build())
                .enqueue(new ApolloCall.Callback<NotificationQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<NotificationQuery.Data> response) {

                        view.hideProgressBar();

                        NotificationQuery.@Nullable GetAll notificationList = response.data().NotificationQuery().getAll();

                        if (notificationList.status() == 200) {

                            view.setNotification(notificationList);

                        }

                        else if (notificationList.status() == 404)
                        {
                            view.onNotFoundData();
                        }


                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        view.onErrorConnection();
                        view.hideProgressBar();
                    }
                });


    }
}
