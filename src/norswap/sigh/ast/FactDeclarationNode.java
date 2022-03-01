package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;
import java.util.List;

public class FactDeclarationNode extends DeclarationNode{

    public final String name;
    public final List<AtomNode> atoms;

    public FactDeclarationNode
        (Span span, Object name, Object atoms) {
        super(span);
        this.name = Util.cast(name, String.class);
        this.atoms = Util.cast(atoms, List.class);
    }

    @Override
    public String name () {
        return name;
    }

    @Override
    public String declaredThing () {
        return "fact";
    }

    @Override
    public String contents () {
        return "fact" + name;
    }
}
