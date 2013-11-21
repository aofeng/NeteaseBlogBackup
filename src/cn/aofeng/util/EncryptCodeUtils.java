/**
 * 建立时间：2010-5-10
 */
package cn.aofeng.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

/**
 * 加密，解密，数字签名实用类.
 *
 * @author 聂勇 <a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class EncryptCodeUtils {
    
    /**
     * Logger for this class.
     */
    private final static Logger _logger = Logger.getLogger(EncryptCodeUtils.class);

    private static char[] digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
        'b', 'c', 'd', 'e', 'f'};
    
    /**
     * 将指定的字符串生成MD5摘要。
     * 
     * @param source 待生成MD5摘要的字符串
     * @return MD5摘要字符串。如果当前JRE不支持MD5则返回null。
     */
    public static String md5(String source) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            StringBuilder resultBuff = new StringBuilder(32);
            byte[] md5Bytes = md.digest(source.getBytes());
            
            for (byte b : md5Bytes) {
                resultBuff.append(byte2HEX(b));
            }
            
            result = resultBuff.toString();
        } catch (NoSuchAlgorithmException e) {
            _logger.error("current jdk not support algorithm:MD5", e);
        }
        
        return result;
    }
    
    /**
     * 将一个byte类型的数据转换成十六进制的ASCII表示.
     * 
     * @param ib byte类型的数据.
     * @return 十六进制的ASCII字符串.
     */
    public static String byte2HEX(byte ib) {
        char[] ob = new char[2];
        ob[0] = digit[(ib >>> 4) & 0X0F];
        ob[1] = digit[ib & 0X0F];
        String s = new String(ob);
        
        return s;
    }

    /**
     * 将byte数组转换成十六进制ASCII字符串.
     * 
     * @param src byte类型的数据数组.
     * @return 十六进制的ASCII字符串.
     */
    public static String byte2HEX(byte[] src) {
        StringBuilder resultBuff = new StringBuilder(32);
        
        for (byte b : src) {
            resultBuff.append(byte2HEX(b));
        }
        
        return (resultBuff.toString());
    }

    /**
     * 根据指定的hash类型生成文件内容的hash值.
     * 
     * @param fileObj {@link java.io.File}}实例.
     * @param hashType hash类型:"MD5", "SHA-1", "SHA-256", "SHA-384", "SHA-512".
     * @param 处理进度访问者.
     * 
     * @return 文件内容的hash值.
     */
    public static String hash(File fileObj, String hashType, Visitor<ProgressInfo> visitor) {
    	String hash = null;
    	
    	if (null == fileObj) {
    		return null;
    	}
    	
    	FileInputStream fIn = null;
    	FileChannel fc = null;
    	ByteBuffer buff = ByteBuffer.allocate(8192);
    	try {
    		MessageDigest digest = MessageDigest.getInstance(hashType);
    		
    		ProgressInfo progressInfo = new ProgressInfo();
    		progressInfo.setTotalSize(fileObj.length());
    		fIn = new FileInputStream(fileObj);
    		fc = fIn.getChannel();
    		int readBytes = 0;
    		while ((readBytes = fc.read(buff)) > 0) {
    		    progressInfo.setProcessedSize(progressInfo.getProcessedSize() + readBytes); // 记录已读取数据量
    		    visitor.process(progressInfo);
    			
    			buff.flip();
    			digest.update(buff);
    			buff.clear();
    		}
    		byte[] hashByte = digest.digest();
    		
    		hash = byte2HEX(hashByte);
    	} catch (FileNotFoundException e) {
    	    _logger.error("can not find file:" + fileObj, e);
    	} catch (IOException e) {
    	    _logger.error("throw I/O exception when read file:" + fileObj, e);
    	} catch (NoSuchAlgorithmException e) {
    	    _logger.error("current jdk not support algorithm:" + hashType, e);
    	} finally {
    		IOUtils.close(fc);
    		IOUtils.close(fIn);
    	}
    	
    	return hash;
    }

}
