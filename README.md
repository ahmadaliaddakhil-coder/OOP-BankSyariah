# Sistem Pembiayaan Modal Syariah

Demo Java untuk pengajuan pembiayaan usaha menggunakan akad Mudharabah atau
Musyarakah. Analisis menghitung laba operasional dan arus kas tersedia untuk
memberi rekomendasi internal kepada pegawai. Pegawai menetapkan keputusan;
factory terpisah memilih class akad; alur demo berhenti setelah pencairan
penuh ke rekening nasabah. Riwayat status pengajuan dicatat dengan linked list.

## Menjalankan GUI JavaFX

Dibutuhkan JDK 21 atau yang lebih baru. Maven akan disiapkan otomatis oleh
Maven Wrapper. Dari PowerShell pada folder proyek, jalankan:

```powershell
.\mvnw.cmd javafx:run
```

Pilih menu **Tampilan** untuk berpindah antara halaman **Nasabah** dan
**Pegawai**. Nasabah memasukkan pekerjaan dan gaji yang diterima per bulan pada
profil, lalu omzet, biaya langsung, biaya operasional, dan kewajiban usaha pada
formulir pengajuan. Setelah pengajuan dibuat, nasabah dapat memilih baris untuk
melihat kembali rincian lengkapnya. Pegawai melihat profil nasabah, rekening,
usaha, pembiayaan, dan laporan keuangan; lalu memeriksa angka yang sama,
melihat rumus dan hasil arus kas, lalu mengambil tiap langkah secara terpisah:
keputusan, draft akad, tanda tangan, dan pencairan. Aplikasi belum mengecek
dokumen penghasilan, riwayat kredit, atau pengeluaran pribadi, sehingga tidak
mengklaim dapat memastikan kredibilitas maupun kemampuan bayar. Tidak ada
angka contoh keuangan yang diisikan otomatis. Antarmuka merupakan demo tanpa
login sehingga kedua sisi melihat daftar pengajuan yang sama. Data disimpan di
memori dan kembali kosong saat aplikasi ditutup.

`Main` tetap menyediakan demo konsol. Jalankan tes model dan alur dengan:

```powershell
.\mvnw.cmd test
```

## Struktur proyek

- `src/main/java/enums`: status dan jenis akad.
- `src/main/java/model`: model nasabah, usaha, pembiayaan, dan prosesnya.
- `src/main/java/ui`: antarmuka JavaFX.
- `src/test/java/model`: tes perhitungan, data pengajuan, dan alur pembiayaan.
- `target/`: keluaran build dan laporan tes; dibuat Maven dan tidak perlu disimpan.

## Dokumentasi

- [README-SISTEM.md](./README-SISTEM.md): gambaran sistem, cakupan, aturan,
  alur proses, class, dan cara menjalankan.
- [UML-DAN-FLOWCHART.md](./UML-DAN-FLOWCHART.md): class diagram dan flowchart
  Mermaid.
