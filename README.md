# Sistem Pembiayaan Syariah

Demo Java sederhana untuk alur pembiayaan usaha dengan akad Mudharabah dan
Musyarakah. Data disimpan dalam memori dan dibuat langsung di `Main`; belum
menggunakan database.

## Menjalankan

Dari folder proyek, kompilasi dan jalankan:

```powershell
$build = Join-Path $env:TEMP 'java-project-build'
New-Item -ItemType Directory -Force -Path $build | Out-Null
$files = Get-ChildItem -Path 'src' -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }
javac -Xlint:all -d $build $files
java -cp $build Main
```

## Alur demo aktif

`Main` memperlihatkan pengajuan, analisis, keputusan, akad Mudharabah,
pencairan, laporan rugi, evaluasi kelalaian yang terbukti, penghentian dini,
pembayaran sisa modal dan ganti rugi, lalu penyelesaian akad.

Ganti rugi adalah nominal yang ditetapkan pemeriksa. Ganti rugi yang terbukti
dicatat terpisah dari alokasi kerugian usaha normal dan harus dibayar sebelum
akad dapat diselesaikan. Penghentian dini menghentikan aktivitas baru pada
akad, tetapi pembayaran kewajiban yang masih ada tetap dapat dilakukan.
Evaluasi yang masih dalam pemeriksaan harus diberi hasil akhir sebelum akad
dapat diselesaikan.

Contoh alternatif Musyarakah, laporan untung/impas, dan skenario pembayaran
lain tersedia sebagai komentar di `src/Main.java`. Aktifkan hanya satu
skenario yang sesuai saat mencoba demo.

## Diagram sistem

Lihat [UML-DAN-FLOWCHART.md](./UML-DAN-FLOWCHART.md) untuk class diagram
lengkap dan flowchart alur bisnis, termasuk cabang alternatif serta batas
fungsi yang belum tersedia.

## Penjelasan sistem

Untuk gambaran sistem secara menyeluruh—domain, akad, aturan perhitungan,
workflow, peran class/service, contoh skenario, serta keterbatasannya—baca
[README-SISTEM.md](./README-SISTEM.md).

## Struktur source

- `src/model`: entitas dan aturan domain.
- `src/service`: operasi untuk pengajuan, akad, pencairan, dan pembayaran.
- `src/enums`: status dan jenis yang digunakan oleh model.
