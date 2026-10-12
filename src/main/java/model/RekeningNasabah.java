package model;

/**
 * Mewakili rekening milik nasabah dan saldonya dalam rupiah.
 */
public class RekeningNasabah {
    private final String nomorRekening;
    private final Nasabah pemilik;
    private long saldo;

    /**
     * Membuat rekening dengan saldo awal.
     *
     * @param nomorRekening nomor rekening
     * @param pemilik nasabah pemilik rekening
     * @param saldoAwal saldo awal dalam rupiah
     * @throws IllegalArgumentException jika nomor kosong, pemilik null, atau saldo negatif
     */
    public RekeningNasabah(
            String nomorRekening,
            Nasabah pemilik,
            long saldoAwal) {
        if (nomorRekening == null || nomorRekening.trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor rekening wajib diisi");
        }
        if (pemilik == null) {
            throw new IllegalArgumentException("Pemilik rekening tidak boleh null");
        }
        if (saldoAwal < 0) {
            throw new IllegalArgumentException("Saldo awal tidak boleh negatif");
        }

        this.nomorRekening = nomorRekening;
        this.pemilik = pemilik;
        this.saldo = saldoAwal;
    }

    /**
     * Mengembalikan nomor rekening.
     *
     * @return nomor rekening
     */
    public String getNomorRekening() {
        return nomorRekening;
    }

    /**
     * Mengembalikan nasabah pemilik rekening.
     *
     * @return nasabah pemilik rekening
     */
    public Nasabah getPemilik() {
        return pemilik;
    }

    /**
     * Mengembalikan saldo rekening dalam rupiah.
     *
     * @return saldo rekening dalam rupiah
     */
    public long getSaldo() {
        return saldo;
    }

    void kredit(long jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException(
                    "Jumlah kredit harus lebih besar dari 0");
        }
        saldo = Math.addExact(saldo, jumlah);
    }

    /**
     * Menghasilkan ringkasan informasi rekening dan saldonya.
     *
     * @return ringkasan informasi rekening dan saldonya
     */
    @Override
    public String toString() {
        return "Rekening Nasabah"
                + "\nNomor rekening: " + nomorRekening
                + "\nPemilik: " + pemilik.getNamaPihak()
                + "\nSaldo: Rp" + saldo;
    }
}
