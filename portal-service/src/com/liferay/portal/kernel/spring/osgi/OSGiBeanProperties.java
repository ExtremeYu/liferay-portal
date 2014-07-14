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

package com.liferay.portal.kernel.spring.osgi;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Provides the OSGi service properties used when publishing Spring beans as
 * services.
 *
 * @author Raymond Augé
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OSGiBeanProperties {

	/**
	 * Returns <code>true</code> if the property prefix should be removed from
	 * <code>portal.properties</code>.
	 *
	 * @return <code>true</code> if the property prefix should be removed from
	 *         <code>portal.properties</code>; <code>false</code> otherwise
	 */
	public boolean portalPropertiesRemovePrefix() default true;

	/**
	 * Returns the value of the property prefix used for retrieving properties
	 * from <code>portal.properties</code>.
	 *
	 * @return the value of the property prefix
	 */
	public String portalPropertyPrefix() default "";

	/**
	 * Returns the service properties.
	 *
	 * <p>
	 * Each property string is specified as <code>"key=value"</code>. To specify
	 * a property with multiple values, use multiple key-value pairs. For
	 * example, <code>"foo=bar", "foo=baz"</code>.
	 * </p>
	 *
	 * @return the service properties
	 */
	public String[] property() default {};

	/**
	 * Converts OSGi bean properties from the {@link OSGiBeanProperties}
	 * annotation into a properties map. This is a helper class.
	 */
	public static class Convert {

		/**
		 * Converts the OSGi bean properties object to a properties map.
		 *
		 * @param  object the object that is possibly annotated with {@link 
		 *         OSGiBeanProperties}
		 * @return a converted properties map, which may be empty if there are
		 *         no properties found, or <code>null</code> if the object is
		 *         not annotated with {@link OSGiBeanProperties}
		 */
		public static Map<String, Object> fromObject(Object object) {
			Class<? extends Object> clazz = object.getClass();

			OSGiBeanProperties osgiBeanProperties = clazz.getAnnotation(
				OSGiBeanProperties.class);

			if (osgiBeanProperties == null) {
				return null;
			}

			return toMap(osgiBeanProperties);
		}

		/**
		 * Converts the {@link OSGiBeanProperties} instance to a properties map.
		 *
		 * @param  osgiBeanProperties the instance of {@link OSGiBeanProperties},
		 *         which is read for properties
		 * @return a converted properties map, which may be empty if there are
		 *         no properties found
		 */
		public static Map<String, Object> toMap(
			OSGiBeanProperties osgiBeanProperties) {

			Map<String, Object> properties = new HashMap<String, Object>();

			for (String property : osgiBeanProperties.property()) {
				String[] parts = property.split(StringPool.EQUAL, 2);

				if (parts.length <= 1) {
					continue;
				}

				String key = parts[0];
				String className = String.class.getSimpleName();

				if (key.indexOf(StringPool.COLON) != -1) {
					String[] keyParts = StringUtil.split(key, StringPool.COLON);

					key = keyParts[0];
					className = keyParts[1];
				}

				String value = parts[1];

				_put(key, value, className, properties);
			}

			String portalPropertyPrefix =
				osgiBeanProperties.portalPropertyPrefix();

			if (Validator.isNotNull(portalPropertyPrefix)) {
				Properties portalProperties = PropsUtil.getProperties(
					portalPropertyPrefix,
					osgiBeanProperties.portalPropertiesRemovePrefix());

				properties.putAll(PropertiesUtil.toMap(portalProperties));
			}

			return properties;
		}

		private static void _put(
			String key, String value, String className,
			Map<String, Object> properties) {

			Type type = Type._getType(className);

			Object previousValue = properties.get(key);

			properties.put(key, type._convert(value, previousValue));
		}

	}

	public enum Type {

		BOOLEAN, BYTE, CHARACTER, DOUBLE, FLOAT, INTEGER, LONG, SHORT, STRING;

		private static Type _getType(String name) {
			name = StringUtil.toUpperCase(name);

			for (Type type : values()) {
				if (name.equals(type.name())) {
					return type;
				}
			}

			return Type.STRING;
		}

		private Object _convert(String value, Object previousValue) {
			if (previousValue == null) {
				return _getTypedValue(value);
			}

			Class<?> clazz = previousValue.getClass();

			if (!clazz.isArray()) {
				Object array = Array.newInstance(_getTypeClass(), 2);

				Array.set(array, 0, previousValue);
				Array.set(array, 1, _getTypedValue(value));

				return array;
			}

			Object array = Array.newInstance(
				_getTypeClass(), Array.getLength(previousValue) + 1);

			for (int i = 0; i < Array.getLength(previousValue); i++) {
				Array.set(array, i, Array.get(previousValue, i));
			}

			Array.set(
				array, Array.getLength(previousValue), _getTypedValue(value));

			return array;
		}

		private Class<?> _getTypeClass() {
			if (this == Type.BOOLEAN) {
				return java.lang.Boolean.class;
			}
			else if (this == Type.BYTE) {
				return java.lang.Byte.class;
			}
			else if (this == Type.CHARACTER) {
				return java.lang.Character.class;
			}
			else if (this == Type.DOUBLE) {
				return java.lang.Double.class;
			}
			else if (this == Type.FLOAT) {
				return java.lang.Float.class;
			}
			else if (this == Type.INTEGER) {
				return java.lang.Integer.class;
			}
			else if (this == Type.LONG) {
				return java.lang.Long.class;
			}
			else if (this == Type.SHORT) {
				return java.lang.Short.class;
			}
			else if (this == Type.STRING) {
				return java.lang.String.class;
			}

			return null;
		}

		private Object _getTypedValue(String value) {
			if (this == Type.BOOLEAN) {
				return GetterUtil.getBoolean(value);
			}
			else if (this == Type.BYTE) {
				return new java.lang.Byte(value).byteValue();
			}
			else if (this == Type.CHARACTER) {
				return value.charAt(0);
			}
			else if (this == Type.DOUBLE) {
				return GetterUtil.getDouble(value);
			}
			else if (this == Type.FLOAT) {
				return GetterUtil.getFloat(value);
			}
			else if (this == Type.INTEGER) {
				return GetterUtil.getInteger(value);
			}
			else if (this == Type.LONG) {
				return GetterUtil.getLong(value);
			}
			else if (this == Type.SHORT) {
				return GetterUtil.getShort(value);
			}

			return value;
		}

	}

}