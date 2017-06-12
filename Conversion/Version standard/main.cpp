#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <vector>
#include <unordered_map>
#include <algorithm>

#include "noeud.h"

using namespace std;

int main(int argc, char** argv)
{
    //Parser et remplissage en mémoire
    //Le programme prends en compte deux paramètres : le nom du fichier dump (sans le .txt) et l'id max des noeuds de JDM.
    string dump_name=argv[1];
    //Comme on a eu des problèmes avec l'id max des noeuds qui n'était pas correcte, on la revoit à la hausse.
    unsigned long int taille_noeuds=stoul(argv[2])*1.05;
    //On créer un vecteur pour indexer les noeuds et pouvoir y accéder par leurs id (temps constant).
    vector<noeud> noeuds(taille_noeuds);
    ifstream f(dump_name+".txt");
    if(f)
    {
        string line, cell;
        line="";
        while(line!="// -- NODES")
        {
            //Entete, on ne l'utilise pas pour l'instant.
            getline(f,line);
        }
        getline(f,line);
        getline(f,line);
        int n=0;
        //On saute deux lignes pour éviter 'NODES' et le saut de ligne.
        while(line!="")
        {
            //Noeuds
            //Syntaxe : eid=|n=""|t=|w=
            //Il peut y avoir une syntaxe eid=|n=""|t=|w=|nf="", dans ce cas on utilise nf(représentation en langage naturel) plutôt que n (représentation avec id JDM).
            stringstream lineStream(line);
            //id
            getline(lineStream,cell,'|');
            cell = cell.substr(4,cell.length()-1);
            unsigned long int id = stoul(cell);
            //representation
            getline(lineStream,cell,'|');
            cell = cell.substr(3,cell.length()-4);
            string rep = cell;
            //type
            getline(lineStream,cell,'|');
            cell = cell.substr(2,cell.length()-1);
            int type = stoi(cell);
            //poids
            getline(lineStream,cell,'|');
            cell = cell.substr(2,cell.length()-1);
            int poids = stoi(cell);
            //nf
            string cell2;
            getline(lineStream,cell2,'|');
            if(cell2!="")
            {
                cell2 = cell2.substr(4,cell2.length()-5);
                rep = cell2;
            }
            //Version jeu de test :
            /*if((type>=0&&type<=2&&poids>=500)||type==4||(type==444&&n%10==0)) //pseudorandom pour limiter le nombre de liens
            {
                noeuds[id].init(id,rep,type,poids);
            }*/
            //Fin version jeu de test.
            noeuds[id].init(id,rep,type,poids);
            getline(f,line);
            n++;
            if(n%1000000==0)
            {
                cout << "Noeuds : " << n << endl;
            }
        }
        cout << "Nombre total noeuds : " << n << endl;
        getline(f,line);
        getline(f,line);
        getline(f,line);
        //On saute trois lignes pour éviter les deux sauts de ligne et 'RELATIONS'
        cout << "Fin du chargement des noeuds." << endl;
        getline(f,line);
        n=0;
        while(line!="")
        {
            //Relations
            //Syntaxe : rid=|n1=|n2=|t=|w=
            //On a décidé de représenter les relations par une liste de relations sortantes par noeud.
            stringstream lineStream(line);
            //id
            getline(lineStream,cell,'|');
            cell = cell.substr(4,cell.length()-1);
            unsigned long int id = stoul(cell);
            //noeud1
            getline(lineStream,cell,'|');
            cell = cell.substr(3,cell.length()-1);
            unsigned long int n1 = stoul(cell);
            //noeud2
            getline(lineStream,cell,'|');
            cell = cell.substr(3,cell.length()-1);
            unsigned long int n2 = stoul(cell);
            //Si les deux noeuds de la relation existent, on ajoute, sinon on passe à la relation suivante.
            if(noeuds[n1].getId()!=0&&noeuds[n2].getId()!=0)
            {
                //type
                getline(lineStream,cell,'|');
                cell = cell.substr(2,cell.length()-1);
                int type = stoi(cell);
                //poids
                getline(lineStream,cell,'|');
                cell = cell.substr(2,cell.length()-1);
                int poids = stoi(cell);
                noeuds[n1].addRelation(id,n2,type,poids);
            }
            n++;
            if(n%1000000==0)
            {
                cout << "Relations : " << n << endl;
            }
            getline(f,line);
        }
        cout << "Nombre de relations valides : " << n << endl;
        //On ignore le reste du fichier (nombre de relations et //EOF).
        f.close();
    }
    else
    {
        cerr << "Impossible d'ouvrir le fichier " << dump_name << ".txt" << endl;
        return 1;
    }
    //Fin chargement fichier dump
    //Chargement dictionnaires
    //Pour faciliter la réutilisation de vocabulaire, on utilise des dictionnaires pour faire l'équivalence (si possible) entre JDM et du vocabulaire existant.
    //Il y a un dictionnaire pour les types de noeuds et un pour les types de relations.
    //Les dictionnaires sont composés de trois attributs par lignes séparés par des virgules : id, nom JDM et équivalence vocabulaire existant
    //Si il n'y a pas de vocabulaire existant, on écrit rien et on crée donc notre vocabulaire avec les noms de JDM
    unordered_map<int,string> dictionnaire_noeuds;
    unordered_map<int,string> dictionnaire_relations;
    ifstream dic_noeuds("noeuds.txt");
    if(dic_noeuds)
    {
        string line, cell;
        while(getline(dic_noeuds,line))
        {
            stringstream lineStream(line);
            getline(lineStream,cell,',');
            int id = stoi(cell);
            getline(lineStream,cell,',');
            //On utilise le nom de jeuxdemots que si il n'y a pas de nom défini
            string nomJDM = cell;
            getline(lineStream,cell,',');
            if(cell=="")
            {
                dictionnaire_noeuds[id]="jdm:"+nomJDM;
            }
            else
            {
                dictionnaire_noeuds[id]=cell;
            }
        }
        dic_noeuds.close();
    }
    else
    {
        cerr << "Impossible d'ouvrir le dictionnaire noeuds.txt" << endl;
        return 1;
    }
    ifstream dic_rel("relations.txt");
    if(dic_rel)
    {
        string line, cell;
        while(getline(dic_rel,line))
        {
            stringstream lineStream(line);
            getline(lineStream,cell,',');
            int id = stoi(cell);
            getline(lineStream,cell,',');
            //On utilise le nom de jeuxdemots que si il n'y a pas de nom défini
            string nomJDM = cell;
            getline(lineStream,cell,',');
            if(cell=="")
            {
                replace(nomJDM.begin(),nomJDM.end(),'>','_');
                dictionnaire_relations[id]="jdm:"+nomJDM;
            }
            else
            {
                dictionnaire_relations[id]=cell;
            }
        }
        dic_rel.close();
    }
    else
    {
        cerr << "Impossible d'ouvrir le dictionnaire relations.txt" << endl;
        return 1;
    }
    //Ecriture fichier .ttl
    ofstream fichier_turtle(dump_name+".ttl", ios::out | ios::trunc);
    if(fichier_turtle)
    {
        //On copie d'abord le contenu du fichier prefixes pour le mettre au début du fichier .ttl
        //Il faut mettre dans ce fichier tous les préfixes du vocabulaire qu'on utilise.
        ifstream prefixes("prefixes.txt");
        if(prefixes)
        {
            string line;
            while(getline(prefixes,line))
            {
                fichier_turtle << line << endl;
            }
        }
        else
        {
            cerr << "Impossible d'ouvrir le fichier prefixes.txt" << endl;
            return 1;
        }
        fichier_turtle << endl << endl;
        //On entre ensuite les données, par ordre d'id de noeud, et on écrit toutes les relations sortantes pour le noeud courant.
        //On écrit d'abord les noeuds.
        for(unsigned long int i=0;i<noeuds.size();i++)
        {
            noeud * noeud_act = &noeuds[i];
            //Vu que le vecteur est peut avoir des noeuds "vides" pour l'indexation, il ne faut pas les mettre dans le résultat final.
            if(noeud_act->getId()!=0)
            {
                fichier_turtle << "jdm:" << noeud_act->getId() << " jdm:poids_relations jdm:poids_" << noeud_act->getId() << " ;" << endl;
                fichier_turtle << "\t jdm:poids " << noeud_act->getPoids() << " ;" << endl;
                //Si le type de noeud n'existe pas dans le dictionnaire, on crée un jdm:type_inconnu.
                string type_noeud = dictionnaire_noeuds[noeud_act->getType()];
                if(type_noeud=="")
                {
                    type_noeud = "jdm:type_inconnu";
                }
                fichier_turtle << "\t rdf:type " << type_noeud << " ;" << endl;
                //Traitement spécifique suivant le type de noeud
                switch(noeud_act->getType())
                {
                    //Si le noeud est un terme, alors il a une forme canonique et une représentation écrite.
                    case 666 :
                    case 777 :
                    case 1 :
                    {
                        fichier_turtle << "\t lime:language \"fr\" ;" << endl;
                        fichier_turtle << "\t ontolex:canonicalForm jdm:form_" << noeud_act->getId() << " ." << endl;
                        fichier_turtle << "jdm:form_" << noeud_act->getId() << " ontolex:writtenRep \"" << noeud_act->getRepresentation() <<"\"@fr ;" << endl;
                        fichier_turtle << "\t rdf:type ontolex:Form ." << endl;
                        break;
                    }
                    //Si c'est une forme il a uniquement une représentation écrite.
                    case 2 :
                    {
                        fichier_turtle << "\t ontolex:writtenRep \"" << noeud_act->getRepresentation() << "\"@fr ." << endl;
                        break;
                    }
                    //Si c'est un noeud-relation, alors on le lie à la relation qu'il représente.
                    case 10 :
                    {
                        string relation_cible = noeud_act->getRepresentation();
                        relation_cible = relation_cible.substr(2);
                        fichier_turtle << "\t skos:related jdm:r_" << relation_cible << " ." << endl;// owl:sameAs ???? skos:related ????
                        break;
                    }
                    //Si c'est un lien, on traite les données pour faire une URI valide.
                    case 444 :
                    {
                        //différents types de liens possibles : umls, babelnet, dbnary, radlex
                        string nom_link = noeud_act->getRepresentation();
                        //On supprime les espaces au cas où il y ait une typo.
                        nom_link.erase (std::remove (nom_link.begin(), nom_link.end(), ' '), nom_link.end());
                        string link_lower = nom_link.substr(0,11);
                        transform(link_lower.begin(),link_lower.end(),link_lower.begin(),::tolower);
                        if(link_lower.substr(0,3)=="bn:") //babelnet
                        {
                            fichier_turtle << "\t owl:sameAs <http://babelnet.org/rdf/s" << nom_link.substr(3) << "> ." << endl;
                        }
                        else
                        {
                            if(link_lower.substr(0,5)=="umls:") //umls
                            {
                                fichier_turtle << "\t owl:sameAs <http://linkedlifedata.com/resource/umls/id/" << nom_link.substr(5) << "> ." << endl;
                            }
                            else
                            {
                                if(link_lower.substr(0,7)=="radlex:") //radlex
                                {
                                    fichier_turtle << "\t owl:sameAs <http://www.radlex.org/RID/" << nom_link.substr(7) << "> ." << endl;
                                }
                                else
                                {
                                    if(link_lower.substr(0,11)=="dbnary:fra:") //dbnary
                                    {
                                        fichier_turtle << "\t owl:sameAs <http://kaiko.getalp.org/dbnary/fra/" << nom_link.substr(11) << "> ." << endl;
                                    }
                                    else
                                    {
                                        if(link_lower.substr(0,5)=="wiki:"&&link_lower.size()>5&&link_lower[5]!='@')
                                        {
                                            //La plupart des liens wiki sont vers wiki:@, et on gère ça avec une requête sparql, sinon on traite ça ici :
                                            fichier_turtle << "\t owl:sameAs <http://fr.dbpedia.org/page/" << char(toupper(link_lower[5])) << nom_link.substr(6) << "> ." << endl;
                                        }
                                        else
                                        {
                                            //on fait comme le default
                                            fichier_turtle << "\t rdfs:label \"" << noeud_act->getRepresentation() << "\" ." << endl;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }
                    //Sinon on utilise un label.
                    default :
                    {
                        //On ne met pas de tag de langage ici parce que beaucoup d'informations ne sont pas en français correct.
                        fichier_turtle << "\t rdfs:label \"" << noeud_act->getRepresentation() << "\" ." << endl;
                        break;
                    }
                }
                //On écrit ensuite les relations des noeuds.
                for(unsigned int j=0;j<noeud_act->getSizeRelations();j++)
                {
                    relation* rel_act = noeud_act->getRelations(j);
                    string nom_rel = dictionnaire_relations[rel_act->getType()];
                    //Si le type de relation n'existe pas dans le dictionnaire, on crée un jdm:relation_inconnu.
                    if(nom_rel=="")
                    {
                        nom_rel = "jdm:relation_inconnue";
                    }
                    fichier_turtle << "jdm:" << noeud_act->getId() << " " << nom_rel << " jdm:" << rel_act->getNoeudCible() << " ." << endl;
                    //Si on veut garder le prefixe pour mettre dans les relations poids :
                    //replace(nom_rel.begin(),nom_rel.end(),':','_');
                    //Sinon :
                    nom_rel = nom_rel.substr(nom_rel.find(":")+1);
                    //On utilise r_ + l'id de la relation pour le noeud intermediaire
                    fichier_turtle << "jdm:poids_" << noeud_act->getId() << " jdm:poids_" << nom_rel << " jdm:r_" << rel_act->getId() << " ." << endl;
                    fichier_turtle << "jdm:r_" << rel_act->getId() << " jdm:val_poids " << rel_act->getPoids() << " ."<< endl;
                    fichier_turtle << "jdm:r_" << rel_act->getId() << " jdm:reference jdm:" << rel_act->getNoeudCible() << " ."<< endl;
                }
                fichier_turtle << endl;
            }
            if(i%100000==0)
            {
                cout << "Position dans le vecteur noeuds : " << i << endl;
            }
        }
        fichier_turtle.close();
    }
    else
    {
        cerr << "Impossible d'ouvrir/créer le fichier " << dump_name << ".ttl" << endl;
        return 1;
    }
    return 0;
}
