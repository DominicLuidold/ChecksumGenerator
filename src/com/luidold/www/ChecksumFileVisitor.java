package com.luidold.www;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ChecksumFileVisitor will print the checksum of each file it visits.
 *
 * @author Dominic Luidold
 * @version 1.0.1
 */
public class ChecksumFileVisitor extends SimpleFileVisitor<Path> {
    private static final Logger _logger = Logger.getLogger(ChecksumFileVisitor.class.getName());
    private static MessageDigest _hashAlgorithm;

    public ChecksumFileVisitor(String hashAlgorithm) {
        try {
            _hashAlgorithm = MessageDigest.getInstance(hashAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            _logger.log(Level.SEVERE, "Hashing algorithm '" + hashAlgorithm + "' unknown. Please specify a correct algorithm");
            System.exit(1);
        }
    }

    /**
     * Print the checksum if the given path represents a regular file.
     */
    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) {
        if (basicFileAttributes.isRegularFile()) {
            System.out.println(path.getFileName() + " - " + generateChecksum(path.toFile(), _hashAlgorithm));
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * Generate checksum for given file.
     *
     * @param file File file used to generate checksum
     * @param md   MessageDigest digest used to generate hash
     *
     * @return String checksum for given file
     */
    private static String generateChecksum(File file, MessageDigest md) {
        try (FileInputStream fis = new FileInputStream(file)) {
            // Create buffer to read data in chunks
            byte[] buffer = new byte[1024];
            int byteCount = 0;

            // Read file data and update message digest
            while ((byteCount = fis.read(buffer)) != -1) {
                md.update(buffer, 0, byteCount);
            }

            // Close FileInputStream
            fis.close();

            // Convert bytes to hexadecimal format
            StringBuilder result = new StringBuilder();
            for (byte b : md.digest()) {
                result.append(String.format("%02x", b));
            }

            // Return checksum
            return result.toString();
        } catch (FileNotFoundException e) {
            _logger.log(Level.SEVERE, "Could not locate specified file or folder '" + e.getMessage() + "'");
            System.exit(1);
        } catch (IOException e) {
            _logger.log(Level.SEVERE, "Severe error occurred - terminating application");
            System.exit(-1);
        }
        return "<Error - could not generate " + _hashAlgorithm + " hash>";
    }
}
