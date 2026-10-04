package com.tosan.http.server.starter.logger;

import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * @author Sadegh Iraji
 * @since 10/29/2023
 **/
public class MultipartMaskSerializer extends ValueSerializer<MultipartFile> {

    private final SerializerUtility serializerUtility;

    public MultipartMaskSerializer(SerializerUtility serializerUtility) {
        this.serializerUtility = serializerUtility;
    }

    @Override
    public void serialize(MultipartFile multipartFile, JsonGenerator jsonGenerator,
                          SerializationContext serializers) throws JacksonException {
        serializerUtility.serialize(multipartFile.getOriginalFilename(), jsonGenerator);
    }
}
