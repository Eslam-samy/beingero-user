package com.corptia.bringero.base;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.corptia.bringero.utils.language.LocaleHelper;

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
