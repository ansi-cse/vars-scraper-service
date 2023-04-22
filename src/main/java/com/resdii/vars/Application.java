package com.resdii.vars;

import com.resdii.ms.common.rest.NooApplication;
import com.resdii.vars.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
@EnableFeignClients
public class Application extends NooApplication {

	private CategoryMapper categoryMapper;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

//	@Value("${rotating-proxy.user-name}")
//	String authUser;
//
//	@Value("${rotating-proxy.password}")
//	String authPassword;

	@Override
	public void commonTaskOnStartup() {
		super.commonTaskOnStartup();
		// init categories
		categoryMapper.initCategories();
		// residential proxy
//		System.setProperty("http.proxyUser", authUser);
//		System.setProperty("http.proxyPassword", authPassword);
//		Authenticator.setDefault(
//				new Authenticator() {
//					public PasswordAuthentication getPasswordAuthentication() {
//						return new PasswordAuthentication(
//								authUser, authPassword.toCharArray());
//					}
//				}
//		);

	}

	@Autowired
	public void setCategoryMapper(CategoryMapper categoryMapper) {
		this.categoryMapper = categoryMapper;
	}
}