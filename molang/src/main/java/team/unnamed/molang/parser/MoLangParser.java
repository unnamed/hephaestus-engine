package team.unnamed.molang.parser;

import team.unnamed.molang.expression.Expression;

import java.io.Reader;
import java.util.List;

/**
 * Responsible of parsing MoLang expressions
 * from simple char streams to evaluable
 * {@link Expression} instances
 */
public interface MoLangParser {

    /**
     * Parses the data from the given {@code reader}
     * to a {@link List} of {@link Expression}
     *
     * <strong>Note that this method won't close
     * the given {@code reader}</strong>
     *
     * @throws ParseException If read failed or there
     * are syntax errors in the script
     */
    List<Expression> parse(Reader reader) throws ParseException;

}
