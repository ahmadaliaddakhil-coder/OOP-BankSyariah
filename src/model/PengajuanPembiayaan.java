package model;

import enums.JenisAkad;
import enums.StatusKeputusan;
import enums.StatusPengajuan;

/**
 * PengajuanPembiayaan
 */
public class PengajuanPembiayaan {

    private final String idPengajuan;
    // class
    private final Nasabah nasabah;
    private final Usaha usaha;

    private final long nominal;
    private final int tenorBulan;
    private final String tujuanPenggunaan;
    private final String tanggalPengajuan;

    // class
    private final JenisAkad jenisAkad;

    private final int nisbahBank;
    private final int nisbahNasabah;
    private final long modalNasabah;

    // class
    private StatusPengajuan status;
    private RiwayatPengajuan riwayatPertama;
    private RiwayatPengajuan riwayatTerakhir;
    private AnalisisKelayakan analisisKelayakan;
    private AkadPembiayaan akad;
    private PegawaiBank pegawaiPenganalisis;

    public PengajuanPembiayaan(
            String idPengajuan,
            Nasabah nasabah,
            Usaha usaha,
            long nominal,
            int tenorBulan,
            String tujuanPenggunaan,
            String tanggalPengajuan,
            JenisAkad jenisAkad,
            int nisbahBank,
            int nisbahNasabah,
            long modalNasabah) {

        if (nasabah == null) {
            throw new IllegalArgumentException("Nasabah tidak boleh null");
        }
        if (usaha == null) {
            throw new IllegalArgumentException("Usaha tidak boleh null");
        }
        if (usaha.getPemilik() != nasabah || !nasabah.getDaftarUsaha().contains(usaha)) {
            throw new IllegalArgumentException("Usaha tersebut bukan milik nasabah ini");
        }
        if (nominal <= 0) {
            throw new IllegalArgumentException("Nominal harus lebih besar dari 0");
        }
        if (tenorBulan <= 0) {
            throw new IllegalArgumentException("Tenor harus lebih besar dari 0");
        }
        if (jenisAkad == null) {
            throw new IllegalArgumentException("Jenis akad tidak boleh null");
        }
        if (nisbahBank < 0 || nisbahNasabah < 0 || nisbahBank + nisbahNasabah != 100) {
            throw new IllegalArgumentException("Nisbah bank dan nasabah harus valid dan totalnya 100");
        }
        if (modalNasabah < 0) {
            throw new IllegalArgumentException("Modal nasabah tidak boleh negatif");
        }
        if (jenisAkad == JenisAkad.MUSYARAKAH && modalNasabah == 0) {
            throw new IllegalArgumentException("Modal nasabah harus lebih besar dari 0 untuk akad musyarakah");
        }
        if (jenisAkad == JenisAkad.MUDHARABAH && modalNasabah != 0) {
            throw new IllegalArgumentException("Modal nasabah harus 0 untuk akad mudharabah");
        }

        this.idPengajuan = idPengajuan;
        this.nasabah = nasabah;
        this.usaha = usaha;
        this.nominal = nominal;
        this.tenorBulan = tenorBulan;
        this.tujuanPenggunaan = tujuanPenggunaan;
        this.tanggalPengajuan = tanggalPengajuan;
        this.jenisAkad = jenisAkad;
        this.nisbahBank = nisbahBank;
        this.nisbahNasabah = nisbahNasabah;
        this.modalNasabah = modalNasabah;
        this.status = StatusPengajuan.DIAJUKAN;

        this.riwayatPertama = new RiwayatPengajuan(tanggalPengajuan, null, this.status, nasabah.getNamaPihak(),
                "Pengajuan dibuat");
        this.riwayatTerakhir = riwayatPertama;
    }

    public String getIdPengajuan() {
        return idPengajuan;
    }

    public Nasabah getNasabah() {
        return nasabah;
    }

    public Usaha getUsaha() {
        return usaha;
    }

