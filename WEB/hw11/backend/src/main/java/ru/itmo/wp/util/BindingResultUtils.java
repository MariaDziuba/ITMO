package ru.itmo.wp.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class BindingResultUtils {
    public static String getErrorMessage(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ObjectError objectError = bindingResult.getAllErrors().get(0);

                return objectError.getDefaultMessage();

        } else {
            return null;
        }
    }
}
