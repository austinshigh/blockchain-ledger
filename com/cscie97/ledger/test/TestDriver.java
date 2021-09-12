package com.cscie97.ledger.test;

import com.cscie97.ledger.CommandProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestDriver {
    public static void main(String[] args) {
        CommandProcessor test = new CommandProcessor();
        test.processCommandFile(args[0]);
    }
}