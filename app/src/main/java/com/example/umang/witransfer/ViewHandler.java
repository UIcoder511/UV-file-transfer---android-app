package com.example.umang.witransfer;

public class ViewHandler {
    private String fname;
    private int picid;
    private double fsize;

    public ViewHandler(String fname,int picid,double fsize)
    {
        this.fname=fname;
        this.picid=picid;
        this.fsize=fsize;
    }

    public String getFname() {
        return fname;
    }

    public int getPicid() {
        return picid;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setPicid(int picid) {
        this.picid = picid;
    }

    public void setFsize(double fsize) {
        this.fsize = fsize;
    }

    public double getFsize() {
        return fsize;
    }
}
