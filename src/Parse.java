/*
 * Thomas Leach
 * Dr. Salimi
 * Programming Languages
 * 28 November 2022
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import Parse.Token;

public class Parse {

    // Global declarations of variables
    static final int MAX_LEXEME_LEN = 100;
    static Token charClass; // Compare to enum to identify the character's class
    static int lexLen; // Current lexeme's length
    static char lexeme[] = new char[MAX_LEXEME_LEN]; // Current lexeme's character array
    static char nextChar;
    static Token nextToken;
    static int charIndex;
    static char ch = '0';
    static FileWriter myOutput;
    static File myInput;
    static String line;
    static Scanner scan;

    // Tokens and categories
    enum Token {
        DIGIT,
        LETTER,
        UNKNOWN,
        ADD_OP,
        ASSIGN_OP,
        DIV_OP,
        END_KEYWORD,
        END_OF_FILE,
        IDENT,
        INT_LIT,
        LEFT_PAREN,
        MULT_OP,
        PRINT_KEYWORD,
        PROGRAM_KEYWORD,
        RIGHT_PAREN,
        SEMICOLON,
        SUB_OP
    }

    /*********************** Main driver ***********************/
    public static void main(String[] args) throws IOException {

        // Variables
        Scanner scan1;
        myOutput = new FileWriter("parseOut.txt");
        // Open the file, and scan each line for lexical analysis
        try {
            myInput = new File("sourceProgram.txt");
            scan1 = new Scanner(myInput);
            scan = new Scanner(myInput);

            /*
             * Formatting for the beginning of the output
             * (Each print statement goes to the console and the output file for
             * convenience)
             */
            System.out.println("Thomas L. Student, CSCI4200, Fall 2022, Parser");
            myOutput.write("Thomas L. Student, CSCI4200, Fall 2022, Parser\n");
            System.out.println("********************************************************************************");
            myOutput.write("********************************************************************************\n");

            // Prints the contents of sourceProgram to parseOut
            while (scan1.hasNextLine()) {
                line = scan1.nextLine();
                System.out.println(line);
                myOutput.write(line + "\n");
            }
            System.out.println("********************");
            myOutput.write("********************\n");

            // For each line, grab each character
            while (scan.hasNextLine()) {
                line = scan.nextLine();
                System.out.println(line.trim());
                myOutput.write(line.trim() + "\n");
                charIndex = 0;

                // Performs lexical analysis and parsing within array bounds
                if (getChar(line)) {
                    lex(line);
                    program();

                }
            }

            System.out.println("Parsing of the program is complete!");
            myOutput.write("Parsing of the program is complete!\n");

            System.out.println("********************************************************************************");
            myOutput.write("********************************************************************************\n");

            scan.close();
        }

        catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        myOutput.close();
    }

    /************************************************
     * Assign each lexeme with its respective token.
     * This allows the lexical analyzer to determine
     * what the Token names connect to.
     ************************************************/
    private static Token lookup(char ch) {

        switch (ch) {
            case '(':
                addChar();
                nextToken = Token.LEFT_PAREN;
                break;

            case ')':
                addChar();
                nextToken = Token.RIGHT_PAREN;
                break;

            case '+':
                addChar();
                nextToken = Token.ADD_OP;
                break;

            case '-':
                addChar();
                nextToken = Token.SUB_OP;
                break;

            case '*':
                addChar();
                nextToken = Token.MULT_OP;
                break;

            case '/':
                addChar();
                nextToken = Token.DIV_OP;
                break;

            case '=':
                addChar();
                nextToken = Token.ASSIGN_OP;
                break;
            case ';':
                addChar();
                nextToken = Token.SEMICOLON;
                break;

            /*
             * No default case - each lexeme should fall
             * within one of the categories set above.
             */
        }

        return nextToken;
    }

    /************* addChar - a function to add nextChar to lexeme *************/
    private static boolean addChar() {

        if (lexLen <= 98) {
            lexeme[lexLen++] = nextChar;
            lexeme[lexLen] = 0;
            return true;
        }

        else {
            System.out.println("Error - lexeme is too long \n");
            return false;
        }
    }

    /*************
     * getChar - a function to get the next character in the line
     *************/
    private static boolean getChar(String ln) {

        if (charIndex >= ln.length()) {
            return false;
        }

        nextChar = ln.charAt(charIndex++);

        if (Character.isDigit(nextChar)) {
            charClass = Token.DIGIT;
        }

        else if (Character.isAlphabetic(nextChar)) {
            charClass = Token.LETTER;
        }

        else {
            charClass = Token.UNKNOWN;
        }

        return true;
    }

    /************* getNonBlank - a method to skip whitespace *************/
    public static boolean getNonBlank(String ln) {
        while (Character.isSpaceChar(nextChar) || nextChar == '	') {
            if (!getChar(ln)) {
                return false;
            }
        }
        return true;
    }

    /* @throws IOException ***************************************************/
    /*************
     * lex - a simple lexical analyzer for arithmetic expressions
     *************/
    public static Token lex(String ln) throws IOException {

        lexLen = 0;
        getNonBlank(ln);

        switch (charClass) {

            // Parse identifiers
            case LETTER:
                nextToken = Token.IDENT;
                addChar();

                if (getChar(ln)) {
                    while (charClass == Token.LETTER || charClass == Token.DIGIT) {
                        addChar();

                        if (!getChar(ln)) {
                            break;
                        }
                    }

                    if (charClass == Token.UNKNOWN && charIndex == ln.length()) {
                        charIndex--;
                    }
                }
                break;

            // Parse integer literals
            case DIGIT:
                nextToken = Token.INT_LIT;
                addChar();

                if (getChar(ln)) {
                    while (charClass == Token.DIGIT) {
                        addChar();

                        if (!getChar(ln)) {
                            break;
                        }
                    }

                    if (charClass == Token.UNKNOWN && charIndex == ln.length()) {
                        charIndex--;
                    }
                }
                break;

            // Parentheses and operators
            case UNKNOWN:
                lookup(nextChar);
                getChar(ln);
                break;

            default:
                nextToken = Token.UNKNOWN;
                break;
        } // End of switch

        // Handles cases when the Next lexeme is: PROGRAM, END, and print
        switch (String.valueOf(lexeme, 0, lexLen).toUpperCase()) {
            case "PROGRAM":
                nextToken = Token.PROGRAM_KEYWORD;
                break;
            case "PRINT":
                nextToken = Token.PRINT_KEYWORD;
                break;
            case "END":
                nextToken = Token.END_KEYWORD;
                break;
        }

        // Print each token
        System.out.printf("Next token is: %-12s\n", String.valueOf(nextToken));
        myOutput.write(String.format("Next token is: %-12s\n", String.valueOf(nextToken)));

        return nextToken;
    } // End of function lex

    /*
     * program function
     * Parses strings in the language generated by the rule:
     * <program> → PROGRAM <statement>{;<statement>} END
     */
    private static void program() throws IOException {
        if (nextToken == Token.PROGRAM_KEYWORD) {
            System.out.println("Enter <program>");
            myOutput.write("Enter <program>\n");
            // For each lexme in the line, grab the token
            while (scan.hasNextLine()) {
                String str = scan.nextLine().trim();
                charIndex = 0;
                if (getChar(str) && nextToken != Token.END_KEYWORD) {
                    while (charIndex < str.length()) {
                        // Get token
                        lex(str);
                        // If the next token is print or idntifier, call statement
                        if (nextToken == Token.PRINT_KEYWORD || nextToken == Token.IDENT) {
                            statement(str);
                        } else if (nextToken != Token.PRINT_KEYWORD && nextToken != Token.IDENT
                                && nextToken != Token.END_KEYWORD) {
                            // It was not a identifer or print keyword
                            error("identifer or print keyword");
                        }
                    }
                }
                if (nextToken != Token.SEMICOLON && nextToken != Token.END_KEYWORD) {
                    // It was not a semicolon or end keyword
                    error("semicolon or end keyword");
                }
            }
        }
        System.out.println("Exit <program>");
        myOutput.write("Exit <program>\n");
    }// End of prgram function

    /*
     * statement function
     * Parses strings in the language generated by the rule:
     * <statement> → <output> | <assign>
     */
    private static void statement(String str) throws IOException {
        // If the next token is print, call output
        if (nextToken == Token.PRINT_KEYWORD) {
            output(str);
        }
        // Else if the next token is indtifier, call assign
        else if (nextToken == Token.IDENT) {
            assign(str);
        }
    }

    /*
     * ouput function
     * Parses strings in the language generated by the rule:
     * <output> → print (<expr>)
     */
    private static void output(String str) throws IOException {
        System.out.println("Enter <output>");
        myOutput.write("Enter <output>\n");
        // Get the next token
        lex(str);
        // As long as the next token is (, get the next token and parse the next term
        while (nextToken == Token.LEFT_PAREN) {
            expr(str);
        }
        System.out.println("Exit <output>");
        myOutput.write("Exit <output>\n");
        System.out.println(" ");
        myOutput.write(" \n");
    }// End of output function

    /*
     * assign function
     * Parses strings in the language generated by the rule:
     * <assign> → IDENT = <expr>
     */
    private static void assign(String str) throws IOException {
        System.out.println("Enter <assign>");
        myOutput.write("Enter <assign>\n");
        // Get the next token
        lex(str);
        // As long as the next token is =, get the next token and parse the next term
        while (nextToken == Token.ASSIGN_OP) {
            lex(str);
            expr(str);
        }
        System.out.println("Exit <assign>");
        myOutput.write("Exit <assign>\n");
        System.out.println(" ");
        myOutput.write(" \n");
    }// End of assign function

    /*
     * expr function
     * Parses strings in the language generated by the rule:
     * <expr> -> <term> {(+ | -) <term>}
     */
    private static void expr(String str) throws IOException {
        System.out.println("Enter <expr>");
        myOutput.write("Enter <expr>\n");
        // Parse the first term
        term(str);
        // As long as the next token is + or -, get the next token and parse the next
        // term
        while (nextToken == Token.ADD_OP || nextToken == Token.SUB_OP) {
            lex(str);
            factor(str);
        }
        System.out.println("Enter <expr>");
        myOutput.write("Exit <expr>\n");
    }// End of expr function

    /*
     * term
     * Parses strings in the language generated by the rule:
     * <term> -> <factor> {(* | /) <factor>)
     */
    private static void term(String str) throws IOException {
        System.out.println("Enter <term>");
        myOutput.write("Enter <term>\n");
        // Parse the first factor
        factor(str);
        // As long as the next token is * or /, get the next token and parse the next
        // factor
        while (nextToken == Token.MULT_OP || nextToken == Token.DIV_OP) {
            lex(str);
            factor(str);
        }
        System.out.println("Enter <term>");
        myOutput.write("Exit <term>\n");
    }// End of term function

    private static void factor(String str) throws IOException {
        System.out.println("Enter <factor>");
        myOutput.write("Enter <factor>\n");
        // Determine which RHS
        if (nextToken == Token.IDENT || nextToken == Token.INT_LIT) {
            // Get the next token
            lex(str);
        } else {
            /*
             * If the right-hand-side is ( <expr> ) then call lex to pass over (, call expr,
             * and check for the )
             */
            if (nextToken == Token.LEFT_PAREN) {
                lex(str);
                expr(str);
                if (nextToken == Token.RIGHT_PAREN) {
                    lex(str);
                } else {
                    error("right-parenthesis");
                }
            } else {
                // It was not an id, an integer literal, or a left parenthesis
                error("identifier, integer, or left-parenthesis");
            }
        }
        System.out.println("Enter <factor>");
        myOutput.write("Exit <factor>\n");
    }// End of factor function

    // error function
    public static void error(String str) throws IOException {
        // Writes to parseOut when the function is called in any other function
        myOutput.write(String.format("**ERROR** - expected %-12s\n", str));
    }
}