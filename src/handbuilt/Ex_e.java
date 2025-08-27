package handbuilt;

import ast.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Ex_e {

    public static Program buildAST() {
        List<VarDecl> decls = new ArrayList<>();
        List<Stm> stms = new ArrayList<>();
        List<StmSwitch.Case> cases = new ArrayList<>();

        decls.add(new VarDecl(new TypeInt(),"x"));
        decls.add(new VarDecl(new TypeInt(), "zz"));

        stms.add(new StmAssign("x", new ExpMinus(new ExpVar("x"), new ExpInt(1))));
        stms.add(new StmAssign("zz", new ExpInt(55)));

        cases.add(new StmSwitch.Case(7, new StmPrintln(new ExpInt(99))));
        cases.add(new StmSwitch.Case(-1, new StmPrintln(new ExpPlus(new ExpVar("x"), new ExpVar("zz")))));

        stms.add(new StmSwitch(new ExpVar("x"),
                new StmPrintln(new ExpVar("x")),
                cases));

        return new Program(decls, stms);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(buildAST());

        Program p = buildAST();

        p.compile();

        AST.write(Paths.get("Ex_e.ssma"));
    }
}
