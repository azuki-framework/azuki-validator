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
package org.azkfw.validator;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.azkfw.validator.annotation.CustomValidate;
import org.azkfw.validator.annotation.Maximum;
import org.azkfw.validator.annotation.Minimum;
import org.azkfw.validator.annotation.NameBindLabel;
import org.azkfw.validator.annotation.NameBindValue;
import org.azkfw.validator.annotation.NotEmpty;
import org.azkfw.validator.annotation.NotNull;
import org.azkfw.validator.annotation.Pattern;
import org.azkfw.validator.annotation.Range;
import org.azkfw.validator.annotation.Required;
import org.azkfw.validator.bundle.LabelBundle;
import org.azkfw.validator.bundle.MessageBundle;
import org.azkfw.validator.validation.MaximumValidation;
import org.azkfw.validator.validation.MinimumValidation;
import org.azkfw.validator.validation.NotEmptyValidation;
import org.azkfw.validator.validation.NotNullValidation;
import org.azkfw.validator.validation.PatternValidation;
import org.azkfw.validator.validation.RangeValidation;
import org.azkfw.validator.validation.RequiredValidation;
import org.azkfw.validator.validation.Validation;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * このクラスは、バリデーションを行うクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2015/02/13
 * @author kawakicchi
 */
public class Validator {

	private MessageBundle messages;
	private LabelBundle labels;

	public Validator() {
		labels = new LabelBundle();
		messages = new MessageBundle();
	}

