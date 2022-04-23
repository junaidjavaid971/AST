package app.com.ast.enums;

public enum LTEOffsets {
    OFFSET1(0),
    OFFSET2(600),
    OFFSET3(1200),
    OFFSET4(1950),
    OFFSET5(2400),
    OFFSET6(2650),
    OFFSET7(2750),
    OFFSET8(3450),
    OFFSET9(3800),
    OFFSET10(4150),
    OFFSET11(4750),
    OFFSET12(5000),
    OFFSET13(5180),
    OFFSET14(5280);

    private final int offset;

    LTEOffsets(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
