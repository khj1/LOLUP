package com.lolup.lolup_project.config;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JasyptConfigTest {

    @Test
    void jasypt_테스트() {

        String url = "jdbc:mariadb://lolup-rds1.c7qm67jnhiks.ap-northeast-2.rds.amazonaws.com:3306/lolup";
        String username = "admin";
        String password = "lolup1234";

        String naverClientId = "wW09JL4TM0LOkyLGm1uS";
        String naverClientSecret = "g_tZBXBFgz";

        String googleClientId = "988347019914-o7367lsmnq231633cerpshve1rth3lmj.apps.googleusercontent.com";
        String googleClientSecret = "gW6W9qMnto02htjOez-I4YBK";

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        config.setPassword("1234");
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        String encryptUrl = encryptor.encrypt(url);
        String encryptUsername = encryptor.encrypt(username);
        String encryptPassword = encryptor.encrypt(password);

        String encryptNCI = encryptor.encrypt(naverClientId);
        String encryptNCS = encryptor.encrypt(naverClientSecret);

        String encryptGCI = encryptor.encrypt(googleClientId);
        String encryptGCS = encryptor.encrypt(googleClientSecret);

        System.out.println("encryptPassword = " + encryptPassword);
        System.out.println("encryptUsername = " + encryptUsername);
        System.out.println("encryptUrl = " + encryptUrl);
        System.out.println("encryptNCI = " + encryptNCI);
        System.out.println("encryptNCS = " + encryptNCS);
        System.out.println("encryptGCI = " + encryptGCI);
        System.out.println("encryptGCS = " + encryptGCS);

        assertThat(url).isEqualTo(encryptor.decrypt(encryptUrl));
    }

}