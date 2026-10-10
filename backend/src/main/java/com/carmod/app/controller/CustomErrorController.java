package com.carmod.app.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            model.addAttribute("statusCode", statusCode);

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorTitle", "Page Not Found");
                model.addAttribute("errorMessage", "The page you are looking for does not exist.");
                return "error/404";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("errorTitle", "Access Denied");
                model.addAttribute("errorMessage", "You do not have permission to access this page.");
                return "error/403";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorTitle", "Internal Server Error");
                model.addAttribute("errorMessage", "Something went wrong on our end. Please try again later.");
                return "error/500";
            }
        }

        model.addAttribute("statusCode", 500);
        model.addAttribute("errorTitle", "Unexpected Error");
        model.addAttribute("errorMessage", message != null ? message.toString() : "An unexpected error occurred.");
        return "error/500";
    }

    @GetMapping("/error/403")
    public String accessDenied(Model model) {
        model.addAttribute("statusCode", 403);
        model.addAttribute("errorTitle", "Access Denied");
        model.addAttribute("errorMessage", "You do not have permission to access this page.");
        return "error/403";
    }
}
