/**
 * 
 */
package com.warwick.service;

import java.io.InputStream;

import com.warwick.model.TransformerResponse;

/**
 * @author duansubramaniam
 *
 */
public interface TransformerService {
		
	/**
	 * 
	 * @param inputStream
	 * @return
	 */
	public TransformerResponse transformCsv(InputStream inputStream);
}
