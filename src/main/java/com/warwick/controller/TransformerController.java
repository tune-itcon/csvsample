/**
 * 
 */
package com.warwick.controller;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.warwick.model.TransformerResponse;
import com.warwick.service.TransformerService;

//import com.opencsv.CSVReader;



/**
 * @author duansubramaniam
 *
 */

@RestController
public class TransformerController {
	private static final Logger log = LoggerFactory.getLogger(TransformerController.class);
	
	@Autowired
	private TransformerService transformService;
	
	/**
	 * Transforms Data from a CSV input and a CSV output
	 * @param payload CSV resource
	 * @return
	 * @throws 
	 * @throws 
	 * @throws IOException
	 */
	@RequestMapping(value="/transformCsv", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public TransformerResponse transformCsv(@RequestParam("file") MultipartFile file) throws IOException{
		log.debug("received file");
		
		InputStream inputStream = file.getInputStream();
		 TransformerResponse transResp = transformService.transformCsv(inputStream);
		 try {
				if(inputStream != null) {
					inputStream.close();
				}
		 }catch (Exception e) {
				log.error("Error during inputStream.close... : ",e);
		 }
		log.debug("done file");		
		return transResp;
	}
	
	
}
