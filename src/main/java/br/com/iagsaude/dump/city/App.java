package br.com.iagsaude.dump.city;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class App {
	public static void main(String[] args) {
		try {
			dump();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void dump() throws Throwable {
		WebResource webResource = Client.create().resource("https://u7lf5wgjt9.execute-api.us-east-1.amazonaws.com/hom/city");
		for (JSONObject jsonObj : extractCsv()) {
			ClientResponse response = webResource.type("application/json").post(ClientResponse.class, jsonObj.toString());
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
		}
	}
	
	private static List<JSONObject> extractCsv() throws Throwable{
		List<JSONObject> retorno = new ArrayList<JSONObject>();
		InputStreamReader isr = new InputStreamReader(App.class.getResourceAsStream("parte3.csv"));
		BufferedReader br = new BufferedReader(isr);
		
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withSkipHeaderRecord()
	            .withQuote('"').withDelimiter(';').parse(br);
		boolean skipHeader = false;
		for (CSVRecord record : records) {
			if (skipHeader) {
				String latitude = (String)record.get(1);
				String longitude = (String)record.get(2);
				String city = (String)record.get(4);
				String state = (String)record.get(5);
				
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("name", city);
				jsonObj.put("state", state);
				jsonObj.put("latitude", latitude);
				jsonObj.put("longitude", longitude);
				retorno.add(jsonObj); 
				
			}
			skipHeader=true;
		}
		return retorno;
	}
}