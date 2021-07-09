package team.unnamed.molang.parser;

import team.unnamed.molang.context.ParseContext;
import team.unnamed.molang.expression.CallExpression;
import team.unnamed.molang.expression.Expression;
import team.unnamed.molang.expression.IdentifierExpression;
import team.unnamed.molang.expression.LiteralExpression;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Standard implementation of {@link MoLangParser},
 * it's Hephaestus-MoLang parser since some MoLang
 * characteristics may change
 */
public class StandardMoLangParser
        implements MoLangParser {

    // MoLang currently only supports single quotes for string
    private static final char QUOTE = '\'';

    private void failUnexpectedToken(ParseContext context, char current, char expected)
            throws ParseException {
        throw new ParseException(
                "Unexpected token: '" + current + "'. Expected: '" + expected + '\'',
                context.getCursor()
        );
    }

    private void assertToken(ParseContext context, char expected) throws ParseException {
        int current;
        if ((current = context.getCurrent()) != expected) {
            // must be closed
            failUnexpectedToken(context, (char) current, expected);
        }
    }

    private Expression parseSingle(ParseContext context) throws ParseException {
        int current = context.getCurrent();

        //#region Expression inside parenthesis
        if (current == '(') {
            context.next();
            // wrapped expression: (expression)
            Expression expression = parse(context);
            assertToken(context, ')');
            // skip the closing parenthesis
            context.next();
            return expression;
        }
        //#endregion

        //#region Identifier expression
        if (Tokens.isValidForIdentifier(current)) {
            StringBuilder identifier = new StringBuilder();
            do {
                identifier.append((char) current);
            } while (Tokens.isValidForIdentifier(current = context.next()));
            return new IdentifierExpression(identifier.toString());
        }
        //#endregion

        //#region String literal expression
        if (current == QUOTE) {
            StringBuilder builder = new StringBuilder();
            while ((current = context.next()) != QUOTE && current != -1) {
                builder.append((char) current);
            }

            // it must be closed with 'QUOTE'
            if (current == -1) {
                throw new ParseException(
                        "Found the end before the closing quote",
                        context.getCursor()
                );
            }

            // skip the last quote
            context.next();
            return new LiteralExpression(builder.toString());
        }
        //#endregion

        //#region Float literal expression
        boolean negative = false;
        if (Character.isDigit(current) || (negative = current == Tokens.HYPHEN)) {
            // skip the sign
            if (negative) {
                current = context.next();
            }
            boolean readingDecimalPart = false;
            float value = 0;
            float divideBy = 1;

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
                    break;
                }
            }

            return new LiteralExpression(value / divideBy);
        }
        //#endregion

        return new LiteralExpression("unknown");
    }

    private Expression parse(ParseContext context, Expression left) throws ParseException {
        int current = context.getCurrent();

        //#region Function call expression
        if (current == '(') {

            List<Expression> arguments = new ArrayList<>();

            // skip the initial parenthesis
            context.next();

            // start reading the arguments
            while (true) {
                arguments.add(parse(context));
                // update current character
                current = context.getCurrent();
                if (current == -1) {
                    failUnexpectedToken(context, (char) -1, ')');
                } else if (current == ')') {
                    // skip closing parenthesis
                    context.next();
                    break;
                } else {
                    assertToken(context, ',');
                }
            }

            return new CallExpression(left, arguments);
        }
        //#endregion

        //#region Addition expression
        if (current == '+') {
            context.next();
            Expression right = parse(context);
            return (ctx) -> "sum(" + left + ", " + right + ")";
        }
        //#endregion

        return left;
    }

    private Expression parse(ParseContext context) throws ParseException {
        // TODO: I think this shouldn't be like this
        return parse(context, parseSingle(context));
    }

    @Override
    public List<Expression> parse(Reader reader) throws ParseException {
        ParseContext context = new ParseContext(reader);
        context.next(); // initial next() call
        return Collections.singletonList(parse(context));
    }

}
