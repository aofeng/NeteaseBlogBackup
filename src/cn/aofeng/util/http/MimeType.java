/**
 * 建立时间：2010-5-7
 */
package cn.aofeng.util.http;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.aofeng.util.Visitor;
import java.net.URI;


/**
 * MIME类型.
 *
 * @author 聂勇，<a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class MimeType {
    
    /**
     * Logger for this class.
     */
    private final static Logger _logger = Logger.getLogger(MimeType.class);
	
	private MimeType() {
		
	}

	static Map<String, String> _mimeTypeMap = new HashMap<String, String>();
	
	public static String getMimeType(String suffix) {
		String mimeType = _mimeTypeMap.get(suffix);
		
		if (StringUtils.isBlank(mimeType)) {
			mimeType = "application/octet-stream";
		}
		
		return mimeType;
	}
	
	private static void loadMimeType() {
	    URI mimeTypeConfigFilePath = null;
        try {
            mimeTypeConfigFilePath = MimeType.class.getResource("/MimeType.xml").toURI();
        } catch (URISyntaxException ex) {
            _logger.error("", ex);
        }

        if (null != mimeTypeConfigFilePath) {
            HtmlParser parser = new HtmlParser();
            Document document = parser.xml2Dom(new File(mimeTypeConfigFilePath), "UTF-8");
            HtmlParser.domIterate(document, new LoadMimeVisitor<Node>());
        }
	    
	}
	
	static {
	    try {
	        loadMimeType();
	        
	        if (_logger.isDebugEnabled()) {
                _logger.debug("load [" + _mimeTypeMap.size() + "] mime-type data from [/MimeType.xml]");
                _logger.debug(_mimeTypeMap.toString());
            }
        } catch (Exception e) {
            _logger.error("load mime type data from [/MimeType.xml] occurs error", e);
        }
	    
	}
	
	static class LoadMimeVisitor<T extends Node> implements Visitor<T> {
	    
        @Override
        public void addVisitor(Visitor<T> visitor) {
            throw new IllegalStateException("not yet implements");
        }
        
        @Override
        public void process(T node) {
            if ("mime-mapping".equalsIgnoreCase(node.getNodeName())) {
                NodeList nodeList = node.getChildNodes();
                String extension = null;
                String mimeType  = null;
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node tempNode = nodeList.item(i);
                    
                    if ("extension".equalsIgnoreCase(tempNode.getNodeName())) {
                        extension = tempNode.getFirstChild().getNodeValue();
                    } else if ("mime-type".equalsIgnoreCase(tempNode.getNodeName())) {
                        mimeType  = tempNode.getFirstChild().getNodeValue();
                    }
                }
                
                if (!StringUtils.isBlank(extension) && !StringUtils.isBlank(mimeType)) {
                    _mimeTypeMap.put(extension, mimeType);
                }
            }
        }
	}

}
