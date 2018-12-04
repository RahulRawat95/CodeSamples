package distributor.w2a.com.distributor.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import distributor.w2a.com.distributor.R;
import distributor.w2a.com.distributor.activity.AbstractDashboardActivity;
import distributor.w2a.com.distributor.interfaces.AbstractSearchInterface;
import retrofit2.Call;
import retrofit2.Callback;

public abstract class AbstractSearchBottomSheetFragment extends BottomSheetDialogFragment implements AbstractSearchInterface {
    protected EditText filterEt;

    protected RelativeLayout mRfiProgressBar;
    protected FloatingActionButton mRfiReloadFAB;

    public void setAbstractFragmentInAdapter() {
        getAdapter().setAbstractFragment(this);
    }

    public AbstractDashboardActivity getAbstractDashBoardActivity() {
        return (AbstractDashboardActivity) getActivity();
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRfiProgressBar = view.findViewById(R.id.progressContainer);
        mRfiReloadFAB = view.findViewById(R.id.reload_button);

        if (mRfiProgressBar == null || mRfiReloadFAB == null) {
            throw new RuntimeException("Add global progress bar layout to " + this.getClass().getSimpleName() + "'s Layout file. Check Access Activity For reference.");
        }
        filterEt = view.findViewById(R.id.filter_et);

        filterEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getAdapter().getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void showReloadAndProgressBar(boolean showProgress, boolean showReload) {
        if (!isAdded())
            return;
        if (showReload) {
            mRfiProgressBar.setVisibility(View.VISIBLE);
            mRfiReloadFAB.show();
        } else if (showProgress) {
            mRfiProgressBar.setVisibility(View.VISIBLE);
            mRfiReloadFAB.hide();
        } else {
            mRfiProgressBar.setVisibility(View.GONE);
        }
    }

    public void setRfiReloadClick(final Call call, final Callback callback) {
        mRfiReloadFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReloadAndProgressBar(true, false);
                call.clone().enqueue(callback);
            }
        });
    }
}