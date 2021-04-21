/*
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.demo;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
public class BApplication {

    public static void main(String[] args) {
        SpringApplication.run(BApplication.class, args);
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @RestController
    class AController {
        private String currentZone;

        @PostConstruct
        private void init() {
            try {
                HttpClient client = HttpClientBuilder.create().build();
                HttpResponse response = client.execute(new HttpGet("http://100.100.100.200/latest/meta-data/zone-id"));
                currentZone = EntityUtils.toString(response.getEntity());
                currentZone = StringUtils.strip(currentZone);
            } catch (Exception e) {
                currentZone = e.getMessage();
            }
        }

        @Autowired
        RestTemplate restTemplate;

        @GetMapping("/b")
        public String a(HttpServletRequest request) {
            //return "B[" + request.getLocalAddr() + "]" + " -> " +
            //    restTemplate.getForObject("http://sc-C/c", String.class);
            return "B[" + request.getLocalAddr() + "]\n";
        }

        @GetMapping("/b-get-zone")
        public String getZone(HttpServletRequest request) {
            return "B[" + currentZone + "]\n";
        }
    }

}
