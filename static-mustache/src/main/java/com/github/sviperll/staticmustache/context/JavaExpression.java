/*
 * Copyright (c) 2014, Victor Nazarov <asviraspossible@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation and/or
 *     other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.sviperll.staticmustache.context;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class JavaExpression {
    private final JavaLanguageModel model;
    private final String text;
    private final TypeMirror type;
    JavaExpression(JavaLanguageModel model, String text, TypeMirror type) {
        this.model = model;
        this.text = text;
        this.type = type;
    }
    String text() {
        return text;
    }
    TypeMirror type() {
        return type;
    }
    JavaLanguageModel model() {
        return model;
    }

    JavaExpression arrayLength() {
        return new JavaExpression(model, text + ".length", model.knownTypes()._int);
    }

    public JavaExpression subscript(JavaExpression indexExpression) {
        return new JavaExpression(model, text + "[" + indexExpression.text() + "]", ((ArrayType)type).getComponentType());
    }

    public JavaExpression fieldAccess(Element element) {
        VariableElement fieldElement = (VariableElement)element;
        TypeMirror memberType = model.asMemberOf((DeclaredType)type, fieldElement);
        return new JavaExpression(model, text + "." + fieldElement.getSimpleName(), memberType);
    }

    public JavaExpression methodCall(Element element, JavaExpression... arguments) {
        ExecutableElement executableElement = (ExecutableElement)element;
        ExecutableType executableType = methodSignature(executableElement);
        StringBuilder result = new StringBuilder();
        result.append(text).append(".").append(executableElement.getSimpleName()).append("(");
        if (arguments.length > 0) {
            result.append(arguments[0].text());
            for (int i = 1; i < arguments.length; i++) {
                result.append(", ");
                result.append(arguments[i].text());
            }
        }
        result.append(")");
        return new JavaExpression(model, result.toString(), executableType.getReturnType());
    }

    public ExecutableType methodSignature(Element element) {
        ExecutableElement executableElement = (ExecutableElement)element;
        return (ExecutableType)model.asMemberOf((DeclaredType)type, executableElement);
    }
}
