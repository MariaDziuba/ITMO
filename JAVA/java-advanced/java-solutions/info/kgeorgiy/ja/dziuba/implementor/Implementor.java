package info.kgeorgiy.ja.dziuba.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * Class implementing {@link Impler}, {@link JarImpler}.
 * Gives methods to generate classes and interfaces.
 *
 * @author Dziuba Maria
 * @version 1.0
 */

public class Implementor implements Impler, JarImpler {

    /**
     * Default constructor
     */

    public Implementor() {
    }

    /**
     * Main method that provides console interface.
     * Usage: ([-jar]) [class name] [root dir]
     *
     * @param args command line arguments
     * @see info.kgeorgiy.ja.dziuba.implementor.Implementor#implement(Class, Path)
     * @see info.kgeorgiy.ja.dziuba.implementor.Implementor#implementJar(Class, Path)
     */

    public static void main(String[] args) {
        // todo :NOTE: do some validation of arguments
        // validateArgs added
        try {
            validateArgs(args);
            if (jarMode(args)) {
                new Implementor().implementJar(Class.forName(args[1]), Paths.get(args[2]));
            } else {
                new Implementor().implement(Class.forName(args[0]), Paths.get(args[1]));
            }

        } catch (ImplerException e) {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found");
        }
    }

    /**
     * Checks if program is launched in jar mode
     *
     * @param args command line arguments
     * @return true if program is launched with [-jar] [class name] [root dir],
     * false otherwise
     * @throws ImplerException in case of wrong arguments format
     */
    private static boolean jarMode(String[] args) throws ImplerException {
        if (args.length == 2) {
            return false;
        } else {
            if (args[0].equals("-jar")) {
                return true;
            } else {
                throw new ImplerException("Wrong arguments format, usage: ([-jar]) [class name] [root dir]");
            }
        }
    }

    /**
     * Validates input arguments
     *
     * @param args command line arguments
     * @throws ImplerException in case of wrong arguments' format
     */

    private static void validateArgs(String[] args) throws ImplerException {
        if (args == null || !(args.length == 2 || args.length == 3)) {
            throw new ImplerException("Wrong arguments format, usage: ([-jar]) [class name] [root dir] ");
        }

        for (String arg : args) {
            if (arg == null) {
                throw new ImplerException("Arguments must be non-null");
            }
        }
    }

    /**
     * @param token {@link Class} which classpath is required
     * @return {@link String} representation of token classpath
     */

