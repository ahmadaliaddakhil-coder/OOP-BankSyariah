package model;

import enums.JenisAkad;
import enums.StatusKeputusan;
import enums.StatusPengajuan;

/**
 * Data pengajuan pembiayaan beserta status, analisis, keputusan, akad, dan
 * riwayat perubahannya.
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
    private final long omzetBulananDilaporkan;
    private final long biayaLangsungBulananDilaporkan;
    private final long biayaOperasionalBulananDilaporkan;
    private final long kewajibanUsahaBulananDilaporkan;

    // class
    private StatusPengajuan status;
    private RiwayatPengajuan riwayatPertama;
    private RiwayatPengajuan riwayatTerakhir;
    private AnalisisKelayakan analisisKelayakan;
    private AkadPembiayaan akad;
    private PegawaiBank pegawaiPenganalisis;

    /**
     * Membuat pengajuan pembiayaan baru dengan data usaha dan keuangan yang
     * dilaporkan nasabah.
     *
     * @param idPengajuan nomor identitas pengajuan
     * @param nasabah nasabah yang mengajukan
     * @param usaha usaha terdaftar milik nasabah
     * @param nominal jumlah pembiayaan yang diminta dalam rupiah
     * @param tenorBulan jangka waktu pembiayaan dalam bulan
     * @param tujuanPenggunaan tujuan penggunaan pembiayaan
     * @param tanggalPengajuan tanggal pengajuan
     * @param jenisAkad jenis akad yang diajukan
     * @param nisbahBank porsi nisbah bank dalam persen
     * @param nisbahNasabah porsi nisbah nasabah dalam persen
     * @param modalNasabah modal kontribusi nasabah dalam rupiah
     * @param omzetBulananDilaporkan omzet bulanan usaha dalam rupiah
     * @param biayaLangsungBulananDilaporkan biaya langsung bulanan dalam rupiah
     * @param biayaOperasionalBulananDilaporkan biaya operasional bulanan dalam rupiah
     * @param kewajibanUsahaBulananDilaporkan kewajiban usaha bulanan dalam rupiah
     * @throws IllegalArgumentException jika relasi nasabah-usaha atau data pengajuan
     *         tidak memenuhi aturan bisnis
     */
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
            long modalNasabah,
            long omzetBulananDilaporkan,
            long biayaLangsungBulananDilaporkan,
            long biayaOperasionalBulananDilaporkan,
            long kewajibanUsahaBulananDilaporkan) {
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
        if (nisbahBank < 0 || nisbahNasabah < 0
                || (long) nisbahBank + nisbahNasabah != 100) {
            throw new IllegalArgumentException("Nisbah bank dan nasabah harus valid dan totalnya 100");
        }
        if (omzetBulananDilaporkan < 0 || biayaLangsungBulananDilaporkan < 0
                || biayaOperasionalBulananDilaporkan < 0
                || kewajibanUsahaBulananDilaporkan < 0) {
            throw new IllegalArgumentException(
                    "Data keuangan bulanan yang dilaporkan tidak boleh negatif");
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
        this.omzetBulananDilaporkan = omzetBulananDilaporkan;
        this.biayaLangsungBulananDilaporkan = biayaLangsungBulananDilaporkan;
        this.biayaOperasionalBulananDilaporkan = biayaOperasionalBulananDilaporkan;
        this.kewajibanUsahaBulananDilaporkan = kewajibanUsahaBulananDilaporkan;
        this.status = StatusPengajuan.DIAJUKAN;

        this.riwayatPertama = new RiwayatPengajuan(tanggalPengajuan, null, this.status, nasabah.getNamaPihak(),
                "Pengajuan dibuat");
        this.riwayatTerakhir = riwayatPertama;
    }

    /**
     * Mengembalikan nomor identitas pengajuan.
     *
     * @return nomor identitas pengajuan
     */
    public String getIdPengajuan() {
        return idPengajuan;
    }

    /**
     * Mengembalikan nasabah yang mengajukan.
     *
     * @return nasabah yang mengajukan
     */
    public Nasabah getNasabah() {
        return nasabah;
    }

    /**
     * Mengembalikan usaha yang menjadi dasar pengajuan.
     *
     * @return usaha yang menjadi dasar pengajuan
     */
    public Usaha getUsaha() {
        return usaha;
    }

    /**
     * Mengembalikan nominal pembiayaan yang diminta dalam rupiah.
     *
     * @return nominal pembiayaan yang diminta dalam rupiah
     */
    public long getNominal() {
        return nominal;
    }

    /**
     * Mengembalikan tenor pembiayaan dalam bulan.
     *
     * @return tenor pembiayaan dalam bulan
     */
    public int getTenorBulan() {
        return tenorBulan;
    }

    /**
     * Mengembalikan tujuan penggunaan pembiayaan.
     *
     * @return tujuan penggunaan pembiayaan
     */
    public String getTujuanPenggunaan() {
        return tujuanPenggunaan;
    }

    /**
     * Mengembalikan tanggal pengajuan.
     *
     * @return tanggal pengajuan
     */
    public String getTanggalPengajuan() {
        return tanggalPengajuan;
    }

    /**
     * Mengembalikan jenis akad yang diajukan.
     *
     * @return jenis akad yang diajukan
     */
    public JenisAkad getJenisAkad() {
        return jenisAkad;
    }

    /**
     * Mengembalikan porsi nisbah bank dalam persen.
     *
     * @return porsi nisbah bank dalam persen
     */
    public int getNisbahBank() {
        return nisbahBank;
    }

    /**
     * Mengembalikan porsi nisbah nasabah dalam persen.
     *
     * @return porsi nisbah nasabah dalam persen
     */
    public int getNisbahNasabah() {
        return nisbahNasabah;
    }

    /**
     * Mengembalikan modal kontribusi nasabah dalam rupiah.
     *
     * @return modal kontribusi nasabah dalam rupiah
     */
    public long getModalNasabah() {
        return modalNasabah;
    }

    /**
     * Mengembalikan omzet bulanan yang dilaporkan dalam rupiah.
     *
     * @return omzet bulanan yang dilaporkan dalam rupiah
     */
    public long getOmzetBulananDilaporkan() {
        return omzetBulananDilaporkan;
    }

    /**
     * Mengembalikan biaya langsung bulanan yang dilaporkan dalam rupiah.
     *
     * @return biaya langsung bulanan yang dilaporkan dalam rupiah
     */
    public long getBiayaLangsungBulananDilaporkan() {
        return biayaLangsungBulananDilaporkan;
    }

    /**
     * Mengembalikan biaya operasional bulanan yang dilaporkan dalam rupiah.
     *
     * @return biaya operasional bulanan yang dilaporkan dalam rupiah
     */
    public long getBiayaOperasionalBulananDilaporkan() {
        return biayaOperasionalBulananDilaporkan;
    }

    /**
     * Mengembalikan kewajiban usaha bulanan yang dilaporkan dalam rupiah.
     *
     * @return kewajiban usaha bulanan yang dilaporkan dalam rupiah
     */
    public long getKewajibanUsahaBulananDilaporkan() {
        return kewajibanUsahaBulananDilaporkan;
    }

    /**
     * Mengembalikan status pengajuan saat ini.
     *
     * @return status pengajuan saat ini
     */
    public StatusPengajuan getStatus() {
        return status;
    }

    /**
     * Mengembalikan catatan pertama dalam riwayat perubahan status.
     *
     * @return catatan pertama dalam riwayat perubahan status
     */
    public RiwayatPengajuan getRiwayatPertama() {
        return riwayatPertama;
    }

    /**
     * Mengembalikan analisis kelayakan yang tercatat, atau {@code null} jika belum ada.
     *
     * @return analisis kelayakan yang tercatat, atau {@code null} jika belum ada
     */
    public AnalisisKelayakan getAnalisisKelayakan() {
        return analisisKelayakan;
    }

    /**
     * Memulai analisis dan mengubah status pengajuan menjadi {@code DIPROSES}.
     *
     * @param pegawai pegawai terdaftar yang menangani analisis
     * @param waktu waktu dimulainya analisis
     * @throws IllegalArgumentException jika pegawai tidak valid
     * @throws IllegalStateException jika transisi status tidak diizinkan
     */
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

    /**
     * Mencatat hasil analisis untuk pengajuan ini.
     *
     * @param analisis hasil analisis yang terkait dengan pengajuan dan pegawainya
     * @throws IllegalArgumentException jika analisis tidak sesuai dengan pengajuan
     *         atau pegawai penanggung jawab
     * @throws IllegalStateException jika pengajuan belum diproses atau sudah
     *         memiliki analisis
     */
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

    /**
     * Mengembalikan akad yang dibuat untuk pengajuan ini, atau {@code null} jika belum ada.
     *
     * @return akad yang dibuat untuk pengajuan ini, atau {@code null} jika belum ada
     */
    public AkadPembiayaan getAkad() {
        return akad;
    }

    /**
     * Mencatat keputusan dan mengubah status pengajuan sesuai hasil keputusan.
     *
     * @param keputusan keputusan untuk pengajuan ini
     * @throws IllegalArgumentException jika keputusan atau pegawai pemutus tidak valid
     * @throws IllegalStateException jika keputusan bukan untuk pengajuan ini atau
     *         analisis belum dicatat
     */
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

    /**
     * Membuat akad yang sesuai dengan jenis akad setelah pengajuan disetujui.
     *
     * @param nomorAkad nomor identitas akad
     * @param keputusan keputusan persetujuan pengajuan
     * @param tanggalJatuhTempo tanggal jatuh tempo akad
     * @return akad yang dibuat
     * @throws IllegalStateException jika akad sudah dibuat atau pengajuan belum
     *         disetujui
     * @throws IllegalArgumentException jika data akad atau keputusan tidak valid
     */
    public AkadPembiayaan buatAkad(
            String nomorAkad,
            KeputusanPembiayaan keputusan,
            String tanggalJatuhTempo) {
        if (akad != null) {
            throw new IllegalStateException(
                    "Pengajuan ini sudah memiliki akad");
        }

        akad = PembuatAkad.buatAkad(
                nomorAkad,
                this,
                keputusan,
                tanggalJatuhTempo);
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

    /**
     * Menghasilkan ringkasan data pengajuan dan statusnya.
     *
     * @return ringkasan data pengajuan dan statusnya
     */
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
                + "\nOmzet bulanan dilaporkan: Rp. " + omzetBulananDilaporkan
                + "\nBiaya langsung bulanan dilaporkan: Rp. " + biayaLangsungBulananDilaporkan
                + "\nBiaya operasional bulanan dilaporkan: Rp. " + biayaOperasionalBulananDilaporkan
                + "\nKewajiban usaha bulanan dilaporkan: Rp. " + kewajibanUsahaBulananDilaporkan
                + "\nStatus: " + status;
    }
}