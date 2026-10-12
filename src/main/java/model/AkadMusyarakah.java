package model;

import enums.JenisAkad;

/**
 * Akad pembiayaan musyarakah yang mencatat modal kontribusi nasabah.
 */
public class AkadMusyarakah extends AkadPembiayaan {

    private final long modalNasabah;

    /**
     * Membuat akad musyarakah berdasarkan keputusan yang disetujui.
     *
     * @param nomorAkad nomor identitas akad
     * @param bank bank pemberi pembiayaan
     * @param keputusan keputusan pembiayaan yang disetujui
     * @param tanggalJatuhTempo tanggal jatuh tempo
     * @param nisbahBank porsi nisbah bank dalam persen
     * @param nisbahNasabah porsi nisbah nasabah dalam persen
     * @param modalNasabah modal kontribusi nasabah dalam rupiah
     * @throws IllegalArgumentException jika modal nasabah tidak positif
     */
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

    /**
     * Mengembalikan modal kontribusi nasabah dalam rupiah.
     *
     * @return modal kontribusi nasabah dalam rupiah
     */
    public long getModalNasabah() {
        return modalNasabah;
    }

    /** {@inheritDoc} */
    @Override
    public JenisAkad getJenisAkad() {
        return JenisAkad.MUSYARAKAH;
    }

    /**
     * Menghasilkan ringkasan akad beserta modal kontribusi nasabah.
     *
     * @return ringkasan akad beserta modal kontribusi nasabah
     */
    @Override
    public String toString() {
        return super.toString()
                + "\nModal nasabah: Rp" + modalNasabah;
    }
}