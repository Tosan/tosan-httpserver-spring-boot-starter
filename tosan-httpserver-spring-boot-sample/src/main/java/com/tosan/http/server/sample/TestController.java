package com.tosan.http.server.sample;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author mina khoshnevisan
 * @since 9/20/2022
 */
@Controller
@RequestMapping("/controller/httpserver")
public class TestController {

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/error")
    public String errorHandler(HttpServletRequest request) {
        return "/errorPage.html";
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/image")
    public String getTestImage(HttpServletRequest request) {
        return "/image.png";
    }

    @GetMapping(value = {"/", "/index"})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("/index.html");
        return modelAndView;
    }
}