package org.jeuxdemots.converter;

import org.jeuxdemots.model.api.lexical.*;
import org.jeuxdemots.model.graph.InMemoryJeuxDeMotsFactory;
import org.jeuxdemots.model.api.graph.JeuxDeMots;
import org.jeuxdemots.model.lexical.DefaultJDMLexicalAspect;
import org.jeuxdemots.reader.JDMLoader;
import org.jeuxdemots.reader.JDMLoaderFromDump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;

public class OntolexJDMConverter implements JDMConverter {
    private static final Logger logger = LoggerFactory.getLogger(OntolexJDMConverter.class);

    @Override
    public void convert(final JeuxDeMots jeuxDeMots) {

    }


    public static void main(final String... args) throws IOException {
        final FileInputStream jdmStream = new FileInputStream(args[0]);
        final JDMLoader jdmLoader = new JDMLoaderFromDump(jdmStream, new InMemoryJeuxDeMotsFactory());
        final JeuxDeMots jeuxDeMots = jdmLoader.load();
        final JDMLexicalAspect lexicalAspect = new DefaultJDMLexicalAspect(jeuxDeMots);
        displayLexicalEntries(lexicalAspect);
    }

    private static void displayLexicalEntries(final JDMLexicalAspect lexicalAspect) {
        for (final JDMLexicalEntry lexicalEntry : lexicalAspect.lexicalEntries()) {
            logger.info("LE(id = {}) {}#{}, #{} senses", lexicalEntry.getId(), lexicalEntry.getName(), lexicalEntry.getPosTag(), lexicalEntry
                    .getSenses()
                    .size());
            showLexicalRelations(lexicalAspect, lexicalEntry);
            for (final JDMLexicalSense sense : lexicalEntry.getSenses()) {
                logger.info("\t\t--> Sense {} gloss={}", sense.getName(), sense.getGloss());
                showSenseRelations(lexicalAspect, sense);
            }
        }
    }

    private static void showLexicalRelations(final JDMLexicalAspect lexicalAspect, final JDMLexicalEntry lexicalEntry) {
        for (final JDMLexicalRelation lexicalRelation : lexicalAspect.getLexicalRelations(lexicalEntry)) {
            logger.info("  |__LR[{}] {}->{}", lexicalRelation
                    .getType()
                    .getName(), lexicalRelation
                    .getSourceEntry()
                    .getName(), lexicalRelation
                    .getTargetEntry()
                    .getName());
        }
    }

    private static void showSenseRelations(final JDMLexicalAspect lexicalAspect, final JDMLexicalSense lexicalSense) {
        for (final JDMSemanticRelation semanticRelation : lexicalAspect.getSemanticRelations(lexicalSense)) {
            logger.info("\t\t |__SR[{}] {}->{}", semanticRelation.getType(), semanticRelation.getSourceSense(), semanticRelation.getTargetSense());
        }
    }
}
