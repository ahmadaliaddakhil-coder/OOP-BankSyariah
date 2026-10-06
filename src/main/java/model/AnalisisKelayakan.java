package model;

public class AnalisisKelayakan {
    public enum Rekomendasi {
        LAYAK_DIPERTIMBANGKAN,
        PERLU_TINJAUAN,
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

    public String getIdAnalisis() {
        return idAnalisis;
    }

    public String getTanggalAnalisis() {
        return tanggalAnalisis;
    }

    public PengajuanPembiayaan getPengajuan() {
        return pengajuan;
    }

    public PegawaiBank getPegawaiAnalisis() {
        return pegawaiAnalisis;
    }

    public long getOmzetBulanan() {
        return omzetBulanan;
    }

    public long getBiayaLangsungBulanan() {
        return biayaLangsungBulanan;
    }

    public long getBiayaOperasionalBulanan() {
        return biayaOperasionalBulanan;
    }

    public long getKewajibanUsahaBulanan() {
        return kewajibanUsahaBulanan;
    }

    public long hitungLabaOperasionalBulanan() {
        return Math.subtractExact(
                Math.subtractExact(omzetBulanan, biayaLangsungBulanan),
                biayaOperasionalBulanan);
    }

    public long hitungArusKasTersediaBulanan() {
        return hitungArusKasTersediaBulanan(
                omzetBulanan,
                biayaLangsungBulanan,
                biayaOperasionalBulanan,
                kewajibanUsahaBulanan);
    }

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

    public Rekomendasi getRekomendasiInternal() {
        return rekomendasiUntukArusKas(hitungArusKasTersediaBulanan());
    }

    public static Rekomendasi rekomendasiUntukArusKas(long arusKasTersedia) {
        if (arusKasTersedia > 0) {
            return Rekomendasi.LAYAK_DIPERTIMBANGKAN;
        }
        if (arusKasTersedia == 0) {
            return Rekomendasi.PERLU_TINJAUAN;
        }
        return Rekomendasi.TIDAK_DIREKOMENDASIKAN;
    }

    public String getCatatanRisiko() {
        return catatanRisiko;
    }

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
