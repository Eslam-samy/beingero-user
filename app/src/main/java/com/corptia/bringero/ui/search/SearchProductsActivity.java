package com.corptia.bringero.ui.search;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.Interface.AdapterIOnProductClickListener;
import com.corptia.bringero.Interface.CallbackListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.CreateCartItemMutation;
import com.corptia.bringero.graphql.GetStoreProductsQuery;
import com.corptia.bringero.graphql.StoreSearchQuery;
import com.corptia.bringero.model.EventBus.CalculateCartEvent;
import com.corptia.bringero.model.MyCart;
import com.corptia.bringero.type.CreateCartItem;
import com.corptia.bringero.type.PaginationInput;
import com.corptia.bringero.type.SEARCH_Input;
import com.corptia.bringero.type.StoreGalleryFilter;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.ui.storesDetail.NewStoreDetailAdapter;
import com.corptia.bringero.ui.storesDetail.StoreDetailActivity;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.utils.recyclerview.PaginationListener;
import com.corptia.bringero.utils.recyclerview.decoration.GridSpacingItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.corptia.bringero.Common.Constants.VIEW_TYPE_ITEM;
import static com.corptia.bringero.Common.Constants.VIEW_TYPE_LOADING;
import static com.corptia.bringero.utils.recyclerview.PaginationListener.PAGE_SIZE;
import static com.corptia.bringero.utils.recyclerview.PaginationListener.PAGE_START;

public class SearchProductsActivity extends BaseActivity {


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

    NewStoreDetailAdapter storeDetailAdapter;

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

    GridLayoutManager gridLayoutManager;


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

    //Speed Cart
    double totalPriceCart;
    int countOfCart;
    boolean isHaveCart = false;
    @BindView(R.id.txt_totalPriceCart)
    TextView txt_totalPriceCart;
    @BindView(R.id.btn_view_cart)
    TextView btn_view_cart;
    @BindView(R.id.layout_speed_cart)
    ConstraintLayout layout_speed_cart;

    //ProgressBar
    @BindView(R.id.progress_search)
    ProgressBar loading;

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
                switch (storeDetailAdapter.getItemViewType(position)) {
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
                Common.dpToPx(17, this),
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

                    storeDetailAdapter.removeSearch();

                    isLoading = false;

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
                isLastPage=false;
                edt_search.setText("");
                layout_placeholder.setVisibility(View.VISIBLE);
                recycler_product.setVisibility(View.GONE);
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
                for (MyCart myCartItem : Common.myLocalCart) {
                    updateCartItem(myCartItem);
                }

                isLoading = true;
                currentPage++;
                if (currentPage <= totalPages) {
                    storeDetailAdapter.addLoadingSearch();
                    performSearch();
                } else {
                    isLastPage = true;
                }
            }

