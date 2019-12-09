package com.warwick;

import static com.warwick.common.CommonConstants.DECISION_0_COLUMN_VALUE;
import static com.warwick.common.CommonConstants.DECISION_1_COLUMN_VALUE;
import static com.warwick.common.CommonConstants.DECISION_COLUMN_KEY;
import static com.warwick.common.CommonConstants.ERROR_STATUS;
import static com.warwick.common.CommonConstants.SUCCESS_MESSAGE;
import static com.warwick.common.CommonConstants.SUCCESS_STATUS;
import static com.warwick.common.CommonConstants.UNSUCCESSFUL_MESSAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warwick.model.TransformerResponse;
import com.warwick.model.ValueDescription;
import com.warwick.service.TransformerService;
import com.warwick.service.TransformerServiceImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
public class WarwickApplicationTests {
	
	private static final Logger log = LoggerFactory.getLogger(WarwickApplicationTests.class);

	@Autowired
	MockMvc mockMvc;

	@MockBean
	TransformerService transformerService;
	
	String exampleC_input = "exampleC_input.csv";
	String exampleC_output = "exampleC_output.csv";
	
	private MvcResult getPOSTMvcResult(String file) throws Exception {
		InputStream inputStream = getFile(file);
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", file, "multipart/form-data", inputStream);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/transformCsv").file(mockMultipartFile).contentType(MediaType.MULTIPART_FORM_DATA).accept(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

        
		return result;
	}
	
	private InputStream getFile(String file) throws IOException {
		return new ClassPathResource(file).getInputStream();
	}
	
	private TransformerResponse getTransformerResponse(String file){
		TransformerResponse resp = new TransformerResponse();
		
		InputStreamReader inputStreamReader = null;
		InputStream inputStream = null;
		CSVParser parser = null;
		try {
			inputStream = getFile(file);
			inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			parser = new CSVParser(inputStreamReader, CSVFormat.DEFAULT.withHeader());
			List<CSVRecord> records = parser.getRecords();
			List<Map<String,String>> responseRows = records.stream()
					.map(record -> {return record.toMap();})
					.collect
	            (Collectors.toList());

			resp.setRows(responseRows);
		 
			resp.setStatus(SUCCESS_STATUS);
			resp.setMessage(SUCCESS_MESSAGE);}catch (Exception e) {
				resp.setStatus(ERROR_STATUS);
				resp.setMessage(UNSUCCESSFUL_MESSAGE);
			}finally {
				try {
					if(inputStream != null) {
						inputStream.close();
					}
				}catch (Exception e) {
					log.error("Test Error during inputStream.close... : ",e);
				}
				try {
					if(inputStreamReader != null) {
						inputStreamReader.close();
					}
				}catch (Exception e) {
					log.error("Test Error during inputStreamReader.close... : ",e);
				}
				try {
					if(parser != null) {
						parser.close();
					}
				}catch (Exception e) {
					log.error("Test Error during parser.close... : ",e);
				}
			}
			
			return resp;
	}
	
	@Test
	public void transformCsv() throws Exception {
		InputStream inputFileInputStream = getFile(exampleC_input);
		TransformerResponse outputFileTransformerResponse = getTransformerResponse(exampleC_output);
		when(transformerService.transformCsv(inputFileInputStream)).thenReturn(outputFileTransformerResponse);
		
		MvcResult mvcResult = getPOSTMvcResult(exampleC_input);

        String payload = mvcResult.getResponse().getContentAsString();
        int status = mvcResult.getResponse().getStatus();

        Assert.assertEquals("failure - expected HTTP status 200", 200, status);
        Assert.assertTrue(
                "failure - expected HTTP response body to have a value",
                payload.trim().length() > 0);
        
        TransformerResponse actualResponse = getModelFromJson(payload);
        
        assertNotNull(actualResponse);
		assertNotNull(actualResponse.getColumnNames());
		assertNotNull(actualResponse.getRows());
		assertEquals(outputFileTransformerResponse.getColumnNames(),actualResponse.getColumnNames());
		assertEquals(outputFileTransformerResponse.getRows(),actualResponse.getRows());
		
        closeInputStream(inputFileInputStream);
		
	}
	
	private void closeInputStream(InputStream inputStream) {
		try {
			if(inputStream != null) {
				inputStream.close();
			}
		}catch (Exception e) {
			log.error("Test Error during inputStream.close... : ",e);
		}	
	}
	
	/**
	 * Converts json string to any class type object
	 * @param payload
	 * @return TransformerResponse
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private TransformerResponse getModelFromJson(String payload) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(payload, TransformerResponse.class);
	}

}
