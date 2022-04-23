package app.com.ast.models;

import java.util.ArrayList;

public class LTEModel {

    private String device, date, name, mcc, enbID, tac, band, mnc, lcid, pci, rfc, state, roam, eci, type, bw;
    private String rsrp, rssi, rsrq, snr, cqi, ta, ul, dl, lat, lon, a, v;
    private ArrayList<NeighborModel> neighborModels;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getEnbID() {
        return enbID;
    }

    public void setEnbID(String enbID) {
        this.enbID = enbID;
    }

    public String getTac() {
        return tac;
    }

    public void setTac(String tac) {
        this.tac = tac;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getLcid() {
        return lcid;
    }

    public void setLcid(String lcid) {
        this.lcid = lcid;
    }

    public String getPci() {
        return pci;
    }

    public void setPci(String pci) {
        this.pci = pci;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRoam() {
        return roam;
    }

    public void setRoam(String roam) {
        this.roam = roam;
    }

    public String getEci() {
        return eci;
    }

    public void setEci(String eci) {
        this.eci = eci;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBw() {
        return bw;
    }

    public void setBw(String bw) {
        this.bw = bw;
    }

    public String getRsrp() {
        return rsrp;
    }

    public void setRsrp(String rsrp) {
        this.rsrp = rsrp;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getRsrq() {
        return rsrq;
    }

    public void setRsrq(String rsrq) {
        this.rsrq = rsrq;
    }

    public String getSnr() {
        return snr;
    }

    public void setSnr(String snr) {
        this.snr = snr;
    }

    public String getCqi() {
        return cqi;
    }

    public void setCqi(String cqi) {
        this.cqi = cqi;
    }

    public String getTa() {
        return ta;
    }

    public void setTa(String ta) {
        this.ta = ta;
    }

    public String getUl() {
        return ul;
    }

    public void setUl(String ul) {
        this.ul = ul;
    }

    public String getDl() {
        return dl;
    }

    public void setDl(String dl) {
        this.dl = dl;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public ArrayList<NeighborModel> getNeighborModels() {
        return neighborModels;
    }

    public void setNeighborModels(ArrayList<NeighborModel> neighborModels) {
        this.neighborModels = neighborModels;
    }
}
