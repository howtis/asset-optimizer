package io.github.howtis.assetoptimizer.svg;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public final class SvgOptimizer {

    private static final Pattern BETWEEN_TAGS = Pattern.compile(">\\s+<");
    private static final Pattern WHITESPACE = Pattern.compile("\\s{2,}");

    public void optimizeFile(Path input, Path output) throws IOException {
        String svg = Files.readString(input);
        String result = optimize(svg);
        Files.createDirectories(output.getParent());
        Files.writeString(output, result);
    }

    String optimize(String svg) throws IOException {
        Document doc = parse(svg);
        removeComments(doc);
        removeElements(doc, "metadata");
        removeEmptyGroups(doc, "g");
        String serialized = serialize(doc);
        return collapseWhitespace(serialized);
    }

    private static Document parse(String xml) throws IOException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setNamespaceAware(true);
            return dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            throw new IOException("Failed to parse SVG", e);
        }
    }

    private static void removeComments(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.COMMENT_NODE) {
                node.removeChild(child);
            } else {
                removeComments(child);
            }
        }
    }

    private static void removeElements(Node node, String tagName) {
        NodeList children = node.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE
                    && tagName.equals(child.getLocalName())) {
                node.removeChild(child);
            } else {
                removeElements(child, tagName);
            }
        }
    }

    private static void removeEmptyGroups(Node node, String tagName) {
        NodeList children = node.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeEmptyGroups(child, tagName);
                if (tagName.equals(child.getLocalName()) && !hasElementChild(child)) {
                    node.removeChild(child);
                }
            }
        }
    }

    private static boolean hasElementChild(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }

    private static String serialize(Document doc) throws IOException {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            throw new IOException("Failed to serialize SVG", e);
        }
    }

    private static String collapseWhitespace(String svg) {
        String result = BETWEEN_TAGS.matcher(svg).replaceAll("><");
        result = WHITESPACE.matcher(result).replaceAll(" ");
        return result.trim();
    }
}
