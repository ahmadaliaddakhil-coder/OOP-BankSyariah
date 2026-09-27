package model;

public class RekeningNasabah {
    private final String nomorRekening;
    private final Nasabah pemilik;
    private long saldo;

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

    public String getNomorRekening() {
        return nomorRekening;
    }

    public Nasabah getPemilik() {
        return pemilik;
    }

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

    void debit(long jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException(
                    "Jumlah debit harus lebih besar dari 0");
        }
        if (jumlah > saldo) {
            throw new IllegalStateException("Saldo rekening tidak mencukupi");
        }
        saldo = Math.subtractExact(saldo, jumlah);
    }

    @Override
    public String toString() {
        return "Rekening Nasabah"
                + "\nNomor rekening: " + nomorRekening
                + "\nPemilik: " + pemilik.getNamaPihak()
                + "\nSaldo: Rp" + saldo;
    }
}
