# Sistem Pembiayaan Modal Syariah

## Gambaran umum

Proyek ini adalah demo Java sederhana untuk proses pembiayaan modal usaha di
bank syariah menggunakan akad Mudharabah atau Musyarakah. Alur sistem sengaja
dibatasi sampai dana berhasil dicairkan ke rekening nasabah. Setelah rekening
bertambah, demo selesai.

Pada GUI, nasabah melaporkan gaji yang diterima per bulan pada profil serta
omzet, biaya langsung, biaya operasional, dan kewajiban usaha per bulan pada
pengajuan. Laporan usaha disimpan pada `PengajuanPembiayaan`; pegawai
memeriksanya dan dapat mencatat nilai terverifikasi pada analisis. Pegawai juga
dapat melihat identitas, pekerjaan, gaji, rekening, data usaha, dan pembiayaan.
Angka masukan tidak otomatis diverifikasi. Tanpa bukti pendapatan, riwayat
kredit, pengeluaran pribadi, atau jadwal pembayaran, aplikasi tidak
menyimpulkan kredibilitas atau kemampuan bayar.

Sistem tetap mencatat riwayat perubahan status pengajuan menggunakan singly
linked list. Jadi, walaupun alurnya pendek, pengguna dapat melihat kapan
pengajuan dibuat, mulai dianalisis, dan disetujui atau ditolak.

Ini adalah model pembelajaran, bukan aplikasi bank produksi atau penetapan
aturan hukum/fatwa. Data untuk demo konsol disiapkan di `Main`; data pada GUI
dimasukkan melalui formulir. Keduanya hanya berada di memori dan hilang setelah
program berhenti.

## Panduan revisi presentasi PDF

Panduan ini merujuk nomor halaman/slide pada PDF 41 halaman yang diberikan.
Tujuannya menyelaraskan presentasi dengan source saat ini: analisis kelayakan
usaha sebelum keputusan, factory akad, dan batas proses sampai pencairan.

