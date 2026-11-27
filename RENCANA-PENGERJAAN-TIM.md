# Rencana Pengerjaan Tim OOP-BankSyariah

Dokumen ini merangkum urutan pengerjaan issue Linear OOP-1 sampai OOP-31,
dependency antar-issue, dan pembagian kerja yang disarankan untuk tiga anggota
tim: Ahmad Ali, Ghazaly, dan Husni.

## Ringkasan

- Kerjakan fondasi lebih dahulu, lalu fitur yang bergantung padanya.
- Beberapa pekerjaan dapat berjalan paralel, tetapi jangan mulai issue sebelum
  dependency wajibnya selesai.
- Status Linear yang terakhir diperiksa: seluruh 31 issue berstatus **Ready**
  (belum dimulai). Status tersebut tidak membuktikan bahwa kode belum ada atau
  sudah selesai; gunakan Definition of Done (DoD) issue untuk verifikasi.
- Dependency saat ini ditulis di deskripsi issue. Relasi blocker Linear belum
  tercatat pada issue yang diperiksa, termasuk OOP-10, OOP-17, dan OOP-28.
- Dokumentasi sistem menyebut scope saat ini berakhir setelah pencairan,
  sedangkan OOP-18 sampai OOP-23 meminta fitur pascapencairan. Putuskan scope
  ini sebelum tim memulai kelompok tersebut.

## Pembagian fokus yang disarankan

Pembagian berikut mengikuti owner yang tertulis di sebagian besar issue. Bila
ada bottleneck, tim boleh membagi ulang pekerjaan setelah menyepakati interface
dan owner baru di Linear.

| Anggota | Fokus | Issue utama |
|---|---|---|
| **Ahmad Ali** | Fondasi, value object, keamanan, workflow, analisis, keputusan, bagi hasil, audit | OOP-1, OOP-3, OOP-5, OOP-6, OOP-7, OOP-13, OOP-14, OOP-20, OOP-22, OOP-26, OOP-28, OOP-29 |
| **Ghazaly** | Repository/seed, autentikasi, produk, akad, pencairan, pembayaran, tes E2E | OOP-2, OOP-4, OOP-9, OOP-15, OOP-17, OOP-21, OOP-23, OOP-25, OOP-30 |
| **Husni** | UI, pengajuan, dokumen, pemeriksaan, konfirmasi akad, pelaporan, akses nasabah, dokumentasi | OOP-8, OOP-10, OOP-11, OOP-12, OOP-16, OOP-18, OOP-19, OOP-24, OOP-27, OOP-31 |

## Urutan kerja dan dependency

### Tahap 0 — Fondasi

1. **OOP-1 — Setup proyek dan kerangka package**
   - Owner: Ahmad Ali.
   - Tidak memiliki dependency.
   - Sepakati struktur package dan kontrak dasar sebelum pekerjaan paralel
     dimulai.

### Tahap 1 — Model dasar

Setelah OOP-1:

- **OOP-2 — Repository generik, store in-memory, dan seed data** (Ghazaly).
- **OOP-3 — Value object Money dan Nisbah** (Ahmad Ali).

OOP-2 dan OOP-3 dapat dikerjakan paralel. OOP-4 dan OOP-6 menunggu OOP-2;
OOP-9 menunggu OOP-2 dan OOP-3.

### Tahap 2 — Keamanan, workflow, katalog, dan UI

Mulai setiap issue saat dependency-nya selesai:

- **OOP-4 — Login BCrypt dan sesi** (Ghazaly), setelah OOP-2.
- **OOP-6 — Audit log append-only** (Ahmad Ali), setelah OOP-2.
- **OOP-5 — RBAC di lapisan service** (Ahmad Ali), setelah OOP-4.
- **OOP-7 — State machine dan four-eyes** (Ahmad Ali), setelah OOP-2 dan OOP-6.
- **OOP-9 — Seed produk dan sektor usaha** (Ghazaly), setelah OOP-2 dan OOP-3.
- **OOP-8 — Shell UI JavaFX per peran** (Husni), setelah OOP-1 dan OOP-4.

Issue di atas memiliki dependency berbeda dan bisa tumpang tindih antaranggota.
Namun, anggota yang sama tetap mengerjakan tugasnya secara berurutan.

### Tahap 3 — Pengajuan dan analisis

- **OOP-10 — Pengajuan pembiayaan** (Husni), setelah OOP-3, OOP-5, OOP-7,
  dan OOP-9.
- Setelah OOP-10, kerjakan secara paralel:
  - **OOP-11 — Unggah dokumen dan SHA-256** (Husni).
  - **OOP-24 — Akses data sendiri dan masking PII** (Husni; dapat dialihkan
    kepada anggota lain agar paralel benar-benar efektif).
- **OOP-12 — Pemeriksaan kelengkapan dan survei** (Husni), setelah OOP-10 dan
  OOP-11.
- **OOP-13 — Analisis kelayakan dan kolektibilitas** (Ahmad Ali), setelah
  OOP-7 dan OOP-12.
- **OOP-14 — Keputusan komite: nisbah dan jumlah** (Ahmad Ali), setelah
  OOP-3 dan OOP-13.

### Tahap 4 — Akad dan pencairan

Urutan kritis:

1. **OOP-15 — Akad dengan snapshot** (Ghazaly), setelah OOP-14.
2. **OOP-16 — Konfirmasi akad dua pihak (SIMULASI)** (Husni), setelah OOP-15.
3. **OOP-17 — Pencairan dan ledger** (Ghazaly), setelah OOP-6, OOP-15, dan
   OOP-16.

