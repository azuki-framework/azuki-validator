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
import org.azkfw.validator.annotation.Maximum;

/**
 * このクラスは、最大値をチェックするバリデーションクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2015/02/13
 * @author kawakicchi
 */
public final class MaximumValidation extends AbstractValidation {

	/** 最大値 */
	private double maximum;

	/**
	 * コンストラクタ
	 * 
	 * @param name 名前
	 * @param an アノテーション
	 */
	public MaximumValidation(final String name, final Maximum an) {
		super(name, an.order(), an.message());
		maximum = an.value();
	}

	@Override
	protected ValidationResult doValidate(final Object value) {
		if (null != value) {
			if (!(value instanceof Double)) {
				String msg = getDecorateString(getMessage("Maximum.format"));
				return ValidationResult.error(msg);
			}
			if ((Double) value > maximum) {
				String msg = getDecorateString(getMessage("Maximum.limit"));
				return ValidationResult.error(msg);
			}
		}
		return ValidationResult.success();
	}

}
