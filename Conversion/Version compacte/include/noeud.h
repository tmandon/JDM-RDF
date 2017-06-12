#ifndef NOEUD_H
#define NOEUD_H
#include <string>
#include <vector>
#include <iostream>
#include <sstream>
#include <unordered_map>
#include <algorithm>

#include "relation.h"

class noeud
{
    public:
        noeud();
        noeud(unsigned long int, std::string, int, int);
        void init(unsigned long int, std::string, int, int); //initialise le noeud avec les valeurs données
        virtual ~noeud();
        void setId(unsigned long int);
        void setPrintable(char);
        char getPrintable();
        unsigned long int getId();
        std::string getRepresentation();
        int getType();
        int getPoids();
        unsigned int getSizeRelations();
        relation* getRelations(int);
        void addRelation(unsigned long int, unsigned long int, int, int);
        void afficher();
    protected:
        char printable;
        unsigned long int id;
        std::string representation;
        int type;
        int poids;
        std::vector<relation*> relations;
    private:
};

#endif // NOEUD_H
