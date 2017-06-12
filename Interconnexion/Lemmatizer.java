package jenaJDM;


import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Lemmatizer {
	
	protected StanfordCoreNLP pipeline;

    public Lemmatizer() {
        //On initialise le lemmatiseur avec les propriétés qu'on veut utiliser.
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        this.pipeline = new StanfordCoreNLP(props);
    }

    public List<String> lemmatize(String documentText)
    {
        List<String> lemmas = new LinkedList<String>();

        // create a new string without punctuation
        String newtext = documentText.replaceAll("[\\?,.:!\\(\\){}\\[\\]<>%'\"_;/+-]", " "); //Ici on ne garde pas '
        newtext = newtext.toLowerCase();
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(newtext);

        // run all Annotators on this text
        this.pipeline.annotate(document);

        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the list of lemmas
            	lemmas.add(token.get(LemmaAnnotation.class));
            }
        }

        return lemmas;
    }
}
