package model;

/**
 * Menyimpan hasil pemeriksaan keuangan usaha untuk suatu pengajuan pembiayaan.
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
    private final long omzetBulanan;
    private final long biayaLangsungBulanan;
    private final long biayaOperasionalBulanan;
    private final long kewajibanUsahaBulanan;
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
     * @throws IllegalArgumentException jika data wajib kosong/null atau nilai keuangan
     *         negatif
     */
    public AnalisisKelayakan(
            String idAnalisis,
            String tanggalAnalisis,
            PengajuanPembiayaan pengajuan,
            PegawaiBank pegawaiAnalisis,
            long omzetBulanan,
            long biayaLangsungBulanan,
            long biayaOperasionalBulanan,
            long kewajibanUsahaBulanan,
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
        if (omzetBulanan < 0 || biayaLangsungBulanan < 0
                || biayaOperasionalBulanan < 0 || kewajibanUsahaBulanan < 0) {
            throw new IllegalArgumentException(
                    "Data keuangan usaha tidak boleh negatif");
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
    public long getOmzetBulanan() {
        return omzetBulanan;
    }

    /**
     * Mengembalikan biaya langsung bulanan dalam rupiah.
     *
     * @return biaya langsung bulanan dalam rupiah
     */
    public long getBiayaLangsungBulanan() {
        return biayaLangsungBulanan;
    }

    /**
     * Mengembalikan biaya operasional bulanan dalam rupiah.
     *
     * @return biaya operasional bulanan dalam rupiah
     */
    public long getBiayaOperasionalBulanan() {
        return biayaOperasionalBulanan;
    }

    /**
     * Mengembalikan kewajiban usaha bulanan dalam rupiah.
     *
     * @return kewajiban usaha bulanan dalam rupiah
     */
    public long getKewajibanUsahaBulanan() {
        return kewajibanUsahaBulanan;
    }

    /**
     * Menghitung omzet setelah dikurangi biaya langsung dan biaya operasional.
     *
     * @return laba operasional bulanan dalam rupiah
     * @throws ArithmeticException jika hasil perhitungan melampaui kapasitas {@code long}
     */
    public long hitungLabaOperasionalBulanan() {
        return Math.subtractExact(
                Math.subtractExact(omzetBulanan, biayaLangsungBulanan),
                biayaOperasionalBulanan);
    }

    /**
     * Menghitung arus kas tersedia berdasarkan data analisis ini.
     *
     * @return arus kas tersedia bulanan dalam rupiah
     * @throws ArithmeticException jika hasil perhitungan melampaui kapasitas {@code long}
     */
    public long hitungArusKasTersediaBulanan() {
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
     * @throws IllegalArgumentException jika ada nilai negatif
     * @throws ArithmeticException jika hasil perhitungan melampaui kapasitas {@code long}
     */
    public static long hitungArusKasTersediaBulanan(
            long omzetBulanan,
            long biayaLangsungBulanan,
            long biayaOperasionalBulanan,
            long kewajibanUsahaBulanan) {
        if (omzetBulanan < 0 || biayaLangsungBulanan < 0
                || biayaOperasionalBulanan < 0 || kewajibanUsahaBulanan < 0) {
            throw new IllegalArgumentException(
                    "Data keuangan usaha tidak boleh negatif");
        }
        return Math.subtractExact(
                Math.subtractExact(
                        Math.subtractExact(omzetBulanan, biayaLangsungBulanan),
                        biayaOperasionalBulanan),
                kewajibanUsahaBulanan);
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
    public static Rekomendasi rekomendasiUntukArusKas(long arusKasTersedia) {
        if (arusKasTersedia > 0) {
            return Rekomendasi.LAYAK_DIPERTIMBANGKAN;
        }
        if (arusKasTersedia == 0) {
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
                + "\nOmzet bulanan: Rp" + omzetBulanan
                + "\nBiaya langsung bulanan: Rp" + biayaLangsungBulanan
                + "\nBiaya operasional bulanan: Rp" + biayaOperasionalBulanan
                + "\nLaba operasional bulanan: Rp" + hitungLabaOperasionalBulanan()
                + "\nKewajiban usaha bulanan: Rp" + kewajibanUsahaBulanan
                + "\nArus kas tersedia bulanan: Rp" + hitungArusKasTersediaBulanan()
                + "\nRekomendasi internal: " + getRekomendasiInternal()
                + "\nCatatan risiko: " + catatanRisiko;
    }
}
