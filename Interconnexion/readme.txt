Le code java est principalement dans la classe JDM_queries. Pour le faire marcher, il faut avoir accès à BabelNet, et à son API (si possible en illimité pour éviter que le programme s'arrête au bout de 1000 requêtes vers BabelNet). On utilise StandfordNLP pour lemmatiser.

Le fichier liens500Inhib contient les liens créés par notre programme en utilisant 500 comme seuil et la relation d'inhibition pour réduire le seuil. Le fichier benchmark.txt contient le benchmark que nous avons créé. Le fichier total.csv contient l'ensemble des liens possibles, il est utilisé pour calculer les vrais négatifs.

Plus d'informations sur l'API BabelNet : http://babelnet.org/guide/
Plus d'informations sur stanford NLP : https://stanfordnlp.github.io/CoreNLP/