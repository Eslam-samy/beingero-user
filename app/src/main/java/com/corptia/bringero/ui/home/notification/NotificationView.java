package com.corptia.bringero.ui.home.notification;


import com.corptia.bringero.base.BaseView;
import com.corptia.bringero.graphql.NotificationQuery;

public interface NotificationView extends BaseView {

    void setNotification(NotificationQuery.GetAll notificationList);
    void onNotFoundData();
    void onErrorConnection();

}
