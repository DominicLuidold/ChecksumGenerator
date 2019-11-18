package com.luidold.www;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ChecksumGenerator will print the checksum for all given files in the specified root folder.
 *
 * @author Dominic Luidold
 * @version 1.0.1
 */
public class ChecksumGenerator {
    private static final Logger _logger = Logger.getLogger(ChecksumGenerator.class.getName());
    private static HashMap<String, String> _params;

    public static void main(String[] args) {
        // Parse command line parameters
        _params = parseParameter(args);
        if (_params.size() < 2 || !_params.containsKey("-a") || !_params.containsKey("-f")) {
            _logger.log(Level.WARNING, "Parameters must be specified using the following scheme: 'ChecksumGenerator -a=algorithm -f=root_folder'");
            System.exit(1);
        }
        // Start checksum generation
        start();
    }

    /**
     * Start generation of checksum list.
     */
    private static void start() {
        try {
            Files.walkFileTree(Paths.get(_params.get("-f").replace("\"", "")), new ChecksumFileVisitor(_params.get("-a")));
        } catch (NoSuchFileException e) {
            _logger.log(Level.SEVERE, "Could not locate specified file or folder '" + e.getMessage() + "'");
            System.exit(1);
        } catch (IOException e) {
            _logger.log(Level.SEVERE, "Severe error occurred - terminating application");
            System.exit(-1);
        }
    }

    /**
     * Parse given arguments to single out command line parameters.
     *
     * @param args program arguments
     *
     * @return parsed parameters in a HashMap
     */
    private static HashMap<String, String> parseParameter(String[] args) {
        HashMap<String, String> params = new HashMap<>();
        for (String param : args) {
            String[] splitResult = param.split("=", 2);

            // Check if given argument has any parameter content
            if (splitResult.length > 1) {
                params.put(splitResult[0], splitResult[1]);
            } else {
                _logger.log(Level.WARNING, "One or more parameters has not been specified correctly");
                System.exit(1);
            }
        }
        return params;
    }
}
