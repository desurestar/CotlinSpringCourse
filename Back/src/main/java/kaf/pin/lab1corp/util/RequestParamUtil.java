package kaf.pin.lab1corp.util;

public class RequestParamUtil {
    
    private RequestParamUtil() {
        // Utility class, no instantiation
    }
    
    public static Long getLongValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
