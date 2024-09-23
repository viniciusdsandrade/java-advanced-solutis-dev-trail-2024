package br.com.agilizeware.enums;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class FilterOperatorEnumDeserializer extends JsonDeserializer<FilterOperatorEnum>{

	@Override
	public FilterOperatorEnum deserialize(JsonParser jsonParser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        if(node.canConvertToInt()) {
            Integer id = Integer.valueOf(node.toString());
            return FilterOperatorEnum.findByCode(id);
        }
        else if(node.get("id") != null) {
            Integer id = node.get("id").intValue();
            return FilterOperatorEnum.findByCode(id);
        }
        return null;
	}
}
