package model;

import enums.StatusKeputusan;
import java.math.BigDecimal;
import util.Uang;

/**
 * Menyimpan hasil keputusan pegawai terhadap suatu pengajuan pembiayaan berbasis BigDecimal.
 */
public class KeputusanPembiayaan {

    private final String idKeputusan;
    private final String tanggalKeputusan;
    private final PengajuanPembiayaan pengajuan;
    private final PegawaiBank pegawaiPemutus;
    private final StatusKeputusan statusKeputusan;
    private final BigDecimal jumlahDisetujui;
    private final String alasan;

    /**
     * Membuat keputusan pembiayaan.
     *
     * @param idKeputusan nomor identitas keputusan
     * @param tanggalKeputusan tanggal keputusan
     * @param pengajuan pengajuan yang diputuskan
     * @param pegawaiPemutus pegawai terdaftar yang membuat keputusan
     * @param statusKeputusan hasil persetujuan atau penolakan
     * @param jumlahDisetujui jumlah yang disetujui dalam rupiah; harus nol jika ditolak
     * @param alasan alasan keputusan
     * @throws IllegalArgumentException jika keputusan, pegawai, atau jumlah tidak valid
     */
    public KeputusanPembiayaan(
            String idKeputusan,
            String tanggalKeputusan,
            PengajuanPembiayaan pengajuan,
            PegawaiBank pegawaiPemutus,
            StatusKeputusan statusKeputusan,
            BigDecimal jumlahDisetujui,
            String alasan) {

        if (pengajuan == null) {
            throw new IllegalArgumentException("Pengajuan tidak boleh null");
        }
        if (pegawaiPemutus == null) {
            throw new IllegalArgumentException("Pegawai tidak boleh null");
        }
        if (!pegawaiPemutus.getBank().memilikiPegawai(pegawaiPemutus)) {
            throw new IllegalArgumentException("Pegawai pemutus harus terdaftar pada banknya");
        }
        if (statusKeputusan == null) {
            throw new IllegalArgumentException("Status keputusan tidak boleh null");
        }
        if (jumlahDisetujui == null) {
            throw new IllegalArgumentException("Jumlah disetujui tidak boleh null");
        }

        if (statusKeputusan == StatusKeputusan.DISETUJUI) {
            if (jumlahDisetujui.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Jumlah yang disetujui harus lebih besar dari 0");
            }

            if (jumlahDisetujui.compareTo(pengajuan.getNominal()) > 0) {
                throw new IllegalArgumentException("Jumlah yang disetujui tidak boleh melebihi nominal pengajuan");
            }
        } else if (statusKeputusan == StatusKeputusan.DITOLAK && jumlahDisetujui.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("Jumlah yang disetujui harus 0 jika pengajuan ditolak");
        }

        this.idKeputusan = idKeputusan;
        this.tanggalKeputusan = tanggalKeputusan;
        this.pengajuan = pengajuan;
        this.pegawaiPemutus = pegawaiPemutus;
        this.statusKeputusan = statusKeputusan;
        this.jumlahDisetujui = jumlahDisetujui;
        this.alasan = alasan;
    }

    public String getIdKeputusan() {
        return idKeputusan;
    }

    public String getTanggalKeputusan() {
        return tanggalKeputusan;
    }

    public PengajuanPembiayaan getPengajuan() {
        return pengajuan;
    }

    public PegawaiBank getPegawaiPemutus() {
        return pegawaiPemutus;
    }

    public StatusKeputusan getStatusKeputusan() {
        return statusKeputusan;
    }

    public BigDecimal getJumlahDisetujui() {
        return jumlahDisetujui;
    }

    public String getAlasan() {
        return alasan;
    }

    @Override
    public String toString() {
        return "Keputusan Pembiayaan: "
                + "\nId keputusan: " + idKeputusan
                + "\nTanggalKeputusan: " + tanggalKeputusan
                + "\nID pengajuan: " + pengajuan.getIdPengajuan()
                + "\nPegawai Pemutus: " + pegawaiPemutus.getNama()
                + "\nStatusKeputusan: " + statusKeputusan
                + "\njumlahDisetujui: " + Uang.formatRupiah(jumlahDisetujui)
                + "\nalasan: " + alasan;
    }
}