    public long getNominal() {
        return nominal;
    }

    public int getTenorBulan() {
        return tenorBulan;
    }

    public String getTujuanPenggunaan() {
        return tujuanPenggunaan;
    }

    public String getTanggalPengajuan() {
        return tanggalPengajuan;
    }

    public JenisAkad getJenisAkad() {
        return jenisAkad;
    }

    public int getNisbahBank() {
        return nisbahBank;
    }

    public int getNisbahNasabah() {
        return nisbahNasabah;
    }

    public long getModalNasabah() {
        return modalNasabah;
    }

    public StatusPengajuan getStatus() {
        return status;
    }

    public RiwayatPengajuan getRiwayatPertama() {
        return riwayatPertama;
    }

    public AnalisisKelayakan getAnalisisKelayakan() {
        return analisisKelayakan;
    }

    public void mulaiAnalisis(PegawaiBank pegawai, String waktu) {
        if (pegawai == null) {
            throw new IllegalArgumentException("Pegawai tidak boleh null");
        }
        if (!pegawai.getBank().memilikiPegawai(pegawai)) {
            throw new IllegalArgumentException(
                    "Pegawai penganalisis harus terdaftar pada banknya");
        }

        ubahStatus(
                StatusPengajuan.DIPROSES,
                waktu,
                pegawai.getNama(),
                "Analisis pengajuan dimulai");
        pegawaiPenganalisis = pegawai;
    }

    public void catatAnalisis(AnalisisKelayakan analisis) {
        if (analisis == null) {
            throw new IllegalArgumentException("Analisis tidak boleh null");
        }

        if (analisis.getPengajuan() != this) {
            throw new IllegalArgumentException(
                    "Analisis tersebut bukan untuk pengajuan ini");
        }
        if (analisis.getPegawaiAnalisis() != pegawaiPenganalisis) {
            throw new IllegalArgumentException(
                    "Analisis harus dibuat oleh pegawai yang memulai proses");
        }
        if (!analisis.getPegawaiAnalisis().getBank()
                .memilikiPegawai(analisis.getPegawaiAnalisis())) {
            throw new IllegalArgumentException(
                    "Pegawai penganalisis harus terdaftar pada banknya");
        }

        if (status != StatusPengajuan.DIPROSES) {
            throw new IllegalStateException(
                    "Pengajuan harus berstatus DIPROSES sebelum analisis dicatat");
        }

        if (analisisKelayakan != null) {
            throw new IllegalStateException(
                    "Pengajuan ini sudah memiliki catatan analisis");
        }

        this.analisisKelayakan = analisis;
    }

    public AkadPembiayaan getAkad() {
        return akad;
    }

    public void catatKeputusan(KeputusanPembiayaan keputusan) {
        if (keputusan == null) {
            throw new IllegalArgumentException("Keputusan tidak boleh null");
        }
        if (keputusan.getPengajuan() != this) {
            throw new IllegalStateException(
                    "Keputusan tersebut bukan untuk pengajuan ini");
        }
        if (analisisKelayakan == null) {
            throw new IllegalStateException(
                    "Analisis kelayakan harus dicatat sebelum keputusan dibuat");
        }
        PegawaiBank pegawai = keputusan.getPegawaiPemutus();
        if (pegawai.getBank() != analisisKelayakan.getPegawaiAnalisis().getBank()
                || !pegawai.getBank().memilikiPegawai(pegawai)) {
            throw new IllegalArgumentException(
                    "Pegawai pemutus harus terdaftar pada bank yang menangani analisis");
        }

        StatusPengajuan statusBaru = keputusan.getStatusKeputusan()
                == StatusKeputusan.DISETUJUI
                        ? StatusPengajuan.DISETUJUI
                        : StatusPengajuan.DITOLAK;
        ubahStatus(
                statusBaru,
                keputusan.getTanggalKeputusan(),
                pegawai.getNama(),
                keputusan.getAlasan());
    }

