package distributor.w2a.com.distributor.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.IOException;

import distributor.w2a.com.distributor.R;
import distributor.w2a.com.distributor.activity.AbstractDashboardActivity;
import retrofit2.Call;
import retrofit2.Callback;

public abstract class AbstractFragment extends Fragment {
    protected RelativeLayout mRfiProgressBar;
    protected FloatingActionButton mRfiReloadFAB;

    protected abstract String getTitle();

    public AbstractDashboardActivity getAbstractDashBoardActivity() {
        return (AbstractDashboardActivity) getActivity();
    }

    public void addFragmentToBackStack(Fragment fragment) {
        getAbstractDashBoardActivity().addFragmentToBackStack(fragment);
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRfiProgressBar = view.findViewById(R.id.progressContainer);
        mRfiReloadFAB = view.findViewById(R.id.reload_button);

        if (mRfiProgressBar == null || mRfiReloadFAB == null) {
            throw new RuntimeException("Add global progress bar layout to " + this.getClass().getSimpleName() + "'s Layout file. Check Access Activity For reference.");
        }

        try {
            getAbstractDashBoardActivity().setActionBarTitle(getTitle());
        } catch (Exception e) {
        }
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

    @Override
    public void onDestroyView() {
        try {
            getAbstractDashBoardActivity().setActionBarTitle("");
        } catch (Exception e) {
        }
        super.onDestroyView();
    }

    protected void popBackStack() {
        try {
            getFragmentManager().popBackStack();
        } catch (Exception e) {
        }
    }

    protected String format(double value, int decimalAllowed) {
        return String.format("%." + decimalAllowed + "f", value);
    }

    public void onBackPressed() {
        getAbstractDashBoardActivity().forceBackPress();
    }

    public void showCancelConfirmation() {
        try {
            new AlertDialog.Builder(getAbstractDashBoardActivity()).setMessage("Are you Sure you want to Cancel?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getFragmentManager().popBackStack();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } catch (Exception e) {
        }
    }
}