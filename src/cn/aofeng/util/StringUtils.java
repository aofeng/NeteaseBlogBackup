/**
 * 建立时间:2008-11-4
 */
package cn.aofeng.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 字符串操作辅助方法.
 * 
 * @author 聂勇 <a href="mailto:nieyongemail@126.com>nieyongemail@126.com</a>
 */
public class StringUtils {
    
    /**
     * Logger for this class.
     */
    private final static Logger _logger = Logger.getLogger(StringUtils.class);

    private StringUtils() {
        
    }
    
    /**
     * 换行符.
     */
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    
    /**
     * 分隔符.
     */
    private final static String SEPARATOR = "/";
    
    /**
     * 特殊字符正则表达式.
     */
    private static String specialCharRegEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<> /?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
    private final static Pattern specialCharPattern = Pattern.compile(specialCharRegEx);

    /**
     * 将GBK字符串转换成Unicode字符串。
     * 
     * @param src GBK字符串。
     * @return 转换成Unicode之后的字符串。
     */
    public static String gbkToUnicode(String src) {
        int len = src.length();
        
        StringBuilder buffer = new StringBuilder(len * 6);
        for (int index = 0; index < len; index++) {
            String hexB = Integer.toHexString(src.charAt(index));
            buffer.append("\\u");            
            if (hexB.length() <= 2) {
                buffer.append("00"); 
            }            
            buffer.append(hexB);
        }
            
        return buffer.toString();
    }
    
    /**
     * 将Unicode字符串转换成GBK字符串。
     * 
     * @param unicodeSrc Unicode字符串。
     * @return GBK字符串。
     * @throws UnsupportedEncodingException 转换字符编码出错。
     */
    public static String unicodeToGbk(String unicodeSrc) {
        String result = null;
        try {
            result = new String(unicodeSrc.getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
            _logger.error("", e);
        }
        
        return result;
    }
    
    private static Pattern patternUnicodeToString = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
    private static String UnicodeToString(String str) {
        Matcher matcher = patternUnicodeToString.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");   
        }
        
        return str;
    }
    
