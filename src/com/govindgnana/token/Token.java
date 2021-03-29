package com.govindgnana.token;

public record Token(TokenType token, String literal) {
    public static Token lookupIdent(String ident) {
        return switch (ident) {
            // General Keywords
            case "let" -> new Token(TokenType.LET, "let");

            case "fn" -> new Token(TokenType.FUNCTION, "fn");
            case "return" -> new Token(TokenType.RETURN, "return");

            // Conditional Statement Keywords
            case "if" -> new Token(TokenType.IF, "if");
            case "else" -> new Token(TokenType.ELSE, "else");

            // Boolean Keywords
            case "true" -> new Token(TokenType.TRUE, "true");
            case "false" -> new Token(TokenType.FALSE, "false");

            // Normal identifier
            default -> new Token(TokenType.IDENTIFIER, ident);

        };
    }
}
