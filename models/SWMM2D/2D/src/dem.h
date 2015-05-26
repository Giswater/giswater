/***********************************************************/
/*                                                         */
/* dem.h                                                   */
/*                                                         */
/* Grid Data manipulation functions -- header file         */
/*                                                         */
/*                                                         */
/* David Tarboton                                          */
/* Utah Water Research Laboratory                          */
/* Utah State University                                   */
/* Logan, UT 84322-8200                                    */
/*                                                         */
/***********************************************************/

/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include <limits.h>
#include <math.h>

/*  ESRI Application Programmers Interface include file  */

#ifndef PI
#define PI 3.14159265359
#endif
#define LINELEN 40
#ifndef MAXLN
#define MAXLN 4096
#endif


int gridread(char *file, void ***data, int *nx, int *ny, float *dx, float *dy, double bndbox[4], float *ndv);
