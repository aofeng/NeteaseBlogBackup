/**
 * 建立时间：2010-2-26
 */
package cn.aofeng.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 文件操作实用工具类.
 *
 * @author 聂勇，<a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class FileUtils {
	
	public static final Logger _logger = Logger.getLogger(FileUtils.class);
	
	/**
	 * 获取不带后缀的文件名.
	 * 
	 * @param file 文件对象.
	 * @return 返回不带后缀的文件名.
	 */
	public static String getFilenameWithoutSuffix(File file) {
		if (null == file) {
			return "";
		}
		
		String filenameWithoutSuffix = "";
		String filename = file.getName();
		int pointIndex = filename.lastIndexOf(".");
		filenameWithoutSuffix = (-1 == pointIndex ? filename.substring(0) : filename.substring(0 ,pointIndex));
		
		return filenameWithoutSuffix;
	}
	
	/**
	 * 获取文件的后缀.
	 * 
	 * @param file 文件对象.
	 * @return 返回不带后缀的文件名.
	 */
	public static String getSuffix(File file) {
		if (null == file) {
			return "";
		}
		
		String filename = file.getName();
		return getSuffix(filename);
	}
	
	public static String getSuffix(String filename) {
		String suffix = "";
		int pointIndex = filename.lastIndexOf(".");
		suffix = (-1 == pointIndex ? "" : filename.substring(pointIndex+1));
		
		return suffix;
	}
	
	/**
	 * 读取文件的整 个内容. 适用于小文件.
	 * 
	 * @param file 文件对象.
	 * @return 文件的整个内容.
	 */
	public static String readFileContent(InputStream ins) {
		if (null == ins) {
			return null;
		}
		
		StringBuilder buff = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(ins));
			
			while (true) {
				String line = reader.readLine();
				if (null == line) {
					break;
				}
				
				buff.append(line);
			}
		} catch (FileNotFoundException e) {
			_logger.error("file not found", e);
		} catch (IOException e) {
			_logger.error("read file occurs error", e);
		} finally {
			IOUtils.close(reader);
		}
		
		return buff.toString();
	}
	
	/**
	 * 压缩文件.
	 * 
	 * @param srcDir 源目录
	 * @param destFileFullPath 目标文件全路径
	 * @return true - 成功；false - 失败.
	 * 
	 * @see #zip(File, File)
	 */
	public static boolean zip(String srcDir, String destFileFullPath) {
		if (StringUtils.isEmpty(destFileFullPath)
				|| StringUtils.isEmpty(srcDir)) {
			throw new IllegalArgumentException("destFileFullPath and srcDir can not empty");
		}

		return zip(new File(destFileFullPath), new File(srcDir));
	}

	/**
	 * 压缩文件.
	 * 
	 * @param srcFile 源文件（文件或目录）
	 * @param destZipFile 目标文件
	 * @return true - 成功；false - 失败.
	 */
	public static boolean zip(File srcFile, File destZipFile) {
		if (null == srcFile || null == destZipFile) {
			throw new IllegalArgumentException("srcFile and destZipFile can not null");
		}

		boolean result = false;
		
		ZipOutputStream zout = null;
		try {
			zout = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(destZipFile)));

			// JDK的BUG, 防止没有文件时关闭ZipOutputStream报异常导致无法释放ZIP文件句柄
			zout.putNextEntry(new ZipEntry("readme.txt"));
			zout.write("empty".getBytes());

			if (srcFile.isDirectory()) {
				File[] files = srcFile.listFiles();
				if (null == files || files.length < 1) {
					return false;
				}
				for (File fileObj : files) {
					addZipEntry(zout, fileObj);
				}
			} else {
				addZipEntry(zout, srcFile);
			}

			result = true;
		} catch (IOException e) {
			_logger.error("An exception occurred while generating compressed file", e);
		} finally {
			IOUtils.close(zout);
		}

		return result;
	}

	/**
	 * 往压缩包中增加一个压缩单元.
	 * 
	 * @param zout 压缩输出流
	 * @param srcFile 需被加入压缩包的文件对象
	 * @throws IOException 操作压缩单元时抛出异常
	 */
	private static void addZipEntry(ZipOutputStream zout, File srcFile)
	throws IOException {
		ZipEntry entry = new ZipEntry(srcFile.getName());
		zout.putNextEntry(entry);
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(srcFile));
			byte[] b = new byte[1024];
			int readCount = 0;
			while (-1 != (readCount = in.read(b))) {
				zout.write(b, 0, readCount);
			}
		} catch (IOException e) {
			_logger.error("Read data to generate compress file occur exception", e);
		} finally {
			IOUtils.close(in);
			IOUtils.close(zout);
		}
		zout.flush();
	}
	
	/**
	 * 解压缩文件.
	 * 
	 * @param srcZip 待解压的*.zip文件完整路径
	 * @param destPath 解压缩输出的完整目的路径
	 */
	public static void unZip(String srcZip, String destPath) {
		if (null == srcZip || null == destPath) {
			throw new IllegalArgumentException("srcZip and destPath can not null");
		}
		
		unZip(new File(srcZip), new File(destPath));
	}
	
	/**
	 * 解压缩文件.
	 * 
	 * @param srcZipFile 待解压的*.zip文件
	 * @param destPath 解压缩输出的完整目的路径文件对象
	 */
	public static void unZip(File srcZipFile, File destPath) {
		if (null == srcZipFile || null == destPath) {
			throw new IllegalArgumentException("srcZipFile and destPath can not null");
		}
		String destBasePath = destPath.getAbsolutePath();
		
		BufferedInputStream in = null;
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(srcZipFile);
			@SuppressWarnings("unchecked")
			Enumeration entrys = zipFile.entries();
			
			ZipEntry entry = null;
			while (entrys.hasMoreElements()) {
				entry = (ZipEntry) entrys.nextElement();
				in = new BufferedInputStream(zipFile.getInputStream(entry));
				
				String filename = entry.getName();
				if (entry.isDirectory()) { // Directory
					new File(destBasePath, filename).mkdirs();
				} else { // File
					File destFile = new File(destBasePath, filename);
					writeZipEntryToFile(in, destFile);
				}
			}
		} catch (FileNotFoundException e) {
			_logger.error("Can't find src file", e);
		} catch (IOException e) {
			_logger.error("An exception occured while getting a ZipEntry from zip file", e);
		} finally {
			IOUtils.close(zipFile);
			IOUtils.close(in);
		}
	}
	
	/**
	 * 输出一个压缩单元至文件.
	 * 
	 * @param in 文件输入流
	 * @param entryName 压缩单元名称（文件或目录名称）
	 * @param destFile 输出的文件
	 */
	private static void writeZipEntryToFile(InputStream in, File destFile) {
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(
					new FileOutputStream(destFile));
			byte[] b = new byte[1024];
			int readNum = 0;
			while (-1 != (readNum = in.read(b))) {
				out.write(b, 0, readNum);
			}
		} catch (IOException e) {
			_logger.error("And Exception occured while writing ZipEntry data to file:" + destFile.getAbsolutePath(), e);
		} finally {
			IOUtils.close(out);
		}
	}
	
	/**
	 * 将路径中的"\"替换成"/"；如果路径不是以"/"结尾，加上"/".
	 * 
	 * @param basePath 需要处理的路径
	 */
	public static String processPath(String basePath) {
		String temp = basePath.replaceAll("\\/g", "/");
		int point = temp.lastIndexOf('/');
		
		if (-1 == point) {
			temp += "/";
		}
		
		return temp;
	}
	
	/**
	 * 校验源文件与目的文件是否有效.
	 * 
	 * @param srcFile 源文件.
	 * @param destFile　目的文件.
	 * @return　成功返回true，否则抛出异常.
	 * @throws IllegalArgumentException 当源文件与目的文件无效时抛出此异常.
	 */
	private static boolean validSrcAndDest(File srcFile, File destFile) {
		if (null == srcFile || null == destFile) {
			throw new IllegalArgumentException("参数为null");
		}
		
		if (! srcFile.exists()) {
			throw new IllegalArgumentException("找不到源文件:"+srcFile.getAbsolutePath());
		}
		
		return true;
	}
	
	/**
	 * 单个文件编码（字符集）转换，即将单个文件从一种编码转换成另一种编码.
	 * 
	 * @param srcFile 源文件.
	 * @param srcCharset 源文件编码（字符集）.
	 * @param destFile 目标文件.
	 * @param destCharset 目标文件编码（字符集）.
	 */
	private static void singleCharsetTransform(File srcFile, String srcCharset, 
			File destFile, String destCharset) {
		Charset inCharset  = Charset.forName(srcCharset);
		CharsetDecoder inDecoder = inCharset.newDecoder();
		Charset outCharset = Charset.forName(destCharset);
		CharsetEncoder outEncoder = outCharset.newEncoder();
		
		RandomAccessFile inFile  = null;
		RandomAccessFile outFile = null;
		FileChannel inChannel  = null;
		FileChannel outChannel = null;
		
		File realDestFile = (destFile.isDirectory() ? new File(destFile, srcFile.getName()) : destFile);
		try {
			inFile  = new RandomAccessFile(srcFile, "r");
			outFile = new RandomAccessFile(realDestFile, "rw");
			inChannel  = inFile.getChannel();
			outChannel = outFile.getChannel();
			
			ByteBuffer tempBuffer = ByteBuffer.allocate(4096);
			while (-1 != (inChannel.read(tempBuffer))) {
				tempBuffer.flip();
				CharBuffer inCharBuffer  = inDecoder.decode(tempBuffer);
				ByteBuffer outByteBuffer = outEncoder.encode(inCharBuffer);
				outChannel.write(outByteBuffer);
				tempBuffer.clear();
			} 
		} catch (FileNotFoundException e) {
			_logger.error("找不到指定的文件", e);
		} catch (IOException e) {
			_logger.error("文件:"+srcFile.getAbsolutePath(), e);
		} finally {
			IOUtils.close(outChannel);
			IOUtils.close(outFile);
			IOUtils.close(inChannel);
			IOUtils.close(inFile);
		}
	}
	
	/**
	 * 文件编码（字符集）转换，即将文件从一种编码转换成另一种编码.
	 * 
	 * @param srcFullFilePath 源文件完整路径.
	 * @param srcCharset 源文件编码（字符集）.
	 * @param destFullFilePath 目标文件完整路径.
	 * @param destCharset 目标文件编码（字符集）.
	 * @param filenameFilter 文件名过滤器.
	 */
	public static void charsetTransform(String srcFullFilePath, String srcCharset, 
			String destFullFilePath, String destCharset, FilenameFilter filenameFilter) {
		if (StringUtils.isEmpty(srcFullFilePath) 
				|| StringUtils.isEmpty(srcCharset)
				|| StringUtils.isEmpty(destFullFilePath)
				|| StringUtils.isEmpty(destCharset)) {
			throw new IllegalArgumentException("invaild arguments");
		}
		
		File srcFile  = new File(srcFullFilePath);
		File destFile = new File(destFullFilePath);
		
		charsetTransform(srcFile, srcCharset, destFile, destCharset, filenameFilter);
	}

	/**
	 * 文件编码（字符集）转换，即将文件从一种编码转换成另一种编码.
	 * 
	 * @param srcFile 源文件.
	 * @param srcCharset 源文件编码（字符集）.
	 * @param destFile 目标文件.
	 * @param destCharset 目标文件编码（字符集）.
	 * @param filenameFilter 文件名过滤器.
	 */
	public static void charsetTransform(File srcFile, String srcCharset, 
			File destFile, String destCharset, FilenameFilter filenameFilter) {
		validSrcAndDest(srcFile, destFile);
		
		boolean isSrcCharsetValid  = Charset.isSupported(srcCharset);
		boolean isDestCharsetValid = Charset.isSupported(destCharset);
		if (! (isSrcCharsetValid || isDestCharsetValid)) {
			throw new IllegalArgumentException("无效的字符集");
		}
		
		if (!srcFile.exists()) {
			throw new IllegalArgumentException("源文件不存在");
		}
		
		if (destFile.isDirectory() && !destFile.exists()) {
			destFile.mkdirs();
		}
		
		if (srcFile.isDirectory()) {
			File newDestSubDir = new File(destFile, srcFile.getName());
			if (! newDestSubDir.exists()) {
				newDestSubDir.mkdirs();
			}
			
			File[] srcFiles = srcFile.listFiles(filenameFilter);
			
			for (File file : srcFiles) {
				charsetTransform(file, srcCharset, newDestSubDir, destCharset, filenameFilter);
			}
		} else {
			singleCharsetTransform(srcFile, srcCharset, destFile, destCharset);
		}
	}

	/**
	 * 获取Classpath路径下*.properties文件内容并解析成{@link java.util.Properties}}.
	 * 
	 * @param baseClasspathUrl 基于Classpath的*.properties 文件路径，以"/"开头.
	 * @return {@link java.util.Properties}}实例.
	 */
	public static Properties getClasspathProperties(String baseClasspathUrl) {
	    Properties pros = new Properties();
	    InputStream ins = null;
	    try {
	        ins = FileUtils.class.getResourceAsStream(baseClasspathUrl);
            pros.load(ins);
        } catch (IOException e) {
            _logger.error("load data from " + baseClasspathUrl + " occur error", e);
        }
        
	    return pros;
	}
	
	/**
	 * 获取Classpath下文件的完整路径.
	 * 
	 * @param baseClasspathUrl 基于Classpath的文件路径，以"/"开头.
	 * @return 文件的完整路径.
	 */
	public static String getClasspathUrl(String baseClasspathUrl) {
        URL baseClasspath = FileUtils.class.getResource(baseClasspathUrl);
        
        String fullPath = baseClasspath.getPath();
        
        if (null != fullPath ) {
            String[] prefixs = {"file:/", "/"};
            for (String prefix : prefixs) {
                if (fullPath.startsWith(prefix)) {
                    fullPath = fullPath.substring(prefix.length());
                } 
            }
            
            if (-1 != fullPath.indexOf("%")) {
                try {
                    fullPath = URLDecoder.decode(fullPath, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    _logger.error("", e);
                }
            }
        }
        
        return fullPath;
	}

}
