package io.github.howtis.assetoptimizer.svg;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SvgOptimizerTest {

    private final SvgOptimizer optimizer = new SvgOptimizer();

    @Test
    void removesXmlComments() throws IOException {
        String input = "<svg><!-- comment --><rect/></svg>";
        String result = optimizer.optimize(input);
        assertEquals("<svg><rect/></svg>", result);
    }

    @Test
    void removesMetadataElement() throws IOException {
        String input = "<svg><metadata><rdf/></metadata><rect/></svg>";
        String result = optimizer.optimize(input);
        assertEquals("<svg><rect/></svg>", result);
    }

    @Test
    void removesEmptyGroup() throws IOException {
        String input = "<svg><g></g><rect/></svg>";
        String result = optimizer.optimize(input);
        assertEquals("<svg><rect/></svg>", result);
    }

    @Test
    void removesWhitespaceBetweenTags() throws IOException {
        String input = "<svg>\n  <rect/>\n  <circle/>\n</svg>";
        String result = optimizer.optimize(input);
        assertEquals("<svg><rect/><circle/></svg>", result);
    }

    @Test
    void preservesTextContent() throws IOException {
        String input = "<svg><text>Hello World</text></svg>";
        String result = optimizer.optimize(input);
        assertEquals("<svg><text>Hello World</text></svg>", result);
    }

    @Test
    void preservesNonEmptyGroup() throws IOException {
        String input = "<svg><g><rect/></g></svg>";
        String result = optimizer.optimize(input);
        assertEquals("<svg><g><rect/></g></svg>", result);
    }

    @Test
    void preservesSvgAttributes() throws IOException {
        String input = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 100 100\"><rect/></svg>";
        String result = optimizer.optimize(input);
        assertEquals("<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 100 100\"><rect/></svg>", result);
    }

    @Test
    void handlesMixedOptimizations() throws IOException {
        String input = "<svg xmlns=\"http://www.w3.org/2000/svg\">\n  <!-- logo -->\n  <metadata><rdf/></metadata>\n  <g>\n    <rect/>\n  </g>\n  <g></g>\n</svg>";
        String result = optimizer.optimize(input);
        assertEquals("<svg xmlns=\"http://www.w3.org/2000/svg\"><g><rect/></g></svg>", result);
    }

    @Test
    void handlesNestedEmptyGroups() throws IOException {
        String input = "<svg><g><g></g></g><rect/></svg>";
        String result = optimizer.optimize(input);
        assertEquals("<svg><rect/></svg>", result);
    }

    @Test
    void removesGroupWithOnlyWhitespace() throws IOException {
        String input = "<svg><g>   </g><rect/></svg>";
        String result = optimizer.optimize(input);
        assertEquals("<svg><rect/></svg>", result);
    }

    @Test
    void preservesGroupWithText() throws IOException {
        String input = "<svg><g><text>label</text></g></svg>";
        String result = optimizer.optimize(input);
        assertEquals("<svg><g><text>label</text></g></svg>", result);
    }

    @Test
    void handlesMultipleComments() throws IOException {
        String input = "<svg><!-- a --><rect/><!-- b --><circle/><!-- c --></svg>";
        String result = optimizer.optimize(input);
        assertEquals("<svg><rect/><circle/></svg>", result);
    }

    @Test
    void handlesCdataInSvg() throws IOException {
        String input = "<svg><style><![CDATA[.cls { fill: red; }]]></style><rect/></svg>";
        String result = optimizer.optimize(input);
        assertTrue(result.contains(".cls"));
        assertTrue(result.contains("<rect/>"));
    }
}
