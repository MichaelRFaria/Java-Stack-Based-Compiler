package handbuilt;

import ast.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Ex_c {

    public static Program buildAST() {
        List<VarDecl> decls = new ArrayList<>();
        List<Stm> stms = new ArrayList<>();
        List<Stm> stmsBody = new ArrayList<>();

        decls.add(new VarDecl(new TypeInt(), "count"));
        stms.add(new StmAssign("count",new ExpInt(3)));

        stmsBody.add(new StmPrintChar(new ExpInt(32)));
        stmsBody.add(new StmPrint(new ExpVar("count")));
        Exp e = new ExpMinus(new ExpVar("count"), new ExpInt(1));
        stmsBody.add(new StmAssign("count",e));

        stms.add(new StmWhile(new ExpLessThan(new ExpInt(0),new ExpPlus(new ExpVar("count"), new ExpInt(1)))
                , new StmBlock(stmsBody)));

        return new Program(decls, stms);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(buildAST());

        Program p = buildAST();

        p.compile();

        AST.write(Paths.get("Ex_c.ssma"));
    }
}
