package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;
import java.util.List;

public class FactDeclarationNode extends DeclarationNode{

    public final String name;
    public final List<ExpressionNode> atoms;

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
        String[] tmp = new String[atoms.size()];
        for(int i = 0; i<tmp.length; i++){
            ExpressionNode atom = atoms.get(i);
            if(atom instanceof AtomNode)
                tmp[i] = ((AtomNode) atom).name;
            else if (atom instanceof ReferenceNode)
                tmp[i] = ((ReferenceNode) atom).name;
        }
        return "(" + String.join(", ", tmp) + ")";
    }

    @Override
    public String contents () {
        return "fact " + name;
    }
}
