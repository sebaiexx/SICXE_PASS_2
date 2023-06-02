package com.company;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws Exception{
        Pass1 a =new Pass1() ;
        a.readOperations();
        a.readFile();
        a.p1();
        a.pass2();
        a.printSymTable();
        a.printProgram();
        a.WriteHTEFile();
    }
}
