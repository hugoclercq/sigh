package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;
import java.util.List;

public class RuleDeclarationNode extends DeclarationNode{

    public final String name;
    public final List<ParameterNode> atoms;
    public final List<FactNode> facts;

    public RuleDeclarationNode (Span span, Object name, Object atoms, Object facts) {
        super(span);
        this.name = Util.cast(name, String.class);
        this.atoms = Util.cast(atoms, List.class);
        this.facts = Util.cast(facts, List.class);
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
        return "rule " + name ;
    }

}