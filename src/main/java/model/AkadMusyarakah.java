package model;

import enums.JenisAkad;
import java.math.BigDecimal;
import util.Uang;

/**
 * Akad pembiayaan musyarakah yang mencatat modal kontribusi nasabah.
 * Kerugian finansial dibagi secara proporsional sesuai porsi penyertaan modal masing-masing.
 */
public class AkadMusyarakah extends AkadPembiayaan {

    private final BigDecimal modalNasabah;

    /**
     * Membuat akad musyarakah berdasarkan keputusan yang disetujui.
     */
    public AkadMusyarakah(
            String nomorAkad,
            BankSyariah bank,
            KeputusanPembiayaan keputusan,
            String tanggalJatuhTempo,
            int nisbahBank,
            int nisbahNasabah,
            BigDecimal modalNasabah) {
        super(
                nomorAkad,
                bank,
                keputusan,
                tanggalJatuhTempo,
                nisbahBank,
                nisbahNasabah);

        if (modalNasabah == null || modalNasabah.compareTo(BigDecimal.ZERO) <= 0) {
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
    public BigDecimal getModalNasabah() {
        return modalNasabah;
    }

    @Override
    public JenisAkad getJenisAkad() {
        return JenisAkad.MUSYARAKAH;
    }

    @Override
    public BigDecimal hitungBebanKerugianBank(BigDecimal totalKerugian) {
        if (totalKerugian == null || totalKerugian.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal modalBank = getJumlahDisetujui();
        BigDecimal totalModal = modalBank.add(modalNasabah);
        if (totalModal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        // Kaidah Syariah: Kerugian ditanggung proporsional modal (modal bank / total modal)
        return totalKerugian.multiply(modalBank)
                .divide(totalModal, Uang.SKALA_PERHITUNGAN, Uang.ROUNDING_MODE);
    }

    @Override
    public String toString() {
        return super.toString()
                + "\nModal nasabah: " + Uang.formatRupiah(modalNasabah);
    }
}
