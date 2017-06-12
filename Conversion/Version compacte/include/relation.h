#ifndef RELATION_H
#define RELATION_H


class relation
{
    public:
        relation();
        relation(unsigned long int, unsigned long int, int, int);
        virtual ~relation();
        unsigned long int getId();
        unsigned long int getNoeudCible();
        int getType();
        int getPoids();
        void setPoids(int);
    protected:
        unsigned long int id;
        unsigned long int noeud_cible;
        int type;
        int poids;
    private:
};

#endif // RELATION_H
