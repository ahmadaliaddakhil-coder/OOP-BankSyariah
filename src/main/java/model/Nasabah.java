package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Menyimpan identitas nasabah serta hubungan nasabah dengan usaha, rekening,
 * dan pengajuan pembiayaannya.
 */
public class Nasabah {
    private final String nomorId;
    private final String nama;
    private final String alamat;
    private final String tanggalLahir; // Format: yyyy-MM-dd
    private final String pekerjaan;
    private final long gajiBulanan;

    private final List<Usaha> daftarUsaha;
    private final List<PengajuanPembiayaan> daftarPengajuan;
    private final List<RekeningNasabah> daftarRekening;

    /**
     * Membuat nasabah dengan data identitas dan gaji bulanan.
     *
     * @param nomorId nomor identitas nasabah
     * @param nama nama nasabah
     * @param alamat alamat nasabah
     * @param tanggalLahir tanggal lahir dalam format {@code yyyy-MM-dd}
     * @param pekerjaan pekerjaan nasabah
     * @param gajiBulanan gaji bulanan dalam rupiah
     * @throws IllegalArgumentException jika gaji bulanan negatif
     */
    public Nasabah(
            String nomorId,
            String nama,
            String alamat,
            String tanggalLahir,
            String pekerjaan,
            long gajiBulanan) {
        if (gajiBulanan < 0) {
            throw new IllegalArgumentException("Gaji bulanan tidak boleh negatif");
        }
        this.nomorId = nomorId;
        this.nama = nama;
        this.alamat = alamat;
        this.tanggalLahir = tanggalLahir;
        this.pekerjaan = pekerjaan;
        this.gajiBulanan = gajiBulanan;
        this.daftarUsaha = new ArrayList<>();
        this.daftarPengajuan = new ArrayList<>();
        this.daftarRekening = new ArrayList<>();
    }

    /**
     * Mendaftarkan rekening milik nasabah ini.
     *
     * @param rekening rekening yang akan didaftarkan
     * @throws IllegalArgumentException jika rekening null, bukan milik nasabah ini,
     *         atau sudah terdaftar
     */
    public void tambahRekening(RekeningNasabah rekening) {
        if (rekening == null) {
            throw new IllegalArgumentException("Rekening tidak boleh null");
        }
        if (rekening.getPemilik() != this) {
            throw new IllegalArgumentException(
                    "Rekening tersebut bukan milik nasabah ini");
        }
        if (daftarRekening.contains(rekening)) {
            throw new IllegalArgumentException(
                    "Rekening tersebut sudah terdaftar");
        }
        for (RekeningNasabah item : daftarRekening) {
            if (item.getNomorRekening().equals(rekening.getNomorRekening())) {
                throw new IllegalArgumentException(
                        "Nomor rekening sudah terdaftar pada nasabah ini");
            }
        }
        daftarRekening.add(rekening);
    }

    /**
     * Mengembalikan salinan daftar rekening nasabah.
     *
     * @return salinan daftar rekening nasabah
     */
    public List<RekeningNasabah> getDaftarRekening() {
        return new ArrayList<>(daftarRekening);
    }

    /**
     * Mendaftarkan usaha yang dimiliki nasabah ini.
     *
     * @param usaha usaha yang akan didaftarkan
     * @throws IllegalArgumentException jika usaha null atau pemiliknya bukan nasabah ini
     */
    public void tambahUsaha(Usaha usaha) {
        if (usaha == null) {
            throw new IllegalArgumentException("Usaha tidak boleh null");
        }

        if (usaha.getPemilik() != this) {
            throw new IllegalArgumentException("Usaha tersebut bukan milik nasabah ini");
        }

        if (!daftarUsaha.contains(usaha)) {
            daftarUsaha.add(usaha);
        }
    }

    /**
     * Mendaftarkan pengajuan yang dibuat oleh nasabah ini untuk usaha terdaftarnya.
     *
     * @param pengajuan pengajuan pembiayaan yang akan didaftarkan
     * @throws IllegalArgumentException jika pengajuan null, milik nasabah lain,
     *         atau usahanya belum terdaftar
     */
    public void ajukanPembiayaan(PengajuanPembiayaan pengajuan) {
        if (pengajuan == null) {
            throw new IllegalArgumentException("Pengajuan tidak boleh null");
        }
        if (pengajuan.getNasabah() != this) {
            throw new IllegalArgumentException("Pengajuan tersebut bukan milik nasabah ini");
        }
        if (!daftarUsaha.contains(pengajuan.getUsaha())) {
            throw new IllegalArgumentException("Usaha pengajuan belum tercatat milik nasabah ini");
        }
        if (!daftarPengajuan.contains(pengajuan)) {
            daftarPengajuan.add(pengajuan);
        }
    }

    /**
     * Mengembalikan salinan daftar usaha nasabah.
     *
     * @return salinan daftar usaha nasabah
     */
    public List<Usaha> getDaftarUsaha() {
        return new ArrayList<>(daftarUsaha);
    }

    /**
     * Mengembalikan salinan daftar pengajuan pembiayaan nasabah.
     *
     * @return salinan daftar pengajuan pembiayaan nasabah
     */
    public List<PengajuanPembiayaan> getDaftarPengajuan() {
        return new ArrayList<>(daftarPengajuan);
    }

    /**
     * Mengembalikan nomor identitas nasabah.
     *
     * @return nomor identitas nasabah
     */
    public String getIdPihak() {
        return nomorId;
    }

    /**
     * Mengembalikan nama nasabah.
     *
     * @return nama nasabah
     */
    public String getNamaPihak() {
        return nama;
    }

    /**
     * Mengembalikan alamat nasabah.
     *
     * @return alamat nasabah
     */
    public String getAlamatPihak() {
        return alamat;
    }

    /**
     * Mengembalikan tanggal lahir nasabah.
     *
     * @return tanggal lahir nasabah
     */
    public String getTanggalLahirPihak() {
        return tanggalLahir;
    }

    /**
     * Mengembalikan pekerjaan nasabah.
     *
     * @return pekerjaan nasabah
     */
    public String getPekerjaanPihak() {
        return pekerjaan;
    }

    /**
     * Mengembalikan gaji bulanan nasabah dalam rupiah.
     *
     * @return gaji bulanan nasabah dalam rupiah
     */
    public long getGajiBulanan() {
        return gajiBulanan;
    }

    /**
     * Menghasilkan ringkasan identitas nasabah dan jumlah usahanya.
     *
     * @return ringkasan identitas nasabah dan jumlah usahanya
     */
    @Override
    public String toString() {
        return "Nasabah: "
                + "\nNomor ID: " + nomorId
                + "\nNama: " + nama
                + "\nAlamat: " + alamat
                + "\nTanggal lahir: " + tanggalLahir
                + "\nPekerjaan: " + pekerjaan
                + "\nGaji bulanan: Rp" + gajiBulanan
                + "\nJumlah usaha: " + daftarUsaha.size();
    }

}