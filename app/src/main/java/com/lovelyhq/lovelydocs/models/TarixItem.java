package com.lovelyhq.lovelydocs.models;

public class TarixItem {
    private String blockLength;
    private String blockNum;
    private String offset;
    private String path;

    public TarixItem(String path, String blockNum, String offset, String blockLength) {
        this.path = path;
        this.blockNum = blockNum;
        this.offset = offset;
        this.blockLength = blockLength;
    }

    public TarixItem() {

    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBlockNum() {
        return this.blockNum;
    }

    public void setBlockNum(String blocknum) {
        this.blockNum = blocknum;
    }

    public String getOffset() {
        return this.offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getBlockLength() {
        return this.blockLength;
    }

    public void setBlockLength(String blocklength) {
        this.blockLength = blocklength;
    }
}
