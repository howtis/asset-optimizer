package io.github.howtis.assetoptimizer.html;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlMinifierTest {

    private final HtmlMinifier minifier = new HtmlMinifier();

    @Test
    void removesWhitespaceBetweenTags() {
        String input = "<div>\n  <p>Hello</p>\n</div>";
        String expected = "<div><p>Hello</p></div>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void collapsesMultipleWhitespaceInText() {
        String input = "<p>Hello    World</p>";
        String expected = "<p>Hello World</p>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void removesHtmlComments() {
        String input = "<div><!-- comment --><p>text</p></div>";
        String expected = "<div><p>text</p></div>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void preservesPreTagContent() {
        String input = "<pre>\n  line1\n  line2\n</pre>";
        String expected = "<pre>\n  line1\n  line2\n</pre>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void preservesTextareaContent() {
        String input = "<textarea>\n  foo bar\n</textarea>";
        String expected = "<textarea>\n  foo bar\n</textarea>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void preservesScriptContent() {
        String input = "<script>\n  var x = 1;\n</script>";
        String expected = "<script>\n  var x = 1;\n</script>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void preservesStyleContent() {
        String input = "<style>\n  body { color: red; }\n</style>";
        String expected = "<style>\n  body { color: red; }\n</style>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void handlesMixedContent() {
        String input = "<html>\n<head>\n  <style>\n    p { margin: 0; }\n  </style>\n</head>\n<body>\n  <!-- header -->\n  <div>\n    <p>Hello  World</p>\n    <pre>\n      code\n    </pre>\n  </div>\n</body>\n</html>";
        String expected = "<html><head><style>\n    p { margin: 0; }\n  </style></head><body><div><p>Hello World</p><pre>\n      code\n    </pre></div></body></html>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void removesThymeleafParserComments() {
        String input = "<div><!--/* parser comment */--><p>text</p></div>";
        String expected = "<div><p>text</p></div>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void convertsThymeleafPrototypeComments() {
        String input = "<div><!--/*/ prototype comment /*/--><p>text</p></div>";
        String expected = "<div><p>text</p></div>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void preservesThInlineTextContent() {
        String input = "<div th:inline=\"text\">\n  Hello   [[${name}]]\n</div>";
        String expected = "<div th:inline=\"text\">\n  Hello   [[${name}]]\n</div>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void preservesInlineExpressions() {
        String input = "<p>Hello [[${name}]]  world</p>";
        String expected = "<p>Hello [[${name}]] world</p>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void handlesThymeleafMixedContent() {
        String input = "<html>\n<head>\n  <!--/* parser */-->\n</head>\n<body>\n  <!--/*/ prototype /*/-->\n  <div th:inline=\"text\">\n    [[${greeting}]], [[${name}]]!\n  </div>\n  <p>Static  text</p>\n</body>\n</html>";
        String expected = "<html><head></head><body><div th:inline=\"text\">\n    [[${greeting}]], [[${name}]]!\n  </div><p>Static text</p></body></html>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void emptyString() {
        assertEquals("", minifier.minify(""));
    }

    @Test
    void whitespaceOnly() {
        assertEquals("", minifier.minify("   \n  \t  "));
    }

    @Test
    void preservesHtmlEntities() {
        String input = "<p>Hello&nbsp;World</p>";
        String expected = "<p>Hello&nbsp;World</p>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void preservesDoctype() {
        String input = "<!DOCTYPE html>\n<html>\n<body><p>text</p></body>\n</html>";
        String result = minifier.minify(input);
        assertTrue(result.startsWith("<!DOCTYPE html>"));
        assertTrue(result.contains("<p>text</p>"));
    }

    @Test
    void handlesMultipleComments() {
        String input = "<div><!-- a --><p><!-- b -->text<!-- c --></p><!-- d --></div>";
        String expected = "<div><p>text</p></div>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void preservesConditionalComments() {
        // conditional comments are treated as regular comments and removed
        String input = "<div><!--[if IE]><p>IE only</p><![endif]--></div>";
        String expected = "<div></div>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void handlesCdataInScriptTags() {
        String input = "<script>\n//<![CDATA[\n  var x = 1;\n//]]>\n</script>";
        // the trailing newline before </script> is collapsed by the minifier
        String expected = "<script>\n//<![CDATA[\n  var x = 1;\n//]]></script>";
        assertEquals(expected, minifier.minify(input));
    }

    // --- Thymeleaf-specific edge cases ---

    @Test
    void preservesUnescapedExpressions() {
        String input = "<p>Hello [( ${name} )] world</p>";
        String expected = "<p>Hello [( ${name} )] world</p>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void preservesThBlockElement() {
        String input = "<th:block th:each=\"item : ${items}\">\n  <p th:text=\"${item}\">desc</p>\n</th:block>";
        String expected = "<th:block th:each=\"item : ${items}\"><p th:text=\"${item}\">desc</p></th:block>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void preservesThymeleafAttributes() {
        String input = "<div th:text=\"${msg}\" th:if=\"${show}\">\n  <p>fallback</p>\n</div>";
        String expected = "<div th:text=\"${msg}\" th:if=\"${show}\"><p>fallback</p></div>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void preservesMultipleThInlineBlocks() {
        String input = "<section>\n  <div th:inline=\"text\">\n    Hello   [[${name}]]\n  </div>\n  <div th:inline=\"text\">\n    Bye   [[${name}]]\n  </div>\n</section>";
        String expected = "<section><div th:inline=\"text\">\n    Hello   [[${name}]]\n  </div><div th:inline=\"text\">\n    Bye   [[${name}]]\n  </div></section>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void preservesThInlineOnParagraph() {
        String input = "<p th:inline=\"text\">\n  Hello   [[${name}]]\n</p>";
        String expected = "<p th:inline=\"text\">\n  Hello   [[${name}]]\n</p>";
        assertEquals(expected, minifier.minify(input));
    }

    @Test
    void preservesThInlineJavascript() {
        String input = "<script th:inline=\"javascript\">\n  var name = /*[[${name}]]*/ 'default';\n</script>";
        String result = minifier.minify(input);
        assertTrue(result.contains("var name = /*[[${name}]]*/ 'default';"));
    }

    @Test
    void handlesThymeleafFragmentReplace() {
        String input = "<header th:fragment=\"header\">\n  <h1 th:text=\"${title}\">Title</h1>\n  <nav th:replace=\"~{fragments :: nav}\"></nav>\n</header>";
        String expected = "<header th:fragment=\"header\"><h1 th:text=\"${title}\">Title</h1><nav th:replace=\"~{fragments :: nav}\"></nav></header>";
        assertEquals(expected, minifier.minify(input));
    }
}
