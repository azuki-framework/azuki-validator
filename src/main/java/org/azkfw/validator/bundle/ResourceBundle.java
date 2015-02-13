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
package org.azkfw.validator.bundle;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * @since 1.0.0
 * @version 1.0.0 2015/02/13
 * @author kawakicchi
 */
public class ResourceBundle implements StringBundle {

	private String name;

	private Map<String, Map<String, String>> resources;

	public ResourceBundle(final String name) {
		this.name = name;
		resources = new HashMap<String, Map<String, String>>();
		resources.put(null, read(null));
	}

	@Override
	public String get(final String name) {
		return get(name, null);
	}

	@Override
	public String get(final String name, final Locale locale) {
		String lang = null;
		if (null != locale) {
			lang = locale.getLanguage();
		} else {
			lang = Locale.getDefault().getLanguage();
		}

		String string = null;

		Map<String, String> resource = null;
		synchronized (resources) {
			if (resources.containsKey(lang)) {
				resource = resources.get(lang);
			} else {
				resource = read(lang);
				resources.put(lang, resource);
			}
		}
		string = resource.get(name);
		if (null == string) {
			resource = resources.get(null);
			string = resource.get(name);
		}

		return string;
	}

	private Map<String, String> read(final String lang) {
		Map<String, String> resource = new HashMap<String, String>();

		String path = name;
		if (null != lang && 0 < lang.length()) {
			int index = path.lastIndexOf(".");
			if (-1 == index) {
				path = String.format("%s_%s", path, lang);
			} else {
				path = String.format("%s_%s%s", path.substring(0, index), lang, path.substring(index));
			}
		}

		InputStream is = null;
		try {
			is = getClass().getResourceAsStream(path);
			if (null != is) {
				Properties p = new Properties();
				p.load(is);
				for (Object obj : p.keySet()) {
					String key = obj.toString();
					String value = p.getProperty(key);
					resource.put(key, value);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		return resource;
	}

}
