# UML dan Flowchart Pembiayaan Syariah

Diagram ini menggambarkan cakupan source saat ini: pengajuan pembiayaan
Mudharabah/Musyarakah, riwayat status pengajuan, persetujuan, akad, dan satu
kali pencairan penuh ke rekening nasabah. Proses berakhir setelah saldo
rekening bertambah.

## Class diagram

```mermaid
classDiagram
direction LR

class Main {
  +main(String[] args) void
}

class BankSyariah {
  -String kodeBank
  -String namaBank
  -List~PegawaiBank~ daftarPegawai
  +tambahPegawai(PegawaiBank) void
  +getKodeBank() String
  +getNamaBank() String
  +getDaftarPegawai() List~PegawaiBank~
  +memilikiPegawai(PegawaiBank) boolean
}

class PegawaiBank {
  -String nomorPegawai
  -String nama
  -String jabatan
  -BankSyariah bank
  +getNomorPegawai() String
  +getNama() String
  +getJabatan() String
  +getBank() BankSyariah
}

class Nasabah {
  -String nomorId
  -String nama
  -String alamat
  -String tanggalLahir
  -String pekerjaan
  -long gajiBulanan
  -List~Usaha~ daftarUsaha
  -List~PengajuanPembiayaan~ daftarPengajuan
  -List~RekeningNasabah~ daftarRekening
  +tambahUsaha(Usaha) void
  +tambahRekening(RekeningNasabah) void
  +ajukanPembiayaan(PengajuanPembiayaan) void
  +getDaftarUsaha() List~Usaha~
  +getDaftarPengajuan() List~PengajuanPembiayaan~
  +getDaftarRekening() List~RekeningNasabah~
  +getIdPihak() String
  +getNamaPihak() String
  +getAlamatPihak() String
  +getTanggalLahirPihak() String
  +getPekerjaanPihak() String
  +getGajiBulanan() long
}

class Usaha {
  -String idUsaha
  -String namaUsaha
  -String sektor
  -String alamat
  -Nasabah pemilik
  +getIdUsaha() String
  +getNamaUsaha() String
  +getSektor() String
  +getAlamat() String
  +getPemilik() Nasabah
}

class RekeningNasabah {
  -String nomorRekening
  -Nasabah pemilik
  -long saldo
  +getNomorRekening() String
  +getPemilik() Nasabah
  +getSaldo() long
  ~kredit(long) void
}

class PengajuanPembiayaan {
  -String idPengajuan
  -Nasabah nasabah
  -Usaha usaha
  -long nominal
  -int tenorBulan
  -String tujuanPenggunaan
  -String tanggalPengajuan
  -JenisAkad jenisAkad
  -int nisbahBank
  -int nisbahNasabah
  -long modalNasabah
  -long omzetBulananDilaporkan
  -long biayaLangsungBulananDilaporkan
  -long biayaOperasionalBulananDilaporkan
  -long kewajibanUsahaBulananDilaporkan
  -StatusPengajuan status
  -RiwayatPengajuan riwayatPertama
  -RiwayatPengajuan riwayatTerakhir
  -AnalisisKelayakan analisisKelayakan
  -AkadPembiayaan akad
  -PegawaiBank pegawaiPenganalisis
  +getStatus() StatusPengajuan
  +getNasabah() Nasabah
  +getUsaha() Usaha
  +getNominal() long
  +getTenorBulan() int
  +getTujuanPenggunaan() String
  +getTanggalPengajuan() String
  +getJenisAkad() JenisAkad
  +getNisbahBank() int
  +getNisbahNasabah() int
  +getModalNasabah() long
  +getOmzetBulananDilaporkan() long
  +getBiayaLangsungBulananDilaporkan() long
  +getBiayaOperasionalBulananDilaporkan() long
  +getKewajibanUsahaBulananDilaporkan() long
  +getRiwayatPertama() RiwayatPengajuan
  +catatAnalisis(AnalisisKelayakan) void
  +getAnalisisKelayakan() AnalisisKelayakan
  +mulaiAnalisis(PegawaiBank, String) void
  +catatKeputusan(KeputusanPembiayaan) void
  +buatAkad(String, KeputusanPembiayaan, String) AkadPembiayaan
  -ubahStatus(StatusPengajuan, String, String, String) void
  +getAkad() AkadPembiayaan
}

class RiwayatPengajuan {
  -String waktu
  -StatusPengajuan statusSebelumnya
  -StatusPengajuan statusSesudahnya
  -String pelaku
  -String keterangan
  -RiwayatPengajuan berikutnya
  +getWaktu() String
  +getStatusSebelumnya() StatusPengajuan
  +getStatusSesudahnya() StatusPengajuan
  +getPelaku() String
  +getKeterangan() String
  +getBerikutnya() RiwayatPengajuan
  ~setBerikutnya(RiwayatPengajuan) void
}

class AnalisisKelayakan {
  -String idAnalisis
  -String tanggalAnalisis
  -PengajuanPembiayaan pengajuan
  -PegawaiBank pegawaiAnalisis
  -long omzetBulanan
  -long biayaLangsungBulanan
  -long biayaOperasionalBulanan
  -long kewajibanUsahaBulanan
  -String catatanRisiko
  +getIdAnalisis() String
  +getTanggalAnalisis() String
  +getPengajuan() PengajuanPembiayaan
  +getPegawaiAnalisis() PegawaiBank
  +getOmzetBulanan() long
  +getBiayaLangsungBulanan() long
  +getBiayaOperasionalBulanan() long
  +getKewajibanUsahaBulanan() long
  +hitungLabaOperasionalBulanan() long
  +hitungArusKasTersediaBulanan() long
  +getRekomendasiInternal() Rekomendasi
  +getCatatanRisiko() String
}

class Rekomendasi {
  <<enumeration>>
  LAYAK_DIPERTIMBANGKAN
  PERLU_TINJAUAN
  TIDAK_DIREKOMENDASIKAN
}

class KeputusanPembiayaan {
  -String idKeputusan
  -String tanggalKeputusan
  -PengajuanPembiayaan pengajuan
  -PegawaiBank pegawaiPemutus
  -StatusKeputusan statusKeputusan
  -long jumlahDisetujui
  -String alasan
  +getIdKeputusan() String
  +getTanggalKeputusan() String
  +getPengajuan() PengajuanPembiayaan
  +getPegawaiPemutus() PegawaiBank
  +getStatusKeputusan() StatusKeputusan
  +getJumlahDisetujui() long
  +getAlasan() String
}

class PembuatAkad {
  <<factory>>
  +buatAkad(String, PengajuanPembiayaan, KeputusanPembiayaan, String)$ AkadPembiayaan
}

class AkadPembiayaan {
  <<abstract>>
  -String nomorAkad
  -BankSyariah bank
  -KeputusanPembiayaan keputusan
  -String tanggalJatuhTempo
  -long jumlahDisetujui
  -int nisbahBank
  -int nisbahNasabah
  -String tanggalTandaTangan
  -StatusAkad status
  -Pencairan pencairan
  +getNomorAkad() String
  +getJenisAkad() JenisAkad
  +getStatus() StatusAkad
  +getJumlahDisetujui() long
  +getPencairan() Pencairan
  +getBank() BankSyariah
  +getKeputusan() KeputusanPembiayaan
  +getTanggalJatuhTempo() String
  +getNisbahBank() int
  +getNisbahNasabah() int
  +getTanggalTandaTangan() String
  +tandatangani(String) void
  +cairkan(String, String, String, RekeningNasabah) Pencairan
}

class AkadMudharabah {
  +getJenisAkad() JenisAkad
}

class AkadMusyarakah {
  -long modalNasabah
  +getModalNasabah() long
  +getJenisAkad() JenisAkad
}

class Pencairan {
  -String idPencairan
  -AkadPembiayaan akad
  -RekeningNasabah rekeningTujuan
  -long jumlah
  -String tanggalPencairan
  -String referensiTransaksi
  +getJumlah() long
  +getIdPencairan() String
  +getAkad() AkadPembiayaan
  +getTanggalPencairan() String
  +getRekeningTujuan() RekeningNasabah
  +getReferensiTransaksi() String
}

class JenisAkad {
  <<enumeration>>
  MUDHARABAH
  MUSYARAKAH
}

class StatusPengajuan {
  <<enumeration>>
  DIAJUKAN
  DIPROSES
  DISETUJUI
  DITOLAK
}

class StatusKeputusan {
  <<enumeration>>
  DISETUJUI
  DITOLAK
}

class StatusAkad {
  <<enumeration>>
  DRAFT
  MENUNGGU_PENCAIRAN
  DICAIRKAN
}

Nasabah "1" o-- "0..*" Usaha : memiliki
Nasabah "1" o-- "0..*" RekeningNasabah : mendaftarkan
Nasabah "1" o-- "0..*" PengajuanPembiayaan : mengajukan
Usaha "1" <-- "0..*" PengajuanPembiayaan : usaha yang dibiayai
PengajuanPembiayaan "1" *-- "1..*" RiwayatPengajuan : riwayat status
RiwayatPengajuan "1" --> "0..1" RiwayatPengajuan : node berikutnya
PengajuanPembiayaan "1" <-- "0..1" AnalisisKelayakan : dianalisis
AnalisisKelayakan "0..*" --> "1" PegawaiBank : analis
KeputusanPembiayaan "0..*" --> "1" PengajuanPembiayaan : memutus
KeputusanPembiayaan "0..*" --> "1" PegawaiBank : pemutus
PengajuanPembiayaan "1" --> "0..1" AkadPembiayaan : memiliki akad
AkadPembiayaan <|-- AkadMudharabah
AkadPembiayaan <|-- AkadMusyarakah
BankSyariah "1" o-- "0..*" PegawaiBank : pegawai
AkadPembiayaan "0..*" --> "1" BankSyariah : diterbitkan bank
AkadPembiayaan "0..*" --> "1" KeputusanPembiayaan : berdasarkan keputusan
AkadPembiayaan "1" *-- "0..1" Pencairan : satu kali pencairan
Pencairan "0..*" --> "1" RekeningNasabah : rekening tujuan

PengajuanPembiayaan ..> PembuatAkad : meminta pembuatan setelah disetujui
PembuatAkad ..> AkadMudharabah : memilih untuk Mudharabah
PembuatAkad ..> AkadMusyarakah : memilih untuk Musyarakah
PembuatAkad ..> JenisAkad : memilih subtype
AkadPembiayaan ..> RekeningNasabah : mengkredit saat pencairan
AkadPembiayaan ..> Pencairan : membuat catatan pencairan
Main ..> PengajuanPembiayaan : menjalankan alur pengajuan
Main ..> AkadPembiayaan : menjalankan alur akad dan pencairan

PengajuanPembiayaan ..> JenisAkad
PengajuanPembiayaan ..> StatusPengajuan
AnalisisKelayakan ..> Rekomendasi : enum internal dan rekomendasi pegawai
RiwayatPengajuan ..> StatusPengajuan
KeputusanPembiayaan ..> StatusKeputusan
AkadPembiayaan ..> StatusAkad
```

