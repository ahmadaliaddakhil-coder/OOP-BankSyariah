# Penjelasan Sistem Pembiayaan Modal Syariah

## 1. Gambaran umum

Proyek ini adalah prototipe pembelajaran berbasis Java untuk menggambarkan
proses pembiayaan modal usaha di bank syariah dengan dua pilihan akad:

- **Mudharabah**: dalam model proyek, bank menyediakan modal pembiayaan dan
  nisbah menentukan pembagian laba antara bank dan nasabah. Kerugian usaha
  normal dicatat sebagai bagian kerugian bank. Temuan kelalaian atau
  pelanggaran dapat menghasilkan ganti rugi terpisah setelah pemeriksaan
  dinyatakan terbukti.
- **Musyarakah**: bank dan nasabah sama-sama berkontribusi modal. Laba dibagi
  menurut nisbah akad. Alokasi kerugian dalam kode dihitung menurut
  perbandingan modal bank yang berhasil dicairkan dan modal nasabah yang
  benar-benar dicatat telah disertakan.

Aturan di atas menjelaskan **cara prototipe ini bekerja**, bukan pengganti
fatwa, nasihat hukum, kebijakan bank, atau penelaahan kepatuhan syariah.
Ketentuan produk nyata perlu divalidasi oleh pihak yang berwenang.

Program berfokus pada alur objek dan aturan domain, bukan aplikasi bank
produksi. Data dibuat langsung dalam `Main`, disimpan di memori, dan tidak
bertahan setelah program berhenti. Belum ada database, antarmuka pengguna,
autentikasi, integrasi rekening bank, maupun layanan jaringan.

## 2. Tujuan dan cakupan

### Yang dimodelkan

1. Data bank, pegawai, nasabah, usaha, dan rekening nasabah.
2. Pengajuan pembiayaan oleh nasabah untuk usaha miliknya.
3. Pemeriksaan dan pencatatan analisis kelayakan.
4. Keputusan pembiayaan: disetujui atau ditolak.
5. Pembuatan akad Mudharabah atau Musyarakah berdasarkan pengajuan yang
   disetujui.
6. Pencairan dana dan perubahan saldo rekening contoh.
7. Pengiriman serta verifikasi laporan usaha.
8. Penghitungan bagi hasil, pencatatan kerugian, serta evaluasi
   kelalaian/pelanggaran.
9. Pembayaran kembali modal, bagian bagi hasil bank, dan ganti rugi yang
   ditetapkan setelah temuan terbukti.
10. Penghentian dini dan penyelesaian akad setelah kewajiban yang tercatat
    lunas.
11. Riwayat perubahan status pengajuan menggunakan singly linked list.

### Yang tidak dimodelkan

- Database, penyimpanan permanen, atau pemulihan data.
- Login, peran/otorisasi pengguna, enkripsi, dan audit log umum.
- Integrasi dengan rekening atau sistem pembayaran bank sungguhan.
- Pembukuan dua sisi, saldo bank, pencatatan jurnal, serta rekonsiliasi.
- Pengiriman/pengeditan ulang laporan yang perlu perbaikan.
- Mesin aturan produk, kalender pembayaran, denda keterlambatan, pajak, atau
  proses penagihan.
- Penetapan otomatis konsekuensi hukum atau syariah dari temuan.
- Validasi seluruh edge case yang diperlukan untuk produksi.

## 3. Pihak dan objek inti

### Bank dan pegawai

- **`BankSyariah`** menyimpan kode/nama bank dan daftar pegawai.
- **`PegawaiBank`** terkait ke satu bank. Pegawai digunakan sebagai analis,
  pemutus pembiayaan, pemeriksa laporan, dan pemeriksa kerugian.
- Model memeriksa agar pegawai pemutus berasal dari bank yang menerbitkan akad;
  evaluasi kerugian juga harus dilakukan pegawai dari bank yang sama.

### Nasabah, usaha, rekening

- **`Nasabah`** menyimpan identitas, profil keuangan, daftar usaha, daftar
  pengajuan, dan daftar rekening.
- **`Nasabah.ProfilKeuangan`** menyimpan pendapatan, kewajiban bulanan,
  tanggungan, aset, dan tanggal pembaruan. Ia dapat menghitung pendapatan dan
  sisa pendapatan bulanan; saat ini bukan mesin penilaian kredit otomatis.
- **`Usaha`** memiliki satu nasabah sebagai pemilik. Pengajuan harus merujuk
  usaha yang terdaftar pada nasabah tersebut.
