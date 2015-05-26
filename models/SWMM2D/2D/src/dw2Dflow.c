/* Gerris - The GNU Flow Solver
 * Copyright (C) 2008-2012 National Institute of Water and Atmospheric Research
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.  
 */

/*
 * Relevant references:
 *
 * Saint-Venant:
 * 
 * [Audusse2005] E. Audusse and M.-O. Bristeau. A well-balanced,
 * positivity-preserving second-order scheme for shallow-water flows
 * on unstructured meshes, JCP, 2005, 311-333.
 *
 * [Popinet2011] S. Popinet. Quadtree-adaptive tsunami modelling. Ocean Dynamics
 * 61(9):1261-1285, 2011.
 *
 * [An2012] Hyunuk An, Soonyoung Yu. Well-balanced shallow water flow
 * simulation on quadtree cut cell grids. Advances in Water Resources
 * 39:60-70, 2012.
 *
 * Multi-layer Saint-Venant, constant density:
 *
 * [Audusse2011a] E. Audusse, M.-O. Bristeau, B. Perthame and J. Sainte-Marie. A
 * multilayer Saint-Venant system with mass exchanges for
 * shallow-water flows. Derivation and numerical
 * validation. Mathematical Modelling and Numerical analysis, 2011.
 * 
 * Multi-layer Saint-Venant, variable density:
 *
 * [Audusse2011b] E. Audusse, M.-O. Bristeau, M. Pelanti,
 * J. Sainte-Marie. Approximation of the hydrostatic Navier-Stokes
 * system for density stratified flows by a multilayer model. Kinetic
 * interpretation and numerical solution, JCP, 2011.
 */

/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/


#include <stdlib.h>
#include <math.h>
#include <stdio.h>


/* generalisation of the limited gradients (in fluid.c) to mixed cells */



/**
 * Solves the Saint-Venant equations.
 * \beginobject{GfsRiver}
 */

#define H  0
#define U  1
#define V  2

void flux(const double *u, double g, double *f)
{
  f[H] = u[H]*u[U];                       /* h*u */
  f[U] = u[H]*(u[U]*u[U] + g*u[H]/2.);    /* h*(u*u + g*h/2) */
  f[V] = u[H]*u[U]*u[V];                  /* h*u*v */

}


/*
 * uL: left state vector [h,u,v,zb].
 * uR: right state vector.
 * g: acceleration of gravity.
 * f: flux vector.
 *
 * Fills @f by solving an approximate Riemann problem using the HLLC
 * scheme. See e.g. Liang, Borthwick, Stelling, IJNMF, 2004.
 */
void riemann_hllc (const double *uL, const double *uR, double *f, double g)
{
  double cL = sqrt (g*uL[H]), cR = sqrt (g*uR[H]);
  double ustar = (uL[U] + uR[U])/2. + cL - cR;
  double cstar = (cL + cR)/2. + (uL[U] - uR[U])/4.;
  double SL = uL[H] == 0. ? uR[U] - 2.*cR : min (uL[U] - cL, ustar - cstar);
  double SR = uR[H] == 0. ? uL[U] + 2.*cL : max (uR[U] + cR, ustar + cstar);

  if (0. <= SL)
    flux (uL, g, f);
  else if (0. >= SR)
    flux (uR, g, f);
  else {
    double fL[3], fR[3], SM;
    flux (uL, g, fL);
    flux (uR, g, fR);
    f[H] = (SR*fL[H] - SL*fR[H] + SL*SR*(uR[H] - uL[H]))/(SR - SL);
    f[U] = (SR*fL[U] - SL*fR[U] + SL*SR*(uR[H]*uR[U] - uL[H]*uL[U]))/(SR - SL);
    SM = ((SL*uR[H]*(uR[U] - SR) - SR*uL[H]*(uL[U] - SL))/
		  (uR[H]*(uR[U] - SR) - uL[H]*(uL[U] - SL)));
    if (SL <= 0. && 0. <= SM)
      f[V] = uL[V]*f[H];
    else if (SM <= 0. && 0. <= SR)
      f[V] = uR[V]*f[H];
    else {
      printf("L: %g %g %g R: %g %g %g\n", uL[H], uL[U], uL[V], uR[H], uR[U], uR[V]);
      printf("SL: %g SR: %g SM: %g\n", SL, SR, SM);
    }
  }
}

