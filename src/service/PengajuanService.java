package service;

import enums.StatusKeputusan;
import enums.StatusPengajuan;
import model.KeputusanPembiayaan;
import model.PegawaiBank;
import model.PengajuanPembiayaan;

public class PengajuanService {

    public void mulaiAnalisis(
            PengajuanPembiayaan pengajuan,
            PegawaiBank pegawai,
            String waktu) {
        if (pengajuan == null) {
            throw new IllegalArgumentException("Pengajuan tidak boleh null");
        }

        if (pegawai == null) {
            throw new IllegalArgumentException("Pegawai tidak boleh null");
        }

        pengajuan.ubahStatus(
                StatusPengajuan.DIPROSES,
                waktu,
                pegawai.getNama(),
                "Analisis pengajuan dimulai");
    }

    public void catatKeputusan(KeputusanPembiayaan keputusan) {
        if (keputusan == null) {
            throw new IllegalArgumentException("Keputusan tidak boleh null");
        }

        PengajuanPembiayaan pengajuan = keputusan.getPengajuan();

        if (pengajuan.getAnalisisKelayakan() == null) {
            throw new IllegalStateException(
                    "Analisis kelayakan harus dicatat sebelum keputusan dibuat");
        }

        PegawaiBank pegawai = keputusan.getPegawaiPemutus();
        StatusPengajuan statusBaru;

        if (keputusan.getStatusKeputusan() == StatusKeputusan.DISETUJUI) {
            statusBaru = StatusPengajuan.DISETUJUI;
        } else {
            statusBaru = StatusPengajuan.DITOLAK;
        }

        pengajuan.ubahStatus(
                statusBaru,
                keputusan.getTanggalKeputusan(),
                pegawai.getNama(),
                keputusan.getAlasan());
    }
}