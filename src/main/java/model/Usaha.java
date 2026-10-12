package model;

/**
 * Mewakili usaha yang dimiliki seorang nasabah dan dapat menjadi dasar pengajuan
 * pembiayaan.
 */
public class Usaha {

    private final String idUsaha;
    private final String namaUsaha;
    private final String sektor;
    private final String alamat;
    // class
    private final Nasabah pemilik;

    /**
     * Membuat usaha yang dimiliki nasabah tertentu.
     *
     * @param idUsaha kode identitas usaha
     * @param namaUsaha nama usaha
     * @param sektor sektor usaha
     * @param alamat alamat usaha
     * @param pemilik nasabah pemilik usaha
     * @throws IllegalArgumentException jika pemilik null
     */
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

    /**
     * Mengembalikan kode identitas usaha.
     *
     * @return kode identitas usaha
     */
    public String getIdUsaha() {
        return idUsaha;
    }

    /**
     * Mengembalikan nama usaha.
     *
     * @return nama usaha
     */
    public String getNamaUsaha() {
        return namaUsaha;
    }

    /**
     * Mengembalikan sektor usaha.
     *
     * @return sektor usaha
     */
    public String getSektor() {
        return sektor;
    }

    /**
     * Mengembalikan alamat usaha.
     *
     * @return alamat usaha
     */
    public String getAlamat() {
        return alamat;
    }

    /**
     * Mengembalikan nasabah pemilik usaha.
     *
     * @return nasabah pemilik usaha
     */
    public Nasabah getPemilik() {
        return pemilik;
    }

    /**
     * Menghasilkan ringkasan informasi usaha.
     *
     * @return ringkasan informasi usaha
     */
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