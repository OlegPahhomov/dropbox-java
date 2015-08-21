package files.validator;

import spark.Request;

import static files.validator.FileValidatorHelper.*;

public class FileValidator {

    public static boolean invalidInsert(Request request) {
        return ! (containsParts(request) && validContent(request));
    }

    public static boolean invalidDelete(Request request) {
        return ! validId(request);
    }

    public static boolean invalidGetById(Request request) {
        return ! validId(request);
    }



}
