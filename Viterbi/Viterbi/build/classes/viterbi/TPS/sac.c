/* Remettre en ordre un sac de mots avec un modele 2-grammes
 * Frederic Bechet - octobre 2016
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

/*................................................................*/

#define MAX_MOTS        100000
#define ALPHA           0.01
#define TAILLE_LIGNE    10000

/*................................................................*/

/* gestion ngram */

typedef struct type_ngram
    {
    int mot,nb;
    struct type_ngram *next;
    } ty_ngram;

ty_ngram *Model[MAX_MOTS];
int Nb1gram,Nb2gram;

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

void load_ngram_model(char *filename)
{
FILE *file;
char ch[TAILLE_LIGNE];
int m1,m2,nb,i;
for(i=0;i<MAX_MOTS;i++) Model[i]=NULL;
Nb1gram=Nb2gram=0;
if (!(file=fopen(filename,"rt"))) { fprintf(stderr,"Impossible d'ouvrir le fichier: %s\n",filename); exit(0); }
while(fgets(ch,TAILLE_LIGNE,file))
 {
 if (sscanf(ch,"%d %d %d",&m1,&m2,&nb)==3)
  {
  if ((Model[m1]==NULL)||(Model[m2]==NULL)) { fprintf(stderr,"Erreur dans le modele : 1gram manquant dans le 2gram (%d,%d)\n",m1,m2); exit(0); }
  add_ngram(Model[m1],m2,nb);
  Nb2gram+=nb;
  }
 else
 if (sscanf(ch,"%d %d",&m1,&nb)==2)
  {
  if (Model[m1]) { fprintf(stderr,"Erreur dans le modele : 1gram deja present (%d)\n",m1); exit(0); }
  Model[m1]=new_ngram(m1,nb,NULL);
  Nb1gram+=nb;
  }
 }
fclose(file);
}

int compte_2gram(int m1, int m2)
{
ty_ngram *pt;
if ((m1>=MAX_MOTS)||(Model[m1]==NULL)||(m2>=MAX_MOTS)||(Model[m2]==NULL)) return 0;
for(pt=Model[m1]->next;(pt)&&(pt->mot!=m2);pt=pt->next);
return pt?pt->nb:0;
}

int compte_1gram(int m1)
{
if ((m1>=MAX_MOTS)||(Model[m1]==NULL)) return 0;
return Model[m1]->nb;
}

float calc_logprob_2g(int m1, int m2)
{
return -logf((float)(compte_2gram(m1,m2)+ALPHA)/(float)(compte_1gram(m1)+Nb1gram*ALPHA));
}

float calc_logprob_1g(int m1)
{
return -logf((float)(compte_1gram(m1)+ALPHA)/(float)(Nb1gram+Nb1gram*ALPHA));
}

/*................................................................*/

#define MAX_SIZE_PHRASE 20

float calc_pplex(int phrase[MAX_SIZE_PHRASE], int permut[MAX_SIZE_PHRASE], int size)
{
int i;
float lp;
if (size==0) return 10000;
lp=calc_logprob_1g(phrase[permut[0]]);
for(i=1;i<size;i++) lp+=calc_logprob_2g(phrase[permut[i-1]],phrase[permut[i]]);
return lp/(float)size;
}

void do_permut(int phrase[MAX_SIZE_PHRASE], int permut[MAX_SIZE_PHRASE], int size, int indice)
{
int i,j;
if (indice==size)
 {
 printf("#%.1f\t",calc_pplex(phrase,permut,size));
 for(i=0;i<size;i++) printf("%d ",phrase[permut[i]]);
 printf("\n");
 }
else
 {
 for(i=0;i<size;i++)
  {
  for(j=0;(j<indice)&&(permut[j]!=i);j++);
  if (j==indice)
   {
   permut[indice]=i;
   do_permut(phrase,permut,size,indice+1);
   permut[indice]=-1;
   }
  }
 }
}

int main(int argc, char **argv)
{
char ch[TAILLE_LIGNE],ch2[TAILLE_LIGNE],*pt;
int i,nb,phrase[MAX_SIZE_PHRASE],permut[MAX_SIZE_PHRASE];
float lp;
if (argc!=2) { fprintf(stderr,"Syntaxe : %s <modele ngram>\n",argv[0]); exit(0); }
load_ngram_model(argv[1]);
fgets(ch,TAILLE_LIGNE,stdin);
for(nb=0,pt=strtok(ch," \t\n\r");(pt)&&(nb<MAX_SIZE_PHRASE);pt=strtok(NULL," \t\n\r"),nb++)
 if (sscanf(pt,"%d",&(phrase[nb]))!=1) { fprintf(stderr,"Erreur : mauvaise valeur => %s\n",pt); exit(0); }
for(i=0;i<nb;i++) permut[i]=-1;
do_permut(phrase,permut,nb,0);

exit(0);
}
  

