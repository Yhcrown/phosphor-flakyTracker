package edu.utexas.ece.flakytracker.agent;

import java.util.ArrayList;
import java.util.List;

public class FlakyTaintLabel {

    static final int RANDOM = 1;
    static final int FIELD = 2;

    static final int STATIC = 3;

    int type;
    String cause;

    String file;
    int line;
    int label;

    List<String> whiteList = new ArrayList<>();

    static int index = 0;


    public FlakyTaintLabel(int type, String cause, String file,int line, int label) {
        this.type = type;
        this.cause = cause;
        this.file = file;
        this.line = line;
        this.label = label;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getStringType() {
        switch (type) {
            case RANDOM:
                return "RANDOM";
            case FIELD:
                return "FIELD";
            case STATIC:
                return "STATIC";
        }
        return null;
    }

    @Override
    public String toString() {
        return "FlakyTaintLabel{" +
                "type=" + getStringType() +
                ", cause='" + cause + '\'' +
                ", file='" + file + '\'' +
                ", line=" + line +
                ", label=" + label +
                '}';
    }

    public void addWhiteList(String testName){
        whiteList.add(testName);
    }

    public boolean isInWhiteList(String testName){
        return whiteList.contains(testName);
    }




}
