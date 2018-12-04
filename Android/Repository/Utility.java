package distributor.w2a.com.distributor.repository;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import distributor.w2a.com.distributor.BuildConfig;
import distributor.w2a.com.distributor.R;
import distributor.w2a.com.distributor.globalLibrary.Validations;
import distributor.w2a.com.distributor.globalLibrary.globalDialogs.FailMessageDialogFragment;
import distributor.w2a.com.distributor.globalLibrary.globalDialogs.MessageDialogFragment;
import distributor.w2a.com.distributor.model.FileAddModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class Utility {

    public static final String dateFormatToShow = "dd-MM-yyyy";
    public static AlertDialog datePickerDialog;
    private static AlertDialog timePickerDialog;

    public static void showDatePickerDialog(final Context mContext, final EditText showDateContainer, final Date minDate, final Date maxDate, Date dateToSet) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //creating layout inflator for inflating the custom datepicer layout
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        //inflating the layout
        View datePickerLayout = layoutInflater.inflate(R.layout.dialog_date_picker, null);
        //finding the date picker view
        DatePicker scroll_date_picker = datePickerLayout.findViewById(R.id.scroller_date_picker);

        //checking for the min date value
        if (minDate != null) {
            scroll_date_picker.setMinDate(minDate.getTime());
        }
        //checking fot max date value
        if (maxDate != null) {
            scroll_date_picker.setMaxDate(maxDate.getTime());
        }

        //finding the textView to show selected date on date picker layout
        final TextView selected_date_tv = datePickerLayout.findViewById(R.id.show_selected_date);

        //finding cancel and done button
        TextView cancel = datePickerLayout.findViewById(R.id.cancel);
        TextView done = datePickerLayout.findViewById(R.id.done);

        //creting calendar variable to set the date on datePicker View
        final Calendar calendar = Calendar.getInstance();


        //setting current date to the calendar
        if (dateToSet != null) {
            calendar.setTime(dateToSet);
        }

        //creating date format to show current day of week
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        //date format to show current date
        SimpleDateFormat postFormater = new SimpleDateFormat("MMM dd, yyyy");

        //setting date to the textView on dialog layout
        selected_date_tv.setText(sdf.format(calendar.getTime()) + ", " + postFormater.format(calendar.getTime()));

        //initializing the datepicker and calling onDateChangeListener function on it
        scroll_date_picker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE");
            SimpleDateFormat postFormater = new SimpleDateFormat("MMM dd, yyyy");

            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                //setting changed date to the calendar
                calendar.set(i, i1, i2);
                //setting date to the textView on dialog layout
                selected_date_tv.setText(sdf.format(calendar.getTime()) + ", " + postFormater.format(calendar.getTime()));
            }
        });


        //setting on clickListener to the done tetView
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setting date to the view
                showDateContainer.setText(toDateToShow(calendar));
                datePickerDialog.dismiss();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.dismiss();
            }
        });

        //setting view to the builder
        builder.setView(datePickerLayout);

        //creating builder for the alertDialog
        datePickerDialog = builder.create();
        //showing alertDilaog
        datePickerDialog.show();

    }

    public static String toDateToShow(Calendar date) {
        return new SimpleDateFormat(dateFormatToShow).format(date.getTime());
    }

    public static List<MultipartBody.Part> getMultiPart(String[] names, File... files) {
        if (files == null || names == null)
            return null;
        return getMultiPart(Arrays.asList(names), Arrays.asList(files));
    }

    public static List<MultipartBody.Part> getMultiPart(List<String> names, List<File> files) {
        if (files == null || names == null)
            return null;
        if (names.size() != files.size())
            return null;
        List<MultipartBody.Part> list = new ArrayList<>();
        RequestBody requestFile;
        MultipartBody.Part body;

        for (int i = 0; i < files.size(); i++) {
            requestFile = RequestBody.create(MediaType.parse(getMimeType(files.get(i))), files.get(i));
            body = MultipartBody.Part.createFormData(names.get(i), files.get(i).getAbsolutePath(), requestFile);
            list.add(body);
        }
        return list;
    }

    public static String getMimeType(File file) {
        String url = file.getPath();
        String mimeType;
        if (url.contains(".doc") || url.contains(".docx")) {
            // Word document
            mimeType = "application/msword";
        } else if (url.contains(".pdf")) {
            // PDF file
            mimeType = "application/pdf";
        } else if (url.contains(".ppt") || url.contains(".pptx")) {
            // Powerpoint file
            mimeType = "application/vnd.ms-powerpoint";
        } else if (url.contains(".xls") || url.contains(".xlsx")) {
            // Excel file
            mimeType = "application/vnd.ms-excel";
        } else if (url.contains(".zip") || url.contains(".rar")) {
            // WAV audio file
            mimeType = "application/x-wav";
        } else if (url.contains(".rtf")) {
            // RTF file
            mimeType = "application/rtf";
        } else if (url.contains(".wav") || url.contains(".mp3")) {
            // WAV audio file
            mimeType = "audio/x-wav";
        } else if (url.contains(".gif")) {
            // GIF file
            mimeType = "image/gif";
        } else if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {
            // JPG file
            mimeType = "image/jpeg";
        } else if (url.contains(".txt")) {
            // Text file
            mimeType = "text/plain";
        } else if (url.contains(".3gp") || url.contains(".mpg") ||
                url.contains(".mpeg") || url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi")
                || url.contains(".mkv")) {
            // Video files
            mimeType = "video/*";
        } else {
            mimeType = "*/*";
        }
        return mimeType;
    }

    public static JsonObject createJsonObject(String key[], Object... objects) {
        if (key.length != objects.length)
            return null;
        JsonObject jsonObject = new JsonObject();
        String dbName = Constants.getToken() == null ? "w2aOxySalesDB"
                : Constants.getToken().getDbName() == null ? "w2aOxySalesDB" : Constants.getToken().getDbName();
        jsonObject.addProperty("dbName", dbName);
        //data object that contains value of different fields
        JsonObject internalDataObject;
        internalDataObject = new JsonObject();
        for (int i = 0; i < key.length; i++) {
            if (objects[i] instanceof Number)
                internalDataObject.addProperty(key[i], (Number) objects[i]);
            else if (objects[i] instanceof Boolean)
                internalDataObject.addProperty(key[i], (Boolean) objects[i]);
            else if (objects[i] instanceof Character)
                internalDataObject.addProperty(key[i], (Character) objects[i]);
            else if (objects[i] instanceof String)
                internalDataObject.addProperty(key[i], (String) objects[i]);
            else if (objects[i] instanceof JsonElement)
                internalDataObject.add(key[i], (JsonElement) objects[i]);
        }
        jsonObject.add("JsonData", internalDataObject);
        /*JsonObject returnObject = new JsonObject();
        returnObject.add("data", jsonObject);*/
        return jsonObject;
    }

    public static void addExtraEntries(JsonObject jsonObject, Map<String, Object> map) {
        if (map == null || map.size() <= 0)
            return;
        JsonObject extraEntries = jsonObject.getAsJsonObject("JsonData");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Number)
                extraEntries.addProperty(entry.getKey(), (Number) entry.getValue());
            else if (entry.getValue() instanceof Boolean)
                extraEntries.addProperty(entry.getKey(), (Boolean) entry.getValue());
            else if (entry.getValue() instanceof Character)
                extraEntries.addProperty(entry.getKey(), (Character) entry.getValue());
            else if (entry.getValue() instanceof String)
                extraEntries.addProperty(entry.getKey(), (String) entry.getValue());
            else if (entry.getValue() instanceof JsonElement)
                extraEntries.add(entry.getKey(), (JsonElement) entry.getValue());
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public static List<View> childCountForViews(Context context, LinearLayout container, boolean addTextWatcher, String... fieldHintsToIgnoreValidations) {

        List<View> validateViewList = new ArrayList<>();

        //getting all the child views from the parent container and adding it the list for validation according to its instance
        for (int i = 0; i < container.getChildCount(); i++) {
            View currentView = container.getChildAt(i);

            //if view is TextInputLayout then adding addTextChangeListener on its editText
            if (currentView instanceof TextInputLayout) {
                EditText currentViewEditText = ((TextInputLayout) currentView).getEditText();

                String tilHint = ((TextInputLayout) currentView).getHint().toString();
                boolean fieldToIgnoreFound = false;
                for (int j = 0; j < fieldHintsToIgnoreValidations.length; j++) {
                    if (tilHint.equalsIgnoreCase(fieldHintsToIgnoreValidations[j]))
                        fieldToIgnoreFound = true;
                }
                if (fieldToIgnoreFound)
                    continue;

                if (addTextWatcher)
                    currentViewEditText.addTextChangedListener(new MyTextWatcher(currentViewEditText, (TextInputLayout) currentView, context));
            }
            //if view is EditText then adding addTextChangeListener on it
            if (currentView instanceof EditText) {
                EditText currentViewEditText = (EditText) currentView;
                currentViewEditText.addTextChangedListener(new MyTextWatcher(currentViewEditText, null, context));
            }


            validateViewList.add(currentView);

        }


        return validateViewList;

    }


    static boolean isNetwork = false;

    public interface Callback {
        void valueGet(boolean check, String message);
    }

    public static void hasActiveInternetConnection(final Context context, final Callback callback) {
        //function to check the device has active internet connection or not
        if (isNetworkAvailable(context)) {
            //checking the internet connection is working or not

            //calling an url to check for working internet
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                }

                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                        urlc.setRequestProperty("User-Agent", "Test");
                        urlc.setRequestProperty("Connection", "close");
                        urlc.setConnectTimeout(1500);
                        urlc.connect();
                        if (urlc.getResponseCode() == 200)
                            isNetwork = true;
                        else
                            isNetwork = false;
                        return null;
                    } catch (IOException e) {
                        isNetwork = false;
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    callback.valueGet(isNetwork, "Check Internet Connection");
                }
            }.execute();

        } else {

            callback.valueGet(false, "No network available!");

        }
    }


    public static Drawable getSymbol(Context context, String symbol, float textSize, int color) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(color);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(symbol) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(symbol, 0, baseline, paint);
        return new BitmapDrawable(context.getResources(), image);
    }

    public static boolean isPermissionGranted(String permission, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        else
            return true;
    }

    public static void requestPermission(Activity callingActivity, int request, String... permissions) {
        ActivityCompat.requestPermissions(callingActivity, permissions, request);
    }


    public static String getPath(final Context context, final Uri uri) throws URISyntaxException {
        // DocumentProvider
        Log.e("Alucard", uri.getAuthority());
        Log.e("Alucard", uri.getScheme());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if ("com.android.externalstorage.documents".equals(
                        uri.getAuthority())) {// ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                        return "/stroage/" + type + "/" + split[1];
                    }
                } else if ("com.android.providers.downloads.documents".equals(
                        uri.getAuthority())) {// DownloadsProvider
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                } else if ("com.android.providers.media.documents".equals(
                        uri.getAuthority())) {// MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    contentUri = MediaStore.Files.getContentUri("external");
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                } else if ("content".equalsIgnoreCase(uri.getScheme())) {//MediaStore
                    return getDataColumn(context, uri, null, null);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {//MediaStore
                return getDataColumn(context, uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
                return uri.getPath();
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {//MediaStore
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }


    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String[] projection = {
                MediaStore.Files.FileColumns.DATA
        };
        try {
            cursor = context.getContentResolver().query(
                    uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Log.d("Alucard", "getDataColumn: " + cursor.getColumnName(i));
                }
                final int cindex = cursor.getColumnIndexOrThrow(projection[0]);
                return cursor.getString(cindex);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static Uri getUriType(Context context, File file) {
        return FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider",
                file);
    }


    private static MessageDialogFragment messageDialogFragment;
    private static FailMessageDialogFragment failMessageDialogFragment;
    private static ConfirmationMessageCallback confirmationMessageCallback;


    public interface ConfirmationMessageCallback {
        void callback();
    }

    public static void dialogMessageShow(String messageToShow, boolean showConfirmationButton, Context mContext,
                                         final ConfirmationMessageCallback confirmationMessageCallback) {

        if (messageDialogFragment != null && messageDialogFragment.isVisible())
            messageDialogFragment.dismiss();

        messageDialogFragment = MessageDialogFragment.newInstance(messageToShow, showConfirmationButton, new MessageDialogFragment.ConfirmationMessageCallback() {
            @Override
            public void callback() {
                confirmationMessageCallback.callback();
            }
        });
        if (showConfirmationButton) {
            messageDialogFragment.setCancelable(false);
        }
        messageDialogFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "Message");
    }

    //Failed Toast Making
    public static void failDialogMessageShow(String messageToShow, Context mContext) {

        if (failMessageDialogFragment != null && failMessageDialogFragment.isVisible())
            failMessageDialogFragment.dismiss();

        failMessageDialogFragment.newInstance(messageToShow)
                .show(((FragmentActivity) mContext).getSupportFragmentManager(), "Error Message");
    }

    //check for already selected files
    public static Boolean checkForFile(List<String> selectedFilesNames, String fileName) {
        for (int i = 0; i < selectedFilesNames.size(); i++) {
            if (selectedFilesNames.get(i).equalsIgnoreCase(fileName)) {
                return true;
            }
        }
        return false;
    }


    public static class MyTextWatcher implements TextWatcher {

        private View view;
        private TextInputLayout textInputLayout;
        private Context context;

        public MyTextWatcher(View view, TextInputLayout textInputLayout, Context context) {
            this.view = view;
            this.textInputLayout = textInputLayout;
            this.context = context;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void afterTextChanged(Editable editable) {
            Validations.singleViewEmptyCheck(view, textInputLayout, context);
        }
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    public static String createRandomPassword() {
        String pass;
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#&".toCharArray();
        StringBuilder sb = new StringBuilder(6);
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        pass = sb.toString();
        return pass;
    }

    public static <X> List<X> getList(JsonArray jsonArray) {
        Type type = new TypeToken<List<X>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    public static <X> X getObject(JsonObject jsonObject) {
        Type type = new TypeToken<X>() {
        }.getType();
        return new Gson().fromJson(jsonObject, type);
    }

    public static boolean isEmpty(View... views) {
        for (View view : views) {
            if (view instanceof TextView){
                if (TextUtils.isEmpty(((TextView) view).getText())) {
                    ((TextView) view).setError("Required");
                    return true;
                } else {
                    ((TextView) view).setError(null);
                }
            }else if (view instanceof TextInputLayout){
                if (TextUtils.isEmpty(((TextInputLayout) view).getEditText().getText())) {
                    ((TextInputLayout) view).setError("Required");
                    return true;
                } else {
                    ((TextInputLayout) view).setError(null);
                }
            }

        }
        return false;
    }

    public static String getFormattedString(TextView stringToFormat) {
        return stringToFormat.getText().toString().trim().replaceAll("\\s+", " ");
    }

    public static File writeResponseBodyToDisk(ResponseBody body, String name) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "distributor");
            if (!file.exists())
                file.mkdirs();
            file = new File(Environment.getExternalStorageDirectory() + File.separator + "distributor" + File.separator + name);
            if (!file.exists())
                file.createNewFile();
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                return file;
            } catch (Exception e) {
                return null;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static void addFilesToDelete(JsonObject jsonObject, JsonArray jsonArray) {
        if (jsonArray != null || jsonArray.size() > 0) {
            jsonObject.add("FILES_TO_DELETE", jsonArray);
        }
    }

    public static void removeKeys(JsonObject jsonObject, String... keys) {
        removeKeys(jsonObject, Arrays.asList(keys));
    }

    public static void removeKeys(JsonObject jsonObject, List<String> keys) {
        JsonObject jsonData = jsonObject.getAsJsonObject("JsonData");
        for (String key : keys) {
            jsonData.remove(key);
        }
    }

    public static void getFilesToDelete(JsonArray jsonArray, String... fileNames) {
        if (jsonArray == null)
            jsonArray = new JsonArray();
        for (String name : fileNames) {
            jsonArray.add(name);
        }
    }


    public static long getDateDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();


        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        return elapsedDays;
    }

    public static FileAddModel getFileModelObject(String filePath, String fileName, boolean isLocal) {
        FileAddModel fileAddModel = new FileAddModel();
        fileAddModel.setFileName(fileName);
        fileAddModel.setFilePath(filePath);
        fileAddModel.setLocal(isLocal);
        return fileAddModel;
    }
    public static String getServerFilePathToUpdate(LinearLayout fileContainer) {
        View view;
        FileAddModel fileAddModel;
        String path;
        String serverPath = null;

        for (int i = 0; i < fileContainer.getChildCount(); i++) {
            view = fileContainer.getChildAt(i);
            fileAddModel = (FileAddModel) view.getTag();
            if (!fileAddModel.isLocal()) {
                if (serverPath == null) {
                    serverPath = fileAddModel.getFilePath();
                } else {
                    path = fileAddModel.getFilePath();
                    serverPath += "#" + path.substring(path.lastIndexOf("/"));
                }
            }
        }

        return serverPath;
    }
}