- **`RekeningNasabah`** menyimpan nomor rekening, pemilik, serta saldo simulasi.
  Pencairan berhasil mengkredit saldo; pembayaran berhasil mendebit saldo.

### Pengajuan dan riwayat

- **`PengajuanPembiayaan`** mengikat nasabah dan usaha dengan nominal, tenor,
  tujuan, pilihan akad, nisbah, dan modal nasabah yang direncanakan.
- Nisbah bank dan nasabah harus berjumlah 100%. Untuk Mudharabah, modal
  nasabah pada pengajuan harus nol. Untuk Musyarakah, modal nasabah harus
  positif.
- Setiap perubahan status pengajuan menambahkan **`RiwayatPengajuan`** baru
  yang terhubung ke node sebelumnya melalui referensi `berikutnya`.
- Riwayat tersebut hanya mencatat perubahan status pengajuan, bukan seluruh
  aktivitas transaksi akad.

### Analisis, keputusan, akad

- **`AnalisisKelayakan`** menyimpan ringkasan, catatan risiko, pegawai analis,
  dan tanggal analisis.
- **`KeputusanPembiayaan`** menyimpan hasil, jumlah yang disetujui, pegawai
  pemutus, alasan, dan tanggal keputusan. Jumlah persetujuan tidak boleh
  melebihi jumlah pengajuan; keputusan ditolak harus memiliki jumlah
  persetujuan nol.
- **`AkadService`** membuat subclass akad yang sesuai dengan jenis akad pada
  pengajuan yang telah disetujui, lalu menghubungkannya kembali dengan
  pengajuan.
- **`AkadPembiayaan`** menjadi kelas dasar abstrak bagi `AkadMudharabah` dan
  `AkadMusyarakah`. Akad menyimpan keputusan, bank, nisbah, status, daftar
  pencairan, pembayaran, perhitungan bagi hasil, kerugian, dan evaluasi
  kerugian.
- **`AkadMusyarakah`** membedakan modal nasabah yang direncanakan dan jumlah
  modal aktual yang telah dicatat disertakan.

### Laporan, hasil usaha, dan pembayaran

- **`LaporanUsaha`** menyimpan periode, pendapatan, biaya, status, dan catatan.
  Laba/rugi dihitung sebagai `pendapatan - biaya`.
- **`VerifikasiLaporan`** merekam pegawai pemeriksa, hasil, tanggal, dan
  catatan. Hasilnya mengubah status laporan menjadi `TERVERIFIKASI` atau
  `PERLU_PERBAIKAN`.
- **`PerhitunganBagiHasil`** menyimpan laba bersih serta bagian bank dan
  nasabah. Objek ini hanya dapat dibuat dari laporan terverifikasi/diterima
  yang menghasilkan laba positif.
- **`KerugianUsaha`** menyimpan nilai rugi, alokasi pihak-pihak, tanggal, dan
  keterangan. Objek ini hanya dapat dibuat dari laporan terverifikasi yang
  menghasilkan rugi.
- **`EvaluasiKerugian`** menyimpan jenis temuan (kelalaian/pelanggaran), hasil
  pemeriksaan, petugas, catatan, dan nominal ganti rugi. Ganti rugi adalah
  kewajiban terpisah, tidak mengganti perhitungan alokasi rugi normal.
- **`Pencairan`** mencatat nilai pencairan, rekening tujuan, status, dan
  referensi transaksi.
- **`Pembayaran`** dapat mencatat tiga komponen secara terpisah: pengembalian
  modal, bagi hasil, dan ganti rugi. Total komponen mendebit rekening nasabah
  ketika pembayaran berhasil.

Relasi lengkap antarkelas, kardinalitas, atribut penting, operasi, dan enum
ditunjukkan di [UML-DAN-FLOWCHART.md](./UML-DAN-FLOWCHART.md).

## 4. Perbedaan alur akad

### Mudharabah

1. Pengajuan memilih `JenisAkad.MUDHARABAH`.
2. Modal nasabah pada pengajuan harus `0`.
3. Service membuat objek `AkadMudharabah`.
4. Nisbah pada akad digunakan untuk membagi laba yang terverifikasi.
5. Dalam aturan yang dikodekan, kerugian usaha normal dialokasikan kepada
   bank. Jika evaluasi menemukan kelalaian/pelanggaran terbukti, pemeriksa
   dapat menetapkan ganti rugi terpisah; besarnya dimasukkan sebagai nominal,
   bukan dihitung otomatis.

