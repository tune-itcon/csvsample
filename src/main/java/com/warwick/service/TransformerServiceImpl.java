/**
 * 
 */
package com.warwick.service;

import static com.warwick.common.CommonConstants.DECISION_0_COLUMN_VALUE;
import static com.warwick.common.CommonConstants.DECISION_1_COLUMN_VALUE;
import static com.warwick.common.CommonConstants.DECISION_COLUMN_KEY;
import static com.warwick.common.CommonConstants.ERROR_STATUS;
import static com.warwick.common.CommonConstants.ID_COLUMN_KEY;
import static com.warwick.common.CommonConstants.SUCCESS_MESSAGE;
import static com.warwick.common.CommonConstants.SUCCESS_STATUS;
import static com.warwick.common.CommonConstants.UNSUCCESSFUL_MESSAGE;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.warwick.model.TransformerResponse;
import com.warwick.model.ValueDescription;

/**
 * @author duansubramaniam
 *
 */

@Service
public class TransformerServiceImpl implements TransformerService {
	private static final Logger log = LoggerFactory.getLogger(TransformerServiceImpl.class);
	
	/**
	 * 
	 * @param inputStream
	 * @return
	 */
	public TransformerResponse transformCsv(InputStream inputStream) {
		TransformerResponse resp = new TransformerResponse();
		InputStreamReader inputStreamReader = null;
		CSVParser parser = null;
		try {
			inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			parser = new CSVParser(inputStreamReader, CSVFormat.DEFAULT.withHeader());
			 
		     List<String> columns = parser.getHeaderNames();
		     resp.setColumnNames(columns);
			 Map<String, ValueDescription> varColDescMap = getVariableColumnValueDescription(columns);
			 
			 List<CSVRecord> records = parser.getRecords();
			 
			 determinVariableColumnMinMax(records,varColDescMap);
			 		 
			 List<Map<String,String>> responseRows = validateAndTransformResponse(records,varColDescMap);
			 
			 resp.setRows(responseRows);
			 
			resp.setStatus(SUCCESS_STATUS);
			resp.setMessage(SUCCESS_MESSAGE);
			
			
		}catch (Exception e) {
			resp.setStatus(ERROR_STATUS);
			resp.setMessage(UNSUCCESSFUL_MESSAGE);
		}finally {
			try {
				if(inputStreamReader != null) {
					inputStreamReader.close();
				}
			}catch (Exception e) {
				log.error("Error during inputStreamReader.close... : ",e);
			}
			try {
				if(parser != null) {
					parser.close();
				}
			}catch (Exception e) {
				log.error("Error during parser.close... : ",e);
			}
		}
		
		return resp;
	}
	
	boolean validateRange(Map<String, ValueDescription> varColDescMap, CSVRecord record) {
		boolean isValid = false;
		for(String column: varColDescMap.keySet()) {
			Double itemValue = Double.valueOf(record.get(column));
			ValueDescription valDesc = varColDescMap.get(column);
			if(valDesc.getMinValue() != null && valDesc.getMaxValue() != null
					&& itemValue>=valDesc.getMinValue() && itemValue<=valDesc.getMaxValue()) {
				isValid = true;
				break;
			}
		}
		
		return isValid;
	}
	
	Map<String, ValueDescription> getVariableColumnValueDescription(List<String> columns){
		Map<String, ValueDescription> varColDescMap = columns
		        .stream()
		        .filter(column -> {return !column.equalsIgnoreCase(DECISION_COLUMN_KEY) && !column.equalsIgnoreCase(ID_COLUMN_KEY);})
		        .collect
		            (Collectors.toMap(column -> column, column -> new ValueDescription()));

		return varColDescMap;
	}
	
	void determinVariableColumnMinMax(List<CSVRecord> records, Map<String, ValueDescription> varColDescMap) {
		records.stream()
		 .filter(record -> {return DECISION_1_COLUMN_VALUE.equals(record.get(DECISION_COLUMN_KEY));})
		 .forEach(record -> { varColDescMap.keySet().stream()
			 					.forEach(column -> { 
			 						Double itemValue = Double.valueOf(record.get(column));
			 						ValueDescription valDesc = varColDescMap.get(column);
			 					    valDesc.setMinValue(valDesc.getMinValue() != null ? Double.min(itemValue,valDesc.getMinValue()) : itemValue);
			 					    valDesc.setMaxValue(valDesc.getMaxValue() != null ? Double.max(itemValue,valDesc.getMaxValue()) : itemValue);
			 					});
		 });
	}
	
	List<Map<String,String>> validateAndTransformResponse(List<CSVRecord> records, Map<String, ValueDescription> varColDescMap){
		List<Map<String,String>> responseRows = records.stream()
					.filter(record -> {return DECISION_1_COLUMN_VALUE.equals(record.get(DECISION_COLUMN_KEY))  
							|| ( DECISION_0_COLUMN_VALUE.equals(record.get(DECISION_COLUMN_KEY)) &&
							validateRange(varColDescMap,record));	
					})
					.map(record -> {return record.toMap();})
					.collect
	            (Collectors.toList());
		return responseRows;
	}

}
