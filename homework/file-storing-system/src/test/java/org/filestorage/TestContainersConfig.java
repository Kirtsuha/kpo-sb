package org.filestorage;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestContainersConfig {

    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("file_storage")
                    .withUsername("file_storage")
                    .withPassword("file_storage");

    static MinIOContainer minio =
            new MinIOContainer("minio/minio:latest")
                    .withUserName("minioadmin")
                    .withPassword("minioadmin");

    static {
        postgres.start();
        minio.start();
    }

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("s3.endpoint", () -> minio.getS3URL());
        registry.add("s3.access-key", () -> "minioadmin");
        registry.add("s3.secret-key", () -> "minioadmin");
        registry.add("s3.bucket", () -> "antiplagiarism-works");
    }
}
