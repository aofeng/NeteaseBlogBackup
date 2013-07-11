/**
 * 建立时间：2010-5-5
 */
package cn.aofeng.util.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cn.aofeng.util.IOUtils;
import cn.aofeng.util.NullWriter;
import cn.aofeng.util.Visitor;


/**
 * HTML/XML 解析器.
 *
 * @author 聂勇 <a href="mailto:nieyong@asiainfo.com">nieyong@asiainfo.com</a>
 */
public class HtmlParser {

    /**
     * Logger for this class.
     */
    private final static Logger _logger = Logger.getLogger(HtmlParser.class);
    
    private String getInputCharset(String inputCharset) {
        if (StringUtils.isBlank(inputCharset)) {
            inputCharset = "GBK";
        }
        
        return inputCharset;
    }
    
    private String getOutputCharset(String outputCharset) {
        if (StringUtils.isBlank(outputCharset)) {
            outputCharset = "UTF-8";
        }
        
        return outputCharset;
    }
    
	/**
	 * 根据XPATH表达式解析HTML内容，获取符合条件的元素.
	 * 
	 * @param urlString URL.
	 * @param expression xPath表达式.
	 * @param inputCharset 输入编码类型.
	 * @param outputCharset 输出编码类型.
	 * @return 符合条件的元素集合.
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public NodeList parseHtml(String urlString, String expression, String inputCharset, 
	                String outputCharset) throws IOException, XPathExpressionException {
	    inputCharset  = getInputCharset(inputCharset);
	    outputCharset = getOutputCharset(outputCharset);
	    
        InputStream ins = null;
        try {
            ins = HttpUtils.getInputStream(urlString, outputCharset);
            
            return parseHtml(ins, expression, inputCharset, outputCharset);
        } finally {
            IOUtils.close(ins);
        }
    }
    
	/**
	 * 根据XPATH表达式解析HTML内容，获取符合条件的元素.
	 * 
	 * @param ins 需解析的输入流.
	 * @param xPathExpression xPath表达式.
	 * @param inputCharset 输入编码类型.
	 * @param outputCharset 输出编码类型.
	 * @return 符合条件的元素集合.
	 * @throws XPathExpressionException
	 * @throws IOException
	 */
    public NodeList parseHtml(InputStream ins, String xPathExpression, String inputCharset, 
                    String outputCharset) throws XPathExpressionException, IOException {
        inputCharset  = getInputCharset(inputCharset);
        outputCharset = getOutputCharset(outputCharset);
        
        Tidy tidy = new Tidy();
        InputStreamReader reader = new InputStreamReader(ins, inputCharset);
        Document document = tidy.parseDOM(reader, new NullWriter());
        
        return parseHtml(document, xPathExpression);
    }
    
    public NodeList parseHtml(Node node, String xPathExpression) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xpath.evaluate(xPathExpression, node, XPathConstants.NODESET);
        
