/**
 * 
 */
package com.k99k.linkAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.HttpActionMsg;
import com.k99k.khunter.JOut;
import com.k99k.khunter.KFilter;
import com.k99k.khunter.TaskManager;
import com.k99k.khunter.WebTool;
import com.k99k.tools.IO;
import com.k99k.tools.JSON;
import com.k99k.tools.StringUtil;
import com.k99k.tools.encrypter.Encrypter;

/**
 * @author keel
 *
 */
public class LinkTo extends Action {

	/**
	 * @param name
	 */
	public LinkTo(String name) {
		super(name);
	}
	
	/**
	 * 保存所有跳转路径的数组
	 */
	private String[] realPathArr;
	
	
	/**
	 * 缓存所有的Link对象
	 */
	private HashMap<String,Object>[] LinkArr;
	
	/**
	 * cookie保持时间
	 */
	private int cookieTime = 60*60*24*30*5;
	
	/**
	 * 配置文件
	 */
	private String linksIni;
	
	/**
	 * Links数组的初始化大小，默认为100
	 */
	private int linksSize = 100;
	
	static final Logger log = Logger.getLogger(LinkTo.class);

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#act(com.k99k.khunter.ActionMsg)
	 */
	public ActionMsg act(ActionMsg msg) {
		HttpActionMsg httpmsg = (HttpActionMsg)msg;
		HttpServletRequest req = httpmsg.getHttpReq();
		HttpServletResponse resp = httpmsg.getHttpResp();
		String to = KFilter.actPath(msg, 2, "");
		if (!StringUtil.isDigits(to)) {
			JOut.err(403,"E403"+Err.ERR_LINKTO_PARA, httpmsg);
			return super.act(msg);
		}
		int id = Integer.parseInt(to);
		if (id<0 || id >= this.linksIni.length()) {
			JOut.err(403,"E403"+Err.ERR_LINKTO_PARA, httpmsg);
			return super.act(msg);
		}
		
		//捕获相关信息,ip,ua,cookie
		String ip = req.getRemoteAddr();
		String ua = req.getHeader("User-Agent");
		String cookie = WebTool.getCookieValue(req.getCookies(), "egto", "");
		//植入cookie
		if (cookie.equals("")) {
			String co = Encrypter.encrypt("eg#"+id+"#"+System.currentTimeMillis());
			WebTool.setCookie("egto", co, this.cookieTime, resp);
		}
		//记录
		ActionMsg atask = new ActionMsg("logTask");
		atask.addData(TaskManager.TASK_TYPE, TaskManager.TASK_TYPE_EXE_POOL);
		atask.addData("id", id);
		atask.addData("ua", ua);
		atask.addData("ip", ip);
		TaskManager.makeNewTask("logTask_"+id, atask);
		
		//使用数字定位跳转地址数组
		String go = this.realPathArr[id];
		msg.removeData("[print]");
		msg.removeData("[jsp]");
		msg.addData("[goto]", go);
		return super.act(msg);
	}

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#init()
	 */
	@SuppressWarnings("unchecked")
	public void init() {
		//缓存所有跳转地址到数组
		this.realPathArr = new String[this.linksSize];
		this.LinkArr = new HashMap[this.linksSize];
		
		try {
			String inif = IO.readTxt(this.linksIni, "utf-8");
			HashMap<String,Object> root = (HashMap<String, Object>) JSON.read(inif);
			ArrayList<HashMap<String,Object>> lks = (ArrayList<HashMap<String, Object>>) root.get("links");
			if (lks == null) {
				log.error("LinkTo read INI error:"+inif);
				return;
			}
			for (Iterator<HashMap<String, Object>> it = lks.iterator(); it.hasNext();) {
				HashMap<String, Object> m = it.next();
				int id = Integer.parseInt(String.valueOf(m.get("id")));
				this.realPathArr[id] = m.get("url").toString();
				this.LinkArr[id] = m;
			}
			
		} catch (Exception e) {
			log.error("LinkTo read INI error!", e);
		}
		
		super.init();
	}

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#exit()
	 */
	public void exit() {
		//清空缓存
		this.realPathArr = null;
		this.LinkArr = null;
		super.exit();
	}

	/**
	 * @return the linksIni
	 */
	public final String getLinksIni() {
		return linksIni;
	}

	/**
	 * @param linksIni the linksIni to set
	 */
	public final void setLinksIni(String linksIni) {
		this.linksIni = linksIni;
	}

	/**
	 * @return the linksSize
	 */
	public final int getLinksSize() {
		return linksSize;
	}

	/**
	 * @param linksSize the linksSize to set
	 */
	public final void setLinksSize(int linksSize) {
		this.linksSize = linksSize;
	}

	/**
	 * @return the cookieTime
	 */
	public final int getCookieTime() {
		return cookieTime;
	}

	/**
	 * @param cookieTime the cookieTime to set
	 */
	public final void setCookieTime(int cookieTime) {
		this.cookieTime = cookieTime;
	}
	
	

}
