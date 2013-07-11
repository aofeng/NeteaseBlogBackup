/**
 * 建立时间:2009-5-9
 */
package cn.aofeng.util;

/**
 * 压缩与压缩（由于JDK的原因，不支持中文）.
 * 
 * @author 聂勇 <a href="mailto:nieyongemail@126.com>nieyongemail@126.com</a>
 */
public class Zip {
	
	/**
	 * @param args 参数列表:
	 * <ul>
	 * 	<li>参数1：zip-压缩文件，unzip-解压缩；</li>
	 * 	<li>参数2：源文件完整路径</li>
	 * 	<li>参数3：输出文件的完整路径</li>
	 * </ul>
	 */
	public static void main(String[] args) {
		if (null == args || 3 != args.length) {
			throw new IllegalArgumentException("参数错误！\r\n正确的参数格式：\r\n参数1：zip-压缩文件，unzip-解压缩；\r\n参数2：源文件完整路径；\r\n参数3：输出文件的完整路径");
		}
		
		if ("zip".equals(args[0])) {
			FileUtils.zip(args[1], args[2]);
		} else if ("unzip".equals(args[0])) {
			FileUtils.unZip(args[1], args[2]);
		}
	}

}
