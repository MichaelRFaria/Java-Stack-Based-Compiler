package parse;

import ast.*;
import sbnf.ParseException;
import sbnf.lex.Lexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** Parse an LPL program and build its AST.  */
public class LPLParser {

    /**
     * File path for the LPL SBNF file.
     * Note: this is a relative path so at runtime the working directory must
     * be the parent directory of this path (ie the root folder
     * of the project) or the file will not be found.
     */
    public static final String LPL_SBNF_FILE = "data/LPL.sbnf";

    private Lexer lex;

    /**
     * Initialise a new LPL parser.
     */
    public LPLParser() {
        lex = new Lexer(LPL_SBNF_FILE);
    }

    /** Parse an LPL source file and return its AST.
     *
     * @param sourcePath a path to an LPL source file
     * @return the AST for the parsed LPL program
     * @throws ParseException if the source file contains syntax errors
     * @throws IOException
     */
    public Program parse(String sourcePath) throws IOException {
        lex.readFile(sourcePath);
        lex.next();
        Program prog = Program();
        if (!lex.tok().isType("EOF")) {
            throw new ParseException(lex.tok(), "EOF");
        }
        return prog;
    }

    private Program Program() {
        List<VarDecl> globals = new LinkedList<>();
        List<Stm> body = new LinkedList<>();
        lex.eat("BEGIN");

        while (lex.tok().isType("INT_TYPE")) {
            lex.eat("INT_TYPE");
            String varName = lex.eat("ID");
            lex.eat("SEMIC");
            globals.add(new VarDecl(new TypeInt(), varName));
        }

        while (!lex.tok().isType("END")) {
            body.add(Stm());
        }

        lex.eat("END");
        return new Program(globals, body);
    }

    private Stm Stm() {
        switch (lex.tok().type) {
            case "LCBR":  // Stm -> LCBR Stm* RCBR -- block
                lex.next();
                List<Stm> blockBody = new ArrayList<>();
                while (!lex.tok().isType("RCBR")) {
                    blockBody.add(Stm());
                }
                lex.eat("RCBR");
                return new StmBlock(blockBody);
            case "ID": {  // Stm -> ID ASSIGN Exp SEMIC -- assignment statement
                String varName = lex.tok().image;
                lex.eat("ID");
                lex.eat("ASSIGN");
                Exp exp = Exp();
                lex.eat("SEMIC");
                return new StmAssign(varName, exp);
            }
            case "IF": {
                lex.eat("IF");
                lex.eat("LBR");
                Exp exp = Exp();
                lex.eat("RBR");
                Stm trueBranch = Stm();
                lex.eat("ELSE");
                Stm falseBranch = Stm();
                return new StmIf(exp, trueBranch, falseBranch);
            }
            case "WHILE": {
                lex.eat("WHILE");
                lex.eat("LBR");
                Exp exp = Exp();
                lex.eat("RBR");
                Stm stm = Stm();
                return new StmWhile(exp, stm);
            }
            case "PRINT": {
                lex.eat("PRINT");
                Exp exp = Exp();
                lex.eat("SEMIC");
                return new StmPrint(exp);
            }
            case "PRINTCH": {
                lex.eat("PRINTCH");
                Exp exp = Exp();
                lex.eat("SEMIC");
                return new StmPrintChar(exp);
            }
            case "PRINTLN": {
                lex.eat("PRINTLN");
                Exp exp = Exp();
                lex.eat("SEMIC");
                return new StmPrintln(exp);
            }
            case "NEWLINE": {
                lex.eat("NEWLINE");
                lex.eat("SEMIC");
                return new StmNewline();
            }
            case "SWITCH": {
                lex.eat("SWITCH");
                lex.eat("LBR");
                Exp exp = Exp();
                lex.eat("RBR");
                lex.eat("LCBR");

                List<StmSwitch.Case> cases = new ArrayList<>();
                while (!lex.tok().isType("DEFAULT")) {
                    cases.add(Case());
                }
                lex.eat("DEFAULT");
                lex.eat("COLON");
                Stm defaultCase = Stm();
                lex.eat("RCBR");

                return new StmSwitch(exp, defaultCase, cases);
            }
            default:
                throw new ParseException(lex.tok(), "Stm");
        }
    }

