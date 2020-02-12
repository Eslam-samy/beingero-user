package com.corptia.bringero.ui.webview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends BaseActivity {

    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.btn_close)
    ImageView btn_close;
    @BindView(R.id.title)
    TextView title;

    String url = "http://app.bringero.site/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        ButterKnife.bind(this);

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if (getIntent() != null) {
            if (getIntent().hasExtra(Constants.EXTRA_TERMS_CONDITIONS)) {

                url = "https://bringero.flycricket.io/privacy.html";
                title.setText(getString(R.string.terms_conditions));
            } else if (getIntent().hasExtra(Constants.EXTRA_PRIVACY_POLICY)) {
                url = "https://bringero.flycricket.io/privacy.html";
                title.setText(getString(R.string.privacy_policy));
            } else if (getIntent().hasExtra(Constants.EXTRA_FAQ_SUPPORT)) {
                url = "https://bringero.flycricket.io/privacy.html";
                title.setText(getString(R.string.faq_support));
            }
        }

        webview.loadUrl(url);

        btn_close.setOnClickListener(view -> {
            webview.stopLoading();
            finish();
            overridePendingTransition( R.anim.fade_in, R.anim.fade_out );
        });


    }



}
