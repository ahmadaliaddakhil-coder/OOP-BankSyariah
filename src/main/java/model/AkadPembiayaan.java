package model;

import enums.JenisAkad;
import enums.StatusAkad;
import enums.StatusKeputusan;

/**
 * Kelas dasar untuk akad pembiayaan yang telah disetujui dan dapat dicairkan.
 */
public abstract class AkadPembiayaan {
    private final String nomorAkad;
    private final BankSyariah bank;
    private final KeputusanPembiayaan keputusan;
    private final String tanggalJatuhTempo;
    private final long jumlahDisetujui;
    private final int nisbahBank;
    private final int nisbahNasabah;
    private String tanggalTandaTangan;
    private StatusAkad status;
    private Pencairan pencairan;

    /**
     * Membuat akad dari keputusan pembiayaan yang telah disetujui.
     *
     * @param nomorAkad nomor identitas akad
     * @param bank bank pemberi pembiayaan
     * @param keputusan keputusan yang menjadi dasar akad
     * @param tanggalJatuhTempo tanggal jatuh tempo
     * @param nisbahBank porsi nisbah bank dalam persen
     * @param nisbahNasabah porsi nisbah nasabah dalam persen
     * @throws IllegalArgumentException jika data tidak valid atau keputusan tidak
     *         disetujui
     */
    protected AkadPembiayaan(
            String nomorAkad,
            BankSyariah bank,
            KeputusanPembiayaan keputusan,
            String tanggalJatuhTempo,
            int nisbahBank,
            int nisbahNasabah) {
        if (nomorAkad == null || nomorAkad.trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor akad wajib diisi");
        }
        if (bank == null) {
            throw new IllegalArgumentException("Bank tidak boleh null");
        }
        if (keputusan == null) {
            throw new IllegalArgumentException("Keputusan tidak boleh null");
        }
        if (keputusan.getStatusKeputusan() != StatusKeputusan.DISETUJUI) {
            throw new IllegalArgumentException(
                    "Akad hanya dapat dibuat dari keputusan yang disetujui");
        }
        if (keputusan.getPegawaiPemutus().getBank() != bank) {
            throw new IllegalArgumentException(
                    "Pegawai pemutus tidak bekerja pada bank pemberi akad");
        }
        if (tanggalJatuhTempo == null || tanggalJatuhTempo.trim().isEmpty()) {
            throw new IllegalArgumentException("Tanggal jatuh tempo wajib diisi");
        }
        if (nisbahBank < 0 || nisbahNasabah < 0
                || nisbahBank + nisbahNasabah != 100) {
            throw new IllegalArgumentException(
                    "Total nisbah bank dan nasabah harus 100%");
        }

        this.nomorAkad = nomorAkad;
        this.bank = bank;
        this.keputusan = keputusan;
        this.tanggalJatuhTempo = tanggalJatuhTempo;
        this.jumlahDisetujui = keputusan.getJumlahDisetujui();
        this.nisbahBank = nisbahBank;
        this.nisbahNasabah = nisbahNasabah;
        this.status = StatusAkad.DRAFT;
    }

    /**
     * Mengembalikan jenis akad konkret.
     *
     * @return jenis akad konkret
     */
    public abstract JenisAkad getJenisAkad();

    /**
     * Mengembalikan nomor identitas akad.
     *
     * @return nomor identitas akad
     */
    public String getNomorAkad() {
        return nomorAkad;
    }

    /**
     * Mengembalikan bank pemberi pembiayaan.
     *
     * @return bank pemberi pembiayaan
     */
    public BankSyariah getBank() {
        return bank;
    }

    /**
     * Mengembalikan keputusan pembiayaan yang mendasari akad.
     *
     * @return keputusan pembiayaan yang mendasari akad
     */
    public KeputusanPembiayaan getKeputusan() {
        return keputusan;
    }

    /**
     * Mengembalikan tanggal jatuh tempo.
     *
     * @return tanggal jatuh tempo
     */
    public String getTanggalJatuhTempo() {
        return tanggalJatuhTempo;
    }

