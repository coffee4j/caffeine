package de.rwth.swc.coffee4j.evaluation.model.ctw;

import de.rwth.swc.coffee4j.evaluation.persistence.antlr.CtwFileBaseVisitor;
import de.rwth.swc.coffee4j.evaluation.persistence.antlr.CtwFileParser;
import org.antlr.v4.runtime.Vocabulary;

import java.util.InputMismatchException;
import java.util.Map;

class ConstraintVisitor extends CtwFileBaseVisitor<Constraint> {
    private final Map<String, Parameter> parameters;
    private final Vocabulary vocabulary;

    ConstraintVisitor(Map<String, Parameter> parameters, Vocabulary vocabulary) {
        this.parameters = parameters;
        this.vocabulary = vocabulary;
    }

    @Override
    public Constraint visitConstraint(CtwFileParser.ConstraintContext ctx) {
        return visit(ctx.constraintExpression());
    }

    @Override
    public Constraint visitElementExpression(CtwFileParser.ElementExpressionContext ctx) {
        RelationConstraint.Operator op = RelationConstraint.Operator.valueOf(vocabulary.getSymbolicName(ctx.op.getType()));
        Parameter left = parameters.get(ctx.left.getText());
        Parameter right = parameters.get(ctx.right.getText());
        if (left == null && right == null) {
            throw new InputMismatchException("Relation Constraint must contain at least on parameter.");
        } else if (left == null) {
            String id = ctx.left.getText().replaceFirst("^" + right.getName() + "\\.", "");
            left = new FixedParameter(right.getMappedValue(id));
        } else if (right == null) {
            String id = ctx.right.getText().replaceFirst("^" + left.getName() + "\\.", "");
            right = new FixedParameter(left.getMappedValue(id));
        }
        return new RelationConstraint(left, right, op);
    }

    @Override
    public Constraint visitNotExpression(CtwFileParser.NotExpressionContext ctx) {
        return new NotConstraint(visit(ctx.constraintExpression()));
    }

    @Override
    public Constraint visitAndExpression(CtwFileParser.AndExpressionContext ctx) {
        return new BinaryConstraint(visit(ctx.left), visit(ctx.right), BinaryConstraint.Operator.AND);
    }

    @Override
    public Constraint visitOrExpression(CtwFileParser.OrExpressionContext ctx) {
        return new BinaryConstraint(visit(ctx.left), visit(ctx.right), BinaryConstraint.Operator.OR);
    }

    @Override
    public Constraint visitImpliesExpression(CtwFileParser.ImpliesExpressionContext ctx) {
        return new BinaryConstraint(visit(ctx.left), visit(ctx.right), BinaryConstraint.Operator.valueOf(vocabulary
                .getSymbolicName(ctx.op.getType())));
    }

    @Override
    public Constraint visitBooleanAtomExpression(CtwFileParser.BooleanAtomExpressionContext ctx) {
        Parameter parameter = parameters.get(ctx.getText());
        if (parameter == null) {
            throw new InputMismatchException(ctx.getText() + " is not a parameter identifier.");
        }
        return new RelationConstraint(parameter, new FixedParameter(1), RelationConstraint.Operator.EQ);
    }

    @Override
    public Constraint visitParenExpression(CtwFileParser.ParenExpressionContext ctx) {
        return visit(ctx.constraintExpression());
    }
}