    public AkadPembiayaan buatAkad(
            String nomorAkad,
            KeputusanPembiayaan keputusan,
            String tanggalJatuhTempo) {
        if (keputusan == null || keputusan.getPengajuan() != this) {
            throw new IllegalArgumentException(
                    "Keputusan tidak sesuai dengan pengajuan ini");
        }
        if (keputusan.getStatusKeputusan() != StatusKeputusan.DISETUJUI
                || status != StatusPengajuan.DISETUJUI) {
            throw new IllegalStateException(
                    "Akad hanya dapat dibuat dari pengajuan yang disetujui");
        }
        if (akad != null) {
            throw new IllegalStateException(
                    "Pengajuan ini sudah memiliki akad");
        }

        BankSyariah bank = keputusan.getPegawaiPemutus().getBank();
        if (jenisAkad == JenisAkad.MUDHARABAH) {
            akad = new AkadMudharabah(
                    nomorAkad,
                    bank,
                    keputusan,
                    tanggalJatuhTempo,
                    nisbahBank,
                    nisbahNasabah);
        } else {
            akad = new AkadMusyarakah(
                    nomorAkad,
                    bank,
                    keputusan,
                    tanggalJatuhTempo,
                    nisbahBank,
                    nisbahNasabah,
                    modalNasabah);
        }
        return akad;
    }

    private void ubahStatus(
            StatusPengajuan statusBaru,
            String waktu,
            String pelaku,
            String keterangan) {
        if (statusBaru == null) {
            throw new IllegalArgumentException("Status baru tidak boleh null");
        }

        boolean transisiDiizinkan = false;

        switch (status) {
            case DIAJUKAN:
                transisiDiizinkan = statusBaru == StatusPengajuan.DIPROSES;
                break;
            case DIPROSES:
                transisiDiizinkan = statusBaru == StatusPengajuan.DISETUJUI
                        || statusBaru == StatusPengajuan.DITOLAK;
                break;
            case DISETUJUI:
            case DITOLAK:
                transisiDiizinkan = false;
                break;
            default:
                transisiDiizinkan = false;
                break;
        }

        if (!transisiDiizinkan) {
            throw new IllegalStateException(
                    "Transisi status pengajuan tidak diizinkan: "
                            + status + " -> " + statusBaru);
        }

        if (waktu == null || waktu.trim().isEmpty()) {
            throw new IllegalArgumentException("Waktu perubahan wajib diisi");
        }
        if (pelaku == null || pelaku.trim().isEmpty()) {
            throw new IllegalArgumentException("Pelaku perubahan wajib diisi");
        }
        if (keterangan == null || keterangan.trim().isEmpty()) {
            throw new IllegalArgumentException("Keterangan perubahan wajib diisi");
        }

        RiwayatPengajuan riwayatBaru = new RiwayatPengajuan(waktu, status, statusBaru, pelaku, keterangan);

        riwayatTerakhir.setBerikutnya(riwayatBaru);
        riwayatTerakhir = riwayatBaru;
        this.status = statusBaru;
    }

    @Override
    public String toString() {
        return "PengajuanPembiayaan: "
                + "\nID pengajuan: " + idPengajuan
                + "\nNasabah: " + nasabah.getNamaPihak()
                + "\nUsaha: " + usaha.getNamaUsaha()
                + "\nNominal: Rp. " + nominal
                + "\nTenor: " + tenorBulan + " bulan"
                + "\nTujuan penggunaan: " + tujuanPenggunaan
                + "\nTanggal pengajuan: " + tanggalPengajuan
                + "\nJenis akad: " + jenisAkad
                + "\nNisbah bank: " + nisbahBank + "%"
                + "\nNisbah nasabah: " + nisbahNasabah + "%"
                + "\nModal nasabah: Rp. " + modalNasabah
                + "\nStatus: " + status;
    }
}