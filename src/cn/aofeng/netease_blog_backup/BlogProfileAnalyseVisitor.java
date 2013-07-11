/**
 * 建立时间：2010-8-28
 */
package cn.aofeng.netease_blog_backup;

import org.w3c.dom.Node;

import cn.aofeng.util.Visitor;

/**
 * 博客个人信息分析器.
 *
 * @author 聂勇，<a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class BlogProfileAnalyseVisitor<T extends Node> implements Visitor<T> {

	private String _scriptString;
	
	public String getScriptString() {
		return _scriptString;
	}
	
	@Override
	public void addVisitor(Visitor<T> visitor) {
		throw new IllegalStateException("not yet implements");
		
	}

	@Override
	public void process(T obj) {
		Node node = obj;
		if (Node.TEXT_NODE == node.getNodeType()) {
			_scriptString = node.getNodeValue();
		}
	}

}
