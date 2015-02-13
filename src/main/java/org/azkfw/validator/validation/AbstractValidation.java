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
 * このクラスは、バリデーション機能を実装する為の基底クラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2015/02/13
 * @author kawakicchi
 */
public abstract class AbstractValidation implements Validation {

	private String name;
	private int order;
	private String message;

	private Map<String, Object> keyword;

	private MessageBundle messages;

	public AbstractValidation() {
		this("", 1, "");
	}

	public AbstractValidation(final String name, final int order, final String message) {
		this.name = name;
		this.order = order;
		this.message = message;
	}

	public final void setMessageBundle(final MessageBundle bundle) {
		messages = bundle;
	}

	@Override
	public final int order() {
		return order;
	}

	@Override
	public final ValidationResult validate(final Object value, final Map<String, Object> keyword) {
		this.keyword = keyword;

		if (null != name && 0 < name.length()) {
			this.keyword.put("name", name);
		}

		ValidationResult result = doValidate(value);
		return result;
	}

	/**
	 * バリデーションを行う。
	 * 
	 * @param value 値
	 * @return バリデーション結果
	 */
	protected abstract ValidationResult doValidate(final Object value);

	protected final void setOrder(final int order) {
		this.order = order;
	}

	protected final void setMessage(final String messge) {
		this.message = messge;
	}

	protected final String getMessage(final String id) {
		if (null != message && 0 < message.length()) {
			return message;
		} else {
			String msg = messages.get(id);
			if (null != msg && 0 < msg.length()) {
				return msg;
			} else {
				return String.format("Undefined validation error message.[%s]", id);
			}
		}
	}

	/**
	 * キーワード値で置き換えた文字列を取得する。
	 * 
	 * @param message メッセージ
	 * @return メッセージ
	 */
	protected final String getDecorateString(final String message) {
		String buf = message;
		if (null != buf) {
			for (String key : keyword.keySet()) {
				Object value = keyword.get(key);
				if (null != value) {
					buf = buf.replaceAll("\\$\\{" + key + "\\}", (null != value) ? value.toString() : "null");
				}
			}
		}
		return buf;
	}
}
