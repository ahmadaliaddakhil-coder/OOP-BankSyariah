package model;

import enums.JenisAkad;

/**
 * AkadMusyarakah
 */
public class AkadMusyarakah extends AkadPembiayaan {

    private final long modalNasabah;

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
    }

    public long getModalNasabah() {
        return modalNasabah;
    }

    @Override
    public JenisAkad getJenisAkad() {
        return JenisAkad.MUSYARAKAH;
    }

    @Override
    public String toString() {
        return super.toString()
                + "\nModal nasabah: Rp" + modalNasabah;
    }
}