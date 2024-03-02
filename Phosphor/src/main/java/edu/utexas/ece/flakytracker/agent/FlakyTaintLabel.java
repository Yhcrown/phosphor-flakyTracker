package edu.utexas.ece.flakytracker.agent;

public class FlakyTaintLabel {

    static final int RANDOM = 1;

    int type;
    String cause;


    int line;
    int label;

    public FlakyTaintLabel(int type, String cause, int line, int label) {
        this.type = type;
        this.cause = cause;
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

    public String getStringType(){
        switch (type){
            case RANDOM:
                return "RANDOM";
        }
        return null;
    }

    @Override
    public String toString() {
        return "FlakyTaintLabel{" +
                "type=" + getStringType() +
                ", cause='" + cause + '\'' +
                ", line=" + line +
                ", label=" + label +
                '}';
    }

}
