package model;

import java.math.BigDecimal;
import util.Uang;

/**
 * Mewakili rekening milik nasabah dan saldonya dalam rupiah berbasis BigDecimal.
 */
public class RekeningNasabah {
    private final String nomorRekening;
    private final Nasabah pemilik;
    private BigDecimal saldo;

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
            BigDecimal saldoAwal) {
        if (nomorRekening == null || nomorRekening.trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor rekening wajib diisi");
        }
        if (pemilik == null) {
            throw new IllegalArgumentException("Pemilik rekening tidak boleh null");
        }
        if (saldoAwal == null || saldoAwal.compareTo(BigDecimal.ZERO) < 0) {
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
    public BigDecimal getSaldo() {
        return saldo;
    }

    void kredit(BigDecimal jumlah) {
        if (jumlah == null || jumlah.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Jumlah kredit harus lebih besar dari 0");
        }
        this.saldo = this.saldo.add(jumlah);
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
                + "\nSaldo: " + Uang.formatRupiah(saldo);
    }
}