| Slide saat ini | Tindakan dan revisi |
|---|---|
| **1** | Pertahankan halaman judul. Opsional: ubah subjudul menjadi “Sistem Pengajuan Pembiayaan Usaha Mudharabah dan Musyarakah — proses hingga pencairan”. |
| **2** | Tidak perlu perubahan class diagram. Pertahankan hanya jika kutipan dan sumbernya sudah diverifikasi serta relevan dengan presentasi. |
| **3** | Pertahankan pengenalan Mudharabah/Musyarakah; pastikan tidak menyiratkan bahwa sistem mengelola pembagian hasil karena scope program berhenti setelah pencairan. |
| **4** | Pertahankan scope, lalu tegaskan analisis usaha dan rekomendasi pegawai terjadi sebelum keputusan. Tetap tulis laporan usaha pascapencairan, bagi hasil, kerugian, evaluasi, pembayaran, dan penyelesaian akad sebagai di luar scope. |
| **5** | Perbarui daftar aktor/entitas: tambahkan `PembuatAkad`; deskripsikan `AnalisisKelayakan` sebagai penghitung indikator snapshot usaha; sebut `PegawaiBank` sebagai pegawai bank yang terdaftar. Hapus `ProfilKeuangan` jika muncul. |
| **6** | Ganti service lama dengan alur pemanggilan model langsung: `mulaiAnalisis`, mencatat analisis, `catatKeputusan`, `pengajuan.buatAkad(...)` yang mendelegasikan ke `PembuatAkad`, tanda tangan, lalu `akad.cairkan(...)`. Sertakan cabang ditolak yang berhenti tanpa akad. |
| **7** | Pertahankan penjelasan konsep Mudharabah, tetapi beri catatan bahwa bagi hasil hanya teori/domain dan tidak dihitung oleh program saat ini. |
| **8** | Pertahankan penjelasan konsep Musyarakah, dengan catatan pembagian hasil/rugi merupakan teori dan tidak dijalankan dalam scope program. |
| **9** | Box `BankSyariah` pada dasarnya sesuai. Pastikan `daftarPegawai` ditampilkan sebagai hubungan kepemilikan/keanggotaan pegawai bank. |
| **10** | Box `PegawaiBank` perlu menyertakan `BankSyariah bank` dan relasi ke bank. Jelaskan bahwa proses memeriksa pegawai terdaftar pada bank terkait. |
| **11** | Hapus field dan relasi `ProfilKeuangan profilKeuangan`. `Nasabah` berisi identitas serta daftar usaha, pengajuan, dan rekening. |
| **12** | Hapus box `ProfilKeuangan` karena class nested tersebut sudah tidak ada. Jangan pindahkan data keuangan usaha ke `Nasabah`; data analisis ada di `AnalisisKelayakan`. |
| **13** | Pertahankan box `Usaha`; usaha dimiliki nasabah dan menjadi usaha yang dibiayai oleh pengajuan. |
| **14** | Pertahankan box `RekeningNasabah`; tampilkan `kredit(long)` sebagai operasi internal yang dipakai saat pencairan. Jangan tampilkan `debit`, karena tidak ada pembayaran/penarikan dalam scope. |
| **15–16** | Perbarui box `PengajuanPembiayaan` mengikuti field dan method source. `ubahStatus(...)` private. `buatAkad(...)` tetap menjadi method yang dipanggil, tetapi tidak lagi memilih subtype sendiri; ia mendelegasikan ke `PembuatAkad`. |
| **17** | Pertahankan box `RiwayatPengajuan` sebagai node linked list; tekankan bahwa riwayat saat ini hanya mencatat perubahan status pengajuan, bukan audit log semua aktivitas. |
| **18** | Pertahankan box `AnalisisKelayakan`. Tambahkan/tegaskan rumus `laba operasional = omzet - biaya langsung - biaya operasional` dan `arus kas tersedia = laba operasional - kewajiban usaha`. `Rekomendasi` adalah enum internal dan hasilnya untuk pegawai, bukan keputusan otomatis. |
| **19** | Pertahankan `KeputusanPembiayaan`; nyatakan keputusan final ditetapkan pegawai setelah meninjau analisis. Persetujuan/penolakan tidak ditentukan otomatis oleh rekomendasi. |
| **20–21** | Perbarui `AkadPembiayaan`: hapus `tanggalMulai`, `List<Pencairan>`, `getDaftarPencairan()`, dan `tambahPencairan()`. Tampilkan satu field `Pencairan pencairan`, `tandatangani(...)`, dan `cairkan(...)`. |
| **22** | Pertahankan `AkadMudharabah`, sesuaikan constructor dengan source, dan tunjukkan override `getJenisAkad()`. |
| **23** | Pertahankan `AkadMusyarakah`, tetapi hanya tampilkan atribut `modalNasabah` serta getter dan `getJenisAkad()`. Hapus `modalNasabahDisertakan` dan method yang tidak ada di source. |
| **24** | Perbarui `Pencairan`: hapus `StatusPencairan`, `catatBerhasil()`, dan `catatGagal()`. Nominal diambil dari keputusan/akad; objek merekam satu pencairan penuh yang berhasil. |
| **25–27** | Hapus `PengajuanService`, `AkadService`, dan `PencairanService`. Ganti bagian ini dengan satu slide box baru `PembuatAkad` (factory) dan penjelasan bahwa tugasnya hanya memilih subtype akad. |
| **28** | Pertahankan `JenisAkad` dengan `MUDHARABAH` dan `MUSYARAKAH`. |
| **29** | Pertahankan `StatusPengajuan`: `DIAJUKAN`, `DIPROSES`, `DISETUJUI`, `DITOLAK`. |
| **30** | Pertahankan `StatusKeputusan`: `DISETUJUI`, `DITOLAK`. |
| **31** | Pertahankan `StatusAkad`: `DRAFT`, `MENUNGGU_PENCAIRAN`, `DICAIRKAN`. |
| **32** | Hapus slide `StatusPencairan`; enum ini tidak ada/dipakai di source sekarang. |
| **33** | Pertahankan nilai rekomendasi, tetapi gambarkan sebagai enum nested `AnalisisKelayakan.Rekomendasi`, bukan class top-level. Jelaskan bahwa rekomendasi ditujukan ke pegawai. |
| **34** | Ganti tautan eksternal saja dengan diagram relasi UML yang tertanam. Tampilkan relasi pegawai-bank, nasabah-usaha/rekening/pengajuan, riwayat linked list, analisis dan keputusan, factory ke subclass akad, serta satu pencairan per akad. |
| **35** | Ganti tautan eksternal saja dengan flowchart tertanam: pengajuan → analisis → rekomendasi internal → keputusan pegawai → ditolak/akad → tanda tangan → pencairan penuh → saldo bertambah → selesai. |
| **36** | Perbarui ringkasan OOP: pertahankan encapsulation, inheritance, polymorphism, dan abstraction pada class akad; tambahkan dependency factory. Hindari klaim komposisi/aggregation yang tidak sesuai dengan relasi yang benar-benar dimodelkan. |
| **37** | Pertahankan slide demo/video. Pastikan demo yang ditautkan menunjukkan scope terbaru dan tidak menampilkan service, bagi hasil, pembayaran, atau penyelesaian akad. |
| **38** | Perbarui batasan: data in-memory, tanggal masih `String`, belum ada UI/auth/database/test suite; tidak ada proses pascapencairan. Ganti “belum ada audit log” dengan keterangan bahwa linked list hanya menyimpan perubahan status. Hapus future work “tambah service layer”; itu tidak diperlukan dalam desain sederhana sekarang. |
| **39** | Pertahankan tautan source code jika masih benar dan dapat diakses. |
| **40** | Ubah kesimpulan “dari pengajuan hingga penyelesaian akad” menjadi “dari pengajuan hingga pencairan dana”; sebut analisis internal, keputusan pegawai, factory akad, dan riwayat status. |
| **41** | Pertahankan halaman penutup dan data anggota kelompok. |

