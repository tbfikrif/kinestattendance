package id.kpunikom.kinestattendance.member;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MembersAmount {

    @SerializedName("jumlah")
    @Expose
    private String jumlah;

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

}
