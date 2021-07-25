package info.kgeorgiy.ja.dziuba.implementor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Assisting class to {@link Implementor}. Provides tools for necessary operations
 * for implementation source code generation.
 *
 * @author Dziuba Maria
 * @version 1.0
 */

class ImplementorCodeGenerator {

    /**
     * Class for generating variables' names
     * <p>
     * {@link Supplier}, which returns unique names
     */

    private static class ArgNameGenerator implements Supplier<String> {
        /**
         * Current variable's number
         */
        static int counter = 0;

        /**
         * {@link Supplier} method realisation
         *
         * @return {@link String} next variable name
         */
        @Override
        public String get() {
            return "arg" + (counter++);
        }
    }

    /**
     * System-defined line separator constant.
     */

    private final String NEWLINE = System.lineSeparator();

    /**
     * Tabulation constant.
     */

    private final String TAB = "\t";

    /**
     * Generates {@link String} declaring implementation class package if available.
     *
     * @param token {@link Class} which implementation is required
     * @return package declaration {@link String} if token is in package or empty
     * {@link String} otherwise
     */

    private String getPackage(Class<?> token) {
        // todo :NOTE: pkg might be empty
        // done
        String pkg = token.getPackageName();
        return pkg == null ? "" : String.join(" ", "package", pkg, ";");
    }

    /**
     * Generates class opening line. Includes modifiers, name and super class.
     *
     * @param token {@link Class} which implementation is required
     * @return implementation class opening line
     */

    private String getClassHeader(Class<?> token) {
        return String.join(" ",
                "public", "class", token.getSimpleName() + "Impl", "implements", token.getCanonicalName());
    }

    /**
     * Generates method definition. Includes modifiers, return code,
     * name, and generated args.
     *
     * @param method {@link Method} which opening line is required
     * @return Opening {@link String} of requested {@link Method}
     */

    private StringBuilder getMethodDefinition(Method method) {

        String returnType = method.getReturnType().getCanonicalName();
        String name = method.getName();

        return new StringBuilder(String.join(" ", "public", returnType, name))
                .append("(")
                .append(getArguments(method))
                .append(")")
                .append(" ")
                .append(" ")
                .append("{");
    }

    /**
     * Generates {@link Method} complete code.
     *
     * @param method {@link Method} which body is required
     * @return Implementation {@link String} of required {@link Method}
     */

    private StringBuilder getMethodDeclaration(Method method) {
        return new StringBuilder()
                .append(TAB.repeat(2))
                .append("return")
                .append(" ")
                .append(getReturnValues(method))
                .append(";")
                .append(NEWLINE)
                .append(TAB)
                .append("}");
    }

    /**
     * Generates {@link String} declaring {@link Method} arguments
     * separated by comma.
     *
     * @param method {@link Method} which arguments are required
     * @return {@link String} of arguments list
     */

    private String getArguments(Method method) {
        info.kgeorgiy.ja.dziuba.implementor.ImplementorCodeGenerator.ArgNameGenerator argNameGenerator = new info.kgeorgiy.ja.dziuba.implementor.ImplementorCodeGenerator.ArgNameGenerator();

        return Arrays.stream(method.getParameterTypes())
                .map(arg -> arg.getCanonicalName() + " " + argNameGenerator.get())
                .collect(Collectors.joining("," + " "));
    }

    /**
     * Generates default value.
     *
     * @param method {@link Method} to find default value of
     * @return {@link String} of default value
     */

    private String getReturnValues(Method method) {
        Class<?> token = method.getReturnType();
        if (token.equals(void.class)) {
            return "";
        } else if (token.equals(boolean.class)) {
            return "false";
        } else if (!token.isPrimitive()) {
            return "null";
        } else {
            return "0";
        }
    }

    /**
     * Generates {@link Method} code.
     *
     * @param method {@link Method} to generate body of
     * @return method implementation body as {@link String}
     */

    private StringBuilder generateMethod(Method method) {
        return new StringBuilder()
                .append(TAB)
                .append(getMethodDefinition(method))
                .append(NEWLINE)
                .append(getMethodDeclaration(method));
    }

    /**
     * Generates {@link String} of implementations of each {@link Method} except for static, default and private ones.
     *
     * @param token {@link Class} which implementation is required
     * @return {@link String} of source code {@link Method}s implementations.
     */

    private String generateAllMethods(Class<?> token) {
        // todo :NOTE: not all methods should be implemented
        // added check for static, default and private methods
        return Arrays.stream(token.getMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers()) &&
                        !method.isDefault() && !Modifier.isPrivate(method.getModifiers()))
                .map(new info.kgeorgiy.ja.dziuba.implementor.ImplementorCodeGenerator()::generateMethod)
                .collect(Collectors.joining(NEWLINE.repeat(2)));
    }

    /**
     * Generated complete source code of given {@link Class}.
     *
     * @param token {@link Class} which implementation is required
     * @return {@link String} containing complete generated source code
     * @see #getClassHeader(Class)
     * @see #generateMethod(Method)
     */

    public String getImplementation(Class<?> token) {
        return new StringBuilder()
                .append(getPackage(token))
                .append(NEWLINE)
                .append(getClassHeader(token))
                .append(" " + "{")
                .append(NEWLINE)
                .append(generateAllMethods(token))
                .append(NEWLINE)
                .append("}").toString();
    }
}
