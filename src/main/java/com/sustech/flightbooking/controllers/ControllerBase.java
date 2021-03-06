package com.sustech.flightbooking.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Arrays;
import java.util.List;

public abstract class ControllerBase {

    protected ModelAndView page(String viewName) {
        return new ModelAndView(viewName);
    }

    protected ModelAndView redirect(String url) {
        RedirectView redirectView = new RedirectView(url);
        redirectView.setExposeModelAttributes(false);
        return new ModelAndView(redirectView);
    }

    protected ModelAndView notFound() {
        ModelAndView modelAndView = new ModelAndView("/error/404");
        modelAndView.getModelMap().put("errorMessage", "");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

    protected ModelAndView badRequest() {
        return badRequest("");
    }

    protected ModelAndView badRequest(String errorMessage) {
        ModelAndView modelAndView = new ModelAndView("/error/400");
        modelAndView.getModelMap().put("errorMessage", errorMessage);
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        return modelAndView;
    }

    protected ModelAndView pageWithViewModel(String viewName, Object viewModel) {
        ModelAndView modelAndView = page(viewName);
        modelAndView.getModelMap().put("model", viewModel);
        return modelAndView;
    }

    protected List<String> errorMessages(String... messages) {
        return Arrays.asList(messages);
    }
}
