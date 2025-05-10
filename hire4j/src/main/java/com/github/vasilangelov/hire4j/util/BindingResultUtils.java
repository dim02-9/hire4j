package com.github.vasilangelov.hire4j.util;

import com.github.vasilangelov.hire4j.util.service.ServiceResult;
import org.springframework.validation.BindingResult;

public class BindingResultUtils {

    public static void addResultErrorsToBindingResult(BindingResult bindingResult, ServiceResult serviceResult) {
        serviceResult.getGeneralErrors()
                .forEach(error -> {
                    bindingResult.reject(error.getMessage());
                });

        serviceResult.getValidationErrors()
                .forEach(error -> {
                    bindingResult.rejectValue(error.getFieldName(),"", error.getMessage());
                });
    }

}
