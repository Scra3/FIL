# Makefile (règles implicites)

CC = gcc
CFLAGS = -Wall -Wextra -Werror

tout: tokenize  GramVF.java


tokenize:tokenize.o
	gcc -o tokenize.o -c tokenize.c
	
EchoClient.class:EchoCLient.java
	javac EchoCLient.java

clean:
	rm *.o *.class
