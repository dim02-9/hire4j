package com.github.vasilangelov.hire4j.util;

import com.github.vasilangelov.hire4j.util.service.ServiceError;
import com.github.vasilangelov.hire4j.util.service.ServiceResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class RedirectAttributesUtils {

    public static void bindServiceResultErrors(RedirectAttributes redirectAttributes, ServiceResult serviceResult) {
        redirectAttributes.addFlashAttribute("errors", serviceResult.getErrors().stream().map(ServiceError::getMessage).toList());
    }

}
