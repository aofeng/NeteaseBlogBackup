/**
 * 建立时间：2010-5-11
 */
package cn.aofeng.util;

import java.io.IOException;
import java.io.Writer;

/**
 * 空输出器, 即向它写的任何内容都直接抛弃.
 *
 * @author 聂勇 <a href="mailto:nieyong@asiainfo.com">nieyong@asiainfo.com</a>
 */
public class NullWriter extends Writer {

    @Override
    public void close() throws IOException {
        
    }

    @Override
    public void flush() throws IOException {
        
    }

    @Override
    public void write(int c) throws IOException {
        
    }
    
    @Override
    public void write(char cbuf[], int off, int len) throws IOException {
        
    }
    
    @Override
    public void write(String str, int off, int len) throws IOException {
        
    }

}
