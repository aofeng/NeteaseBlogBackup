/**
 * 建立时间：2010-5-6
 */
package cn.aofeng.netease_blog_backup;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import cn.aofeng.util.FileUtils;
import cn.aofeng.util.Visitor;
import cn.aofeng.util.http.HttpUtils;
import cn.aofeng.util.http.MimeType;

/**
 * URL类型节点(图片,链接)访问者.
 *
 * @author 聂勇 <a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public abstract class AbstractUrlVisitor<T extends Node> implements Visitor<T> {

    /**
     * Logger for this class.
     */
    private final static Logger _logger = Logger.getLogger(AbstractUrlVisitor.class);
    
    private JavaMailUtils _javaMailUtil;
    
    /**
     * 节点(元素)名称.
     */
    protected String _nodeName = "";
    
    /**
     * 属性名称.
     */
    protected String _attrName = "";
    
    protected static Set<String> _suffixs = new HashSet<String>();
    
    private Set<String> _urls = new HashSet<String>();
    
    public AbstractUrlVisitor(JavaMailUtils javaMailUtil) {
        _javaMailUtil = javaMailUtil;
    }
    
    private static boolean needDownload(String url) {
        for (String suffix : _suffixs) {
            if (url.toLowerCase().endsWith(suffix)) {
                return true;
            }
        }
        
        return false;
    }
    
    /*
     * @see cn.aofeng.html.Visitor#addVisitor(cn.aofeng.html.Visitor)
     */
    @Override
    public void addVisitor(Visitor<T> visitor) {
        throw new IllegalStateException("not yet implements");
    }

    /*
     * @see cn.aofeng.html.Visitor#process(org.w3c.dom.Node)
     */
    @Override
    public void process(T node) {
        String nodeName = node.getNodeName();
        
        if (_nodeName.equalsIgnoreCase(nodeName)) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("NodeName="+nodeName);
            }
            
            NamedNodeMap attrMap = node.getAttributes();
            for (int i = 0; i < attrMap.getLength(); i++) {
                Node attrNode = attrMap.item(i);
                
                if (_logger.isDebugEnabled()) {
                    _logger.debug("AttrName=" + attrNode.getNodeName() + ", AttrValue=" + attrNode.getNodeValue());
                }
                
                if (_attrName.equalsIgnoreCase(attrNode.getNodeName())) {
                    try {
                        String attrValue = attrNode.getNodeValue();
                        if (needDownload(attrValue)) {
                            if (attrValue.toLowerCase().startsWith("http://")) {
                                if (! _urls.contains(attrValue)) {
                                    int point = attrValue.lastIndexOf("/");
                                    String filename = attrValue.substring(point+1);
                                    String suffix   = FileUtils.getSuffix(filename);
                                    
                                    InputStream ins = HttpUtils.getInputStream(attrValue, "UTF-8");
                                    ByteArrayDataSourceImpl dataSource = new ByteArrayDataSourceImpl(ins, MimeType.getMimeType(suffix)+"");
                                    dataSource.setName(filename);
                                    dataSource.setUrl(attrValue);
                                    
                                    _javaMailUtil.addAttachment(dataSource); 
                                    
                                    _urls.add(attrValue);
                                }
                            }
                        }
                    } catch (MalformedURLException e) {
                        _logger.error("", e);
                    } catch (DOMException e) {
                        _logger.error("", e);
                    } catch (HttpException e) {
                        _logger.error("", e);
                    } catch (IOException e) {
                        _logger.error("", e);
                    }
                    
                }
            }
        }
    }

    static {
        _suffixs.add(".jpg");
        _suffixs.add(".jpeg");
        _suffixs.add(".png");
        _suffixs.add(".bmp");
        _suffixs.add(".gif");
        _suffixs.add(".tiff");
        _suffixs.add(".tga");
        _suffixs.add(".svg");
        _suffixs.add(".psd");
    }

}
