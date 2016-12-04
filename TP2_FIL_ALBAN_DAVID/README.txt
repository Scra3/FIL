Travail Pratique 2 FIL

Auteurs : Alban BERTOLINI et David Guerroudj
Langage : JAVA
class : GramVf

Les variables :  
	N => type de n-gram 
   	texte => le texte tokenizé dans le désordre.

Tests :

Entrée :
	N = 2
	texte = 3323 47548 2493 84767 83250
Sorties :
	Voici les tokens dans le bon ordre : 
		3323 47548 2493 84767 83250
	Phrase traduite : 
		alors j' ai un trajet
*********************************************
Entrée :
	N = 3
	texte = 3323 47548 2493 84767 83250
Sorties :
	Voici les tokens dans le bon ordre : 
		47548 2493 84767 83250 3323
	Phrase traduite : 
		j' ai un trajet alors
*********************************************

Entrée :
	N = 2
	texte = 79732 7041 47758 53452 53065
Sorties :
	Voici les tokens dans le bon ordre : 
		47758 79732 7041 53452 53065
	Phrase traduite : 
		je suis au métro Ménilmontant
*********************************************

Entrée :
	N = 3
	texte = 79732 7041 47758 53452 53065
Sorties :
	Voici les tokens dans le bon ordre : 
		47758 79732 7041 53452 53065
	Phrase traduite : 
		je suis au métro Ménilmontant
*********************************************

Entrée :
	N = 2
	texte = 63702 49618 77501 27691 0
Sorties :
	Voici les tokens dans le bon ordre : 
		27691 63702 49618 77501 0
	Phrase traduite : 
		donc pour Levallois-Perret soixante
*********************************************

Entrée :
	N = 3
	texte = 63702 49618 77501 27691 0
Sorties :
	Voici les tokens dans le bon ordre : 
		27691 63702 49618 77501 0
	Phrase traduite : 
		donc pour Levallois-Perret soixante
*********************************************


Entrée :
	N = 2
	texte = 59771 55931 66831
Sorties :
	Voici les tokens dans le bon ordre : 
		55931 66831 59771
	Phrase traduite : 
		ne quittez pas
*********************************************
