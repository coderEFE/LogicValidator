package com.efe.logicvalidator;

import java.util.Optional;



public class Lexer {

    private String input;

    private int index = 0;



    public Lexer(String input) {

        this.input = input;

    }



    public Optional<Token> nextToken() throws LexException {

        skipWhitespace();

        if (index >= input.length())

            return Optional.empty();



        switch (input.charAt(index)) {

            case 't':

                ensureInputMatches("rue");

                return Optional.of(Token.TRUE);

            case 'T':

                ensureInputMatches("RUE");

                return Optional.of(Token.TRUE);

            case 'f':

                ensureInputMatches("alse");

                return Optional.of(Token.FALSE);

            case 'F':

                ensureInputMatches("ALSE");

                return Optional.of(Token.FALSE);

            case '(':

                index += 1;

                return Optional.of(Token.LEFT_PAREN);

            case ')':

                index += 1;

                return Optional.of(Token.RIGHT_PAREN);

            case '!':

                index += 1;

                return Optional.of(Token.NOT);

            case '~':

                index += 1;

                return Optional.of(Token.NOT);

            case '&':

                ensureInputMatches("&");

                return Optional.of(Token.AND);

            case 'a':

                ensureInputMatches("nd");

                return Optional.of(Token.AND);

            case 'A':

                ensureInputMatches("ND");

                return Optional.of(Token.AND);

            case '|':

                ensureInputMatches("|");

                return Optional.of(Token.OR);

            case 'o':

                ensureInputMatches("r");

                return Optional.of(Token.OR);

            case 'O':

                ensureInputMatches("R");

                return Optional.of(Token.OR);

            case ']':

                index += 1;

                return Optional.of(Token.THEREFORE);

            case '>':

                index += 1;

                return Optional.of(Token.THEREFORE);

            case '=':

                ensureInputMatches("=");

                return Optional.of(Token.BICONDITIONAL);

            default:

                throw new LexException(String.format("No rules matched the input (%c)", input.charAt(index)));

        }

    }



    private void skipWhitespace() {

        try {

            for (char c = input.charAt(index); c == ' ' || c == '\n' || c == '\t'; c = input.charAt(index)) {

                index += 1;

            }

        } catch (IndexOutOfBoundsException ignore) { }

    }



    private void ensureInputMatches(String token) throws LexException {

        index += 1;



        for (int i = 0; i < token.length(); ++i) {

            if (input.charAt(index) != token.charAt(i))

                throw new LexException(String.format("Expected '%c', got '%c'", input.charAt(index), token.charAt(i)));

            index += 1;

        }

    }



    public enum Token {

        TRUE, FALSE, LEFT_PAREN, RIGHT_PAREN, NOT, AND, OR, THEREFORE, BICONDITIONAL

    }

}
