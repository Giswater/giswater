//-----------------------------------------------------------------------------
//   hash.h
//
//   Header file for Hash Table module hash.c.
//-----------------------------------------------------------------------------

/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

//this file has been modified from the original EPA version to the GISWATER version

#define HTMAXSIZE 1999
#define NOTFOUND  -1

struct HTentry
{
    char   *key;
    int    data;
    struct HTentry *next;
};

typedef struct HTentry *HTtable;

HTtable *HTcreate(void);
int     HTinsert(HTtable *, char *, int);
int     HTfind(HTtable *, char *);
char    *HTfindKey(HTtable *, char *);
void    HTfree(HTtable *);
int  samestr(char *, char *);
