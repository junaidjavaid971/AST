package app.com.ast.enums;

public enum GSMOffsets {
    GSM450(18000),
    GSM480(18600),
    GSM750(19200),
    GSM850(19950),
    PGSM(20400),
    EGSM(20650),
    GSMR(20750),
    DCS1800(21450),
    PCS1900(21800);

    private final int offset;

    GSMOffsets(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
