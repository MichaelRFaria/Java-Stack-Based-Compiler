package handbuilt;

import ast.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

public class Ex_a {

    public static Program buildAST() {
        List<VarDecl> decls = new ArrayList<>();
        List<Stm> stms = new ArrayList<>();

        decls.add(new VarDecl(new TypeInt(), "x"));
        stms.add(new StmAssign("x", new ExpInt(3)));

        Exp e = new ExpTimes(new ExpVar("x"),new ExpInt(9));
        stms.add(new StmPrint(e));

        return new Program(decls, stms);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(buildAST());

        Program p = buildAST();

        p.compile();

        AST.write(Paths.get("Ex_a.ssma"));
    }
}
