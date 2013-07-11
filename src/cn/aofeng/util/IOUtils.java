/**
 * 建立时间:2008-10-30
 */
package cn.aofeng.util;

import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipFile;

/**
 * IO操作实用类.
 *
 * @author 聂勇 <a href="mailto:nieyongemail@126.com>nieyongemail@126.com</a>
 */
public class IOUtils {

	/**
	 * Logger for this class.
	 */
	public static final Logger _logger = Logger.getLogger(IOUtils.class);

	private IOUtils() {

	}

    /**
     * 关闭实现了{@link  java.io.Closeable}接口的实例。
     *
     * @param closeable 实现了{@link  java.io.Closeable}接口的实例
     */
	public static void close(Closeable closeable) {
		if (null != closeable) {
			try {
				closeable.close();
				closeable = null;
			} catch (IOException e) {
				_logger.warn("close Closeable error", e);
			}
		}
	}

    /**
     * 关闭{@link java.net.Socket}对象。
     *
     * @param socket {@link java.net.Socket}对象
     */
	public static void close(Socket socket) {
		if (null != socket) {
			try {
				socket.close();
				socket = null;
			} catch (IOException e) {
				_logger.warn("close Socket error", e);
			}
		}
	}

	public static void close(ServerSocket serverSocket) {
		if (null != serverSocket) {
			try {
				serverSocket.close();
				serverSocket = null;
			} catch (IOException e) {
				_logger.warn("close ServerSocket error", e);
			}
		}
	}

	/**
	 * 关闭{@link ExecutorService}.
	 *
	 * @param executor 一个实现了{@link ExecutorService}接口的实例
	 * @param shutdownNow 是否不等待任务执行完毕就强制关闭
	 */
	public static void close(ExecutorService executor, boolean shutdownNow) {
		if (null != executor) {
			if (shutdownNow) {
				executor.shutdownNow();
			} else {
				executor.shutdown();
			}
			executor = null;
		}
	}

	/**
	 * 关闭队列：将队列中所有的元素清除。
	 *
	 * @param queue
	 */
	@SuppressWarnings("unchecked")
	public static void close(Queue queue) {
		if (null != queue) {
			while(null != queue.poll()) {

			}
			queue = null;
		}
	}

	public static void close(Selector selector) {
		if (null != selector) {
			try {
				selector.close();
			} catch (IOException e) {
				_logger.warn("close Selector error", e);
			}
			selector = null;
		}
	}

    public static void close(SelectionKey key) {
        if(null != key) {
            key.cancel();
            try {
                key.channel().close();
            } catch (IOException ex) {
            	_logger.warn("", ex);
            }
        }
    }
    

	/**
	 * 关闭JDBC连接.
	 * 
	 * @param conn JDBC连接
	 */
	public static void close(Connection conn) {
		if (null != conn) {
			try {
				conn.close();
			} catch (SQLException e) {
				_logger.error("Close JDBC connection occur exception", e);
			}
		}
	}
	
	/**
	 * 关闭JDBC会话.
	 * 
	 * @param stat JDBC会话
	 */
	public static void close(Statement stat) {
		if (null != stat) {
			try {
				stat.close();
			} catch (SQLException e) {
				_logger.error("Close JDBC statement occur exception", e);
			}
		}
	}
	
	/**
	 * 关闭JDBC结果集.
	 * 
	 * @param rs JDBC结果集
	 */
	public static void close(ResultSet rs) {
		if (null != rs) {
			try {
				rs.close();
			} catch (SQLException e) {
				_logger.error("Close JDBC resultset occur exception", e);
			}
		}
	}
	
	public static void close(ZipFile zipFile) {
		if (null != zipFile) {
			try {
				zipFile.close();
			} catch (IOException e) {
				_logger.error("Close ZipFile occur exception", e);
			}
		}
	}
	
	public static void close(Reader reader) {
		if (null != reader) {
			try {
				reader.close();
			} catch (IOException e) {
				_logger.error("Close Reader occur exception", e);
			}
		}
	}
	
	public static void close(Channel channel) {
		if (null != channel) {
			try {
				channel.close();
			} catch (IOException e) {
				_logger.error("Close Channel occur exception", e);
			}
		}
	}

}
