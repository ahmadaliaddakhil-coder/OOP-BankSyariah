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
  +memilikiPegawai(PegawaiBank) boolean
}

class PegawaiBank {
  -String nomorPegawai
  -String nama
  -String jabatan
  -BankSyariah bank
}

class Nasabah {
  -String nomorId
  -String nama
  -String alamat
  -String tanggalLahir
  -String pekerjaan
  -List~Usaha~ daftarUsaha
  -List~PengajuanPembiayaan~ daftarPengajuan
  -List~RekeningNasabah~ daftarRekening
  +tambahUsaha(Usaha) void
  +tambahRekening(RekeningNasabah) void
  +ajukanPembiayaan(PengajuanPembiayaan) void
}

class Usaha {
  -String idUsaha
  -String namaUsaha
  -String sektor
  -String alamat
  -Nasabah pemilik
}

class RekeningNasabah {
  -String nomorRekening
  -Nasabah pemilik
  -long saldo
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
  -StatusPengajuan status
  -RiwayatPengajuan riwayatPertama
  -RiwayatPengajuan riwayatTerakhir
  -AnalisisKelayakan analisisKelayakan
  -AkadPembiayaan akad
  -PegawaiBank pegawaiPenganalisis
  +catatAnalisis(AnalisisKelayakan) void
  +getAnalisisKelayakan() AnalisisKelayakan
  +mulaiAnalisis(PegawaiBank, String) void
  +catatKeputusan(KeputusanPembiayaan) void
  +buatAkad(String, KeputusanPembiayaan, String) AkadPembiayaan
  -ubahStatus(StatusPengajuan, String, String, String) void
  +getRiwayatPertama() RiwayatPengajuan
}

class RiwayatPengajuan {
  -String waktu
  -StatusPengajuan statusSebelumnya
  -StatusPengajuan statusSesudahnya
  -String pelaku
  -String keterangan
  -RiwayatPengajuan berikutnya
  +getBerikutnya() RiwayatPengajuan
  ~setBerikutnya(RiwayatPengajuan) void
}

