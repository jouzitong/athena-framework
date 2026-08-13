<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>{{SPRING_BOOT_VERSION}}</version>
        <relativePath/>
    </parent>

    <groupId>{{GROUP_ID}}</groupId>
    <artifactId>{{ARTIFACT_ID}}</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <properties>
        <java.version>{{JAVA_VERSION}}</java.version>
        <athena.version>{{ATHENA_VERSION}}</athena.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.athena</groupId>
                <artifactId>framework-dependencies</artifactId>
                <version>${athena.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.athena</groupId>
            <artifactId>athena-framework-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
