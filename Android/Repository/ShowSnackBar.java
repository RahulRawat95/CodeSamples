package distributor.w2a.com.distributor.repository;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

public class ShowSnackBar {
    private static Snackbar sSnackBar;
    private static Callback callback;

    public static void showSnackBar(Activity activity, String text, int length) {
        try {
            showSnackBar(activity.findViewById(android.R.id.content), text, length);
        } catch (Exception e) {
        }
    }

    public interface Callback{
        void callback();
    }

    public static void showSnackBar(Activity activity, String text, int length, final Callback callback) {

        try {
            if (sSnackBar != null) {
                sSnackBar.dismiss();
            }
            sSnackBar = Snackbar.make(activity.findViewById(android.R.id.content), text, length);

            View snackBarView = sSnackBar.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            snackBarView.setLayoutParams(params);

            sSnackBar.setActionTextColor(Color.WHITE);
            sSnackBar.setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.callback();
                }
            });
            sSnackBar.show();
        } catch (Exception e) {
        }
    }

    public static void showSnackBar(View view, String text, int length) {
        try {
            if (sSnackBar != null) {
                sSnackBar.dismiss();
            }
            sSnackBar = Snackbar.make(view, text, length);

            View snackBarView = sSnackBar.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            snackBarView.setLayoutParams(params);

            sSnackBar.setActionTextColor(Color.WHITE);
            sSnackBar.setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sSnackBar.dismiss();
                }
            });
            sSnackBar.show();
        } catch (Exception e) {
        }
    }

    public static void showSnackBar(Activity activity, int id, int length) {
        try {
            showSnackBar(activity.findViewById(android.R.id.content), id, length);
        } catch (Exception e) {
        }
    }

    public static void showSnackBar(View view, int id, int length) {
        try {
            if (sSnackBar != null) {
                sSnackBar.dismiss();
            }
            sSnackBar = Snackbar.make(view, id, length);

            View snackBarView = sSnackBar.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            snackBarView.setLayoutParams(params);

            sSnackBar.setActionTextColor(Color.WHITE);
            sSnackBar.setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sSnackBar.dismiss();
                }
            });
            sSnackBar.show();
        } catch (Exception e) {
        }
    }



    public static void dismissSnackBar() {
        try {
            if (sSnackBar != null)
                sSnackBar.dismiss();
            sSnackBar = null;
        } catch (Exception e) {
        }
    }
}
