import norswap.autumn.AutumnTestFixture;
import norswap.sigh.SighGrammar;
import norswap.sigh.ast.*;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static norswap.sigh.ast.BinaryOperator.*;

public class GrammarTests extends AutumnTestFixture {
    // ---------------------------------------------------------------------------------------------

    private final SighGrammar grammar = new SighGrammar();
    private final Class<?> grammarClass = grammar.getClass();

    // ---------------------------------------------------------------------------------------------

    private static IntLiteralNode intlit (long i) {
        return new IntLiteralNode(null, i);
    }

    private static FloatLiteralNode floatlit (double d) {
        return new FloatLiteralNode(null, d);
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void atom_decl () {
        rule = grammar.statement;

        successExpect("var X: Atom = _jean", new VarDeclarationNode(null,
            "X", new SimpleTypeNode(null, "Atom"), new AtomNode(null, "_jean")));

        successExpect("var X: Atom = failure(Smtg)", new VarDeclarationNode(null,
            "X", new SimpleTypeNode(null, "Atom"), new FunCallNode(null, new ReferenceNode(null,"failure"), asList(new ReferenceNode(null, "Smtg")))));

        successExpect("var X: Atom = Jean", new VarDeclarationNode(null,
            "X", new SimpleTypeNode(null, "Atom"), new ReferenceNode(null, "Jean")));

        failure("var x: Atom = _jean");
    }

    @Test
    public void fact_decl () {
        rule = grammar.fact_decl;

        successExpect("fact likes(_john, _marry)", new FactDeclarationNode(null, "likes", asList(new AtomNode(null, "_john"), new AtomNode(null, "_marry"))));
        successExpect("fact human(_jean)", new FactDeclarationNode(null, "human", asList(new AtomNode(null, "_jean"))));
        successExpect("fact test()", new FactDeclarationNode(null, "test", asList()));

        failure("fact Test(_john, _marry)");
        failure("Fact test(_john, _marry)");

        //failure as of now but should be success at some point later
        failure("fact likes(_john, Who)");
    }

    @Test
    public void rule_decl () {
        rule = grammar.rule_decl;

        successExpect("rule sibling(_x, _y) := sibling(_y, _x)", new RuleDeclarationNode(null, "sibling", asList(new AtomNode(null, "_x"), new AtomNode(null, "_y")), "sibling", asList(new AtomNode(null, "_y"), new AtomNode(null, "_x"))));
        /*
        successExpect("fact human(jean)", new FactDeclarationNode(null, "human", asList(new AtomNode(null, "jean"))));
        successExpect("fact test()", new FactDeclarationNode(null, "test", asList()));

        failure("fact Test(john, marry)");
        failure("Fact test(john, marry)");

        //failure as of now but should be success at some point later
        failure("fact likes(john, Who)");
        failure("fact likes(john, Who[1])");
        failure("fact likes(john, var Who:Atom");
        */
    }


    @Test
    public void testLiteralsAndUnary () {
        rule = grammar.expression;

        successExpect("42", intlit(42));
        successExpect("42.0", floatlit(42d));
        successExpect("\"hello\"", new StringLiteralNode(null, "hello"));
        successExpect("(42)", new ParenthesizedNode(null, intlit(42)));
        successExpect("[1, 2, 3]", new ArrayLiteralNode(null, asList(intlit(1), intlit(2), intlit(3))));
        successExpect("true", new ReferenceNode(null, "true"));
        successExpect("false", new ReferenceNode(null, "false"));
        successExpect("null", new ReferenceNode(null, "null"));
        successExpect("!false", new UnaryExpressionNode(null, UnaryOperator.NOT, new ReferenceNode(null, "false")));
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testNumericBinary () {
        successExpect("1 + 2", new BinaryExpressionNode(null, intlit(1), ADD, intlit(2)));
        successExpect("2 - 1", new BinaryExpressionNode(null, intlit(2), SUBTRACT,  intlit(1)));
        successExpect("2 * 3", new BinaryExpressionNode(null, intlit(2), MULTIPLY, intlit(3)));
        successExpect("2 / 3", new BinaryExpressionNode(null, intlit(2), DIVIDE, intlit(3)));
        successExpect("2 % 3", new BinaryExpressionNode(null, intlit(2), REMAINDER, intlit(3)));

        successExpect("1.0 + 2.0", new BinaryExpressionNode(null, floatlit(1), ADD, floatlit(2)));
        successExpect("2.0 - 1.0", new BinaryExpressionNode(null, floatlit(2), SUBTRACT, floatlit(1)));
        successExpect("2.0 * 3.0", new BinaryExpressionNode(null, floatlit(2), MULTIPLY, floatlit(3)));
        successExpect("2.0 / 3.0", new BinaryExpressionNode(null, floatlit(2), DIVIDE, floatlit(3)));
        successExpect("2.0 % 3.0", new BinaryExpressionNode(null, floatlit(2), REMAINDER, floatlit(3)));

        successExpect("2 * (4-1) * 4.0 / 6 % (2+1)", new BinaryExpressionNode(null,
            new BinaryExpressionNode(null,
                new BinaryExpressionNode(null,
                    new BinaryExpressionNode(null,
                        intlit(2),
                        MULTIPLY,
                        new ParenthesizedNode(null, new BinaryExpressionNode(null,
                            intlit(4),
                            SUBTRACT,
                            intlit(1)))),
                    MULTIPLY,
                    floatlit(4d)),
                DIVIDE,
                intlit(6)),
            REMAINDER,
            new ParenthesizedNode(null, new BinaryExpressionNode(null,
                intlit(2),
                ADD,
                intlit(1)))));
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testArrayStructAccess () {
        rule = grammar.expression;
        successExpect("[1][0]", new ArrayAccessNode(null,
            new ArrayLiteralNode(null, asList(intlit(1))), intlit(0)));
        successExpect("[1].Length", new FieldAccessNode(null,
            new ArrayLiteralNode(null, asList(intlit(1))), "Length"));
        successExpect("P.X", new FieldAccessNode(null, new ReferenceNode(null, "P"), "X"));
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testDeclarations() {
        rule = grammar.statement;

        successExpect("var X: Int = 1", new VarDeclarationNode(null,
            "X", new SimpleTypeNode(null, "Int"), intlit(1)));

        successExpect("struct P {}", new StructDeclarationNode(null, "P", asList()));

        successExpect("struct P { var X: Int; var Y: Int }",
            new StructDeclarationNode(null, "P", asList(
                new FieldDeclarationNode(null, "X", new SimpleTypeNode(null, "Int")),
                new FieldDeclarationNode(null, "Y", new SimpleTypeNode(null, "Int")))));

        successExpect("fun f (X: Int): Int { return 1 }",
            new FunDeclarationNode(null, "f",
                asList(new ParameterNode(null, "X", new SimpleTypeNode(null, "Int"))),
                new SimpleTypeNode(null, "Int"),
                new BlockNode(null, asList(new ReturnNode(null, intlit(1))))));
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testStatements() {
        rule = grammar.statement;

        successExpect("return", new ReturnNode(null, null));
        successExpect("return 1", new ReturnNode(null, intlit(1)));
        successExpect("print(1)", new ExpressionStatementNode(null,
            new FunCallNode(null, new ReferenceNode(null, "print"), asList(intlit(1)))));
        successExpect("{ return }", new BlockNode(null, asList(new ReturnNode(null, null))));


        successExpect("if true return 1 else return 2", new IfNode(null, new ReferenceNode(null, "true"),
            new ReturnNode(null, intlit(1)),
            new ReturnNode(null, intlit(2))));

        successExpect("if false return 1 else if true return 2 else return 3 ",
            new IfNode(null, new ReferenceNode(null, "false"),
                new ReturnNode(null, intlit(1)),
                new IfNode(null, new ReferenceNode(null, "true"),
                    new ReturnNode(null, intlit(2)),
                    new ReturnNode(null, intlit(3)))));

        successExpect("while 1 < 2 { return } ", new WhileNode(null,
            new BinaryExpressionNode(null, intlit(1), LOWER, intlit(2)),
            new BlockNode(null, asList(new ReturnNode(null, null)))));
    }

    // ---------------------------------------------------------------------------------------------
}
