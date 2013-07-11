/**
 * 建立时间：2010-5-4
 */
package cn.aofeng.netease_blog_backup;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import cn.aofeng.util.IOUtils;
import cn.aofeng.util.Visitor;
import cn.aofeng.util.http.HtmlParser;

/**
 * JavaMail操作实用类.
 *
 * @author 聂勇，<a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class JavaMailUtils {
    
    private static final Logger _logger = Logger.getLogger(JavaMailUtils.class);
	
    private boolean _debug = false;
    
    private Session _session;
    
	/**
	 * 编码类型.
	 */
	private String _charset = "UTF-8";
	
	/**
	 * 标题.
	 */
	private String _subject;
	
	/**
	 * 正文内容.
	 */
	private Object _content;
	
	/**
	 * 正文内容的MIME类型.
	 */
	private String _contentType = "text/html; charset=UTF-8";
	
	/**
	 * 来源.
	 */
	private String _from = "Saved_By_HTML_To_MHT_Of_Aofeng_Tool";
	
	/**
	 * 收件地址清单.
	 */
	private List<Address> _toAddressList = new ArrayList<Address>();
	
	/**
     * 抄送地址清单.
     */
    private List<Address> _ccAddressList = new ArrayList<Address>();
    
    /**
     * 密送地址清单.
     */
    private List<Address> _bccAddressList = new ArrayList<Address>();
	
	/**
	 * 发送时间.
	 */
	private Date _sentDate = new Date();
	
	/**
	 * 附件清单.
	 */
	private Set<DataSource> _attachmentList = new HashSet<DataSource>();

	/**
	 * 增加收件地址.
	 * 
	 * @param toAddress 收件地址.
	 * @return 增加收件地址是否成功.
	 */
	public boolean addToAddress(Address toAddress) {
	    return _toAddressList.add(toAddress);
	}
	
	/**
     * 增加抄送地址.
     * 
     * @param toAddress 抄送地址.
     * @return 增加抄送地址是否成功.
     */
    public boolean addCcAddress(Address ccAddress) {
        return _ccAddressList.add(ccAddress);
    }
    
    /**
     * 增加密送地址.
     * 
     * @param toAddress 密送地址.
     * @return 增加密送地址是否成功.
     */
    public boolean addBccAddress(Address bccAddress) {
        return _bccAddressList.add(bccAddress);
    }
	
	/**
	 * 增加附件.
	 * 
	 * @param attachmentFile 附件的文件实例.
	 * @return 增加附件是否成功.
	 */
	public boolean addAttachment(File attachmentFile) {
	    return _attachmentList.add(new FileDataSource(attachmentFile));
	}
	
	/**
     * 增加附件.
     * 
     * @param attachmentURL 附件的URL.
     * @return 增加附件是否成功.
     */
    public boolean addAttachment(URL attachmentURL) {
        return _attachmentList.add(new URLDataSource(attachmentURL));
    }
    
    /**
     * 增加附件.
     * 
     * @param dataSource 附件.
     * @return 增加附件是否成功.
     */
    public boolean addAttachment(DataSource dataSource) {
        return _attachmentList.add(dataSource);
    }
	
	/**
	 * 增加（收件 / 抄送 / 密送）地址.
	 * 
	 * @param message 信封及内容.
	 * @param recipientType 接收类型：收件 / 抄送 / 密送.
	 * @param addressList 接收地址清单.
	 * @throws MessagingException 加入信封的内容不符合RFC822规范.
	 */
	private void addRecipients(Message message, RecipientType recipientType, 
	                List<Address> addressList) throws MessagingException {
	    if (null != addressList && !addressList.isEmpty()) {
            Address[] temp = new InternetAddress[addressList.size()];
            addressList.toArray(temp);
            
            message.setRecipients(recipientType, temp);
        }
	}
	
	private void parseContent(String inputCharset) {
	    HtmlParser htmlParser = new HtmlParser();
	    Document document = htmlParser.xml2Dom(getContent().toString(), inputCharset);
	    
	    Visitor<Node> visitor = new NodeVisitor<Node>();
	    visitor.addVisitor(new ImageVisitor<Node>(this));
	    visitor.addVisitor(new LinkVisitor<Node>(this));
	    HtmlParser.domIterate(document, visitor);
	}
	
	/**
	 * 封装信封及内容.
	 * 
	 * @return 信封及内容.
	 * @throws MessagingException 加入信封的内容不符合RFC822规范.
	 * @throws UnsupportedEncodingException 编码类型不支持.
	 */
	private Message assembleMessage(Properties pros) throws MessagingException, UnsupportedEncodingException {
	    parseContent("GBK");
	    
	    MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(getContent(), getContentType());
        
        // multipart/mixed、multipart/related、multipart/alternative, default is "mixed"
        Multipart multipart = new MimeMultipart("related");
        multipart.addBodyPart(mimeBodyPart);
        
        _session = Session.getDefaultInstance(pros);
        _session.setDebug(_debug);
        MimeMessage message = new MimeMessageImpl(_session);
        
        message.setSubject(getSubject(), getCharset());
        message.setFrom(new InternetAddress(getFrom()));
        
        if (_logger.isDebugEnabled()) {
			_logger.debug("attachment size="+_attachmentList.size());
			_logger.debug(_attachmentList.toString());
		}
        
        if (null != _attachmentList && !_attachmentList.isEmpty()) {
            for (DataSource attachment : _attachmentList) {
                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                attachmentBodyPart.setDisposition(Part.ATTACHMENT);
                attachmentBodyPart.setDataHandler(new DataHandler(attachment));
                attachmentBodyPart.setFileName(MimeUtility.encodeText(attachment.getName(), getCharset(), "B"));
                if (attachment instanceof URLDataSource) {
                    attachmentBodyPart.setHeader("Content-Location", 
                                    ((URLDataSource) attachment).getURL().toString());
                } else if (attachment instanceof FileDataSource) {
                    attachmentBodyPart.setHeader("Content-Location", 
                                    ((FileDataSource) attachment).getFile().getPath());
                } else if (attachment instanceof ByteArrayDataSourceImpl) {
                    attachmentBodyPart.setHeader("Content-Location", 
                                    ((ByteArrayDataSourceImpl) attachment).getUrl());
                }
                
                multipart.addBodyPart(attachmentBodyPart);
            }
        }
        message.setContent(multipart);
        
        addRecipients(message, RecipientType.TO,  _toAddressList);
        addRecipients(message, RecipientType.CC,  _ccAddressList);
        addRecipients(message, RecipientType.BCC, _bccAddressList);
        
        message.setSentDate(null == getSentDate() ? new Date() : getSentDate());
        message.saveChanges();
        
        return message;
	}
	
	/**
	 * 发送邮件.
	 * 
	 * @param pros 邮件服务器的连接信息，负含如下内容：
	 * <ul>
	 * <li>mail.smpt.host : SMTP服务器连接地址。</li>
	 * <li>mail.smpt.port : SMTP服务器连接端口，默认端口25。</li>
	 * <li>mail.smtp.user : 连接SMTP服务器鉴权使用的用户名。</li>
	 * <li>mail.smtp.password : 连接SMTP服务器鉴权使用的密码。</li>
	 * <li>mail.smtp.auth : 连接SMTP服务器是否鉴权，可选值(true / false)，默认false。</li>
	 * <li>mail.transport.protocol : 连接协议，默认smtp。</li>
	 * </ul>
	 * 
	 * @return 发送邮件至邮件服务器是否成功，成功返回true，失败返回false.
	 */
	public boolean send(Properties pros) {
	    if (null == _toAddressList || _toAddressList.isEmpty()) {
            throw new IllegalArgumentException("not set to address");
        }
	    
	    Transport transport = null;
	    try {
	        Message message = assembleMessage(pros);
	        
	        transport = _session.getTransport("smtp");
	        transport.connect(
	                        pros.getProperty("mail.smtp.host"),
	                        Integer.parseInt(pros.getProperty("mail.smtp.port", "25")),
	                        pros.getProperty("mail.smtp.user"), 
	                        pros.getProperty("mail.smtp.password"));
	        transport.sendMessage(message, message.getAllRecipients());
            
            return true;
        } catch (UnsupportedEncodingException e) {
            _logger.error("", e);
            
            return false;
        } catch (MessagingException e) {
            _logger.error("", e);
            
            return false;
        } finally {
            if (null != transport) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    _logger.error("", e);
                }
            }
        }
	}
	
	/**
	 * 将邮件信息输出至文件.
	 * 
	 * @param destFile 输出邮件的目的文件实例.
	 * 
	 * @return 输出是否成功，成功返回true，失败返回false.
	 */
	public boolean write(File destFile) {
		if (null == destFile) {
			throw new IllegalArgumentException("dest file is null");
		}
		
		BufferedOutputStream out = null;
		try {
			if (! destFile.exists()) {
				destFile.createNewFile();
			}
			
			out = new BufferedOutputStream(new FileOutputStream(destFile));
			
			Message message = assembleMessage(System.getProperties());
			message.writeTo(out);
			
			return true;
		} catch (MessagingException e) {
			_logger.error("", e);
			
			return false;
		} catch (IOException e) {
			_logger.error("", e);
			
			return false;
		} finally {
			IOUtils.close(out);
		}
	}
	
	public void setDebug(boolean debug) {
	    _debug = debug;
	}
	
	/**
	 * @return 编码类型。如：GBK，UTF-8.
	 */
	public String getCharset() {
		return _charset;
	}
	
	/**
	 * @param charset 编码类型值。如：GBK，UTF-8.
	 */
	public void setCharset(String charset) {
		_charset = charset;
	}
	
    /**
     * @return 标题.
     */
    public String getSubject() {
        return _subject;
    }
    
    /**
     * @param subject 标题.
     */
    public void setSubject(String subject) {
        _subject = subject;
    }
    
    /**
     * @return 正文内容.
     */
    public Object getContent() {
        return _content;
    }
    
    /**
     * @param content 正文内容.
     */
    public void setContent(Object content) {
        _content = content;
    }
    
    
    
    /**
     * @return 正文内容的MIME类型.
     */
    public String getContentType() {
        return _contentType;
    }

    /**
     * @param contentType 正文内容的MIME类型.
     */
    public void setContentType(String contentType) {
        _contentType = contentType;
    }

    /**
     * @return 来源.
     */
    public String getFrom() {
        return _from;
    }
    
    /**
     * @param from 来源.
     */
    public void setFrom(String from) {
        _from = from;
    }
    
    /**
     * @return 发送日期.
     */
    public Date getSentDate() {
        return _sentDate;
    }
    
    /**
     * @param sentDate 发送日期.
     */
    public void setSentDate(Date sentDate) {
        _sentDate = sentDate;
    }

}
