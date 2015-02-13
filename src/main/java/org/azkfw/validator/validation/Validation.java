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

import java.util.Map;

import org.azkfw.validator.ValidationResult;
import org.azkfw.validator.bundle.MessageBundle;

/**
 * このインターフェースは、バリデーション機能を定義する為のインターフェースです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2015/02/13
 * @author kawakicchi
 */
public interface Validation {

	/**
	 * バリデーションを行う。
	 * 
	 * @param value 値
	 * @param keyword キーワード
	 * @return バリデーション結果
	 */
	public ValidationResult validate(final Object value, final Map<String, Object> keyword);

	/**
	 * 実行順を取得する。
	 * 
	 * @return 実行順
	 */
	public int order();

	public void setMessageBundle(final MessageBundle bundle);

}