### Konten slide 5: entitas utama

Pihak: `BankSyariah`, `PegawaiBank`, `Nasabah`, `Usaha`, dan
`RekeningNasabah`.

Proses: `PengajuanPembiayaan`, `RiwayatPengajuan`, `AnalisisKelayakan`,
`KeputusanPembiayaan`, `PembuatAkad`, `AkadPembiayaan`,
`AkadMudharabah`, `AkadMusyarakah`, dan `Pencairan`.

### Konten slide 6: alur `Main`

```text
Siapkan bank dan pegawai
→ siapkan nasabah, usaha, rekening
→ ajukan pembiayaan dan catat riwayat awal
→ mulai analisis oleh pegawai bank terdaftar
→ catat snapshot keuangan usaha, hitung laba dan arus kas
→ pegawai meninjau rekomendasi dan menetapkan keputusan
   ├─ ditolak: catat status DITOLAK, selesai tanpa akad
   └─ disetujui: catat status DISETUJUI
       → pengajuan meminta PembuatAkad memilih subtype
       → tandatangani akad
       → cairkan penuh satu kali ke rekening terdaftar nasabah
       → saldo bertambah, tampilkan pencairan dan riwayat, selesai
```

Rekomendasi adalah bantuan internal untuk pegawai. `Main` mengorkestrasi
demo, `PengajuanPembiayaan` mengelola status/proses pengajuan, dan
`PembuatAkad` hanya memilih class akad konkret. Operasi bisnis tetap berada
pada class domain; tidak ada service layer.

## Ruang lingkup

### Termasuk

- Data bank dan pegawai bank.
- Data nasabah, usaha, dan rekening.
- Pengajuan pembiayaan yang dihubungkan dengan usaha milik nasabah.
- Riwayat status pengajuan dengan linked list.
- Analisis snapshot keuangan usaha dan rekomendasi internal untuk pegawai.
- Keputusan pegawai: disetujui atau ditolak.
- Pembuatan akad sesuai jenis pembiayaan yang disetujui.
- Penandatanganan akad.
- Satu kali pencairan penuh sesuai jumlah yang disetujui.
- Penambahan dana pencairan ke saldo rekening nasabah.
- Tampilan ringkasan pengajuan, akad, pencairan, saldo akhir, dan riwayat.