Notasi: `*--` adalah komposisi, `o--` adalah agregasi, panah segitiga kosong
menunjukkan pewarisan, dan garis putus-putus menunjukkan ketergantungan.
Tanda `~` menunjukkan akses package-private. `getJenisAkad()` dideklarasikan
abstrak pada `AkadPembiayaan` dan diimplementasikan oleh kedua turunannya.

## UML box factory untuk slide presentasi

Potongan ini dapat dipakai sebagai box `PembuatAkad` dan relasi langsungnya
pada slide class diagram:

```mermaid
classDiagram
class PengajuanPembiayaan {
  +buatAkad(String, KeputusanPembiayaan, String) AkadPembiayaan
}

class PembuatAkad {
  <<factory>>
  -PembuatAkad()
  +buatAkad(String, PengajuanPembiayaan, KeputusanPembiayaan, String)$ AkadPembiayaan
}

class JenisAkad {
  <<enumeration>>
  MUDHARABAH
  MUSYARAKAH
}

class AkadPembiayaan {
  <<abstract>>
}

class AkadMudharabah
class AkadMusyarakah

PengajuanPembiayaan ..> PembuatAkad : meminta setelah disetujui
PembuatAkad ..> JenisAkad : membaca jenis akad
PembuatAkad ..> AkadMudharabah : membuat untuk Mudharabah
PembuatAkad ..> AkadMusyarakah : membuat untuk Musyarakah
AkadPembiayaan <|-- AkadMudharabah
AkadPembiayaan <|-- AkadMusyarakah
```

