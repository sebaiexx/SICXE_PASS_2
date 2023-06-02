package com.company;

public class Operation {
    private String opcode;
    private String operation;
    private String Format;
    public Operation(String opcode) {
        this.opcode = opcode;
    }
    public Operation()
    {
    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }
}
