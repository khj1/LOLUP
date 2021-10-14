package com.lolup.lolup_project.config;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JasyptConfigTest {

    PooledPBEStringEncryptor encryptor;
    SimpleStringPBEConfig config;

    @BeforeEach
    void setup() {
        encryptor = new PooledPBEStringEncryptor();
        config = new SimpleStringPBEConfig();

        config.setPassword(System.getProperty("jasypt.encryptor.password"));
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");

        encryptor.setConfig(config);
    }

    @Test
    void jasypt_테스트() {
        String url = "https://test.url";
        String encryptUrl = encryptor.encrypt(url);

        assertThat(url).isEqualTo(encryptor.decrypt(encryptUrl));
    }

}