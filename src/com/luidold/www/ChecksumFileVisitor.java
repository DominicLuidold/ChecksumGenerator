package com.luidold.www;

import java.io.FileInputStream;
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
 * The ChecksumFileVisitor will print the checksum of the file it visits.
 *
 * @author Dominic Luidold
 * @version 1.0.0
 */
public class ChecksumFileVisitor extends SimpleFileVisitor<Path> {
    private static final Logger _logger = Logger.getLogger(ChecksumFileVisitor.class.getName());
    private static MessageDigest _hashAlgorithm;

    public ChecksumFileVisitor(String hashAlgorithm) {
        try {
            _hashAlgorithm = MessageDigest.getInstance(hashAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            _logger.log(Level.SEVERE, "Hashing algorithm '" + _hashAlgorithm + "' unknown. Please specify a correct algorithm");
            System.exit(1);
        }
    }

    /**
     * Print the checksum if the given path represents a regular file.
     */
    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) {
        if (basicFileAttributes.isRegularFile()) {
            System.out.println(path.toString() + " - " + generateChecksum(path.toString(), _hashAlgorithm));
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * Generate checksum for given file.
     *
     * @param filePath String path to file
     *
     * @return String checksum for given file
     */
    private static String generateChecksum(String filePath, MessageDigest md) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            // Create buffer to read data in chunks
            byte[] buffer = new byte[1024];
            int byteCount = 0;

            // Read file data and update message digest
            while ((byteCount = fis.read(buffer)) != -1) {
                md.update(buffer, 0, byteCount);
            }
            ;

            // Close FileInputStream
            fis.close();

            // Convert bytes to hexadecimal format
            StringBuilder result = new StringBuilder();
            for (byte b : md.digest()) {
                result.append(String.format("%02x", b));
            }

            // Return checksum
            return result.toString();
        } catch (IOException e) {
            _logger.log(Level.SEVERE, "Severe error occurred - terminating application\n" + e.getMessage());
            System.exit(-1);
        }
        return "<Error - could not generate " + _hashAlgorithm + " hash>";
    }
}
