package com.blaine.lox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.blaine.lox.generated.Expr;

class Lox {
    public static void main(String[] args) throws Exception {
        if (args.length > 1) {
            System.out.println("usage: jlox [script]");
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
    }

    private static void runPrompt() throws Exception {
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader bufferReader = new BufferedReader(reader); 

        while (true) {
            System.out.print("> ");
            String line = bufferReader.readLine();
            if (line == null) break;
            run(line);
        }
    }

    private static void run(String script) {
        Scanner scanner = new Scanner(script);
        List<Token> tokens = scanner.scan();

        for (Token token : tokens) {
            System.out.println(String.format("%s %s line:%d column:%d", token.lexeme, token.type.name(), token.line, token.column));
        }
    }
}