### Tidak termasuk

Setelah batas pencairan, sistem tidak mengelola laporan usaha, verifikasi
laporan, bagi hasil, pembagian kerugian, evaluasi kelalaian/pelanggaran,
pembayaran cicilan, penghentian dini, atau pelunasan akad. Fitur-fitur tersebut
dihapus agar cakupan program tetap fokus.

Sistem juga belum memakai database, UI, login, jaringan, integrasi bank,
ledger/pembukuan, atau test suite otomatis.

## Jenis akad

### Mudharabah

Dalam contoh domain ini, bank menyediakan modal pembiayaan dan pengajuan
memilih `JenisAkad.MUDHARABAH`. Modal nasabah pada data pengajuan harus nol.
Nisbah bank dan nasabah disimpan sebagai ketentuan akad, tetapi demo tidak
menghitung atau membagikan laba karena alur berhenti setelah pencairan.

### Musyarakah

Pengajuan memilih `JenisAkad.MUSYARAKAH` dan mencantumkan modal nasabah yang
direncanakan lebih dari nol. Akad menyimpan nominal modal nasabah tersebut.
Demo tidak melanjutkan ke pengelolaan hasil usaha.

Kedua pilihan memakai jumlah persetujuan dari keputusan bank sebagai jumlah
yang dicairkan. Pencairan dilakukan sekaligus, bukan beberapa tahap.

## Alur sistem

1. Siapkan bank dan pegawai.
2. Buat nasabah, usaha, dan rekening.
3. Nasabah mengisi data usaha, melaporkan keuangan bulanan, lalu mengajukan
   pembiayaan untuk usaha yang dimilikinya. Laporan keuangan disimpan pada
   `PengajuanPembiayaan`.
4. Status awal pengajuan `DIAJUKAN` dicatat sebagai node pertama riwayat.
5. Pegawai bank yang terdaftar memeriksa laporan keuangan nasabah. Form
   analisis mengambil nilai awal dari pengajuan, lalu pegawai dapat
   mengoreksinya berdasarkan pemeriksaan.
6. Setelah pegawai mengonfirmasi pemeriksaan, status menjadi `DIPROSES`,
   riwayat bertambah, dan nilai yang telah diperiksa dicatat pada
   `AnalisisKelayakan`. Sistem menghitung laba operasional serta arus kas
   tersedia dan menampilkan rekomendasi internal.
7. Pegawai menetapkan keputusan pada langkah terpisah setelah membaca hasil.
   - Jika ditolak, status menjadi `DITOLAK`, riwayat diperbarui, lalu alur
     selesai tanpa akad atau pencairan.
   - Jika disetujui, status menjadi `DISETUJUI` dan riwayat diperbarui.
8. Pegawai membuat draft akad pada langkah terpisah.
   `PengajuanPembiayaan.buatAkad(...)` meminta `PembuatAkad` membuat
   `AkadMudharabah` atau `AkadMusyarakah` mengikuti jenis akad pada pengajuan.
9. Pegawai menandatangani draft akad; status berubah menjadi
   `MENUNGGU_PENCAIRAN`.
10. Pegawai memproses pencairan. `AkadPembiayaan.cairkan(...)` mencairkan tepat
    satu kali dengan nilai sama
    dengan jumlah yang disetujui.
11. Jika data pencairan valid, rekening nasabah dikredit dan status akad
    menjadi `DICAIRKAN`.
12. Demo menampilkan nominal pencairan, saldo baru, dan riwayat pengajuan,
    lalu selesai.

Flowchart visual ada di [UML-DAN-FLOWCHART.md](./UML-DAN-FLOWCHART.md).

## Analisis kelayakan

Analisis memakai angka bulanan yang dilaporkan nasabah pada pengajuan lalu
ditinjau atau dikoreksi oleh pegawai. Nilai hasil pemeriksaan menjadi snapshot
analisis; nilai laporan asli tetap tersimpan terpisah pada pengajuan. Keduanya
bukan laporan berkala setelah pencairan.

