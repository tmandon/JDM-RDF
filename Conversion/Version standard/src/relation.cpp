#include "relation.h"

using namespace std;

relation::relation()
{

}

relation::relation(unsigned long int i, unsigned long int n, int t, int p) : id(i), noeud_cible(n), type(t), poids(p)
{

}

relation::~relation()
{

}

unsigned long int relation::getId()
{
    return id;
}

unsigned long int relation::getNoeudCible()
{
    return noeud_cible;
}

int relation::getPoids()
{
    return poids;
}

int relation::getType()
{
    return type;
}
