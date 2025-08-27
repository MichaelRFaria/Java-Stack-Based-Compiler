package handbuilt;

import ast.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Ex_d {

    public static Program buildAST() {
        List<VarDecl> decls = new ArrayList<>();
        List<Stm> stms = new ArrayList<>();
        List<Stm> left = new ArrayList<>();
        List<Stm> right = new ArrayList<>();

        decls.add(new VarDecl(new TypeInt(),"x"));
        stms.add(new StmAssign("x", new ExpInt(20)));

        Exp cond1 = new ExpLessThan(new ExpVar("x"), new ExpInt(20));

        left.add(new StmAssign("x", new ExpMinus(new ExpVar("x"), new ExpInt(7))));

        Exp cond2 = new ExpLessThan(new ExpVar("x"), new ExpInt(30));
        right.add(new StmIf(cond2,new StmPrintln(new ExpInt(77)), new StmPrintln(new ExpInt(88))));

        stms.add(new StmIf(cond1, new StmBlock(left), new StmBlock(right)));

        stms.add(new StmPrintln(new ExpVar("x")));

        return new Program(decls, stms);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(buildAST());

        Program p = buildAST();

        p.compile();

        AST.write(Paths.get("Ex_d.ssma"));
    }
}
