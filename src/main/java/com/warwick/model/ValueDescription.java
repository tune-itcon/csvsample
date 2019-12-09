/**
 * 
 */
package com.warwick.model;

import java.util.Objects;

/**
 * @author duansubramaniam
 *
 */
public class ValueDescription {
	private Double minValue = null;
	private Double maxValue = null;
	/**
	 * 
	 */
	public ValueDescription() {
	}
	/**
	 * @return the minValue
	 */
	public Double getMinValue() {
		return minValue;
	}
	/**
	 * @param minValue the minValue to set
	 */
	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}
	/**
	 * @return the maxValue
	 */
	public Double getMaxValue() {
		return maxValue;
	}
	/**
	 * @param maxValue the maxValue to set
	 */
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}
	@Override
	public int hashCode() {
		return Objects.hash(maxValue, minValue);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValueDescription other = (ValueDescription) obj;
		return Objects.equals(maxValue, other.maxValue) && Objects.equals(minValue, other.minValue);
	}
	
	

}
