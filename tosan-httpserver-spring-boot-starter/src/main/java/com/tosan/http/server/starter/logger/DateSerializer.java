package com.tosan.http.server.starter.logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date> {

    private final SerializerUtility serializerUtility;

    public DateSerializer(SerializerUtility serializerUtility) {
        this.serializerUtility = serializerUtility;
    }

    @Override
    public void serialize(Date value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        serializerUtility.serialize(value.toString(), jsonGenerator);
    }
}