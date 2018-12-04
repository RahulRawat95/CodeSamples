package distributor.w2a.com.distributor.repository;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import distributor.w2a.com.distributor.network.ApiClient;
import distributor.w2a.com.distributor.network.ApiInterface;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JsonFileWriter {

    private static final String JSON_FILE_NAME = "saveForOffline";
    private static final String JSON_OBJECT_TO_SEND = "dataForSending";
    private static final String FILES_TO_SEND = "filesToSend";
    private static final String UNIQUE_NAME = "storageName";
    private static final String PART_NAME = "partName";

    public static void syncWithServer(Context context) {
        ApiInterface apiInterface = ApiClient.getApiInterface();
        JsonObject jsonObject = getExistingJson(context);
        if (jsonObject == null)
            return;
        Set<String> keySet = jsonObject.keySet();
        if (keySet.size() == 0)
            return;
        ContextWrapper wrapper = new ContextWrapper(context.getApplicationContext());
        File dir = wrapper.getDir("filesToStore", Context.MODE_PRIVATE);
        Call call = null;
        for (String s : keySet) {
            JsonArray jsonArray = jsonObject.getAsJsonArray(s);
            for (int i = 0; i < jsonArray.size(); ) {
                JsonObject jsonElement = jsonArray.get(i).getAsJsonObject();

                try {
                    File[] files = null;
                    String[] strings = null;
                    JsonArray jsonElements = jsonElement.getAsJsonArray(FILES_TO_SEND);
                    if (jsonElements.size() > 0) {
                        files = new File[jsonElements.size()];
                        strings = new String[jsonElements.size()];
                        JsonObject element;
                        for (int j = 0; j < jsonElements.size(); j++) {
                            element = jsonElements.get(j).getAsJsonObject();
                            strings[j] = element.get(PART_NAME).getAsString();
                            files[j] = new File(dir.getAbsolutePath() + File.separator + element.get(UNIQUE_NAME).getAsString());
                        }
                        call = apiInterface.dynamic(s, jsonObject.getAsJsonObject(JSON_OBJECT_TO_SEND)
                                , Utility.getMultiPart(strings, files));
                    } else {
                        call = apiInterface.dynamic(s, jsonObject.getAsJsonObject(JSON_OBJECT_TO_SEND));
                    }
                    Response response = call.execute();
                    if (!response.isSuccessful()) {
                        throw new Exception("");
                    }
                    jsonArray.remove(jsonElement);
                    if (files != null && files.length > 0) {
                        for (File file : files) {
                            file.delete();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    i++;
                }
            }
            if (jsonArray.size() == 0) {
                jsonObject.remove(s);
            }
        }
        writeJsonObject(context, jsonObject);
    }

    public static <X> void callApiOrSave(final Context context, Call<X> call, final JsonElement jsonElement, final List<MultipartBody.Part> parts, final Callback<X> callback) {
        call.enqueue(new Callback<X>() {
            @Override
            public void onResponse(Call<X> call, Response<X> response) {
                if (!response.isSuccessful())
                    writeJsonWhenUnsuccessfull(context, call, jsonElement, parts);
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<X> call, Throwable t) {
                writeJsonWhenUnsuccessfull(context, call, jsonElement, parts);
                callback.onFailure(call, t);
            }
        });
    }

    public static <X> Response<X> callApiOrSave(Context context, Call<X> call, JsonElement jsonElement, List<MultipartBody.Part> parts) {
        try {
            Response<X> response = call.execute();
            if (!response.isSuccessful()) {
                throw new Exception("");
            }
            return response;
        } catch (Exception e) {
            writeJsonWhenUnsuccessfull(context, call, jsonElement, parts);
        }
        return null;
    }

    private static void writeJsonWhenUnsuccessfull(Context context, Call call, JsonElement jsonElement, List<MultipartBody.Part> parts) {
        JsonObject existingObject = getExistingJson(context);
        if (existingObject == null)
            existingObject = new JsonObject();
        JsonArray jsonArray = existingObject.getAsJsonArray(call.request().url().toString());
        if (jsonArray == null) {
            jsonArray = new JsonArray();
            existingObject.add(call.request().url().toString(), jsonArray);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(JSON_OBJECT_TO_SEND, jsonElement);

        String uuid = null;
        JsonArray filesToSave = null;
        if (parts != null) {
            JsonObject object = null;
            filesToSave = new JsonArray();
            for (MultipartBody.Part part : parts) {
                String[] headers = part.headers().get("Content-Disposition").split(";");
                headers[1] = headers[1].substring(headers[1].indexOf("\"") + 1, headers[1].length() - 1);
                headers[2] = headers[2].substring(headers[2].indexOf("\"") + 1, headers[2].length() - 1);
                object = new JsonObject();
                uuid = UUID.randomUUID().toString() + headers[2].substring(headers[2].lastIndexOf("."));
                object.addProperty(UNIQUE_NAME, uuid);
                object.addProperty(PART_NAME, headers[1]);
                filesToSave.add(object);
                copyFile(context, headers[2], uuid);
            }
        }
        jsonObject.add(FILES_TO_SEND, filesToSave);
        jsonArray.add(jsonObject);
        writeJsonObject(context, existingObject);
    }

    private static void copyFile(Context context, String inputPath, String outputName) {
        ContextWrapper wrapper = new ContextWrapper(context.getApplicationContext());
        InputStream in = null;
        OutputStream out = null;
        try {
            //create output directory if it doesn't exist
            File dir = wrapper.getDir("filesToStore", Context.MODE_PRIVATE);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            in = new FileInputStream(inputPath);
            out = new FileOutputStream(dir.getAbsolutePath() + File.separator + outputName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        } finally {
            try {
                in.close();
                in = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void writeJsonObject(Context context, JsonObject existingObject) {
        Writer output = null;
        ContextWrapper wrapper = new ContextWrapper(context.getApplicationContext());
        File file = wrapper.getFileStreamPath(JSON_FILE_NAME);
        try {
            output = new BufferedWriter(new FileWriter(file, false));
            output.write(existingObject.toString());
            output.close();
        } catch (Exception ex) {
            Log.d("exception", "writeJsonObject: ex ", ex);
        } finally {
            try {
                output.close();
            } catch (Exception exception) {
                Log.d("exception", "writeJsonObject: exception ", exception);
            }
        }
    }

    private static JsonObject getExistingJson(Context context) {
        ContextWrapper wrapper = new ContextWrapper(context.getApplicationContext());
        File file = wrapper.getFileStreamPath(JSON_FILE_NAME);
        StringBuilder text = new StringBuilder();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            JsonParser parser = new JsonParser();
            return parser.parse(text.toString()).getAsJsonObject();
        } catch (Exception e) {
            Log.d("exception", "getExistingFile: e ", e);
        } finally {
            try {
                br.close();
            } catch (Exception e) {
            }
        }
        return null;
    }
}