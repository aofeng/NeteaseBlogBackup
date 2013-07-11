/**
 * 建立时间：：2010-5-14
 */
package cn.aofeng.netease_blog_backup;

import cn.aofeng.util.IOUtils;
import cn.aofeng.util.Proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

/**
 * 博客备份工具辅助类.
 *
 * @author 聂勇，<a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class NeteaseBlogBackupHelp {

    private final static Logger _logger = Logger.getLogger(NeteaseBlogBackupHelp.class);
    
    private final static String PROXY_CONFIGURE_FILE = "proxy.af";
    
    public static boolean saveProxyConfigureInfo(Proxy proxy) {
        boolean saveSuccess = false;
        ObjectOutputStream out = null;
        File proxyConfigureFile = new File(PROXY_CONFIGURE_FILE);

        if (_logger.isDebugEnabled()) {
            _logger.debug("Save proxy configure file:" + proxyConfigureFile.getAbsolutePath());
        }

        try {
            if (!proxyConfigureFile.exists()) {
                proxyConfigureFile.createNewFile();
            }
            out = new ObjectOutputStream(new FileOutputStream(proxyConfigureFile));
            out.writeObject(proxy);

            out.flush();

            saveSuccess = true;
        } catch (IOException e) {
            _logger.error("", e);
        } finally {
            IOUtils.close(out);
        }

        return saveSuccess;
    }

    public static void loadProxyConfigureInfo() {
        File proxyConfigureFile = new File(PROXY_CONFIGURE_FILE);

        if (_logger.isDebugEnabled()) {
            _logger.debug("Load proxy configure file:" + proxyConfigureFile.getAbsolutePath());
        }

        if (proxyConfigureFile.exists()) {
            ObjectInputStream ins = null;
            try {
                ins = new ObjectInputStream(new FileInputStream(proxyConfigureFile));
                
                Proxy proxy = (Proxy) ins.readObject();
                Proxy.getInstance().setProxy(proxy);
            } catch (ClassNotFoundException ex) {
                _logger.error("", ex);
            } catch (FileNotFoundException ex) {
                _logger.error("", ex);
            } catch (IOException ex) {
                _logger.error("", ex);
            } finally {
                IOUtils.close(ins);
            }
        }
    }

}
