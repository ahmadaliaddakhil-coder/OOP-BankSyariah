import enums.JenisAkad;
import enums.StatusKeputusan;
import model.AkadPembiayaan;
import model.AnalisisKelayakan;
import model.BankSyariah;
import model.KeputusanPembiayaan;
import model.Nasabah;
import model.PegawaiBank;
import model.Pencairan;
import model.PengajuanPembiayaan;
import model.RekeningNasabah;
import model.RiwayatPengajuan;
import model.Usaha;

public class Main {
    public static void main(String[] args) {
        BankSyariah bank = new BankSyariah("B-001", "Bank Syariah Contoh");
        PegawaiBank pegawai = new PegawaiBank(
                "PG-001", "Ahmad Fauzi", "Analis Pembiayaan", bank);
        bank.tambahPegawai(pegawai);

        Nasabah nasabah = new Nasabah(
                "N-001",
                "Siti Rahma",
                "Bandung",
                "1995-04-12",
                "Wiraswasta");
        Usaha usaha = new Usaha(
                "U-001", "Kedai Berkah", "Kuliner", "Bandung", nasabah);
        nasabah.tambahUsaha(usaha);

        RekeningNasabah rekening = new RekeningNasabah(
                "1234567890", nasabah, 1_000_000L);
        nasabah.tambahRekening(rekening);

        PengajuanPembiayaan pengajuan = new PengajuanPembiayaan(
                "P-001",
                nasabah,
                usaha,
                80_000_000L,
                24,
                "Modal kerja",
                "2026-09-27",
                JenisAkad.MUDHARABAH,
                40,
                60,
                0L);
        nasabah.ajukanPembiayaan(pengajuan);

        /*
         * Alternatif Musyarakah:
         * Ganti pengajuan Mudharabah di atas dengan:
         *
         * PengajuanPembiayaan pengajuan = new PengajuanPembiayaan(
         *         "P-001",
         *         nasabah,
         *         usaha,
         *         80_000_000L,
         *         24,
         *         "Modal kerja",
         *         "2026-09-27",
         *         JenisAkad.MUSYARAKAH,
         *         40,
         *         60,
         *         20_000_000L);
         * nasabah.ajukanPembiayaan(pengajuan);
         */

        System.out.println("\n=== DATA NASABAH ===");
        System.out.println(nasabah);

        System.out.println("\n=== DATA USAHA ===");
        System.out.println(usaha);

        System.out.println("\n=== REKENING SEBELUM PENCAIRAN ===");
        System.out.println(rekening);

        pengajuan.mulaiAnalisis(pegawai, "2026-09-27 10:00");

        AnalisisKelayakan analisis = new AnalisisKelayakan(
                "A-001",
                "2026-09-27",
                pengajuan,
                pegawai,
                30_000_000L,
                12_000_000L,
                8_000_000L,
                3_000_000L,
                "Perlu verifikasi bukti omzet dan biaya usaha");
        pengajuan.catatAnalisis(analisis);

        KeputusanPembiayaan keputusan = new KeputusanPembiayaan(
                "K-001",
                "2026-09-27 14:00",
                pengajuan,
                pegawai,
                StatusKeputusan.DISETUJUI,
                80_000_000L,
                "Pengajuan disetujui berdasarkan hasil analisis");
        pengajuan.catatKeputusan(keputusan);

        System.out.println("\n=== PENGAJUAN PEMBIAYAAN ===");
        System.out.println(pengajuan);

        System.out.println("\n=== ANALISIS KELAYAKAN ===");
        System.out.println(analisis);

        System.out.println("\n=== KEPUTUSAN PEMBIAYAAN ===");
        System.out.println(keputusan);

        AkadPembiayaan akad = pengajuan.buatAkad(
                "AK-001", keputusan, "2028-09-27");
        akad.tandatangani("2026-09-28");

        Pencairan pencairan = akad.cairkan(
                "PC-001",
                "2026-09-29",
                "TRX-20260929-001",
                rekening);

        System.out.println("\n=== AKAD PEMBIAYAAN ===");
        System.out.println(akad);

        System.out.println("\n=== PENCAIRAN ===");
        System.out.println(pencairan);

        System.out.println("\n=== REKENING SETELAH PENCAIRAN ===");
        System.out.println(rekening);

        System.out.println("\n=== RIWAYAT PENGAJUAN ===");
        RiwayatPengajuan riwayat = pengajuan.getRiwayatPertama();
        while (riwayat != null) {
            System.out.println(riwayat);
            riwayat = riwayat.getBerikutnya();
        }

        System.out.println("\nAlur demo selesai setelah pencairan berhasil.");
    }
}