	public List<ValidationResult> validate(final String data, final Class<?> clazz) {
		List<ValidationResult> result = new ArrayList<ValidationResult>();
		Gson gson = new Gson();
		try {
			Object obj = gson.fromJson(data, Object.class);
			doValidate(obj, clazz);
		} catch (JsonSyntaxException ex) {
			System.out.println("Validate error.[No json format]");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public List<ValidationResult> validate(final Object data, final Class<?> clazz) {
		List<ValidationResult> result = new ArrayList<ValidationResult>();
		try {
			doValidate(data, clazz);
		} catch (JsonSyntaxException ex) {
			System.out.println("Validate error.[No json format]");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * バリデーションを行う。
	 * 
	 * @param data データ
	 * @param clazz バリデーションクラス
	 */
	private void doValidate(final Object data, final Class<?> clazz) throws Exception {
		if (null == data) {

		} else if (data instanceof Map) {
			checkObject((Map<?, ?>) data, clazz, "");
		} else if (data instanceof List) {
			checkList((List<?>) data, clazz, "");
		}
	}

	private void checkList(final List<?> data, final Class<?> clazz, final String path) throws Exception {
		for (int i = 0; i < data.size(); i++) {
			Object value = data.get(i);

			if (null == value) {

			} else if (value instanceof String) {

			} else if (value instanceof Double) {

			} else if (value instanceof Map) {
				checkObject((Map<?, ?>) value, clazz, path + "[" + i + "]");
			} else if (value instanceof List) {

			}
		}
	}

	private void checkObject(final Map<?, ?> data, final Class<?> clazz, final String path) throws Exception {
		if (null != clazz.getSuperclass()) {
			checkObject(data, clazz.getSuperclass(), path);
		}

		final Field[] fields = clazz.getDeclaredFields();
		for (final Field field : fields) {

			String name = field.getName();
			final JsonProperty jp = (JsonProperty) field.getAnnotation(JsonProperty.class);
			if (null != jp) {
				name = jp.value();
			}
			final String myPath = path + "/" + name;
			final Object value = data.get(name);

			boolean validationSuccess = true;
			List<Validation> validations = getFieldValidations(field);
			if (!validations.isEmpty()) {
				Map<String, Object> keywords = new HashMap<String, Object>();
				keywords.put("name", name);
				keywords.put("value", value);
				keywords.put("path", myPath);

				for (Validation validation : validations) {
					if (validation instanceof RequiredValidation) {
						if (!data.containsKey(name)) {
							ValidationResult result = validation.validate(value, keywords);
							System.out.println(result.getMessage());
							validationSuccess = false;
							break;
						}
					} else {
						ValidationResult result = validation.validate(value, keywords);
						if (!result.isResult()) {
							System.out.println(result.getMessage());
							validationSuccess = false;
							break;
						}
					}
				}
			}

			if (validationSuccess) {
				if (value instanceof Map) {
					checkObject((Map<?, ?>) value, field.getType(), myPath);
				} else if (value instanceof List) {
					Type type = field.getGenericType();
					if (null != type) {
						if (type instanceof ParameterizedType) {
							ParameterizedType pType = (ParameterizedType) type;
							Type t = pType.getActualTypeArguments()[0];
							try {
								Class<?> cls = Class.forName(t.toString().split(" ")[1]);
								checkList((List<?>) value, cls, myPath);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	private String getLabel(final String id) {
		return labels.get(id);
	}

	private String getName(final Field field) {
		String name = null;

		NameBindLabel an1 = field.getAnnotation(NameBindLabel.class);
		if (null != an1) {
			name = getLabel(an1.value());
		}

		if (null == name || 0 == name.length()) {
			NameBindValue an2 = field.getAnnotation(NameBindValue.class);
			if (null != an2) {
				name = an2.value();
			}
		}

		return name;
	}

	/**
	 * フィールドの付与されたアノテーションからバリデーション一覧を取得する。
	 * 
	 * @param field フィールド
	 * @return アノテーション一覧
	 */
	private List<Validation> getFieldValidations(final Field field) throws Exception {
		String name = getName(field);

		List<Validation> validations = new ArrayList<Validation>();
		{ // CustomValidate
			CustomValidate an = field.getAnnotation(CustomValidate.class);
			if (null != an) {
				Class<? extends Validation>[] classes = an.value();
				for (Class<? extends Validation> clazz : classes) {
					Validation validation = clazz.newInstance();
					validation.setMessageBundle(messages);
					validations.add(validation);
				}
			}
		}
		{ // Maximum
			Maximum an = field.getAnnotation(Maximum.class);
			if (null != an) {
				Validation validation = new MaximumValidation(name, an);
				validation.setMessageBundle(messages);
				validations.add(validation);
			}
		}
		{ // Minimum
			Minimum an = field.getAnnotation(Minimum.class);
			if (null != an) {
				Validation validation = new MinimumValidation(name, an);
				validation.setMessageBundle(messages);
				validations.add(validation);
			}
		}
		{ // NotEmpty
			NotEmpty an = field.getAnnotation(NotEmpty.class);
			if (null != an) {
				Validation validation = new NotEmptyValidation(name, an);
				validation.setMessageBundle(messages);
				validations.add(validation);
			}
		}
		{ // NotNull
			NotNull an = field.getAnnotation(NotNull.class);
			if (null != an) {
				Validation validation = new NotNullValidation(name, an);
				validation.setMessageBundle(messages);
				validations.add(validation);
			}
		}
		{ // Pattern
			Pattern an = field.getAnnotation(Pattern.class);
			if (null != an) {
				Validation validation = new PatternValidation(name, an);
				validation.setMessageBundle(messages);
				validations.add(validation);
			}
		}
		{ // Range
			Range an = field.getAnnotation(Range.class);
			if (null != an) {
				Validation validation = new RangeValidation(name, an);
				validation.setMessageBundle(messages);
				validations.add(validation);
			}
		}
		{ // Required
			Required an = field.getAnnotation(Required.class);
			if (null != an) {
				Validation validation = new RequiredValidation(name, an);
				validation.setMessageBundle(messages);
				validations.add(validation);
			}
		}

		Collections.sort(validations, new Comparator<Validation>() {
			@Override
			public int compare(final Validation o1, final Validation o2) {
				return o1.order() - o2.order();
			}
		});
		return validations;
	}

}
