import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shared helpers for fetching a LeetCode problem and formatting it as a
 * comment block, using the unofficial LeetCode public JSON/GraphQL endpoints.
 */
class LeetcodeLib {

    static final String START_MARKER = "LEETCODE-PROBLEM-START";
    static final String END_MARKER = "LEETCODE-PROBLEM-END";

    static String commentPrefixForExt(String ext) {
        switch (ext) {
            case ".py":
            case ".rb":
                return "#";
            default:
                return "//";
        }
    }

    // ---------- HTTP ----------

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private static String httpGet(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            throw new RuntimeException("HTTP " + res.statusCode() + " for " + url);
        }
        return res.body();
    }

    private static String httpPostJson(String url, String jsonBody) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            throw new RuntimeException("HTTP " + res.statusCode() + " for " + url);
        }
        return res.body();
    }

    // ---------- LeetCode fetch ----------

    @SuppressWarnings("unchecked")
    static String fetchSlugByNumber(String number) throws Exception {
        String body = httpGet("https://leetcode.com/api/problems/all/");
        Map<String, Object> root = (Map<String, Object>) MiniJson.parse(body);
        List<Object> pairs = (List<Object>) root.get("stat_status_pairs");
        for (Object o : pairs) {
            Map<String, Object> pair = (Map<String, Object>) o;
            Map<String, Object> stat = (Map<String, Object>) pair.get("stat");
            Object fid = stat.get("frontend_question_id");
            String fidStr = fid instanceof Double ? String.valueOf(((Double) fid).intValue()) : String.valueOf(fid);
            if (fidStr.equals(number)) {
                return (String) stat.get("question__title_slug");
            }
        }
        throw new RuntimeException("No LeetCode problem found with number " + number);
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> fetchProblemBySlug(String slug) throws Exception {
        String query = "query questionData($titleSlug: String!) { question(titleSlug: $titleSlug) { "
                + "questionFrontendId title titleSlug difficulty content } }";
        String jsonBody = "{\"query\":" + jsonString(query)
                + ",\"variables\":{\"titleSlug\":" + jsonString(slug) + "}}";

        String body = httpPostJson("https://leetcode.com/graphql", jsonBody);
        Map<String, Object> root = (Map<String, Object>) MiniJson.parse(body);
        Map<String, Object> data = (Map<String, Object>) root.get("data");
        Map<String, Object> question = data == null ? null : (Map<String, Object>) data.get("question");
        if (question == null) {
            throw new RuntimeException("No problem data returned for slug " + slug);
        }
        return question;
    }

    static Map<String, Object> fetchProblemByNumber(String number) throws Exception {
        String slug = fetchSlugByNumber(number);
        return fetchProblemBySlug(slug);
    }

    private static String jsonString(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    // ---------- HTML -> text ----------

    static String htmlToText(String html) {
        String text = html;
        text = replaceAll(text, "<sup>(.*?)</sup>", "^$1");
        text = replaceAll(text, "<sub>(.*?)</sub>", "_$1");
        text = replaceAll(text, "</li>", "\n");
        text = replaceAll(text, "<li>", "- ");
        text = replaceAll(text, "<br\\s*/?>", "\n");
        text = replaceAll(text, "</p>", "\n\n");
        text = replaceAll(text, "<p>", "");
        text = replaceAll(text, "</pre>", "\n");
        text = replaceAll(text, "<pre>", "\n");
        text = replaceAll(text, "<[^>]+>", "");

        text = decodeEntities(text);

        String[] lines = text.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
        }
        text = String.join("\n", lines);
        text = text.replaceAll("\n{3,}", "\n\n").trim();
        return text;
    }

    private static String replaceAll(String input, String regex, String replacement) {
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(input).replaceAll(replacement);
    }

    private static String decodeEntities(String s) {
        return s.replace("&nbsp;", " ")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&frasl;", "/");
    }

    // ---------- comment formatting ----------

    static String buildCommentBlock(Map<String, Object> problem, String prefix) {
        String content = (String) problem.get("content");
        String[] bodyLines = htmlToText(content).split("\n", -1);

        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append(" ").append(START_MARKER).append("\n");
        sb.append(prefix).append(" ").append(problem.get("questionFrontendId")).append(". ")
                .append(problem.get("title")).append(" [").append(problem.get("difficulty")).append("]\n");
        sb.append(prefix).append(" https://leetcode.com/problems/").append(problem.get("titleSlug")).append("/\n");
        sb.append(prefix).append("\n");

        for (String line : bodyLines) {
            if (!line.isEmpty()) {
                sb.append(prefix).append(" ").append(line).append("\n");
            } else {
                sb.append(prefix).append("\n");
            }
        }

        sb.append(prefix).append(" ").append(END_MARKER);
        return sb.toString();
    }

    static void writeCommentBlock(Path filePath, String commentBlock) throws IOException {
        String existing = Files.exists(filePath) ? Files.readString(filePath) : "";

        Pattern blockPattern = Pattern.compile("[^\\n]*" + START_MARKER + "[\\s\\S]*?" + END_MARKER + "\\n?");
        Matcher m = blockPattern.matcher(existing);

        String updated;
        if (m.find()) {
            updated = m.replaceFirst(Matcher.quoteReplacement(commentBlock + "\n"));
        } else {
            updated = existing.isEmpty() ? commentBlock + "\n" : commentBlock + "\n\n" + existing;
        }

        Files.writeString(filePath, updated);
    }
}

