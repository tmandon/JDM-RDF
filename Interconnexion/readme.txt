Le code java est principalement dans la classe JDM_queries. Pour le faire marcher, il faut avoir acc�s � BabelNet, et � son API (si possible en illimit� pour �viter que le programme s'arr�te au bout de 1000 requ�tes vers BabelNet). On utilise StandfordNLP pour lemmatiser.

Le fichier liens500Inhib contient les liens cr��s par notre programme en utilisant 500 comme seuil et la relation d'inhibition pour r�duire le seuil. Le fichier benchmark.txt contient le benchmark que nous avons cr��. Le fichier total.csv contient l'ensemble des liens possibles, il est utilis� pour calculer les vrais n�gatifs.

Plus d'informations sur l'API BabelNet : http://babelnet.org/guide/
Plus d'informations sur stanford NLP : https://stanfordnlp.github.io/CoreNLP/