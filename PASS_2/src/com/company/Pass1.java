package com.company;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Pass1 {
    ArrayList<Instruction> instructions = new ArrayList<>();
    HashMap<String,Operation> Operations = new HashMap<>();
    HashMap<String,String> symboleTable = new HashMap<>();
    ArrayList<String> mrecords=new ArrayList<>();
    ArrayList<Literal> LiteralTable = new ArrayList<Literal>();

    public void readOperations() {
        File f = new File("Operations.txt");
        try {
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] x = line.split(",");
                Operation  o =new Operation();
                o.setOperation(x[0]);
                o.setFormat(x[1]);
                o.setOpcode(x[2]);
                Operations.put(x[0],o);
            }

        } catch (Exception e) {

        }

    }

    public void readFile() {
        File f = new File("Program.txt");
        try {
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                String line = s.nextLine();
                line = fixLineSpacing(line);
                String[] x = line.split(" ");
                Instruction i = new Instruction();

                String op = "";
                if (x.length == 3) {
                    i.setLabel(x[0]);
                    op = x[1];
                    i.setOperation(x[1]);
                    i.setOperand(x[2]);
                } else if (x.length == 2) {
                    i.setLabel("    ");
                    op = x[0];
                    i.setOperation(x[0]);
                    i.setOperand(x[1]);
                } else if (x.length == 1) {
                    i.setLabel("    ");
                    op = x[0];
                    i.setOperation(x[0]);
                    i.setOperand("    ");
                }
                if (op.startsWith("+"))
                    i.setFormat("4");
                else if (op.startsWith("&"))
                    i.setFormat("5");
                else if (op.startsWith("$"))
                    i.setFormat("6");
                else {
                    Operation oper = Operations.get(op);
                    if (oper != null) {
                        i.setFormat(oper.getFormat());
                    } else {
                        i.setFormat("");
                    }
                }
                instructions.add(i);

            }


        } catch (Exception e) {

        }
    }

    public void p1() {
        String address = instructions.get(0).getOperand();
        instructions.get(0).setAddress(address);
        int increment = 0;
        for (int i = 1; i < instructions.size(); i++) {
            Instruction x = instructions.get(i);
            address = convHexa(address, increment);
            x.setAddress(address);
            if(!x.getLabel().trim().equals(""))
            {
                symboleTable.put(x.getLabel(),address);
            }
            String Operand =x.getOperand();
            if (Operand.startsWith("=")) {
                boolean found =false;
                for (int k = 0; k < LiteralTable.size(); k++)
                {
                    if(LiteralTable.get(k).getName().equals(Operand))
                    {
                        found=true;
                        break;

                    }
                }
                if(!found){
                    Literal m = new Literal();
                    m.setName(Operand);
                    m.setAddress("");
                    if (Operand.startsWith("=X")) {
                        m.setLength((Operand.length() - 4) / 2);
                        m.setValue(Operand.substring(3, Operand.length() - 1));
                    } else {
                        m.setLength((Operand.length() - 4));
                        String value = "";
                        for (int j = 3; j < Operand.length() - 1; j++) {
                            int y = (int) Operand.charAt(j);
                            value += Integer.toHexString(y);
                        }
                        m.setValue(value);
                    }
                    LiteralTable.add(m);}
            }
            if (instructions.get(i).getOperation().equalsIgnoreCase("LTORG")) {
                increment=0;
                for (int j = 0; j < LiteralTable.size(); j++) {
                    if (LiteralTable.get(j).getAddress().equals("")) {
                        address=convHexa(address,increment);
                        address = addZeros(address, 4);
                        LiteralTable.get(j).setAddress(address);
                        Instruction ins = new Instruction();
                        ins.setLabel("*");
                        ins.setOperation(LiteralTable.get(j).getName());
                        ins.setOperand("");
                        ins.setObjectCode(LiteralTable.get(j).getValue());
                        ins.setAddress(address);
                        instructions.add(i + 1, ins);
                        increment=LiteralTable.get(j).getLength();
                        i++;
                    }
                }
            }
            if(x.getOperation().startsWith("+"))
            {
                increment=4;
            }
            else if(x.getOperation().startsWith("&"))
            {
                increment=3;
            }
           else if(x.getOperation().startsWith("$"))
            {
                increment=4;
            }
            else if(x.getOperation().equals("RESW")||x.getOperation().equals("resw"))
            {
                int a =Integer.parseInt(x.getOperand());
                increment=a*3;
            }
            else if(x.getOperation().equals("RESB")||x.getOperation().equals("resb"))
            {
                int a =Integer.parseInt(x.getOperand());
                increment=a;
            }
            else if(x.getOperation().equals("BYTE")||x.getOperation().equals("byte"))
            {
                if(x.getOperand().startsWith("X")||x.getOperand().startsWith("x"))
                {
                    int a =(x.getOperand().length()-3)/2;
                    increment=a;
                }
                if(x.getOperand().startsWith("C")||x.getOperand().startsWith("c"))
                {
                    int b =(x.getOperand().length()-3);
                    increment=b;
                }
            }
            else if(x.getOperation().equals("WORD")||x.getOperation().equals("word"))
            {

                increment=3;
            }
            else if (instructions.get(i).getOperation().equalsIgnoreCase("BASE") || instructions.get(i).getOperation().equalsIgnoreCase("END")) {
                increment = 0;
            }
            else {
                String op = x.getOperation();
                Operation o = Operations.get(op);
                try {
                    increment = Integer.parseInt(o.getFormat());
                }
                catch (Exception e)
                {

                }
            }
        }
        increment=0;
        for (int j = 0; j < LiteralTable.size(); j++) {
            if (LiteralTable.get(j).getAddress().equals("")) {
                address=convHexa(address,increment);
                address = addZeros(address, 4);
                LiteralTable.get(j).setAddress(address);
                Instruction ins = new Instruction();
                ins.setLabel("*");
                ins.setOperation(LiteralTable.get(j).getName());
                ins.setOperand("");
                ins.setObjectCode(LiteralTable.get(j).getValue());
                ins.setAddress(address);
                instructions.add( ins);
                increment=LiteralTable.get(j).getLength();
            }
        }

    }

    public void printProgram() throws FileNotFoundException {
        File f = new File("out.txt");
        PrintWriter output = new PrintWriter(f);
        for (int j = 0; j < instructions.size(); j++) {
            Instruction i = instructions.get(j);
            System.out.println( i.getAddress() + "\t" + i.getLabel() + "\t" + i.getOperation() + "\t" + i.getOperand() +"\t"+ i.getObjectCode() + "\t"+"\n");
            output.print(i.getAddress() + "\t" + i.getLabel() + "\t" + i.getOperation() + "\t" + i.getOperand()  + "\t" + i.getObjectCode() + "\t"+"\n" );
        }
        output.close();
    }
    public String getValueofB(){
        for (int i = 0; i < instructions.size(); i++) {
            if(instructions.get(i).getOperation().equalsIgnoreCase("LDB"))
            {
                String operand=instructions.get(i).getOperand();
                if(operand.startsWith("#"))
                    operand=operand.substring(1);

                return symboleTable.get(operand);
            }
        }
        return  "";
    }



    public void printSymTable() throws FileNotFoundException {
        File f = new File("symbTable.txt");
        PrintWriter output = new PrintWriter(f);
        output.println(symboleTable);
        output.close();
    }
    public String fixLineSpacing(String line) {
        String x = line.trim();

        while (x.contains("  ")) {
            x = x.replace("  ", " ");
        }
        return x;
    }

    public String convHexa(String hexanumber, int value) {
        int x = Integer.parseInt(hexanumber, 16);
        x = x + value;
        String m = Integer.toHexString(x);
        m = m.toUpperCase();
        return m;
    }
    public void pass2() {

        for (int i = 1; i < instructions.size(); i++) {
            Instruction is = instructions.get(i);
            String hexa="";
            String operand="";
            String operation="";
            String binary="";
            String opcode="";
            if (is.getLabel().equalsIgnoreCase("*")) {
                continue;
            }
            if (is.getOperation().equalsIgnoreCase("WORD")) {
                int value = Integer.parseInt(is.getOperand());
                hexa = Integer.toHexString(value);
                hexa = hexa.toUpperCase();
                hexa=addZeros(hexa,6);
            } else if (is.getOperation().equalsIgnoreCase("BYTE")) {
                operand=is.getOperand();
                if(operand.startsWith("X"))
                {
                    hexa=operand.substring(2,operand.length()-1);
                }
                else
                {
                    operand=operand.substring(2,operand.length()-1);
                    for (int k =0;k<operand.length();k++)
                    {
                        int x=(int)operand.charAt(k);
                        hexa=hexa+Integer.toHexString(x);
                    }
                }
            } else if(is.getFormat().equals("1")) {
                hexa=Operations.get(is.getOperation()).getOpcode();
            }
            else if(is.getFormat().equals("2")) {
                hexa=Operations.get(is.getOperation()).getOpcode();
                String reg=is.getOperand();
                if(reg.contains(","))
                {
                    String y[]=reg.split(",");
                    hexa=hexa+getRegNum(y[0]);
                    hexa=hexa+getRegNum(y[1]);
                }
                else {
                    hexa = hexa + getRegNum(reg);
                }
            }else if(is.getFormat().equals("3")) {
                hexa=format3(is);
            }else if(is.getFormat().equals("4")) {
                int x=Integer.parseInt(is.getAddress(),16);
                x++;
                String add=Integer.toHexString(x);
                add=addZeros(add,6);
                mrecords.add("M"+add+"05");
                hexa=format4(is);
            }else if(is.getFormat().equals("5")) {
                hexa=format5(is);

            }
            else if(is.getFormat().equals("6")) {
                hexa=format6(is);
            }
            is.setObjectCode(hexa);
        }
    }

    public String addZeros(String x, int num) {
        while (x.length() < num)
            x = "0" + x;
        return x;
    }

    private  String format3(Instruction is)
    {
        String operation=is.getOperation();
        String    opcode=Operations.get(operation).getOpcode();
        String    binary=removezeros(opcode);
        String   operand=is.getOperand();
        String hexa="";

        if(is.getOperand().startsWith("@")) {
            operand=operand.substring(1);
            binary = binary + "10";
        }
        else if(is.getOperand().startsWith("#"))
        {   operand=operand.substring(1);
            binary=binary+"01";
        }
        else
        {
            binary=binary+"11";
        }
        if(operand.endsWith(",X"))
        {
            binary+="1";
        }
        else {
            binary += "0";
        }
        int disp,ta,pc;
        System.out.println(operand);
        String TA = "";
        if (operand.startsWith("=")) {
            for (int j = 0; j < LiteralTable.size(); j++) {
                if (LiteralTable.get(j).getName().equals(operand)) {
                    TA = LiteralTable.get(j).getAddress();
                    break;
                }
            }

        } else {
            TA = symboleTable.get(operand);
        }
        String PC=is.getAddress();
        ta= Integer.parseInt(TA,16);
        pc=Integer.parseInt(PC,16)+Integer.parseInt(is.getFormat());
        disp=ta-pc;
        String DISP;
        if(disp>=-2048&&disp<=2047)
        {
            binary+="010";
            int m=Integer.parseInt(binary,2);
            hexa=Integer.toHexString(m);
            hexa=addZeros(hexa,3);
            DISP=Integer.toHexString(disp);
            DISP=addZeros(DISP,3);
        }
        else
        {
            String baseReg=getValueofB();
            int b= Integer.parseInt(baseReg);
            disp=ta-b;
            binary+="100";
            int m=Integer.parseInt(binary,2);
            hexa=Integer.toHexString(m);
            hexa=addZeros(hexa,3);
            DISP=Integer.toHexString(disp);
            DISP=addZeros(DISP,3);
        }
        hexa=hexa+DISP;
        return  hexa;
    }
    private  String format5(Instruction is)
    {
        String operation=is.getOperation().substring(1);//remove +
        String    opcode=Operations.get(operation).getOpcode();
        String    binary=removezeros(opcode);
        String   operand=is.getOperand();
        String hexa="";


        int disp,ta,pc;
        System.out.println(operand);
        String TA=symboleTable.get(operand);
        String PC=is.getAddress();
        ta= Integer.parseInt(TA,16);
        pc=Integer.parseInt(PC,16)+Integer.parseInt(is.getFormat());
        disp=ta-pc;
        String DISP;
        if(disp>=-2048&&disp<=2047)
        {
            if(disp%2==0)
            {
                binary+="1";
            }
            else {
                binary += "0";
            }
            if(disp>0)
            {
                binary+="0";
            }
            else {
                binary += "1";
            }
            if(operand.endsWith(",X"))
            {
                binary+="1";
            }
            else {
                binary += "0";
            }
            binary+="01";
            if(disp==0)
            {
                binary+="1";
            }
            else {
                binary += "0";
            }
            int m=Integer.parseInt(binary,2);
            hexa=Integer.toHexString(m);
            hexa=addZeros(hexa,3);
            DISP=Integer.toHexString(disp);
            DISP=addZeros(DISP,3);
        }
        else
        {
            String baseReg=getValueofB();
            int b= Integer.parseInt(baseReg);
            disp=ta-b;

            if(disp%2==0)
            {
                binary+="1";
            }
            else {
                binary += "0";
            }
            if(disp>0)
            {
                binary+="0";
            }
            else {
                binary += "1";
            }
            if(operand.endsWith(",X"))
            {
                binary+="1";
            }
            else {
                binary += "0";
            }
            binary+="10";
            if(disp==0)
            {
                binary+="1";
            }
            else {
                binary += "0";
            }
            int m=Integer.parseInt(binary,2);
            hexa=Integer.toHexString(m);
            hexa=addZeros(hexa,3);
            DISP=Integer.toHexString(disp);
            DISP=addZeros(DISP,3);
        }
        hexa=hexa+DISP;
        return  hexa;
    }
    public int Subtract(String TA,String PC)
    {
        int ta= Integer.parseInt(TA,16);
        int pc=Integer.parseInt(PC,16);
        return ta-pc;
    }
    private  String format4(Instruction is)
    {
        String operation=is.getOperation().substring(1);//remove +
        String    opcode=Operations.get(operation).getOpcode();
        String    binary=removezeros(opcode);
        String   operand=is.getOperand();
        String hexa="";
        if(is.getOperand().startsWith("@")) {
            operand=operand.substring(1);
            binary = binary + "10";
        }
        else if(is.getOperand().startsWith("#"))
        {   operand=operand.substring(1);
            binary=binary+"01";
        }
        else {
            binary = binary + "11";
        }

        if(operand.endsWith(",X"))
        {
            binary+="1001";
        }
        else {
            binary += "0001";
        }
        System.out.println(binary);
        int m=Integer.parseInt(binary,2);
        hexa=Integer.toHexString(m);
        hexa=addZeros(hexa,3);
        String address=symboleTable.get(operand);
        address=addZeros(address,5);
        hexa=hexa+address;
        return  hexa;
    }
    private  String format6(Instruction is)
    {
        String operation=is.getOperation().substring(1);//remove +
        String    opcode=Operations.get(operation).getOpcode();
        String    binary=removezeros(opcode);
        String   operand=is.getOperand();
        String hexa="";
        if(is.getOperand().startsWith("@")) {
            operand=operation.substring(1);
            binary = binary + "10";
        }
        else if(is.getOperand().startsWith("#"))
        {   operand=operation.substring(1);
            binary=binary+"01";
        }
        else {
            binary = binary + "11";

        }

        if(operand.endsWith(",X"))
        {
            binary+="1";
        }
        else {
            binary += "0";
        }
        String address=symboleTable.get(operand);
        int add=Integer.parseInt(address,16);
        if(add%2==0)
        {
            binary += "0";
        }
        else

        {
            binary += "1";
        }
        if(add==0)
        {
            binary += "0";
        }
        else
        {
            binary += "1";
        }
        String base=getValueofB();
        int b=Integer.parseInt(base,16);
        if(add==b)
        {
            binary += "0";
        }
        else
        {
            binary += "1";
        }

        System.out.println(binary);
        int m=Integer.parseInt(binary,2);
        hexa=Integer.toHexString(m);
        hexa=addZeros(hexa,3);
        address=addZeros(address,5);
        hexa=hexa+address;
        return  hexa;
    }

    public boolean isDirective(String oper)
    {
        if(oper.equalsIgnoreCase("RESW")||oper.equalsIgnoreCase("RESB")||oper.equalsIgnoreCase("START")||oper.equalsIgnoreCase("END"))
            return  true;
        else
            return false;
    }
    public  String getRegNum(String name)
    {
        if(name.equalsIgnoreCase("A"))
            return "0";
        if(name.equalsIgnoreCase("X"))
            return "1";
        if(name.equalsIgnoreCase("L"))
            return "2";
        if(name.equalsIgnoreCase("B"))
            return "3";
        if(name.equalsIgnoreCase("S"))
            return "4";
        if(name.equalsIgnoreCase("T"))
            return "5";
        if(name.equalsIgnoreCase("F"))
            return "6";
        return  "0";
    }
    public String removezeros(String opcode)
    {
        int x=Integer.parseInt(opcode,16);
        String binary=Integer.toBinaryString(x);
        binary=addZeros(binary,8);
        binary=binary.substring(0,binary.length()-2);
        return binary;
    }
    public void WriteHTEFile() throws IOException {
        File f = new File("HTE.txt");
        FileWriter fw = new FileWriter(f);
        String progname = instructions.get(0).getLabel();
        while (progname.length() < 6) {
            progname = progname + " ";
        }
        String st = instructions.get(0).getOperand();
        String en = instructions.get(instructions.size() - 1).getAddress();
        int start = Integer.parseInt(st, 16);
        int end = Integer.parseInt(en, 16);
        int length = end - start;
        String programLength = Integer.toHexString(length);
        while (programLength.length() < 6) {
            programLength = "0" + programLength;
        }
        while (st.length() < 6) {
            st = "0" + st;
        }
        fw.write("H" + progname + st + programLength);
        fw.write(System.lineSeparator());
        String objectcode = "";
        String len = "";
        String add = "";
        for (int i = 1; i < instructions.size() ; i++) {
            if (instructions.get(i).getOperation().equalsIgnoreCase("BASE"))
                continue;
            if (add.equals("") && !instructions.get(i).getObjectCode().equals("")) {
                add = instructions.get(i).getAddress();
            }
            while (add.length() < 6) {
                add = "0" + add;
            }
            if (objectcode.length() + instructions.get(i).getObjectCode().length() <= 60
                    && instructions.get(i).getObjectCode().length() > 0) {
                objectcode = objectcode + instructions.get(i).getObjectCode();
            } else {
                if (objectcode.length() > 0) {
                    len = Integer.toHexString(objectcode.length() / 2);
                    if (len.length() < 2) {
                        len = "0" + len;
                    }
                    fw.write("T" + add + len + objectcode);
                    fw.write(System.lineSeparator());

                }add = "";
                objectcode = instructions.get(i).getObjectCode();
                if (!objectcode.equals("")) {
                    add = instructions.get(i).getAddress();
                }
            }

        }
        if (objectcode.length() > 0) {
            len = Integer.toHexString(objectcode.length() / 2);
            if (len.length() < 2) {
                len = "0" + len;
            }
            fw.write("T" + add + len + objectcode);
            fw.write(System.lineSeparator());

        }


        for (int i = 0; i < mrecords.size(); i++) {
            fw.write(mrecords.get(i));
            fw.write(System.lineSeparator());
        }



        fw.write("E" + st);
        fw.close();

    }
}