    /**
     * 将文件中的Unicode字符串的转换成GBK字符串.
     * 
     * @param srcFile 包含有Unicode字符中的源文件.
     * @param inputCharset 源文件保存的编码格式.
     * @param destFile 目的文件.
     * @param outpurCharset 目的文件保存的编码格式.
     */
    public static void unicodeToGbk(File srcFile, String inputCharset, File destFile, String outpurCharset) {
        if (null == srcFile || null == destFile) {
            throw new IllegalArgumentException("src file or dest file is null");
        }
        
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            if (! destFile.exists()) {
                destFile.createNewFile();
            }
            
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(srcFile), inputCharset));
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(destFile), outpurCharset));
            if (null != reader) {
                String line = reader.readLine();
                while (null != line) {
                    String gbkLine = UnicodeToString(line);
                    writer.write(gbkLine);
                    
                    line = reader.readLine();
                }
                writer.flush();
            }
        } catch (IOException e) {
            _logger.error("", e);
        } finally {
            IOUtils.close(writer);
            IOUtils.close(reader);
        }
    }
    
    /**
     * 将字符串转换成UTF8字符串.
     * 
     * @param src 原字符串.
     */
    public static String gbkToUTF8(String src) {
        StringBuilder buff = new StringBuilder(src.length() * 6);
        for (int i = 0; i < src.length(); i++ ) {
            char c = src.charAt(i);
            int intAsc = (int) c;
            if (intAsc > 128) {
                buff.append("&#x").append(Integer.toHexString(intAsc));
            } else {
                buff.append(intAsc);
            }
        }
        
        return buff.toString();
    }

    public static String gbkToUTF16LE(String src) {
        if (org.apache.commons.lang.StringUtils.isEmpty(src)) {
            return "";
        }
        
        StringBuffer result = new StringBuffer(src.length()*4);
        Charset csets = Charset.forName("UTF-16LE");
        ByteBuffer buff = csets.encode(src);
        while(buff.hasRemaining()) {
            result.append(Integer.toHexString(buff.get()));
        }
        
        return result.toString();
    }
    
    /**
     * 获取指定的“基于 ClassPath”的路径的完整路径.
     * 
     * @param baseClassesDir “基于 ClassPath”的路径.
     * @return 完整路径.
     * @throws UnsupportedEncodingException 
     */
    public static String getFullPathBaseClassPath(String baseClassesDir) throws UnsupportedEncodingException {
        URL baseDir = StringUtils.class.getResource(SEPARATOR);
        
        String fullPath = assemblePath(baseDir.getPath(), baseClassesDir);
        
        if (null != fullPath && fullPath.startsWith("file:/")) {
            fullPath = fullPath.substring(6);
        }
        
        return URLDecoder.decode(fullPath, "UTF-8");
    }
    
    /**
     * 组装路径.
     * 
     * @param mainPath 主路径.
     * @param subPath 子路径.
     * @return 主路径 + 子路径.
     * @throws IllegalArgumentException 当mainPath或subPath为空的时候抛出此异常.
     */
    public static String assemblePath(String mainPath, String subPath) {
    	if (org.apache.commons.lang.StringUtils.isBlank(mainPath) 
    			|| org.apache.commons.lang.StringUtils.isBlank(subPath)) {
			throw new IllegalArgumentException("mainPath or subPath is an blank string");
		}
    	
    	String mainPathTemp = mainPath.replaceAll("\\\\", SEPARATOR);
    	String subPathTemp  = subPath.replaceAll("\\\\", SEPARATOR);
    	
    	String delPrefix = subPathTemp.startsWith(SEPARATOR) ? subPathTemp.substring(1) : subPathTemp;
        String delSuffix = delPrefix.endsWith(SEPARATOR) ? delPrefix.substring(0, delPrefix.length()-1) : delPrefix;
    	
    	String fullPath = null;
        if (mainPathTemp.endsWith(SEPARATOR)) {
            fullPath = mainPathTemp + delSuffix;
        } else {
            fullPath = mainPathTemp + SEPARATOR + delSuffix;
        } 
        
        return fullPath;
    }

    /**
     * 将所有特殊字符转换成指定的字符.
     * 
     * @param src 源字符串.
     * @param replacement 替换的字符.
     * @return 转换掉特殊字符后的字符串.
     */
    public static String replaceSpecialChar(String src, String replacement) {
        Matcher matcher = specialCharPattern.matcher(src);
        
        return matcher.replaceAll(replacement).trim();
    }

    /**
     * 产生数字或大写字母或小写字母基础方法.
     * @param baseType 0:仅数字；1:仅小写英文字符；2:仅大写英文字符；
     * @param ls 需要保存数据的链表对象.
     */
    private static void addData2LinkList(int baseType,LinkedList<String> ls) {
        switch (baseType) {
        case 0:
            for (int i = 0; i < 9; i++) {// 1-9
                ls.add(String.valueOf(49 + i));
            }
            break;
        case 1:
            for (int i = 0; i < 26; i++) {// a-z
                if(i==14) continue;//排除小写字母o
                ls.add(String.valueOf(97 + i));
            }
            break;
        case 2:
            for (int i = 0; i < 26; i++) {// A-Z
                if(i==14) continue;//排除大写字母O
                ls.add(String.valueOf(65 + i));
            }
            break;
        default:
            break;
        }
    }
    
    /**
     * 获取随机字符.
     * @param seed 生成随机数的种子.
     * @param type 产生随机字符串类型，值从0到6。
     * 0:仅数字；1:仅小写英文字符；2:仅大写英文字符；3:数字和小写字母；
     * 4:数字和大写字母；5:英文大、小写字母；6:数字和大小写英文字母；
     * @return 指定类型的随机字符.
     */
    private static char getChar(int seed,int type) {
        Random random = new Random(System.currentTimeMillis() + seed);
        char ch = '0';
        LinkedList<String> ls = new LinkedList<String>();
        switch (type) {
        case 0:
            addData2LinkList(0,ls);
            break;
        case 1:
            addData2LinkList(1,ls);
            break;
        case 2:
            addData2LinkList(2,ls);
            break;
        case 3:
            addData2LinkList(0,ls);
            addData2LinkList(1,ls);
            break;
        case 4:
            addData2LinkList(0,ls);
            addData2LinkList(2,ls);
            break;
        case 5:
            addData2LinkList(1,ls);
            addData2LinkList(2,ls);
            break;
        case 6:
            addData2LinkList(0,ls);
            addData2LinkList(1,ls);
            addData2LinkList(2,ls);
            break;
        default:
            break;
        }
        int index = random.nextInt(ls.size());
        if (index > (ls.size() - 1)) {
            index = ls.size() - 1;
        }
        ch = (char) Integer.parseInt(String.valueOf(ls.get(index)));
        return ch;
    }
    
    /**
     * 获取指定长度和类型的随机字符串.
     * @param len 随机串的长度.
     * @param type 随机串类型，值从0到6。
     * 0:仅数字；1:仅小写英文字符；2:仅大写英文字符；3:数字和小写字母；
     * 4:数字和大写字母；5:英文大、小写字母；6:数字和大小写英文字母；
     * @return
     */
    public static String generateRandomString(int len,int type) {
        String content = "";
        for (int i = 0; i < len; i++) {
            content += getChar(i,type);
        }
        return content;
    }

    /**
     * 生成时间戳，格式为：yyyyMMddHHmmss。
     * 
     * @return 时间戳
     */
    public static String createTimestamp() {
        Format formater = new SimpleDateFormat("yyyyMMddHHmmss");
    
        return formater.format(new Date());
    }

}
