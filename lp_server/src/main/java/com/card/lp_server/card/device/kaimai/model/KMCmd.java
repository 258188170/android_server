package com.card.lp_server.card.device.kaimai.model;

public class KMCmd {
    private int frame1;
    private int frame2;
    private int cmdFrame;
    private int checksum;
    private int frameEnd1;
    private int frameEnd2;

    public  byte[] getBytes(){
        return new byte[]{};
    }

    public int getFrame1() {
        return frame1;
    }

    public void setFrame1(int frame1) {
        this.frame1 = frame1;
    }

    public int getFrame2() {
        return frame2;
    }

    public void setFrame2(int frame2) {
        this.frame2 = frame2;
    }

    public int getCmdFrame() {
        return cmdFrame;
    }

    public void setCmdFrame(int cmdFrame) {
        this.cmdFrame = cmdFrame;
    }

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    public int getFrameEnd1() {
        return frameEnd1;
    }

    public void setFrameEnd1(int frameEnd1) {
        this.frameEnd1 = frameEnd1;
    }

    public int getFrameEnd2() {
        return frameEnd2;
    }

    public void setFrameEnd2(int frameEnd2) {
        this.frameEnd2 = frameEnd2;
    }
}
