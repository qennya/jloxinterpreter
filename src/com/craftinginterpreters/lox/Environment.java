package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Environment {
    final Environment enclosing;

    // Name-based storage (globals / fallback).
    private final Map<String, Object> values = new HashMap<>();

    // Indexed storage for locals.
    private final List<Object> slots = new ArrayList<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    void define(String name, Object value) {
        values.put(name, value);
    }

    // --------- Indexed locals (new) ---------
    void defineAt(int index, Object value) {
        while (slots.size() <= index) slots.add(null);
        slots.set(index, value);
    }

    Object getAt(int distance, int index) {
        return ancestor(distance).slots.get(index);
    }

    void assignAt(int distance, int index, Object value) {
        ancestor(distance).slots.set(index, value);
    }

    // Keep the old helper for any code that still calls it (optional).
    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }
        return environment;
    }
}