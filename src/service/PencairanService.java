package service;

import model.AkadPembiayaan;
import model.Pencairan;
import model.RekeningNasabah;

public class PencairanService {

    public Pencairan daftarkanPencairan(
            String idPencairan,
            AkadPembiayaan akad,
            long jumlah,
            String tanggalPencairan,
            RekeningNasabah rekeningTujuan) {
        if (akad == null) {
            throw new IllegalArgumentException("Akad tidak boleh null");
        }
        if (rekeningTujuan == null) {
            throw new IllegalArgumentException("Rekening tujuan tidak boleh null");
        }

        Pencairan pencairan = new Pencairan(
                idPencairan,
                akad,
                jumlah,
                tanggalPencairan,
                rekeningTujuan);
        akad.tambahPencairan(pencairan);
        return pencairan;
    }

    public void catatPencairanBerhasil(
            Pencairan pencairan,
            String referensiTransaksi) {
        if (pencairan == null) {
            throw new IllegalArgumentException("Pencairan tidak boleh null");
        }

        pencairan.catatBerhasil(referensiTransaksi);
    }

    public void catatPencairanGagal(Pencairan pencairan) {
        if (pencairan == null) {
            throw new IllegalArgumentException("Pencairan tidak boleh null");
        }

        pencairan.catatGagal();
    }
}
