package com.craftinginterpreters.lox;

import java.util.Map;

class LoxClass implements LoxCallable {
    final String name;
    final LoxClass superclass;
    private final Map<String, LoxFunction> methods;

    LoxClass(String name, LoxClass superclass, Map<String, LoxFunction> methods) {
        this.name = name;
        this.superclass = superclass;
        this.methods = methods;
    }

    LoxFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (superclass != null) {
            return superclass.findMethod(name);
        }

        return null;
    }

    LoxFunction findTopMethod(String name) {
        LoxFunction top = null;

        if (superclass != null) {
            top = superclass.findTopMethod(name); }
        if (top != null) return top;
        if (methods.containsKey(name)) {
            return methods.get(name); }
        return null; }
    LoxFunction findInnerMethod(String name, LoxClass currentDefiningClass) {
        return findInnerMethodBelow(name, currentDefiningClass);
    }

    private LoxFunction findInnerMethodBelow(String name, LoxClass targetClass) {
        if (superclass == targetClass && methods.containsKey(name)) {
            return methods.get(name); }

        if (superclass != null) {
            LoxFunction found = superclass.findInnerMethodBelow(name, targetClass);
            if (found != null) return found; }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object call(Interpreter interpreter, java.util.List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        LoxFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    @Override
    public int arity() {
        LoxFunction initializer = findMethod("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }
}