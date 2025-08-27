package ast;

import compile.StaticAnalysisException;
import compile.SymbolTable;

import java.util.*;

public class StmSwitch extends Stm {

    public final Exp caseExp;
    public final Stm defaultCase;
    public final List<Case> cases;

    public StmSwitch(Exp caseExp, Stm defaultCase, List<Case> cases) {
        this.caseExp = caseExp;
        this.defaultCase = defaultCase;
        this.cases = cases;
    }

    @Override
    public void compile(SymbolTable st) {
        String endLabel = st.freshLabel("end");
        String defaultLabel = st.freshLabel("default");

        HashMap<Integer, String> hm = new HashMap<>();
        for (Case cs : cases) {
            hm.put(cs.caseNumber, st.freshLabel("case_" + Math.abs(cs.caseNumber)));
        }

        for (Case cs : cases) {
            String caseLabel = hm.get(cs.caseNumber);
            if (cs.caseNumber < 0) {
                caseLabel += "_negative";
                hm.put(cs.caseNumber, caseLabel);
            }

            caseExp.compile(st);
            emit("push " + cs.caseNumber);
            emit("sub");
            emit("jumpi_z " + caseLabel);
        }

        emit("jumpi " + defaultLabel);

        for (Case cs : cases) {
            emit(hm.get(cs.caseNumber) + ":");
            cs.stm.compile(st);
            emit("jumpi " + endLabel);
        }

        emit(defaultLabel + ":");
        defaultCase.compile(st);

        emit(endLabel + ":");
    }

    public static class Case {

        public final int caseNumber;
        public final Stm stm;

        public Case(int caseNumber, Stm stm) {
            this.caseNumber = caseNumber;
            this.stm = stm;
        }
    }

    @Override
    public <T> T accept(ast.util.Visitor<T> visitor) { return visitor.visit(this); }

}