```text
Laba operasional = omzet - biaya langsung - biaya operasional
Arus kas tersedia = laba operasional - kewajiban usaha
```

Rekomendasi internal yang ditampilkan kepada pegawai:

- Arus kas tersedia positif: `LAYAK_DIPERTIMBANGKAN`.
- Arus kas tersedia nol: `PERLU_TINJAUAN`.
- Arus kas tersedia negatif: `TIDAK_DIREKOMENDASIKAN`.

Hasil ini hanya indikator awal berdasarkan data yang dimasukkan. Sistem tidak
memverifikasi bukti transaksi dan tidak otomatis menentukan persetujuan,
jumlah persetujuan, atau keputusan untuk nasabah. Pegawai bank meninjau hasil
dan catatan risiko lalu menetapkan keputusan. Ambang rekomendasi ini bersifat
ilustratif untuk demo, bukan kebijakan bank atau fatwa.

## Riwayat pengajuan

`PengajuanPembiayaan` menyimpan referensi ke node pertama dan terakhir.
`RiwayatPengajuan` menyimpan:

- waktu perubahan;
- status sebelumnya dan status baru;
- pelaku perubahan;
- keterangan;
- referensi ke node selanjutnya.

Transisi status yang dicatat:

```text
DIAJUKAN -> DIPROSES -> DISETUJUI
                     \-> DITOLAK
```

Untuk melihat seluruh riwayat, `Main` mulai dari `getRiwayatPertama()` dan
berjalan mengikuti `getBerikutnya()` sampai tidak ada node selanjutnya.

## Class utama

| Class | Tanggung jawab |
|---|---|
| `BankSyariah` | Menyimpan identitas bank dan daftar pegawai. |
| `PegawaiBank` | Mewakili pegawai yang menganalisis atau memutus pengajuan. |
| `Nasabah` | Menyimpan identitas, pekerjaan, gaji bulanan yang dilaporkan, usaha, rekening, dan daftar pengajuan. |
| `Usaha` | Menyimpan identitas usaha dan referensi pemiliknya. |
| `RekeningNasabah` | Menyimpan saldo contoh dan menyediakan operasi kredit internal. |
| `PengajuanPembiayaan` | Menyimpan kebutuhan modal, laporan keuangan nasabah, jenis akad, status, analisis, akad, dan riwayat; juga memulai analisis, mencatat keputusan, dan membuat akad. |
| `RiwayatPengajuan` | Node linked list untuk perubahan status pengajuan. |
| `AnalisisKelayakan` | Menyimpan snapshot keuangan usaha, menghitung laba dan arus kas, serta menampilkan rekomendasi internal untuk pegawai. |
| `KeputusanPembiayaan` | Menyimpan hasil keputusan dan jumlah yang disetujui. |
| `PembuatAkad` | Factory sederhana yang memilih class akad konkret berdasarkan jenis akad. |
| `AkadPembiayaan` | Kelas abstrak untuk data akad, penandatanganan, dan pencairan penuh. |
| `AkadMudharabah` | Jenis akad Mudharabah. |
| `AkadMusyarakah` | Jenis akad Musyarakah dan modal nasabah yang direncanakan. |
| `Pencairan` | Catatan satu kali pencairan penuh yang berhasil; dibuat oleh akad. |

Tidak ada lapisan service umum dalam versi sederhana ini. Operasi domain
berada pada class yang memiliki data dan bertanggung jawab atas prosesnya;
factory hanya memusatkan pemilihan subtype akad:

- `PengajuanPembiayaan` mengubah status dan menambahkan riwayat, menerima
  analisis dan keputusan, serta meminta pembuatan akad setelah disetujui.
- `PembuatAkad` memilih subtype akad; detail pemilihan tidak perlu ditaruh
  pada alur pengajuan.
- `AkadPembiayaan` menandatangani akad dan mencairkan dana penuh ke rekening.
- `Main` menyiapkan data dan memanggil operasi tersebut secara berurutan.

