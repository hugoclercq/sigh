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


}