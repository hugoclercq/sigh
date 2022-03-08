package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;

public class AtomNode extends ExpressionNode{

    public final String name;

    public AtomNode (Span span, String name) {
        super(span);
        this.name = name;
    }

    public String name () {
        return name;
    }

    @Override
    public String contents () {

        System.out.println("slt");
        return name;
    }
}
