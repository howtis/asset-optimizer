package io.github.howtis.assetoptimizer.html;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HtmlMinifier {

    private static final Pattern PRESERVE = Pattern.compile(
        "<(pre|textarea|script|style)\\b[^>]*>.*?</\\1>",
        Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private static final Pattern BETWEEN_TAGS = Pattern.compile(">\\s+<");

    private static final Pattern WHITESPACE = Pattern.compile("\\s{2,}");

    public void minifyFile(Path input, Path output) throws IOException {
        String html = Files.readString(input);
        String result = minify(html);
        Files.createDirectories(output.getParent());
        Files.writeString(output, result);
    }

    String minify(String html) {
        java.util.List<String> preserved = new java.util.ArrayList<>();
        Matcher preserveMatcher = PRESERVE.matcher(html);
        StringBuilder sb = new StringBuilder();
        while (preserveMatcher.find()) {
            preserved.add(preserveMatcher.group());
            preserveMatcher.appendReplacement(sb, "%%PRESERVE_" + (preserved.size() - 1) + "%%");
        }
        preserveMatcher.appendTail(sb);
        String result = sb.toString();

        result = COMMENT.matcher(result).replaceAll("");
        result = BETWEEN_TAGS.matcher(result).replaceAll("><");
        result = WHITESPACE.matcher(result).replaceAll(" ");
        result = result.trim();

        for (int i = 0; i < preserved.size(); i++) {
            result = result.replace("%%PRESERVE_" + i + "%%", preserved.get(i));
        }

        result = BETWEEN_TAGS.matcher(result).replaceAll("><");
        return result;
    }
}
