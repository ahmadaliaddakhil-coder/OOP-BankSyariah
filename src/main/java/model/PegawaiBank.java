package model;

/**
 * PegawaiBank
 */
public class PegawaiBank {
    private final String nomorPegawai;
    private final String nama;
    private final String jabatan;
    private final BankSyariah bank;

    public PegawaiBank(
            String nomorPegawai,
            String nama,
            String jabatan,
            BankSyariah bank) {
        if (bank == null) {
            throw new IllegalArgumentException("Bank tidak boleh null");
        }

        this.nomorPegawai = nomorPegawai;
        this.nama = nama;
        this.jabatan = jabatan;
        this.bank = bank;
    }

    public String getNomorPegawai() {
        return nomorPegawai;
    }

    public String getNama() {
        return nama;
    }

    public String getJabatan() {
        return jabatan;
    }

    public BankSyariah getBank() {
        return bank;
    }

    @Override
    public String toString() {
        return "Pegawai Bank: " +
                "\nNomor pegawai: " + nomorPegawai
                + "\nNama: " + nama
                + "\nJabatan: " + jabatan
                + "\nBank: " + bank.getNamaBank();
    }
}