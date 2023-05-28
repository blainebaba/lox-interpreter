package com.blaine.lox.interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.blaine.lox.Scanner;
import com.blaine.lox.Token;
import com.blaine.lox.generated.Stmt;
import com.blaine.lox.generated.Stmt.DeclareStmt;
import com.blaine.lox.generated.Stmt.ExpressionStmt;
import com.blaine.lox.generated.Stmt.PrintStmt;
import com.blaine.lox.parser.Parser;
import com.blaine.lox.parser.ParserError;

// test statement evaluation and execution
public class StmtExecuteTest {

    // we can check internal states in interpreter to check correctness.
    private Interpreter interpreter;

    @Before
    public void setup() {
        interpreter = new Interpreter();
    }

    private Stmt parseOneStatement(String script) {
        List<Token> tokens = new Scanner(script).scan();
        Parser parser = new Parser(tokens);
        Stmt stmt = parser.parseStatement();
        assertTrue(parser.isEnd());
        return stmt;
    }

    private void parseExpectError(String script) {
        List<Token> tokens = new Scanner(script).scan();
        Parser parser = new Parser(tokens);
        try {
            parser.parseStatement();
        } catch (ParserError e) {
            return;
        }
        fail("parser error expected");
    }

    private void executeExpectError(Stmt stmt) {
        try {
            stmt.accept(interpreter);
        } catch (RuntimeError e) {
            return;
        }
        fail("runtime error expected");
    }

    @Test
    public void testPrintStmt() {
        Stmt stmt = parseOneStatement("print 1 + 1;");
        assertEquals(PrintStmt.class, stmt.getClass());
        interpreter.execute(stmt);
    }

    @Test
    public void testExpressionStmt() {
        Stmt stmt = parseOneStatement("1 + 1;");
        assertEquals(ExpressionStmt.class, stmt.getClass());
        interpreter.execute(stmt);
    }
    
    @Test
    public void testDeclareStmt() {
        // happy case
        {
            Stmt stmt = parseOneStatement("var a = 1;");
            assertEquals(DeclareStmt.class, stmt.getClass());

            interpreter.execute(stmt);
            Object value = interpreter.getEnv().evaluateGlobalVar("a");
            assertEquals(1.0, value);
        }
        // invalid statements
        {
            parseExpectError("var 123 = 1;");
            parseExpectError("var 123 = print 123;");
            parseExpectError("var a == b;");
        }
    }
}
