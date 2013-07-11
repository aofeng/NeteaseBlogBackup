/**
 * 建立时间：2010-5-14
 */
package cn.aofeng.util.gui;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 图形界面实用类.
 *
 * @author 聂勇，<a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class GuiUtils {
	
	private GuiUtils() {
		
	}

    private final static Logger _logger = Logger.getLogger(GuiUtils.class);
	
	private static ResourceBundle _resourceBundle = ResourceBundle.getBundle("GuiUtilsResource", Locale.getDefault());
	
	/**
	 * 皮肤风格.
	 *
	 * @author 聂勇，<a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
	 */
	public enum SKIN {
		WindowsClassic,
		Metal,
		Motif,
		PlasticXP,
		Plastic3D,
		Plastic
	}
	
	private final static Map<SKIN, String> SKIN_MAP;
	
	/**
	 * 应用图标.
	 */
	public final static Image _applicationIcon = new ImageIcon(GuiUtils.class.getResource("/icon/aofeng_32.jpg")).getImage();

	/**
	 * 作者图标.
	 */
	public final static Icon _authorIcon = new ImageIcon(GuiUtils.class.getResource("/icon/aofeng_large.jpg"));
	
	/**
	 * 复制图标.
	 */
	public final static Icon _copyIcon = new ImageIcon(GuiUtils.class.getResource("/icon/copy.gif"));
	
	/**
	 * 粘贴图标.
	 */
	public final static Icon _pasteIcon = new ImageIcon(GuiUtils.class.getResource("/icon/paste.gif"));
	
	/**
	 * 清除图标.
	 */
	public final static Icon _clearIcon = new ImageIcon(GuiUtils.class.getResource("/icon/clear.gif"));
	
	/**
	 * 生成右键菜单.
	 */
	public static JPopupMenu createHashPopupMenu(final JTextField jTextField) {
	    JPopupMenu menu = new JPopupMenu();
	    JMenuItem clearItem = new JMenuItem();
	    clearItem.setIcon(_clearIcon);
	    clearItem.setText(_resourceBundle.getString("menu.clear"));
	    clearItem.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            jTextField.setText("");
	        }
	    });
	    menu.add(clearItem);
	
	    JMenuItem copyItem = new JMenuItem();
	    copyItem.setIcon(_copyIcon);
	    copyItem.setText(_resourceBundle.getString("menu.copy"));
	    copyItem.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            String hashText = jTextField.getText();
	            StringSelection content = new StringSelection(hashText);
	            Clipboard cliploard = Toolkit.getDefaultToolkit()
	                    .getSystemClipboard();
	            cliploard.setContents(content, null);
	        }
	    });
	    menu.add(copyItem);
	
	    JMenuItem pasteItem = new JMenuItem();
	    pasteItem.setIcon(_pasteIcon);
	    pasteItem.setText(_resourceBundle.getString("menu.paste"));
	    pasteItem.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            DataFlavor flavor = DataFlavor.stringFlavor;
	
	            Clipboard cliploard = Toolkit.getDefaultToolkit()
	                    .getSystemClipboard();
	            if (cliploard.isDataFlavorAvailable(flavor)) {
	                String content = null;
	                try {
	                    content = (String) cliploard.getData(flavor);
	                } catch (IOException ex) {
	
	                } catch (UnsupportedFlavorException ex) {
	
	                }
	
	                if (! StringUtils.isEmpty(content)) {
	                    jTextField.setText(content);
	                }
	            }
	        }
	    });
	    menu.add(pasteItem);
	
	    return menu;
	}

	/**
	 * 获取关于作者信息面板组件.
	 * 
	 * @param parent 父组件.
	 * @return 关于作者信息面板组件.
	 */
	public static JEditorPane aboutAuthorPanel(Component parent) {
		StringBuilder aboutInfo = new StringBuilder()
				.append("<html>")
				.append(_resourceBundle.getString("about.author"))
				.append(_resourceBundle.getString("about.authorName"))
				.append("<br>")
				.append(_resourceBundle.getString("about.email"))
				.append("aofengblog@163.com")
				.append("<br>")
				.append(_resourceBundle.getString("about.blog"))
				.append("<a href=\"http://aofengblog.blog.163.com\">http://aofengblog.blog.163.com</a>")
				.append("</html>");

		final JEditorPane messagePane = new JEditorPane();
		messagePane.setEditable(false);
		messagePane.setContentType("text/html");
		messagePane.setText(aboutInfo.toString());
		messagePane.setBackground(parent.getBackground());
		messagePane.addHyperlinkListener(new HyperlinkListener() {
			@Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (e instanceof HyperlinkEvent) {
                        if (Desktop.isDesktopSupported()) {
                            try {
                                URI uri = e.getURL().toURI();
                                Desktop dp = Desktop.getDesktop();
                                //判断系统桌面是否支持要执行的功能
                                if (dp.isSupported(Desktop.Action.BROWSE)) {
                                    //获取系统默认浏览器打开链接
                                    dp.browse(uri);
                                }
                            } catch (URISyntaxException ex) {
                                _logger.error("", ex);
                            } catch (IOException ex) {
                                _logger.error("Open system default browser occurs error", ex);
                            }
                        }
                    } else if (e instanceof HTMLFrameHyperlinkEvent) {
                        HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                        HTMLDocument doc = (HTMLDocument) messagePane.getDocument();
                        doc.processHTMLFrameHyperlinkEvent(evt);
                    }
                }
            }
		});
		
		return messagePane;
	}
	
	/**
	 * 关于作者对话框.
	 * 
	 * @param parent 对话框的父组件.
	 */
    public static void aboutAuthorDialog(Component parent) {
    	JEditorPane messagePane = aboutAuthorPanel(parent);
        JOptionPane.showMessageDialog(parent, messagePane,
                _resourceBundle.getString("jOptionPane.about.title"),
                JOptionPane.INFORMATION_MESSAGE,
                _authorIcon);
    }
    
    private static void useThemeStyle(SKIN themeStyle) {
    	try {
			UIManager.setLookAndFeel(SKIN_MAP.get(themeStyle));
		} catch (ClassNotFoundException e) {
			_logger.error("", e);
		} catch (InstantiationException e) {
			_logger.error("", e);
		} catch (IllegalAccessException e) {
			_logger.error("", e);
		} catch (UnsupportedLookAndFeelException e) {
			_logger.error("", e);
		}
    }
    
    /**
     * 使用Windows主题样式.
     */
    public static void useWindowsThemeStyle() {
    	useThemeStyle(SKIN.WindowsClassic);
    }
    
    /**
     * 使用Metal主题样式.
     */
    public static void useMetalThemeStyle() {
    	useThemeStyle(SKIN.Metal);
    }
    
    /**
     * 使用Motif主题样式.
     */
    public static void useMotifThemeStyle() {
    	useThemeStyle(SKIN.Motif);
    }
    
    /**
     * 使用Plastic主题样式.
     */
    public static void usePlasticThemeStyle() {
    	useThemeStyle(SKIN.Plastic);
    }
    
    /**
     * 使用Plastic3D主题样式.
     */
    public static void usePlastic3DThemeStyle() {
    	useThemeStyle(SKIN.Plastic3D);
    }
    
    /**
     * 使用PlasticXP主题样式.
     */
    public static void usePlasticXPThemeStyle() {
    	useThemeStyle(SKIN.PlasticXP);
    }

   /**
    * 动态更换GUI主题样式.
    * 
    * @param frame 应用窗口.
    * @param skinType 主题样式, 参考{@link SKIN}}.
    */
    public static void changeGuiThemeStyle(JFrame frame, SKIN themeStyle) {
    	try {
			UIManager.setLookAndFeel(SKIN_MAP.get(themeStyle));
			SwingUtilities.updateComponentTreeUI(frame);
		} catch (ClassNotFoundException e) {
			_logger.error("", e);
		} catch (InstantiationException e) {
			_logger.error("", e);
		} catch (IllegalAccessException e) {
			_logger.error("", e);
		} catch (UnsupportedLookAndFeelException e) {
			_logger.error("", e);
		}
    }

    static {
    	SKIN_MAP = new HashMap<SKIN, String>();
    	SKIN_MAP.put(SKIN.WindowsClassic, "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    	SKIN_MAP.put(SKIN.Metal,     "javax.swing.plaf.metal.MetalLookAndFeel");
    	SKIN_MAP.put(SKIN.Motif,     "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    	SKIN_MAP.put(SKIN.Plastic,   "com.jgoodies.looks.plastic.PlasticLookAndFeel");
    	SKIN_MAP.put(SKIN.Plastic3D, "com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
    	SKIN_MAP.put(SKIN.PlasticXP, "com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
    }

}
