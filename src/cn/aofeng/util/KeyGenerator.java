/**
 * 建立时间：2008-9-17
 */
package cn.aofeng.util;

import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 唯一key生成器.
 * 
 * @author 聂勇 <a href="mailto:nieyong@asiainfo.com">nieyong@asiainfo.com</a>
 * @version 1.0
 * @since 2008-10-29
 */
public class KeyGenerator {

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(KeyGenerator.class);

    /*
     * 保存数据的key后缀生成范围.
     */
    private int min = 0;
    
    private int max = 99999;
    
    // key的后缀当前值
    private int current = 0;
    
    // key的后缀长度
    private int len = 5 ;
    
    private String hostIpAddress = null;
    
    private static KeyGenerator mg = new KeyGenerator();
    
    private KeyGenerator() {
        this.len = String.valueOf(max).length();
        
        try {
            hostIpAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("", e);
        }
    }
    
    public static KeyGenerator getInstance() {
        return mg;
    }
    
    /**
     * 生成key，采用MD5(当前主机IP地址+主机时间（毫秒）)作为生成key的前缀。
     * 
     * @return key
     * 
     * @see #generate(String)
     */
    public String generate() {
        String prefix = EncryptCodeUtils.md5(hostIpAddress + System.currentTimeMillis());
        
        return generate(prefix);
    }
    
    /**
     * 生成key，采用前缀+顺序号，顺序号长度不够的左边加'0'补全.<br/>
     * 
     * <strong>注意：</strong>如果是在多机环境下，必须要保证你的各主机提供的前缀不一样，否则多主机有可能生成相同的key。
     * 
     * @param prefix key的前缀
     * 
     * @return key
     */
    public String generate(String prefix) {
        String result = prefix + StringUtils.leftPad(
                        String.valueOf(nextNumber()), len, "0");
        
        return result;
    }
    
    private synchronized int nextNumber() {
        current ++;
        
        if (current > max) {
            current = min;
        }
        
        return current;
    }

}
