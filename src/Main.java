import enums.HasilVerifikasi;
import enums.HasilEvaluasiKerugian;
import enums.JenisAkad;
import enums.JenisTemuanKerugian;
import enums.StatusKeputusan;
import model.AkadPembiayaan;
import model.AnalisisKelayakan;
import model.BankSyariah;
import model.EvaluasiKerugian;
import model.KeputusanPembiayaan;
import model.KerugianUsaha;
import model.LaporanUsaha;
import model.Nasabah;
import model.PegawaiBank;
import model.Pembayaran;
import model.Pencairan;
import model.PerhitunganBagiHasil;
import model.PengajuanPembiayaan;
import model.RekeningNasabah;
import model.RiwayatPengajuan;
import model.Usaha;
import model.VerifikasiLaporan;
import service.AkadService;
import service.PembayaranService;
import service.PencairanService;
import service.PengajuanService;

public class Main {
    public static void main(String[] args) {
        // Siapkan pihak-pihak yang terlibat dalam pembiayaan.
        BankSyariah bank = new BankSyariah("B-001", "Bank Syariah Contoh");
        PegawaiBank pegawai = new PegawaiBank(
                "PG-001", "Ahmad Fauzi", "Analis Pembiayaan", bank);
        bank.tambahPegawai(pegawai);

        Nasabah nasabah = new Nasabah(
                "N-001",
                "Siti Rahma",
                "Bandung",
                "1995-04-12",
                "Wiraswasta",
                new Nasabah.ProfilKeuangan(
                        8_000_000L,
                        1_000_000L,
                        2_000_000L,
                        2,
                        50_000_000L,
                        "2026-09-27"));
        Usaha usaha = new Usaha(
                "U-001", "Kedai Berkah", "Kuliner", "Bandung", nasabah);
        nasabah.tambahUsaha(usaha);

        RekeningNasabah rekening = new RekeningNasabah(
                "1234567890", nasabah, 1_000_000L);
        nasabah.tambahRekening(rekening);

        // Nasabah mengajukan pembiayaan untuk usaha yang dimilikinya.
        System.out.println("\n=== DATA NASABAH ===");
        System.out.println(nasabah);

        System.out.println("\n=== DATA USAHA ===");
        System.out.println(usaha);

        System.out.println("\n=== REKENING NASABAH ===");
        System.out.println(rekening);

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
         * Ganti ke skenario Musyarakah:
         * komentari pengajuan Mudharabah di atas, lalu aktifkan blok ini.
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

        // Bank menganalisis pengajuan lalu mencatat keputusannya.
        PengajuanService pengajuanService = new PengajuanService();
        pengajuanService.mulaiAnalisis(
                pengajuan, pegawai, "2026-09-27 10:00");

        AnalisisKelayakan analisis = new AnalisisKelayakan(
                "A-001",
                "2026-09-27",
                pengajuan,
                pegawai,
                "Dokumen dan kondisi usaha telah dianalisis",
                "Risiko usaha dinilai dapat diterima");
        pengajuan.catatAnalisis(analisis);

        KeputusanPembiayaan keputusan = new KeputusanPembiayaan(
                "K-001",
                "2026-09-27 14:00",
                pengajuan,
                pegawai,
                StatusKeputusan.DISETUJUI,
                80_000_000L,
                "Pengajuan disetujui berdasarkan hasil analisis");
        pengajuanService.catatKeputusan(keputusan);

        System.out.println("\n=== PENGAJUAN PEMBIAYAAN ===");
        System.out.println(pengajuan);

        System.out.println("\n=== ANALISIS KELAYAKAN ===");
        System.out.println(analisis);

        System.out.println("\n=== KEPUTUSAN PEMBIAYAAN ===");
        System.out.println(keputusan);

        // Keputusan yang disetujui menjadi akad, lalu dana dicairkan.
        AkadPembiayaan akad = new AkadService().buatAkad(
                "AK-001", keputusan, "2028-09-27");
        akad.tandatangani("2026-09-28");

        PencairanService pencairanService = new PencairanService();
        Pencairan pencairan = pencairanService.daftarkanPencairan(
                "PC-001",
                akad,
                80_000_000L,
                "2026-09-29",
                rekening);
        pencairanService.catatPencairanBerhasil(
                pencairan, "TRX-20260929-001");

        /*
         * Khusus skenario Musyarakah, tambahkan import:
         * import model.AkadMusyarakah;
         *
         * Setelah pencairan berhasil dan akad aktif, catat modal nasabah:
         * ((AkadMusyarakah) akad).catatModalNasabahDisertakan(20_000_000L);
         */

        System.out.println("\n=== AKAD PEMBIAYAAN ===");
        System.out.println(akad);

        System.out.println("\n=== PENCAIRAN ===");
        System.out.println(pencairan);

        // Laporan diverifikasi; karena rugi, kerugian dicatat pada akad.
        LaporanUsaha laporan = new LaporanUsaha(
                "L-001",
                akad,
                "2026-09",
                "2026-10-05",
                5_000_000L,
                15_000_000L,
                "Laporan usaha mengalami kerugian");
        laporan.mulaiVerifikasi();
        VerifikasiLaporan verifikasi = new VerifikasiLaporan(
                "V-001",
                laporan,
                pegawai,
                "2026-10-06",
                HasilVerifikasi.DITERIMA,
                "Pendapatan dan biaya telah diperiksa");

        KerugianUsaha kerugian = new KerugianUsaha(
                "KRG-001",
                verifikasi,
                "2026-10-07",
                "Kerugian usaha normal");
        akad.catatKerugianUsaha(kerugian);

        /*
         * ALTERNATIF: laporan UNTUNG.
         * Untuk mengaktifkan: komentari pembuatan/pencatatan rugi di atas,
         * tampilan laporan rugi serta evaluasi di bawah, lalu hapus komentar
         * pada blok ini.
         *
         * LaporanUsaha laporanLaba = new LaporanUsaha(
         *         "L-002",
         *         akad,
         *         "2026-10",
         *         "2026-11-05",
         *         10_000_000L,
         *         8_000_000L,
         *         "Laporan usaha memperoleh laba");
         * laporanLaba.mulaiVerifikasi();
         *
         * VerifikasiLaporan verifikasiLaba = new VerifikasiLaporan(
         *         "V-002",
         *         laporanLaba,
         *         pegawai,
         *         "2026-11-06",
         *         HasilVerifikasi.DITERIMA,
         *         "Pendapatan dan biaya telah diperiksa");
         *
         * PerhitunganBagiHasil bagiHasil = new PerhitunganBagiHasil(
         *         "BH-001", verifikasiLaba, "2026-11-07");
         * akad.catatPerhitunganBagiHasil(bagiHasil);
         *
         * System.out.println("\n=== LAPORAN USAHA UNTUNG ===");
         * System.out.println(laporanLaba);
         * System.out.println(verifikasiLaba);
         * System.out.println(bagiHasil);
         */

        /*
         * ALTERNATIF: laporan IMPAS.
         * Untuk mengaktifkan: komentari pembuatan/pencatatan rugi di atas,
         * tampilan laporan rugi serta evaluasi di bawah, lalu hapus komentar
         * pada blok ini.
         *
         * LaporanUsaha laporanImpas = new LaporanUsaha(
         *         "L-002",
         *         akad,
         *         "2026-10",
         *         "2026-11-05",
         *         8_000_000L,
         *         8_000_000L,
         *         "Pendapatan sama dengan biaya");
         * laporanImpas.mulaiVerifikasi();
         * VerifikasiLaporan verifikasiImpas = new VerifikasiLaporan(
         *         "V-002",
         *         laporanImpas,
         *         pegawai,
         *         "2026-11-06",
         *         HasilVerifikasi.DITERIMA,
         *         "Pendapatan dan biaya telah diperiksa");
         *
         * System.out.println("\n=== LAPORAN USAHA IMPAS ===");
         * System.out.println(laporanImpas);
         * System.out.println(verifikasiImpas);
         * System.out.println(
         *         "Hasil impas: tidak ada bagi hasil maupun alokasi kerugian.");
         */

        // Tampilan ini hanya digunakan oleh skenario laporan rugi.
        System.out.println("\n=== LAPORAN USAHA RUGI ===");
        System.out.println(laporan);

        System.out.println("\n=== VERIFIKASI LAPORAN RUGI ===");
        System.out.println(verifikasi);

        System.out.println("\n=== KERUGIAN USAHA ===");
        System.out.println(kerugian);

        /*
         * Evaluasi hanya untuk skenario rugi. Komentari seluruh blok evaluasi
         * ini saat memilih laporan untung atau impas.
         * Ganti KELALAIAN menjadi PELANGGARAN jika sesuai temuannya.
         * Isi nominal ganti rugi hanya jika hasilnya TERBUKTI.
         */
        EvaluasiKerugian evaluasiKerugian = new EvaluasiKerugian(
                "EVK-001",
                kerugian,
                pegawai,
                "2026-10-08",
                JenisTemuanKerugian.KELALAIAN,
                HasilEvaluasiKerugian.DALAM_PEMERIKSAAN,
                0,
                "Ada indikasi kelalaian; bukti masih diperiksa.");
        akad.catatEvaluasiKerugian(evaluasiKerugian);
        evaluasiKerugian.selesaikanPemeriksaan(
                HasilEvaluasiKerugian.TERBUKTI,
                2_000_000L);

        System.out.println("\n=== EVALUASI KERUGIAN ===");
        System.out.println(evaluasiKerugian);

        // Penghentian dini menutup aktivitas usaha baru, tetapi sisa kewajiban
        // tetap dapat dibayar hingga akad benar-benar selesai.
        akad.hentikanDini();
        System.out.println(
                "\nStatus akad setelah dihentikan dini: " + akad.getStatus());

        // Nasabah membayar kewajiban secara bertahap hingga akad lunas.
        PembayaranService pembayaranService = new PembayaranService();
        Pembayaran pembayaranAwal = pembayaranService.daftarkanPembayaran(
                "PB-001",
                akad,
                null,
                "2026-11-15",
                10_000_000L,
                0,
                rekening);
        pembayaranService.catatPembayaranBerhasil(
                pembayaranAwal, "TRX-BAYAR-001");

        Pembayaran pelunasanModal = pembayaranService.daftarkanPembayaran(
                "PB-002",
                akad,
                null,
                "2026-11-20",
                60_000_000L,
                0,
                rekening);
        pembayaranService.catatPembayaranBerhasil(
                pelunasanModal, "TRX-BAYAR-002");
        Pembayaran pembayaranGantiRugi = pembayaranService.daftarkanPembayaran(
                "PB-003",
                akad,
                null,
                "2026-11-21",
                0,
                0,
                evaluasiKerugian,
                2_000_000L,
                rekening);
        pembayaranService.catatPembayaranBerhasil(
                pembayaranGantiRugi, "TRX-BAYAR-003");
        akad.selesaikan();

        /*
         * ALTERNATIF PEMBAYARAN - pilih satu skenario saja.
         *
         * Jika memakai Musyarakah + rugi Rp10 juta, pada kode aktif di atas
         * ganti pelunasan modal 60_000_000L menjadi 62_000_000L.
         *
         * Untuk laporan IMPAS: komentari pembayaran aktif di atas, lalu
         * aktifkan kode berikut. Total modal dikembalikan Rp80 juta.
         *
         * Pembayaran pembayaranAwal = pembayaranService.daftarkanPembayaran(
         *         "PB-001", akad, null, "2026-11-15",
         *         10_000_000L, 0, rekening);
         * pembayaranService.catatPembayaranBerhasil(
         *         pembayaranAwal, "TRX-BAYAR-001");
         * Pembayaran pelunasanModal = pembayaranService.daftarkanPembayaran(
         *         "PB-002", akad, null, "2026-11-20",
         *         70_000_000L, 0, rekening);
         * pembayaranService.catatPembayaranBerhasil(
         *         pelunasanModal, "TRX-BAYAR-002");
         * akad.selesaikan();
         *
         * Untuk laporan UNTUNG: aktifkan laporan laba dan bagi hasil terlebih
         * dahulu, komentari pembayaran aktif di atas, lalu aktifkan kode ini.
         *
         * Pembayaran pembayaranAwal = pembayaranService.daftarkanPembayaran(
         *         "PB-001", akad, bagiHasil, "2026-11-15",
         *         10_000_000L, 300_000L, rekening);
         * pembayaranService.catatPembayaranBerhasil(
         *         pembayaranAwal, "TRX-BAYAR-001");
         * Pembayaran pelunasanModal = pembayaranService.daftarkanPembayaran(
         *         "PB-002", akad, null, "2026-11-20",
         *         70_000_000L, 0, rekening);
         * pembayaranService.catatPembayaranBerhasil(
         *         pelunasanModal, "TRX-BAYAR-002");
         * Pembayaran pelunasanBagiHasil = pembayaranService.daftarkanPembayaran(
         *         "PB-003", akad, bagiHasil, "2026-11-21",
         *         0, 500_000L, rekening);
         * pembayaranService.catatPembayaranBerhasil(
         *         pelunasanBagiHasil, "TRX-BAYAR-003");
         * akad.selesaikan();
         */

        System.out.println("\n=== PEMBAYARAN AWAL ===");
        System.out.println(pembayaranAwal);

        System.out.println("\n=== PELUNASAN MODAL ===");
        System.out.println(pelunasanModal);

        System.out.println("\n=== PEMBAYARAN GANTI RUGI ===");
        System.out.println(pembayaranGantiRugi);

        // Tampilkan ringkasan status kewajiban setelah seluruh pembayaran.
        System.out.println("\n=== STATUS AKHIR AKAD ===");
        System.out.println("Status akhir akad: " + akad.getStatus());
        System.out.println(
                "Sisa pengembalian modal: Rp"
                        + akad.hitungSisaPengembalianModal());
        System.out.println(
                "Sisa ganti rugi: Rp" + akad.hitungSisaTotalGantiRugi());
        System.out.println("Saldo akhir rekening: Rp" + rekening.getSaldo());

        System.out.println("\n=== RIWAYAT PENGAJUAN ===");
        RiwayatPengajuan riwayat = pengajuan.getRiwayatPertama();
        while (riwayat != null) {
            System.out.println();
            System.out.println(riwayat);
            riwayat = riwayat.getBerikutnya();
        }
    }
}
