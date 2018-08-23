package id.kpunikom.kinestattendance.api;

import java.util.ArrayList;

import id.kpunikom.kinestattendance.member.Members;
import id.kpunikom.kinestattendance.member.MembersAmount;
import id.kpunikom.kinestattendance.member.MembersCheck;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface

ApiInterface {

    @POST("api/getListSudahAbsen.php")
    Call<ArrayList<Members>> getListSudahAbsen();

    @POST("api/getListBelumAbsen.php")
    Call<ArrayList<Members>> getListBelumAbsen();

    @FormUrlEncoded
    @POST("api/postHadir.php")
    Call<ArrayList<Members>> postHadir(@Field("id_anggota") String id_anggota, @Field("jam_masuk") String jam_masuk);

    @POST("api/getJumlahAnggota.php")
    Call<MembersAmount> getJumlahAnggota();

    @FormUrlEncoded
    @POST("api/checkSudahAbsen.php")
    Call<MembersCheck> checkMember(@Field("id_anggota") String id_anggota);
}
