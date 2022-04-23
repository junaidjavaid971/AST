package app.com.ast.models;

public class NeighborModel {
        public String plmn, sys, cellID, lac, code, freq, rxl, rxq;

        public NeighborModel() {
        }

        public NeighborModel(String plmn, String sys, String cellID, String lac, String code, String freq, String rxl, String rxq) {
            this.plmn = plmn;
            this.sys = sys;
            this.cellID = cellID;
            this.lac = lac;
            this.code = code;
            this.freq = freq;
            this.rxl = rxl;
            this.rxq = rxq;
        }
    }