            @Override
            public boolean isLastPage() {
                Log.i("TAG Moha search", "isLastPage: " + isLastPage);
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        storeDetailAdapter = new NewStoreDetailAdapter(SearchProductsActivity.this, null, true);
        recycler_product.setAdapter(storeDetailAdapter);

        initPlaceHolderSearch();

        getSpeedCart();

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

        loading.setVisibility(View.VISIBLE);

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

                                loading.setVisibility(View.GONE);


                                if (data.status() == 200) {

                                    recycler_product.setVisibility(View.VISIBLE);


                                    if (isLoading) {
                                        storeDetailAdapter.removeLoadingSearch();
                                        isLoading = false;
                                    }

                                    layout_placeholder.setVisibility(View.GONE);

                                    totalPages = response.data().PricingProductQuery().getStoreProducts().pagination().totalPages();
                                    storeDetailAdapter.addItemsSearch(data.ProductQuery());
                                    storeDetailAdapter.setListener(new AdapterIOnProductClickListener() {
                                        @Override
                                        public void onClickNoSearch(GetStoreProductsQuery.Product product, String cartID, double amount, double price, boolean inCart, boolean isDecrease, View txt_amount, View btn_delete, View bg_delete) {
                                        }

                                        @Override
                                        public void onClickSearch(StoreSearchQuery.ProductQuery product, String cartID, double amount, double price, boolean inCart, boolean isDecrease, View txt_amount, View btn_delete, View bg_delete) {
                                            String id = product._id();
                                            Boolean isPackaged = product.Product().isPackaged();
                                            Double minAmount = product.Product().minSellingUnits();
                                            if (!Common.myLocalCart.isEmpty())
                                                for (int i = Common.myLocalCart.size() - 1; i >= 0; i--) {
                                                    MyCart myCartItem = Common.myLocalCart.get(i);
                                                    if (!myCartItem.getProductId().equals(id)) {
                                                        updateCartItem(myCartItem);
                                                    }
                                                }
                                            if (!isDecrease) {
                                                if (!Common.myLocalCartIds.contains(id)) {
                                                    Common.myLocalCartIds.add(id);
                                                    MyCart myCartItem = new MyCart();
                                                    myCartItem.setAmount(amount);
                                                    myCartItem.setCartId(cartID);
                                                    myCartItem.setInCart(inCart);
                                                    myCartItem.setDecrease(isDecrease);
                                                    myCartItem.setPrice(price);
                                                    myCartItem.setPackaged(isPackaged);
                                                    myCartItem.setMinAmount(minAmount);
                                                    myCartItem.setProductId(id);
                                                    Common.myLocalCart.add(myCartItem);
                                                } else {
                                                    for (MyCart myCartItem : Common.myLocalCart) {
                                                        if (myCartItem.getProductId().equals(id)) {
                                                            int index = Common.myLocalCart.indexOf(myCartItem);
                                                            myCartItem.setAmount(amount);
                                                            myCartItem.setDecrease(isDecrease);
                                                            Common.myLocalCart.set(index, myCartItem);
                                                        }
                                                    }
                                                }
                                                EventBus.getDefault().postSticky(new CalculateCartEvent(true, price, amount));
                                            } else {
                                                MyCart myCartItem = new MyCart();
                                                if (!Common.myLocalCartIds.contains(id)) {
                                                    Common.myLocalCartIds.add(id);
                                                    myCartItem.setAmount(amount);
                                                    myCartItem.setCartId(cartID);
                                                    myCartItem.setInCart(true);
                                                    myCartItem.setDecrease(true);
                                                    myCartItem.setPrice(price);
                                                    myCartItem.setProductId(id);
                                                    myCartItem.setPackaged(isPackaged);
                                                    myCartItem.setMinAmount(minAmount);
                                                    Common.myLocalCart.add(myCartItem);
                                                } else {
                                                    for (MyCart myCart : Common.myLocalCart) {
                                                        if (myCart.getProductId().equals(id)) {
                                                            myCart.setAmount(amount);
                                                            myCart.setDecrease(true);
                                                            myCartItem = myCart;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            if (amount > 0) {
                                                txt_amount.setVisibility(View.VISIBLE);
                                                btn_delete.setVisibility(View.VISIBLE);
                                                bg_delete.setVisibility(View.VISIBLE);
                                            } else {
                                                txt_amount.setVisibility(View.INVISIBLE);
                                                btn_delete.setVisibility(View.INVISIBLE);
                                                bg_delete.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });

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
                                loading.setVisibility(View.GONE);
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
            searchWord = spokenText;

            currentPage = 1;
            storeDetailAdapter.removeSearch();
            isLoading = false;

            performSearch();
            // Do something with spokenText
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    void showPlaceHolder() {
        layout_placeholder.setVisibility(View.VISIBLE);
    }


    public void getSpeedCart() {
        if (Common.TOTAL_CART_AMOUNT <= 0) {
            Common.TOTAL_CART_AMOUNT = 0;
            isHaveCart = false;
            layout_speed_cart.setVisibility(View.GONE);
        } else {
            isHaveCart = true;
            layout_speed_cart.setVisibility(View.VISIBLE);
            totalPriceCart = Common.TOTAL_CART_PRICE;
            countOfCart = Common.TOTAL_CART_AMOUNT;

            txt_totalPriceCart.setText(new StringBuilder().append(Common.getDecimalNumber(totalPriceCart)).append(" ").append(getString(R.string.currency)));
//            btn_view_cart.setText(new StringBuilder().append(getString(R.string.view_cart)).append(" ( ").append(countOfCart).append(" ) "));
            btn_view_cart.setText(new StringBuilder().append(getString(R.string.view_cart)));
        }
        layout_speed_cart.setOnClickListener(view -> gotoCart());
    }

    void gotoCart() {
        try {
            Intent intent = new Intent(SearchProductsActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.EXTRA_SPEED_CART, "EXTRA_SPEED_CART");
            EventBus.getDefault().unregister(this);
            startActivity(intent);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void request(CalculateCartEvent event) {
        if (event != null) {
            if (event.isSuccess()) {
                countOfCart = Common.TOTAL_CART_AMOUNT;
                totalPriceCart += event.getProductPrice();

                isHaveCart = true;
                layout_speed_cart.setVisibility(View.VISIBLE);
                txt_totalPriceCart.setText(new StringBuilder().append(Common.getDecimalNumber(totalPriceCart)).append(" ").append(getString(R.string.currency)));
                btn_view_cart.setText(new StringBuilder().append(getString(R.string.view_cart)));
                Common.totalPriceCart=totalPriceCart;
                if (totalPriceCart <= 0.0f) {
                    isHaveCart = false;
                    layout_speed_cart.setVisibility(View.GONE);
                }
            } else {
                if (Common.TOTAL_CART_AMOUNT == 0) {
                    isHaveCart = false;
                    layout_speed_cart.setVisibility(View.GONE);
                }
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
        Log.d("HAZEM", "onDestroy");


    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().postSticky(new CalculateCartEvent(false, 0, 0));
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onPause() {
        sendToServer();
        Common.myLocalCart.clear();
        Common.myLocalCartIds.clear();
        super.onPause();
    }

    private void sendToServer() {
        for (MyCart myCartItem : Common.myLocalCart) {
            updateCartItem(myCartItem);
        }

    }

    private void updateCartItem(MyCart myCartItem) {
        if (!myCartItem.getInCart()) {
            if (myCartItem.getAmount() > 0) {
                addToCart(myCartItem);
            }
        } else {
            if (myCartItem.getAmount() > 0) {
                storeDetailAdapter.updateCartItems(myCartItem);
            } else {
                storeDetailAdapter.deleteCartItems(myCartItem);
            }
        }
    }

    //TODO Here Make Refresh
    //notifyItemChanged(position); (have two adapter product and search)
    public void addToCart(MyCart myCartItem) {
        String id = myCartItem.getProductId();
        double amount = myCartItem.getAmount();
        CreateCartItem item = CreateCartItem.builder().amount(amount).pricingProductId(id).build();
        MyApolloClient.getApolloClient().mutate(CreateCartItemMutation.builder().data(item).build())
                .enqueue(new ApolloCall.Callback<CreateCartItemMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<CreateCartItemMutation.Data> response) {
                        CreateCartItemMutation.Create createResponse = response.data().CartItemMutation().create();
                        if (createResponse.status() == 200) {
                            myCartItem.setInCart(true);
                            myCartItem.setCartId(createResponse.data()._id());
                            Common.GetCartItemsCount(null);
                        } else {

                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                    }
                });
    }


}
