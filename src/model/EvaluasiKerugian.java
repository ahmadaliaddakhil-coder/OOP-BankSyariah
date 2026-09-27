package model;

import enums.HasilEvaluasiKerugian;
import enums.JenisTemuanKerugian;
import enums.StatusAkad;

public class EvaluasiKerugian {
    private final String idEvaluasi;
    private final KerugianUsaha kerugian;
    private final PegawaiBank pegawaiPemeriksa;
    private final String tanggalEvaluasi;
    private final JenisTemuanKerugian jenisTemuan;
    private HasilEvaluasiKerugian hasil;
    private long jumlahGantiRugi;
    private final String catatan;

    public EvaluasiKerugian(
            String idEvaluasi,
            KerugianUsaha kerugian,
            PegawaiBank pegawaiPemeriksa,
            String tanggalEvaluasi,
            JenisTemuanKerugian jenisTemuan,
            HasilEvaluasiKerugian hasil,
            long jumlahGantiRugi,
            String catatan) {
        if (idEvaluasi == null || idEvaluasi.trim().isEmpty()) {
            throw new IllegalArgumentException("ID evaluasi wajib diisi");
        }
        if (kerugian == null) {
            throw new IllegalArgumentException("Kerugian usaha tidak boleh null");
        }
        if (pegawaiPemeriksa == null) {
            throw new IllegalArgumentException("Pegawai pemeriksa tidak boleh null");
        }
        if (pegawaiPemeriksa.getBank() != kerugian.getAkad().getBank()) {
            throw new IllegalArgumentException(
                    "Pegawai pemeriksa harus berasal dari bank pada akad");
        }
        if (tanggalEvaluasi == null || tanggalEvaluasi.trim().isEmpty()) {
            throw new IllegalArgumentException("Tanggal evaluasi wajib diisi");
        }
        if (jenisTemuan == null) {
            throw new IllegalArgumentException("Jenis temuan tidak boleh null");
        }
        if (hasil == null) {
            throw new IllegalArgumentException("Hasil evaluasi tidak boleh null");
        }
        if (catatan == null || catatan.trim().isEmpty()) {
            throw new IllegalArgumentException("Catatan evaluasi wajib diisi");
        }
        if (jumlahGantiRugi < 0
                || (hasil == HasilEvaluasiKerugian.TERBUKTI
                        && jumlahGantiRugi == 0)
                || (hasil != HasilEvaluasiKerugian.TERBUKTI
                        && jumlahGantiRugi != 0)) {
            throw new IllegalArgumentException(
                    "Ganti rugi hanya boleh bernilai positif jika temuan terbukti");
        }
        if (jumlahGantiRugi > kerugian.getJumlahKerugian()) {
            throw new IllegalArgumentException(
                    "Ganti rugi tidak boleh melebihi jumlah kerugian laporan");
        }

        this.idEvaluasi = idEvaluasi;
        this.kerugian = kerugian;
        this.pegawaiPemeriksa = pegawaiPemeriksa;
        this.tanggalEvaluasi = tanggalEvaluasi;
        this.jenisTemuan = jenisTemuan;
        this.hasil = hasil;
        this.jumlahGantiRugi = jumlahGantiRugi;
        this.catatan = catatan;
    }

    public String getIdEvaluasi() {
        return idEvaluasi;
    }

    public KerugianUsaha getKerugian() {
        return kerugian;
    }

    public PegawaiBank getPegawaiPemeriksa() {
        return pegawaiPemeriksa;
    }

    public String getTanggalEvaluasi() {
        return tanggalEvaluasi;
    }

    public JenisTemuanKerugian getJenisTemuan() {
        return jenisTemuan;
    }

    public HasilEvaluasiKerugian getHasil() {
        return hasil;
    }

    public long getJumlahGantiRugi() {
        return jumlahGantiRugi;
    }

    public void selesaikanPemeriksaan(
            HasilEvaluasiKerugian hasilAkhir,
            long jumlahGantiRugi) {
        if (hasil != HasilEvaluasiKerugian.DALAM_PEMERIKSAAN) {
            throw new IllegalStateException(
                    "Hanya evaluasi dalam pemeriksaan yang dapat diselesaikan");
        }
        StatusAkad statusAkad = kerugian.getAkad().getStatus();
        if (statusAkad != StatusAkad.AKTIF
                && statusAkad != StatusAkad.DITERMINASI) {
            throw new IllegalStateException(
                    "Pemeriksaan hanya dapat diselesaikan sebelum akad ditutup");
        }
        if (hasilAkhir == null
                || hasilAkhir == HasilEvaluasiKerugian.DALAM_PEMERIKSAAN) {
            throw new IllegalArgumentException(
                    "Hasil akhir harus TERBUKTI atau TIDAK_TERBUKTI");
        }
        if (jumlahGantiRugi < 0
                || (hasilAkhir == HasilEvaluasiKerugian.TERBUKTI
                        && jumlahGantiRugi == 0)
                || (hasilAkhir == HasilEvaluasiKerugian.TIDAK_TERBUKTI
                        && jumlahGantiRugi != 0)) {
            throw new IllegalArgumentException(
                    "Ganti rugi harus positif untuk temuan terbukti dan 0 jika tidak terbukti");
        }
        if (jumlahGantiRugi > kerugian.getJumlahKerugian()) {
            throw new IllegalArgumentException(
                    "Ganti rugi tidak boleh melebihi jumlah kerugian laporan");
        }

        this.hasil = hasilAkhir;
        this.jumlahGantiRugi = jumlahGantiRugi;
    }

    public String getCatatan() {
        return catatan;
    }

    @Override
    public String toString() {
        return "Evaluasi Kerugian"
                + "\nID evaluasi: " + idEvaluasi
                + "\nID kerugian: " + kerugian.getIdKerugian()
                + "\nPegawai pemeriksa: " + pegawaiPemeriksa.getNama()
                + "\nTanggal evaluasi: " + tanggalEvaluasi
                + "\nJenis temuan: " + jenisTemuan
                + "\nHasil: " + hasil
                + "\nGanti rugi ditetapkan: Rp" + jumlahGantiRugi
                + "\nCatatan: " + catatan
                + "\nAlokasi rugi normal tetap terpisah dari ganti rugi.";
    }
}
