package model;

import enums.JenisAkad;
import enums.StatusKeputusan;
import enums.StatusPengajuan;

public final class PembuatAkad {
    private PembuatAkad() {
    }

    public static AkadPembiayaan buatAkad(
            String nomorAkad,
            PengajuanPembiayaan pengajuan,
            KeputusanPembiayaan keputusan,
            String tanggalJatuhTempo) {
        if (pengajuan == null) {
            throw new IllegalArgumentException("Pengajuan tidak boleh null");
        }
        if (keputusan == null || keputusan.getPengajuan() != pengajuan) {
            throw new IllegalArgumentException(
                    "Keputusan tidak sesuai dengan pengajuan ini");
        }
        if (keputusan.getStatusKeputusan() != StatusKeputusan.DISETUJUI
                || pengajuan.getStatus() != StatusPengajuan.DISETUJUI) {
            throw new IllegalStateException(
                    "Akad hanya dapat dibuat dari pengajuan yang disetujui");
        }

        BankSyariah bank = keputusan.getPegawaiPemutus().getBank();
        JenisAkad jenisAkad = pengajuan.getJenisAkad();
        switch (jenisAkad) {
            case MUDHARABAH:
                return new AkadMudharabah(
                        nomorAkad,
                        bank,
                        keputusan,
                        tanggalJatuhTempo,
                        pengajuan.getNisbahBank(),
                        pengajuan.getNisbahNasabah());
            case MUSYARAKAH:
                return new AkadMusyarakah(
                        nomorAkad,
                        bank,
                        keputusan,
                        tanggalJatuhTempo,
                        pengajuan.getNisbahBank(),
                        pengajuan.getNisbahNasabah(),
                        pengajuan.getModalNasabah());
            default:
                throw new IllegalArgumentException(
                        "Jenis akad belum didukung: " + jenisAkad);
        }
    }
}
