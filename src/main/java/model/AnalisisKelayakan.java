package model;

import java.math.BigDecimal;
import util.Uang;

/**
 * Menyimpan hasil pemeriksaan keuangan usaha untuk suatu pengajuan pembiayaan berbasis BigDecimal.
 */
public class AnalisisKelayakan {
    /** Rekomendasi internal berdasarkan arus kas tersedia bulanan. */
    public enum Rekomendasi {
        /** Arus kas tersedia positif; pengajuan dapat dipertimbangkan. */
        LAYAK_DIPERTIMBANGKAN,
        /** Arus kas tersedia nol; diperlukan tinjauan lebih lanjut. */
        PERLU_TINJAUAN,
        /** Arus kas tersedia negatif; pengajuan tidak direkomendasikan. */
        TIDAK_DIREKOMENDASIKAN
    }

    private final String idAnalisis;
    private final String tanggalAnalisis;
    private final PengajuanPembiayaan pengajuan;
    private final PegawaiBank pegawaiAnalisis;
    private final BigDecimal omzetBulanan;
    private final BigDecimal biayaLangsungBulanan;
    private final BigDecimal biayaOperasionalBulanan;
    private final BigDecimal kewajibanUsahaBulanan;
    private final String catatanRisiko;

    /**
     * Membuat analisis keuangan usaha untuk pengajuan tertentu.
     *
     * @param idAnalisis nomor identitas analisis
     * @param tanggalAnalisis tanggal analisis
     * @param pengajuan pengajuan yang dianalisis
     * @param pegawaiAnalisis pegawai yang melakukan analisis
     * @param omzetBulanan omzet bulanan dalam rupiah
     * @param biayaLangsungBulanan biaya langsung bulanan dalam rupiah
     * @param biayaOperasionalBulanan biaya operasional bulanan dalam rupiah
     * @param kewajibanUsahaBulanan kewajiban usaha bulanan dalam rupiah
     * @param catatanRisiko catatan risiko hasil analisis
     * @throws IllegalArgumentException jika data wajib kosong/null atau nilai keuangan negatif
     */
    public AnalisisKelayakan(
            String idAnalisis,
            String tanggalAnalisis,
            PengajuanPembiayaan pengajuan,
            PegawaiBank pegawaiAnalisis,
            BigDecimal omzetBulanan,
            BigDecimal biayaLangsungBulanan,
            BigDecimal biayaOperasionalBulanan,
            BigDecimal kewajibanUsahaBulanan,
            String catatanRisiko) {
        if (idAnalisis == null || idAnalisis.trim().isEmpty()) {
            throw new IllegalArgumentException("ID analisis wajib diisi");
        }
        if (tanggalAnalisis == null || tanggalAnalisis.trim().isEmpty()) {
            throw new IllegalArgumentException("Tanggal analisis wajib diisi");
        }
        if (pengajuan == null) {
            throw new IllegalArgumentException("Pengajuan tidak boleh null");
        }
        if (pegawaiAnalisis == null) {
            throw new IllegalArgumentException("Pegawai penganalisis tidak boleh null");
        }
        if (omzetBulanan == null || omzetBulanan.compareTo(BigDecimal.ZERO) < 0
                || biayaLangsungBulanan == null || biayaLangsungBulanan.compareTo(BigDecimal.ZERO) < 0
                || biayaOperasionalBulanan == null || biayaOperasionalBulanan.compareTo(BigDecimal.ZERO) < 0
                || kewajibanUsahaBulanan == null || kewajibanUsahaBulanan.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Data keuangan usaha tidak boleh negatif atau null");
        }
        if (catatanRisiko == null || catatanRisiko.trim().isEmpty()) {
            throw new IllegalArgumentException("Catatan risiko wajib diisi");
        }

        this.idAnalisis = idAnalisis;
        this.tanggalAnalisis = tanggalAnalisis;
        this.pengajuan = pengajuan;
        this.pegawaiAnalisis = pegawaiAnalisis;
        this.omzetBulanan = omzetBulanan;
        this.biayaLangsungBulanan = biayaLangsungBulanan;
        this.biayaOperasionalBulanan = biayaOperasionalBulanan;
        this.kewajibanUsahaBulanan = kewajibanUsahaBulanan;
        this.catatanRisiko = catatanRisiko;
    }

    /**
     * Mengembalikan nomor identitas analisis.
     *
     * @return nomor identitas analisis
     */
    public String getIdAnalisis() {
        return idAnalisis;
    }

    /**
     * Mengembalikan tanggal analisis.
     *
     * @return tanggal analisis
     */
    public String getTanggalAnalisis() {
        return tanggalAnalisis;
    }

    /**
     * Mengembalikan pengajuan yang dianalisis.
     *
     * @return pengajuan yang dianalisis
     */
    public PengajuanPembiayaan getPengajuan() {
        return pengajuan;
    }

    /**
     * Mengembalikan pegawai yang melakukan analisis.
     *
     * @return pegawai yang melakukan analisis
     */
    public PegawaiBank getPegawaiAnalisis() {
        return pegawaiAnalisis;
    }

    /**
     * Mengembalikan omzet bulanan dalam rupiah.
     *
     * @return omzet bulanan dalam rupiah
     */
    public BigDecimal getOmzetBulanan() {
        return omzetBulanan;
    }

    /**
     * Mengembalikan biaya langsung bulanan dalam rupiah.
     *
     * @return biaya langsung bulanan dalam rupiah
     */
    public BigDecimal getBiayaLangsungBulanan() {
        return biayaLangsungBulanan;
    }

