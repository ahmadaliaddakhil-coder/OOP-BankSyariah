package model;

import enums.StatusKeputusan;

/**
 * Menyimpan hasil keputusan pegawai terhadap suatu pengajuan pembiayaan.
 */
public class KeputusanPembiayaan {

    private final String idKeputusan;
    private final String tanggalKeputusan;
    private final PengajuanPembiayaan pengajuan;
    private final PegawaiBank pegawaiPemutus;
    private final StatusKeputusan statusKeputusan;
    private final long jumlahDisetujui;
    private final String alasan;

    /**
     * Membuat keputusan pembiayaan.
     *
     * @param idKeputusan nomor identitas keputusan
     * @param tanggalKeputusan tanggal keputusan
     * @param pengajuan pengajuan yang diputuskan
     * @param pegawaiPemutus pegawai terdaftar yang membuat keputusan
     * @param statusKeputusan hasil persetujuan atau penolakan
     * @param jumlahDisetujui jumlah yang disetujui dalam rupiah; harus nol jika ditolak
     * @param alasan alasan keputusan
     * @throws IllegalArgumentException jika keputusan, pegawai, atau jumlah tidak valid
     */
    public KeputusanPembiayaan(String idKeputusan, String tanggalKeputusan, PengajuanPembiayaan pengajuan,
            PegawaiBank pegawaiPemutus, StatusKeputusan statusKeputusan, long jumlahDisetujui, String alasan) {

        if (pengajuan == null) {
            throw new IllegalArgumentException("Pengajuan tidak boleh null");
        }
        if (pegawaiPemutus == null) {
            throw new IllegalArgumentException("Pegawai tidak boleh null");
        }
        if (!pegawaiPemutus.getBank().memilikiPegawai(pegawaiPemutus)) {
            throw new IllegalArgumentException(
                    "Pegawai pemutus harus terdaftar pada banknya");
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

    /**
     * Mengembalikan nomor identitas keputusan.
     *
     * @return nomor identitas keputusan
     */
    public String getIdKeputusan() {
        return idKeputusan;
    }

    /**
     * Mengembalikan tanggal keputusan.
     *
     * @return tanggal keputusan
     */
    public String getTanggalKeputusan() {
        return tanggalKeputusan;
    }

    /**
     * Mengembalikan pengajuan yang diputuskan.
     *
     * @return pengajuan yang diputuskan
     */
    public PengajuanPembiayaan getPengajuan() {
        return pengajuan;
    }

    /**
     * Mengembalikan pegawai yang membuat keputusan.
     *
     * @return pegawai yang membuat keputusan
     */
    public PegawaiBank getPegawaiPemutus() {
        return pegawaiPemutus;
    }

    /**
     * Mengembalikan status persetujuan keputusan.
     *
     * @return status persetujuan keputusan
     */
    public StatusKeputusan getStatusKeputusan() {
        return statusKeputusan;
    }

    /**
     * Mengembalikan jumlah yang disetujui dalam rupiah.
     *
     * @return jumlah yang disetujui dalam rupiah
     */
    public long getJumlahDisetujui() {
        return jumlahDisetujui;
    }

    /**
     * Mengembalikan alasan keputusan.
     *
     * @return alasan keputusan
     */
    public String getAlasan() {
        return alasan;
    }

    /**
     * Menghasilkan ringkasan keputusan pembiayaan.
     *
     * @return ringkasan keputusan pembiayaan
     */
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