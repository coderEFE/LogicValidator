package com.efe.logicvalidator;

import java.util.EmptyStackException;

import java.util.HashMap;

import java.util.Optional;

import java.util.Stack;





public class Parser {

    private Lexer tokens;

    private static HashMap<Lexer.Token, Integer> precedences = new HashMap<Lexer.Token, Integer>() {{

        put(Lexer.Token.LEFT_PAREN, -1);

        put(Lexer.Token.BICONDITIONAL, 1);

        put(Lexer.Token.THEREFORE, 2);

        put(Lexer.Token.AND, 3);

        put(Lexer.Token.OR, 3);
        //TODO: Check if NOT should have a lower precedence value
        put(Lexer.Token.NOT, 4);

    }};



    public Parser(Lexer tokens) {

        this.tokens = tokens;

    }



    // https://en.wikipedia.org/wiki/Shunting-yard_algorithm :)

    // Kind of a dual parser and executor

    public boolean parseAndExecute() throws LexException, ParseException {

        Stack<Lexer.Token> operators = new Stack<>();

        Stack<Lexer.Token> out = new Stack<>();



        for (Optional<Lexer.Token> o = tokens.nextToken(); o.isPresent(); o = tokens.nextToken()) {

            Lexer.Token t = o.get();

            switch (t) {

                case TRUE:

                case FALSE:

                    out.push(t);

                    break;

                case LEFT_PAREN:

                    operators.push(t);

                    break;

                case RIGHT_PAREN:

                    try {

                        while (!operators.peek().equals(Lexer.Token.LEFT_PAREN))

                            out.push(executeOperator(operators.pop(), out));

                    } catch (EmptyStackException e) {
                        throw new ParseException("Mismatched Parentheses", e);

                    }



                    operators.pop(); // pop off left paren

                    break;

                default: // Operator

                    int prec = precedences.get(t);

                    while (!operators.empty() && precedences.get(operators.peek()) >= prec) {

                        out.push(executeOperator(operators.pop(), out));

                    }

                    operators.push(t);

                    break;

            }

        }



        while (!operators.empty()) {

            if (operators.peek().equals(Lexer.Token.LEFT_PAREN)) {
                throw new ParseException("Mismatched Parentheses");

            }



            out.push(executeOperator(operators.pop(), out));

        }



        if (!(out.size() == 1 && (out.peek() == Lexer.Token.TRUE || out.peek() == Lexer.Token.FALSE))) {
            throw new ParseException("Trailing values");

        }



        return out.pop() == Lexer.Token.TRUE;

    }



    // Single & and | are used because they do not short circuit

    private Lexer.Token executeOperator(Lexer.Token op, Stack<Lexer.Token> input) throws ParseException {

        switch (op) {

            case NOT:

                return popBoolean(input) ? Lexer.Token.FALSE : Lexer.Token.TRUE;

            case OR:

                return popBoolean(input) | popBoolean(input) ? Lexer.Token.TRUE : Lexer.Token.FALSE;

            case AND:

                return popBoolean(input) & popBoolean(input) ? Lexer.Token.TRUE : Lexer.Token.FALSE;

            case THEREFORE:
                //I needed to switch around the logic because it wasn't returning true conditional results. Original: !popBoolean(input) | popBoolean(input)
                return !(!popBoolean(input) & popBoolean(input)) ? Lexer.Token.TRUE : Lexer.Token.FALSE;

            case BICONDITIONAL:

                return popBoolean(input) == popBoolean(input) ? Lexer.Token.TRUE : Lexer.Token.FALSE;

            default:

                throw new ParseException("Invalid operator passed to executeOperator (please pass this one along to the developers)");

        }

    }



    private boolean popBoolean(Stack<Lexer.Token> input) {

        return input.pop().equals(Lexer.Token.TRUE);

    }

}