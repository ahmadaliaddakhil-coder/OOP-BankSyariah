package model;

import enums.HasilVerifikasi;

/**
 * VerifikasiLaporan
 */
public class VerifikasiLaporan {
    private final String idVerifikasi;
    private final LaporanUsaha laporan;
    private final PegawaiBank pegawaiPemeriksa;
    private final String tanggalVerifikasi;
    private final HasilVerifikasi hasil;
    private final String catatan;

    public VerifikasiLaporan(
            String idVerifikasi,
            LaporanUsaha laporan,
            PegawaiBank pegawaiPemeriksa,
            String tanggalVerifikasi,
            HasilVerifikasi hasil,
            String catatan) {
        if (laporan == null) {
            throw new IllegalArgumentException("Laporan tidak boleh null");
        }

        if (pegawaiPemeriksa == null) {
            throw new IllegalArgumentException("Pegawai pemeriksa tidak boleh null");
        }

        if (hasil == null) {
            throw new IllegalArgumentException("Hasil verifikasi tidak boleh null");
        }

        if (laporan.getStatus() != enums.StatusLaporan.DALAM_VERIFIKASI) {
            throw new IllegalStateException(
                    "Laporan harus berstatus DALAM_VERIFIKASI sebelum diverifikasi");
        }

        this.idVerifikasi = idVerifikasi;
        this.laporan = laporan;
        this.pegawaiPemeriksa = pegawaiPemeriksa;
        this.tanggalVerifikasi = tanggalVerifikasi;
        this.hasil = hasil;
        this.catatan = catatan;
        laporan.selesaikanVerifikasi(hasil);
    }

    public String getIdVerifikasi() {
        return idVerifikasi;
    }

    public LaporanUsaha getLaporan() {
        return laporan;
    }

    public PegawaiBank getPegawaiPemeriksa() {
        return pegawaiPemeriksa;
    }

    public String getTanggalVerifikasi() {
        return tanggalVerifikasi;
    }

    public HasilVerifikasi getHasil() {
        return hasil;
    }

    public String getCatatan() {
        return catatan;
    }

    @Override
    public String toString() {
        return "Verifikasi Laporan: "
                + "\nID verifikasi: " + idVerifikasi
                + "\nID laporan: " + laporan.getIdLaporan()
                + "\nPegawai pemeriksa: " + pegawaiPemeriksa.getNama()
                + "\nTanggal verifikasi: " + tanggalVerifikasi
                + "\nHasil: " + hasil
                + "\nCatatan: " + catatan;
    }
}