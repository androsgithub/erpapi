package com.api.erp.v1.main.config.startup.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class PermissionReflectionUtil {

    public static List<String> extrairPermissions(Class<?> permissionClass) {
        List<String> permissions = new ArrayList<>();

        for (Field field : permissionClass.getDeclaredFields()) {

            boolean isString = field.getType().equals(String.class);
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            boolean isFinal = Modifier.isFinal(field.getModifiers());

            if (isString && isStatic && isFinal) {
                try {
                    String valor = (String) field.get(null);

                    if (valor != null && valor.contains(".")) {
                        permissions.add(valor);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(
                            "Erro ao acessar campo " + field.getName() +
                                    " da classe " + permissionClass.getSimpleName(), e
                    );
                }
            }
        }

        return permissions;
    }
}
