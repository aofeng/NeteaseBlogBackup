/**
 * 建立时间：2010-5-6
 */
package cn.aofeng.netease_blog_backup;

import org.w3c.dom.Node;

import cn.aofeng.util.Visitor;

/**
 * 图像节点访问者.
 *
 * @author 聂勇 <a href="mailto:nieyong@asiainfo.com">nieyong@asiainfo.com</a>
 */
public class ImageVisitor<T extends Node> extends AbstractUrlVisitor<T> implements Visitor<T> {

    public ImageVisitor(JavaMailUtils javaMailUtil) {
        super(javaMailUtil);
        
        super._nodeName = "img";
        super._attrName = "src";
    }
    
}
