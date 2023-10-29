package com.tosan.http.server.starter.logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Sadegh Iraji
 * @since 10/29/2023
 **/
public class MultipartMaskSerializer extends JsonSerializer<MultipartFile> {

    private final SerializerUtility serializerUtility;

    public MultipartMaskSerializer(SerializerUtility serializerUtility) {
        this.serializerUtility = serializerUtility;
    }

    @Override
    public void serialize(MultipartFile multipartFile, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        serializerUtility.serialize(multipartFile.getOriginalFilename(), jsonGenerator);
    }
}
