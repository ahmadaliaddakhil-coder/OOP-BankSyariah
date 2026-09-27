package model;

import enums.StatusPembayaran;

public class Pembayaran {
    private final String idPembayaran;
    private final AkadPembiayaan akad;
    private final RekeningNasabah rekeningSumber;
    private final PerhitunganBagiHasil perhitunganBagiHasil;
    private final String tanggalPembayaran;
    private final long jumlahPengembalianModal;
    private final long jumlahBagiHasil;
    private final EvaluasiKerugian evaluasiKerugian;
    private final long jumlahGantiRugi;
    private StatusPembayaran status;
    private String referensiTransaksi;

    public Pembayaran(
            String idPembayaran,
            AkadPembiayaan akad,
            PerhitunganBagiHasil perhitunganBagiHasil,
            String tanggalPembayaran,
            long jumlahPengembalianModal,
            long jumlahBagiHasil,
            RekeningNasabah rekeningSumber) {
        this(
                idPembayaran,
                akad,
                perhitunganBagiHasil,
                tanggalPembayaran,
                jumlahPengembalianModal,
                jumlahBagiHasil,
                null,
                0,
                rekeningSumber);
    }

    public Pembayaran(
            String idPembayaran,
            AkadPembiayaan akad,
            PerhitunganBagiHasil perhitunganBagiHasil,
            String tanggalPembayaran,
            long jumlahPengembalianModal,
            long jumlahBagiHasil,
            EvaluasiKerugian evaluasiKerugian,
            long jumlahGantiRugi,
            RekeningNasabah rekeningSumber) {
        if (idPembayaran == null || idPembayaran.trim().isEmpty()) {
            throw new IllegalArgumentException("ID pembayaran wajib diisi");
        }
        if (akad == null) {
            throw new IllegalArgumentException("Akad tidak boleh null");
        }
        if (rekeningSumber == null) {
            throw new IllegalArgumentException("Rekening sumber wajib diisi");
        }
        if (rekeningSumber.getPemilik()
                        != akad.getKeputusan().getPengajuan().getNasabah()) {
            throw new IllegalArgumentException(
                    "Rekening sumber bukan milik nasabah pengajuan");
        }
        if (!rekeningSumber.getPemilik()
                        .getDaftarRekening().contains(rekeningSumber)) {
            throw new IllegalArgumentException(
                    "Rekening sumber belum terdaftar pada nasabah");
        }
        if (tanggalPembayaran == null || tanggalPembayaran.trim().isEmpty()) {
            throw new IllegalArgumentException("Tanggal pembayaran wajib diisi");
        }
        if (jumlahPengembalianModal < 0 || jumlahBagiHasil < 0
                || jumlahGantiRugi < 0) {
            throw new IllegalArgumentException(
                    "Komponen pembayaran tidak boleh negatif");
        }
        if (Math.addExact(
                        Math.addExact(jumlahPengembalianModal, jumlahBagiHasil),
                        jumlahGantiRugi) == 0) {
            throw new IllegalArgumentException(
                    "Total pembayaran harus lebih besar dari 0");
        }
        if (jumlahBagiHasil > 0 && perhitunganBagiHasil == null) {
            throw new IllegalArgumentException(
                    "Perhitungan bagi hasil wajib dipilih untuk pembayaran bagi hasil");
        }
        if (perhitunganBagiHasil != null
                && perhitunganBagiHasil.getAkad() != akad) {
            throw new IllegalArgumentException(
                    "Perhitungan bagi hasil tersebut bukan untuk akad ini");
        }
        if (jumlahBagiHasil == 0 && perhitunganBagiHasil != null) {
            throw new IllegalArgumentException(
                    "Perhitungan bagi hasil tidak perlu dipilih jika jumlah bagi hasil 0");
        }
        if (jumlahGantiRugi > 0
                && (evaluasiKerugian == null
                        || evaluasiKerugian.getKerugian().getAkad() != akad)) {
            throw new IllegalArgumentException(
                    "Evaluasi ganti rugi harus berasal dari akad ini");
        }
        if (jumlahGantiRugi == 0 && evaluasiKerugian != null) {
            throw new IllegalArgumentException(
                    "Evaluasi tidak perlu dipilih jika jumlah ganti rugi 0");
        }

        this.idPembayaran = idPembayaran;
        this.akad = akad;
        this.rekeningSumber = rekeningSumber;
        this.perhitunganBagiHasil = perhitunganBagiHasil;
        this.tanggalPembayaran = tanggalPembayaran;
        this.jumlahPengembalianModal = jumlahPengembalianModal;
        this.jumlahBagiHasil = jumlahBagiHasil;
        this.evaluasiKerugian = evaluasiKerugian;
        this.jumlahGantiRugi = jumlahGantiRugi;
        this.status = StatusPembayaran.DICATAT;
    }

    public String getIdPembayaran() {
        return idPembayaran;
    }

    public AkadPembiayaan getAkad() {
        return akad;
    }

    public RekeningNasabah getRekeningSumber() {
        return rekeningSumber;
    }

    public PerhitunganBagiHasil getPerhitunganBagiHasil() {
        return perhitunganBagiHasil;
    }

    public String getTanggalPembayaran() {
        return tanggalPembayaran;
    }

    public long getJumlahPengembalianModal() {
        return jumlahPengembalianModal;
    }

    public long getJumlahBagiHasil() {
        return jumlahBagiHasil;
    }

    public EvaluasiKerugian getEvaluasiKerugian() {
        return evaluasiKerugian;
    }

    public long getJumlahGantiRugi() {
        return jumlahGantiRugi;
    }

    public long getJumlahTotal() {
        return Math.addExact(
                Math.addExact(jumlahPengembalianModal, jumlahBagiHasil),
                jumlahGantiRugi);
    }

    public String getReferensiTransaksi() {
        return referensiTransaksi;
    }

    public StatusPembayaran getStatus() {
        return status;
    }

    void tandaiBerhasil(String referensiTransaksi) {
        if (status != StatusPembayaran.DICATAT) {
            throw new IllegalStateException(
                    "Pembayaran hanya dapat diproses dari status DICATAT");
        }
        if (referensiTransaksi == null || referensiTransaksi.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Referensi transaksi wajib diisi untuk pembayaran berhasil");
        }

        rekeningSumber.debit(getJumlahTotal());
        this.referensiTransaksi = referensiTransaksi;
        this.status = StatusPembayaran.BERHASIL;
    }

    void tandaiGagal() {
        if (status != StatusPembayaran.DICATAT) {
            throw new IllegalStateException(
                    "Pembayaran hanya dapat diproses dari status DICATAT");
        }

        this.status = StatusPembayaran.GAGAL;
    }

    @Override
    public String toString() {
        return "Pembayaran"
                + "\nID pembayaran: " + idPembayaran
                + "\nNomor akad: " + akad.getNomorAkad()
                + "\nRekening sumber: "
                + (rekeningSumber == null
                        ? "Tidak dicatat"
                        : rekeningSumber.getNomorRekening())
                + "\nID perhitungan bagi hasil: "
                + (perhitunganBagiHasil == null
                        ? "Tidak ada"
                        : perhitunganBagiHasil.getIdPerhitungan())
                + "\nTanggal pembayaran: " + tanggalPembayaran
                + "\nPengembalian modal: Rp" + jumlahPengembalianModal
                + "\nBagi hasil: Rp" + jumlahBagiHasil
                + "\nGanti rugi: Rp" + jumlahGantiRugi
                + "\nTotal pembayaran: Rp" + getJumlahTotal()
                + "\nStatus: " + status
                + "\nReferensi transaksi: " + referensiTransaksi;
    }
}
