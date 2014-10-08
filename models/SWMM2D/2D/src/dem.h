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
