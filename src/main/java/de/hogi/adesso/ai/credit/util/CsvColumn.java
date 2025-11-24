package de.hogi.adesso.ai.credit.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvColumn {

    String name();

    Type type() default Type.STRING;

    public static enum Type {
        STRING,
        INTEGER,
        FLOAT,
        CURRENCY
    }
}
