package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utilitas penanganan nilai uang dan pembulatan perbankan syariah berbasis BigDecimal.
 */
public final class Uang {

    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    public static final int SKALA_PERHITUNGAN = 2;
    private static final NumberFormat FORMAT_RUPIAH = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    private Uang() {
    }

    /**
     * Memformat BigDecimal menjadi string representasi mata uang Rupiah.
     *
     * @param nominal nilai uang
     * @return string format Rupiah, misal "Rp80.000.000,00"
     */
    public static String formatRupiah(BigDecimal nominal) {
        if (nominal == null) {
            return FORMAT_RUPIAH.format(BigDecimal.ZERO);
        }
        return FORMAT_RUPIAH.format(nominal);
    }

    /**
     * Menghitung nilai porsi persentase dari suatu total nominal uang.
     * Menggunakan banker's rounding (HALF_EVEN) untuk menjamin keadilan akuntansi.
     *
     * @param total nominal dasar
     * @param persen persentase porsi (0 - 100)
     * @return nominal porsi hasil perhitungan
     */
    public static BigDecimal hitungPorsiPersen(BigDecimal total, int persen) {
        if (total == null) {
            return BigDecimal.ZERO;
        }
        return total.multiply(BigDecimal.valueOf(persen))
                .divide(BigDecimal.valueOf(100), SKALA_PERHITUNGAN, ROUNDING_MODE);
    }

    /**
     * Mengonversi string angka nominal tanpa tanda pemisah menjadi BigDecimal.
     *
     * @param text string angka, misal "80000000"
     * @return BigDecimal hasil konversi
     * @throws IllegalArgumentException jika format tidak valid atau negatif
     */
    public static BigDecimal dariString(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Nominal tidak boleh kosong.");
        }
        try {
            BigDecimal nilai = new BigDecimal(text.trim().replace(".", ""));
            if (nilai.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Nominal tidak boleh negatif.");
            }
            return nilai;
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Format nominal angka tidak valid.");
        }
    }
}

