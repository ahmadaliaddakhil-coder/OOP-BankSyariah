package model;

import enums.JenisAkad;
import enums.StatusAkad;
import enums.StatusKeputusan;

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

    public abstract JenisAkad getJenisAkad();

    public String getNomorAkad() {
        return nomorAkad;
    }

    public BankSyariah getBank() {
        return bank;
    }

    public KeputusanPembiayaan getKeputusan() {
        return keputusan;
    }

    public String getTanggalJatuhTempo() {
        return tanggalJatuhTempo;
    }

    public long getJumlahDisetujui() {
        return jumlahDisetujui;
    }

    public int getNisbahBank() {
        return nisbahBank;
    }

    public int getNisbahNasabah() {
        return nisbahNasabah;
    }

    public String getTanggalTandaTangan() {
        return tanggalTandaTangan;
    }

    public StatusAkad getStatus() {
        return status;
    }

    public Pencairan getPencairan() {
        return pencairan;
    }

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
