package org.jeuxdemots.aligner;

import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.InvalidBabelSynsetIDException;
import it.uniroma1.lcl.babelnet.data.*;
import it.uniroma1.lcl.jlt.util.Language;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.util.FileManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class JDMQueries {

    @SuppressWarnings("HardcodedFileSeparator")
    private static final String PREFIXES = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  " + System.lineSeparator() +
            "prefix lime:	<http://www.w3.org/ns/lemon/lime#> " + System.lineSeparator() +
            "prefix ontolex: <http://www.w3.org/ns/ontolex#> " + System.lineSeparator() +
            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + System.lineSeparator() +
            "prefix jdm: <http://www.jeuxdemots.org/data#> " + System.lineSeparator() +
            "prefix lexinfo: <http://www.lexinfo.net/ontology/2.0/lexinfo#>";
    private static final double DEFINITION_OVERLAPPING_UPDATE_WEIGHT = 2.0;
    private static final double DEFINITION_NON_OVERLAPPING_UPDATE_WEIGHT = 3.0;
    private static final Pattern WRITTEN_REPREPSENTATION_SEPARATOR = Pattern.compile("[>@]");
    private static final double GLOSSES_OVERLAPPING_UPDATE_WEIGHT = 1.5;
    private static final double GLOSSES_NON_OVERLAPPING_UPDATE_WEIGHT = 2.5;

    private JDMQueries() {
    }

    private static void update(final Dataset dataset) {
        final String update_sense = PREFIXES + "DELETE { ?o rdf:type ontolex:lexicalEntry . ?o lime:language \"fr\" . ?o ontolex:canonicalForm ?form . " +
                "?form rdf:type ontolex:form . ?form ontolex:writtenRep ?wr . } " +
                "INSERT { ?o rdf:type ontolex:lexicalSense . " +
                "?o ontolex:isSenseOf ?s . " +
                "?o rdfs:label ?wr . } " +
                "WHERE { ?s ontolex:sense ?o . " +
                "?o ontolex:canonicalForm ?form . " +
                "?form ontolex:writtenRep ?wr . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_sense ?rel . " +
                "?rel jdm:reference ?o . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        UpdateAction.parseExecute(update_sense, dataset);
        String update_lemma = PREFIXES + "INSERT { ?o ontolex:otherForm ?s . } " +
                "WHERE  { ?s jdm:r_lemma ?o . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_lemma ?rel . " +
                "?rel jdm:reference ?o . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . " +
                "FILTER(?s != ?o) }";
        UpdateAction.parseExecute(update_lemma, dataset);
        String update_nom = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:noun . } WHERE { " +
                "?pos rdfs:label \"Nom:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_verbe = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:verb . } WHERE { " +
                "?pos rdfs:label \"Ver:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_adv = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:adverb . } WHERE { " +
                "?pos rdfs:label \"Adv:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_adj = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:adjective . } WHERE { " +
                "?pos rdfs:label \"Adj:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_abr = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:abbreviation . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Abr:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_card = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:cardinalNumeral . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Card:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_det = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:determiner . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Det:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_expr = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:expression . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Expression:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_inter = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:interjection . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Int:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_prep = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:preposition . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Pre:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_pref = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:prefix .} " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Prefix:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_pro = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:pronoun . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Pro:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_suff = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:suffix . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Suffix:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_symb = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:symbol . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Symbole:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_conj1 = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:Conjunction . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Conj:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . } ";
        String update_conj2 = PREFIXES + "INSERT { ?s lexinfo:partOfSpeech lexinfo:Conjunction . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Con:\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . " +
                "MINUS { ?s jdm:r_pos \"Con:\" . " +
                "?pos2 rdfs:label  \"Conj:\" . } }";
        String update_masc = PREFIXES + "INSERT { ?s lexinfo:gender lexinfo:masculine . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Gender:Mas\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) .}";
        String update_fem = PREFIXES + "INSERT { ?s lexinfo:gender lexinfo:feminine . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Gender:Fem\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_plur = PREFIXES + "INSERT { ?s lexinfo:number lexinfo:plural .} " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Number:Plur\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_sing = PREFIXES + "INSERT { ?s lexinfo:number lexinfo:singular . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Number:Sing\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_indic = PREFIXES + "INSERT { ?s lexinfo:verbFormMood lexinfo:indicative . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"VerbalMode:Indicatif\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_subj = PREFIXES + "INSERT { ?s lexinfo:verbFormMood lexinfo:subjonctive . }" +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"VerbalMode:Subjonctif\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) .}";
        String update_condit = PREFIXES + "INSERT { ?s lexinfo:verbFormMood lexinfo:conditional . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"VerbalMode:Conditionnel\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_infi = PREFIXES + "INSERT { ?s lexinfo:verbFormMood lexinfo:infinitive . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"Ver:Inf\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_pres = PREFIXES + "INSERT { ?s lexinfo:tense lexinfo:present . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"VerbalTime:Present\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_past = PREFIXES + "INSERT { ?s lexinfo:tense lexinfo:past .} " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"VerbalTime:Past\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        String update_futur = PREFIXES + "INSERT { ?s lexinfo:tense lexinfo:future . } " +
                "WHERE { ?s jdm:r_pos ?pos . " +
                "?pos rdfs:label \"VerbalTime:Future\" . " +
                "?s jdm:poids_relations ?pr . " +
                "?pr jdm:poids_r_pos ?rel . " +
                "?rel jdm:reference ?pos . " +
                "?rel jdm:val_poids ?poids . " +
                "FILTER(?poids > 0) . }";
        UpdateAction.parseExecute(update_nom, dataset);
        UpdateAction.parseExecute(update_verbe, dataset);
        UpdateAction.parseExecute(update_adv, dataset);
        UpdateAction.parseExecute(update_adj, dataset);
        UpdateAction.parseExecute(update_abr, dataset);
        UpdateAction.parseExecute(update_card, dataset);
        UpdateAction.parseExecute(update_det, dataset);
        UpdateAction.parseExecute(update_expr, dataset);
        UpdateAction.parseExecute(update_inter, dataset);
        UpdateAction.parseExecute(update_prep, dataset);
        UpdateAction.parseExecute(update_pref, dataset);
        UpdateAction.parseExecute(update_pro, dataset);
        UpdateAction.parseExecute(update_suff, dataset);
        UpdateAction.parseExecute(update_symb, dataset);
        UpdateAction.parseExecute(update_conj1, dataset);
        UpdateAction.parseExecute(update_conj2, dataset);
        UpdateAction.parseExecute(update_masc, dataset);
        UpdateAction.parseExecute(update_fem, dataset);
        UpdateAction.parseExecute(update_plur, dataset);
        UpdateAction.parseExecute(update_sing, dataset);
        UpdateAction.parseExecute(update_indic, dataset);
        UpdateAction.parseExecute(update_subj, dataset);
        UpdateAction.parseExecute(update_condit, dataset);
        UpdateAction.parseExecute(update_infi, dataset);
        UpdateAction.parseExecute(update_pres, dataset);
        UpdateAction.parseExecute(update_past, dataset);
        UpdateAction.parseExecute(update_past, dataset);
        UpdateAction.parseExecute(update_futur, dataset);
    }

    private static List<String> removeStopWords(final Iterable<String> text, final Collection<String> stop) {
        final List<String> res = new LinkedList<String>();
        for (final String word : text) {
            final String wordCompare = word.toLowerCase();
            if (!stop.contains(wordCompare)) {
                res.add(word);
            }
        }
        return res;
    }

    public static void main(final String... args) throws IOException, InvalidBabelSynsetIDException {
        final String filename = "C:/Users/Gundenspand/Desktop/Tout/Travail/Stage/Partie pratique/Outils/JDMExtractor/JDM-RDF-compact-UTF8.ttl";
        //Fichier trop gros pour charger les donn�es en m�moire. Cr�ation de la base tdb.
        final Dataset dataset = TDBFactory.createDataset("storage");
        final Model tdb = dataset.getDefaultModel();
        FileManager.get().readModel(tdb, filename);
        dataset.begin(ReadWrite.WRITE);
        update(dataset);
        //Fichier peut �tre charg� en m�moire.
        //FileManager.get().addLocatorClassLoader(JDMQueries.class.getClassLoader());
        //tdb = FileManager.get().loadModel(filename, null, "TURTLE");
        //Chargement de la base tdb (base d�j� existante).
        //dataset = TDBFactory.createDataset("storage");
        //tdb = dataset.getDefaultModel();
        //dataset.begin(ReadWrite.WRITE);
        //Fin cr�ation / chargement JDM
        //Chargement BabelNet
        final BabelNet bn = BabelNet.getInstance();
        //Initialisation des outils pour lemmatiser et supprimer les stop words.
        final LemmatizerImpl lemmat = new LemmatizerImpl();
        final Collection<String> stopWordsSet = new HashSet<>();
        final FileReader fr = new FileReader("./stopwords.txt");
        final BufferedReader br = new BufferedReader(fr);
        String CurrentLine;
        while (null != (CurrentLine = br.readLine())) {
            stopWordsSet.add(CurrentLine);
        }
        br.close();
        System.out.println("Fin chargements.");
        //On s�l�ctionne d'abord les racines des mots polys�miques dans JDM.
        final String query_polysemie_JDM = PREFIXES + "SELECT DISTINCT ?mot ?wr WHERE { " +
                "?mot ontolex:sense ?raff1 . " +
                "?mot ontolex:sense ?raff2 . " +
                "?mot ontolex:canonicalForm ?cf ." +
                "?cf ontolex:writtenRep ?wr ." +
                "?raff1 lexinfo:partOfSpeech lexinfo:noun . " +
                "FILTER( ?raff1 != ?raff2 ) . " +
                "MINUS { ?pere ontolex:sense ?mot } " +
                "MINUS { ?morph jdm:r_raff_morpho ?mot } } ";
        //Creation du fichier resultat
        try {
            try (PrintWriter writer = new PrintWriter("resultats.csv", "UTF-8")) {
                writer.println("idversion,idJDM,idBN,valeurComparaison,repJDM,sensBN,defBN");
                final Query query_polysemie = QueryFactory.create(query_polysemie_JDM);
                int nbJDM = 0, nbBN = 0, nbraff = 0;
                try (QueryExecution qexec = QueryExecutionFactory.create(query_polysemie, dataset)) {
                    final ResultSet results = qexec.execSelect();
                    while (results.hasNext()) {
                        final QuerySolution mots_polysemiques = results.nextSolution();
                        final RDFNode mot = mots_polysemiques.get("?mot");
                        final String rep_ecrite = WRITTEN_REPREPSENTATION_SEPARATOR.split(mots_polysemiques.getLiteral("?wr").toString())[0];
                        //On rajoute la representation ecrite (si mot unique) aux stopwords pour r�duire le bruit. On l'enl�ve � la fin de la boucle.
                        if (!rep_ecrite.contains(" ")) {
                            stopWordsSet.add(rep_ecrite);
                        }
                        //On recup�re l'id de mot
                        final String id_mot = mot.toString().split("#")[1];
                        final List<MotCompare> bMots = new ArrayList<>();
                        final List<BabelSynset> byl = bn.getSynsets(rep_ecrite, Language.FR, BabelPOS.NOUN
                                , BabelSenseSource.WN, BabelSenseSource.OMWN, BabelSenseSource.WONEF, BabelSenseSource.WIKI,
                                BabelSenseSource.WIKIDATA, BabelSenseSource.OMWIKI, BabelSenseSource.WIKIRED, BabelSenseSource.WIKT);
                        int b_index = 0;
                        for (final BabelSynset synact : byl) {
                            if (synact != null) {
                                final String id_bn = synact.getId().toString().split(":")[1];
                                bMots.add(new MotCompare());
                                bMots.get(b_index).setId_bn(id_bn);
                                bMots.get(b_index).setSens_princip(synact.getMainSense(Language.FR).toString());
                                //On cr�e des mesures pour savoir si les termes sont bien renseign�s.
                                int renseign_bn = 0;
                                //On cr�e un sac de mot avec un multiplicateur
                                final Map<String, Double> sacMots = new HashMap<>();
                                final List<BabelSense> bSenses = synact.getSenses(Language.FR);
                                final StringBuilder fulltext = new StringBuilder();
                                for (final BabelSense bSense : bSenses) {
                                    final String b_sens = bSense.getSimpleLemma();
                                    fulltext.append(b_sens).append(" ");
                                    final List<String> senseBDefinitionWords = removeStopWords(lemmat.lemmatize(b_sens), stopWordsSet);
                                    for (final String targetSenseDefinitionWord : senseBDefinitionWords) {
                                        if (sacMots.containsKey(targetSenseDefinitionWord)) {
                                            sacMots.put(targetSenseDefinitionWord, sacMots.get(targetSenseDefinitionWord) + DEFINITION_OVERLAPPING_UPDATE_WEIGHT);
                                        } else {
                                            sacMots.put(targetSenseDefinitionWord, DEFINITION_NON_OVERLAPPING_UPDATE_WEIGHT);
                                        }
                                        renseign_bn++;
                                    }
                                }
                                final List<BabelGloss> b_glosses = synact.getGlosses(Language.FR);
                                for (int j = 0; j < b_glosses.size(); j++) {
                                    final String b_glosse = b_glosses.get(j).getGloss();
                                    fulltext.append(b_glosses).append(" ");
                                    final List<String> mots_glosse_b = removeStopWords(lemmat.lemmatize(b_glosse), stopWordsSet);
                                    for (final String mot_res : mots_glosse_b) {
                                        if (sacMots.containsKey(mot_res)) {
                                            sacMots.put(mot_res, sacMots.get(mot_res) + GLOSSES_OVERLAPPING_UPDATE_WEIGHT);
                                        } else {
                                            sacMots.put(mot_res, GLOSSES_NON_OVERLAPPING_UPDATE_WEIGHT);
                                        }
                                        renseign_bn++;
                                    }
                                }
                                if (!b_glosses.isEmpty()) {
                                    bMots.get(b_index).setDef(synact.getMainGloss(Language.FR).getGloss());
                                }
                                final List<BabelCategory> b_categs = synact.getCategories(Language.FR);
                                for (final BabelCategory b_categ1 : b_categs) {
                                    final String b_categ = b_categ1.getCategory();
                                    fulltext.append(b_categ).append(" ");
                                    final List<String> mots_categ_b = removeStopWords(lemmat.lemmatize(b_categ), stopWordsSet);
                                    for (final String mot_res : mots_categ_b) {
                                        if (sacMots.containsKey(mot_res)) {
                                            sacMots.put(mot_res, sacMots.get(mot_res) + 1.5);
                                        } else {
                                            sacMots.put(mot_res, 2.5);
                                        }
                                        renseign_bn++;
                                    }
                                }
                                final List<BabelExample> b_examples = synact.getExamples(Language.FR);
                                for (final BabelExample b_example : b_examples) {
                                    final String b_ex = b_example.getExample();
                                    fulltext.append(b_ex).append(" ");
                                    final List<String> mots_ex_b = removeStopWords(lemmat.lemmatize(b_ex), stopWordsSet);
                                    for (final String mot_res : mots_ex_b) {
                                        if (sacMots.containsKey(mot_res)) {
                                            sacMots.put(mot_res, sacMots.get(mot_res) + 0.75);
                                        } else {
                                            sacMots.put(mot_res, 1.75);
                                        }
                                        renseign_bn++;
                                    }
                                }
                                //On rentre ensuite les donn�es dans le BabelMot, qu'on normalise par la longueur.
                                final Set<Entry<String, Double>> setSM = sacMots.entrySet();
                                for (final Entry<String, Double> act : setSM) {
                                    double newVal = act.getValue() - 1.0;
                                    newVal /= renseign_bn;
                                    newVal += 1.0;
                                    bMots.get(b_index).put(act.getKey(), newVal);
                                }
                                bMots.get(b_index).setFulltext(fulltext.toString());
                                bMots.get(b_index).setRenseignBN(renseign_bn);
                                b_index++;
                                nbBN++;
                            }
                        }
                        //On selectionne les raffinements
                        final String query_raff = PREFIXES + "Select ?raff ?label where { jdm:" + id_mot + " ontolex:sense ?raff . ?raff rdfs:label ?label . }";
                        try (QueryExecution qexec_raff = QueryExecutionFactory.create(query_raff, dataset)) {
                            final ResultSet results_raff = qexec_raff.execSelect();
                            while (results_raff.hasNext()) {
                                //On cr�e une valeur pour comparer les mesures de similarit�
                                final int[] valeur_comparaison = new int[bMots.size()];
                                for (int bm = 0; bm < bMots.size(); bm++) {
                                    valeur_comparaison[bm] = 0;
                                }
                                final QuerySolution result_raff = results_raff.nextSolution();
                                final RDFNode raff = result_raff.get("?raff");
                                final String id_raff = raff.toString().split("#")[1];
                                final String rep_raff = result_raff.get("?label").toString();
                                final String query_assoc = PREFIXES + "Select ?wr ?poids where { jdm:" + id_raff + " jdm:r_associated ?asso . ?asso ontolex:canonicalForm ?cf ." +
                                        "?cf ontolex:writtenRep ?wr . jdm:" + id_raff + " jdm:poids_relations ?npoids . ?npoids jdm:poids_r_associated ?rel ." +
                                        "?rel jdm:val_poids ?poids . ?rel jdm:reference ?asso . } LIMIT 100"; //Limit pour �viter les >75000 relations d'association
                                int renseign_jdm = 0;
                                try (QueryExecution qexec_assoc = QueryExecutionFactory.create(query_assoc, dataset)) {
                                    final ResultSet results_assoc = qexec_assoc.execSelect();
                                    while (results_assoc.hasNext()) {
                                        final QuerySolution result_assoc = results_assoc.nextSolution();
                                        final String mot_assoc = result_assoc.getLiteral("?wr").toString().split("[>@]")[0];
                                        final int poids_assoc = result_assoc.getLiteral("?poids").getInt();
                                        for (int bm = 0; bm < bMots.size(); bm++) {
                                            final Set<Entry<String, Double>> setSM = bMots.get(bm).SacMots.entrySet();
                                            for (final Entry<String, Double> act : setSM) {
                                                if (mot_assoc.equals(act.getKey())) {
                                                    valeur_comparaison[bm] += act.getValue() * poids_assoc;
                                                }
                                            }
                                            //cas des mots compos�s
                                            if (mot_assoc.contains(" ") && bMots.get(bm).getFulltext().contains(mot_assoc)) {
                                                valeur_comparaison[bm] += poids_assoc;
                                            }
                                        }
                                        renseign_jdm++;
                                    }

                                }
                                //Inhibition sur des mots.
                                final String query_inhib_mot = PREFIXES + "Select ?wr ?poids where { ?inhib jdm:r_inhib jdm:" + id_raff + " . " +
                                        "?inhib jdm:poids_relations ?npoids . ?npoids jdm:poids_r_inhib ?rel .?rel jdm:val_poids ?poids . " +
                                        "?rel jdm:reference jdm:" + id_raff + " . ?inhib ontolex:canonicalForm ?cf . ?cf ontolex:writtenRep ?wr . }";
                                try (QueryExecution qexec_inhib_mot = QueryExecutionFactory.create(query_inhib_mot, dataset)) {
                                    final ResultSet results_inhib_mot = qexec_inhib_mot.execSelect();
                                    while (results_inhib_mot.hasNext()) {
                                        final QuerySolution result_inhib_mot = results_inhib_mot.nextSolution();
                                        final String mot_inhib = result_inhib_mot.getLiteral("?wr").toString().split("[>@]")[0];
                                        final int poids_inhib = result_inhib_mot.getLiteral("?poids").getInt();
                                        for (int bm = 0; bm < bMots.size(); bm++) {
                                            Set<Entry<String, Double>> setSM = bMots.get(bm).SacMots.entrySet();
                                            Iterator<Entry<String, Double>> it = setSM.iterator();
                                            while (it.hasNext()) {
                                                Entry<String, Double> act = it.next();
                                                if (mot_inhib.equals(act.getKey())) {
                                                    valeur_comparaison[bm] = (int) (act.getValue() * poids_inhib * -10);
                                                    //System.out.println(mot_inhib+" "+rep_raff+" "+bMots.get(bm).getSens_princip()+" "+poids_inhib);							    			}
                                                }
                                            }
                                            //cas des mots compos�s
                                            if (mot_inhib.contains(" ") && bMots.get(bm).getFulltext().contains(mot_inhib)) {
                                                valeur_comparaison[bm] += poids_inhib * -10;
                                            }
                                        }
                                        renseign_jdm++;
                                    }
                                }
                                for (int bm = 0; bm < bMots.size(); bm++) {
                                    bMots.get(bm).addJDMC(id_raff, renseign_jdm, valeur_comparaison[bm], rep_raff);
                                }
                                nbraff++;
                            }
                        }
                        for (final MotCompare bMot : bMots) {
                            for (int jdmc = 0; jdmc < bMot.getJDMCsize(); jdmc++) {
                                final JDMCompare jdmc_act = bMot.getJDMC(jdmc);
                                if (jdmc_act.getComparisonValue() > 1000) {
                                    //idversion,idJDM,idBN,valeurComparaison,repJDM,sensBN,defBN
                                    writer.println("2," + jdmc_act.getIdJdm() + "," + bMot.getId_bn() + "," + jdmc_act.getComparisonValue() + ","
                                            + jdmc_act.getRep() + "," + bMot.getSens_princip());//+","+bMots.get(bm).getDef());
                                    writer.flush();
                                }
                            }
                        }
                        if ((nbJDM % 100) == 0) {
                            System.out.println(">>>>>>>>>>>>>>Nombre de mots polys�miques trait�s : " + nbJDM);
                        }
                        stopWordsSet.remove(rep_ecrite);
                        nbJDM++;
                    }
                }
                System.out.println("Nombre mots polys�miques JDM : " + nbJDM);
                System.out.println("Nombre raffinements JDM : " + nbraff);
                System.out.println("Nombre mots matchant BN : " + nbBN);
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Impossible d'ouvrir le fichier resultats.");
        }
        dataset.commit();
        tdb.close();
        dataset.close();
    }
}
