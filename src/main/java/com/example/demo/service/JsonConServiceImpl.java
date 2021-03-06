package com.example.demo.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.model.InputSaveJson;
import com.example.demo.pojo.Components;
import com.example.demo.pojo.MyPojo;
import com.example.demo.repository.SaveJsonRepository;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Service
public class JsonConServiceImpl implements JsonConService{
	
	@Value("${input.path}")
	private String inputPath;
	
	@Value("${output.path}")
	private String outputPath;
	
	@Autowired
	SaveJsonRepository saveJsonRepository;
	@Override
	public void convertJson() {

		ObjectMapper objectMapper = new ObjectMapper();

		InputStream input = null;
		try {
			input = new FileInputStream(inputPath+"InputJson.json");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(input!=null) {
			try {
				MyPojo myPojo = objectMapper.readValue(input, MyPojo.class);
				List<Components> componentList=myPojo.getData().get(0).getTaskSpecs().getComponentScoping().get(0).getComponents();
				for(Components component:componentList) {
					for(ObjectNode record:component.getRecords()) {
						if(record.has("ruleResult")) {
							ObjectNode ruleResult =(ObjectNode) record.get("ruleResult");
							if(ruleResult.has("result")) {
								ArrayNode result = (ArrayNode) ruleResult.get("result");
								if(result.get(0).asText().equalsIgnoreCase("Include")) {
									ObjectNode multiEngineResults=objectMapper.createObjectNode();
									multiEngineResults.put("suspectResult", " ");
									multiEngineResults.put("cbvutvi4vResult"," ");
									multiEngineResults.put("wellknownResult", " ");
									multiEngineResults.put("uniqueResult", " ");
									record.set("multiEngineResults", multiEngineResults);
								}
							}
						}
					}
				}
				String myPojoStr=objectMapper.writeValueAsString(myPojo);
				System.out.println(myPojoStr);
				String newMyPojoStr=myPojoStr.replace("taskSpecs", "result");
				System.out.println("result:"+newMyPojoStr);
				ObjectNode newMyPojoNode=(ObjectNode) objectMapper.readTree(newMyPojoStr);
				objectMapper.writeValue(Paths.get(outputPath+"outputJson1.json").toFile(), newMyPojoNode);
			} catch (StreamReadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DatabindException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	
	}

	@Override
	public String saveJson(JsonNode saveJsonNode) {
		InputSaveJson saveJson = new InputSaveJson();
		saveJson.setRequestJson(saveJsonNode);
		saveJson.setStatus("new");
		InputSaveJson newSaveJson = saveJsonRepository.save(saveJson);
		if(newSaveJson!=null) {
			return "Saved";
		}
		return "Not Saved";
	}

	@Override
	public String findJsonAndSaveReponse() {
		List<InputSaveJson> newSaveJsonList = saveJsonRepository.findByStatus("new");
		for(InputSaveJson saveJson: newSaveJsonList) {
			JsonNode requestJson =saveJson.getRequestJson();
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				MyPojo myPojo = objectMapper.treeToValue(requestJson, MyPojo.class);
				List<Components> componentList=myPojo.getData().get(0).getTaskSpecs().getComponentScoping().get(0).getComponents();
				for(Components component:componentList) {
					for(ObjectNode record:component.getRecords()) {
						if(record.has("ruleResult")) {
							ObjectNode ruleResult =(ObjectNode) record.get("ruleResult");
							if(ruleResult.has("result")) {
								ArrayNode result = (ArrayNode) ruleResult.get("result");
								if(result.get(0).asText().equalsIgnoreCase("Include")) {
									ObjectNode multiEngineResults=objectMapper.createObjectNode();
									multiEngineResults.put("suspectResult", " ");
									multiEngineResults.put("cbvutvi4vResult"," ");
									multiEngineResults.put("wellknownResult", " ");
									multiEngineResults.put("uniqueResult", " ");
									record.set("multiEngineResults", multiEngineResults);
								}
							}
						}
					}
				}
				String myPojoStr=objectMapper.writeValueAsString(myPojo);
				System.out.println(myPojoStr);
				String newMyPojoStr=myPojoStr.replace("taskSpecs", "result");
				System.out.println("result:"+newMyPojoStr);
				ObjectNode newMyPojoNode=(ObjectNode) objectMapper.readTree(newMyPojoStr);
				saveJson.setResponseJson(newMyPojoNode);
				saveJson.setStatus("processed");
				saveJsonRepository.save(saveJson);
			} catch (StreamReadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DatabindException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "processed";
	}

}
