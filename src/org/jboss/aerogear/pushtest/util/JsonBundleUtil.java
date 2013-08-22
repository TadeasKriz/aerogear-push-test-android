package org.jboss.aerogear.pushtest.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

/**
 * Converts Bundle to JSON
 * 
 * @author kpiwko
 * 
 */
public class JsonBundleUtil {

    public JsonBundleUtil() {
        throw new UnsupportedOperationException("Unable to instantiate JsonBundleUtil. Please use it as static.");
    }

    public static JSONObject asJson(Bundle bundle) {
        JSONObject json = new JSONObject();
        try {
            return asJSON(json, bundle);
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String asJsonPrettyPrint(Bundle bundle) {
        return prettyPrint(asJson(bundle));
    }

    public static String prettyPrint(JSONObject json) {
        try {
            return json.toString(2);
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static JSONObject asJSON(JSONObject result, Bundle bundle) throws JSONException {

        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            if (value instanceof String) {
                value = tryToConvertToPrimitive((String) value);
            }

            // now handle primitives
            if (value instanceof Integer) {
                result.put(key, ((Integer) value).intValue());
            } else if (value instanceof Long) {
                result.put(key, ((Long) value).longValue());
            } else if (value instanceof Boolean) {
                result.put(key, ((Boolean) value).booleanValue());
            } else if (value instanceof Double) {
                result.put(key, ((Double) value).doubleValue());
            } else if (value instanceof String) {
                result.put(key, value);
            } else if (value instanceof JSONArray || value instanceof JSONObject || value == null) {
                result.put(key, value);
            } else if (value instanceof Bundle) {
                JSONObject nestedJson = asJson((Bundle) bundle);
                result.put(key, nestedJson);
            } else {
                throw new JSONException("Unable to convert key: " + key + " with value: " + value.toString() + " to JSON.");
            }
        }

        return result;
    }

    private static Object tryToConvertToPrimitive(String value) {
        Object newValue = null;

        // try to convert to integer
        if (newValue == null) {
            try {
                newValue = Integer.valueOf(value);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        // try to convert to long
        if (newValue == null) {
            try {
                newValue = Long.valueOf(value);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        // try to convert to double
        if (newValue == null) {
            try {
                newValue = Double.valueOf(value);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        // try to convert to boolean
        if (newValue == null) {
            if ("true".equalsIgnoreCase(value)) {
                newValue = Boolean.TRUE;
            } else if ("false".equalsIgnoreCase(value)) {
                newValue = Boolean.FALSE;
            }
        }

        // if newValue is not empty, we changed the object to primitive
        // otherwise, we return original value
        return newValue == null ? value : newValue;
    }
}