OOP-17 adalah milestone penting: fitur laporan pascapencairan bergantung
padanya.

### Tahap 5 — Pascapencairan

Jalankan hanya setelah tim/PO menyetujui bahwa fitur pascapencairan masuk scope:

1. **OOP-18 — Laporan usaha bulanan** (Husni), setelah OOP-17.
2. **OOP-19 — Verifikasi laporan dan flag selisih** (Husni), setelah OOP-18.
3. **OOP-20 — Hitung bagi hasil** (Ahmad Ali), setelah OOP-15 dan OOP-19.
4. Setelah OOP-20, kerjakan paralel:
   - **OOP-21 — Pembayaran pokok dan bagi hasil** (Ghazaly).
   - **OOP-22 — Penanganan rugi** (Ahmad Ali).
5. **OOP-23 — Pelunasan dan penutupan akad** (Ghazaly), setelah OOP-21.

### Tahap 6 — Hardening, audit, dan demo

- Setelah OOP-23, kerjakan paralel:
  - **OOP-25 — Tes alur end-to-end** (Ghazaly).
  - **OOP-26 — Sapu bersih float/double dan PII** (Ahmad Ali).
  - **OOP-27 — README, UML, dan matriks kesetiaan** (Husni).
- **OOP-28 — Audit checkpoint akhir** (Ahmad Ali), setelah seluruh B1–B10,
  yaitu OOP-18 sampai OOP-27, selesai.
- Setelah pekerjaan fitur dan audit siap, kerjakan latihan demo:
  - **OOP-29 — Latihan demo Ahmad**.
  - **OOP-30 — Latihan demo Ghazaly**.
  - **OOP-31 — Latihan demo Husni**.

Ketiga latihan dapat dilakukan paralel setelah skenario demo dan build yang
akan dipresentasikan sudah stabil.

## Daftar dependency issue

Dependency di bawah mengikuti teks pada deskripsi Linear:

| Issue | Menunggu issue |
|---|---|
| OOP-1 | — |
| OOP-2, OOP-3 | OOP-1 |
| OOP-4, OOP-6 | OOP-2 |
| OOP-5 | OOP-4 |
| OOP-7 | OOP-2, OOP-6 |
| OOP-8 | OOP-1, OOP-4 |
| OOP-9 | OOP-2, OOP-3 |
| OOP-10 | OOP-3, OOP-5, OOP-7, OOP-9 |
| OOP-11 | OOP-10 |
| OOP-12 | OOP-10, OOP-11 |
| OOP-13 | OOP-7, OOP-12 |
| OOP-14 | OOP-3, OOP-13 |
| OOP-15 | OOP-14 |
| OOP-16 | OOP-15 |
| OOP-17 | OOP-6, OOP-15, OOP-16 |
| OOP-18 | OOP-17 |
| OOP-19 | OOP-18 |
| OOP-20 | OOP-15, OOP-19 |
| OOP-21 | OOP-17, OOP-20 |
| OOP-22 | OOP-20 |
| OOP-23 | OOP-21 |
| OOP-24 | OOP-10 |
| OOP-25, OOP-26, OOP-27 | OOP-23 |
| OOP-28 | OOP-18 sampai OOP-27 |
| OOP-29, OOP-30, OOP-31 | Semua pekerjaan sebelumnya sesuai deskripsi issue |

**Tindak lanjut Linear:** tambahkan relasi `blocked by` berdasarkan tabel ini
agar dependency terlihat di board/issue, bukan hanya tertulis dalam deskripsi.
Jangan menandai issue sebagai Done sebelum DoD issue terpenuhi.

## Aturan status dan review

- Gunakan **In Progress** saat pekerjaan benar-benar dimulai.
- Gunakan **In Review** saat implementasi siap diperiksa.
- Gunakan **Done** hanya setelah DoD terukur lulus dan reviewer/PO menyetujui.
- Gunakan **Ready** untuk pekerjaan yang siap diambil dan belum dimulai.
- Jika dependency belum selesai, jangan mulai pekerjaan yang terblokir; catat
  relasi blocker di Linear.
- Setiap perubahan penting harus memiliki tes yang relevan dan review anggota
  lain.
- Untuk issue yang memiliki DoD umum, periksa juga kebutuhan seperti tes
  normal/batas, larangan float/double untuk uang dan nisbah, pemeriksaan akses
  di service, audit perubahan penting, serta label **SIMULASI** untuk fitur
  simulasi.

## Keputusan scope yang diperlukan

README sistem saat ini menyatakan alur selesai setelah pencairan. Sebaliknya,
OOP-18 sampai OOP-23 meminta laporan bulanan, verifikasi, bagi hasil,
pembayaran, penanganan rugi, dan pelunasan.

Sebelum Tahap 5, PO/tim perlu memilih salah satu:

1. **Scope diperluas:** lanjutkan OOP-18 sampai OOP-23 sesuai urutan di atas dan
   perbarui dokumentasi sistem.
2. **Scope tetap sampai pencairan:** tunda atau keluarkan OOP-18 sampai OOP-23
   melalui keputusan PO, lalu sesuaikan dependency OOP-25 sampai OOP-28.

Jangan menandai fitur pascapencairan sebagai selesai hanya karena diputuskan
untuk tidak dikerjakan; dokumentasikan keputusan scope dan status issue secara
jujur di Linear.
