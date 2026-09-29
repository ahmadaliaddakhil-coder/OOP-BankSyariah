# Sistem Pembiayaan Modal Syariah

Demo Java untuk pengajuan pembiayaan usaha menggunakan akad Mudharabah atau
Musyarakah. Analisis menghitung laba operasional dan arus kas tersedia untuk
memberi rekomendasi internal kepada pegawai. Pegawai menetapkan keputusan;
alur demo berhenti setelah pencairan penuh ke rekening nasabah. Riwayat status
pengajuan dicatat dengan linked list.

## Menjalankan

Dari PowerShell pada folder proyek:

```powershell
$build = Join-Path $env:TEMP 'java-project-build'
New-Item -ItemType Directory -Force -Path $build | Out-Null
$files = Get-ChildItem -Path 'src' -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }
javac -Xlint:all -d $build $files
java -cp $build Main
```

## Dokumentasi

- [README-SISTEM.md](./README-SISTEM.md): gambaran sistem, cakupan, aturan,
  alur proses, class, dan cara menjalankan.
- [UML-DAN-FLOWCHART.md](./UML-DAN-FLOWCHART.md): class diagram dan flowchart
  Mermaid.
