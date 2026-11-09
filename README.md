# Sistem Pembiayaan Modal Syariah

Demo Java untuk pengajuan pembiayaan usaha menggunakan akad Mudharabah atau
Musyarakah. Analisis menghitung laba operasional dan arus kas tersedia untuk
memberi rekomendasi internal kepada pegawai. Pegawai menetapkan keputusan;
factory terpisah memilih class akad; alur demo berhenti setelah pencairan
penuh ke rekening nasabah. Riwayat status pengajuan dicatat dengan linked list.
Proyek menyediakan antarmuka desktop JavaFX dan demo konsol. Data hanya
disimpan di memori selama aplikasi berjalan; belum ada login atau database.

## Menjalankan Antarmuka Digital Banking Syariah

Dibutuhkan JDK 21 atau yang lebih baru. Maven akan disiapkan otomatis oleh
Maven Wrapper. Dari PowerShell pada folder proyek, jalankan:

```powershell
.\mvnw.cmd javafx:run
```

Aplikasi hadir dengan antarmuka **Digital Internet Banking Syariah Modern**:
1. **Sistem Sesi & Akun Nasabah:**
   * Terdapat fitur **`+ Daftar Akun Baru`** untuk onboarding nasabah baru dengan rekening tabungan otomatis.
   * Terdapat tombol **`⎋ Keluar / Ganti Akun`** di topbar dan sidebar untuk beralih sesi antara nasabah terdaftar (Siti Rahma, Hendra Pratama, atau akun buatan Anda) maupun masuk ke portal Analis Bank.
2. **Dashboard Nasabah (70% Tabel & Stepper | 30% Panel Rincian):**
   * Metrik 3-kolom: Total Plafon Aktif, Saldo Rekening Terdaftar, dan Status Berkas Terkini.
   * Tabel pengajuan per nasabah yang dilengkapi kolom tanggal pengajuan.
   * Tracking tahapan progres (stepper) 5 langkah: *Pengajuan ➔ Analisis Usaha ➔ Keputusan Komite ➔ Akad Digital ➔ Pencairan Dana*.
   * Formulir **`+ Ajukan Pembiayaan Baru`** otomatis mengisikan data diri nasabah yang sedang aktif.
3. **Portal Analis Bank:**
   * Antrean pengajuan masuk dari seluruh nasabah.
   * Pencarian berkas berdasarkan nomor, nama nasabah, atau usaha.
   * *Action Hub* untuk melakukan pemeriksaan keuangan usaha, keputusan komite, penerbitan akad, tanda tangan digital, dan eksekusi pencairan dana ke rekening.
4. **Panduan Prinsip Syariah:**
   * Tombol **`Pelajari Akad Syariah (?)`** untuk membuka pop-up edukasi perbedaan dan fatwa DSN-MUI akad Mudharabah dan Musyarakah.

Struktur tampilan dan formulir proses menggunakan FXML di
`src/main/resources/views`, sedangkan stylesheet ada di `src/main/resources/css/app.css`.
File `.fxml` dapat dibuka dan diatur dengan JavaFX Scene Builder. `BankSyariahApp`
menjalankan aplikasi, sedangkan logika tombol, validasi, dan komunikasi dengan
model berada di `BankSyariahController`.

`Main` tetap menyediakan demo konsol. Kompilasi proyek dengan:

```powershell
.\mvnw.cmd test
```

Folder source tes `src/test` tidak disertakan di proyek saat ini, jadi perintah
tersebut mengompilasi proyek tetapi tidak menjalankan tes otomatis.

## Alur dan diagram

Nasabah mengisi profil, usaha, rekening, serta laporan keuangan bulanan saat
mengajukan pembiayaan. Pegawai memeriksa laporan dan mencatat analisis sebelum
menetapkan keputusan. Jika disetujui, pegawai membuat draft akad, kemudian
menandatanganinya dan memproses pencairan penuh. Jika ditolak, proses berakhir
tanpa akad. Rekomendasi arus kas hanya menjadi bahan pertimbangan pegawai; tidak
menentukan keputusan secara otomatis.

Diagram kelas, box UML, dan flowchart yang mengikuti implementasi tersedia di
[UML-DAN-FLOWCHART.md](./UML-DAN-FLOWCHART.md). Gambaran aturan domain dan
cakupan sistem ada di [README-SISTEM.md](./README-SISTEM.md).

## Membuat Javadoc

Komentar Javadoc menjelaskan model, status, dan operasi utama pada kode.
Untuk menghasilkan dokumentasi HTML dari PowerShell, jalankan:

```powershell
.\mvnw.cmd javadoc:javadoc
```

Dokumentasi dapat dibuka di `target/site/apidocs/index.html`.

## Struktur proyek

- `src/main/java/enums`: status dan jenis akad.
- `src/main/java/model`: model nasabah, usaha, pembiayaan, dan prosesnya.
- `src/main/java/ui`: antarmuka JavaFX.
- `src/main/resources/views`: struktur tampilan FXML yang dapat dibuka dengan Scene Builder.
- `src/main/resources/css`: stylesheet tampilan JavaFX.
- `target/`: keluaran build dan laporan tes; dibuat Maven dan tidak perlu disimpan.

## Dokumentasi

- [PANDUAN-MEMAHAMI-KODE.pdf](./PANDUAN-MEMAHAMI-KODE.pdf): panduan belajar
  rinci untuk struktur proyek, FXML, CSS JavaFX, controller, dan alur model.
- [README-SISTEM.md](./README-SISTEM.md): gambaran sistem, cakupan, aturan,
  alur proses, class, dan cara menjalankan.
- [UML-DAN-FLOWCHART.md](./UML-DAN-FLOWCHART.md): class diagram dan flowchart
  Mermaid.
