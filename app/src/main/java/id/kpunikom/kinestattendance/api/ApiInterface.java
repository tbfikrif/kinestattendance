package id.kpunikom.kinestattendance.api;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface

ApiInterface {

    @POST("getListSudahAbsen.php")
    Call<ArrayList<id.kpunikom.absensihadir.model.Members>> getListSudahAbsen();

    @POST("getListBelumAbsen.php")
    Call<ArrayList<id.kpunikom.absensihadir.model.Members>> getListBelumAbsen();

    @FormUrlEncoded
    @POST("postHadir.php")
    Call<ArrayList<id.kpunikom.absensihadir.model.Members>> postHadir(@Field("id_anggota") String id_anggota, @Field("jam_masuk") String jam_masuk);
}
