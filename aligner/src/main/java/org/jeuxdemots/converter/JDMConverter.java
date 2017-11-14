package org.jeuxdemots.converter;

import org.jeuxdemots.model.api.graph.JeuxDeMots;

@FunctionalInterface
public interface JDMConverter {
    void convert(JeuxDeMots jeuxDeMots);
}
