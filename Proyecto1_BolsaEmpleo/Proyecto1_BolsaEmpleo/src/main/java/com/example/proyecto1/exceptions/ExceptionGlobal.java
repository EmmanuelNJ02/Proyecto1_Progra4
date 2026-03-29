package com.example.proyecto1.exceptions;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionGlobal {

    @ExceptionHandler(NoEncontrado.class)
    public String noEncontrado(NoEncontrado e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error/no-encontrado";
    }


    @ExceptionHandler(Exception.class)
    public String errorGeneral(Exception e, Model model) {
        model.addAttribute("error", "Ocurrió un error inesperado");
        return "error/general";
    }
}