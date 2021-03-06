/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.form.evaluator.internal.rules;

import com.liferay.dynamic.data.mapping.expression.DDMExpression;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionException;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluationException;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormFieldEvaluationResult;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.rules.functions.FieldAtFunction;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.rules.functions.PropertyGetFunction;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.rules.functions.PropertySetFunction;

import java.util.List;
import java.util.Map;

/**
 * @author Leonardo Barros
 */
public class DDMFormRuleEvaluator {

	public DDMFormRuleEvaluator(
		DDMExpressionFactory ddmExpressionFactory,
		Map<String, List<DDMFormFieldEvaluationResult>>
			ddmFormFieldEvaluationResults, String expression) {

		_ddmExpressionFactory = ddmExpressionFactory;
		_ddmFormFieldEvaluationResults = ddmFormFieldEvaluationResults;
		_expression = expression;
	}

	public boolean evaluate() throws DDMFormEvaluationException {
		try {
			DDMExpression<Boolean> ddmExpression =
				_ddmExpressionFactory.createBooleanDDMExpression(_expression);

			setFunctions(ddmExpression);

			return ddmExpression.evaluate();
		}
		catch (DDMExpressionException ddmee) {
			throw new DDMFormEvaluationException(ddmee);
		}
	}

	public void execute() throws DDMFormEvaluationException {
		try {
			DDMExpression<String> ddmExpression =
				_ddmExpressionFactory.createStringDDMExpression(_expression);

			setFunctions(ddmExpression);

			ddmExpression.evaluate();
		}
		catch (DDMExpressionException ddmee) {
			throw new DDMFormEvaluationException(ddmee);
		}
	}

	protected void setFunctions(DDMExpression<?> ddmExpression) {
		ddmExpression.setDDMExpressionFunction(
			"fieldAt", new FieldAtFunction());
		ddmExpression.setDDMExpressionFunction(
			"get", new PropertyGetFunction(_ddmFormFieldEvaluationResults));
		ddmExpression.setDDMExpressionFunction(
			"set", new PropertySetFunction(_ddmFormFieldEvaluationResults));
	}

	private final DDMExpressionFactory _ddmExpressionFactory;
	private final Map<String, List<DDMFormFieldEvaluationResult>>
		_ddmFormFieldEvaluationResults;
	private final String _expression;

}