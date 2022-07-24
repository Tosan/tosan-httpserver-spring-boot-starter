package com.tosan.http.server.sample.mask;

import com.tosan.tools.mask.starter.business.ValueMasker;
import com.tosan.tools.mask.starter.business.enumeration.MaskType;
import org.springframework.stereotype.Component;

/**
 * @author mina khoshnevisan
 * @since 7/6/2022
 */
@Component
public class TestValueMasker implements ValueMasker {

    @Override
    public MaskType getType() {
        return UserMaskType.TEST_MASK_TYPE;
    }

    @Override
    public String mask(String parameterPlainValue) {
        return "test*****";
    }
}