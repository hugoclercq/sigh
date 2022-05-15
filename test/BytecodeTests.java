import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import norswap.sigh.SemanticAnalysis;
import norswap.sigh.SighGrammar;
import norswap.sigh.ast.SighNode;
import norswap.sigh.bytecode.ByteArrayClassLoader;
import norswap.sigh.bytecode.BytecodeCompiler;
import norswap.sigh.bytecode.CompilationResult;
import norswap.uranium.Reactor;
import norswap.utils.IO;
import norswap.utils.visitors.Walker;
import org.testng.annotations.Test;

import static norswap.utils.Util.cast;
import static org.testng.Assert.assertEquals;

@SuppressWarnings("FieldCanBeLocal")
public class BytecodeTests
{
    // TODO test multi dimensional arrays

    // ---------------------------------------------------------------------------------------------

    /**
     * Checks that the input program can be compiled, run and prints the {@code expected} string if
     * non-null (to which a newline is appended if not empty).
     */
    public void check (String input, String expected)
    {
        SighGrammar grammar = new SighGrammar();
        ParseOptions options = ParseOptions.builder().recordCallStack(true).get();
        ParseResult parseResult = Autumn.parse(grammar.root, input, options);
        if (!parseResult.fullMatch) throw new AssertionError(parseResult.toString());

        SighNode tree = cast(parseResult.topValue());
        Reactor reactor = new Reactor();
        Walker<SighNode> walker = SemanticAnalysis.createWalker(reactor);
        walker.walk(tree);
        reactor.run();

        if (!reactor.errors().isEmpty())
            throw new AssertionError(reactor.reportErrors(Object::toString));

        String className = "BytecodeTestsRun";
        BytecodeCompiler compiler = new BytecodeCompiler(reactor);
        CompilationResult result = compiler.compile(className, tree);

        // using a new loader each time allows to overwrite the class every time.
        Class<?> mainClass = result.load(new ByteArrayClassLoader());

        if (expected == null) {
            CompilationResult.callMain(mainClass);
            return;
        }

        // TODO utils capture stdout with runnable
        String capture = IO.captureStdout(() -> {
            CompilationResult.callMain(mainClass);
            return null;
        }).a;

        if (!expected.isEmpty())
            expected = expected + "\n";

        assertEquals(capture, expected);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Checks that the input <b>expression</b> can be converted to a string and printed, and that the printed
     * string corresponds to the {@code expected} string.
     */
    public void checkExpr (String input, String expected) {
        check("print(\"\" + ("+ input + "))", expected);
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void AtomTest() {
        checkExpr("_jhon", "_jhon");

        check("var X: Atom = _jhon; print(\"\" + X)", "_jhon");
    }

    @Test
    public void basicPrintTest() {
        // explicit unroll basic tests that involve print
        check("print(\"hello\")", "hello");
        check("print(\"hello\") ; return", "hello");
        check("print(\"\" + 1)", "1");
        check("print(\"\" + 1 + 2)", "12");
        check("print(1 + 2 + \"\")", "3");
        check("print(\"\" + (1 + 2))", "3");
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testLiteralsAndUnary () {
        checkExpr("42", "42");
        checkExpr("42.0", "42.0");
        checkExpr("\"hello\"", "hello");
        checkExpr("(42)", "42");
        checkExpr("[1, 2, 3]", "[1, 2, 3]");
        checkExpr("true", "true");
        checkExpr("false", "false");
        checkExpr("null", "null");
        checkExpr("!false", "true");
        checkExpr("!true", "false");
        checkExpr("!!true", "true");
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testNumericBinary () {
        checkExpr("1 + 2", "3");
        checkExpr("2 - 1", "1");
        checkExpr("2 * 3", "6");
        checkExpr("2 / 3", "0");
        checkExpr("3 / 2", "1");
        checkExpr("2 % 3", "2");
        checkExpr("3 % 2", "1");

        checkExpr("1.0 + 2.0", "3.0");
        checkExpr("2.0 - 1.0", "1.0");
        checkExpr("2.0 * 3.0", "6.0");
        checkExpr("2.0 / 3.0", "" + (2d / 3d));
        checkExpr("3.0 / 2.0", "" + (3d / 2d));
        checkExpr("2.0 % 3.0", "2.0");
        checkExpr("3.0 % 2.0", "1.0");

        checkExpr("1 + 2.0", "3.0");
        checkExpr("2 - 1.0", "1.0");
        checkExpr("2 * 3.0", "6.0");
        checkExpr("2 / 3.0", "" + (2d / 3d));
        checkExpr("3 / 2.0", "" + (3d / 2d));
        checkExpr("2 % 3.0", "2.0");
        checkExpr("3 % 2.0", "1.0");

        checkExpr("1.0 + 2", "3.0");
        checkExpr("2.0 - 1", "1.0");
        checkExpr("2.0 * 3", "6.0");
        checkExpr("2.0 / 3", "" + (2d / 3d));
        checkExpr("3.0 / 2", "" + (3d / 2d));
        checkExpr("2.0 % 3", "2.0");
        checkExpr("3.0 % 2", "1.0");

        checkExpr("2 * (4-1) * 4.0 / 6 % (2+1)", "1.0");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testLogic() {
        // check boolean logic
        checkExpr("true  && true",  "true");
        checkExpr("true  || true",  "true");
        checkExpr("true  || false", "true");
        checkExpr("false || true",  "true");
        checkExpr("false && true",  "false");
        checkExpr("true  && false", "false");
        checkExpr("false && false", "false");
        checkExpr("false || false", "false");

        checkExpr("1 + \"a\"", "1a");
        checkExpr("\"a\" + 1", "a1");
        checkExpr("\"a\" + true", "atrue");

        checkExpr("1 == 1", "true");
        checkExpr("1 == 2", "false");
        checkExpr("1.0 == 1.0", "true");
        checkExpr("1.0 == 2.0", "false");
        checkExpr("true == true", "true");
        checkExpr("false == false", "true");
        checkExpr("true == false", "false");
        checkExpr("1 == 1.0", "true");
        checkExpr("[1] == [1]", "false");

        checkExpr("1 != 1", "false");
        checkExpr("1 != 2", "true");
        checkExpr("1.0 != 1.0", "false");
        checkExpr("1.0 != 2.0", "true");
        checkExpr("true != true", "false");
        checkExpr("false != false", "false");
        checkExpr("true != false", "true");
        checkExpr("1 != 1.0", "false");
        checkExpr("\"hi\" != \"hi2\"", "true");
        checkExpr("[1] != [1]", "true");

        // test short circuit
        checkExpr("true || print(\"x\") == \"y\"", "true");
        checkExpr("false && print(\"x\") == \"y\"", "false");
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testVarDecl () {
        check("var X: Int = 1; print(\"\" + X)", "1");
        check("var X: Float = 2.0; print(\"\" + X)", "2.0");

        check("var X: Int = 0; X = 3; print(\"\" + X)", "3");

        // TODO fails
        //check("var x: String = \"0\"; print(x = \"S\")", "S");
        check("var X: String = \"0\"; X = \"S\"; print(X)", "S");

        // implicit conversions
        check("var X: Float = 1; X = 2; print(\"\" + X)", "2.0");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testArrays() {
        checkExpr("[1, 2, 3]", "[1, 2, 3]");
        checkExpr("[\"a\", \"b\", \"c\"]", "[a, b, c]");
        checkExpr("[1.0, 2.0]", "[1.0, 2.0]");

        checkExpr("[1, 2, 3][0]", "1");
        checkExpr("[1, 2, 3][1]", "2");
        checkExpr("[1, 2, 3][2]", "3");
        checkExpr("[\"a\", \"b\", \"c\"][0]", "a");
        checkExpr("[\"a\", \"b\", \"c\"][2]", "c");

        check("var X: Float[] = [1.0, 2.0]; X[0] = 3.0; print(\"\" + X[0])", "3.0");
        check("var X: Float[] = [1.0, 2.0]; X[0] = 3; print(\"\" + X[0])", "3.0");
    }

    private final String printa = "print(\"a\")";
    private final String printb = "print(\"b\")";
    private final String printx = "print(\"\" + (X))";
    private final String printy = "print(\"\" + (Y))";

    @Test public void testVariables() {
        check("var X: Int = 1;" + printx, "1");
        check("var X: String = \"a\";" + printx, "a");
        check("var X: Int = 1 ; " + printx + " ; X = 2 ; " + printx, "1\n2");

        // longs and double have double width
        check("var X: Int = 1 ; var Y: Float = 2.0 ;" + printx + printy, "1\n2.0");
        check("var X: Int = 1 ; var Y: String = \"a\" ;" + printx + printy, "1\na");
        check("var X: String = \"a\" ; var Y: Int = 1 ;" + printx + printy, "a\n1");
        check("var X: Float = 1.0 ; var Y: String = \"a\" ;" + printx + printy, "1.0\na");

        // implicit conversion
        check("var X: Float = 1 ;" + printx, "1.0");
        check("var X: Float = 1 ;" + printx + "X = 2 ;" + printx, "1.0\n2.0");
    }

    @Test public void testIfWhile() {
        check("if 1 == 1 " + printa, "a");
        check("if 1 == 1 " + printa + "else " + printb, "a");
        check("if 1 == 0 " + printa + "else " + printb, "b");

        check("var X: Int = 1 ; while X == 3 { " + printx + "}", "");
        check("var X: Int = 1 ; while X <= 3 { " + printx + " ; X = X + 1 }", "1\n2\n3");
    }

    @Test public void testMethod() {
        check("fun test (X: String):String { return X } print(test(\"a\"))", "a");
        check("fun test (X: String) { print(X) } ; test(\"a\")", "a");
        check("fun test () { fun foo() { print(\"a\") } foo() foo() } test()", "a\na");
    }

    private final String makePair =
        "struct Pair { var X: Int ; var Y: Float }" +
        "var X: Pair = $Pair(1, 2.0) ;";

    @Test public void testStructs() {
        check(makePair + "print(\"\" + X.X + \":\" + X.Y)", "1:2.0");
        check(makePair + "X.X = 3; print(\"\" + X.X)", "3");
        check(makePair + "X.Y = 3; print(\"\" + X.Y)", "3.0");
    }
}
