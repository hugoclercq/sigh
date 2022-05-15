package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;
import java.util.List;

public class FactNode extends ExpressionNode{
    public final String name;
    public final List<ExpressionNode> arguments;

    @SuppressWarnings("unchecked")
    public FactNode (Span span, Object name, Object arguments) {
        super(span);
        this.name = Util.cast(name, String.class);
        this.arguments = Util.cast(arguments, List.class);
    }

    @Override public String contents ()
    {
        String args = arguments.size() == 0 ? "()" : "(...)";
        return name + args;
    }

}