### Musyarakah

1. Pengajuan memilih `JenisAkad.MUSYARAKAH`.
2. Modal nasabah yang direncanakan harus lebih besar dari nol.
3. Service membuat objek `AkadMusyarakah`.
4. Setelah akad aktif, modal aktual nasabah dicatat dengan
   `catatModalNasabahDisertakan(...)`. Nilai aktual harus positif dan tidak
   boleh melebihi nilai yang direncanakan.
5. Nisbah akad digunakan untuk pembagian laba.
6. Untuk kerugian, kode menggunakan pencairan bank yang berhasil dan modal
   aktual nasabah. Bagian bank dihitung secara proporsional dengan pembagian
   bilangan bulat; sisa rupiah menjadi bagian nasabah.

Rumus teknis alokasi rugi Musyarakah yang ada di source:

```text
total modal = modal bank berhasil dicairkan + modal nasabah aktual
bagian bank = total rugi x modal bank / total modal
bagian nasabah = total rugi - bagian bank
```

Nilai `modal bank` di sini adalah total pencairan berhasil pada akad. Prototipe
belum membagi kontribusi modal menurut waktu/periode laporan atau menghitung
perubahan modal sepanjang umur akad.

## 5. Alur proses bisnis terperinci

### Tahap A — Persiapan dan pengajuan

1. Demo membuat objek bank dan pegawai, lalu mendaftarkan pegawai ke bank.
2. Nasabah dan profil keuangannya dibuat.
3. Usaha dibuat dengan nasabah sebagai pemilik, lalu ditambahkan ke daftar
   usaha nasabah.
4. Rekening dibuat dan didaftarkan ke nasabah.
5. Nasabah membuat `PengajuanPembiayaan` untuk salah satu usahanya.
6. Konstruktor memvalidasi relasi pemilik usaha, nominal, tenor, jenis akad,
   nisbah, serta aturan modal untuk akad terpilih.
7. Status awal menjadi `DIAJUKAN` dan node riwayat awal dibuat.

### Tahap B — Analisis dan keputusan

1. `PengajuanService.mulaiAnalisis(...)` mengubah status ke `DIPROSES` dan
   menambahkan catatan riwayat.
2. `AnalisisKelayakan` dibuat dan dicatat ke pengajuan.
3. `KeputusanPembiayaan` dibuat setelah analisis tersedia.
4. `PengajuanService.catatKeputusan(...)` mengubah status menjadi
   `DISETUJUI` atau `DITOLAK`, kemudian mencatat transisi tersebut di riwayat.
5. Jika ditolak, tidak ada akad yang dibuat.
6. Jika disetujui, `AkadService` memilih subtype akad dan memastikan satu
   pengajuan tidak menerima akad kedua.

### Tahap C — Penandatanganan dan pencairan

1. Akad baru dimulai pada status `DRAFT`.
2. `tandatangani(...)` mengubah status ke `MENUNGGU_PENCAIRAN`.
3. `PencairanService` mendaftarkan pencairan dengan rekening tujuan milik
   nasabah yang mengajukan.
4. Total pencairan yang belum gagal tidak boleh melebihi jumlah persetujuan.
5. Jika berhasil, saldo rekening dikredit, referensi transaksi disimpan, dan
   akad diaktifkan menjadi `AKTIF`.
6. Jika gagal, pencairan menjadi `GAGAL` dan saldo tidak berubah.
7. Untuk Musyarakah, modal aktual nasabah dicatat setelah akad aktif dan
   sebelum perhitungan kerugian.

Status akad yang tersedia:

```text
DRAFT -> MENUNGGU_PENCAIRAN -> AKTIF -> SELESAI
                                  \-> DITERMINASI -> SELESAI
```

`DITERMINASI` berarti aktivitas baru dihentikan; itu bukan berarti kewajiban
otomatis dihapus. Pembayaran sisa kewajiban masih dapat dilakukan.

### Tahap D — Pelaporan dan verifikasi usaha

1. `LaporanUsaha` hanya dapat dibuat untuk akad `AKTIF`.
2. Pendapatan dan biaya tidak boleh negatif.
3. Laporan dimulai dari status `TERKIRIM`.
4. `mulaiVerifikasi()` mengubah status menjadi `DALAM_VERIFIKASI`.
5. Pembuatan `VerifikasiLaporan` menetapkan hasil dan mengubah status laporan:
   - diterima: `TERVERIFIKASI`;
   - perlu perbaikan: `PERLU_PERBAIKAN`.
