package com.lj.library.fragment.http;

import android.view.LayoutInflater;
import android.view.View;

import com.lj.library.R;
import com.lj.library.bean.UserInfo;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.http.okhttp.OkHttpManager;
import com.lj.library.http.okhttp.SimpleCallback;
import com.lj.library.http.retrofit.RetrofitManager;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by liujie_gyh on 16/3/11.
 */
public class HttpDemoFragment extends BaseFragment {

    private final OkHttpClient HTTP_CLIENT = OkHttpManager.INSTANCE.getClient();

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.http_demo_fragment, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.okhttp_get_btn)
    public void okHttpGet() {
        //创建一个Request
        final Request request = new Request.Builder().url("https://github.com/hongyangAndroid").build();
        HTTP_CLIENT.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Logger.e("okHttpGet   onFailure");
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Logger.i("okHttpGet   onResponse");
//                response.body().string();
//                response.body().bytes();
//                response.body().byteStream();
            }
        });
    }

    @OnClick(R.id.okhttp_post_btn)
    public void okHttpPost() {
        // post提交json字符串
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "{\"name\": \"LJ\"}");
        //post提交键值对
//        RequestBody body = new FormEncodingBuilder()
//                .add("platform", "android")
//                .add("name", "bug")
//                .add("subject", "XXXXXXXXXXXXXXX")
//                .build();

        Request request = new Request.Builder()
                .url("https://github.com").post(body).build();
        HTTP_CLIENT.newCall(request).enqueue(new SimpleCallback());
    }

    /**
     * 下载，若需要下载进度，可以查看
     */
    @OnClick(R.id.okhttp_download_btn)
    public void okHttpDownload() {
        final Request request = new Request.Builder()
                .url("https://github.com")
                .build();
        HTTP_CLIENT.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                response.body().byteStream();
            }
        });
    }

    /**
     * 上传文件，若需要下载进度，可以查看
     */
    @OnClick(R.id.okhttp_upload_btn)
    public void okHttpUpload() {
        File file = new File("README.md");
        //构造上传请求，类似web表单
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("hello", "android")
                .addFormDataPart("photo", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(requestBody)
                .build();
        HTTP_CLIENT.newCall(request).enqueue(new SimpleCallback());
    }

    @OnClick(R.id.retrofit_get_btn)
    public void retrofitGet() {
        GitHubService gitHubService = RetrofitManager.create(GitHubService.class);
        Call<List<UserInfo>> call = gitHubService.groupList(1);
        call.enqueue(new com.lj.library.http.retrofit.SimpleCallback<List<UserInfo>>());
    }

    @OnClick(R.id.retrofit_post_btn)
    public void retrofitPost() {
        GitHubService gitHubService = RetrofitManager.create(GitHubService.class);
        Call<UserInfo> call = gitHubService.createUser(new UserInfo());
        call.enqueue(new com.lj.library.http.retrofit.SimpleCallback<UserInfo>());
    }

    @OnClick(R.id.retrofit_download_btn)
    public void retrofitDownload() {
        GitHubService gitHubService = RetrofitManager.create(GitHubService.class);
        Call<String> call = gitHubService.downloadFile("README.txt");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                response.body().getBytes();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.retrofit_upload_btn)
    public void retrofitUpload() {
        File file = new File("README.md");
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("hello", "android")
                .addFormDataPart("photo", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        GitHubService gitHubService = RetrofitManager.create(GitHubService.class);
        Call<UserInfo> call = gitHubService.updateUser(requestBody, requestBody);
        call.enqueue(new com.lj.library.http.retrofit.SimpleCallback<UserInfo>());
    }

    public interface GitHubService {
        @GET("users/{user}/repos")
        Call<List<UserInfo>> listRepos(@Path("user") String user);

        @GET("group/{id}/users")
        Call<List<UserInfo>> groupList(@Path("id") int groupId);

        @GET("group/{id}/users")
        Call<List<UserInfo>> groupList(@Path("id") int groupId, @Query("sort") String sort);

        @GET("group/{id}/users")
        Call<List<UserInfo>> groupList(@Path("id") int groupId, @QueryMap Map<String, String> options);

        @GET("group/{filename}")
        Call<String> downloadFile(@Path("filename") String filename);


        @POST("users/new")
        Call<UserInfo> createUser(@Body UserInfo user);

        @FormUrlEncoded
        @POST("user/edit")
        Call<UserInfo> updateUser(@Field("first_name") String first, @Field("last_name") String last);

        @Multipart
        @PUT("user/photo")
        Call<UserInfo> updateUser(@Part("photo") RequestBody photo, @Part("description") RequestBody description);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
