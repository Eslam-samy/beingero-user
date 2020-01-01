package com.corptia.bringero.ui.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.StoreSearchQuery;
import com.corptia.bringero.type.PaginationInput;
import com.corptia.bringero.type.ProductFilterInput;
import com.corptia.bringero.type.SEARCH_Input;
import com.corptia.bringero.type.StoreGalleryFilter;
import com.corptia.bringero.ui.storesDetail.StoreDetailAdapter;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.utils.recyclerview.PaginationListener;
import com.corptia.bringero.utils.recyclerview.decoration.GridSpacingItemDecoration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.corptia.bringero.Common.Constants.VIEW_TYPE_ITEM;
import static com.corptia.bringero.Common.Constants.VIEW_TYPE_LOADING;
import static com.corptia.bringero.utils.recyclerview.PaginationListener.PAGE_SIZE;
import static com.corptia.bringero.utils.recyclerview.PaginationListener.PAGE_START;

public class SearchProductsActivity extends AppCompatActivity {


    //For Pagination
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    int totalPages = 1;


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.edt_search)
    EditText edt_search;

    @BindView(R.id.recycler_product)
    RecyclerView recycler_product;

    StoreDetailAdapter adapter;

    String storeId;

    @BindView(R.id.img_logo)
    ImageView img_logo;
    @BindView(R.id.txt_name)
    TextView txt_name;

    String imageUrl;

    @BindView(R.id.img_clean)
    ImageView img_clean;
    @BindView(R.id.img_speech)
    ImageView img_speech;

    String searchWord = "";

    private static final int SPEECH_REQUEST_CODE = 0;

    GridLayoutManager gridLayoutManager ;


    //For Placeholder
    @BindView(R.id.layout_placeholder)
    ConstraintLayout layout_placeholder;
    @BindView(R.id.img_placeholder)
    ImageView img_placeholder;
    @BindView(R.id.txt_placeholder_title)
    TextView txt_placeholder_title;
    @BindView(R.id.txt_placeholder_dec)
    TextView txt_placeholder_dec;
    @BindView(R.id.btn_1)
    Button btn_1;
    @BindView(R.id.btn_2)
    Button btn_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        ButterKnife.bind(this);
        initActionBar();



        gridLayoutManager = new GridLayoutManager(this, 2);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case VIEW_TYPE_ITEM:
                        return 1;
                    case VIEW_TYPE_LOADING:
                        return 2; //number of columns of the grid
                    default:
                        return -1;
                }
            }
        });

        recycler_product.setLayoutManager(gridLayoutManager);
        recycler_product.addItemDecoration(new GridSpacingItemDecoration(
                2,
                Common.dpToPx(10, this),
                true,
                0,
                Common.dpToPx(17,this),
                Common.dpToPx(2, this),
                Common.dpToPx(2, this)));


        if (getIntent() != null) {
            Intent intent = getIntent();
            storeId = intent.getStringExtra(Constants.EXTRA_STORE_ID);
            imageUrl = intent.getStringExtra(Constants.EXTRA_STORE_IMAGE);
        }

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    searchWord = edt_search.getText().toString();

                    currentPage = 1;

                    adapter.removeSearch();

                    performSearch();
                    return true;
                }
                return false;
            }
        });

        //SetData To Toolbar
        PicassoUtils.setImage(imageUrl, img_logo);
        txt_name.setText(Common.CURRENT_STORE.name());


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

                if (edt_search.getText().toString().isEmpty()) {
                    img_speech.setVisibility(View.VISIBLE);
                    img_clean.setVisibility(View.GONE);
                } else {
                    img_speech.setVisibility(View.GONE);
                    img_clean.setVisibility(View.VISIBLE);
                }


            }
        });


        recycler_product.addOnScrollListener(new PaginationListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {

                isLoading = true;
                currentPage++;
                if (currentPage <= totalPages) {
                    adapter.addLoadingSearch();
                    performSearch();
                } else {
                    isLastPage = true;
                }
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });



        adapter = new StoreDetailAdapter(SearchProductsActivity.this, null , true);
        recycler_product.setAdapter(adapter);

        adapter.setListener((view, position) -> {

//                    Intent intent = new Intent(getActivity() , ProductDetailActivity.class);
//                    GetStoreProductsQuery.Product mProduct =  storeDetailAdapter.getSelectProduct(position);
//                    intent.putExtra(Constants.EXTRA_PRODUCT_ID , mProduct._id());
//                    if (mProduct.Product().ImageResponse().data()!=null)
//                    intent.putExtra(Constants.EXTRA_PRODUCT_IMAGE , mProduct.Product().ImageResponse().data().name());
//                    startActivity(intent);

            //Here Add To Cart

            Log.d("HAZEM" , "From Activity : " +position);

        });

        initPlaceHolderSearch();

    }

    private void initPlaceHolderSearch() {

        layout_placeholder.setVisibility(View.VISIBLE);

        btn_1.setVisibility(View.GONE);
        btn_2.setVisibility(View.GONE);
        img_placeholder.setImageResource(R.drawable.ic_placeholder_search);
        txt_placeholder_title.setText(getString(R.string.placeholder_title_search));
        txt_placeholder_dec.setText(getString(R.string.placeholder_dec_search));

    }

    private void performSearch() {

        SEARCH_Input search_input = SEARCH_Input.builder().searchWord(searchWord).build();
        PaginationInput paginationInput = PaginationInput.builder().page(currentPage).limit(PAGE_SIZE).build();

//        ProductFilterInput productFilterInput = ProductFilterInput.builder().sEARCH(search_input).build();
        StoreGalleryFilter storeGalleryFilter = StoreGalleryFilter.builder().isAvailable(true)
                .productName(searchWord).build();

        MyApolloClient.getApollowClientAuthorization().query(StoreSearchQuery.builder()
                .storeId(storeId)
                .filter(storeGalleryFilter)
                .pagination(paginationInput)
                .build())
                .enqueue(new ApolloCall.Callback<StoreSearchQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<StoreSearchQuery.Data> response) {

                        StoreSearchQuery.GetStoreProducts data = response.data().PricingProductQuery().getStoreProducts();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (data.status() == 200) {

                                    if (isLoading) {
                                        adapter.removeLoadingSearch();
                                        isLoading = false;
                                    }

                                    layout_placeholder.setVisibility(View.GONE);

                                    totalPages = response.data().PricingProductQuery().getStoreProducts().pagination().totalPages();
                                    adapter.addItemsSearch(data.ProductQuery());



                                } else {
                                    layout_placeholder.setVisibility(View.VISIBLE);
                                }

                            }
                        });


                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layout_placeholder.setVisibility(View.VISIBLE);
                            }
                        });
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
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Common.CURRENT_USER.getLanguage().toLowerCase());
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


    void showPlaceHolder(){
        layout_placeholder.setVisibility(View.VISIBLE);
    }

}