        return nodeList;
    }
    
    /**
     * 将XML节点(元素)对象输出成XML字符串.
     * 
     * @param node XML节点(元素).
     * @param outputCharset 输出编码类型.
     * @return XML字符串.
     * @throws TransformerException
     * @throws UnsupportedEncodingException 输出编码类型无效或不支持.
     */
    public String dom2Xml(Node node, String outputCharset) 
                    throws TransformerException, UnsupportedEncodingException {
    	if (null != node) {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, outputCharset);
            
            DOMSource source = new DOMSource(node);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            StreamResult streamResult = new StreamResult(outputStream);
            
            transformer.transform(source, streamResult);
            String xml = outputStream.toString(outputCharset);
            
            String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            if (xml.startsWith(prefix)) {
                xml = xml.substring(prefix.length());
            }
            
            return xml;
        }
        
        return null;
    }
    
    /**
     * 将多个XML节点(元素)对象输出成XML字符串.
     * 
     * @param nodeList 多个XML节点(元素)
     * @param outputCharset 输出编码类型.
     * @return XML字符串.
     * @throws TransformerException
     * @throws UnsupportedEncodingException 输出编码类型无效或不支持.
     * @throws ParserConfigurationException 
     */
    public String dom2Xml(NodeList nodeList, String outputCharset) 
            throws TransformerException, UnsupportedEncodingException {
        if (null != nodeList) {
            StringBuilder buff = new StringBuilder();
            for (int i = 0; i < nodeList.getLength(); i++) {
                buff.append(dom2Xml(nodeList.item(i), outputCharset));
            }
            
            return buff.toString();
		}
        
        return null;
    }
    
    /**
     * 将XML字符串转换成{@link org.w3c.dom.Document}}实例.
     * 
     * @param inputStream XML内容输入流.
     * @param inputCharset 输入流编码类型.
     * @return {@link org.w3c.dom.Document}}实例, 转换失败返回null.
     * @throws ParserConfigurationException
     */
    private Document xml2Dom(InputStream inputStream, String inputCharset) throws ParserConfigurationException {
        Document result = null;
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(inputStream, inputCharset);
            result = documentBuilder.parse(new InputSource(reader));
        } catch (UnsupportedEncodingException e) {
            _logger.error("", e);
        } catch (SAXException e) {
            _logger.error("", e);
        } catch (IOException e) {
            _logger.error("", e);
        } finally {
            IOUtils.close(reader);
        }
        
        return result;
    }
    
    /**
     * 将XML字符串转换成{@link org.w3c.dom.Document}}实例.
     * 
     * @param xmlString XML字符串.
     * @param inputCharset 输入流编码类型.
     * @return {@link org.w3c.dom.Document}}实例, 转换失败返回null.
     */
    public Document xml2Dom(String xmlString, String inputCharset) {
        if (StringUtils.isBlank(xmlString) || StringUtils.isBlank(inputCharset)) {
            throw new IllegalArgumentException("xml string and charset can not be empty");
        }
        
        Document result = null;
        ByteArrayInputStream ins = null;
        try {
            ins = new ByteArrayInputStream(xmlString.getBytes(inputCharset));
            
            result = xml2Dom(ins, inputCharset);
        } catch (UnsupportedEncodingException e) {
            _logger.error("", e);
        } catch (ParserConfigurationException e) {
            _logger.error("", e);
        } finally {
            IOUtils.close(ins);
        }
        
        return result;
    }
    
    /**
     * 将XML字符串转换成{@link org.w3c.dom.Document}}实例.
     * 
     * @param xmlFile 存储XML字符串的文件实例.
     * @param inputCharset 输入流编码类型.
     * @return {@link org.w3c.dom.Document}}实例, 转换失败返回null.
     */
    public Document xml2Dom(File xmlFile, String inputCharset) {
        if (null == xmlFile || StringUtils.isBlank(inputCharset)) {
            throw new IllegalArgumentException("xml file instance and charset can not be null");
        }
        
        Document document = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(xmlFile);
            document = xml2Dom(inputStream, inputCharset);
        } catch (FileNotFoundException e) {
            _logger.error("", e);
        } catch (ParserConfigurationException e) {
            _logger.error("", e);
        }
        
        return document;
    }
    
    private static void nodeIterate(Node node, Visitor<Node> visitor) {
        NodeList nodeList = node.getChildNodes();
        nodeListIterate(nodeList, visitor);
    }
    
    private static void nodeListIterate(NodeList nodeList, Visitor<Node> visitor) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            visitor.process(node);
            nodeIterate(node, visitor);
        }
    }
    
    /**
     * 历遍{@link org.w3c.dom.Document}}的DOM树.
     * 
     * @param document {@link org.w3c.dom.Document}}实例.
     * @param visitor 访问者.
     */
    public static void domIterate(Document document, Visitor<Node> visitor) {
        if (null == document) {
            throw new IllegalArgumentException("document can not be null");
        }
        
        NodeList nodeList = document.getChildNodes();
        nodeListIterate(nodeList, visitor);
    }

    /**
     * 历遍{@link org.w3c.dom.NodeList}}的DOM树.
     * 
     * @param nodeList {@link org.w3c.dom.NodeList}}实例.
     * @param visitor 访问者.
     */
    public static void domIterate(NodeList nodeList, Visitor<Node> visitor) {
    	nodeListIterate(nodeList, visitor);
    }

}