    /**
     * Mengembalikan jumlah pembiayaan yang disetujui dalam rupiah.
     *
     * @return jumlah pembiayaan yang disetujui dalam rupiah
     */
    public long getJumlahDisetujui() {
        return jumlahDisetujui;
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
     * Mengembalikan tanggal akad ditandatangani, atau {@code null} jika belum ditandatangani.
     *
     * @return tanggal akad ditandatangani, atau {@code null} jika belum ditandatangani
     */
    public String getTanggalTandaTangan() {
        return tanggalTandaTangan;
    }

    /**
     * Mengembalikan status akad saat ini.
     *
     * @return status akad saat ini
     */
    public StatusAkad getStatus() {
        return status;
    }

    /**
     * Mengembalikan data pencairan, atau {@code null} jika belum dicairkan.
     *
     * @return data pencairan, atau {@code null} jika belum dicairkan
     */
    public Pencairan getPencairan() {
        return pencairan;
    }

    /**
     * Menandatangani akad dan mengubah statusnya menjadi menunggu pencairan.
     *
     * @param tanggalTandaTangan tanggal penandatanganan
     * @throws IllegalStateException jika akad bukan berstatus {@code DRAFT}
     * @throws IllegalArgumentException jika tanggal kosong
     */
    public void tandatangani(String tanggalTandaTangan) {
        if (status != StatusAkad.DRAFT) {
            throw new IllegalStateException(
                    "Akad hanya dapat ditandatangani saat berstatus DRAFT");
        }
        if (tanggalTandaTangan == null || tanggalTandaTangan.trim().isEmpty()) {
            throw new IllegalArgumentException("Tanggal tanda tangan wajib diisi");
        }

        this.tanggalTandaTangan = tanggalTandaTangan;
        this.status = StatusAkad.MENUNGGU_PENCAIRAN;
    }

    /**
     * Mencairkan jumlah yang disetujui ke rekening nasabah.
     *
     * @param idPencairan nomor identitas pencairan
     * @param tanggalPencairan tanggal pencairan
     * @param referensiTransaksi referensi transaksi pencairan
     * @param rekeningTujuan rekening nasabah penerima dana
     * @return catatan pencairan yang dibuat
     * @throws IllegalStateException jika akad belum menunggu pencairan atau sudah
     *         dicairkan
     * @throws IllegalArgumentException jika data pencairan atau rekening tidak valid
     */
    public Pencairan cairkan(
            String idPencairan,
            String tanggalPencairan,
            String referensiTransaksi,
            RekeningNasabah rekeningTujuan) {
        if (status != StatusAkad.MENUNGGU_PENCAIRAN) {
            throw new IllegalStateException(
                    "Akad harus menunggu pencairan");
        }
        if (pencairan != null) {
            throw new IllegalStateException("Akad ini sudah dicairkan");
        }

        Pencairan pencairanBaru = new Pencairan(
                idPencairan,
                this,
                rekeningTujuan,
                tanggalPencairan,
                referensiTransaksi);
        rekeningTujuan.kredit(jumlahDisetujui);
        this.pencairan = pencairanBaru;
        this.status = StatusAkad.DICAIRKAN;
        return pencairanBaru;
    }

    /**
     * Menghasilkan ringkasan informasi akad dan status pencairannya.
     *
     * @return ringkasan informasi akad dan status pencairannya
     */
    @Override
    public String toString() {
        return "Akad Pembiayaan:"
                + "\nNomor akad: " + nomorAkad
                + "\nBank: " + bank.getNamaBank()
                + "\nID keputusan: " + keputusan.getIdKeputusan()
                + "\nJenis akad: " + getJenisAkad()
                + "\nTanggal tanda tangan: " + tanggalTandaTangan
                + "\nTanggal jatuh tempo: " + tanggalJatuhTempo
                + "\nJumlah disetujui: Rp" + jumlahDisetujui
                + "\nNisbah bank: " + nisbahBank + "%"
                + "\nNisbah nasabah: " + nisbahNasabah + "%"
                + "\nTanggal pencairan: "
                + (pencairan == null ? "Belum dicairkan" : pencairan.getTanggalPencairan())
                + "\nStatus: " + status;
    }
}
