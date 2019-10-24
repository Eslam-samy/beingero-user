package com.corptia.bringero.base;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.corptia.bringero.Utils.language.LocaleHelper;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public  abstract class BaseActivity extends AppCompatActivity {


//    protected Unbinder unbinder;

//    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

//    protected void binding(int layout) {
//        setContentView(layout);
//        unbinder = ButterKnife.bind(this);
//    }

//    @Override protected void onDestroy() {
//        super.onDestroy();
//        unbinder.unbind();
//    }

    //For Get Language
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
