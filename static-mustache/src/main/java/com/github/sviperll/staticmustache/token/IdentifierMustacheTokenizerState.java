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
package com.github.sviperll.staticmustache.token;

import com.github.sviperll.staticmustache.MustacheToken;
import com.github.sviperll.staticmustache.ProcessingException;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class IdentifierMustacheTokenizerState implements MustacheTokenizerState {
    final MustacheTagKind kind;
    final StringBuilder name;
    private final MustacheTokenizer tokenizer;

    IdentifierMustacheTokenizerState(MustacheTagKind kind,
                                     StringBuilder name,
                                     MustacheTokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.kind = kind;
        this.name = name;
    }

    @Override
    public Void twoOpenBraces() throws ProcessingException {
        tokenizer.error("Unexpected open braces");
        return null;
    }

    @Override
    public Void threeOpenBraces() throws ProcessingException {
        tokenizer.error("Unexpected open braces");
        return null;
    }

    @Override
    public Void twoClosingBraces() throws ProcessingException {
        if (kind == MustacheTagKind.UNESCAPED_VARIABLE_THREE_BRACES)
            tokenizer.error("Expecting three closing braces, not two");
        else
            tokenizer.setState(new OutsideMustacheTokenizerState(tokenizer));
        return null;
    }

    @Override
    public Void threeClosingBraces() throws ProcessingException {
        if (kind == MustacheTagKind.UNESCAPED_VARIABLE_THREE_BRACES)
            tokenizer.setState(new OutsideMustacheTokenizerState(tokenizer));
        else
            tokenizer.error("Expecting two closing braces, not three");
        return null;
    }

    @Override
    public Void character(char c) throws ProcessingException {
        if (Character.isWhitespace(c)) {
            boolean expectsThree = kind == MustacheTagKind.UNESCAPED_VARIABLE_THREE_BRACES;
            tokenizer.setState(new EndMustacheTokenizerState(tokenizer, expectsThree));
        } else {
            name.append(c);
        }
        return null;
    }

    @Override
    public Void endOfFile() throws ProcessingException {
        tokenizer.error("Unclosed field at the end of file");
        return null;
    }

    @Override
    public void beforeStateChange() throws ProcessingException {
        String nameString = name.toString();
        switch (kind) {
            case VARIABLE:
                tokenizer.emitToken(MustacheToken.variable(nameString));
                break;
            case UNESCAPED_VARIABLE_TWO_BRACES:
                tokenizer.emitToken(MustacheToken.unescapedVariable(nameString));
                break;
            case UNESCAPED_VARIABLE_THREE_BRACES:
                tokenizer.emitToken(MustacheToken.unescapedVariable(nameString));
                break;
            case BEGIN_SECTION:
                tokenizer.emitToken(MustacheToken.beginSection(nameString));
                break;
            case BEGIN_INVERTED_SECTION:
                tokenizer.emitToken(MustacheToken.beginInvertedSection(nameString));
                break;
            case END_SECTION:
                tokenizer.emitToken(MustacheToken.endSection(nameString));
                break;
            default:
                throw new IllegalStateException("Wrong kind in parser: " + kind);
        }
    }

}