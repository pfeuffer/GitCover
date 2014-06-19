package de.pfeufferweb.gitcover;

import static java.lang.Integer.parseInt;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CoverageBuilder
{
    public Coverage computeAll(File directory) throws Exception
    {
        Coverage overallCoverage = new Coverage();
        Collection<File> files = FileUtils.listFiles(directory, new IOFileFilter()
        {

            @Override
            public boolean accept(File dir, String name)
            {
                return false;
            }

            @Override
            public boolean accept(File file)
            {
                return file.getName().equals("coverage.xml");
            }
        }, new IOFileFilter()
        {

            @Override
            public boolean accept(File dir, String name)
            {
                return false;
            }

            @Override
            public boolean accept(File file)
            {
                return true;
            }
        });
        for (File file : files)
        {
            overallCoverage.addAll(compute(file));
        }
        return overallCoverage;
    }

    public Coverage compute(File file) throws Exception
    {
        Coverage result = new Coverage();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver(new EntityResolver()
        {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
            {
                if (systemId.contains("cobertura.sourceforge.net"))
                {
                    return new InputSource(new StringReader(""));
                }
                else
                {
                    return null;
                }
            }
        });
        Document doc = builder.parse(file);
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression classExpr = xpath.compile("//class/@filename");
        NodeList foundFileNodes = (NodeList) classExpr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < foundFileNodes.getLength(); ++i)
        {
            String fileName = foundFileNodes.item(i).getNodeValue();
            result.addFile(fileName);
            XPathExpression linesExpr = xpath.compile("//class[@filename='" + fileName + "']//line");
            NodeList foundLineNodes = (NodeList) linesExpr.evaluate(doc, XPathConstants.NODESET);
            for (int j = 0; j < foundLineNodes.getLength(); ++j)
            {
                NamedNodeMap attributes = foundLineNodes.item(j).getAttributes();
                int line = parseInt(attributes.getNamedItem("number").getNodeValue());
                int hits = parseInt(attributes.getNamedItem("hits").getNodeValue());
                result.addLine(fileName, line, hits);
            }
        }
        return result;
    }
}
