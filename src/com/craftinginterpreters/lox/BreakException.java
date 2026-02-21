package com.craftinginterpreters.lox;

class BreakException extends RuntimeException {
    // No stack trace needed for control flow.
    BreakException() {
        super(null, null, false, false);
    }
}