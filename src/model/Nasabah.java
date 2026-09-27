package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Nasabah
 */
public class Nasabah {
    private final String nomorId;
    private String nama;
    private String alamat;
    private String tanggalLahir; // Format: yyyy-MM-dd
    private String pekerjaan;

    // class
    private final ProfilKeuangan profilKeuangan;
    private final List<Usaha> daftarUsaha;
    private final List<PengajuanPembiayaan> daftarPengajuan;
    private final List<RekeningNasabah> daftarRekening;

    public Nasabah(
            String nomorId,
            String nama,
            String alamat,
            String tanggalLahir,
            String pekerjaan,
            ProfilKeuangan profilKeuangan) {
        this.nomorId = nomorId;
        this.nama = nama;
        this.alamat = alamat;
        this.tanggalLahir = tanggalLahir;
        this.pekerjaan = pekerjaan;
        this.profilKeuangan = profilKeuangan;
        this.daftarUsaha = new ArrayList<>();
        this.daftarPengajuan = new ArrayList<>();
        this.daftarRekening = new ArrayList<>();
    }

    public void tambahRekening(RekeningNasabah rekening) {
        if (rekening == null) {
            throw new IllegalArgumentException("Rekening tidak boleh null");
        }
        if (rekening.getPemilik() != this) {
            throw new IllegalArgumentException(
                    "Rekening tersebut bukan milik nasabah ini");
        }
        if (daftarRekening.contains(rekening)) {
            throw new IllegalArgumentException(
                    "Rekening tersebut sudah terdaftar");
        }
        for (RekeningNasabah item : daftarRekening) {
            if (item.getNomorRekening().equals(rekening.getNomorRekening())) {
                throw new IllegalArgumentException(
                        "Nomor rekening sudah terdaftar pada nasabah ini");
            }
        }
        daftarRekening.add(rekening);
    }

    public List<RekeningNasabah> getDaftarRekening() {
        return new ArrayList<>(daftarRekening);
    }

    public void tambahUsaha(Usaha usaha) {
        if (usaha == null) {
            throw new IllegalArgumentException("Usaha tidak boleh null");
        }

        if (usaha.getPemilik() != this) {
            throw new IllegalArgumentException("Usaha tersebut bukan milik nasabah ini");
        }

        if (!daftarUsaha.contains(usaha)) {
            daftarUsaha.add(usaha);
        }
    }

    public void ajukanPembiayaan(PengajuanPembiayaan pengajuan) {
        if (pengajuan == null) {
            throw new IllegalArgumentException("Pengajuan tidak boleh null");
        }
        if (pengajuan.getNasabah() != this) {
            throw new IllegalArgumentException("Pengajuan tersebut bukan milik nasabah ini");
        }
        if (!daftarUsaha.contains(pengajuan.getUsaha())) {
            throw new IllegalArgumentException("Usaha pengajuan belum tercatat milik nasabah ini");
        }
        if (!daftarPengajuan.contains(pengajuan)) {
            daftarPengajuan.add(pengajuan);
        }
    }

    public List<Usaha> getDaftarUsaha() {
        return new ArrayList<>(daftarUsaha);
    }

    public List<PengajuanPembiayaan> getDaftarPengajuan() {
        return new ArrayList<>(daftarPengajuan);
    }

    public String getIdPihak() {
        return nomorId;
    }

    public String getNamaPihak() {
        return nama;
    }

    public String getAlamatPihak() {
        return alamat;
    }

    public String getTanggalLahirPihak() {
        return tanggalLahir;
    }

    public String getPekerjaanPihak() {
        return pekerjaan;
    }

    public ProfilKeuangan getProfilKeuangan() {
        return profilKeuangan;
    }

    @Override
    public String toString() {
        return "Nasabah: "
                + "\nNomor ID: " + nomorId
                + "\nNama: " + nama
                + "\nAlamat: " + alamat
                + "\nTanggal lahir: " + tanggalLahir
                + "\nPekerjaan: " + pekerjaan
                + "\nJumlah usaha: " + daftarUsaha.size();
    }

    public static class ProfilKeuangan {
        private long gajiBulanan;
        private long pendapatanLainBulanan;
        private long kewajibanBulanan;
        private int jumlahTanggungan;
        private long totalAset;
        private String tanggalPembaruan; // Format: yyyy-MM-dd

        public ProfilKeuangan(
                long gajiBulanan,
                long pendapatanLainBulanan,
                long kewajibanBulanan,
                int jumlahTanggungan,
                long totalAset,
                String tanggalPembaruan) {
            this.gajiBulanan = gajiBulanan;
            this.pendapatanLainBulanan = pendapatanLainBulanan;
            this.kewajibanBulanan = kewajibanBulanan;
            this.jumlahTanggungan = jumlahTanggungan;
            this.totalAset = totalAset;
            this.tanggalPembaruan = tanggalPembaruan;
        }

        public long hitungPendapatanBulanan() {
            return gajiBulanan + pendapatanLainBulanan;
        }

        public long hitungSisaPendapatanBulanan() {
            return hitungPendapatanBulanan() - kewajibanBulanan;
        }

        public void perbaruiData(
                long gajiBulanan,
                long pendapatanLainBulanan,
                long kewajibanBulanan,
                int jumlahTanggungan,
                long totalAset,
                String tanggalPembaruan) {
            this.gajiBulanan = gajiBulanan;
            this.pendapatanLainBulanan = pendapatanLainBulanan;
            this.kewajibanBulanan = kewajibanBulanan;
            this.jumlahTanggungan = jumlahTanggungan;
            this.totalAset = totalAset;
            this.tanggalPembaruan = tanggalPembaruan;
        }

        public long getGajiBulanan() {
            return gajiBulanan;
        }

        public long getPendapatanLainBulanan() {
            return pendapatanLainBulanan;
        }

        public long getKewajibanBulanan() {
            return kewajibanBulanan;
        }

        public int getJumlahTanggungan() {
            return jumlahTanggungan;
        }

        public long getTotalAset() {
            return totalAset;
        }

        public String getTanggalPembaruan() {
            return tanggalPembaruan;
        }

        @Override
        public String toString() {
            return "ProfilKeuangan: "
                    + "\nGaji bulanan: " + gajiBulanan
                    + "\nPendapatan lain bulanan: " + pendapatanLainBulanan
                    + "\nKewajiban bulanan: " + kewajibanBulanan
                    + "\nJumlah tanggungan: " + jumlahTanggungan
                    + "\nTotal aset: " + totalAset
                    + "\nTanggal pembaruan: " + tanggalPembaruan;
        }
    }
}