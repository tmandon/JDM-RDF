package jenaJDM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.util.FileManager;

import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.InvalidBabelSynsetIDException;
import it.uniroma1.lcl.babelnet.data.BabelCategory;
import it.uniroma1.lcl.babelnet.data.BabelExample;
import it.uniroma1.lcl.babelnet.data.BabelGloss;
import it.uniroma1.lcl.babelnet.data.BabelPOS;
import it.uniroma1.lcl.babelnet.data.BabelSenseSource;
import it.uniroma1.lcl.jlt.util.Language;

public class JDM_queries {

	public static void miseajour(Dataset dataset)
	{
		String prefixes = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  " +
		"prefix lime:	<http://www.w3.org/ns/lemon/lime#> " +
		"prefix ontolex: <http://www.w3.org/ns/ontolex#> " +
		"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
		"prefix jdm: <http://www.jeuxdemots.org/data#> " +
		"prefix lexinfo: <http://www.lexinfo.net/ontology/2.0/lexinfo#>";
		String update_sense = prefixes + "DELETE { ?o rdf:type ontolex:lexicalEntry . ?o lime:language \"fr\" . ?o ontolex:canonicalForm ?form . "+
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
		String update_lemma = prefixes + "INSERT { ?o ontolex:otherForm ?s . } "+
		"WHERE  { ?s jdm:r_lemma ?o . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_lemma ?rel . "+
		"?rel jdm:reference ?o . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . "+
		"FILTER(?s != ?o) }";
		UpdateAction.parseExecute(update_lemma, dataset);
		String update_nom = prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:noun . } WHERE { " +
	    "?pos rdfs:label \"Nom:\" . "+
	    "?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . " +
		"?rel jdm:reference ?pos . " +
		"?rel jdm:val_poids ?poids . " +
		"FILTER(?poids > 0) . }";
		String update_verbe = prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:verb . } WHERE { "+
	    "?pos rdfs:label \"Ver:\" . " +
		"?s jdm:poids_relations ?pr . " +
		"?pr jdm:poids_r_pos ?rel . " +
		"?rel jdm:reference ?pos . " +
		"?rel jdm:val_poids ?poids . " +
		"FILTER(?poids > 0) . }";
		String update_adv = prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:adverb . } WHERE { "+
	    "?pos rdfs:label \"Adv:\" . " +
		"?s jdm:poids_relations ?pr . " +
		"?pr jdm:poids_r_pos ?rel . " +
		"?rel jdm:reference ?pos . " +
		"?rel jdm:val_poids ?poids . " +
		"FILTER(?poids > 0) . }";
		String update_adj = prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:adjective . } WHERE { " +
	    "?pos rdfs:label \"Adj:\" . " +
		"?s jdm:poids_relations ?pr . " +
		"?pr jdm:poids_r_pos ?rel . " +
		"?rel jdm:reference ?pos . " +
		"?rel jdm:val_poids ?poids . " +
		"FILTER(?poids > 0) . }";
		String update_abr = prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:abbreviation . } " +
		"WHERE { ?s jdm:r_pos ?pos . " +
		"?pos rdfs:label \"Abr:\" . " +
		"?s jdm:poids_relations ?pr . " +
		"?pr jdm:poids_r_pos ?rel . " +
		"?rel jdm:reference ?pos . " +
		"?rel jdm:val_poids ?poids . " +
		"FILTER(?poids > 0) . }";
		String update_card = prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:cardinalNumeral . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Card:\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }";
		String update_det = prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:determiner . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Det:\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }";
		String update_expr = prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:expression . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Expression:\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }";
		String update_inter = prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:interjection . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Int:\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }";
		String update_prep = prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:preposition . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Pre:\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }";
		String update_pref = prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:prefix .} "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Prefix:\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }";
		String update_pro = prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:pronoun . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Pro:\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }";
		String update_suff =prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:suffix . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Suffix:\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }"; 
		String update_symb =prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:symbol . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Symbole:\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }"; 
		String update_conj1 = prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:Conjunction . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Conj:\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . } ";
		String update_conj2 = prefixes + "INSERT { ?s lexinfo:partOfSpeech lexinfo:Conjunction . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Con:\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . "+
		"MINUS { ?s jdm:r_pos \"Con:\" . "+
		"?pos2 rdfs:label  \"Conj:\" . } }";
		String update_masc = prefixes + "INSERT { ?s lexinfo:gender lexinfo:masculine . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Gender:Mas\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) .}"; 
		String update_fem = prefixes + "INSERT { ?s lexinfo:gender lexinfo:feminine . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Gender:Fem\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }";
		String update_plur = prefixes + "INSERT { ?s lexinfo:number lexinfo:plural .} "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Number:Plur\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }";
		String update_sing = prefixes + "INSERT { ?s lexinfo:number lexinfo:singular . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Number:Sing\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }";
		String update_indic = prefixes + "INSERT { ?s lexinfo:verbFormMood lexinfo:indicative . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"VerbalMode:Indicatif\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }";
		String update_subj = prefixes + "INSERT { ?s lexinfo:verbFormMood lexinfo:subjonctive . }"+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"VerbalMode:Subjonctif\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) .}"; 
		String update_condit = prefixes + "INSERT { ?s lexinfo:verbFormMood lexinfo:conditional . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"VerbalMode:Conditionnel\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }";
		String update_infi = prefixes + "INSERT { ?s lexinfo:verbFormMood lexinfo:infinitive . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"Ver:Inf\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }";
		String update_pres = prefixes + "INSERT { ?s lexinfo:tense lexinfo:present . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"VerbalTime:Present\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }";
		String update_past = prefixes + "INSERT { ?s lexinfo:tense lexinfo:past .} "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"VerbalTime:Past\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
		"FILTER(?poids > 0) . }"; 
		String update_futur = prefixes + "INSERT { ?s lexinfo:tense lexinfo:future . } "+
		"WHERE { ?s jdm:r_pos ?pos . "+
		"?pos rdfs:label \"VerbalTime:Future\" . "+
		"?s jdm:poids_relations ?pr . "+
		"?pr jdm:poids_r_pos ?rel . "+
		"?rel jdm:reference ?pos . "+
		"?rel jdm:val_poids ?poids . "+
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
	
	public static List<String> removestopwords(List<String> text, Set<String> stop)
	{
		List<String> res = new LinkedList<String>();
		for(String word : text)
		{
			String wordCompare = word.toLowerCase();
	        	if(!stop.contains(wordCompare))
	        	{
	        		res.add(word);
	        	}
		}
		return res;
	}
	
	public static void main(String[] args) throws IOException, InvalidBabelSynsetIDException {
		Model tdb;
		Dataset dataset;
		String filename = "C:/Users/Gundenspand/Desktop/Tout/Travail/Stage/Partie pratique/Outils/JDMExtractor/JDM-RDF-compact-UTF8.ttl";
		//Fichier trop gros pour charger les données en mémoire. Création de la base tdb.
		dataset = TDBFactory.createDataset("storage");
		tdb = dataset.getDefaultModel();
		FileManager.get().readModel(tdb, filename);
		dataset.begin(ReadWrite.WRITE);
		miseajour(dataset);
		//Fichier peut être chargé en mémoire.
		//FileManager.get().addLocatorClassLoader(JDM_queries.class.getClassLoader());
		//tdb = FileManager.get().loadModel(filename, null, "TURTLE");
		//Chargement de la base tdb (base déjà existante).
		//dataset = TDBFactory.createDataset("storage");
		//tdb = dataset.getDefaultModel();
		//dataset.begin(ReadWrite.WRITE);
		//Fin création / chargement JDM
		//Chargement BabelNet
		BabelNet bn = BabelNet.getInstance();
		//Initialisation des outils pour lemmatiser et supprimer les stop words.
		Lemmatizer lemmat = new Lemmatizer();
		Set<String> stopWordsSet = new HashSet<String>();
		FileReader fr=new FileReader("./stopwords.txt");
		BufferedReader br= new BufferedReader(fr);
		String CurrentLine;
		while ((CurrentLine = br.readLine()) != null){
			stopWordsSet.add(CurrentLine);
		}
		br.close();
		System.out.println("Fin chargements.");
		//On séléctionne d'abord les racines des mots polysémiques dans JDM.
		String prefixes = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  " +
		"prefix lime:	<http://www.w3.org/ns/lemon/lime#> " +
		"prefix ontolex: <http://www.w3.org/ns/ontolex#> " +
		"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
		"prefix jdm: <http://www.jeuxdemots.org/data#> " +
		"prefix lexinfo: <http://www.lexinfo.net/ontology/2.0/lexinfo#> " +
		"prefix owl: <http://www.w3.org/2002/07/owl#> ";
		String query_polysemie_JDM = prefixes + "SELECT DISTINCT ?mot ?wr WHERE { "+
		"?mot ontolex:sense ?raff1 . "+
		"?mot ontolex:sense ?raff2 . "+
		"?mot ontolex:canonicalForm ?cf ."+
		"?cf ontolex:writtenRep ?wr ."+
		"?raff1 lexinfo:partOfSpeech lexinfo:noun . "+
		"FILTER( ?raff1 != ?raff2 ) . "+
		"MINUS { ?pere ontolex:sense ?mot } "+
		"MINUS { ?morph jdm:r_raff_morpho ?mot } } ";
		//Creation du fichier resultat
		try{
		    PrintWriter writer = new PrintWriter("resultats.csv", "UTF-8");
		    writer.println("idversion,idJDM,idBN,valeurComparaison,repJDM,sensBN,defBN");
			Query query_polysemie = QueryFactory.create(query_polysemie_JDM);
			int nbJDM=0, nbBN=0, nbraff=0;
			try (QueryExecution qexec = QueryExecutionFactory.create(query_polysemie, dataset)) {
			    ResultSet results = qexec.execSelect() ;
			    for ( ; results.hasNext() ; )
			    {
			    	QuerySolution mots_polysemiques = results.nextSolution();
			      	RDFNode mot = mots_polysemiques.get("?mot");
			      	String rep_ecrite = mots_polysemiques.getLiteral("?wr").toString().split("[>@]")[0];
			      	//On rajoute la representation ecrite (si mot unique) aux stopwords pour réduire le bruit. On l'enlève à la fin de la boucle.
			      	if(!rep_ecrite.contains(" "))
			      	{
			      		stopWordsSet.add(rep_ecrite);
			      	}
			      	//On recupère l'id de mot
			      	String id_mot = mot.toString().split("#")[1];
			      	ArrayList<MotCompare> BMots = new ArrayList<MotCompare>();
			      	List<BabelSynset> byl = bn.getSynsets(rep_ecrite,Language.FR,BabelPOS.NOUN
				    ,BabelSenseSource.WN,BabelSenseSource.OMWN,BabelSenseSource.WONEF,BabelSenseSource.WIKI,
				    BabelSenseSource.WIKIDATA,BabelSenseSource.OMWIKI,BabelSenseSource.WIKIRED,BabelSenseSource.WIKT);
					int b_index=0;
					for (int byl_i=0;byl_i<byl.size();byl_i++)
					{
					   	BabelSynset synact = byl.get(byl_i);
					   	if(synact!=null)
					   	{
					   		String fulltext ="";
					   		String id_bn = synact.getId().toString().split(":")[1];
					   		BMots.add(new MotCompare());
					   		BMots.get(b_index).setId_bn(id_bn);
					   		BMots.get(b_index).setSens_princip(synact.getMainSense(Language.FR).toString());
					   		//On crée des mesures pour savoir si les termes sont bien renseignés.
					   		int renseign_bn=0;
					   		//On crée un sac de mot avec un multiplicateur
					   		Map<String, Double> SacMots = new HashMap<>();
					   		List<BabelSense> b_senses = synact.getSenses(Language.FR);
					   		for(int j=0;j<b_senses.size();j++)
					   		{
					   			String b_sens = b_senses.get(j).getSimpleLemma();
					   			fulltext += b_sens +" ";
					   			List<String> mots_sens_b = removestopwords(lemmat.lemmatize(b_sens),stopWordsSet);
					   			for(String mot_res : mots_sens_b)
					   			{
					   				if(!SacMots.containsKey(mot_res))
					   				{
					   					SacMots.put(mot_res, 3.0);
					   				} else {
					   					SacMots.put(mot_res,SacMots.get(mot_res)+2.0);
					   				}
					   				renseign_bn++;
					   			}
					   		}
					   		List<BabelGloss> b_glosses = synact.getGlosses(Language.FR);
					   		for(int j=0;j<b_glosses.size();j++)
					   		{
					  			String b_glosse = b_glosses.get(j).getGloss();
					  			fulltext += b_glosses +" ";
					   			List<String> mots_glosse_b = removestopwords(lemmat.lemmatize(b_glosse),stopWordsSet);
					   			for(String mot_res : mots_glosse_b)
					   			{
					   				if(!SacMots.containsKey(mot_res))
					   				{
					   					SacMots.put(mot_res, 2.5);
					   				} else {
					   					SacMots.put(mot_res,SacMots.get(mot_res)+1.5);
					   				}
					   				renseign_bn++;
					   			}    			
					   		}
					   		if(b_glosses.size()>0)
					   		{
					   			BMots.get(b_index).setDef(synact.getMainGloss(Language.FR).getGloss());
					   		}
					   		List<BabelCategory> b_categs = synact.getCategories(Language.FR);
						    for(int j=0;j<b_categs.size();j++)
						    {
						    	String b_categ = b_categs.get(j).getCategory();
						    	fulltext += b_categ +" ";
						    	List<String> mots_categ_b = removestopwords(lemmat.lemmatize(b_categ),stopWordsSet);
						    	for(String mot_res : mots_categ_b)
						    	{
						    		if(!SacMots.containsKey(mot_res))
						    		{
						    			SacMots.put(mot_res, 2.5);
						    		} else {
						    			SacMots.put(mot_res,SacMots.get(mot_res)+1.5);
						    		}
						    		renseign_bn++;
						    	}
						    }
				      		List<BabelExample> b_examples = synact.getExamples(Language.FR);
				      		for(int j=0;j<b_examples.size();j++)
				      		{
				      			String b_ex = b_examples.get(j).getExample();
				      			fulltext += b_ex +" ";
				      			List<String> mots_ex_b = removestopwords(lemmat.lemmatize(b_ex),stopWordsSet);
				      			for(String mot_res : mots_ex_b)
				      			{
				      				if(!SacMots.containsKey(mot_res))
				      				{
				      					SacMots.put(mot_res, 1.75);
				      				} else {
				      					SacMots.put(mot_res,SacMots.get(mot_res)+0.75);
				      				}
				      				renseign_bn++;
				      			}
				      		}
				      		//On rentre ensuite les données dans le BabelMot, qu'on normalise par la longueur.
				      		Set<Entry<String, Double>> setSM = SacMots.entrySet();
				      		Iterator<Entry<String, Double>> it_cap = setSM.iterator();									      		
				      		while(it_cap.hasNext())
				      		{
				      			Entry<String, Double> act = it_cap.next();
				      			double newVal = act.getValue() - 1.0;
				      			newVal = newVal / renseign_bn;
				      			newVal += 1.0;
				      			BMots.get(b_index).put(act.getKey(), newVal);
				      		}
				      		BMots.get(b_index).setFulltext(fulltext);
				      		BMots.get(b_index).setRenseignBN(renseign_bn);
				      		b_index++;
				      		nbBN++;
			    		}
					}
					//On selectionne les raffinements
				    String query_raff = prefixes + "Select ?raff ?label where { jdm:"+id_mot+" ontolex:sense ?raff . ?raff rdfs:label ?label . }";
		      		try (QueryExecution qexec_raff = QueryExecutionFactory.create(query_raff, dataset)) {
					    ResultSet results_raff = qexec_raff.execSelect() ;
					    for ( ; results_raff.hasNext() ; )
					    {
					    	int renseign_jdm=0;
					    	//On crée une valeur pour comparer les mesures de similarité
					    	int[] valeur_comparaison = new int[BMots.size()];
					    	for(int bm = 0;bm<BMots.size();bm++)
				      		{
					    		valeur_comparaison[bm]=0;
				      		}
					    	QuerySolution result_raff = results_raff.nextSolution();
					    	RDFNode raff = result_raff.get("?raff");
					      	String id_raff = raff.toString().split("#")[1];
					      	String rep_raff = result_raff.get("?label").toString();
				      		String query_assoc = prefixes + "Select ?wr ?poids where { jdm:"+id_raff+" jdm:r_associated ?asso . ?asso ontolex:canonicalForm ?cf ."+
					      	"?cf ontolex:writtenRep ?wr . jdm:"+id_raff+" jdm:poids_relations ?npoids . ?npoids jdm:poids_r_associated ?rel ."+
					      	"?rel jdm:val_poids ?poids . ?rel jdm:reference ?asso . } LIMIT 100"; //Limit pour éviter les >75000 relations d'association
			    			try (QueryExecution qexec_assoc = QueryExecutionFactory.create(query_assoc, dataset))
			    			{
							    ResultSet results_assoc = qexec_assoc.execSelect() ;
							    for ( ; results_assoc.hasNext() ; )
							    {
							    	QuerySolution result_assoc = results_assoc.nextSolution();
							    	String mot_assoc = result_assoc.getLiteral("?wr").toString().split("[>@]")[0];
							    	int poids_assoc = result_assoc.getLiteral("?poids").getInt();
							    	for(int bm = 0;bm<BMots.size();bm++)
						      		{
							    		Set<Entry<String, Double>> setSM = BMots.get(bm).SacMots.entrySet();
							    		Iterator<Entry<String, Double>> it = setSM.iterator();
							    		while(it.hasNext())
							    		{
							    			Entry<String, Double> act = it.next();
							    			if(mot_assoc.equals(act.getKey()))
							    			{
							    				valeur_comparaison[bm] += act.getValue()*poids_assoc;
							    			}
							    		}
							    		//cas des mots composés
							    		if(mot_assoc.contains(" ")&&BMots.get(bm).getFulltext().contains(mot_assoc))
							    		{
							    			valeur_comparaison[bm] += poids_assoc;
							    		}
						      		}
						      		renseign_jdm++;
							    }
							   
			    			}
			    			//Inhibition sur des mots.
			    			String query_inhib_mot = prefixes + "Select ?wr ?poids where { ?inhib jdm:r_inhib jdm:"+id_raff+" . "+
					      	"?inhib jdm:poids_relations ?npoids . ?npoids jdm:poids_r_inhib ?rel .?rel jdm:val_poids ?poids . "+
					      	"?rel jdm:reference jdm:"+id_raff+" . ?inhib ontolex:canonicalForm ?cf . ?cf ontolex:writtenRep ?wr . }";
			    			try (QueryExecution qexec_inhib_mot = QueryExecutionFactory.create(query_inhib_mot, dataset))
			    			{
							    ResultSet results_inhib_mot = qexec_inhib_mot.execSelect() ;
							    for ( ; results_inhib_mot.hasNext() ; )
							    {
							    	QuerySolution result_inhib_mot = results_inhib_mot.nextSolution();
							    	String mot_inhib = result_inhib_mot.getLiteral("?wr").toString().split("[>@]")[0];
							    	int poids_inhib = result_inhib_mot.getLiteral("?poids").getInt();
							    	for(int bm = 0;bm<BMots.size();bm++)
						      		{
							    		Set<Entry<String, Double>> setSM = BMots.get(bm).SacMots.entrySet();
							    		Iterator<Entry<String, Double>> it = setSM.iterator();
							    		while(it.hasNext())
							    		{
							    			Entry<String, Double> act = it.next();
							    			if(mot_inhib.equals(act.getKey()))
							    			{
							    				valeur_comparaison[bm] = (int) (act.getValue()*poids_inhib*-10);
							    				//System.out.println(mot_inhib+" "+rep_raff+" "+BMots.get(bm).getSens_princip()+" "+poids_inhib);							    			}
							    			}
							    		}
							    		//cas des mots composés
							    		if(mot_inhib.contains(" ")&&BMots.get(bm).getFulltext().contains(mot_inhib))
							    		{
							    			valeur_comparaison[bm] += poids_inhib*-10;
							    		}
						      		}
							    	renseign_jdm++;
							    }
			    			}
			    			for(int bm = 0;bm<BMots.size();bm++)
			    			{
			    				BMots.get(bm).addJDMC(id_raff, renseign_jdm, valeur_comparaison[bm], rep_raff);
			    			}
			    			nbraff++;
			    		}
					}
		      		for(int bm = 0;bm<BMots.size();bm++)
		      		{
		      			for(int jdmc = 0; jdmc<BMots.get(bm).getJDMCsize();jdmc++)
		      			{
		      				JDMCompare jdmc_act = BMots.get(bm).getJDMC(jdmc);
		      				if(jdmc_act.getValeur_comparaison()>1000)
		      				{
		      					//idversion,idJDM,idBN,valeurComparaison,repJDM,sensBN,defBN
		      					writer.println("2,"+jdmc_act.getId_JDM()+","+BMots.get(bm).getId_bn()+","+jdmc_act.getValeur_comparaison()+","
		      					+jdmc_act.getRep()+","+BMots.get(bm).getSens_princip());//+","+BMots.get(bm).getDef());
		      					writer.flush();
		      				}
		      			}
		      		}
		      		if(nbJDM%100==0)
			      	{
			      		System.out.println(">>>>>>>>>>>>>>Nombre de mots polysémiques traités : "+nbJDM);
			      	}
			      	stopWordsSet.remove(rep_ecrite);
			      	nbJDM++;
			    }
			}
			System.out.println("Nombre mots polysémiques JDM : "+nbJDM);
			System.out.println("Nombre raffinements JDM : "+nbraff);
			System.out.println("Nombre mots matchant BN : "+nbBN);
			writer.close();
		} catch (IOException e) {
			   System.out.println("Impossible d'ouvrir le fichier resultats.");
		}
		dataset.commit();
		tdb.close();
		dataset.close();
	}
}
