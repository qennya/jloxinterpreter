package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

class LoxInstance {
    protected LoxClass klass;
    protected final Map<String, Object> fields = new HashMap<>();

    LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

    // For subclasses (LoxClass) to initialize klass later.
    protected LoxInstance() {}

    Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        LoxFunction method = klass.findMethod(name.lexeme);
        if (method != null) return method.bind(this);

        throw new RuntimeError(name,
                "Undefined property '" + name.lexeme + "'.");
    }

    void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    // Convenience for internal wiring (static methods on class object)
    void set(String name, Object value) {
        fields.put(name, value);
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }
}