package com.epam.finaltask.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorMsg = "";
        Integer statusCode = null;

        if (status != null) {
            statusCode = Integer.valueOf(status.toString());
            model.addAttribute("statusCode", statusCode);

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                errorMsg = "Page Not Found";
            } else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorMsg = "Internal Server Error";
            } else if(statusCode == HttpStatus.FORBIDDEN.value()) {
                errorMsg = "Access Denied";
            } else {
                errorMsg = "Something went wrong";
            }
        }

        model.addAttribute("errorMessage", errorMsg);
        return "error";
    }
}
