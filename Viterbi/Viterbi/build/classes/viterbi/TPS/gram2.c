/* Calcule un modele 2-grammes a partir d'un corpus tokenize 
 * et traduit en codes 
 * Frederic Bechet - octobre 2015
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

/*................................................................*/

#define MAX_MOTS        100000
#define TAILLE_LIGNE    10000

/*................................................................*/

/* gestion ngram */

typedef struct type_ngram
    {
    int mot,nb;
    struct type_ngram *next;
    } ty_ngram;

ty_ngram *new_ngram(int mot, int nb, ty_ngram *next)
{
ty_ngram *pt;
pt=(ty_ngram*)malloc(sizeof(ty_ngram));
pt->mot=mot;
pt->nb=nb;
pt->next=next;
return pt;
}

void add_ngram(ty_ngram *tete, int mot, int nb)
{
ty_ngram *pt,*ptbak;
for(ptbak=tete,pt=tete->next;(pt)&&(pt->mot!=mot);pt=pt->next) ptbak=pt;
if (pt) pt->nb+=nb; else ptbak->next=new_ngram(mot,nb,NULL);
}

/*................................................................*/

int main(int argc, char **argv)
{
ty_ngram *model[MAX_MOTS],*ptgram;
int n,mot,prevmot,nbmots=0;
char ch[TAILLE_LIGNE],*pt;
/* lecture corpus */
for(n=0;n<MAX_MOTS;n++) model[n]=NULL;
while (fgets(ch,TAILLE_LIGNE,stdin))
 {
 for(prevmot=-1,pt=strtok(ch," \t\n\r");pt;pt=strtok(NULL," \t\n\r"),prevmot=mot,nbmots++)
  {
  if ((sscanf(pt,"%d",&mot)!=1)||(mot>=MAX_MOTS)) { fprintf(stderr,"ERREUR: valueur de code incorrecte en entree = [%s]\n",pt); exit(1); }
  if (model[mot]) model[mot]->nb++; else model[mot]=new_ngram(mot,1,NULL);
  if (prevmot!=-1) add_ngram(model[prevmot],mot,1);
  }
 }
/* affichage modele */
printf("1-gram:\n"); for(n=0;n<MAX_MOTS;n++) if(model[n]) printf("%d %d\n",n,model[n]->nb);
printf("2-gram:\n");
for(n=0;n<MAX_MOTS;n++) if(model[n]) for(ptgram=model[n]->next;ptgram;ptgram=ptgram->next) printf("%d %d %d\n",n,ptgram->mot,ptgram->nb);

exit(0);
}
 

