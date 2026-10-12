package enums;

/** Tahapan status yang dapat dialami pengajuan pembiayaan. */
public enum StatusPengajuan {
    /** Pengajuan baru dicatat oleh nasabah. */
    DIAJUKAN,
    /** Pengajuan sedang dianalisis oleh pegawai bank. */
    DIPROSES,
    /** Pengajuan disetujui. */
    DISETUJUI,
    /** Pengajuan ditolak. */
    DITOLAK
}