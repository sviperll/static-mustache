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
import com.github.sviperll.staticmustache.Position;
import com.github.sviperll.staticmustache.PositionedToken;
import com.github.sviperll.staticmustache.ProcessingException;
import com.github.sviperll.staticmustache.TokenProcessor;
import com.github.sviperll.staticmustache.token.util.BracesTokenizer;
import com.github.sviperll.staticmustache.token.util.PositionHodingTokenProcessor;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class MustacheTokenizer implements TokenProcessor<PositionedToken<BracesToken>> {
    public static TokenProcessor<Character> createInstance(String fileName, TokenProcessor<PositionedToken<MustacheToken>> downstream) {
        TokenProcessor<PositionedToken<BracesToken>> mustacheTokenizer = new MustacheTokenizer(new PositionHodingTokenProcessor<MustacheToken>(downstream));
        return BracesTokenizer.createInstance(fileName, mustacheTokenizer);
    }

    private final PositionHodingTokenProcessor<MustacheToken> downstream;
    private MustacheTokenizerState state = new OutsideMustacheTokenizerState(this);
    private Position position;
    MustacheTokenizer(PositionHodingTokenProcessor<MustacheToken> downstream) {
        this.downstream = downstream;
    }

    @Override
    public void processToken(PositionedToken<BracesToken> positionedToken) throws ProcessingException {
        position = positionedToken.position();
        downstream.setPosition(position);
        BracesToken token = positionedToken.innerToken();
        token.accept(state);
    }

    void setState(MustacheTokenizerState newState) throws ProcessingException {
        state.beforeStateChange();
        state = newState;
    }

    void error(String message) throws ProcessingException {
        throw new ProcessingException(position, message);
    }

    void emitToken(MustacheToken token) throws ProcessingException {
        downstream.processToken(token);
    }

}