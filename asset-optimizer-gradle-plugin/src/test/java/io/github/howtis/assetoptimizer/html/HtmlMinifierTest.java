package io.github.howtis.assetoptimizer.html;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
