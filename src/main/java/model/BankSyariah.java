package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Mewakili bank yang mengelola pegawai dan menjadi pihak dalam pembiayaan.
 */
public class BankSyariah {
    private final String kodeBank;
    private final String namaBank;
    private final List<PegawaiBank> daftarPegawai;

    /**
     * Membuat bank dengan kode dan nama yang diberikan.
     *
     * @param kodeBank kode identitas bank
     * @param namaBank nama bank
     */
    public BankSyariah(String kodeBank, String namaBank) {
        this.kodeBank = kodeBank;
        this.namaBank = namaBank;
        this.daftarPegawai = new ArrayList<>();
    }

    /**
     * Mendaftarkan pegawai yang memiliki relasi ke bank ini.
     *
     * @param pegawai pegawai yang akan didaftarkan
     * @throws IllegalArgumentException jika pegawai null atau terkait dengan bank lain
     */
    public void tambahPegawai(PegawaiBank pegawai) {
        if (pegawai == null) {
            throw new IllegalArgumentException("Pegawai tidak boleh null");
        }

        if (pegawai.getBank() != this) {
            throw new IllegalArgumentException(
                    "Pegawai tersebut tidak terdaftar pada bank ini");
        }

        if (!daftarPegawai.contains(pegawai)) {
            daftarPegawai.add(pegawai);
        }
    }

    /**
     * Mengembalikan kode identitas bank.
     *
     * @return kode identitas bank
     */
    public String getKodeBank() {
        return kodeBank;
    }

    /**
     * Mengembalikan nama bank.
     *
     * @return nama bank
     */
    public String getNamaBank() {
        return namaBank;
    }

    /**
     * Mengembalikan salinan daftar pegawai yang terdaftar.
     *
     * @return salinan daftar pegawai yang terdaftar
     */
    public List<PegawaiBank> getDaftarPegawai() {
        return new ArrayList<>(daftarPegawai);
    }

    /**
     * Memeriksa apakah pegawai terdaftar pada bank ini.
     *
     * @param pegawai pegawai yang diperiksa
     * @return {@code true} jika pegawai terdaftar pada bank ini
     */
    public boolean memilikiPegawai(PegawaiBank pegawai) {
        return pegawai != null
                && pegawai.getBank() == this
                && daftarPegawai.contains(pegawai);
    }

    /**
     * Menghasilkan ringkasan informasi bank dan jumlah pegawainya.
     *
     * @return ringkasan informasi bank dan jumlah pegawainya
     */
    @Override
    public String toString() {
        return "Bank Syariah: "
                + "\nKode bank: " + kodeBank
                + "\nNama bank: " + namaBank
                + "\nJumlah pegawai: " + daftarPegawai.size();
    }
}