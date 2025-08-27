package handbuilt;

import ast.*;

import java.util.ArrayList;
import java.util.List;

public class Ex_b {

    public static Program buildAST() {
        List<VarDecl> decls = new ArrayList<>();
        List<Stm> stms = new ArrayList<>();

        decls.add(new VarDecl(new TypeInt(), "part"));
        decls.add(new VarDecl(new TypeInt(), "y"));

        Exp ve1 = new ExpPlus(new ExpInt(6), new ExpVar("y"));
        stms.add(new StmAssign("part", ve1));

        Exp ve2 = new ExpMinus(new ExpVar("part"), new ExpInt(3));
        stms.add(new StmAssign("y", ve2));

        Exp ve3 = new ExpPlus(new ExpVar("part"), new ExpVar("y"));

        Exp c = new ExpTimes(ve3, new ExpInt(10));

        stms.add(new StmPrintChar(c));

        return new Program(decls, stms);
    }

    public static void main(String[] args) {
        System.out.println(buildAST());
    }
}
