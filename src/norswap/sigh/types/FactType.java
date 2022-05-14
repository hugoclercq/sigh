package norswap.sigh.types;

import norswap.utils.NArrays;
import java.util.Arrays;

public final class FactType extends Type
{
    public final String[] paramNames;

    public FactType (String... paramNames) {

        this.paramNames = paramNames;
    }

    @Override public String name() {
        return String.format("%s", String.join(", ", paramNames));
    }

    @Override public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof FactType)) return false;
        FactType other = (FactType) o;

        return Arrays.equals(paramNames, other.paramNames);
    }

    @Override public int hashCode () {
        return Arrays.hashCode(paramNames);
    }
}
