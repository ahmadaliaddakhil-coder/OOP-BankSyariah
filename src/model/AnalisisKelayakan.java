package model;

/**
 * AnalisisKelayakan
 */
public class AnalisisKelayakan {
    private String idAnalisis;
    private String tanggalAnalisis;
    private PengajuanPembiayaan pengajuan;
    private PegawaiBank pegawaiAnalisis;
    private String ringkasan;
    private String catatanRisiko;

    public AnalisisKelayakan(
            String idAnalisis,
            String tanggalAnalisis,
            PengajuanPembiayaan pengajuan,
            PegawaiBank pegawaiAnalisis,
            String ringkasan,
            String catatanRisiko) {

        this.idAnalisis = idAnalisis;
        this.tanggalAnalisis = tanggalAnalisis;
        this.pengajuan = pengajuan;
        this.pegawaiAnalisis = pegawaiAnalisis;
        this.ringkasan = ringkasan;
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

    public String getRingkasan() {
        return ringkasan;
    }

    public String getCatatanRisiko() {
        return catatanRisiko;
    }

    @Override
    public String toString() {
        return "Analisis Kelayakan: "
                + "\nID analisis: " + idAnalisis
                + "\nTanggal analisis: " + tanggalAnalisis
                + "\nID pengajuan: " + pengajuan.getIdPengajuan()
                + "\nPegawai penganalisis: " + pegawaiAnalisis.getNama()
                + "\nRingkasan: " + ringkasan
                + "\nCatatan risiko: " + catatanRisiko;
    }
}