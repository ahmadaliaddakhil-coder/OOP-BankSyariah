package model;

import java.util.ArrayList;
import java.util.List;

import enums.JenisAkad;
import enums.HasilEvaluasiKerugian;
import enums.StatusAkad;
import enums.StatusKeputusan;
import enums.StatusPencairan;

/**
 * AkadPembiayaan
 */
public abstract class AkadPembiayaan {

    private final String nomorAkad;
    private final BankSyariah bank;
    private final KeputusanPembiayaan keputusan;
    private String tanggalTandaTangan;
    private String tanggalMulai;
    private final String tanggalJatuhTempo;
    private final long jumlahDisetujui;
    private final int nisbahBank;
    private final int nisbahNasabah;
    private StatusAkad status;
    private final List<Pencairan> daftarPencairan = new ArrayList<>();
    private final List<Pembayaran> daftarPembayaran = new ArrayList<>();
    private final List<PerhitunganBagiHasil> daftarPerhitunganBagiHasil = new ArrayList<>();
    private final List<KerugianUsaha> daftarKerugianUsaha = new ArrayList<>();
    private final List<EvaluasiKerugian> daftarEvaluasiKerugian = new ArrayList<>();

    public AkadPembiayaan(
            String nomorAkad,
            BankSyariah bank,
            KeputusanPembiayaan keputusan,
            String tanggalJatuhTempo,
            int nisbahBank,
            int nisbahNasabah) {

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

        if (nisbahBank < 0 || nisbahNasabah < 0
                || nisbahBank + nisbahNasabah != 100) {
            throw new IllegalArgumentException(
                    "Total nisbah bank dan nasabah harus 100%");
        }

        this.nomorAkad = nomorAkad;
        this.bank = bank;
        this.keputusan = keputusan;
        this.tanggalTandaTangan = null;
        this.tanggalMulai = null;
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

    public String getTanggalTandaTangan() {
        return tanggalTandaTangan;
    }

    public String getTanggalMulai() {
        return tanggalMulai;
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

    public StatusAkad getStatus() {
        return status;
    }

    public void tambahPencairan(Pencairan pencairan) {
        if (pencairan == null) {
            throw new IllegalArgumentException("Pencairan tidak boleh null");
        }

        if (pencairan.getAkad() != this) {
            throw new IllegalArgumentException(
                    "Pencairan tersebut bukan untuk akad ini");
        }

        if (status != StatusAkad.MENUNGGU_PENCAIRAN
                && status != StatusAkad.AKTIF) {
            throw new IllegalStateException(
                    "Akad harus menunggu pencairan atau sudah aktif");
        }

        if (daftarPencairan.contains(pencairan)) {
            throw new IllegalArgumentException(
                    "Pencairan tersebut sudah tercatat pada akad ini");
        }

        long totalTerpakai = 0;
        for (Pencairan item : daftarPencairan) {
            if (item.getStatus() != StatusPencairan.GAGAL) {
                totalTerpakai += item.getJumlah();
            }
        }

        if (pencairan.getJumlah() > jumlahDisetujui - totalTerpakai) {
            throw new IllegalArgumentException(
                    "Total pencairan melebihi jumlah yang disetujui");
        }

        daftarPencairan.add(pencairan);
    }

    boolean memilikiPencairan(Pencairan pencairan) {
        return daftarPencairan.contains(pencairan);
    }

    public long hitungTotalPencairanBerhasil() {
        long total = 0;

        for (Pencairan pencairan : daftarPencairan) {
            if (pencairan.getStatus() == StatusPencairan.BERHASIL) {
                total += pencairan.getJumlah();
            }
        }

        return total;
    }

    public List<Pencairan> getDaftarPencairan() {
        return new ArrayList<>(daftarPencairan);
    }

    public void daftarkanPembayaran(Pembayaran pembayaran) {
        if (pembayaran == null) {
            throw new IllegalArgumentException("Pembayaran tidak boleh null");
        }
        if (pembayaran.getAkad() != this) {
            throw new IllegalArgumentException(
                    "Pembayaran tersebut bukan untuk akad ini");
        }
        if (!bolehMemprosesPembayaran()) {
            throw new IllegalStateException(
                    "Pembayaran hanya dapat didaftarkan untuk akad aktif atau yang dihentikan dini");
        }
        if (pembayaran.getPerhitunganBagiHasil() != null
                && !daftarPerhitunganBagiHasil.contains(
                        pembayaran.getPerhitunganBagiHasil())) {
            throw new IllegalArgumentException(
                    "Perhitungan bagi hasil harus dicatat pada akad sebelum pembayaran");
        }
        validasiGantiRugiPembayaran(pembayaran);
        if (daftarPembayaran.contains(pembayaran)) {
            throw new IllegalArgumentException(
                    "Pembayaran tersebut sudah terdaftar");
        }
        for (Pembayaran item : daftarPembayaran) {
            if (item.getIdPembayaran().equals(pembayaran.getIdPembayaran())) {
                throw new IllegalArgumentException(
                        "ID pembayaran sudah digunakan pada akad ini");
            }
        }

        daftarPembayaran.add(pembayaran);
    }

    public void catatPembayaranBerhasil(
            Pembayaran pembayaran,
            String referensiTransaksi) {
        pastikanPembayaranTerdaftar(pembayaran);
        validasiBatasPembayaran(pembayaran);
        pembayaran.tandaiBerhasil(referensiTransaksi);
    }

    public void catatPembayaranGagal(Pembayaran pembayaran) {
        pastikanPembayaranTerdaftar(pembayaran);
        pembayaran.tandaiGagal();
    }

    private void pastikanPembayaranTerdaftar(Pembayaran pembayaran) {
        if (pembayaran == null) {
            throw new IllegalArgumentException("Pembayaran tidak boleh null");
        }
        if (pembayaran.getAkad() != this
                || !daftarPembayaran.contains(pembayaran)) {
            throw new IllegalStateException(
                    "Pembayaran belum terdaftar pada akad ini");
        }
        if (!bolehMemprosesPembayaran()) {
            throw new IllegalStateException(
                    "Pembayaran hanya dapat diproses untuk akad aktif atau yang dihentikan dini");
        }
    }

    private boolean bolehMemprosesPembayaran() {
        return status == StatusAkad.AKTIF || status == StatusAkad.DITERMINASI;
    }

    private void validasiGantiRugiPembayaran(Pembayaran pembayaran) {
        EvaluasiKerugian evaluasi = pembayaran.getEvaluasiKerugian();
        if (pembayaran.getJumlahGantiRugi() == 0) {
            return;
        }
        if (evaluasi == null || !daftarEvaluasiKerugian.contains(evaluasi)) {
            throw new IllegalArgumentException(
                    "Evaluasi ganti rugi harus tercatat pada akad ini");
        }
        if (evaluasi.getHasil() != HasilEvaluasiKerugian.TERBUKTI) {
            throw new IllegalArgumentException(
                    "Ganti rugi hanya dapat dibayar untuk temuan yang terbukti");
        }
        if (pembayaran.getJumlahGantiRugi() > hitungSisaGantiRugi(evaluasi)) {
            throw new IllegalArgumentException(
                    "Pembayaran ganti rugi melebihi sisa ganti rugi");
        }
    }

    private void validasiBatasPembayaran(Pembayaran pembayaran) {
        long sisaModal = hitungSisaPengembalianModal();
        if (pembayaran.getJumlahPengembalianModal() > sisaModal) {
            throw new IllegalArgumentException(
                    "Pengembalian modal melebihi sisa modal bank");
        }

        PerhitunganBagiHasil perhitungan = pembayaran.getPerhitunganBagiHasil();
        if (pembayaran.getJumlahBagiHasil() > 0) {
            long sisaBagiHasil = hitungSisaBagiHasil(perhitungan);
            if (pembayaran.getJumlahBagiHasil() > sisaBagiHasil) {
                throw new IllegalArgumentException(
                        "Pembayaran bagi hasil melebihi bagian bank yang belum dibayar");
            }
        }
        validasiGantiRugiPembayaran(pembayaran);
    }

    public long hitungTotalPengembalianModalBerhasil() {
        long total = 0;
        for (Pembayaran pembayaran : daftarPembayaran) {
            if (pembayaran.getStatus() == enums.StatusPembayaran.BERHASIL) {
                total = Math.addExact(
                        total, pembayaran.getJumlahPengembalianModal());
            }
        }
        return total;
    }

    public long hitungSisaPengembalianModal() {
        long totalKerugianBank = hitungTotalKerugianBank();
        long modalSetelahKerugian = totalKerugianBank
                >= hitungTotalPencairanBerhasil()
                        ? 0
                        : hitungTotalPencairanBerhasil() - totalKerugianBank;
        long totalPengembalian = hitungTotalPengembalianModalBerhasil();
        return totalPengembalian >= modalSetelahKerugian
                ? 0
                : modalSetelahKerugian - totalPengembalian;
    }

    public void catatKerugianUsaha(KerugianUsaha kerugian) {
        if (kerugian == null) {
            throw new IllegalArgumentException("Kerugian usaha tidak boleh null");
        }
        if (kerugian.getAkad() != this) {
            throw new IllegalArgumentException(
                    "Kerugian tersebut bukan untuk akad ini");
        }
        if (status != StatusAkad.AKTIF) {
            throw new IllegalStateException(
                    "Kerugian hanya dapat dicatat pada akad aktif");
        }
        for (KerugianUsaha item : daftarKerugianUsaha) {
            if (item == kerugian
                    || item.getVerifikasi().getLaporan()
                            == kerugian.getVerifikasi().getLaporan()) {
                throw new IllegalArgumentException(
                        "Laporan tersebut sudah memiliki catatan kerugian");
            }
            if (item.getIdKerugian().equals(kerugian.getIdKerugian())) {
                throw new IllegalArgumentException(
                        "ID kerugian sudah digunakan pada akad ini");
            }
        }
        daftarKerugianUsaha.add(kerugian);
    }

    public long hitungTotalKerugianBank() {
        long total = 0;
        for (KerugianUsaha kerugian : daftarKerugianUsaha) {
            total = Math.addExact(total, kerugian.getBagianKerugianBank());
        }
        return total;
    }

    public void catatEvaluasiKerugian(EvaluasiKerugian evaluasi) {
        if (evaluasi == null) {
            throw new IllegalArgumentException("Evaluasi kerugian tidak boleh null");
        }
        if (evaluasi.getKerugian().getAkad() != this
                || !daftarKerugianUsaha.contains(evaluasi.getKerugian())) {
            throw new IllegalArgumentException(
                    "Kerugian evaluasi harus tercatat pada akad ini");
        }
        if (status != StatusAkad.AKTIF && status != StatusAkad.DITERMINASI) {
            throw new IllegalStateException(
                    "Evaluasi hanya dapat dicatat pada akad aktif atau yang dihentikan dini");
        }
        for (EvaluasiKerugian item : daftarEvaluasiKerugian) {
            if (item == evaluasi
                    || item.getIdEvaluasi().equals(evaluasi.getIdEvaluasi())
                    || item.getKerugian() == evaluasi.getKerugian()) {
                throw new IllegalArgumentException(
                        "Evaluasi untuk kerugian tersebut sudah tercatat pada akad ini");
            }
        }
        daftarEvaluasiKerugian.add(evaluasi);
    }

    public long hitungSisaGantiRugi(EvaluasiKerugian evaluasi) {
        if (evaluasi == null || !daftarEvaluasiKerugian.contains(evaluasi)) {
            throw new IllegalArgumentException(
                    "Evaluasi ganti rugi harus tercatat pada akad ini");
        }
        long telahDibayar = 0;
        for (Pembayaran pembayaran : daftarPembayaran) {
            if (pembayaran.getStatus() == enums.StatusPembayaran.BERHASIL
                    && pembayaran.getEvaluasiKerugian() == evaluasi) {
                telahDibayar = Math.addExact(
                        telahDibayar, pembayaran.getJumlahGantiRugi());
            }
        }
        return Math.subtractExact(evaluasi.getJumlahGantiRugi(), telahDibayar);
    }

    public long hitungSisaTotalGantiRugi() {
        long total = 0;
        for (EvaluasiKerugian evaluasi : daftarEvaluasiKerugian) {
            total = Math.addExact(total, hitungSisaGantiRugi(evaluasi));
        }
        return total;
    }

    public long hitungTotalBagiHasilBerhasil(
            PerhitunganBagiHasil perhitungan) {
        if (perhitungan == null || perhitungan.getAkad() != this) {
            throw new IllegalArgumentException(
                    "Perhitungan bagi hasil bukan untuk akad ini");
        }

        long total = 0;
        for (Pembayaran pembayaran : daftarPembayaran) {
            if (pembayaran.getStatus() == enums.StatusPembayaran.BERHASIL
                    && pembayaran.getPerhitunganBagiHasil() == perhitungan) {
                total = Math.addExact(total, pembayaran.getJumlahBagiHasil());
            }
        }
        return total;
    }

    public long hitungSisaBagiHasil(PerhitunganBagiHasil perhitungan) {
        if (perhitungan == null || perhitungan.getAkad() != this) {
            throw new IllegalArgumentException(
                    "Perhitungan bagi hasil bukan untuk akad ini");
        }

        return Math.subtractExact(
                perhitungan.getBagianBank(),
                hitungTotalBagiHasilBerhasil(perhitungan));
    }

    public List<Pembayaran> getDaftarPembayaran() {
        return new ArrayList<>(daftarPembayaran);
    }

    public void catatPerhitunganBagiHasil(PerhitunganBagiHasil perhitungan) {
        if (perhitungan == null) {
            throw new IllegalArgumentException(
                    "Perhitungan bagi hasil tidak boleh null");
        }
        if (perhitungan.getAkad() != this) {
            throw new IllegalArgumentException(
                    "Perhitungan bagi hasil tersebut bukan untuk akad ini");
        }
        if (status != StatusAkad.AKTIF) {
            throw new IllegalStateException(
                    "Perhitungan bagi hasil hanya dapat dicatat pada akad aktif");
        }
        for (PerhitunganBagiHasil item : daftarPerhitunganBagiHasil) {
            if (item == perhitungan
                    || item.getVerifikasi().getLaporan()
                            == perhitungan.getVerifikasi().getLaporan()) {
                throw new IllegalArgumentException(
                        "Laporan tersebut sudah memiliki perhitungan bagi hasil");
            }
            if (item.getIdPerhitungan().equals(perhitungan.getIdPerhitungan())) {
                throw new IllegalArgumentException(
                        "ID perhitungan bagi hasil sudah digunakan pada akad ini");
            }
        }

        daftarPerhitunganBagiHasil.add(perhitungan);
    }

    public boolean memilikiPerhitunganBagiHasil(
            PerhitunganBagiHasil perhitungan) {
        return daftarPerhitunganBagiHasil.contains(perhitungan);
    }

    public long hitungSisaTotalBagiHasil() {
        long total = 0;
        for (PerhitunganBagiHasil perhitungan : daftarPerhitunganBagiHasil) {
            total = Math.addExact(
                    total, hitungSisaBagiHasil(perhitungan));
        }
        return total;
    }

    public void selesaikan() {
        if (status != StatusAkad.AKTIF
                && status != StatusAkad.DITERMINASI) {
            throw new IllegalStateException(
                    "Akad hanya dapat diselesaikan saat aktif atau dihentikan dini");
        }
        if (hitungSisaPengembalianModal() != 0) {
            throw new IllegalStateException(
                    "Akad belum dapat diselesaikan karena modal bank belum lunas");
        }
        if (hitungSisaTotalBagiHasil() != 0) {
            throw new IllegalStateException(
                    "Akad belum dapat diselesaikan karena bagi hasil bank belum lunas");
        }
        if (hitungSisaTotalGantiRugi() != 0) {
            throw new IllegalStateException(
                    "Akad belum dapat diselesaikan karena ganti rugi belum lunas");
        }
        for (EvaluasiKerugian evaluasi : daftarEvaluasiKerugian) {
            if (evaluasi.getHasil()
                    == HasilEvaluasiKerugian.DALAM_PEMERIKSAAN) {
                throw new IllegalStateException(
                        "Akad belum dapat diselesaikan karena masih ada evaluasi dalam pemeriksaan");
            }
        }
        for (Pencairan pencairan : daftarPencairan) {
            if (pencairan.getStatus() == StatusPencairan.DICATAT) {
                throw new IllegalStateException(
                        "Akad belum dapat diselesaikan karena masih ada pencairan yang belum diproses");
            }
        }
        for (Pembayaran pembayaran : daftarPembayaran) {
            if (pembayaran.getStatus() == enums.StatusPembayaran.DICATAT) {
                throw new IllegalStateException(
                        "Akad belum dapat diselesaikan karena masih ada pembayaran yang belum diproses");
            }
        }

        status = StatusAkad.SELESAI;
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

    void aktifkanSetelahPencairanBerhasil(String tanggalPencairan) {
        if (status == StatusAkad.AKTIF) {
            return;
        }

        if (status != StatusAkad.MENUNGGU_PENCAIRAN) {
            throw new IllegalStateException(
                    "Akad harus menunggu pencairan sebelum diaktifkan");
        }

        if (tanggalPencairan == null || tanggalPencairan.trim().isEmpty()) {
            throw new IllegalArgumentException("Tanggal pencairan wajib diisi");
        }

        this.tanggalMulai = tanggalPencairan;
        this.status = StatusAkad.AKTIF;
    }

    public void hentikanDini() {
        if (status == StatusAkad.SELESAI || status == StatusAkad.DITERMINASI) {
            throw new IllegalStateException(
                    "Akad yang sudah selesai atau diterminasi tidak dapat dihentikan lagi");
        }
        for (Pencairan pencairan : daftarPencairan) {
            if (pencairan.getStatus() == StatusPencairan.DICATAT) {
                throw new IllegalStateException(
                        "Pencairan yang masih dicatat harus diproses sebelum akad dihentikan");
            }
        }
        for (Pembayaran pembayaran : daftarPembayaran) {
            if (pembayaran.getStatus() == enums.StatusPembayaran.DICATAT) {
                throw new IllegalStateException(
                        "Pembayaran yang masih dicatat harus diproses sebelum akad dihentikan");
            }
        }

        this.status = StatusAkad.DITERMINASI;
    }

    @Override
    public String toString() {
        return "Akad Pembiayaan: "
                + "\nNomor akad: " + nomorAkad
                + "\nBank: " + bank.getNamaBank()
                + "\nID keputusan: " + keputusan.getIdKeputusan()
                + "\nJenis akad: " + getJenisAkad()
                + "\nTanggal tanda tangan: " + tanggalTandaTangan
                + "\nTanggal mulai: " + tanggalMulai
                + "\nTanggal jatuh tempo: " + tanggalJatuhTempo
                + "\nJumlah disetujui: Rp" + jumlahDisetujui
                + "\nNisbah bank: " + nisbahBank + "%"
                + "\nNisbah nasabah: " + nisbahNasabah + "%"
                + "\nStatus: " + status;
    }
}