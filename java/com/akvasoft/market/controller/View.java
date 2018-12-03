package com.akvasoft.market.controller;

import com.akvasoft.market.config.Scrape;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class View {
    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    @ResponseBody
    private ModelAndView scrape() throws InterruptedException {
        return new ModelAndView("index");
    }

    @RequestMapping(value = {"/results"}, method = RequestMethod.GET)
    @ResponseBody
    private ModelAndView result() throws InterruptedException {
        return new ModelAndView("result");
    }

}
