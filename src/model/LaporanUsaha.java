package model;

import enums.StatusLaporan;
import enums.HasilVerifikasi;
import enums.StatusAkad;

/**
 * LaporanUsaha
 */
public class LaporanUsaha {

    private final String idLaporan;
    private final AkadPembiayaan akad;
    private final String periode;
    private final String tanggalPengiriman;
    private final long pendapatan;
    private final long biaya;
    private StatusLaporan status;
    private final String catatan;

    public LaporanUsaha(
            String idLaporan,
            AkadPembiayaan akad,
            String periode,
            String tanggalPengiriman,
            long pendapatan,
            long biaya,
            String catatan) {
        if (akad == null) {
            throw new IllegalArgumentException("Akad tidak boleh null");
        }

        if (akad.getStatus() != StatusAkad.AKTIF) {
            throw new IllegalStateException(
                    "Laporan hanya dapat dibuat untuk akad berstatus AKTIF");
        }

        if (pendapatan < 0 || biaya < 0) {
            throw new IllegalArgumentException(
                    "Pendapatan dan biaya tidak boleh negatif");
        }

        this.idLaporan = idLaporan;
        this.akad = akad;
        this.periode = periode;
        this.tanggalPengiriman = tanggalPengiriman;
        this.pendapatan = pendapatan;
        this.biaya = biaya;
        this.status = StatusLaporan.TERKIRIM;
        this.catatan = catatan;
    }

    public String getIdLaporan() {
        return idLaporan;
    }

    public AkadPembiayaan getAkad() {
        return akad;
    }

    public String getPeriode() {
        return periode;
    }

    public String getTanggalPengiriman() {
        return tanggalPengiriman;
    }

    public long getPendapatan() {
        return pendapatan;
    }

    public long getBiaya() {
        return biaya;
    }

    public StatusLaporan getStatus() {
        return status;
    }

    public String getCatatan() {
        return catatan;
    }

    public long hitungLabaRugi() {
        return Math.subtractExact(pendapatan, biaya);
    }

    public void mulaiVerifikasi() {
        if (status != StatusLaporan.TERKIRIM) {
            throw new IllegalStateException(
                    "Verifikasi hanya dapat dimulai untuk laporan berstatus TERKIRIM");
        }

        this.status = StatusLaporan.DALAM_VERIFIKASI;
    }

    void selesaikanVerifikasi(HasilVerifikasi hasil) {
        if (status != StatusLaporan.DALAM_VERIFIKASI) {
            throw new IllegalStateException(
                    "Laporan harus berstatus DALAM_VERIFIKASI");
        }
        if (hasil == null) {
            throw new IllegalArgumentException("Hasil verifikasi tidak boleh null");
        }

        if (hasil == HasilVerifikasi.DITERIMA) {
            this.status = StatusLaporan.TERVERIFIKASI;
        } else {
            this.status = StatusLaporan.PERLU_PERBAIKAN;
        }
    }

    @Override
    public String toString() {
        return "Laporan Usaha: "
                + "\nID laporan: " + idLaporan
                + "\nNomor akad: " + akad.getNomorAkad()
                + "\nPeriode: " + periode
                + "\nTanggal pengiriman: " + tanggalPengiriman
                + "\nPendapatan: Rp" + pendapatan
                + "\nBiaya: Rp" + biaya
                + "\nLaba/rugi: Rp" + hitungLabaRugi()
                + "\nStatus: " + status
                + "\nCatatan: " + catatan;
    }
}