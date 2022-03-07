package com.corptia.bringero.ui.home.setting.contactUs;

import com.corptia.bringero.base.BaseView;
import com.corptia.bringero.graphql.GeneralOptionAllQuery;

import java.util.List;

public interface ContactusView extends BaseView {


    void setSocialMedia(List<GeneralOptionAllQuery.Data1> SocialList);

}
