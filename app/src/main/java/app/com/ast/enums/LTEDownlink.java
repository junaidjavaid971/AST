package app.com.ast.enums;

public enum LTEDownlink {
    OFFSET1(2110),
    OFFSET2(1930),
    OFFSET3(1805),
    OFFSET4(2110),
    OFFSET5(869),
    OFFSET6(875),
    OFFSET7(2620),
    OFFSET8(925),
    OFFSET9(1845),
    OFFSET10(2110),
    OFFSET11(1475),
    OFFSET12(725),
    OFFSET13(746),
    OFFSET14(758);

    private final int offset;

    LTEDownlink(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
