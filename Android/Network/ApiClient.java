package distributor.w2a.com.distributor.network;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import distributor.w2a.com.distributor.model.AuthenticationToken;
import distributor.w2a.com.distributor.repository.Constants;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static String BASE_URL = null;

    private static final int SESSION_AUTHORIZATION_TOKEN_OFFSET = 12 * 1000;

    private static ApiInterface apiInterface = null;

    private static Timer mTimer;

    public interface AuthTokenCallback {
        int RESPONSE_UNSUCCESSFUL = 1;
        int RESPONSE_FAILURE = 2;
        int RESPONSE_SUCCESSFUL = 3;

        void callback(int responseCode);
    }

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
        apiInterface = null;
    }

    public static boolean isBaseUrlEmpty() {
        return BASE_URL == null;
    }

    public static ApiInterface getApiInterface() {
        if (apiInterface == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    HttpUrl url = original.url()
                            .newBuilder()
                            .addQueryParameter("DB_NAME", Constants.getToken().getDbName())
                            .build();



                    Request request = original.newBuilder()
                            .header("authorization", Constants.getToken().getAuthenticationToken())
                            .header("one-time-token", Constants.getOneTimeToken())
                            .url(url)
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });

            OkHttpClient client = httpClient.build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl()
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiInterface = retrofit.create(ApiInterface.class);
        }
        return apiInterface;
    }

    public static void getAuthToken() {
        getApiInterface().getAuthToken(Constants.getUserId()).enqueue(new Callback<AuthenticationToken>() {
            @Override
            public void onResponse(Call<AuthenticationToken> call, retrofit2.Response<AuthenticationToken> response) {
                if (response.isSuccessful()) {
                    Constants.setToken(response.body());
                }
                setAuthTokenTimer();
            }

            @Override
            public void onFailure(Call<AuthenticationToken> call, Throwable t) {
                setAuthTokenTimer();
            }
        });
    }

    private static void setAuthTokenTimer() {
        if (mTimer != null)
            mTimer.cancel();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getAuthToken();
            }
        }, Constants.getToken().getExpiresInMillisecond() - SESSION_AUTHORIZATION_TOKEN_OFFSET);
    }
}