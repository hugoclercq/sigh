import norswap.autumn.Grammar;
import norswap.autumn.AutumnTestFixture;
import norswap.sigh.SighGrammar;

import org.testng.annotations.Test;

import static norswap.utils.exceptions.Exceptions.suppress;
import static norswap.utils.Util.cast;

@SuppressWarnings("FieldMayBeFinal")
public class PersonalGrammarTests extends AutumnTestFixture {
    // ---------------------------------------------------------------------------------------------

    private final Object grammar;
    private final Class<?> grammarClass;

    // ---------------------------------------------------------------------------------------------

    /**
     * Use this constructor in subclasses to test alternative Java grammars that use the same rule
     * names as the original.
     */
    public PersonalGrammarTests (Object grammar) {
        this.grammar = grammar;
        this.grammarClass = grammar.getClass();
    }

    // ---------------------------------------------------------------------------------------------

    public PersonalGrammarTests () {
        this(new SighGrammar());
    }

    // ---------------------------------------------------------------------------------------------

    private Grammar.rule rule (String name) {
        return cast(suppress(() -> grammarClass.getField(name).get(grammar)));
    }

    // ---------------------------------------------------------------------------------------------


    @Test public void var_decl()
    {
        rule = rule("var_decl");

        success("var X: Int = 3");
        success("var X: String = \"test\"");
        success("var X: Int = 3 + 5");
        success("var X: Float = 4.0");
        success("var X: Float = 4.0 + 3.2");
        success("var X: Int = [1,2,3]");
        success("var X: Int[] = [1,2,3]");
        success("var X: Bool = [1,2,3]");

        failure("var X: Int[atom] = 5");
        failure("var X: Int[Indice] = 4");
        failure("var X: Int[3] = 4");
        failure("var x: Int = 3");
        failure("var X: Int = 3, 12, 50");
    }

    @Test public void fact_decl()
    {
        rule = rule("fact_decl");

        success("fact likes(john, marry)");
        success("fact human(jean)");

        failure("fact Test(john, marry)");
        failure("Fact test(john, marry)");

        //failure as of now but should be success at some point later
        failure("fact likes(john, Who)");
        failure("fact likes(john, Who[1])");
        failure("fact likes(john, var Who:Atom");
    }

}