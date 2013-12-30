/**
 * 建立时间：2010-5-5
 */
package cn.aofeng.netease_blog_backup;

import java.io.InputStream;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

/**
 * 自定义实现 {@link javax.mail.internet.MimeMessage} 的updateMessage()和updateHeader()方法.
 * 
 * @author 聂勇 <a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class MimeMessageImpl extends MimeMessage {

    public MimeMessageImpl(Session session) {
        super(session);
    }
    
    public MimeMessageImpl(Session session, InputStream is)
                    throws MessagingException {
        super(session, is);
    }
    
    public MimeMessageImpl(MimeMessage source) throws MessagingException {
        super(source);
    }
    
    protected MimeMessageImpl(Folder folder, int msgnum) {
        super(folder, msgnum);
    }
    
    protected MimeMessageImpl(Folder folder, InputStream is, int msgnum)
                    throws MessagingException {
        super(folder, is, msgnum);
    }
    
    protected MimeMessageImpl(Folder folder, InternetHeaders headers,
                    byte content[], int msgnum) throws MessagingException {
        super(folder, headers, content, msgnum);
    }
    
    private static int uniqueId;
    
    private synchronized int getUniqueId() {
        return uniqueId++;
    }
    
    @Override
    protected void updateMessageID() throws MessagingException {
        String suffix = "aofengblog@163.com";
//        InternetAddress addr = InternetAddress.getLocalAddress(super.session);
//        if (addr != null) {
//            suffix = addr.getAddress();
//        } else {
//            suffix = "aofengblog@163.com";
//        }
        
        StringBuilder buff = new StringBuilder();
        buff.append("<")
            .append(buff.hashCode())
            .append('.')
            .append(getUniqueId())
            .append('.')
            .append(System.currentTimeMillis())
            .append('.')
            .append("AofengTool.")
            .append(suffix)
            .append(">");
        
        setHeader("Message-ID", buff.toString());
    }

    @Override
    protected void updateHeaders() throws MessagingException {
        super.updateHeaders();
        setHeader("X-Mailer", "aofeng-netease-blog-backup");
    }

}
