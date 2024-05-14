package com.mrthinkj.integratemiddlewareapplication;

import com.mrthinkj.integratemiddlewareapplication.model.SqlEmployee;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
            title = "IntegrateTwoDBMSMiddleware",
            description = "Spring Boot Middleware Application REST APIs Documentation",
            version = "v1.0",
            contact = @Contact(
                    name = "MrThinkJ",
                    email = "ledinhthinh.ws@gmail.com"
            ),
            license = @License(
                    name = "Apache 2.0"
            )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Spring Boot Middleware Application Documentation",
                url = "https://github.com/MrThinkJ/IntegrateDBMSMiddleware"
        )
)
public class IntegrateMiddlewareApplication{

    @Autowired
    private JdbcTemplate template;

    public static void main(String[] args) {
        SpringApplication.run(IntegrateMiddlewareApplication.class, args);
    }


//    @Override
//    public void run(String... args) throws Exception {
//        String sql = "select * from Personal";
//        List<SqlEmployee> employees = template.query(sql, BeanPropertyRowMapper.newInstance(SqlEmployee.class));
//        System.out.println("Employee");
//        employees.forEach(System.out::println);
//    }
}
