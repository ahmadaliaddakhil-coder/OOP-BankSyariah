package service;

import model.AkadPembiayaan;
import model.EvaluasiKerugian;
import model.Pembayaran;
import model.PerhitunganBagiHasil;
import model.RekeningNasabah;

public class PembayaranService {

    public Pembayaran daftarkanPembayaran(
            String idPembayaran,
            AkadPembiayaan akad,
            PerhitunganBagiHasil perhitunganBagiHasil,
            String tanggalPembayaran,
            long jumlahPengembalianModal,
            long jumlahBagiHasil,
            RekeningNasabah rekeningSumber) {
        if (akad == null) {
            throw new IllegalArgumentException("Akad tidak boleh null");
        }

        Pembayaran pembayaran = new Pembayaran(
                idPembayaran,
                akad,
                perhitunganBagiHasil,
                tanggalPembayaran,
                jumlahPengembalianModal,
                jumlahBagiHasil,
                rekeningSumber);
        akad.daftarkanPembayaran(pembayaran);
        return pembayaran;
    }

    public Pembayaran daftarkanPembayaran(
            String idPembayaran,
            AkadPembiayaan akad,
            PerhitunganBagiHasil perhitunganBagiHasil,
            String tanggalPembayaran,
            long jumlahPengembalianModal,
            long jumlahBagiHasil,
            EvaluasiKerugian evaluasiKerugian,
            long jumlahGantiRugi,
            RekeningNasabah rekeningSumber) {
        if (akad == null) {
            throw new IllegalArgumentException("Akad tidak boleh null");
        }

        Pembayaran pembayaran = new Pembayaran(
                idPembayaran,
                akad,
                perhitunganBagiHasil,
                tanggalPembayaran,
                jumlahPengembalianModal,
                jumlahBagiHasil,
                evaluasiKerugian,
                jumlahGantiRugi,
                rekeningSumber);
        akad.daftarkanPembayaran(pembayaran);
        return pembayaran;
    }

    public void catatPembayaranBerhasil(
            Pembayaran pembayaran,
            String referensiTransaksi) {
        if (pembayaran == null) {
            throw new IllegalArgumentException("Pembayaran tidak boleh null");
        }

        pembayaran.getAkad().catatPembayaranBerhasil(
                pembayaran,
                referensiTransaksi);
    }

    public void catatPembayaranGagal(Pembayaran pembayaran) {
        if (pembayaran == null) {
            throw new IllegalArgumentException("Pembayaran tidak boleh null");
        }

        pembayaran.getAkad().catatPembayaranGagal(pembayaran);
    }
}
