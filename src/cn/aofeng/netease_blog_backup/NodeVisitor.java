/**
 * 建立时间：2010-5-6
 */
package cn.aofeng.netease_blog_backup;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import cn.aofeng.util.Visitor;

/**
 * 节点访问者.
 *
 * @author 聂勇 <a href="mailto:nieyong@asiainfo.com">nieyong@asiainfo.com</a>
 */
public class NodeVisitor<T extends Node> implements Visitor<T> {

    private List<Visitor<T>> vistors = new ArrayList<Visitor<T>>();
    
    /*
     * @see cn.aofeng.html.Visitor#addVisitor(cn.aofeng.html.Visitor)
     */
    @Override
    public void addVisitor(Visitor<T> visitor) {
        vistors.add(visitor);
    }

    /*
     * @see cn.aofeng.html.Visitor#process(org.w3c.dom.Node)
     */
    @Override
    public void process(T node) {
        for (Visitor<T> visitor : vistors) {
            visitor.process(node);
        }
    }

}
