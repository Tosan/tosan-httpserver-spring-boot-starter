package com.tosan.http.server.sample.mask;

import com.tosan.tools.mask.starter.business.enumeration.MaskType;

/**
 * @author mina khoshnevisan
 * @since 7/6/2022
 */
public class UserMaskType extends MaskType {

    public static final MaskType TEST_MASK_TYPE = new MaskType();

    public static final MaskType SECRET_MASK_TYPE = new MaskType();
}