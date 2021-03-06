package parser;

import exceptions_reduced_version.ParsingException;
import expressions.*;
import generics.GenericType;

import java.util.List;


public class ExpressionParser<T extends Number> implements Parser<T> {
    private int curPosition = 0;
    private char curChar;
    private String expression;
    private final String EOF = " ";

    private final List<String> operators = List.of(
            "count",
            "min",
            "max"
    );

    private final List<String> expectedChars = List.of(
            "+", "-", "*", "/", "(", ")", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", EOF
    );

    private final GenericType<T> mode;

    public ExpressionParser(GenericType<T> mode) {
        this.mode = mode;
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(expression.charAt(curPosition))) {
            curPosition++;
        }
    }

    private T getValue() throws ParsingException {
        int start = curPosition;
        while (Character.isDigit(expression.charAt(curPosition))) {
            curPosition++;
        }
        return mode.convertToNumber(expression.substring(start, curPosition));
    }

    private String getOperator() {
        int end = curPosition;
        while (Character.isLetter(expression.charAt(end))) {
            end++;
        }
        return expression.substring(curPosition, end);
    }

    private void getCurChar() throws ParsingException {
        skipWhitespace();
        curChar = expression.charAt(curPosition);
        if (!expectedChars.contains(Character.toString(curChar)) && !operators.contains(getOperator())) {
            System.out.println();
            throw new ParsingException("Unknown symbol " + curChar + " at position " + curPosition);
        }
    }

    public TripleExpression<T> parse(final String expression) throws ParsingException {
        this.expression = expression + EOF;
        return fourthLevel();
    }

    private TripleExpression<T> fourthLevel() throws ParsingException {
        TripleExpression<T> fourthLevel = thirdLevel();
        getCurChar();
        while (true) {
            switch (getOperator()) {
                case "min":
                    curPosition += 3;
                    fourthLevel = new Min<T>(fourthLevel, thirdLevel());
                    break;
                case "max":
                    curPosition += 3;
                    fourthLevel = new Max<T>(fourthLevel, thirdLevel());
                    break;
                default:
                    return fourthLevel;
            }
        }
    }

    private TripleExpression<T> thirdLevel() throws ParsingException {
        TripleExpression<T> secondLevel = secondLevel();
        getCurChar();
        while (true) {
            switch (curChar) {
                case '+': {
                    curPosition++;
                    secondLevel = new Add<T>(secondLevel, secondLevel());
                    break;
                }
                case '-': {
                    curPosition++;
                    secondLevel = new Subtract<T>(secondLevel, secondLevel());
                    break;
                }
                default:
                    return secondLevel;
            }
            getCurChar();
        }
    }

    private TripleExpression<T> secondLevel() throws ParsingException {
        TripleExpression<T> firstLevel = firstLevel();
        getCurChar();
        while (true) {
            switch (curChar) {
                case '*': {
                    curPosition++;
                    firstLevel = new Multiply<T>(firstLevel, firstLevel());
                    break;
                }
                case '/': {
                    curPosition++;
                    skipWhitespace();
                    firstLevel = new Divide<T>(firstLevel, firstLevel());
                    break;
                }
                default:
                    skipWhitespace();
                    return firstLevel;
            }
            getCurChar();
        }
    }

    private TripleExpression<T> firstLevel() throws ParsingException {
        getCurChar();
        switch (curChar) {
            case '(': {
                curPosition++;
                TripleExpression<T> curRes = fourthLevel();
                curPosition++;
                return curRes;
            }
            case '-': {
                curPosition++;
                return new Negate<T>(firstLevel());
            }
            case 'x': {
                curPosition++;
                return new Variable<T>("x");
            }
            case 'y': {
                curPosition++;
                return new Variable<T>("y");
            }
            case 'z': {
                curPosition++;
                return new Variable<T>("z");
            }
            case 'c': {
                curPosition += 5;
                return new Count<T>(firstLevel());
            }
            default:
                if (Character.isDigit(curChar)) {
                    return new Const<>(getValue());
                } else {
                    throw new ParsingException("Missed argument at position " + curPosition);
                }
        }
    }
}