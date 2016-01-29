/* 
 * Author: Rana
 * email id: ranakrishnapaul@gmail.com
 * skype id: rana.krishna.paul
 * file name: CommonUtils.java
 * This class is used for validation of 'Item Cost'
 *
 *  */

package com.sg.ranaz.trackingitemsdemoapp;

import java.util.regex.Pattern;


public class CommonUtils {

	public static final Pattern QUANTITIES_PATTERN = Pattern.compile(
	          "[0-9]{1,10}" 
	          
	      );

}