Method factory bersifat static. `PengajuanPembiayaan.buatAkad(...)` tetap
menjadi pintu pemanggilan dari alur utama, sedangkan pemilihan subclass
didelegasikan ke `PembuatAkad`. Jika ada akad baru, daftar enum dan factory
perlu diperbarui bersama class akad baru; class pengajuan tidak perlu diberi
percabangan baru.

## Flowchart

Ringkasan berikut cocok untuk slide alur sistem; flowchart lengkap di bawahnya
menampilkan validasi dan perubahan status secara lebih rinci.

```mermaid
flowchart TD
    A([Mulai]) --> B[Siapkan bank dan pegawai terdaftar]
    B --> C[Nasabah isi data dan laporan keuangan bulanan]
    C --> D[Validasi dan simpan pengajuan]
    D --> E[Pegawai memeriksa laporan nasabah]
    E --> F[Catat analisis dan hasil perhitungan]
    F --> G[Tampilkan rekomendasi sebagai bahan pertimbangan]
    G --> H[Pegawai menetapkan keputusan pada langkah terpisah]
    H --> I{Disetujui?}
    I -- Tidak --> J[Catat status DITOLAK dan riwayat]
    J --> K([Selesai tanpa akad])
    I -- Ya --> L[Catat status DISETUJUI dan riwayat]
    L --> M[pengajuan.buatAkad meminta PembuatAkad]
    M --> N{Jenis akad}
    N -- Mudharabah --> O[Buat AkadMudharabah berstatus DRAFT]
    N -- Musyarakah --> P[Buat AkadMusyarakah berstatus DRAFT]
    O --> Q[Pegawai menandatangani pada langkah terpisah]
    P --> Q
    Q --> R[Akad MENUNGGU_PENCAIRAN]
    R --> S[Pegawai memproses pencairan]
    S --> T[Kredit rekening nasabah satu kali]
    T --> U[Tampilkan pencairan, saldo, dan riwayat]
    U --> V([Selesai])
```