    private static Path getClassPath(Class<?> token) {
        try {
            return Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Creates {@link Path} of implementation source code and constructs missing parent directories.
     *
     * @param token {@link Class} which implementation is required
     * @param root  Root {@link Path} for implementation files
     * @return {@link Path} where implementation must be created
     * @throws ImplerException in case of invalid generated path
     */

    private Path createPath(Class<?> token, Path root) throws ImplerException {
        String tokenPath = getTokenPath(token, "java");
        Path completePath = Paths.get(root.toString(), tokenPath);
        createParentDirectories(completePath);
        return completePath;
    }

    /**
     * Provides valid source code directories. Formed considering package
     * and name.
     *
     * @param token      {@link Class} which implementation is required
     * @param extension: either .java or .class as {@link String}
     * @return Path to source code as {@link String}
     */

    private String getTokenPath(Class<?> token, String extension) {
        // todo :NOTE: not cross-platform file separator
        // File.separator instead of "/"
        return String.join(File.separator, token.getPackageName().split("\\.")) +
                File.separator + token.getSimpleName() + "Impl." + extension;
    }

    /**
     * Creates missing parent directories of given path.
     *
     * @param path {@link Path} to generate parent directories for
     * @return Generated parent directory {@link Path}
     * @throws ImplerException if an error occurs while creating directories
     */

    private Path createParentDirectories(Path path) throws ImplerException {
        Path parentPath = path.toAbsolutePath().normalize().getParent();
        try {
            Files.createDirectories(Objects.requireNonNull(parentPath));
        } catch (IOException e) {
            throw new ImplerException("Couldn't create directories", e);
        }
        return parentPath;
    }

    // new functions for hw5 and hw6

    /**
     * Implements the given class.
     * Invokes {@link ImplementorCodeGenerator#getImplementation(Class)} to generate the code of the implementing class
     *
     * @param token the {@link Class} token of implemented class
     * @param root  the directory for putting the implementation
     * @throws ImplerException if an error occurs while writing the result into the output file
     * @see ImplementorCodeGenerator#getImplementation(Class)
     */

    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {

        Path pathToSrc = createPath(token, root);

        int modifiers = token.getModifiers();

        // todo :NOTE: you should not try to implement classes
        // isInterface check added
        if (token.isPrimitive() || Modifier.isPrivate(modifiers) || !token.isInterface()) {
            throw new ImplerException("Unsupported type");
        }
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(pathToSrc)) {
            bufferedWriter.write(new ImplementorCodeGenerator().getImplementation(token));
        } catch (IOException e) {
            throw new ImplerException("Couldn't open file: " + e.getMessage());
        }
    }

    /**
     * Invoked in case the program was run with -jar key.
     * Creates the temporary directory and invokes methods for implementing, compiling, and creating jar file.
     *
     * @param token   the {@link Class} token of implemented class
     * @param jarFile name of jar file to be generated
     * @throws ImplerException if an error occurs while creating parent path for jar or temporary directory for source
     * @see #createJar(Class, Path, Path)
     * @see #compile(Class, Path)
     * @see #implement(Class, Path)
     */

    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        if (token == null || jarFile == null) {
            throw new ImplerException("Arguments must not be null");
        }

        Path parentDir = createParentDirectories(jarFile);
        Path sourceDir = createTempFolder(parentDir);

        try {
            implement(token, sourceDir);
            compile(token, sourceDir);
            createJar(token, sourceDir, jarFile);
        } catch (ImplerException e) {
            throw new ImplerException("Implementation failed", e);
        } finally {
            deleteDirectories(sourceDir.toFile());
        }

    }

    /**
     * Deletes all contents of directory {@link File} recursively.
     *
     * @param file target directory {@link Path}
     */

    private void deleteDirectories(File file) {

        File[] files = file.listFiles();
        if (files != null) {
            for (File subFile : files) {
                deleteDirectories(subFile);
            }
        }
        file.deleteOnExit();
    }

    /**
     * Creates the jar file for the solution.
     *
     * @param token      the {@link Class} token of implemented class
     * @param sourceDir  the temporary directory with classes
     * @param neededPath name of jar file to be generated
     * @throws ImplerException if an error occurs while creating jar file
     */

    private void createJar(Class<?> token, Path sourceDir, Path neededPath) throws ImplerException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        try (JarOutputStream stream = new JarOutputStream(Files.newOutputStream(neededPath), manifest)) {
            String implementationPath = getTokenPath(token, "class");
            stream.putNextEntry(new ZipEntry(implementationPath));
            Files.copy(Path.of(sourceDir.toString(), implementationPath), stream);
        } catch (IOException e) {
            throw new ImplerException("Can't create Jar", e);
        }
    }

    /**
     * Compiles the solution.
     *
     * @param token     the {@link Class} token of implemented class
     * @param sourceDir the temporary directory with classes
     * @throws ImplerException if an error occurs while compiling the result or searching for classpath
     */

    private void compile(Class<?> token, Path sourceDir) throws ImplerException {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new ImplerException("Compiler is not provided");
        }

        String[] compilationArgs = {"-encoding", "UTF-8",
                "-cp",
                sourceDir.toString() + File.pathSeparator + getClassPath(token),
                Path.of(sourceDir.toString(), getTokenPath(token, "java")).toString()
        };

        if (compiler.run(System.in, System.out, System.err, compilationArgs) != 0) {
            throw new ImplerException("Compilation failed");
        }

    }

    /**
     * Creates temporary directory in given {@link Path}.
     *
     * @param parentDir {@link Path} where temporary directory needs to be created
     * @return {@link Path} of created temporary directory
     * @throws ImplerException if an error occurs while creating temporary directories
     */

    private Path createTempFolder(Path parentDir) throws ImplerException {
        Path sourceDir;
        try {
            sourceDir = Files.createTempDirectory(parentDir, "impler-tmp-dir");
        } catch (IOException e) {
            throw new ImplerException("Can't create temporary directories", e);
        }
        return sourceDir;
    }
}