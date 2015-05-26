/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/


// Code to define the polygon points order (clockwise)

/*
	Return the clockwise status of a curve, clockwise or anticlockwise
	n vertices making up curve p
          CLOCKWISE == 1
          ANTICLOCKWISE == -1
*/
#include "headers.h"
#include "Clockwise.h"

int ClockWise(TPolygon polygon)
{
	int i,j;
	double area=0;
	int n;

	n = polygon.numVertex;

	if (n < 3)
		return(0);

	for (i = 0; i < n; i++) {
		j = (i + 1) % n;
		area += (polygon.vertex[i].x*polygon.vertex[j].y - polygon.vertex[j].x*polygon.vertex[i].y);
	}
	if (area < 0)
		return(CLOCKWISE);
	else if (area > 0)
		return(ANTICLOCKWISE);
	else
		return(0);
} 
