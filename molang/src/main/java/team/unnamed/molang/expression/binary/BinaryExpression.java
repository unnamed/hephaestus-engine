package team.unnamed.molang.expression.binary;

import team.unnamed.molang.context.EvalContext;
import team.unnamed.molang.expression.Expression;

/**
 * Abstraction for all {@link Expression}
 * composed by two {@link Expression}, they
 * are the left-hand expression and the
 * right-hand expression, respectively
 */
public abstract class BinaryExpression
        implements Expression {

    protected final Expression leftHand;
    protected final Expression rightHand;

    public BinaryExpression(
            Expression leftHand,
            Expression rightHand
    ) {
        this.leftHand = leftHand;
        this.rightHand = rightHand;
    }

    public Expression getLeftHand() {
        return leftHand;
    }

    public Expression getRightHand() {
        return rightHand;
    }

    //#region Standard binary expression implementations
    /**
     * {@link BinaryExpression} implementation for
     * addition of two numerical expressions
     */
    public static class Addition
            extends BinaryExpression {

        public Addition(
                Expression leftHand,
                Expression rightHand
        ) {
            super(leftHand, rightHand);
        }

        @Override
        public float evalAsFloat(EvalContext context) {
            // override to avoid unboxing
            return leftHand.evalAsFloat(context)
                    + rightHand.evalAsFloat(context);
        }

        @Override
        public Object eval(EvalContext context) {
            return evalAsFloat(context);
        }

        @Override
        public String toString() {
            return "sum(" + leftHand + ", " + rightHand + ")";
        }

    }

    /**
     * {@link BinaryExpression} implementation for
     * subtraction of two numerical expressions
     */
    public static class Subtraction
            extends BinaryExpression {

        public Subtraction(
                Expression leftHand,
                Expression rightHand
        ) {
            super(leftHand, rightHand);
        }

        @Override
        public float evalAsFloat(EvalContext context) {
            // override to avoid unboxing
            return leftHand.evalAsFloat(context)
                    - rightHand.evalAsFloat(context);
        }

        @Override
        public Object eval(EvalContext context) {
            return evalAsFloat(context);
        }

        @Override
        public String toString() {
            return "subtract(" + leftHand + ", " + rightHand + ")";
        }
    }
    //#endregion

}
