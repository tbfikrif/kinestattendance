package id.kpunikom.kinestattendance.member;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Members {
    @SerializedName("id_anggota")
    @Expose
    private String idAnggota;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("jam_masuk")
    @Expose
    private String jamMasuk;
    @SerializedName("status_id")
    @Expose
    private String statusId;
    @SerializedName("keterangan")
    @Expose
    private Object keterangan;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("foto")
    @Expose
    private String foto;


    public Members(String idAnggota, String nama, String jamMasuk, String statusId, Object keterangan, String email, String foto) {
        this.idAnggota = idAnggota;
        this.nama = nama;
        this.jamMasuk = jamMasuk;
        this.statusId = statusId;
        this.keterangan = keterangan;
        this.email = email;
        this.foto = foto;
    }

    public String getIdAnggota() {
        return idAnggota;
    }

    public void setIdAnggota(String idAnggota) {
        this.idAnggota = idAnggota;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJamMasuk() {
        return jamMasuk;
    }

    public void setJamMasuk(String jamMasuk) {
        this.jamMasuk = jamMasuk;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public Object getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(Object keterangan) {
        this.keterangan = keterangan;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}