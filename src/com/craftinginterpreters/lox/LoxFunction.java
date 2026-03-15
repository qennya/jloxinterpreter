package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    private final Environment closure;
    private final boolean isInitializer;
    private final String methodName;
    private final LoxClass definingClass;
    private final LoxFunction innerMethod;

    LoxFunction(Stmt.Function declaration, Environment closure,
                boolean isInitializer, String methodName,
                LoxClass definingClass, LoxFunction innerMethod) {
        this.isInitializer = isInitializer;
        this.closure = closure;
        this.declaration = declaration;
        this.methodName = methodName;
        this.definingClass = definingClass;
        this.innerMethod = innerMethod;
    }

    LoxFunction bind(LoxInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);

        LoxFunction computedInner = null;
        if (methodName != null && definingClass != null) {
            computedInner = instance.getLoxClass().findInnerMethod(methodName, definingClass);
            if (computedInner != null) {
                computedInner = computedInner.bind(instance);
            }
        }

        LoxFunction bound = new LoxFunction(
                declaration,
                environment,
                isInitializer,
                methodName,
                definingClass,
                computedInner);

        environment.define("inner", bound);


        return bound;
    }

    String getMethodName() {
        return methodName;
    }

    LoxClass getDefiningClass() {
        return definingClass;
    }

    LoxFunction getInnerMethod() {
        return innerMethod;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme,
                    arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            if (isInitializer) return closure.getAt(0, "this");
            return returnValue.value;
        }
        if (isInitializer) return closure.getAt(0, "this");
        return null;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }
}