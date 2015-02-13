/**
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
package org.azkfw.validator.validation;

import org.azkfw.validator.ValidationResult;
import org.azkfw.validator.annotation.Range;

/**
 * このクラスは、範囲内かチェックするバリデーションクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2015/02/13
 * @author kawakicchi
 */
public final class RangeValidation extends AbstractValidation {

	/** 最小値 */
	private double minimum;

	/** 最大値 */
	private double maximum;

	/**
	 * コンストラクタ
	 * 
	 * @param name 名前
	 * @param an アノテーション
	 */
	public RangeValidation(final String name, final Range an) {
		super(name, an.order(), an.message());
		minimum = an.min();
		maximum = an.max();
	}

	@Override
	protected ValidationResult doValidate(final Object value) {
		if (null != value) {
			if (!(value instanceof Double)) {
				String msg = getDecorateString(getMessage("Range.format"));
				return ValidationResult.error(msg);
			}
			if ((Double) value < minimum) {
				String msg = getDecorateString(getMessage("Range.limit.minimum"));
				return ValidationResult.error(msg);
			}
			if ((Double) value > maximum) {
				String msg = getDecorateString(getMessage("Range.limit.maximum"));
				return ValidationResult.error(msg);
			}
		}
		return ValidationResult.success();
	}

}
