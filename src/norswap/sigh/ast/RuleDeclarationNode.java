package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;
import java.util.List;

public class RuleDeclarationNode extends DeclarationNode{

    public final String name;
    public final List<AtomNode> atoms;
    public final String name2;
    public final List<AtomNode> atoms2;

    public RuleDeclarationNode (Span span, Object name, Object atoms, Object name2, Object atoms2) {
        super(span);
        this.name = Util.cast(name, String.class);
        this.atoms = Util.cast(atoms, List.class);
        this.name2 = Util.cast(name2, String.class);
        this.atoms2 = Util.cast(atoms2, List.class);
    }


    @Override
    public String name () {
            return name;
        }

    @Override
    public String declaredThing () {
            return "rule";
        }

    @Override
    public String contents () {
        return "rule " + name;
    }

}