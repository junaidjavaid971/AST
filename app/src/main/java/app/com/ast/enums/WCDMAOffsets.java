package app.com.ast.enums;

public enum WCDMAOffsets {
    BAND1(2100),
    BAND2(1900),
    BAND3(1800),
    BAND4(1700),
    BAND5(850),
    BAND6(800),
    BAND7(2600),
    BAND8(900),
    BAND9(1800),
    BAND10(1700),
    BAND11(1500),
    BAND12(700),
    BAND13(700),
    BAND14(700);

    private final int offset;

    WCDMAOffsets(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
