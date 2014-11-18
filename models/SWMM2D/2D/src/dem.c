/***********************************************************/
/*                                                         */
/* dem.c                                                */
/*                                                         */
/* I/O routines for terrain stability mapping              */
/*                                                         */
/***********************************************************/

/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

#include "dem.h"

int readline(FILE *fp,char *fline)
{
  int i = 0, ch;

  for(i=0; i< MAXLN; i++)
  {
    ch = getc(fp);

    if(ch == EOF) { *(fline+i) = '\0'; return(EOF); }
    else          *(fline+i) = (char)ch;

    if((char)ch == '\n') { *(fline+i) = '\0'; return(0); }

  }

  return(1);
}

/*
  matalloc(...) allocates memory for matrix navigation pointer
  and for matrix data, then returns matrix navigation pointer
  Modification of matrixalloc by DGT to not use so many pointers
  7/1/97
*/
void **matalloc(int nx,int ny)
{
    int i,arrsize;
    void **mat;
    void *data;
    float **fmat;

      mat = (void **)malloc(sizeof(float *)*(nx));
      arrsize = sizeof(float)*(nx)*(ny);

    if(mat == NULL)
    {
      printf("\nError: Cannot allocate memory for matrix navigation pointers");
      printf("\n       nx = %d, ny = %d\n\n",nx,ny);
      exit(2);
    }
    
    data = malloc(arrsize);
    
    if(data == NULL)
    {
      printf("\nError: Cannot allocate memory for matrix of dimension");
      printf("\n       nx = %d, ny = %d\n\n",nx,ny);
      exit(3);
    }
    
	fmat = (float **)mat;
	for(i=0; i<(nx); i++)
	{
		fmat[i] = &(((float *)(data))[i*(ny)]);
	}

    return mat;
}

int gridread(char *file, void ***data, int *nx, int *ny, float *dx, float *dy, double bndbox[4], float *nodata)
{
FILE *fp;

    int i, j, hdrlines = 0;
    float value;
    char fline[MAXLN], keyword[21], utmetag, utmntag;
    float **farr;
    double utme,utmn;
	double csize;
 
    fp = fopen(file,"r");
    if(fp == NULL)
    {
        printf("\nERROR: Cannot open input file (%s).\n\n",file);
        return(1);
    }
    
    /* read ARC-Info header */
    while(1)
    {   
        readline(fp, fline);       
        if(!isalpha(*fline) || *fline == '-')
            break;       
        
        hdrlines++;

        sscanf(fline,"%s %f",keyword,&value);


	if(strcmp(keyword,"ncols") == 0 || strcmp(keyword,"NCOLS") == 0)
	    *nx = (int)value;
	else if(strcmp(keyword,"nrows") == 0 || strcmp(keyword,"NROWS") == 0)
	    *ny = (int)value;
	else if(strcmp(keyword,"xllcenter") == 0 || strcmp(keyword,"XLLCENTER") == 0)
	{
	    utmetag = 'c';
	    utme = value;
	}
	else if(strcmp(keyword,"xllcorner") == 0 || strcmp(keyword,"XLLCORNER") == 0)
	{
	    utmetag = 'e';
	    utme = value;
	}
	else if(strcmp(keyword,"yllcenter") == 0 || strcmp(keyword,"YLLCENTER") == 0)
	{
	    utmntag = 'c';
	    utmn = value;
	}
	else if(strcmp(keyword,"yllcorner") == 0 || strcmp(keyword,"YLLCORNER") == 0)
	{
	    utmntag = 'e';
	    utmn = value;
	}
	else if(strcmp(keyword,"cellsize") == 0 || strcmp(keyword,"CELLSIZE") == 0)
	{
	    *dx = *dy = value;
	    csize = (double) value;
	}
	else if(strcmp(keyword,"nodata_value") == 0 || strcmp(keyword,"NODATA_VALUE") == 0 ||
		strcmp(keyword,"NODATA_value") == 0)
	    *nodata = value;
    }
    
    /* adjust utme and utmn if necessary (we store center of reference cell) */
    if(utmetag == 'e') utme = utme + *dx/2;
    if(utmntag == 'e') utmn = utmn + *dy/2;
     bndbox[0] = utme - csize/2.;   
     bndbox[1] = utmn - csize/2.;
     bndbox[2] = bndbox[0] + csize * (*nx);
     bndbox[3] = bndbox[1] + csize * (*ny);
    /* position file pointer for ARC-Info file to beginning of image data */
    rewind(fp);
    for(i=0; i<hdrlines; i++) readline(fp, fline);
    

       farr = (float **) matalloc(*nx, *ny);

        /* read in the ARC-Info file */
        for(i=0; i< *ny; i++)
        {
            for(j=0; j< *nx; j++)
			{

                fscanf(fp,"%f",&farr[j][i]);
			}
        }
        *data = (void **) farr;   

    fclose(fp);
    return(0);
}


