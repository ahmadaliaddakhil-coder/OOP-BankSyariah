package model;

import enums.JenisAkad;

/**
 * AkadMudharabah
 */
public class AkadMudharabah extends AkadPembiayaan {

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

    @Override
    public JenisAkad getJenisAkad() {
        return JenisAkad.MUDHARABAH;
    }
}