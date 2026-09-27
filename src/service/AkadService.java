package service;

import enums.JenisAkad;
import enums.StatusKeputusan;
import enums.StatusPengajuan;
import model.AkadPembiayaan;
import model.AkadMudharabah;
import model.AkadMusyarakah;
import model.BankSyariah;
import model.KeputusanPembiayaan;
import model.PengajuanPembiayaan;

public class AkadService {

    public AkadPembiayaan buatAkad(
            String nomorAkad,
            KeputusanPembiayaan keputusan,
            String tanggalJatuhTempo) {
        if (keputusan == null) {
            throw new IllegalArgumentException("Keputusan tidak boleh null");
        }

        if (keputusan.getStatusKeputusan() != StatusKeputusan.DISETUJUI) {
            throw new IllegalStateException(
                    "Akad hanya dapat dibuat dari keputusan yang disetujui");
        }

        PengajuanPembiayaan pengajuan = keputusan.getPengajuan();
        if (pengajuan.getStatus() != StatusPengajuan.DISETUJUI) {
            throw new IllegalStateException(
                    "Pengajuan harus berstatus DISETUJUI sebelum akad dibuat");
        }

        if (pengajuan.getAkad() != null) {
            throw new IllegalStateException(
                    "Pengajuan ini sudah memiliki akad");
        }

        BankSyariah bank = keputusan.getPegawaiPemutus().getBank();
        AkadPembiayaan akadBaru;

        if (pengajuan.getJenisAkad() == JenisAkad.MUDHARABAH) {
            akadBaru = new AkadMudharabah(
                    nomorAkad,
                    bank,
                    keputusan,
                    tanggalJatuhTempo,
                    pengajuan.getNisbahBank(),
                    pengajuan.getNisbahNasabah());
        } else if (pengajuan.getJenisAkad() == JenisAkad.MUSYARAKAH) {
            akadBaru = new AkadMusyarakah(
                    nomorAkad,
                    bank,
                    keputusan,
                    tanggalJatuhTempo,
                    pengajuan.getNisbahBank(),
                    pengajuan.getNisbahNasabah(),
                    pengajuan.getModalNasabah());
        } else {
            throw new IllegalArgumentException("Jenis akad tidak didukung");
        }

        pengajuan.catatAkad(akadBaru);
        return akadBaru;
    }
}
