package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;

public class AtomNode extends DeclarationNode{

    public final String name;

    public AtomNode (Span span, String name) {
        super(span);
        this.name = name;
    }

    @Override
    public String name () {
        return name;
    }

    @Override
    public String declaredThing () {
        return "atom";
    }

    @Override
    public String contents () {
        return name;
    }
}
