package model;

import enums.JenisAkad;

/**
 * AkadMusyarakah
 */
public class AkadMusyarakah extends AkadPembiayaan {

    private final long modalNasabah;
    private long modalNasabahDisertakan;

    public AkadMusyarakah(
            String nomorAkad,
            BankSyariah bank,
            KeputusanPembiayaan keputusan,
            String tanggalJatuhTempo,
            int nisbahBank,
            int nisbahNasabah,
            long modalNasabah) {
        super(
                nomorAkad,
                bank,
                keputusan,
                tanggalJatuhTempo,
                nisbahBank,
                nisbahNasabah);

        if (modalNasabah <= 0) {
            throw new IllegalArgumentException(
                    "Modal nasabah harus lebih besar dari 0 untuk akad musyarakah");
        }

        this.modalNasabah = modalNasabah;
        this.modalNasabahDisertakan = 0;
    }

    public long getModalNasabah() {
        return modalNasabah;
    }

    public long getModalNasabahDisertakan() {
        return modalNasabahDisertakan;
    }

    public void catatModalNasabahDisertakan(long jumlah) {
        if (getStatus() != enums.StatusAkad.AKTIF) {
            throw new IllegalStateException(
                    "Kontribusi modal nasabah hanya dapat dicatat pada akad aktif");
        }

        if (modalNasabahDisertakan != 0) {
            throw new IllegalStateException(
                    "Kontribusi modal nasabah sudah tercatat");
        }

        if (jumlah <= 0) {
            throw new IllegalArgumentException(
                    "Kontribusi modal nasabah harus lebih besar dari 0");
        }

        if (jumlah > modalNasabah) {
            throw new IllegalArgumentException(
                    "Kontribusi modal aktual tidak boleh melebihi modal yang direncanakan");
        }

        this.modalNasabahDisertakan = jumlah;
    }

    @Override
    public JenisAkad getJenisAkad() {
        return JenisAkad.MUSYARAKAH;
    }

    @Override
    public String toString() {
        return super.toString()
                + "\nModal nasabah direncanakan: Rp" + modalNasabah
                + "\nModal nasabah disertakan: Rp" + modalNasabahDisertakan;
    }
}