```mermaid
flowchart TD
    A([Mulai]) --> B[Siapkan bank dan pegawai]
    B --> C[Buat nasabah, usaha, dan rekening]
    C --> D[Nasabah mengisi data usaha, keuangan bulanan, dan pengajuan]
    D --> E{Data pengajuan valid?}
    E -- Tidak --> X([Validasi gagal dan proses dihentikan])
    E -- Ya --> F[Status DIAJUKAN dan catat riwayat awal]
    F --> G[pengajuan.mulaiAnalisis pegawai bank terdaftar dan waktu]
    G --> H[Status DIPROSES dan tambahkan riwayat]
    H --> I[Pegawai memeriksa data keuangan yang dilaporkan nasabah]
    I --> I1[Gunakan data terverifikasi untuk mencatat analisis]
    I1 --> I2[Hitung laba operasional dan arus kas tersedia]
    I2 --> I3[Tampilkan rekomendasi internal kepada pegawai]
    I3 --> J[Pegawai meninjau hasil dan menetapkan keputusan]
    J --> J1[pengajuan.catatKeputusan]
    J1 --> K{Disetujui?}
    K -- Tidak --> L[Status DITOLAK dan catat riwayat]
    L --> Z1([Selesai tanpa akad dan pencairan])
    K -- Ya --> M[Status DISETUJUI dan catat riwayat]
    M --> N[pengajuan.buatAkad meminta PembuatAkad]
    N --> N1{Jenis akad pada pengajuan?}
    N1 -- Mudharabah --> O[PembuatAkad membuat AkadMudharabah]
    N1 -- Musyarakah --> P[PembuatAkad membuat AkadMusyarakah dengan modal nasabah]
    O --> Q[Pengajuan menyimpan referensi akad]
    P --> Q
    Q --> R[Tandatangani akad]
    R --> S[Status MENUNGGU_PENCAIRAN]
    S --> T[akad.cairkan satu kali sebesar jumlah disetujui]
    T --> U{Data pencairan valid dan rekening terdaftar milik nasabah?}
    U -- Tidak --> V[Validasi gagal dan proses dihentikan]
    V --> X
    U -- Ya --> W[Kredit saldo rekening sebesar jumlah disetujui]
    W --> Y[Catat tanggal dan referensi pencairan]
    Y --> AA[Status akad DICAIRKAN]
    AA --> AB[Tampilkan akad, pencairan, saldo baru, dan riwayat]
    AB --> Z([Selesai setelah pencairan])
```

