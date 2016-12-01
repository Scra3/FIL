/* tokenize: un programme pour tokenizer un texte
 * avec un algorithme glouton a partir d'un lexique
 * code sous la forme d'un arbre de prefixe
 * Frederic Bechet - octobre 2015
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

/*................................................................*/

/* utilitaires */

int separateur_fort(char c)
{
static char t[]={' ','\n','\t','.',':',';',',','"','\'','-','_','!','?','\0'};
int i;
for(i=0;(t[i])&&(t[i]!=c);i++);
return t[i];
}

int espace(char c)
{
static char t[]={' ','\n','\t','\0'};
int i;
for(i=0;(t[i])&&(t[i]!=c);i++);
return t[i];
}

/*................................................................*/

/* arbre de prefixe */

typedef struct type_noeud {
    char car;
    int code;
    struct type_noeud *fg,*fd;
} ty_noeud;

ty_noeud *new_noeud(char car, int code)
{
ty_noeud *pt;
pt=(ty_noeud*)malloc(sizeof(ty_noeud));
pt->car=car;
pt->code=code;
pt->fg=pt->fd=NULL;
return pt;
}

ty_noeud *ajoute_mot(ty_noeud *lexique, char *mot, int code)
{
if (lexique==NULL) lexique=new_noeud(*mot,*(mot+1)?0:code);
if (lexique->car==*mot)
 {
 if (*(mot+1)=='\0') lexique->code=code;
 else lexique->fg=ajoute_mot(lexique->fg,mot+1,code);
 }
else lexique->fd=ajoute_mot(lexique->fd,mot,code);
return lexique;
}

ty_noeud *lire_lexique(FILE *file)
{
char ch[1000],mot[1000];
int code;
ty_noeud *racine=NULL;
while(fgets(ch,1000,file))
 {
 if (sscanf(ch,"%s %d",mot,&code)!=2) exit(1);
 racine=ajoute_mot(racine,mot,code);
 }
return racine;
}

/*................................................................*/

/* tokenizeur */

int tokenize(ty_noeud *lexique, char *buffer, int indice, int *code)
{
int imot;
*code=0; imot=indice;
while ((buffer[indice])&&(lexique))
 {
 if ((buffer[indice]==lexique->car)||((buffer[indice]==' ')&&(lexique->car=='_')))
  {
  if ((lexique->code!=0)&&((separateur_fort(buffer[indice+1]))||(separateur_fort(buffer[indice]))))
   {
   *code=lexique->code;
   imot=indice;
   }
  lexique=lexique->fg;
  indice++;
  }
 else lexique=lexique->fd;
 }
return imot;
}

/*................................................................*/

/* traiter un corpus */

#define TAILLE_BUFFER   50000

int main(int argc, char **argv)
{
FILE *f;
ty_noeud *lexique;
char *buffer,*temp,c;
int i,j,size,indice,code,nbbuffer;

if (argc<=1) { fprintf(stderr,"syntaxe : %s <fichier lexique>\n",argv[0]); exit(1); }

/* lecture lexique */
f=fopen(argv[1],"rt");
lexique=lire_lexique(f);
fclose(f);

/* lecture corpus */
buffer=(char*)malloc(sizeof(char)*TAILLE_BUFFER);
for(size=0,nbbuffer=1;(c=fgetc(stdin))!=EOF;size++) 
 {
 if ((size!=0)&&((size%TAILLE_BUFFER)==0))
  {
  temp=buffer;
  buffer=(char*)malloc(sizeof(char)*TAILLE_BUFFER*(nbbuffer+1));
  memcpy(buffer,temp,TAILLE_BUFFER*nbbuffer);
  free(temp);
  nbbuffer++;
  }
 buffer[size]=c;
 }
buffer[size]='\0';

/* tokenization corpus */
for(i=0;i<size;i++)
 if (!espace(buffer[i]))
  {
  i=tokenize(lexique,buffer,i,&code);
  if (code) printf("%d ",code);
  else
   { /* mot inconnu, on affiche le code '0' et on avance au prochain mot */
   printf("0 ");
   for (j=i;(i<size)&&(!separateur_fort(buffer[i]));i++);
   if (i>j) i--; /* si jamais le separateur fort est aussi inconnu, on avance quand meme */
   }
  }
 else if (buffer[i]=='\n') printf("\n");

exit(0);
}
 

