package main.java.springboot;

import java.io.InputStream;
import main.java.iniloader.IniLoader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;

@SpringBootApplication
@ComponentScan("main.java.springboot.backend")
public class WebService {

	public static void main(String[] args) {
		String filepath = "config.ini";
		IniLoader iniLoader = new IniLoader();
		if (args.length == 1){
			filepath = args[0];
			System.out.println("Using config file: " + filepath);
			iniLoader.parse(filepath);
		}else{
			InputStream in = WebService.class.getResourceAsStream("config.ini");
			iniLoader.parse(in);
		}
		
		SpringApplication.run(WebService.class, args);
	}
	
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {	
	   return (container -> {
	        ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401.html");
	        ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
	        ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
	 
	        container.addErrorPages(error401Page, error404Page, error500Page);
	   });
	}

}
