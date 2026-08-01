import java.util.*;

public class Json {
    public static String escape(String s) {
        if (s == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                default: sb.append(c);
            }
        }
        return sb.append("\"").toString();
    }

    public static String toJson(Application a) {
        return "{\"id\":" + a.id()
            + ",\"companyName\":" + escape(a.companyName())
            + ",\"role\":" + escape(a.role())
            + ",\"status\":" + escape(a.status())
            + ",\"appliedDate\":" + escape(a.appliedDate())
            + ",\"followUpDate\":" + escape(a.followUpDate())
            + ",\"notes\":" + escape(a.notes()) + "}";
    }

    public static String toJsonArray(List<Application> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(toJson(list.get(i)));
            if (i < list.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    // Minimal parser: reads a flat {"key":"value",...} object (our only need)
    public static Map<String, String> parseFlatObject(String json) {
        Map<String, String> map = new LinkedHashMap<>();
        int i = json.indexOf('{') + 1, n = json.length();
        while (i < n) {
            while (i < n && " \n\r\t,".indexOf(json.charAt(i)) >= 0) i++;
            if (i >= n || json.charAt(i) == '}') break;
            i++; // skip opening quote of key
            StringBuilder key = new StringBuilder();
            while (json.charAt(i) != '"') key.append(json.charAt(i++));
            i++; // skip closing quote
            while (json.charAt(i) == ' ' || json.charAt(i) == ':') i++;
            String value;
            if (json.charAt(i) == '"') {
                i++;
                StringBuilder val = new StringBuilder();
                while (json.charAt(i) != '"') {
                    if (json.charAt(i) == '\\') { i++; val.append(json.charAt(i)); }
                    else val.append(json.charAt(i));
                    i++;
                }
                i++;
                value = val.toString();
            } else {
                StringBuilder val = new StringBuilder();
                while (i < n && json.charAt(i) != ',' && json.charAt(i) != '}') val.append(json.charAt(i++));
                String raw = val.toString().trim();
                value = raw.equals("null") ? null : raw;
            }
            map.put(key.toString(), value);
        }
        return map;
    }
}