Enum yang dipakai adalah `JenisAkad`, `StatusPengajuan`,
`StatusKeputusan`, `StatusAkad`, serta `AnalisisKelayakan.Rekomendasi`.

## Aturan dan validasi penting

- Usaha pada pengajuan harus dimiliki dan terdaftar pada nasabah pengaju.
- Nisbah bank dan nasabah harus berjumlah 100%.
- Modal nasabah harus nol untuk Mudharabah dan lebih dari nol untuk
  Musyarakah.
- Pegawai penganalisis dan pemutus harus tercatat sebagai pegawai bank yang
  menangani pengajuan.
- Akad hanya dibuat dari keputusan yang disetujui dan memakai bank pegawai
  pemutus.
- Jumlah pencairan tidak dimasukkan terpisah: sistem memakai jumlah
  persetujuan agar pencairan selalu penuh dan tidak melebihi keputusan.
- Pencairan hanya dapat dilakukan pada akad berstatus `MENUNGGU_PENCAIRAN`.
- Pencairan hanya menuju rekening terdaftar milik nasabah pengaju.
- Pencairan yang sama tidak dapat dilakukan dua kali untuk akad yang sama.
- Setelah pencairan berhasil, saldo rekening bertambah sebesar jumlah
  persetujuan dan status akad menjadi `DICAIRKAN`.
- Perubahan status pengajuan dilakukan melalui alur internal
  `mulaiAnalisis` dan `catatKeputusan`; method pengubah status tidak dibuka
  untuk pemanggil dari luar class.
- Pemilihan class akad konkret dipusatkan pada `PembuatAkad`. Jika jenis akad
  baru ditambahkan, daftar `JenisAkad` dan factory ini perlu diperbarui, lalu
  class akad barunya dibuat; alur utama pengajuan tetap tidak perlu memuat
  percabangan jenis akad.

## Cara menjalankan

Dibutuhkan JDK 21 atau yang lebih baru. Maven akan disiapkan otomatis oleh
Maven Wrapper. Jalankan GUI JavaFX dari PowerShell pada folder proyek:

```powershell
.\mvnw.cmd javafx:run
```

Menu **Tampilan** memisahkan sisi **Nasabah** dari sisi **Pegawai** tanpa login
atau penyimpanan persisten. Sisi nasabah mengumpulkan data, mengajukan, dan
melihat status; sisi pegawai mencari pengajuan, memeriksa data keuangan,
mencatat analisis, menetapkan keputusan, membuat draft akad, menandatangani
akad, lalu memproses pencairan sebagai langkah-langkah terpisah. Karena ini
demo tanpa login, kedua sisi melihat daftar pengajuan yang sama. Semua data GUI
hanya tersimpan selama aplikasi berjalan.

Jalankan tes rumus, data laporan keuangan, dan urutan alur domain dengan:

```powershell
.\mvnw.cmd test
```

Demo konsol lama tetap dapat dijalankan setelah proyek dikompilasi:

```powershell
.\mvnw.cmd package
java -cp target/classes Main
```

`Main` menjalankan skenario Mudharabah. Tepat di dekat pembuatan pengajuan
tersedia contoh Musyarakah yang dikomentari. Sistem menampilkan rekomendasi
analisis sebagai bahan pertimbangan; pegawai tetap menetapkan keputusan.

## Contoh hasil saldo

Data demo dimulai dengan saldo Rp1.000.000 dan mencairkan Rp80.000.000:

```text
Saldo sebelum pencairan: Rp1.000.000
Jumlah pencairan:        Rp80.000.000
Saldo sesudah pencairan: Rp81.000.000
```

## Struktur direktori

```text
src/
  Main.java
  enums/
  model/
README.md
README-SISTEM.md
UML-DAN-FLOWCHART.md
```

Diagram kelas dan flowchart Mermaid tersedia pada
[UML-DAN-FLOWCHART.md](./UML-DAN-FLOWCHART.md). Untuk pengantar singkat,
lihat [README.md](./README.md).
