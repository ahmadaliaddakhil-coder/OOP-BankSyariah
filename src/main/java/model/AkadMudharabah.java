package model;

import enums.JenisAkad;

/**
 * Akad pembiayaan dengan jenis mudharabah.
 */
public class AkadMudharabah extends AkadPembiayaan {

    /**
     * Membuat akad mudharabah berdasarkan keputusan yang disetujui.
     *
     * @param nomorAkad nomor identitas akad
     * @param bank bank pemberi pembiayaan
     * @param keputusan keputusan pembiayaan yang disetujui
     * @param tanggalJatuhTempo tanggal jatuh tempo
     * @param nisbahBank porsi nisbah bank dalam persen
     * @param nisbahNasabah porsi nisbah nasabah dalam persen
     */
    public AkadMudharabah(
            String nomorAkad,
            BankSyariah bank,
            KeputusanPembiayaan keputusan,
            String tanggalJatuhTempo,
            int nisbahBank,
            int nisbahNasabah) {
        super(
                nomorAkad,
                bank,
                keputusan,
                tanggalJatuhTempo,
                nisbahBank,
                nisbahNasabah);
    }

    /** {@inheritDoc} */
    @Override
    public JenisAkad getJenisAkad() {
        return JenisAkad.MUDHARABAH;
    }
}