/**
 * 
 */
package com.k99k.linkAnalysis;

import org.apache.log4j.Logger;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.DaoInterface;
import com.k99k.khunter.DaoManager;
import com.k99k.khunter.KObject;
import com.k99k.tools.StringUtil;

/**
 * @author keel
 *
 */
public class LogTask extends Action {

	public LogTask(String name) {
		super(name);
	}
	static final Logger log = Logger.getLogger(LogTask.class);
	
	static DaoInterface dao = DaoManager.findDao("LinkLogDao");

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#act(com.k99k.khunter.ActionMsg)
	 */
	@Override
	public ActionMsg act(ActionMsg msg) {
		String ids = msg.getData("id").toString();
		String ip = msg.getData("ip").toString();
		String ua = msg.getData("ua").toString();
		if (!StringUtil.isDigits(ids)) {
			log.error("LogTask id error:"+ids);
			return super.act(msg);
		}
		//数据库保存
		KObject linkLog = new KObject();
		linkLog.setProp("linkId", Integer.parseInt(ids));
		linkLog.setProp("ip", ip);
		linkLog.setProp("ua", ua);
		if(!dao.add(linkLog)){
			log.error("LogTask save to Mongo error:"+linkLog.toString());
			return super.act(msg);
		}
		//log.info("LogTask:"+ids+"#"+ip+"#"+ua);
		return super.act(msg);
	}
	
	

}
