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

import java.util.List;

import junit.framework.TestCase;

import org.azkfw.validator.annotation.CustomValidate;
import org.azkfw.validator.annotation.NameBindLabel;
import org.azkfw.validator.annotation.NotEmpty;
import org.azkfw.validator.annotation.NotNull;
import org.azkfw.validator.annotation.Required;
import org.azkfw.validator.validation.AbstractValidation;
import org.codehaus.jackson.annotate.JsonProperty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * 
 * @since 1.0.0
 * @version 1.0.0 2015/02/13
 * @author kawakicchi
 */
@RunWith(JUnit4.class)
public class ValidatorTest extends TestCase {

	@Test
	public void test() {
		String data = "{ \"result\":true , \"users\":[{ \"aaaa\":\"aa\" }]}";
		Validator validator = new Validator();
		validator.validate(data, UserListResponse.class);
	}

	public static abstract class AbstractRequest {
		@Required
		@JsonProperty("result")
		private boolean result;

		@JsonProperty("errorCode")
		private int errorCode;

		@JsonProperty("errorMessage")
		private String errorMessage;
	}

	public static class UserListResponse extends AbstractRequest {

		@NameBindLabel("UserList")
		@Required(order = 1)
		@NotNull(order = 2)
		@NotEmpty(order = 3)
		@JsonProperty("users")
		private List<UserDto> users;
	}

	public static class UserDto {

		@Required(order = 1)
		@NotNull(order = 2)
		@NotEmpty(order = 3)
		@NameBindLabel("UserName")
		@JsonProperty("name")
		private String name;
		
		@CustomValidate(CustomVali.class)
		private String aaaa;
	}

	public static class CustomVali extends AbstractValidation {

		@Override
		protected ValidationResult doValidate(final Object value) {
			if (null != value) {
				String s = value.toString();
				if (2 != s.length()) {
					return ValidationResult.error("エラー");
				}
			}
			return ValidationResult.success();
		}

	}
}