/**
 * Minimal recursive-descent JSON parser (object/array/string/number/boolean/null),
 * just enough to read LeetCode's API responses without an external dependency.
 */
class MiniJson {
    private final String s;
    private int i;

    private MiniJson(String s) {
        this.s = s;
        this.i = 0;
    }

    static Object parse(String s) {
        MiniJson p = new MiniJson(s);
        p.skipWs();
        return p.parseValue();
    }

    private void skipWs() {
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
    }

    private Object parseValue() {
        skipWs();
        char c = s.charAt(i);
        if (c == '{') return parseObject();
        if (c == '[') return parseArray();
        if (c == '"') return parseString();
        if (c == 't') { i += 4; return Boolean.TRUE; }
        if (c == 'f') { i += 5; return Boolean.FALSE; }
        if (c == 'n') { i += 4; return null; }
        return parseNumber();
    }

    private Map<String, Object> parseObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        i++; // {
        skipWs();
        if (s.charAt(i) == '}') { i++; return map; }
        while (true) {
            skipWs();
            String key = parseString();
            skipWs();
            i++; // :
            Object val = parseValue();
            map.put(key, val);
            skipWs();
            char c = s.charAt(i);
            if (c == ',') { i++; continue; }
            if (c == '}') { i++; break; }
        }
        return map;
    }

    private List<Object> parseArray() {
        List<Object> list = new ArrayList<>();
        i++; // [
        skipWs();
        if (s.charAt(i) == ']') { i++; return list; }
        while (true) {
            Object val = parseValue();
            list.add(val);
            skipWs();
            char c = s.charAt(i);
            if (c == ',') { i++; continue; }
            if (c == ']') { i++; break; }
        }
        return list;
    }

    private String parseString() {
        StringBuilder sb = new StringBuilder();
        i++; // opening quote
        while (true) {
            char c = s.charAt(i++);
            if (c == '"') break;
            if (c == '\\') {
                char e = s.charAt(i++);
                switch (e) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'u':
                        String hex = s.substring(i, i + 4);
                        sb.append((char) Integer.parseInt(hex, 16));
                        i += 4;
                        break;
                    default: sb.append(e);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private Double parseNumber() {
        int start = i;
        while (i < s.length() && "-+.eE0123456789".indexOf(s.charAt(i)) >= 0) i++;
        return Double.parseDouble(s.substring(start, i));
    }
}