    /**
     * Mengembalikan biaya operasional bulanan dalam rupiah.
     *
     * @return biaya operasional bulanan dalam rupiah
     */
    public BigDecimal getBiayaOperasionalBulanan() {
        return biayaOperasionalBulanan;
    }

    /**
     * Mengembalikan kewajiban usaha bulanan dalam rupiah.
     *
     * @return kewajiban usaha bulanan dalam rupiah
     */
    public BigDecimal getKewajibanUsahaBulanan() {
        return kewajibanUsahaBulanan;
    }

    /**
     * Menghitung omzet setelah dikurangi biaya langsung dan biaya operasional.
     *
     * @return laba operasional bulanan dalam rupiah
     */
    public BigDecimal hitungLabaOperasionalBulanan() {
        return omzetBulanan.subtract(biayaLangsungBulanan).subtract(biayaOperasionalBulanan);
    }

    /**
     * Menghitung arus kas tersedia berdasarkan data analisis ini.
     *
     * @return arus kas tersedia bulanan dalam rupiah
     */
    public BigDecimal hitungArusKasTersediaBulanan() {
        return hitungArusKasTersediaBulanan(
                omzetBulanan,
                biayaLangsungBulanan,
                biayaOperasionalBulanan,
                kewajibanUsahaBulanan);
    }

    /**
     * Menghitung arus kas setelah mengurangi seluruh biaya dan kewajiban dari omzet.
     *
     * @param omzetBulanan omzet bulanan dalam rupiah
     * @param biayaLangsungBulanan biaya langsung bulanan dalam rupiah
     * @param biayaOperasionalBulanan biaya operasional bulanan dalam rupiah
     * @param kewajibanUsahaBulanan kewajiban usaha bulanan dalam rupiah
     * @return arus kas tersedia bulanan dalam rupiah
     * @throws IllegalArgumentException jika ada nilai negatif atau null
     */
    public static BigDecimal hitungArusKasTersediaBulanan(
            BigDecimal omzetBulanan,
            BigDecimal biayaLangsungBulanan,
            BigDecimal biayaOperasionalBulanan,
            BigDecimal kewajibanUsahaBulanan) {
        if (omzetBulanan == null || omzetBulanan.compareTo(BigDecimal.ZERO) < 0
                || biayaLangsungBulanan == null || biayaLangsungBulanan.compareTo(BigDecimal.ZERO) < 0
                || biayaOperasionalBulanan == null || biayaOperasionalBulanan.compareTo(BigDecimal.ZERO) < 0
                || kewajibanUsahaBulanan == null || kewajibanUsahaBulanan.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Data keuangan usaha tidak boleh negatif atau null");
        }
        return omzetBulanan.subtract(biayaLangsungBulanan)
                .subtract(biayaOperasionalBulanan)
                .subtract(kewajibanUsahaBulanan);
    }

    /**
     * Mengembalikan rekomendasi internal berdasarkan arus kas analisis ini.
     *
     * @return rekomendasi internal berdasarkan arus kas analisis ini
     */
    public Rekomendasi getRekomendasiInternal() {
        return rekomendasiUntukArusKas(hitungArusKasTersediaBulanan());
    }

    /**
     * Menentukan rekomendasi berdasarkan arus kas tersedia.
     *
     * @param arusKasTersedia arus kas bulanan dalam rupiah
     * @return rekomendasi yang sesuai dengan tanda nilai arus kas
     */
    public static Rekomendasi rekomendasiUntukArusKas(BigDecimal arusKasTersedia) {
        if (arusKasTersedia == null) {
            return Rekomendasi.PERLU_TINJAUAN;
        }
        int perbandingan = arusKasTersedia.compareTo(BigDecimal.ZERO);
        if (perbandingan > 0) {
            return Rekomendasi.LAYAK_DIPERTIMBANGKAN;
        }
        if (perbandingan == 0) {
            return Rekomendasi.PERLU_TINJAUAN;
        }
        return Rekomendasi.TIDAK_DIREKOMENDASIKAN;
    }

    /**
     * Mengembalikan catatan risiko analisis.
     *
     * @return catatan risiko analisis
     */
    public String getCatatanRisiko() {
        return catatanRisiko;
    }

    /**
     * Menghasilkan ringkasan data dan perhitungan analisis.
     *
     * @return ringkasan data dan perhitungan analisis
     */
    @Override
    public String toString() {
        return "Analisis Kelayakan (internal untuk pegawai): "
                + "\nID analisis: " + idAnalisis
                + "\nTanggal analisis: " + tanggalAnalisis
                + "\nID pengajuan: " + pengajuan.getIdPengajuan()
                + "\nPegawai penganalisis: " + pegawaiAnalisis.getNama()
                + "\nOmzet bulanan: " + Uang.formatRupiah(omzetBulanan)
                + "\nBiaya langsung bulanan: " + Uang.formatRupiah(biayaLangsungBulanan)
                + "\nBiaya operasional bulanan: " + Uang.formatRupiah(biayaOperasionalBulanan)
                + "\nLaba operasional bulanan: " + Uang.formatRupiah(hitungLabaOperasionalBulanan())
                + "\nKewajiban usaha bulanan: " + Uang.formatRupiah(kewajibanUsahaBulanan)
                + "\nArus kas tersedia bulanan: " + Uang.formatRupiah(hitungArusKasTersediaBulanan())
                + "\nRekomendasi internal: " + getRekomendasiInternal()
                + "\nCatatan risiko: " + catatanRisiko;
    }
}
