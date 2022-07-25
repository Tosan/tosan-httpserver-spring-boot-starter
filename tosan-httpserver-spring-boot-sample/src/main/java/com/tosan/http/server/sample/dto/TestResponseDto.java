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
public class TestResponseDto {
    private String secretKey;
    private String password;

    @Override
    public String toString() {
        final ToStringBuilder sb = new ToStringBuilderImpl(this);
        sb.append("superClass", super.toString());
        sb.encryptedAppend("secretKey", secretKey);
        sb.encryptedAppend("password", password);
        return sb.toString();
    }
}