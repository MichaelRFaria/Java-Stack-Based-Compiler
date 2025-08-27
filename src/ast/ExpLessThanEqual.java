package ast;

import compile.SymbolTable;

public class ExpLessThanEqual extends Exp {

    public final Exp left, right;

    public ExpLessThanEqual(Exp left, Exp right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void compile(SymbolTable st) {
        String endLabel = st.freshLabel("end");
        emit("push 1");

        left.compile(st);
        right.compile(st);
        emit("sub");

        emit("jumpi_n " + endLabel);

        left.compile(st);
        right.compile(st); // 1 2 2
        emit("sub"); // 1 0
        emit("test_z");
        emit("swap");
        emit("pop");

        emit(endLabel + ":");
    }

    // 1 <= 2, -1, 1, 1 1, 0 1, 1, 0

    @Override
    public <T> T accept(ast.util.Visitor<T> visitor) { return visitor.visit(this); }

}
