package com.codingworld.liquibasedemo;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

@SpringBootApplication
public class LiquibaseDemoApplication {

	public static void main(String[] args) throws IOException {
		Properties properties = new Properties();
		for (String arg : args) {
			String[] keyValue = arg.split("=");
			if (keyValue.length == 2) {
				properties.setProperty(keyValue[0], keyValue[1]);
			}
		}
		String sqlPath = properties.getProperty("-sqlPath");
		moveSqlQueryToResources(sqlPath);

		String author = properties.getProperty("-author");
		String id = properties.getProperty("-id");
		System.out.println("Details from Args = " + author+" | " +id +" | " +sqlPath);
		/*String author = "Dummy22";
		String id = "24355";
		String sqlPath = "classpath:queryTemp/sqlQuery.sql";*/

		// Initialize Velocity
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init();

		// Load the template
		String templatePath = "src/main/resources/db/changelog/changelog-template.vm";
		//String templatePath = "db/changelog/changelog-template.vm";
		VelocityContext context = new VelocityContext();
		context.put("dynamicAuthor", author);
		context.put("id", id);
		//context.put("sqlPath", sqlPath);
		context.put("sqlPath", "queryTemp/sqlQuery.sql");

		// Merge template with dynamic values
		StringWriter writer = new StringWriter();
		velocityEngine.mergeTemplate(templatePath, "UTF-8", context, writer);

		// Destination path for the generated XML file
		String destinationPath = "src/main/resources/db/changelog/generated-changelog.xml";

		// Write the content to the destination file
		try {
			FileWriter fileWriter = new FileWriter(new File(destinationPath));
			fileWriter.write(writer.toString());
			fileWriter.close();
			System.out.println("XML file generated and moved to " + destinationPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		SpringApplication.run(LiquibaseDemoApplication.class, args);
	}

	private static void moveSqlQueryToResources(String sourceFilePath) {
		//String sourceFilePathCnst = "C:\\Users\\ragrajam\\Downloads\\sqlQuery.sql";
		String resourcesFolderPath = "src/main/resources/db/changelog/queryTemp";
		try {
			Path sourcePath = Paths.get(sourceFilePath);
			Path targetPath = Paths.get(resourcesFolderPath, sourcePath.getFileName().toString());
			Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("SQL file moved successfully to resources folder.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
