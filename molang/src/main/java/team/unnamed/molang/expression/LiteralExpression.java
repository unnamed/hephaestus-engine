package team.unnamed.molang.expression;

import team.unnamed.molang.context.EvalContext;
import team.unnamed.molang.context.ParseContext;
import team.unnamed.molang.parser.ParseException;
import team.unnamed.molang.parser.Tokens;

/**
 * Literal {@link Expression} implementation,
 * the parsed value is the same as the
 * evaluated
 */
public class LiteralExpression
        implements Expression {

    private final Object value;

    public LiteralExpression(Object value) {
        this.value = value;
    }

    @Override
    public Object eval(EvalContext context) {
        return value;
    }

    @Override
    public String toString() {
        return "literal(" + value.toString() + ")";
    }

    public static Expression parseFloat(
            ParseContext context,
            float divideByInitial
    ) throws ParseException {

        int current = context.getCurrent();
        boolean readingDecimalPart = false;
        float value = 0;
        float divideBy = divideByInitial;

        while (true) {
            if (Character.isDigit(current)) {
                value *= 10;
                value += Character.getNumericValue(current);
                if (readingDecimalPart) {
                    divideBy *= 10;
                }
                current = context.next();
            } else if (current == Tokens.DOT) {
                if (readingDecimalPart) {
                    throw new ParseException(
                            "Numbers can't have multiple floating points!",
                            context.getCursor()
                    );
                }
                readingDecimalPart = true;
                current = context.next();
            } else {
                // skip whitespace
                context.skipWhitespace();
                break;
            }
        }

        return new LiteralExpression(value / divideBy);
    }

}
