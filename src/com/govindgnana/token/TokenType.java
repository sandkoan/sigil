package com.govindgnana.token;

public enum TokenType {
    // Flow control
    IF,
    // Arithmetic
    ADD, NEG, MUL, INV, REM,
    // Logical
    EQ, LESS,
    // List manipulation
    HEAD, TAIL, PAIR, FUSE,
    // String manipulation
    LITR, STR, WORDS,
    // I/O
    IN, OUT,

    FN,
    IS,

    INTRINSIC,
    IDENTIFIER,
    VALUE
}

