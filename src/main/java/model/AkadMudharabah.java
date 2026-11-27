package model;

import enums.JenisAkad;
import java.math.BigDecimal;

/**
 * Akad pembiayaan dengan jenis mudharabah.
 * Seluruh modal disediakan oleh bank (shahibul maal) dan kerugian finansial ditanggung 100% oleh bank
 * selama bukan diakibatkan oleh kelalaian atau kecurangan mudharib (nasabah).
 */
public class AkadMudharabah extends AkadPembiayaan {

    /**
     * Membuat akad mudharabah berdasarkan keputusan yang disetujui.
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

    @Override
    public JenisAkad getJenisAkad() {
        return JenisAkad.MUDHARABAH;
    }

    @Override
    public BigDecimal hitungBebanKerugianBank(BigDecimal totalKerugian) {
        if (totalKerugian == null || totalKerugian.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        // Pada Mudharabah murni, kerugian finansial 100% ditanggung shahibul maal (bank)
        return totalKerugian;
    }
}