class AnalisisKelayakan {
  <<recommendation to bank staff>>
  -String idAnalisis
  -String tanggalAnalisis
  -PengajuanPembiayaan pengajuan
  -PegawaiBank pegawaiAnalisis
  -long omzetBulanan
  -long biayaLangsungBulanan
  -long biayaOperasionalBulanan
  -long kewajibanUsahaBulanan
  -String catatanRisiko
  +hitungLabaOperasionalBulanan() long
  +hitungArusKasTersediaBulanan() long
  +getRekomendasiInternal() Rekomendasi
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
  getJenisAkad() JenisAkad
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
PegawaiBank "0..*" --> "1" BankSyariah : bekerja pada
PengajuanPembiayaan "1" --> "0..1" AkadPembiayaan : memiliki akad
AkadPembiayaan <|-- AkadMudharabah
AkadPembiayaan <|-- AkadMusyarakah
BankSyariah "1" o-- "0..*" PegawaiBank : pegawai
AkadPembiayaan "0..*" --> "1" BankSyariah : diterbitkan bank
AkadPembiayaan "0..*" --> "1" KeputusanPembiayaan : berdasarkan keputusan
AkadPembiayaan "1" *-- "0..1" Pencairan : satu kali pencairan
Pencairan "0..*" --> "1" RekeningNasabah : rekening tujuan

PengajuanPembiayaan ..> AkadMudharabah : membuat sesuai jenis akad
PengajuanPembiayaan ..> AkadMusyarakah : membuat sesuai jenis akad
PengajuanPembiayaan ..> Rekomendasi : menampilkan ke pegawai bank
AkadPembiayaan ..> RekeningNasabah : mengkredit saat pencairan
AkadPembiayaan ..> Pencairan : membuat catatan pencairan
Main ..> PengajuanPembiayaan : menjalankan alur pengajuan
Main ..> AkadPembiayaan : menjalankan alur akad dan pencairan

PengajuanPembiayaan ..> JenisAkad
PengajuanPembiayaan ..> StatusPengajuan
AnalisisKelayakan ..> Rekomendasi
RiwayatPengajuan ..> StatusPengajuan
KeputusanPembiayaan ..> StatusKeputusan
AkadPembiayaan ..> StatusAkad
```

Notasi: `*--` adalah komposisi, `o--` adalah agregasi, panah segitiga kosong
menunjukkan pewarisan, dan garis putus-putus menunjukkan ketergantungan.
Tanda `~` menunjukkan akses package-private. `getJenisAkad()` dideklarasikan
abstrak pada `AkadPembiayaan` dan diimplementasikan oleh kedua turunannya.

## Flowchart

```mermaid
flowchart TD
    A([Mulai]) --> B[Siapkan bank dan pegawai]
    B --> C[Buat nasabah, usaha, dan rekening]
    C --> D[Nasabah mengajukan pembiayaan untuk usahanya]
    D --> E{Data pengajuan valid?}
    E -- Tidak --> X([Perbaiki data sebelum proses dapat dilanjutkan])
    E -- Ya --> F[Status DIAJUKAN dan catat riwayat awal]
    F --> G[pengajuan.mulaiAnalisis pegawai bank terdaftar dan waktu]
    G --> H[Status DIPROSES dan tambahkan riwayat]
    H --> I[Catat snapshot omzet biaya dan kewajiban usaha]
    I --> I1[Hitung laba operasional dan arus kas tersedia]
    I1 --> I2[Tampilkan rekomendasi internal kepada pegawai]
    I2 --> J[Pegawai meninjau hasil dan menetapkan keputusan]
    J --> J1[pengajuan.catatKeputusan]
    J1 --> K{Disetujui?}
    K -- Tidak --> L[Status DITOLAK dan catat riwayat]
    L --> Z1([Selesai tanpa akad dan pencairan])
    K -- Ya --> M[Status DISETUJUI dan catat riwayat]
    M --> N{Jenis akad pada pengajuan?}
    N -- Mudharabah --> O[pengajuan.buatAkad membuat AkadMudharabah]
    N -- Musyarakah --> P[pengajuan.buatAkad membuat AkadMusyarakah dengan modal nasabah]
    O --> Q[Hubungkan akad ke pengajuan]
    P --> Q
    Q --> R[Tandatangani akad]
    R --> S[Status MENUNGGU_PENCAIRAN]
    S --> T[akad.cairkan satu kali sebesar jumlah disetujui]
    T --> U{Data pencairan valid dan rekening milik nasabah?}
    U -- Tidak --> V[Batalkan proses dengan pesan validasi]
    V --> X
    U -- Ya --> W[Kredit saldo rekening sebesar jumlah disetujui]
    W --> Y[Catat tanggal dan referensi pencairan]
    Y --> AA[Status akad DICAIRKAN]
    AA --> AB[Tampilkan akad, pencairan, saldo baru, dan riwayat]
    AB --> Z([Selesai setelah pencairan])
```

Rekomendasi adalah alat bantu internal bagi pegawai, bukan keputusan otomatis
dan bukan pesan persetujuan kepada nasabah. Rumus snapshot:
`laba operasional = omzet - biaya langsung - biaya operasional` dan
`arus kas tersedia = laba operasional - kewajiban usaha`. Hasil positif,
nol, dan negatif masing-masing menghasilkan rekomendasi
`LAYAK_DIPERTIMBANGKAN`, `PERLU_TINJAUAN`, dan `TIDAK_DIREKOMENDASIKAN`.
`Rekomendasi` adalah enum yang didefinisikan di dalam `AnalisisKelayakan`.

## Batas alur

- Satu akad hanya memiliki satu pencairan penuh.
- Pencairan menggunakan nominal keputusan yang disetujui; pengguna tidak
  mengisi nominal pencairan terpisah.
- Rekening nasabah dikredit saat pencairan berhasil.
- Setelah status akad `DICAIRKAN`, tidak ada proses lanjutan dalam cakupan
  program saat ini.
- Riwayat linked list hanya untuk perubahan status pengajuan.