Nasabah mengisi omzet, biaya langsung, biaya operasional, dan kewajiban usaha
bulanan ketika mengajukan pembiayaan. Nilai tersebut disimpan pada
`PengajuanPembiayaan`, ditampilkan kembali sebagai nilai laporan, lalu disalin
ke formulir analisis agar pegawai dapat memeriksa atau menyesuaikannya. Tidak
ada angka contoh yang otomatis dipakai dalam GUI. Rekomendasi adalah alat
bantu internal berdasarkan angka yang telah diperiksa pegawai, bukan keputusan
otomatis dan bukan pesan persetujuan kepada nasabah. Rumus:
`laba operasional = omzet - biaya langsung - biaya operasional` dan
`arus kas tersedia = laba operasional - kewajiban usaha`. Hasil positif,
nol, dan negatif masing-masing menghasilkan rekomendasi
`LAYAK_DIPERTIMBANGKAN`, `PERLU_TINJAUAN`, dan `TIDAK_DIREKOMENDASIKAN`.
`Rekomendasi` adalah enum yang didefinisikan di dalam `AnalisisKelayakan`.
Nilai analisis hanya ditujukan untuk pegawai bank. Pegawai pemutus harus
terdaftar pada bank yang menangani analisis; pegawai merupakan bagian dari
bank dan direlasikan langsung pada diagram.

GUI memisahkan aksi pemeriksaan, penetapan keputusan, pembuatan draft akad,
penandatanganan, dan pencairan. Dengan demikian status `DIPROSES`, akad
`DRAFT`, dan akad `MENUNGGU_PENCAIRAN` tetap terlihat sebagai tahap tersendiri
sebelum pengguna melanjutkan.

## Batas alur

- Satu akad hanya memiliki satu pencairan penuh.
- Pencairan menggunakan nominal keputusan yang disetujui; pengguna tidak
  mengisi nominal pencairan terpisah.
- Rekening nasabah dikredit saat pencairan berhasil.
- Setelah status akad `DICAIRKAN`, tidak ada proses lanjutan dalam cakupan
  program saat ini.
- Riwayat linked list hanya untuk perubahan status pengajuan.
