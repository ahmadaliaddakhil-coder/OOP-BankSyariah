package model;

import enums.StatusKeputusan;

/**
 * KeputusanPembiayaan
 */
public class KeputusanPembiayaan {

    private String idKeputusan;
    private String tanggalKeputusan;
    private PengajuanPembiayaan pengajuan;
    private PegawaiBank pegawaiPemutus;
    private StatusKeputusan statusKeputusan;
    private long jumlahDisetujui;
    private String alasan;

    public KeputusanPembiayaan(String idKeputusan, String tanggalKeputusan, PengajuanPembiayaan pengajuan,
            PegawaiBank pegawaiPemutus, StatusKeputusan statusKeputusan, long jumlahDisetujui, String alasan) {

        if (pengajuan == null) {
            throw new IllegalArgumentException("Pengajuan tidak boleh null");
        }
        if (pegawaiPemutus == null) {
            throw new IllegalArgumentException("Pegawai tidak boleh null");
        }
        if (statusKeputusan == null) {
            throw new IllegalArgumentException("Status keputusan tidak boleh null");
        }
        if (statusKeputusan == StatusKeputusan.DISETUJUI) {
            if (jumlahDisetujui <= 0) {
                throw new IllegalArgumentException("Jumlah yang disetujui harus lebih besar dari 0");
            }

            if (jumlahDisetujui > pengajuan.getNominal()) {
                throw new IllegalArgumentException("Jumlah yang disetujui tidak boleh melebihi nominal pengajuan");
            }
        } else if (statusKeputusan == StatusKeputusan.DITOLAK && jumlahDisetujui != 0) {
            throw new IllegalArgumentException("Jumlah yang disetujui harus 0 jika pengajuan ditolak");
        }

        this.idKeputusan = idKeputusan;
        this.tanggalKeputusan = tanggalKeputusan;
        this.pengajuan = pengajuan;
        this.pegawaiPemutus = pegawaiPemutus;
        this.statusKeputusan = statusKeputusan;
        this.jumlahDisetujui = jumlahDisetujui;
        this.alasan = alasan;
    }

    public String getIdKeputusan() {
        return idKeputusan;
    }

    public String getTanggalKeputusan() {
        return tanggalKeputusan;
    }

    public PengajuanPembiayaan getPengajuan() {
        return pengajuan;
    }

    public PegawaiBank getPegawaiPemutus() {
        return pegawaiPemutus;
    }

    public StatusKeputusan getStatusKeputusan() {
        return statusKeputusan;
    }

    public long getJumlahDisetujui() {
        return jumlahDisetujui;
    }

    public String getAlasan() {
        return alasan;
    }

    @Override
    public String toString() {
        return "Keputusan Pembiayaan: "
                + "Id keputusan: " + idKeputusan
                + "\nTanggalKeputusan: " + tanggalKeputusan
                + "\nID pengajuan: " + pengajuan.getIdPengajuan()
                + "\nPegawai Pemutus: " + pegawaiPemutus.getNama()
                + "\nStatusKeputusan: " + statusKeputusan
                + "\njumlahDisetujui: " + jumlahDisetujui
                + "\nalasan: " + alasan;
    }
}