package distributor.w2a.com.distributor.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import distributor.w2a.com.distributor.R;
import distributor.w2a.com.distributor.adapter.ApiSearchBottomSheetAdapter;
import distributor.w2a.com.distributor.network.ApiClient;
import distributor.w2a.com.distributor.network.ApiInterface;
import distributor.w2a.com.distributor.repository.Utility;
import retrofit2.Call;
import retrofit2.Response;

public class ApiSearchFragment<X> extends BottomSheetDialogFragment {
    private static final int LIMIT_OF_ITEMS = 10;

    private boolean isLastPage = false, isLoading = false;

    public interface Callback<X> {
        void callback(X x);
    }

    protected Callback<X> callback;
    protected String apiUrl;

    private EditText search_option_item;
    private RecyclerView option_item_list;
    private ImageView close;
    private ApiSearchBottomSheetAdapter<X> apiSearchBottomSheetAdapter;

    private ApiInterface apiInterface;

    private HashMap<String, List<X>> map;
    private List<X> list;
    private X x;
    private Type type;
    private HashMap<String, Object> extraEntries;

    public static <X> ApiSearchFragment newInstance(String apiUrl, X x, Type type, Callback<X> callback, HashMap<String, ? extends Object> extraDetails) {

        Bundle args = new Bundle();

        ApiSearchFragment fragment = new ApiSearchFragment();
        fragment.apiUrl = apiUrl;
        fragment.callback = callback;
        fragment.x = x;
        fragment.map = new HashMap();
        fragment.type = type;
        fragment.extraEntries = extraDetails;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_option_bottom_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        close = view.findViewById(R.id.close_dialog);
        search_option_item = view.findViewById(R.id.search_option_item);
        option_item_list = view.findViewById(R.id.option_item_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        option_item_list.setLayoutManager(linearLayoutManager);

        search_option_item.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 3) {
                    getApiData(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        list = new ArrayList<>();
        apiSearchBottomSheetAdapter = new ApiSearchBottomSheetAdapter<X>(getActivity(), list, new ApiSearchBottomSheetAdapter.ListOnItemClickListener<X>() {
            @Override
            public void onItemClick(X selectedItem) {
                callback.callback(selectedItem);
                dismiss();
            }
        });

        option_item_list.setAdapter(apiSearchBottomSheetAdapter);

        option_item_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int PAGE_SIZE = LIMIT_OF_ITEMS;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        isLoading = true;
                        getMoreItems();
                    }
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setList(List<X> list) {
        this.list = list;
        apiSearchBottomSheetAdapter.setList(list);
        isLastPage = false;
    }

    public void getApiData(String s) {
        if (map.get(s) != null) {
            setList(map.get(s));
        } else {
            list = new ArrayList<>();
            setList(list);
            getMoreItems();
        }
    }

    public void getMoreItems() {
        if (apiInterface == null)
            apiInterface = ApiClient.getApiInterface();
        int size = 0;
        if (list.size() > 0)
            size = list.size() - 1;
        JsonObject jsonObject = Utility.createJsonObject(new String[]{"STARTINDEX", "LIMITS", "SEARCH"}, size, LIMIT_OF_ITEMS, search_option_item.getText().toString());
        Utility.addExtraEntries(jsonObject, extraEntries);
        apiInterface.getApiSearch(apiUrl, jsonObject).enqueue(new retrofit2.Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (!response.isSuccessful()) {
                    getMoreItems();
                    return;
                }
                List<X> productList = new Gson().fromJson(response.body(), type);
                try {
                    list.remove(list.size() - 1);
                } catch (Exception e) {
                }
                if (productList.size() > 0) {
                    int oldSize = list.size() == 0 ? 0 : list.size() - 1;
                    list.addAll(productList);
                    if (productList.size() >= LIMIT_OF_ITEMS) {
                        list.add(x);
                    } else {
                        isLastPage = true;
                    }
                    apiSearchBottomSheetAdapter.notifyItemRangeChanged(oldSize, list.size());
                } else {
                    isLastPage = true;
                    apiSearchBottomSheetAdapter.notifyItemRemoved(list.size());
                }
                isLoading = false;
                map.put(search_option_item.getText().toString(), list);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                getMoreItems();
            }
        });
    }

}