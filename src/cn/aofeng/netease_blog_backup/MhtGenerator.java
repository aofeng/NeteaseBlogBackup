/**
 * 建立时间：2010-5-9
 */
package cn.aofeng.netease_blog_backup;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.aofeng.util.StringUtils;
import cn.aofeng.util.Visitor;
import cn.aofeng.util.http.HtmlParser;

/**
 * MHT生成器.
 * 
 * @author 聂勇，<a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class MhtGenerator implements Runnable {
	
	private static final Logger _logger = Logger.getLogger(MhtGenerator.class);
	
	public final static String SLASH = "/";

	private JavaMailUtils _javaMailUtil;
	
	private HtmlParser   _htmlParser;

	private BlogLogInfo  _blogLogInfo;
	
	private String _xPath;
	
	private String _destDir;

	/**
	 * 构造方法.
	 * 
	 * @param blogLogInfo 博客日志信息.
	 * @param destDir 保存目录.
	 * @param xPath 备份日志内容的XPath选择符（整 个日志 或日志的正文）.
	 */
	public MhtGenerator(BlogLogInfo blogLogInfo, String destDir, String xPath) {
		_javaMailUtil = new JavaMailUtils();
		_htmlParser   = new HtmlParser();
		_xPath        = xPath;
		_blogLogInfo  = blogLogInfo;
		_destDir      = destDir;
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
    @Override
	public void run() {
		if (_logger.isInfoEnabled()) {
    		_logger.info("start processing the log");
		}
		
		try {
			NodeList nodeList = _htmlParser.parseHtml(_blogLogInfo.getLink(),
							_xPath, "GBK", "UTF-8");
			
			String blogContent = _htmlParser.dom2Xml(nodeList, "UTF-8");
			
			Visitor<Node> visitor = new NodeVisitor<Node>();
			visitor.addVisitor(new ImageVisitor<Node>(_javaMailUtil));
			visitor.addVisitor(new LinkVisitor<Node>(_javaMailUtil));

			Document document = _htmlParser.xml2Dom(blogContent, "GBK");
			HtmlParser.domIterate(document, visitor);

			_javaMailUtil.setSubject(_blogLogInfo.getTitle());
			_javaMailUtil.setContent(blogContent);
			
			String fileFullPath = _destDir;
			if (! fileFullPath.endsWith(SLASH)) {
				fileFullPath += SLASH;
			}
			fileFullPath += (StringUtils.replaceSpecialChar(_blogLogInfo.getTitle(), "_") + ".mht");
			File destFile = new File(fileFullPath);
			
			if (_logger.isInfoEnabled()) {
	    		_logger.info("start writing data to file:" + destFile.getAbsolutePath());
			}
			
			_javaMailUtil.write(destFile);
		} catch (XPathExpressionException e) {
			_logger.error("", e);
		} catch (IOException e) {
			_logger.error("", e);
		} catch (TransformerException e) {
			_logger.error("", e);
		} catch (Exception e) {
			_logger.error("", e);
		}
		
		if (_logger.isInfoEnabled()) {
    		_logger.info("processed the log over");
		}
	}

}
