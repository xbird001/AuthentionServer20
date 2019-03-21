package com.dse.security.config;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;

/**
 * 默认的加密方式(MD5)
 */
public class DefaultDsePasswordEncoder implements PasswordEncoder {

    private Logger logger = LoggerFactory.getLogger(DefaultDsePasswordEncoder.class);

    @Override
    public String encode(CharSequence rawPassword) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        byte[] byteArray;
        try {
            byteArray = String.valueOf(rawPassword).getBytes("UTF-8");
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString().toUpperCase();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.length() == 0) {
            logger.warn("Empty encoded password");
            return false;
        }
        if(StringUtils.isBlank(String.valueOf(rawPassword))) {
            return false;
        }
        return StringUtils.equalsIgnoreCase(encode(rawPassword),encodedPassword);
    }

}
