package model;

public class Pencairan {
    private final String idPencairan;
    private final AkadPembiayaan akad;
    private final RekeningNasabah rekeningTujuan;
    private final long jumlah;
    private final String tanggalPencairan;
    private final String referensiTransaksi;

    Pencairan(
            String idPencairan,
            AkadPembiayaan akad,
            RekeningNasabah rekeningTujuan,
            String tanggalPencairan,
            String referensiTransaksi) {
        if (idPencairan == null || idPencairan.trim().isEmpty()) {
            throw new IllegalArgumentException("ID pencairan wajib diisi");
        }
        if (akad == null) {
            throw new IllegalArgumentException("Akad tidak boleh null");
        }
        if (rekeningTujuan == null) {
            throw new IllegalArgumentException("Rekening tujuan wajib diisi");
        }
        if (rekeningTujuan.getPemilik()
                != akad.getKeputusan().getPengajuan().getNasabah()) {
            throw new IllegalArgumentException(
                    "Rekening tujuan bukan milik nasabah pengajuan");
        }
        if (!rekeningTujuan.getPemilik().getDaftarRekening()
                .contains(rekeningTujuan)) {
            throw new IllegalArgumentException(
                    "Rekening tujuan belum terdaftar pada nasabah");
        }
        if (tanggalPencairan == null || tanggalPencairan.trim().isEmpty()) {
            throw new IllegalArgumentException("Tanggal pencairan wajib diisi");
        }
        if (referensiTransaksi == null || referensiTransaksi.trim().isEmpty()) {
            throw new IllegalArgumentException("Referensi transaksi wajib diisi");
        }

        this.idPencairan = idPencairan;
        this.akad = akad;
        this.rekeningTujuan = rekeningTujuan;
        this.jumlah = akad.getJumlahDisetujui();
        this.tanggalPencairan = tanggalPencairan;
        this.referensiTransaksi = referensiTransaksi;
    }

    public String getIdPencairan() {
        return idPencairan;
    }

    public AkadPembiayaan getAkad() {
        return akad;
    }

    public RekeningNasabah getRekeningTujuan() {
        return rekeningTujuan;
    }

    public long getJumlah() {
        return jumlah;
    }

    public String getTanggalPencairan() {
        return tanggalPencairan;
    }

    public String getReferensiTransaksi() {
        return referensiTransaksi;
    }

    @Override
    public String toString() {
        return "Pencairan:"
                + "\nID pencairan: " + idPencairan
                + "\nNomor akad: " + akad.getNomorAkad()
                + "\nJumlah: Rp" + jumlah
                + "\nTanggal pencairan: " + tanggalPencairan
                + "\nRekening tujuan: " + rekeningTujuan.getNomorRekening()
                + "\nReferensi transaksi: " + referensiTransaksi;
    }
}
