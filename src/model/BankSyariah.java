package model;

import java.util.ArrayList;
import java.util.List;

/**
 * BankSyariah
 */
public class BankSyariah {
    private final String kodeBank;
    private final String namaBank;
    private final List<PegawaiBank> daftarPegawai;

    public BankSyariah(String kodeBank, String namaBank) {
        this.kodeBank = kodeBank;
        this.namaBank = namaBank;
        this.daftarPegawai = new ArrayList<>();
    }

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

    public String getKodeBank() {
        return kodeBank;
    }

    public String getNamaBank() {
        return namaBank;
    }

    public List<PegawaiBank> getDaftarPegawai() {
        return new ArrayList<>(daftarPegawai);
    }

    public boolean memilikiPegawai(PegawaiBank pegawai) {
        return pegawai != null
                && pegawai.getBank() == this
                && daftarPegawai.contains(pegawai);
    }

    @Override
    public String toString() {
        return "Bank Syariah: "
                + "\nKode bank: " + kodeBank
                + "\nNama bank: " + namaBank
                + "\nJumlah pegawai: " + daftarPegawai.size();
    }
}