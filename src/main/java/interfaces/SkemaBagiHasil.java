package interfaces;

import java.math.BigDecimal;

/**
 * Kontrak perhitungan bagi hasil dan alokasi risiko usaha pada akad syariah.
 */
public interface SkemaBagiHasil {

    /**
     * Menghitung porsi keuntungan yang menjadi hak bank berdasarkan nisbah.
     *
     * @param labaKotor total laba kotor usaha yang dilaporkan
     * @return jumlah bagian keuntungan bank
     */
    BigDecimal hitungBagianBank(BigDecimal labaKotor);

    /**
     * Menghitung porsi keuntungan yang menjadi hak nasabah berdasarkan nisbah.
     *
     * @param labaKotor total laba kotor usaha yang dilaporkan
     * @return jumlah bagian keuntungan nasabah
     */
    BigDecimal hitungBagianNasabah(BigDecimal labaKotor);

    /**
     * Menghitung beban kerugian finansial yang wajib ditanggung bank sesuai kaidah syariah.
     * Pada Mudharabah, kerugian finansial ditanggung 100% oleh bank (selama bukan kelalaian nasabah).
     * Pada Musyarakah, kerugian dibagi proporsional terhadap porsi modal masing-masing pihak.
     *
     * @param totalKerugian total kerugian finansial usaha
     * @return beban kerugian yang ditanggung bank
     */
    BigDecimal hitungBebanKerugianBank(BigDecimal totalKerugian);

    /**
     * Mengembalikan deskripsi pembagian nisbah antara bank dan nasabah.
     *
     * @return teks ringkasan nisbah, misal "Bank: 40% | Nasabah: 60%"
     */
    String getDeskripsiNisbah();
}

