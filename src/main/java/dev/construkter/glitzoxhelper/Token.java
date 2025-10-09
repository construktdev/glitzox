package dev.construkter.glitzoxhelper;

import dev.construkter.glitzoxhelper.util.TokenNotFoundException;

import java.io.File;
import java.util.Scanner;

public class Token {
    public static String get() {
        String token;
        try (Scanner scanner = new Scanner(new File(".token"))) {
            token = scanner.nextLine();
        } catch (Exception e) {
            throw new TokenNotFoundException("Token not found: " + e.getMessage());
        }

        if (token.isEmpty() || token.equals(" ")) {
            throw new TokenNotFoundException("Token not found");
        }

        return token;
    }
}
