package app.com.ast;

import app.com.ast.enums.LTEBandwidths;
import app.com.ast.enums.LTEDownlink;
import app.com.ast.enums.LTEOffsets;
import app.com.ast.enums.WCDMAOffsets;

public class BandFrequency {
    public BandFrequency() {

    }

    public static int calculateLTEFrequency(int rfc) {
        int band = 0;
        if (rfc >= 0 && rfc <= 599) {
            band = (int) (LTEDownlink.OFFSET1.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET1.getOffset())));
        } else if (rfc >= 600 && rfc <= 1199) {
            band = (int) (LTEDownlink.OFFSET2.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET2.getOffset())));
        } else if (rfc >= 1200 && rfc <= 1949) {
            band = (int) (LTEDownlink.OFFSET3.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET3.getOffset())));
        } else if (rfc >= 1950 && rfc <= 2399) {
            band = (int) (LTEDownlink.OFFSET4.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET4.getOffset())));
        } else if (rfc >= 2400 && rfc <= 2649) {
            band = (int) (LTEDownlink.OFFSET5.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET5.getOffset())));
        } else if (rfc >= 2650 && rfc <= 2749) {
            band = (int) (LTEDownlink.OFFSET6.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET6.getOffset())));
        } else if (rfc >= 2750 && rfc <= 3449) {
            band = (int) (LTEDownlink.OFFSET7.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET7.getOffset())));
        } else if (rfc >= 3450 && rfc <= 3799) {
            band = (int) (LTEDownlink.OFFSET8.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET8.getOffset())));
        } else if (rfc >= 3800 && rfc <= 4149) {
            band = (int) (LTEDownlink.OFFSET9.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET9.getOffset())));
        } else if (rfc >= 4150 && rfc <= 4749) {
            band = (int) (LTEDownlink.OFFSET10.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET10.getOffset())));
        } else if (rfc >= 4750 && rfc <= 4999) {
            band = (int) (LTEDownlink.OFFSET11.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET11.getOffset())));
        } else if (rfc >= 5000 && rfc <= 5179) {
            band = (int) (LTEDownlink.OFFSET12.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET12.getOffset())));
        } else if (rfc >= 5180 && rfc <= 5279) {
            band = (int) (LTEDownlink.OFFSET13.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET13.getOffset())));
        } else if (rfc >= 5280 && rfc <= 5379) {
            band = (int) (LTEDownlink.OFFSET14.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET14.getOffset())));
        }

        return getActualLTEBandValue(band);
    }

    public static int getDownlinkBandValue(int rfc) {
        int band = 0;
        if (rfc >= 0 && rfc <= 599) {
            band = (int) (LTEDownlink.OFFSET1.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET1.getOffset())));
        } else if (rfc >= 600 && rfc <= 1199) {
            band = (int) (LTEDownlink.OFFSET2.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET2.getOffset())));
        } else if (rfc >= 1200 && rfc <= 1949) {
            band = (int) (LTEDownlink.OFFSET3.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET3.getOffset())));
        } else if (rfc >= 1950 && rfc <= 2399) {
            band = (int) (LTEDownlink.OFFSET4.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET4.getOffset())));
        } else if (rfc >= 2400 && rfc <= 2649) {
            band = (int) (LTEDownlink.OFFSET5.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET5.getOffset())));
        } else if (rfc >= 2650 && rfc <= 2749) {
            band = (int) (LTEDownlink.OFFSET6.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET6.getOffset())));
        } else if (rfc >= 2750 && rfc <= 3449) {
            band = (int) (LTEDownlink.OFFSET7.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET7.getOffset())));
        } else if (rfc >= 3450 && rfc <= 3799) {
            band = (int) (LTEDownlink.OFFSET8.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET8.getOffset())));
        } else if (rfc >= 3800 && rfc <= 4149) {
            band = (int) (LTEDownlink.OFFSET9.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET9.getOffset())));
        } else if (rfc >= 4150 && rfc <= 4749) {
            band = (int) (LTEDownlink.OFFSET10.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET10.getOffset())));
        } else if (rfc >= 4750 && rfc <= 4999) {
            band = (int) (LTEDownlink.OFFSET11.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET11.getOffset())));
        } else if (rfc >= 5000 && rfc <= 5179) {
            band = (int) (LTEDownlink.OFFSET12.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET12.getOffset())));
        } else if (rfc >= 5180 && rfc <= 5279) {
            band = (int) (LTEDownlink.OFFSET13.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET13.getOffset())));
        } else if (rfc >= 5280 && rfc <= 5379) {
            band = (int) (LTEDownlink.OFFSET14.getOffset() + (0.1 * (rfc - LTEOffsets.OFFSET14.getOffset())));
        }
        return band;
    }

    public static int getLTEBandwidth(int rfc) {
        int band = getDownlinkBandValue(rfc);
        int bandwidth = 0;
        if (band >= 2110 && band <= 2170) {
            bandwidth = LTEBandwidths.BANDWIDTH60.getBandwidth();
        } else if (band >= 1930 && band <= 1990) {
            bandwidth = LTEBandwidths.BANDWIDTH60.getBandwidth();
        } else if (band >= 1805 && band <= 1880) {
            bandwidth = LTEBandwidths.BANDWIDTH75.getBandwidth();
        } else if (band >= 875 && band <= 890) {
            bandwidth = LTEBandwidths.BANDWIDTH15.getBandwidth();
        } else if (band >= 869 && band <= 894) {
            bandwidth = LTEBandwidths.BANDWIDTH25.getBandwidth();
        } else if (band >= 2620 && band <= 2690) {
            bandwidth = LTEBandwidths.BANDWIDTH70.getBandwidth();
        } else if (band >= 925 && band <= 960) {
            bandwidth = LTEBandwidths.BANDWIDTH35.getBandwidth();
        } else if (band >= 1844.9 && band <= 1879.9) {
            bandwidth = LTEBandwidths.BANDWIDTH35.getBandwidth();
        } else if (band >= 1475.9 && band <= 1500.9) {
            bandwidth = LTEBandwidths.BANDWIDTH20.getBandwidth();
        } else if (band >= 728 && band <= 746) {
            bandwidth = LTEBandwidths.BANDWIDTH18.getBandwidth();
        } else if (band >= 746 && band <= 768) {
            bandwidth = LTEBandwidths.BANDWIDTH10.getBandwidth();
        } else if (band >= 2600 && band <= 2620) {
            bandwidth = LTEBandwidths.BANDWIDTH20.getBandwidth();
        } else if (band >= 2585 && band <= 2600) {
            bandwidth = LTEBandwidths.BANDWIDTH15.getBandwidth();
        } else if (band >= 860 && band <= 875) {
            bandwidth = LTEBandwidths.BANDWIDTH15.getBandwidth();
        } else if (band >= 791 && band <= 821) {
            bandwidth = LTEBandwidths.BANDWIDTH30.getBandwidth();
        } else if (band >= 1495.5 && band <= 1510.9) {
            bandwidth = LTEBandwidths.BANDWIDTH15.getBandwidth();
        } else if (band >= 3510 && band <= 3600) {
            bandwidth = LTEBandwidths.BANDWIDTH90.getBandwidth();
        } else if (band >= 2180 && band <= 2200) {
            bandwidth = LTEBandwidths.BANDWIDTH20.getBandwidth();
        } else if (band >= 1525 && band <= 1559) {
            bandwidth = LTEBandwidths.BANDWIDTH34.getBandwidth();
        } else if (band >= 1930 && band <= 1995) {
            bandwidth = LTEBandwidths.BANDWIDTH65.getBandwidth();
        } else if (band >= 859 && band <= 894) {
            bandwidth = LTEBandwidths.BANDWIDTH30.getBandwidth();
        } else if (band >= 852 && band <= 869) {
            bandwidth = LTEBandwidths.BANDWIDTH17.getBandwidth();
        } else if (band >= 758 && band <= 803) {
            bandwidth = LTEBandwidths.BANDWIDTH45.getBandwidth();
        } else if (band >= 717 && band <= 728) {
            bandwidth = LTEBandwidths.BANDWIDTH11.getBandwidth();
        } else if (band >= 2350 && band <= 2360) {
            bandwidth = LTEBandwidths.BANDWIDTH10.getBandwidth();
        } else if (band >= 462.5 && band <= 467.5) {
            bandwidth = LTEBandwidths.BANDWIDTH5.getBandwidth();
        } else if (band >= 1452 && band <= 1496) {
            bandwidth = LTEBandwidths.BANDWIDTH44.getBandwidth();
        } else if (band >= 2110 && band <= 2200) {
            bandwidth = LTEBandwidths.BANDWIDTH90.getBandwidth();
        } else if (band >= 2570 && band <= 2620) {
            bandwidth = LTEBandwidths.BANDWIDTH50.getBandwidth();
        } else if (band >= 1995 && band <= 2020) {
            bandwidth = LTEBandwidths.BANDWIDTH15.getBandwidth();
        } else if (band >= 617 && band <= 652) {
            bandwidth = LTEBandwidths.BANDWIDTH35.getBandwidth();
        } else if (band >= 461 && band <= 466) {
            bandwidth = LTEBandwidths.BANDWIDTH5.getBandwidth();
        } else if (band >= 1475 && band <= 1518) {
            bandwidth = LTEBandwidths.BANDWIDTH43.getBandwidth();
        } else if (band >= 1432 && band <= 1517) {
            bandwidth = LTEBandwidths.BANDWIDTH85.getBandwidth();
        } else if (band >= 1427 && band <= 1432) {
            bandwidth = LTEBandwidths.BANDWIDTH5.getBandwidth();
        } else if (band >= 410 && band <= 417) {
            bandwidth = LTEBandwidths.BANDWIDTH5.getBandwidth();
        }
        return bandwidth;
    }

    public static int calculateGSMFrequency(int rfc) {
        int band = 0;
        if (rfc >= 259 && rfc <= 293) {
            band = (int) (450.6 + (0.2 * (rfc - 259)));
        } else if (rfc >= 306 && rfc <= 340) {
            band = (int) (479 + (0.2 * (rfc - 306)));
        } else if (rfc >= 438 && rfc <= 511) {
            band = (int) (747.2 + (0.2 * (rfc - 438)));
        } else if (rfc >= 128 && rfc <= 251) {
            band = (int) (824.2 + (0.2 * (rfc - 128)));
        } else if (rfc >= 1 && rfc <= 124) {
            band = (int) (890 + (0.2 * rfc));
        } else if (rfc >= 955 && rfc <= 1023) {
            band = (int) (890 + (0.2 * (rfc - 1024)));
        } else if (rfc >= 512 && rfc <= 810) {
            band = (int) (1850 + (0.2 * (rfc - 512)));
        } else if (rfc >= 512 && rfc <= 885) {
            band = (int) (1710.2 + (0.2 * (rfc - 512)));
        }
        return getActualGSMBandValue(band);
    }

    public static int getBand(int rfc) {
        int band = 0;
        if (rfc >= 10562 && rfc <= 10838) {
            band = WCDMAOffsets.BAND1.getOffset();
        } else if (rfc >= 9662 && rfc <= 9938) {
            band = WCDMAOffsets.BAND2.getOffset();
        } else if (rfc >= 1162 && rfc <= 1513) {
            band = WCDMAOffsets.BAND3.getOffset();
        } else if (rfc >= 1537 && rfc <= 1738) {
            band = WCDMAOffsets.BAND4.getOffset();
        } else if (rfc >= 4357 && rfc <= 4458) {
            band = WCDMAOffsets.BAND5.getOffset();
        } else if (rfc >= 2237 && rfc <= 2563) {
            band = WCDMAOffsets.BAND7.getOffset();
        } else if (rfc >= 2937 && rfc <= 3088) {
            band = WCDMAOffsets.BAND8.getOffset();
        } else if (rfc >= 9237 && rfc <= 9387) {
            band = WCDMAOffsets.BAND9.getOffset();
        } else if (rfc >= 3112 && rfc <= 3388) {
            band = WCDMAOffsets.BAND10.getOffset();
        } else if (rfc >= 3712 && rfc <= 3812) {
            band = WCDMAOffsets.BAND11.getOffset();
        } else if (rfc >= 3837 && rfc <= 3903) {
            band = WCDMAOffsets.BAND12.getOffset();
        } else if (rfc >= 4017 && rfc <= 4043) {
            band = WCDMAOffsets.BAND13.getOffset();
        } else if (rfc >= 4117 && rfc <= 4143) {
            band = WCDMAOffsets.BAND14.getOffset();
        }
        return band;
    }

    private static int getActualLTEBandValue(int band) {
        if (band % 100 == 0) return band;

        band--;
        while (band % 100 != 0) {
            band--;
        }
        return band;
    }

    private static int getActualGSMBandValue(int band) {
        if (band % 100 == 0) return band;

        band++;
        while (band % 100 != 0) {
            band++;
        }
        return band;
    }
}
