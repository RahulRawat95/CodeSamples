package distributor.w2a.com.distributor.globalLibrary.globalDialogs;


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

import java.util.List;

import distributor.w2a.com.distributor.R;
import distributor.w2a.com.distributor.adapter.OptionBottomSheetAdapter;


public class OptionBottomSheetFragment<X> extends BottomSheetDialogFragment {

    private EditText search_option_item;
    private RecyclerView option_item_list;
    private ImageView close;
    private SelectedCallback<X> selectedCallBack;
    private List<X> list;
    private OptionBottomSheetAdapter<X> adapter;

    public interface SelectedCallback<X> {
        void stringSelectedCallBack(X x);
    }


    public static <X> OptionBottomSheetFragment newInstance(List<X> list, SelectedCallback<X> selectedCallback) {

        Bundle args = new Bundle();

        OptionBottomSheetFragment<X> fragment = new OptionBottomSheetFragment<X>();
        fragment.selectedCallBack = selectedCallback;
        fragment.list = list;
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
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        adapter = new OptionBottomSheetAdapter<X>(list, getActivity(), new OptionBottomSheetAdapter.ListOnItemClickListener<X>() {
            @Override
            public void onItemClick(X stateForSpinner) {
                selectedCallBack.stringSelectedCallBack(stateForSpinner);
                dismiss();
            }
        });

        option_item_list.setAdapter(adapter);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
