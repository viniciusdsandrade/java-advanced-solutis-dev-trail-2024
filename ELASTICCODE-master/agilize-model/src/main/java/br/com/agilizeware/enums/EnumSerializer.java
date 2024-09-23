package br.com.agilizeware.enums;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class EnumSerializer extends JsonSerializer<IEnum<Integer>> {

	@Override
	public void serialize(IEnum<Integer> enumObj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException, JsonProcessingException {
		
		jsonGenerator.writeStartObject();
		jsonGenerator.writeFieldName("id");
		jsonGenerator.writeNumber(enumObj.getId());
		jsonGenerator.writeFieldName("label");
		jsonGenerator.writeString(enumObj.getLabel());
		jsonGenerator.writeFieldName("description");
		jsonGenerator.writeString(enumObj.getDescription());
		jsonGenerator.writeEndObject();
	}
}
