package com.fallwater.androidutils2017.network;

import com.fallwater.androidutils2017.bean.LoginResultBean;
import com.fallwater.androidutils2017.constant.UrlConstant;
import com.fallwater.utilslibrary.bean.BaseBean;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author fallwater on 2017/11/1.
 *         功能描述:ApiService使用示范
 */
public interface ApiService {

    @GET(UrlConstant.TEST_URL)
    Observable<BaseBean<String>> test(@Query("p1") boolean p1);

    @GET(UrlConstant.TEST_URL)
    Observable<BaseBean<String>> test(@Header("Authorization") String Authorization,
            @Query("p1") boolean p1);

    @POST(UrlConstant.TEST_URL)
    @FormUrlEncoded
    Observable<BaseBean> test1(@Header("Authorization") String Authorization,
            @Field("p1") String p1);

    @POST(UrlConstant.TEST_URL)
    @FormUrlEncoded
    Observable<BaseBean<LoginResultBean>> test(
            @Field("p1") String p1, @Field("p2") String p2
    );

    @POST(UrlConstant.TEST_URL)
    @FormUrlEncoded
    Observable<BaseBean> test(
            @FieldMap Map<String, Object> map);

    @Multipart
    @POST()
    Observable<BaseBean> test(
            @Url() String url,
            @Part() MultipartBody.Part file);

    @Multipart
    @POST()
    Observable<BaseBean> test(
            @Url() String url,
            @PartMap Map<String, RequestBody> partMap,
            @Part MultipartBody.Part... files);

    @PUT()
    Observable<BaseBean> test(
            @Url() String url,
            @Body RequestBody file
    );
}