    private StmSwitch.Case Case() { // maybe implement OptionalSign as class
        lex.eat("CASE");
        boolean isNegative = false;

        if (lex.tok().isType("MINUS")) {
            lex.eat("MINUS");
            isNegative = true;
        }

        // 204 "729713"

        if (lex.tok().isType("INTLIT")) { /// fix for test 400 10 a (204) (colon as input - no clue why this happens)
            int value = Integer.parseInt(lex.tok().image);
            lex.eat("INTLIT");
            if (isNegative) {value = -value; }
            lex.eat("COLON");

            return new StmSwitch.Case(value, Stm());
        } else {
            throw new ParseException(lex.tok(), "Colon as input");
        }
    }

    private Exp Exp() { // finished (may need proper throw parse error instead of null, workaround may not work for mutant tests)
        Exp left = SimpleExp();
        Exp right = OperatorClause(left);
        if (right == null) {return left; }
        else {return right; }
    }

    private Exp SimpleExp() { // finished
        System.out.println("Token: " + lex.tok());
        switch (lex.tok().type) {
            case "ID":
                String idName = lex.tok().image;
                lex.eat("ID");
                return new ExpVar(idName);
            case "INTLIT":
                int value = Integer.parseInt(lex.tok().image);
                lex.eat("INTLIT");
                return new ExpInt(value);
            case "MINUS": // fix for (110 and 214) (double minus) doesn't work for 274 for some reason
                lex.eat("MINUS");
                if (lex.tok().isType("MINUS")) {
                    throw new ParseException(lex.tok(), "Second minus sign");
                }
                return new ExpMinus(new ExpInt(0), SimpleExp());
            case "NOT":
                lex.eat("NOT");
                return new ExpNot(SimpleExp());
            case "LBR":
                lex.eat("LBR");
                Exp exp = Exp();
                lex.eat("RBR");
                return exp;
            default:
                throw new ParseException(lex.tok(), "SimpleExp");
        }
    }

    private Exp OperatorClause(Exp left) { // finished
        switch (lex.tok().type) {
            case "MUL":
                lex.eat("MUL");
                return new ExpTimes(left, SimpleExp());
            case "DIV":
                lex.eat("DIV");
                return new ExpDiv(left, SimpleExp());
            case "MINUS":
                lex.eat("MINUS");
                return new ExpMinus(left, SimpleExp());
            case "ADD":
                lex.eat("ADD");
                return new ExpPlus(left, SimpleExp());
            case "LT":
                lex.eat("LT");
                return new ExpLessThan(left, SimpleExp());
            case "LE":
                lex.eat("LE");
                return new ExpLessThanEqual(left, SimpleExp());
            case "EQ":
                lex.eat("EQ");
                return new ExpEqual(left, SimpleExp());
            case "AND":
                lex.eat("AND");
                return new ExpAnd(left, SimpleExp());
            case "OR":
                lex.eat("OR");
                return new ExpOr(left, SimpleExp());
            default:
                return null;
        }
    }

//    private Exp OptionalSign(Exp right) {
//        switch (lex.tok().type) {
//            case "MINUS":
//                lex.eat("MINUS");
//                return new ExpMinus(new ExpInt(0), right);
//            default:
//                return null;
//        }
//    }

    /**
     * Parse and pretty-print an LPL source file specified
     * by a command line argument.
     * @param args command-line arguments
     * @throws ParseException if the source file contains syntax errors
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: parse.LPLParser <source-file>");
            System.exit(1);
        }
        LPLParser parser = new LPLParser();
        Program program = parser.parse(args[0]);
        System.out.println(program);
    }
}
