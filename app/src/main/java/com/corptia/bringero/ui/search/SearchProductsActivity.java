package com.corptia.bringero.ui.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.GetPricedByQuery;
import com.corptia.bringero.type.ProductFilterInput;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.utils.recyclerview.decoration.GridSpacingItemDecoration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchProductsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.edt_search)
    EditText edt_search;

    @BindView(R.id.recycler_product)
    RecyclerView recycler_product;

    SearchAdapter adapter ;

    String storeId;

    @BindView(R.id.img_logo)
    ImageView img_logo ;
    @BindView(R.id.txt_name)
    TextView txt_name ;

    String imageUrl;

    @BindView(R.id.img_clean)
    ImageView img_clean;
    @BindView(R.id.img_speech)
    ImageView img_speech;

    private static final int SPEECH_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        ButterKnife.bind(this);
        initActionBar();

        recycler_product.setLayoutManager(new GridLayoutManager(this, 2));
        recycler_product.addItemDecoration(new GridSpacingItemDecoration(
                2,
                Common.dpToPx(30, this),
                true,
                0,
                Common.dpToPx(10, this),
                Common.dpToPx(10, this),
                Common.dpToPx(10, this)));

        if (getIntent()!=null)
        {
            Intent intent = getIntent();
            storeId = intent.getStringExtra(Constants.EXTRA_STORE_ID);
            imageUrl = intent.getStringExtra(Constants.EXTRA_STORE_IMAGE);

        }

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        //SetData To Toolbar
        PicassoUtils.setImage(imageUrl , img_logo);
        txt_name.setText( Common.CURRENT_STORE.name());


        img_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_search.setText("");
            }
        });

        img_speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySpeechRecognizer();
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (edt_search.getText().toString().isEmpty())
                {
                    img_speech.setVisibility(View.VISIBLE);
                    img_clean.setVisibility(View.GONE);
                }
                else
                {
                    img_speech.setVisibility(View.GONE);
                    img_clean.setVisibility(View.VISIBLE);
                }


            }
        });

    }

    private void performSearch() {


        ProductFilterInput productFilterInput = ProductFilterInput.builder().name(edt_search.getText().toString()).build();
        MyApolloClient.getApollowClientAuthorization().query(GetPricedByQuery.builder().storeId(storeId).filter(productFilterInput).build())
                .enqueue(new ApolloCall.Callback<GetPricedByQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetPricedByQuery.Data> response) {
                        GetPricedByQuery.@Nullable GetStoreProducts data = response.data().ProductQuery().getStoreProducts();
                        if (data.status() == 200)
                        {
                            adapter = new SearchAdapter(SearchProductsActivity.this , data.Products()) ;
                            recycler_product.setAdapter(adapter);
                        }
                        else
                        {

                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });


    }


    private void initActionBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//
////        MenuItem item = menu.findItem(R.id.action_search);
//
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }



    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            edt_search.setText(spokenText);
            // Do something with spokenText
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}