6. Kode belum menyediakan pengeditan dan pengiriman ulang laporan yang perlu
   perbaikan.

### Tahap E — Pemrosesan laba, impas, atau rugi

Hitung:

```text
hasil usaha = pendapatan - biaya
```

- **Laba positif:** buat `PerhitunganBagiHasil`, catat pada akad, dan gunakan
  hasil untuk pembayaran bagi hasil bank.
- **Nol/impas:** tidak membuat perhitungan bagi hasil maupun objek rugi. Tidak
  ada distribusi dari hasil periode tersebut.
- **Rugi negatif:** buat `KerugianUsaha`, lalu daftarkan pada akad. Kerugian
  yang didaftarkan akan mengurangi sisa pengembalian modal bank menurut bagian
  kerugian bank.

Laporan, hasil verifikasi, dan hasil perhitungan/kerugian terkait satu sama
lain melalui referensi objek. Akad menyimpan hasil bagi hasil dan rugi, tetapi
belum menyimpan daftar laporan dan verifikasi sebagai koleksi tersendiri.

### Tahap F — Evaluasi kelalaian atau pelanggaran

1. Evaluasi dibuat untuk kerugian usaha yang sudah dicatat pada akad.
2. Hasil dapat berupa `DALAM_PEMERIKSAAN`, `TIDAK_TERBUKTI`, atau `TERBUKTI`.
3. Evaluasi yang masih diperiksa dapat ditutup dengan
   `selesaikanPemeriksaan(...)`.
4. Hasil akhir hanya dapat berupa `TERBUKTI` atau `TIDAK_TERBUKTI`.
5. Ganti rugi harus nol ketika tidak terbukti dan harus positif ketika
   terbukti. Nominalnya tidak boleh melebihi total kerugian pada laporan.
6. Evaluasi tidak mengubah `bagianKerugianBank` maupun
   `bagianKerugianNasabah`. Ganti rugi menjadi kewajiban terpisah.
7. Akad tidak dapat diselesaikan saat masih ada evaluasi `DALAM_PEMERIKSAAN`.

### Tahap G — Pembayaran dan penyelesaian

1. `PembayaranService` mendaftarkan pembayaran pada akad.
2. Pembayaran dapat berisi satu atau beberapa komponen yang bernilai positif:
   pengembalian modal, bagian bagi hasil bank, dan ganti rugi.
3. Total pengembalian modal tidak boleh melebihi sisa modal bank setelah
   memperhitungkan kerugian bank.
4. Pembayaran bagi hasil tidak boleh melebihi sisa bagian bank untuk
   perhitungan yang dirujuk.
5. Ganti rugi hanya boleh dibayar jika evaluasi terkait tercatat di akad,
   hasilnya `TERBUKTI`, dan jumlahnya tidak melebihi sisa ganti rugi.
6. Rekening sumber harus milik nasabah yang terkait dengan akad dan saldo
   harus cukup.
7. Pembayaran berhasil mendebit rekening serta dicatat dengan referensi
   transaksi. Pembayaran gagal tidak mendebit saldo.
8. Pembayaran dapat dilakukan bertahap. Setelah penghentian dini, pembayaran
   tetap diizinkan untuk menyelesaikan kewajiban yang tersisa.
9. Akad dapat menjadi `SELESAI` jika:
   - sisa pengembalian modal nol;
   - sisa bagi hasil bank nol;
   - sisa ganti rugi nol;
   - tidak ada pencairan atau pembayaran berstatus `DICATAT`; dan
   - tidak ada evaluasi yang masih dalam pemeriksaan.

## 6. Peran package dan service

| Lokasi | Tanggung jawab |
|---|---|
| `src/model` | Entitas domain, relasi objek, validasi, perubahan status, dan perhitungan. |
| `src/service/PengajuanService.java` | Memulai analisis dan menerapkan keputusan ke status pengajuan. |
| `src/service/AkadService.java` | Membuat subtype akad dari pengajuan yang disetujui. |
| `src/service/PencairanService.java` | Mendaftarkan pencairan dan mencatat hasil prosesnya. |
| `src/service/PembayaranService.java` | Mendaftarkan pembayaran dan mencatat berhasil/gagal. |
| `src/enums` | Nilai terbatas untuk jenis akad, status, hasil verifikasi, dan temuan. |
| `src/Main.java` | Titik masuk demo; merangkai pembuatan objek, pemanggilan service, dan output. |

