package com.ibm.automation.parasoft.util;

import io.apptik.json.JsonElement;
import io.apptik.json.JsonObject;
import io.apptik.json.exception.JsonException;
import io.apptik.json.generator.JsonGenerator;
import io.apptik.json.generator.JsonGeneratorConfig;
import io.apptik.json.schema.SchemaV4;
import io.apptik.json.schema.fetch.SchemaResourceFetcher;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * @author kalpana.l
 *
 */
public class JsonGeneratorFromSchema {
	public static JsonElement generateInputSampleString(
			String incomingJsonInputSchema, String schemaFilePath)
					throws JsonException, IOException, ParseException {

		SchemaV4 schema1 = new SchemaV4().wrap(JsonElement.readFrom(
				incomingJsonInputSchema).asJsonObject());

		File f1 = new File(schemaFilePath);

		if (f1.exists()) {
			// schemaFilePath =
			// "C:/Kalpana/TD Bank/RAML files/EPC_RAML_NEW/jsd/AddLocatorStreetAddressRq.1.schema.json";
			// System.out.println("schemaFilePath inside if -------->"+schemaFilePath);
			URI url = new File(schemaFilePath).toURI();
			schema1.setOrigSrc(url);
			// System.out.println("schemaFilePath"+schemaFilePath);
		} else {
			schemaFilePath = schemaFilePath.replace("\\jsd", "");
			// System.out.println("schemaFilePath inside else-------->"+schemaFilePath);
		}
		return generateWithSettings(schema1);

	}

	private static JsonElement generateWithSettings(SchemaV4 schema)
			throws ParseException {

		JsonGeneratorConfig gConf = new JsonGeneratorConfig();
		ArrayList<String> images = new ArrayList<String>();
		images.add("/photos/image.jpg");
		images.add("/photos/image.jpg");

		gConf.uriPaths.put("seven", images);

		gConf.globalArrayItemsMax = 2;

		gConf.globalIntegerMin = 0;
		gConf.globalIntegerMax = 100;
		// can still limit numbers as long as its still valid according to the
		// schema
		gConf.integerMin.put("five1", 3);
		gConf.integerMax.put("five1", 3);
		gConf.arrayItemsMin.put("min", 1);
		gConf.arrayItemsMax.put("max", 1);
		gConf.skipObjectProperties.add("two");
		// schema.getJson()gConf.

		new SchemaResourceFetcher();
		System.out.println(new JsonGenerator(schema, gConf).generate());
		new JSONObject(new JsonGenerator(schema, gConf).generate());

		// loopThroughJson(myjson);
		return new JsonGenerator(schema, gConf).generate();
	}

	/*
	 * private JsonElement generateNoSettings(Schema schema) { return new
	 * Generator(schema, new GeneratorConfig()).generate(); }
	 *
	 * private JsonElement generateWithSettings(Schema schema) { GeneratorConfig
	 * gConf = new GeneratorConfig(); ArrayList<String> images = new
	 * ArrayList<String>(); images.add("/photos/image.jpg");
	 * images.add("/photos/image.jpg");
	 *
	 * gConf.uriPaths.put("seven", images);
	 *
	 * gConf.globalArrayItemsMax = 7;
	 *
	 * gConf.globalIntegerMin = 0; gConf.globalIntegerMax = 100; //can still
	 * limit numbers as long as its still valid according to the schema
	 * gConf.integerMin.put("five1", 300); gConf.integerMax.put("five1", 400);
	 * gConf.skipObjectProperties.add("two");
	 *
	 * return new Generator(schema, gConf).generate(); }
	 */

	SchemaV4 schema1;

	public JsonGeneratorFromSchema() {
		try {
			schema1 = new SchemaV4()
			.wrap((JsonObject) JsonElement
					.readFrom("{"
							+ "\"type\" : \"object\","
							+ "\"oneOf\" :  ["
							+ "{"
							+ "\"type\" : \"object\","
							+ "\"oneOf\" :  ["
							+ "{\n"
							+

									"\"type\" : \"object\","
									+ "\"properties\" : {"
									+ "\"one\" : {\"type\" : \"number\"  } ,"
									+ "\"two\" : {\"type\" : \"string\" },"
									+ "\"three\" : "
									+ "{"
									+ "\"type\" : \"object\","
									+ "\"properties\" : {"
									+ "\"one\" : {\"type\" : \"number\"  } ,"
									+ "\"two\" : {\"type\" : \"string\" }"
									+ "}"
									+ "},"
									+ "\"four\" : {\"type\" : \"boolean\"  },"
									+ "\"five\" : {\"type\" : \"integer\", \"minimum\": 200, \"maximum\":5000 },"
									+ "\"five1\" : {\"type\" : \"integer\", \"minimum\": 200, \"maximum\":5000 },"
									+ "\"five2\" : {\"type\" : \"integer\"},"
									+ "\"six\" : {\"enum\" : [\"one\", 2, 3.5, true, [\"almost empty aray\"], {\"one-item\":\"object\"}, null]  }, "
									+ "\"seven\" : {\"type\" : \"string\", \"format\": \"uri\" }"
									+ "}" + "}]" + "}]" + "}"));

			System.out.println("generation without settings"
					+ new JsonGenerator(schema1, null).generate());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}