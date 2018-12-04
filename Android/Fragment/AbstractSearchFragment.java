package distributor.w2a.com.distributor.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;

import distributor.w2a.com.distributor.R;
import distributor.w2a.com.distributor.activity.AbstractDashboardActivity;
import distributor.w2a.com.distributor.adapter.AbstractRecyclerViewAdapter;
import distributor.w2a.com.distributor.interfaces.AbstractSearchInterface;

public abstract class AbstractSearchFragment extends AbstractFragment implements AbstractSearchInterface {

    public void setFragment() {
        ((AbstractDashboardActivity) getActivity()).setVisibleFragment(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setAbstractFragmentInAdapter() {
        getAdapter().setAbstractFragment(this);
    }


    //make sure all child call super.onViewCreated in their respective onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setFragment();
    }

    protected void clearSearch() {
        try {
            getAbstractDashBoardActivity().clearSearchView();
        } catch (Exception e) {
        }
    }
}