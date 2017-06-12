#include "noeud.h"

using namespace std;

noeud::noeud()
{

}

noeud::noeud(unsigned long int i , string r, int t, int p) : id(i), representation(r), type(t), poids(p)
{

}

void noeud::init(unsigned long int i , string r, int t, int p)
{
    id=i;
    representation=r;
    type=t;
    poids=p;
}

noeud::~noeud()
{
    for(unsigned int i=0;i<relations.size();i++)
    {
        delete relations[i];
    }
}

void noeud::setId(unsigned long int i)
{
    id = i;
}

void noeud::setPrintable(char b)
{
    printable = b;
}

char noeud::getPrintable()
{
    return printable;
}

unsigned long int noeud::getId()
{
    return id;
}

string noeud::getRepresentation()
{
    return representation;
}

int noeud::getType()
{
    return type;
}

int noeud::getPoids()
{
    return poids;
}

unsigned int noeud::getSizeRelations()
{
    return relations.size();
}

relation* noeud::getRelations(int i)
{
    return relations[i];
}

void noeud::addRelation(unsigned long int i, unsigned long int n, int t, int p)
{
    relations.push_back(new relation(i,n,t,p));
}

void noeud::afficher()
{
    cout << "id : " << id << ", rep : " << representation << ", type : " << type << ", poids : " << poids << endl;
}
