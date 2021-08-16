package team.unnamed.molang.parser;

import team.unnamed.molang.context.ParseContext;
import team.unnamed.molang.expression.BinaryExpression;
import team.unnamed.molang.expression.CallExpression;
import team.unnamed.molang.expression.Expression;
import team.unnamed.molang.expression.IdentifierExpression;
import team.unnamed.molang.expression.LiteralExpression;
import team.unnamed.molang.expression.NegationExpression;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard implementation of {@link MoLangParser},
 * it's Hephaestus-MoLang parser since some MoLang
 * characteristics may change
 *
 * <p>There are some contracts for the parse methods:
 *
 * - After an invoke, the {@link ParseContext#getCurrent()} should
 *   return a non-whitespace new token that the next parse method can parse
 *
 * - They must assume that the {@link ParseContext#getCurrent()} will be
 *   a new non-whitespace token when they are called
 * </p>
 *
 * @see Tokens
 * @see Expression
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
            context.nextNoWhitespace();
            // wrapped expression: (expression)
            Expression expression = parse(context);
            assertToken(context, ')');
            // skip the closing parenthesis and
            // following spaces
            context.nextNoWhitespace();
            return expression;
        }
        //#endregion

        //#region Identifier expression
        if (Tokens.isValidForIdentifier(current)) {
            StringBuilder identifier = new StringBuilder();
            do {
                identifier.append((char) current);
            } while (Tokens.isValidForIdentifier(current = context.next()));
            // skip whitespace
            context.skipWhitespace();
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

            // skip the last quote and following whitespaces
            context.nextNoWhitespace();
            return new LiteralExpression(builder.toString());
        }
        //#endregion

        //#region Float literal expression
        if (Character.isDigit(current)) {
            return LiteralExpression.parseFloat(context, 1);
        }
        //#endregion

        //#region Negation
        if (current == Tokens.HYPHEN) {
            current = context.next();
            if (Character.isDigit(current)) {
                // if negated expression is numeral, make it
                // negative instead of creating a negation expression
                return LiteralExpression.parseFloat(context, -1);
            } else {
                Expression expression = parseSingle(context);
                return new NegationExpression(expression);
            }
        }
        //#endregion

        return new LiteralExpression(0);
    }

    private Expression parseMultiplication(ParseContext context, Expression left)
        throws ParseException {
        int current = context.getCurrent();
        if (current == '*') {
            context.nextNoWhitespace();
            Expression right = parseSingle(context);
            return new BinaryExpression.Multiplication(left, right);
        } else if (current == '/') {
            context.nextNoWhitespace();
            Expression right = parseSingle(context);
            return new BinaryExpression.Division(left, right);
        }
        return left;
    }

    private Expression parseAddition(ParseContext context, Expression left)
        throws ParseException {
        int current = context.getCurrent();
        if (current == '+') {
            context.nextNoWhitespace();
            Expression right = parse(context);
            return new BinaryExpression.Addition(left, right);
        } else if (current == '-') {
            context.nextNoWhitespace();
            Expression right = parse(context);
            return new BinaryExpression.Subtraction(left, right);
        }
        // try fallback-ing to multiplication/division
        return parseMultiplication(context, left);
    }

    private Expression parse(ParseContext context, Expression left) throws ParseException {
        int current = context.getCurrent();

        //#region Function call expression
        if (current == '(') {

            List<Expression> arguments = new ArrayList<>();

            // skip the initial parenthesis and
            // following spaces
            context.nextNoWhitespace();

            // start reading the arguments
            while (true) {
                arguments.add(parse(context));
                // update current character
                current = context.getCurrent();
                if (current == -1) {
                    failUnexpectedToken(context, (char) -1, ')');
                } else if (current == ')') {
                    // skip closing parenthesis and
                    // following whitespace
                    context.nextNoWhitespace();
                    break;
                } else {
                    assertToken(context, ',');
                    // skip current comma and following whitespace
                    context.nextNoWhitespace();
                }
            }

            return new CallExpression(left, arguments);
        }
        //#endregion

        //#region Dot access expression
        if (current == '.') {
            context.nextNoWhitespace();
            Expression right = parseSingle(context);
            return new BinaryExpression.Access(left, right);
        }

        return parseAddition(context, left);
    }

    private Expression parse(ParseContext context) throws ParseException {
        Expression expression = parseSingle(context);
        while (true) {
            Expression compositeExpr = parse(context, expression);
            if (compositeExpr == expression) {
                break;
            } else {
                expression = compositeExpr;
            }
        }
        return expression;
    }

    @Override
    public List<Expression> parse(Reader reader) throws ParseException {

        ParseContext context = new ParseContext(reader);
        // initial next() call
        context.nextNoWhitespace();

        List<Expression> expressions = new ArrayList<>();
        int current;
        while (true) {
            expressions.add(parse(context));
            current = context.getCurrent();
            if (current == -1) {
                // end reached, break
                break;
            } else {
                assertToken(context, ';');
                // skip current semicolon and
                // following whitespace
                context.nextNoWhitespace();
            }
        }

        return expressions;
    }

}
