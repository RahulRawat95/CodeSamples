package distributor.w2a.com.distributor.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

import distributor.w2a.com.distributor.model.AuthenticationToken;
import distributor.w2a.com.distributor.model.TBL_M_BSO;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {
    @Multipart
    @POST
    Call<JsonElement> dynamic(@Url String url, @Part("data") JsonElement jsonObject);

    @Multipart
    @POST
    Call<JsonElement> dynamic(@Url String url, @Part("data") JsonElement jsonObject, @Part List<MultipartBody.Part> parts);

    @Multipart
    @POST
    Call<JsonArray> getApiSearch(@Url String url, @Part("data") JsonObject jsonObject);

    @GET("/getTokenAndDb")
    Call<AuthenticationToken> getAuthToken(@Query("id") String id);

    @GET("/getFileByPath")
    Call<ResponseBody> getFileByPath(@Query("PATH") String path);

    @GET("/getOneTimeToken/")
    Call<JsonObject> getOneTimeToken();

    @Multipart
    @POST("/router/apiWithJson")
    Call<JsonObject> apiWithJson(@Part("data") JsonObject jsonObject);

    @Multipart
    @POST("/router/apiWithJsonAndFiles")
    Call<JsonObject> apiWithJsonAndFiles(@Part("data") JsonObject jsonObject, @Part List<MultipartBody.Part> parts);


}