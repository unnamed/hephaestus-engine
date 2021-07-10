package team.unnamed.molang.expression;

import team.unnamed.molang.context.EvalContext;

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

    /**
     * {@link BinaryExpression} implementation for
     * multiplication of two numerical expressions
     */
    public static class Multiplication
            extends BinaryExpression {

        public Multiplication(
                Expression leftHand,
                Expression rightHand
        ) {
            super(leftHand, rightHand);
        }

        @Override
        public float evalAsFloat(EvalContext context) {
            // override to avoid unboxing
            return leftHand.evalAsFloat(context)
                    * rightHand.evalAsFloat(context);
        }

        @Override
        public Object eval(EvalContext context) {
            return evalAsFloat(context);
        }

        @Override
        public String toString() {
            return "multiply(" + leftHand + ", " + rightHand + ")";
        }
    }

    /**
     * {@link BinaryExpression} implementation for
     * division of two numerical expressions
     */
    public static class Division
            extends BinaryExpression {

        public Division(
                Expression leftHand,
                Expression rightHand
        ) {
            super(leftHand, rightHand);
        }

        @Override
        public float evalAsFloat(EvalContext context) {
            float divisor = rightHand.evalAsFloat(context);
            if (divisor == 0F) {
                // MoLang specification declares that division by
                // zero returns zero
                // "Errors (such as divide by zero, ...) generally return a value of 0.0"
                return 0F;
            }
            // override to avoid unboxing
            return leftHand.evalAsFloat(context) / divisor;
        }

        @Override
        public Object eval(EvalContext context) {
            return evalAsFloat(context);
        }

        @Override
        public String toString() {
            return "divide(" + leftHand + ", " + rightHand + ")";
        }
    }

    /**
     * {@link BinaryExpression} implementation for
     * representing field accessing
     */
    public static class Access
            extends BinaryExpression {

        public Access(
                Expression leftHand,
                Expression rightHand
        ) {
            super(leftHand, rightHand);
        }

        @Override
        public Object eval(EvalContext context) {
            return leftHand.evalProperty(context, rightHand); // temporary
        }

        @Override
        public String toString() {
            return "access(" + leftHand + ", " + rightHand + ")";
        }
    }
    //#endregion

}
