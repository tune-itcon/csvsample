/**
 * 
 */
package com.warwick.model;

import static com.warwick.common.CommonConstants.UNDEFINED_STATUS;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author duansubramaniam
 *
 */
public class TransformerResponse {
	private short status = UNDEFINED_STATUS;
	private String message;
	
	private List<String> columnNames;
	private List<Map<String,String>> rows;
	/**
	 * 
	 */
	public TransformerResponse() {
		

	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(columnNames, message, rows, status);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransformerResponse other = (TransformerResponse) obj;
		return Objects.equals(columnNames, other.columnNames) && Objects.equals(message, other.message)
				&& Objects.equals(rows, other.rows) && status == other.status;
	}


	/**
	 * @return the status
	 */
	public short getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(short status) {
		this.status = status;
	}


	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}


	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}


	/**
	 * @return the columnNames
	 */
	public List<String> getColumnNames() {
		return columnNames;
	}


	/**
	 * @param columnNames the columnNames to set
	 */
	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}


	/**
	 * @return the rows
	 */
	public List<Map<String, String>> getRows() {
		return rows;
	}


	/**
	 * @param rows the rows to set
	 */
	public void setRows(List<Map<String, String>> rows) {
		this.rows = rows;
	}


	
	
}
