package model;

/**
 * Usaha
 */
public class Usaha {

    private final String idUsaha;
    private final String namaUsaha;
    private final String sektor;
    private final String alamat;
    // class
    private final Nasabah pemilik;

    public Usaha(String idUsaha, String namaUsaha, String sektor, String alamat, Nasabah pemilik) {
        {
            if (pemilik == null) {
                throw new IllegalArgumentException("Pemilik usaha tidak boleh null");
            }
        }
        this.idUsaha = idUsaha;
        this.namaUsaha = namaUsaha;
        this.sektor = sektor;
        this.alamat = alamat;
        this.pemilik = pemilik;
    }

    public String getIdUsaha() {
        return idUsaha;
    }

    public String getNamaUsaha() {
        return namaUsaha;
    }

    public String getSektor() {
        return sektor;
    }

    public String getAlamat() {
        return alamat;
    }

    public Nasabah getPemilik() {
        return pemilik;
    }

    @Override
    public String toString() {
        return "Usaha: "
                + "\nID usaha: " + idUsaha
                + "\nNama usaha: " + namaUsaha
                + "\nSektor: " + sektor
                + "\nAlamat: " + alamat
                + "\nPemilik: " + pemilik.getNamaPihak();
    }
}