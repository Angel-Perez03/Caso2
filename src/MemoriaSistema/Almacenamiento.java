package MemoriaSistema;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Almacenamiento {
    public static BufferedReader openFile(String filename) throws IOException {
        return new BufferedReader(new FileReader(filename));
    }
}
