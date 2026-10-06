package model;

import enums.StatusPengajuan;

/**
 * RiwayatPengajuan
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

    public String getWaktu() {
        return waktu;
    }

    public StatusPengajuan getStatusSebelumnya() {
        return statusSebelumnya;
    }

    public StatusPengajuan getStatusSesudahnya() {
        return statusSesudahnya;
    }

    public String getPelaku() {
        return pelaku;
    }

    public String getKeterangan() {
        return keterangan;
    }

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

    @Override
    public String toString() {
        return "\nwaktu: " + waktu
                + "\nstatusSebelumnya: " + statusSebelumnya
                + "\nstatusSesudahnya: " + statusSesudahnya
                + "\npelaku: " + pelaku
                + "\nketerangan: " + keterangan;
    }
}