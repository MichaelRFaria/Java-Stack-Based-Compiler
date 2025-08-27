package ast;

import compile.StaticAnalysisException;
import compile.SymbolTable;

public class StmWhile extends Stm {

    public final Exp exp;

    public final Stm body;

    public StmWhile(Exp exp, Stm body) {
        this.exp = exp;
        this.body = body;
    }

    @Override
    public void compile(SymbolTable st) {
        String startLabel = st.freshLabel("start");
        String endLabel = st.freshLabel("end");

        emit(startLabel + ":");

        exp.compile(st);
        emit("push 1");
        emit("sub");
        emit("jumpi_n " + endLabel);

        body.compile(st);
        emit("jumpi " + startLabel);

        emit( endLabel + ":");
    }

    @Override
    public <T> T accept(ast.util.Visitor<T> visitor) { return visitor.visit(this); }
}
