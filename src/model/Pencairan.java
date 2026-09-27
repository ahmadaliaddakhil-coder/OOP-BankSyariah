package model;

import enums.StatusPencairan;
import enums.StatusAkad;

/**
 * Pencairan
 */
public class Pencairan {
    private final String idPencairan;
    private final AkadPembiayaan akad;
    private final RekeningNasabah rekeningNasabah;
    private final long jumlah;
    private final String tanggalPencairan; // Format: yyyy-MM-dd
    private final String rekeningTujuan;

    private StatusPencairan status;
    private String referensiTransaksi;

    public Pencairan(
            String idPencairan,
            AkadPembiayaan akad,
            long jumlah,
            String tanggalPencairan,
            RekeningNasabah rekeningNasabah) {
        if (akad == null) {
            throw new IllegalArgumentException("Akad tidak boleh null");
        }
        if (rekeningNasabah == null) {
            throw new IllegalArgumentException("Rekening tujuan wajib diisi");
        }
        if (idPencairan == null || idPencairan.trim().isEmpty()) {
            throw new IllegalArgumentException("ID pencairan wajib diisi");
        }
        if (tanggalPencairan == null || tanggalPencairan.trim().isEmpty()) {
            throw new IllegalArgumentException("Tanggal pencairan wajib diisi");
        }
        if (rekeningNasabah.getPemilik()
                        != akad.getKeputusan().getPengajuan().getNasabah()) {
            throw new IllegalArgumentException(
                    "Rekening tujuan bukan milik nasabah pengajuan");
        }
        if (!rekeningNasabah.getPemilik()
                        .getDaftarRekening().contains(rekeningNasabah)) {
            throw new IllegalArgumentException(
                    "Rekening tujuan belum terdaftar pada nasabah");
        }

        if (jumlah <= 0) {
            throw new IllegalArgumentException(
                    "Jumlah pencairan harus lebih besar dari 0");
        }

        if (jumlah > akad.getJumlahDisetujui()) {
            throw new IllegalArgumentException(
                    "Jumlah pencairan tidak boleh melebihi jumlah yang disetujui");
        }

        this.idPencairan = idPencairan;
        this.akad = akad;
        this.rekeningNasabah = rekeningNasabah;
        this.jumlah = jumlah;
        this.tanggalPencairan = tanggalPencairan;
        this.rekeningTujuan = rekeningNasabah.getNomorRekening();
        this.status = StatusPencairan.DICATAT;
        this.referensiTransaksi = null;
    }

    public String getIdPencairan() {
        return idPencairan;
    }

    public AkadPembiayaan getAkad() {
        return akad;
    }

    public long getJumlah() {
        return jumlah;
    }

    public String getTanggalPencairan() {
        return tanggalPencairan;
    }

    public String getRekeningTujuan() {
        return rekeningTujuan;
    }

    public RekeningNasabah getRekeningNasabah() {
        return rekeningNasabah;
    }

    public StatusPencairan getStatus() {
        return status;
    }

    public String getReferensiTransaksi() {
        return referensiTransaksi;
    }

    public void catatBerhasil(String referensiTransaksi) {
        if (referensiTransaksi == null || referensiTransaksi.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Referensi transaksi wajib diisi untuk pencairan berhasil");
        }

        if (status != StatusPencairan.DICATAT) {
            throw new IllegalStateException(
                    "Pencairan hanya dapat diproses dari status DICATAT");
        }

        if (!akad.memilikiPencairan(this)) {
            throw new IllegalStateException(
                    "Pencairan belum didaftarkan pada akad");
        }

        if (akad.getStatus() != StatusAkad.MENUNGGU_PENCAIRAN
                && akad.getStatus() != StatusAkad.AKTIF) {
            throw new IllegalStateException(
                    "Akad tidak dalam status yang mengizinkan pencairan");
        }

        rekeningNasabah.kredit(jumlah);
        akad.aktifkanSetelahPencairanBerhasil(tanggalPencairan);
        this.referensiTransaksi = referensiTransaksi;
        this.status = StatusPencairan.BERHASIL;
    }

    public void catatGagal() {
        if (status != StatusPencairan.DICATAT) {
            throw new IllegalStateException(
                    "Pencairan hanya dapat diproses dari status DICATAT");
        }

        if (!akad.memilikiPencairan(this)) {
            throw new IllegalStateException(
                    "Pencairan belum didaftarkan pada akad");
        }

        this.status = StatusPencairan.GAGAL;
    }

    @Override
    public String toString() {
        return "Pencairan: "
                + "\nID pencairan: " + idPencairan
                + "\nNomor akad: " + akad.getNomorAkad()
                + "\nJumlah: Rp" + jumlah
                + "\nTanggal pencairan: " + tanggalPencairan
                + "\nRekening tujuan: " + rekeningTujuan
                + "\nStatus: " + status
                + "\nReferensi transaksi: " + referensiTransaksi;
    }
}