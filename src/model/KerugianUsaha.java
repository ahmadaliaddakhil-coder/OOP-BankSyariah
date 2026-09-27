package model;

import java.math.BigInteger;

import enums.HasilVerifikasi;
import enums.JenisAkad;
import enums.StatusAkad;
import enums.StatusLaporan;

/**
 * KerugianUsaha
 */
public class KerugianUsaha {
    private final String idKerugian;
    private final VerifikasiLaporan verifikasi;
    private final String tanggalPencatatan;
    private final String keterangan;
    private final AkadPembiayaan akad;
    private final long jumlahKerugian;
    private final long bagianKerugianBank;
    private final long bagianKerugianNasabah;

    public KerugianUsaha(
            String idKerugian,
            VerifikasiLaporan verifikasi,
            String tanggalPencatatan,
            String keterangan) {
        if (verifikasi == null) {
            throw new IllegalArgumentException("Verifikasi tidak boleh null");
        }

        if (verifikasi.getHasil() != HasilVerifikasi.DITERIMA) {
            throw new IllegalArgumentException(
                    "Kerugian hanya dapat dicatat dari laporan yang diterima");
        }

        if (verifikasi.getLaporan() == null) {
            throw new IllegalArgumentException("Laporan tidak boleh null");
        }

        LaporanUsaha laporan = verifikasi.getLaporan();
        if (laporan.getStatus() != StatusLaporan.TERVERIFIKASI) {
            throw new IllegalStateException(
                    "Kerugian hanya dapat dicatat dari laporan yang terverifikasi");
        }

        AkadPembiayaan akadLaporan = laporan.getAkad();
        if (akadLaporan.getStatus() != StatusAkad.AKTIF) {
            throw new IllegalStateException(
                    "Kerugian hanya dapat dicatat untuk akad berstatus AKTIF");
        }
        if (idKerugian == null || idKerugian.trim().isEmpty()) {
            throw new IllegalArgumentException("ID kerugian wajib diisi");
        }
        if (tanggalPencatatan == null || tanggalPencatatan.trim().isEmpty()) {
            throw new IllegalArgumentException("Tanggal pencatatan wajib diisi");
        }
        if (keterangan == null || keterangan.trim().isEmpty()) {
            throw new IllegalArgumentException("Keterangan kerugian wajib diisi");
        }

        long labaRugi = verifikasi.getLaporan().hitungLabaRugi();

        if (labaRugi >= 0) {
            throw new IllegalArgumentException(
                    "Laporan tidak menunjukkan kerugian");
        }

        long totalKerugian = Math.negateExact(labaRugi);
        long modalBank = akadLaporan.hitungTotalPencairanBerhasil();
        if (modalBank <= 0) {
            throw new IllegalStateException(
                    "Tidak ada modal bank yang berhasil dicairkan untuk dialokasikan");
        }

        long bagianBank;
        long bagianNasabah;
        if (akadLaporan.getJenisAkad() == JenisAkad.MUDHARABAH) {
            bagianBank = totalKerugian;
            bagianNasabah = 0;
        } else if (akadLaporan instanceof AkadMusyarakah) {
            AkadMusyarakah akadMusyarakah = (AkadMusyarakah) akadLaporan;
            long modalNasabah = akadMusyarakah.getModalNasabahDisertakan();
            if (modalNasabah <= 0) {
                throw new IllegalStateException(
                        "Kontribusi modal aktual nasabah harus dicatat sebelum kerugian dihitung");
            }

            BigInteger totalModal = BigInteger.valueOf(modalBank)
                    .add(BigInteger.valueOf(modalNasabah));
            bagianBank = BigInteger.valueOf(totalKerugian)
                    .multiply(BigInteger.valueOf(modalBank))
                    .divide(totalModal)
                    .longValueExact();
            bagianNasabah = totalKerugian - bagianBank;
        } else {
            throw new IllegalStateException(
                    "Jenis akad tidak didukung untuk evaluasi kerugian");
        }

        this.idKerugian = idKerugian;
        this.verifikasi = verifikasi;
        this.tanggalPencatatan = tanggalPencatatan;
        this.keterangan = keterangan;
        this.akad = akadLaporan;
        this.jumlahKerugian = totalKerugian;
        this.bagianKerugianBank = bagianBank;
        this.bagianKerugianNasabah = bagianNasabah;
    }

    public String getIdKerugian() {
        return idKerugian;
    }

    public VerifikasiLaporan getVerifikasi() {
        return verifikasi;
    }

    public String getTanggalPencatatan() {
        return tanggalPencatatan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public long getJumlahKerugian() {
        return jumlahKerugian;
    }

    public AkadPembiayaan getAkad() {
        return akad;
    }

    public long getBagianKerugianBank() {
        return bagianKerugianBank;
    }

    public long getBagianKerugianNasabah() {
        return bagianKerugianNasabah;
    }

    @Override
    public String toString() {
        return "Kerugian Usaha"
                + "\nID kerugian: " + idKerugian
                + "\nID laporan: " + verifikasi.getLaporan().getIdLaporan()
                + "\nNomor akad: " + akad.getNomorAkad()
                + "\nTanggal pencatatan: " + tanggalPencatatan
                + "\nJumlah kerugian: Rp" + jumlahKerugian
                + "\nTanggungan bank: Rp" + bagianKerugianBank
                + "\nTanggungan nasabah: Rp" + bagianKerugianNasabah
                + "\nKeterangan: " + keterangan;
    }
}