Service berperan sebagai penghubung operasi, sedangkan validasi utama dan
perubahan saldo/status tetap dilakukan oleh objek domain.

## 7. Skenario demo yang disiapkan

### Skenario yang saat ini aktif

`Main` menjalankan satu skenario:

1. Pengajuan Mudharabah Rp80.000.000 dengan nisbah bank 40% dan nasabah 60%.
2. Pencairan berhasil seluruhnya.
3. Laporan usaha menunjukkan rugi Rp10.000.000.
4. Rugi normal Mudharabah dibebankan kepada bank dalam model ini; sisa modal
   yang dikembalikan menjadi Rp70.000.000.
5. Pemeriksaan kelalaian dimulai, lalu hasil contoh ditetapkan `TERBUKTI`
   dengan ganti rugi Rp2.000.000.
6. Akad dihentikan dini. Pembayaran tetap berjalan.
7. Nasabah membayar Rp70.000.000 sebagai pengembalian modal dan Rp2.000.000
   sebagai ganti rugi.
8. Sisa kewajiban tercatat nol dan akad diselesaikan.

Saldo rekening adalah simulasi arus dana pada satu rekening nasabah. Nilainya
tidak mencerminkan pembukuan lengkap bank dan tidak membuktikan penyelesaian
finansial di sistem nyata.

### Skenario alternatif dalam komentar

`Main.java` juga menyediakan potongan alternatif untuk:

- akad Musyarakah beserta pencatatan modal nasabah;
- laporan untung dan perhitungan bagi hasil;
- laporan impas tanpa distribusi;
- pembayaran yang disesuaikan dengan kewajiban skenario terkait.

Blok-blok itu adalah contoh kode yang dapat diaktifkan. Satu eksekusi `Main`
belum menjalankan semua alternatif sekaligus; ketika berpindah skenario,
blok laporan/evaluasi/pembayaran harus disesuaikan bersama agar tidak
mencampur objek atau nilai dari skenario berbeda.

## 8. Cara menjalankan

Dari PowerShell di folder proyek:

```powershell
$build = Join-Path $env:TEMP 'java-project-build'
New-Item -ItemType Directory -Force -Path $build | Out-Null
$files = Get-ChildItem -Path 'src' -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }
javac -Xlint:all -d $build $files
java -cp $build Main
```

Output akan menampilkan objek dan tahap demo secara berurutan. Kode keluar
kompilasi bukan pengganti pengujian terpisah untuk semua alternatif.

## 9. Batasan dan pengembangan berikutnya

Beberapa batasan sengaja dipertahankan agar demo tetap sederhana:

- Input masih hardcode di `Main`; tanggal dan waktu berupa `String`.
- Penyimpanan objek hanya di memori. Tidak ada database atau serialisasi.
- Belum ada test suite aktif; perubahan aturan perlu dicoba dengan skenario
  kompilasi/eksekusi yang sesuai.
- Tidak ada ID generator atau pemeriksaan keunikan lintas seluruh sistem;
  beberapa model hanya memeriksa duplikasi dalam koleksi akad tertentu.
- Riwayat linked list hanya mencakup status pengajuan; belum ada log umum
  untuk pencairan, pembayaran, perubahan akad, atau perubahan evaluasi.
- Tidak ada fasilitas laporan ulang ketika verifikasi meminta perbaikan.
- Penghitungan sisa modal menjumlahkan pencairan, alokasi kerugian bank, dan
  pembayaran berhasil; belum merekonstruksi posisi modal berdasarkan urutan
  waktu setiap transaksi/periode.
- Saldo rekening hanya satu sisi simulasi. Belum ada ledger bank atau transaksi
  atomik yang menjamin pembaruan saldo dan catatan transaksi bersama-sama.
- Penghentian dini saat ini memindahkan akad ke `DITERMINASI`; pembayaran
  diperbolehkan untuk melunasi kewajiban, sementara operasi baru seperti
  pencatatan laporan atau kerugian tetap mensyaratkan akad aktif.
- Asumsi perhitungan nisbah, pembulatan, alokasi rugi, dan nominal ganti rugi
  mengikuti kode demo serta perlu ditinjau sebelum dipakai untuk kebutuhan
  produk sebenarnya.

Diagram visual struktur kelas dan alur tersedia di
[UML-DAN-FLOWCHART.md](./UML-DAN-FLOWCHART.md). Untuk ringkasan singkat proyek
dan langkah menjalankan, lihat [README.md](./README.md).
