package com.tosan.http.server.sample.dto;

import com.tosan.tools.tostring.ToStringBuilder;
import com.tosan.tools.tostring.ToStringBuilderImpl;
import lombok.Getter;
import lombok.Setter;

/**
 * @author mina khoshnevisan
 * @since 7/12/2022
 */
@Setter
@Getter
public class TestRequestDto {
    private String name;
    private String family;
    private String pan;
    private String test;

    @Override
    public String toString() {
        final ToStringBuilder sb = new ToStringBuilderImpl(this);
        sb.append("superClass", super.toString());
        sb.append("name", name);
        sb.append("family", family);
        sb.panEncryptedAppend("pan", pan);
        sb.append("test", test);
        return sb.toString();
    }
}