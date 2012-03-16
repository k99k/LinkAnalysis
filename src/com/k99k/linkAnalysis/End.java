/**
 * 
 */
package com.k99k.linkAnalysis;

import com.k99k.khunter.Action;

/**
 * 最后一个Action,负责Action初始化后的收尾工作
 * @author keel
 *
 */
public class End extends Action {

	/**
	 * @param name
	 */
	public End(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#init()
	 */
	@Override
	public void init() {
		//插入收尾动作
		
		super.init();
	}
	
	

}
