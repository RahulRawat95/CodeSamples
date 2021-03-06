package distributor.w2a.com.distributor.repository;

import android.content.Context;
import android.widget.Toast;

public class ShowToast {
    private static Toast sToast;

    public static void showToast(Context context, String text) {
        if (sToast != null) {
            try {
                sToast.cancel();
            } catch (Exception e) {
                sToast = null;
            }
        }
        try {
            sToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            sToast.show();
        } catch (Exception e) {
            sToast = null;
        }
    }

    public static void showToast(Context context, int id) {
        if (sToast != null) {
            try {
                sToast.cancel();
            } catch (Exception e) {
                sToast = null;
            }
        }
        try {
            sToast = Toast.makeText(context, id, Toast.LENGTH_SHORT);
            sToast.show();
        } catch (Exception e) {
            sToast = null;
        }
    }
}
