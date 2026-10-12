package model;

/**
 * Mewakili pegawai yang bekerja pada satu bank.
 */
public class PegawaiBank {
    private final String nomorPegawai;
    private final String nama;
    private final String jabatan;
    private final BankSyariah bank;

    /**
     * Membuat pegawai yang terhubung dengan bank tertentu.
     *
     * @param nomorPegawai nomor identitas pegawai
     * @param nama nama pegawai
     * @param jabatan jabatan pegawai
     * @param bank bank tempat pegawai bekerja
     * @throws IllegalArgumentException jika bank null
     */
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

    /**
     * Mengembalikan nomor identitas pegawai.
     *
     * @return nomor identitas pegawai
     */
    public String getNomorPegawai() {
        return nomorPegawai;
    }

    /**
     * Mengembalikan nama pegawai.
     *
     * @return nama pegawai
     */
    public String getNama() {
        return nama;
    }

    /**
     * Mengembalikan jabatan pegawai.
     *
     * @return jabatan pegawai
     */
    public String getJabatan() {
        return jabatan;
    }

    /**
     * Mengembalikan bank tempat pegawai bekerja.
     *
     * @return bank tempat pegawai bekerja
     */
    public BankSyariah getBank() {
        return bank;
    }

    /**
     * Menghasilkan ringkasan informasi pegawai.
     *
     * @return ringkasan informasi pegawai
     */
    @Override
    public String toString() {
        return "Pegawai Bank: " +
                "\nNomor pegawai: " + nomorPegawai
                + "\nNama: " + nama
                + "\nJabatan: " + jabatan
                + "\nBank: " + bank.getNamaBank();
    }
}