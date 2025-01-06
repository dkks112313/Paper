package io.papermc.generator.rewriter.types.simple.trial;

import io.papermc.typewriter.parser.token.CharSequenceBlockToken;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

class ConstantInfo {

    private @MonotonicNonNull String constantName;
    private @MonotonicNonNull CharSequenceBlockToken token;

    public void constantName(String name) {
        this.constantName = name;
    }

    public void javadocs(CharSequenceBlockToken token) {
        this.token = token;
    }

    public String constantName() {
        return this.constantName;
    }

    public CharSequenceBlockToken javadocs() {
        return this.token;
    }

    public boolean isComplete() {
        return this.constantName != null && this.token != null;
    }
}
