/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.dsls.sql.interpreter.operator;

import org.apache.beam.dsls.sql.interpreter.BeamSqlFnExecutorTestBase;
import org.apache.beam.dsls.sql.interpreter.operator.comparison.BeamSqlIsNotNullExpression;
import org.apache.beam.dsls.sql.interpreter.operator.comparison.BeamSqlIsNullExpression;
import org.apache.calcite.sql.type.SqlTypeName;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link BeamSqlIsNullExpression} and
 * {@link BeamSqlIsNotNullExpression}.
 */
public class BeamNullExperssionTest extends BeamSqlFnExecutorTestBase {

  @Test
  public void testIsNull() {
    BeamSqlIsNullExpression exp1 = new BeamSqlIsNullExpression(
        new BeamSqlInputRefExpression(SqlTypeName.BIGINT, 0));
    Assert.assertEquals(false, exp1.evaluate(record).getValue());

    BeamSqlIsNullExpression exp2 = new BeamSqlIsNullExpression(
        BeamSqlPrimitive.of(SqlTypeName.BIGINT, null));
    Assert.assertEquals(true, exp2.evaluate(record).getValue());
  }

  @Test
  public void testIsNotNull() {
    BeamSqlIsNotNullExpression exp1 = new BeamSqlIsNotNullExpression(
        new BeamSqlInputRefExpression(SqlTypeName.BIGINT, 0));
    Assert.assertEquals(true, exp1.evaluate(record).getValue());

    BeamSqlIsNotNullExpression exp2 = new BeamSqlIsNotNullExpression(
        BeamSqlPrimitive.of(SqlTypeName.BIGINT, null));
    Assert.assertEquals(false, exp2.evaluate(record).getValue());
  }

}
