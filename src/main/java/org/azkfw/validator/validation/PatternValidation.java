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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.azkfw.validator.ValidationResult;

/**
 * このクラスは、パターン(正規表現)にマッチするかチェックするバリデーションクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2015/02/13
 * @author kawakicchi
 */
public final class PatternValidation extends AbstractValidation {

	/** パターン */
	private String pattern;

	private static Map<String, Pattern> CASH_PATTERNS = new HashMap<String, Pattern>();

	/**
	 * コンストラクタ
	 * 
	 * @param name 名前
	 * @param an アノテーション
	 */
	public PatternValidation(final String name, final org.azkfw.validator.annotation.Pattern an) {
		super(name, an.order(), an.message());
		pattern = an.value();
	}

	@Override
	protected ValidationResult doValidate(final Object value) {
		if (null != value) {
			if (!(value instanceof String)) {
				String msg = getDecorateString(getMessage("Pattern.format"));
				return ValidationResult.error(msg);
			}

			java.util.regex.Pattern p = null;
			synchronized (CASH_PATTERNS) {
				if (CASH_PATTERNS.containsKey(pattern)) {
					p = CASH_PATTERNS.get(pattern);
				} else {
					p = java.util.regex.Pattern.compile(pattern);
					CASH_PATTERNS.put(pattern, p);
				}
			}

			if (!p.matcher((String) value).matches()) {
				String msg = getDecorateString(getMessage("Pattern.match"));
				return ValidationResult.error(msg);
			}
		}
		return ValidationResult.success();
	}

}
