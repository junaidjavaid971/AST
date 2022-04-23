package app.com.ast.enums;

public enum LTEBandwidths {
    BANDWIDTH20(20),
    BANDWIDTH15(15),
    BANDWIDTH60(60),
    BANDWIDTH75(75),
    BANDWIDTH25(25),
    BANDWIDTH35(35),
    BANDWIDTH18(18),
    BANDWIDTH10(10),
    BANDWIDTH30(30),
    BANDWIDTH90(90),
    BANDWIDTH34(34),
    BANDWIDTH44(44),
    BANDWIDTH17(17),
    BANDWIDTH65(65),
    BANDWIDTH45(45),
    BANDWIDTH43(43),
    BANDWIDTH40(40),
    BANDWIDTH50(50),
    BANDWIDTH100(100),
    BANDWIDTH194(194),
    BANDWIDTH200(200),
    BANDWIDTH775(775),
    BANDWIDTH70(70),
    BANDWIDTH150(150),
    BANDWIDTH85(85),
    BANDWIDTH5(5),
    BANDWIDTH11(11);

    private final int bandwidth;

    LTEBandwidths(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public int getBandwidth() {
        return bandwidth;
    }
}
