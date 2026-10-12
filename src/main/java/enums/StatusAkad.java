package enums;

/** Status proses penandatanganan dan pencairan akad. */
public enum StatusAkad {
    /** Akad telah dibuat tetapi belum ditandatangani. */
    DRAFT,
    /** Akad telah ditandatangani dan menunggu pencairan. */
    MENUNGGU_PENCAIRAN,
    /** Dana akad telah dicairkan. */
    DICAIRKAN
}