package model;

import enums.StatusPengajuan;

/**
 * Satu catatan perubahan status pada rangkaian riwayat pengajuan.
 */
public class RiwayatPengajuan {

    private final String waktu;

    // class
    private final StatusPengajuan statusSebelumnya;
    private final StatusPengajuan statusSesudahnya;

    private final String pelaku;
    private final String keterangan;

    // class
    private RiwayatPengajuan berikutnya;

    /**
     * Membuat catatan riwayat pengajuan.
     *
     * @param waktu waktu perubahan status
     * @param statusSebelumnya status sebelum perubahan, atau {@code null} untuk
     *        status awal pengajuan
     * @param statusSesudahnya status setelah perubahan
     * @param pelaku nama pihak yang melakukan perubahan
     * @param keterangan alasan atau keterangan perubahan
     */
    public RiwayatPengajuan(
            String waktu,
            StatusPengajuan statusSebelumnya,
            StatusPengajuan statusSesudahnya,
            String pelaku,
            String keterangan) {

        this.waktu = waktu;
        this.statusSebelumnya = statusSebelumnya;
        this.statusSesudahnya = statusSesudahnya;
        this.pelaku = pelaku;
        this.keterangan = keterangan;
    }

    /**
     * Mengembalikan waktu perubahan status.
     *
     * @return waktu perubahan status
     */
    public String getWaktu() {
        return waktu;
    }

    /**
     * Mengembalikan status sebelum perubahan, atau {@code null} pada catatan awal.
     *
     * @return status sebelum perubahan, atau {@code null} pada catatan awal
     */
    public StatusPengajuan getStatusSebelumnya() {
        return statusSebelumnya;
    }

    /**
     * Mengembalikan status setelah perubahan.
     *
     * @return status setelah perubahan
     */
    public StatusPengajuan getStatusSesudahnya() {
        return statusSesudahnya;
    }

    /**
     * Mengembalikan nama pihak yang melakukan perubahan.
     *
     * @return nama pihak yang melakukan perubahan
     */
    public String getPelaku() {
        return pelaku;
    }

    /**
     * Mengembalikan keterangan perubahan status.
     *
     * @return keterangan perubahan status
     */
    public String getKeterangan() {
        return keterangan;
    }

    /**
     * Mengembalikan catatan berikutnya dalam riwayat, atau {@code null} jika ini catatan terakhir.
     *
     * @return catatan berikutnya dalam riwayat, atau {@code null} jika ini catatan terakhir
     */
    public RiwayatPengajuan getBerikutnya() {
        return berikutnya;
    }

    void setBerikutnya(RiwayatPengajuan berikutnya) {
        if (this.berikutnya != null) {
            throw new IllegalStateException(
                    "Node ini sudah memiliki riwayat berikutnya");
        }

        if (berikutnya == null || berikutnya == this) {
            throw new IllegalArgumentException(
                    "Node berikutnya tidak valid");
        }

        this.berikutnya = berikutnya;
    }

    /**
     * Menghasilkan ringkasan perubahan status.
     *
     * @return ringkasan perubahan status
     */
    @Override
    public String toString() {
        return "\nwaktu: " + waktu
                + "\nstatusSebelumnya: " + statusSebelumnya
                + "\nstatusSesudahnya: " + statusSesudahnya
                + "\npelaku: " + pelaku
                + "\nketerangan: " + keterangan;
    }
}