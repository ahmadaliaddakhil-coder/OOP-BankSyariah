package model;

import enums.HasilVerifikasi;
import enums.StatusAkad;

/**
 * PerhitunganBagiHasil
 */
public class PerhitunganBagiHasil {
    private final String idPerhitungan;
    private final VerifikasiLaporan verifikasi;
    private final AkadPembiayaan akad;
    private final String tanggalPerhitungan;
    private final long labaBersih;
    private final long bagianBank;
    private final long bagianNasabah;

    public PerhitunganBagiHasil(
            String idPerhitungan,
            VerifikasiLaporan verifikasi,
            String tanggalPerhitungan) {
        if (verifikasi == null) {
            throw new IllegalArgumentException("Verifikasi tidak boleh null");
        }

        if (verifikasi.getHasil() != HasilVerifikasi.DITERIMA) {
            throw new IllegalArgumentException(
                    "Bagi hasil hanya dapat dihitung dari laporan yang diterima");
        }

        if (verifikasi.getLaporan() == null) {
            throw new IllegalArgumentException("Laporan tidak boleh null");
        }

        if (verifikasi.getLaporan().getStatus() != enums.StatusLaporan.TERVERIFIKASI) {
            throw new IllegalArgumentException(
                    "Bagi hasil hanya dapat dihitung dari laporan berstatus TERVERIFIKASI");
        }

        if (verifikasi.getLaporan().getAkad() == null) {
            throw new IllegalArgumentException("Akad laporan tidak boleh null");
        }

        AkadPembiayaan akadLaporan = verifikasi.getLaporan().getAkad();
        if (akadLaporan.getStatus() != StatusAkad.AKTIF) {
            throw new IllegalStateException(
                    "Bagi hasil hanya dapat dihitung untuk akad berstatus AKTIF");
        }

        long labaDilaporkan = verifikasi.getLaporan().hitungLabaRugi();
        if (labaDilaporkan <= 0) {
            throw new IllegalArgumentException(
                    "Bagi hasil hanya dihitung jika usaha memperoleh laba");
        }

        this.idPerhitungan = idPerhitungan;
        this.verifikasi = verifikasi;
        this.akad = akadLaporan;
        this.tanggalPerhitungan = tanggalPerhitungan;
        this.labaBersih = labaDilaporkan;

        int nisbahBank = akad.getNisbahBank();

        // Bagian bank dibulatkan ke bawah; sisa rupiah menjadi bagian nasabah.
        this.bagianBank = (labaBersih / 100) * nisbahBank
                + ((labaBersih % 100) * nisbahBank) / 100;
        this.bagianNasabah = labaBersih - bagianBank;
    }

    public String getIdPerhitungan() {
        return idPerhitungan;
    }

    public VerifikasiLaporan getVerifikasi() {
        return verifikasi;
    }

    public AkadPembiayaan getAkad() {
        return akad;
    }

    public String getTanggalPerhitungan() {
        return tanggalPerhitungan;
    }

    public long getLabaBersih() {
        return labaBersih;
    }

    public long getBagianBank() {
        return bagianBank;
    }

    public long getBagianNasabah() {
        return bagianNasabah;
    }

    @Override
    public String toString() {
        return "Perhitungan Bagi Hasil: "
                + "\nID perhitungan: " + idPerhitungan
                + "\nID laporan: " + verifikasi.getLaporan().getIdLaporan()
                + "\nNomor akad: " + akad.getNomorAkad()
                + "\nTanggal perhitungan: " + tanggalPerhitungan
                + "\nLaba bersih: Rp" + labaBersih
                + "\nNisbah bank: " + akad.getNisbahBank() + "%"
                + "\nNisbah nasabah: " + akad.getNisbahNasabah() + "%"
                + "\nBagian bank: Rp" + bagianBank
                + "\nBagian nasabah: Rp" + bagianNasabah;
    }
}