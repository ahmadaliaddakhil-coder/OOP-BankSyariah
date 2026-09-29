# Sistem Pembiayaan Modal Syariah

## Gambaran umum

Proyek ini adalah demo Java sederhana untuk proses pembiayaan modal usaha di
bank syariah menggunakan akad Mudharabah atau Musyarakah. Alur sistem sengaja
dibatasi sampai dana berhasil dicairkan ke rekening nasabah. Setelah rekening
bertambah, demo selesai.

Sistem tetap mencatat riwayat perubahan status pengajuan menggunakan singly
linked list. Jadi, walaupun alurnya pendek, pengguna dapat melihat kapan
pengajuan dibuat, mulai dianalisis, dan disetujui atau ditolak.

Ini adalah model pembelajaran, bukan aplikasi bank produksi atau penetapan
aturan hukum/fatwa. Seluruh data contoh dibuat di `Main`, hanya berada di
memori, dan hilang setelah program berhenti.

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
3. Nasabah mengajukan pembiayaan untuk usaha yang dimilikinya.
4. Status awal pengajuan `DIAJUKAN` dicatat sebagai node pertama riwayat.
5. Pegawai bank yang terdaftar memulai analisis; status menjadi `DIPROSES`
   dan riwayat bertambah.
6. Catat snapshot omzet, biaya, dan kewajiban usaha pada
   `AnalisisKelayakan`. Sistem menghitung laba operasional serta arus kas
   tersedia dan menampilkan rekomendasi internal kepada pegawai. Angka ini
   adalah input analisis sebelum keputusan, bukan laporan setelah pencairan.
7. Pegawai meninjau hasil, lalu membuat keputusan pembiayaan secara manual.
   - Jika ditolak, status menjadi `DITOLAK`, riwayat diperbarui, lalu alur
     selesai tanpa akad atau pencairan.
   - Jika disetujui, status menjadi `DISETUJUI`, riwayat diperbarui, dan akad
     dibuat.
8. `PengajuanPembiayaan.buatAkad(...)` meminta `PembuatAkad` membuat
   `AkadMudharabah` atau `AkadMusyarakah` mengikuti jenis akad pada pengajuan.
9. Akad ditandatangani sehingga status menjadi `MENUNGGU_PENCAIRAN`.
10. `AkadPembiayaan.cairkan(...)` mencairkan tepat satu kali dengan nilai sama
    dengan jumlah yang disetujui.
11. Jika data pencairan valid, rekening nasabah dikredit dan status akad
    menjadi `DICAIRKAN`.
12. Demo menampilkan nominal pencairan, saldo baru, dan riwayat pengajuan,
    lalu selesai.

Flowchart visual ada di [UML-DAN-FLOWCHART.md](./UML-DAN-FLOWCHART.md).

## Analisis kelayakan

Analisis menggunakan angka bulanan yang dicatat saat pengajuan sedang
ditinjau. Nilai tersebut menjadi snapshot analisis, bukan laporan berkala
setelah pencairan.

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
| `Nasabah` | Menyimpan identitas, usaha, rekening, dan daftar pengajuan. |
| `Usaha` | Menyimpan identitas usaha dan referensi pemiliknya. |
| `RekeningNasabah` | Menyimpan saldo contoh dan menyediakan operasi kredit internal. |
| `PengajuanPembiayaan` | Menyimpan kebutuhan modal, jenis akad, status, analisis, akad, dan riwayat; juga memulai analisis, mencatat keputusan, dan membuat akad. |
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

Dari PowerShell pada folder proyek:

```powershell
$build = Join-Path $env:TEMP 'java-project-build'
New-Item -ItemType Directory -Force -Path $build | Out-Null
$files = Get-ChildItem -Path 'src' -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }
javac -Xlint:all -d $build $files
java -cp $build Main
```

`Main` saat ini menjalankan skenario Mudharabah. Tepat di dekat pembuatan
pengajuan tersedia contoh Musyarakah yang dikomentari. Untuk mencobanya,
komentari pengajuan aktif dan aktifkan contoh Musyarakah.

Skenario demo menggunakan hasil analisis positif dan keputusan disetujui.
Jalur rekomendasi lain serta penolakan dapat dicoba dengan mengganti nilai
input dan keputusan secara konsisten; sistem tidak mengubah rekomendasi
menjadi keputusan otomatis.

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
