package id.kpunikom.absensihadir.model;

public class Members {
    private String nama;
    private String email;
    private String jam_masuk;
    private String foto;
    private String status_id;

    public Members(String nama, String email, String jam_masuk, String foto, String status_id) {
        this.nama = nama;
        this.email = email;
        this.jam_masuk = jam_masuk;
        this.foto = foto;
        this.status_id = status_id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJam_masuk() {
        return jam_masuk;
    }

    public void setJam_masuk(String jam_masuk) {
        this.jam_masuk = jam_masuk;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }
}