package id.kpunikom.kinestattendance.api;

import id.kpunikom.kinestattendance.utils.UnsafeOkHttpClient;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "https://absensi.kakatu.co/";
    //public static final String BASE_URL = "http://192.168.1.32/kakatu/";
    public static Retrofit retrofit = null;
    public static OkHttpClient client;

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static Retrofit getApiClient(){
        client = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
