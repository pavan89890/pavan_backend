package com.pavan.util;

import java.text.SimpleDateFormat;
import java.util.Collection;

public class Utility {
	public static final SimpleDateFormat onlyDateSdf = new SimpleDateFormat("dd-MM-yyyy");

	public static boolean isEmpty(Object obj) {
		try {

			if (obj instanceof Collection) {
				return obj == null || ((Collection<?>) obj).size() == 0;
			} else if (obj instanceof String) {
				return obj == null || ((String) obj).isEmpty();
			} else if (obj instanceof Integer) {
				return obj == null || Integer.valueOf(String.valueOf(obj)) == 0;
			} else if (obj instanceof Long) {
				return obj == null || Long.valueOf(String.valueOf(obj)) == 0;
			} else if (obj instanceof Float) {
				return obj == null || Float.valueOf(String.valueOf(obj)) == 0;
			} else if (obj instanceof Double) {
				return obj == null || Double.valueOf(String.valueOf(obj)) == 0;
			} else {
				return obj == null;
			}

		} catch (Exception e) {
			return false;
		}
	}

}