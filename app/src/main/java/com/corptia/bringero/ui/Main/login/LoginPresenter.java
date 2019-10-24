package com.corptia.bringero.ui.Main.login;

import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.Utils.language.LocaleHelper;
import com.corptia.bringero.graphql.LogInMutation;
import com.corptia.bringero.graphql.MeQuery;
import com.corptia.bringero.graphql.SingleStoreQuery;
import com.corptia.bringero.type.LoginInput;
import com.corptia.bringero.type.RoleEnum;
import com.corptia.bringero.type.StoreFilterInput;
import com.corptia.bringero.ui.splash.SplashActivity;

import org.jetbrains.annotations.NotNull;

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
            loginView.showErrorMessage("phone number is Empty");
        } else if (isSuccess == 1)
            loginView.showErrorMessage("phone not matches");

        else if (isSuccess == 2)
            loginView.showErrorMessage("password is too short");

        else {

            loginView.showProgressBar();


            LoginInput loginInput = LoginInput.builder().phone(phone).password(password).build();

            MyApolloClient.getApollowClient().mutate(LogInMutation.builder().loginData(loginInput).build())
                    .enqueue(new ApolloCall.Callback<LogInMutation.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<LogInMutation.Data> response) {


                            if (response.data().UserMutation().login().status() == 200)
                            {
                                getMe(response.data().UserMutation().login().token(), userData -> {


                                });
                            }
                            else
                            {
                                loginView.hideProgressBar();
                                loginView.showErrorMessage("" + response.data().UserMutation().login().message());
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            loginView.hideProgressBar();
                            loginView.showErrorMessage("[LOG IN]"+e.getMessage());

                        }
                    });
        }

    }


    public void getMe(String token , onSuccessCall onSuccessCall){

        MyApolloClient.getApollowClientAuthorization(token)
                .query(MeQuery.builder().build())
                .enqueue(new ApolloCall.Callback<MeQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<MeQuery.Data> response) {

                        MeQuery.UserData userData =response.data().UserQuery().me().UserData();

                        if (response.data().UserQuery().me().status() == 200)
                        {

                            Common.CURRENT_USER = userData;
                            Common.CURRENT_USER_TOKEN = token;

                            if (userData.roleName().rawValue().equalsIgnoreCase(RoleEnum.STOREADMIN.rawValue()))
                            {
                                getStoreTypes(userData._id() ,token);
                            }

                            else
                            {
                                loginView.hideProgressBar();
                                loginView.onSuccessMessage("login success");

                            }

                            onSuccessCall.CallBack(userData);
                        }

                        else
                        {
                            loginView.hideProgressBar();
                            loginView.showErrorMessage("[ERROR GET ME]"+response.data().UserQuery().me().message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        loginView.hideProgressBar();
                        loginView.showErrorMessage("[ERROR GET ME]"+e.getMessage());
                        Log.d("HAZEM" , ""+e.toString());
                    }
                });

    }

    public void getStoreTypes(String adminUserId , String token){

        StoreFilterInput storeFilterInput = StoreFilterInput.builder().adminUserId(adminUserId).build();
        MyApolloClient.getApollowClientAuthorization(token).query(SingleStoreQuery.builder().filter(storeFilterInput).build())
                .enqueue(new ApolloCall.Callback<SingleStoreQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SingleStoreQuery.Data> response) {

                        SingleStoreQuery.GetAll responseData = response.data().StoreQuery().getAll();

                        if (responseData.status() == 200)
                        {
                           Common.CURRENT_STORE = responseData.CurrentStore().get(0);
                            loginView.hideProgressBar();
                            loginView.onSuccessMessage("login success");
                        }

                        else
                        {
                            loginView.showErrorMessage("[GET DATA STORE]"+responseData.message());

                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        loginView.showErrorMessage("ERROR DATA STORE"+e.getMessage());
                    }
                });

    }

    public interface onSuccessCall{
        void CallBack(MeQuery.UserData userData);
    }
}
