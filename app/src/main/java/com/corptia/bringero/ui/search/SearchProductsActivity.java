package com.corptia.bringero.ui.search;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
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
import com.corptia.bringero.databinding.ActivitySearchProductsBinding;
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
import com.corptia.bringero.ui.home.HomeModefiedActivity;
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

    ActivitySearchProductsBinding binding;
    //For Pagination
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    int totalPages = 1;


    NewStoreDetailAdapter storeDetailAdapter;

    String storeId;
    String imageUrl;
    String searchWord = "";
    private static final int SPEECH_REQUEST_CODE = 0;
    GridLayoutManager gridLayoutManager;

    //Speed Cart
    double totalPriceCart;
    int countOfCart;
    boolean isHaveCart = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_products);


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

        binding.recyclerProduct.setLayoutManager(gridLayoutManager);
        binding.recyclerProduct.addItemDecoration(new GridSpacingItemDecoration(
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

        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {

            Log.d("ESLAM", "onCreate: " + actionId + EditorInfo.IME_ACTION_SEARCH);
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchWord = binding.searchInput.getText().toString();

                currentPage = 1;

                storeDetailAdapter.removeSearch();

                isLoading = false;

                performSearch();

                return true;
            }
            return false;
        });

        //SetData To Toolbar


        binding.imgClean.setOnClickListener(view -> {
            isLastPage = false;
            binding.searchInput.setText("");
            binding.placeHolder.getRoot().setVisibility(View.VISIBLE);
            binding.recyclerProduct.setVisibility(View.GONE);
        });

        binding.imgSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySpeechRecognizer();
            }
        });

        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (binding.searchInput.getText().toString().isEmpty()) {
                    binding.imgSpeech.setVisibility(View.VISIBLE);
                    binding.imgClean.setVisibility(View.GONE);
                } else {
                    binding.imgSpeech.setVisibility(View.GONE);
                    binding.imgClean.setVisibility(View.VISIBLE);
                }


            }
        });


        binding.recyclerProduct.addOnScrollListener(new PaginationListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                for (MyCart myCartItem : Common.myLocalCart) {
                    updateCartItem(myCartItem);
                }

                isLoading = true;
                currentPage++;
                if (currentPage <= totalPages) {
                    Common.adapterIsLoading = true;
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
        binding.recyclerProduct.setAdapter(storeDetailAdapter);

        initPlaceHolderSearch();

        getSpeedCart();

    }

    private void initPlaceHolderSearch() {

        binding.placeHolder.getRoot().setVisibility(View.VISIBLE);
        binding.placeHolder.btn1.setVisibility(View.GONE);
        binding.placeHolder.btn2.setVisibility(View.GONE);
        binding.placeHolder.imgPlaceholder.setImageResource(R.drawable.ic_placeholder_search);
        binding.placeHolder.txtPlaceholderTitle.setText(getString(R.string.placeholder_title_search));
        binding.placeHolder.txtPlaceholderDec.setText(getString(R.string.placeholder_dec_search));

    }

    private void performSearch() {

        binding.progressSearch.setVisibility(View.VISIBLE);

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

                                binding.progressSearch.setVisibility(View.GONE);
                                Common.adapterIsLoading = false;

                                if (data.status() == 200) {

                                    binding.recyclerProduct.setVisibility(View.VISIBLE);


                                    if (isLoading) {
                                        storeDetailAdapter.removeLoadingSearch();
                                        isLoading = false;
                                    }

                                    binding.placeHolder.getRoot().setVisibility(View.GONE);

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
                                    binding.placeHolder.getRoot().setVisibility(View.VISIBLE);
                                }

                            }
                        });


                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.progressSearch.setVisibility(View.GONE);
                                binding.placeHolder.getRoot().setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });


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
            binding.searchInput.setText(spokenText);
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
        binding.placeHolder.getRoot().setVisibility(View.VISIBLE);
    }


    public void getSpeedCart() {
        if (Common.TOTAL_CART_AMOUNT <= 0) {
            Common.TOTAL_CART_AMOUNT = 0;
            isHaveCart = false;
            binding.speedCart.getRoot().setVisibility(View.GONE);
        } else {
            isHaveCart = true;
            binding.speedCart.getRoot().setVisibility(View.VISIBLE);
            totalPriceCart = Common.TOTAL_CART_PRICE;
            countOfCart = Common.TOTAL_CART_AMOUNT;


            binding.speedCart.price.setText(new StringBuilder().append(Common.getDecimalNumber(totalPriceCart)).append(" ").append(getString(R.string.currency)));
            binding.speedCart.count.setText("X " + countOfCart);
//            btn_view_cart.setText(new StringBuilder().append(getString(R.string.view_cart)).append(" ( ").append(countOfCart).append(" ) "));
        }
        binding.speedCart.getRoot().setOnClickListener(view -> gotoCart());
    }

    void gotoCart() {
        try {
            Intent intent = new Intent(SearchProductsActivity.this, HomeModefiedActivity.class);
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
                binding.speedCart.getRoot().setVisibility(View.VISIBLE);
                binding.speedCart.getRoot().setVisibility(View.VISIBLE);
                binding.speedCart.price.setText(new StringBuilder().append(Common.getDecimalNumber(totalPriceCart)).append(" ").append(getString(R.string.currency)));
                binding.speedCart.count.setText("X " + countOfCart);
                Common.totalPriceCart = totalPriceCart;
                if (totalPriceCart <= 0.0f) {
                    isHaveCart = false;
                    binding.speedCart.getRoot().setVisibility(View.GONE);
                }
            } else {
                if (Common.TOTAL_CART_AMOUNT == 0) {
                    isHaveCart = false;
                    binding.speedCart.getRoot().setVisibility(View.GONE);
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
