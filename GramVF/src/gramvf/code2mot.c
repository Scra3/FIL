/* code2mot : traduit une liste de codes vers
 * la liste de mots correspondants.
 * Frederic Bechet - octobre 2015
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

/*................................................................*/

char **lire_lexique(FILE *file, int *maxcode)
{
char ch[1000],mot[1000],**tab;
int code;
*maxcode=0;
while(fgets(ch,1000,file))
 {
 if (sscanf(ch,"%s %d",mot,&code)!=2) exit(1);
 if (*maxcode<code) *maxcode=code;
 }
tab=(char**)malloc(sizeof(char*)*((*maxcode)+1));
for(code=0;code<=*maxcode;code++) tab[code]=NULL;
rewind(file);
while(fgets(ch,1000,file))
 {
 if (sscanf(ch,"%s %d",mot,&code)!=2) exit(1);
 tab[code]=strdup(mot);
 }
return tab;
}

/*................................................................*/

int main(int argc, char **argv)
{
FILE *f;
char **tab,ch[6000],*pt;
int code,maxcode;
if (argc<=1) { fprintf(stderr,"syntaxe : cat <fichier corpus code> | %s <fichier lexique> \n",argv[0]); exit(1); }
/* lecture lexique */
f=fopen(argv[1],"rt");
tab=lire_lexique(f,&maxcode);
fclose(f);
/* lecture corpus */
while(fgets(ch,6000,stdin))
 {
 for(pt=strtok(ch," \t\n\r");pt;pt=strtok(NULL," \t\n\r"))
  {
  if (sscanf(pt,"%d",&code)!=1) { fprintf(stderr,"mauvais format: %s\n",pt); exit(1); }
  if ((code>=0)&&(code<=maxcode)&&(tab[code])) printf("%s ",tab[code]); else printf("CODE_INCONNU(%d) ",code);
  }
 printf("\n");
 }

exit(0);
}


