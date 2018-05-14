package com.put.Chatterbox.Controller;


import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class BaseRequestController {

    @RequestMapping("/")
    public String index(@RequestParam(value="color", required=false) List<String> colors) {
        final StringBuilder ret = new StringBuilder();
        ret.append("Greetings from Spring Boot!");
        if(colors != null)
        	colors.forEach(item ->{
        	ret.append(item);
        });;
        return ret.toString();
    }


}