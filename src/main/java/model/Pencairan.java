package model;

/**
 * Catatan pencairan dana akad ke rekening nasabah.
 */
public class Pencairan {
    private final String idPencairan;
    private final AkadPembiayaan akad;
    private final RekeningNasabah rekeningTujuan;
    private final long jumlah;
    private final String tanggalPencairan;
    private final String referensiTransaksi;

    /**
     * Membuat catatan pencairan untuk rekening milik nasabah pada akad.
     *
     * @param idPencairan nomor identitas pencairan
     * @param akad akad yang dicairkan
     * @param rekeningTujuan rekening terdaftar milik nasabah penerima
     * @param tanggalPencairan tanggal pencairan
     * @param referensiTransaksi referensi transaksi
     * @throws IllegalArgumentException jika data tidak lengkap atau rekening tidak
     *         sesuai dengan nasabah pada akad
     */
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

    /**
     * Mengembalikan nomor identitas pencairan.
     *
     * @return nomor identitas pencairan
     */
    public String getIdPencairan() {
        return idPencairan;
    }

    /**
     * Mengembalikan akad yang dicairkan.
     *
     * @return akad yang dicairkan
     */
    public AkadPembiayaan getAkad() {
        return akad;
    }

    /**
     * Mengembalikan rekening tujuan pencairan.
     *
     * @return rekening tujuan pencairan
     */
    public RekeningNasabah getRekeningTujuan() {
        return rekeningTujuan;
    }

    /**
     * Mengembalikan jumlah pencairan dalam rupiah.
     *
     * @return jumlah pencairan dalam rupiah
     */
    public long getJumlah() {
        return jumlah;
    }

    /**
     * Mengembalikan tanggal pencairan.
     *
     * @return tanggal pencairan
     */
    public String getTanggalPencairan() {
        return tanggalPencairan;
    }

    /**
     * Mengembalikan referensi transaksi pencairan.
     *
     * @return referensi transaksi pencairan
     */
    public String getReferensiTransaksi() {
        return referensiTransaksi;
    }

    /**
     * Menghasilkan ringkasan transaksi pencairan.
     *
     * @return ringkasan transaksi pencairan
     */
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
