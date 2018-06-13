package br.com.iagsaude.dump.city;

import java.io.FileReader;

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
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withSkipHeaderRecord()
	            .withQuote('"').withDelimiter(';').parse(new FileReader("MunicipiosBrasil.csv"));
		
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
				post(jsonObj);
			}
			skipHeader=true;
		}
	}
	
	private static void post(JSONObject jsonObj) {
		try {
			Client client = Client.create();
			WebResource webResource = client.resource("https://u2unfesqt2.execute-api.us-east-1.amazonaws.com/dev/city");
			ClientResponse response = webResource.type("application/json").post(ClientResponse.class, jsonObj.toString());
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
