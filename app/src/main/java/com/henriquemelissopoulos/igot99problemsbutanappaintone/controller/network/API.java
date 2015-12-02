package com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.BuildConfig;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.Config;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.model.Taxi;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by h on 01/12/15.
 */
public class API {

    private static Retrofit REST_ADAPTER;

    public API() {

        if (REST_ADAPTER == null) {

            Gson gson = new GsonBuilder().create();

            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(0, TimeUnit.MILLISECONDS);
            client.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Accept-Language", "pt-BR")
                            .method(original.method(), original.body())
                            .build();

                    Response response = chain.proceed(request);

                    if (BuildConfig.DEBUG) {
                        String bodyString = response.body().string();
                        Log.d("api", String.format("Sending request %s with headers %s ", original.url(), original.headers()));
                        Log.d("api", String.format("Got response HTTP %s %s \n\n with body %s \n\n with headers %s ", response.code(), response.message(), bodyString, response.headers()));
                        response = response.newBuilder().body(ResponseBody.create(response.body().contentType(), bodyString)).build();
                    }

                    return response;
                }
            });


            REST_ADAPTER = new Retrofit.Builder()
                    .baseUrl(Config.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
    }

    public EndpointInterface service() {
        return REST_ADAPTER.create(EndpointInterface.class);
    }


    public interface EndpointInterface {

        //List of taxis
        @GET("lastLocations")
        Observable<ArrayList<Taxi>> taxis(
                @Query("sw") String sw,
                @Query("ne") String ne);
    }
}
