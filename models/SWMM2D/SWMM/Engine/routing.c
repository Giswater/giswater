//-----------------------------------------------------------------------------
//   routing.c
//
//   Project:  EPA SWMM5
//   Version:  5.1
//   Date:     03/19/14  (Build 5.1.000)
//   Author:   L. Rossman (EPA)
//             M. Tryby (EPA)
//
//   Conveyance system routing functions.
//
//-----------------------------------------------------------------------------

/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

//this file has been modified from the original EPA version to the GISWATER version

#define _CRT_SECURE_NO_DEPRECATE

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include "headers.h"

//-----------------------------------------------------------------------------
// Shared variables
//-----------------------------------------------------------------------------
static int* SortedLinks;

//-----------------------------------------------------------------------------
//  External functions (declared in funcs.h)
//-----------------------------------------------------------------------------
// routing_open            (called by swmm_start in swmm5.c)
// routing_getRoutingStep  (called by swmm_step in swmm5.c)
// routing_execute         (called by swmm_step in swmm5.c)
// routing_close           (called by swmm_end in swmm5.c)

//-----------------------------------------------------------------------------
// Function declarations
//-----------------------------------------------------------------------------
static void addExternalInflows(DateTime currentDate);
static void addDryWeatherInflows(DateTime currentDate);
static void addWetWeatherInflows(double routingTime);
static void addGroundwaterInflows(double routingTime);
static void addRdiiInflows(DateTime currentDate);
static void addIfaceInflows(DateTime currentDate);
static void removeStorageLosses(double tStep);
static void removeOutflows(void);
static void removeConduitLosses(void); 
static int  inflowHasChanged(void);

//=============================================================================

int routing_open()
//
//  Input:   none
//  Output:  returns an error code
//  Purpose: initializes the routing analyzer.
//
{
    // --- open treatment system
    if ( !treatmnt_open() ) return ErrorCode;

    // --- topologically sort the links
    SortedLinks = NULL;
    if ( Nobjects[LINK] > 0 )
    {
        SortedLinks = (int *) calloc(Nobjects[LINK], sizeof(int));
        if ( !SortedLinks )
        {
            report_writeErrorMsg(ERR_MEMORY, "");
            return ErrorCode;
        }
        toposort_sortLinks(SortedLinks);
        if ( ErrorCode ) return ErrorCode;
    }

    // --- open any routing interface files
    iface_openRoutingFiles();
    return ErrorCode;
}

//=============================================================================

void routing_close(int routingModel)
//
//  Input:   routingModel = routing method code
//  Output:  none
//  Purpose: closes down the routing analyzer.
//
{
    // --- close any routing interface files
    iface_closeRoutingFiles();

    // --- free allocated memory
    flowrout_close(routingModel);
    treatmnt_close();
    FREE(SortedLinks);
}

//=============================================================================

double routing_getRoutingStep(int routingModel, double fixedStep)
//
//  Input:   routingModel = routing method code
//           fixedStep = user-supplied time step (sec)
//  Output:  returns a routing time step (sec)
//  Purpose: determines time step used for flow routing at current time period.
//
{
    if ( Nobjects[LINK] == 0 ) return fixedStep;
    else return flowrout_getRoutingStep(routingModel, fixedStep);
}

//=============================================================================

void routing_execute(int routingModel, double routingStep)
//
//  Input:   routingModel = routing method code
//           routingStep = routing time step (sec)
//  Output:  none
//  Purpose: executes the routing process at the current time period.
//
{
    int      j;
    int      stepCount = 1;
    int      actionCount = 0;
    int      inSteadyState = FALSE;
    DateTime currentDate;
    double   stepFlowError;

	int		node,subcatchLeft,subcatchRight;
	double  uL[3],uR[3],f[3];
	double leftCentroidZ, rightCentroidZ;
	double dt_area;

	int i;
	double h, qx, qy, fK, fKn, fric2, maxGrav, GravX, GravY, Cx, Cy;
	double res, DirFluxX, DirFluxY, qfin, Fobj, dF_dqfin, dinc;
	double auxDepth;

    // --- update continuity with current state
    //     applied over 1/2 of time step
    if ( ErrorCode ) return;
    massbal_updateRoutingTotals(routingStep/2.);

    // --- evaluate control rules at current date and elapsed time
    currentDate = getDateTime(NewRoutingTime);
    for (j=0; j<Nobjects[LINK]; j++) link_setTargetSetting(j);
    controls_evaluate(currentDate, currentDate - StartDateTime,
                      routingStep/SECperDAY);
    for (j=0; j<Nobjects[LINK]; j++)
    {
        if ( Link[j].targetSetting != Link[j].setting )
        {
            link_setSetting(j, routingStep);
            actionCount++;
        } 
    }

    // --- update value of elapsed routing time (in milliseconds)
    OldRoutingTime = NewRoutingTime;
    NewRoutingTime = NewRoutingTime + 1000.0 * routingStep;
    currentDate = getDateTime(NewRoutingTime);

    // --- initialize mass balance totals for time step
    stepFlowError = massbal_getStepFlowError();
    massbal_initTimeStepTotals();

    // --- replace old water quality state with new state
    if ( Nobjects[POLLUT] > 0 )
    {
        for (j=0; j<Nobjects[NODE]; j++) node_setOldQualState(j);
        for (j=0; j<Nobjects[LINK]; j++) link_setOldQualState(j);
    }

    // --- add lateral inflows to nodes
    for (j = 0; j < Nobjects[NODE]; j++)
    {
        Node[j].oldLatFlow  = Node[j].newLatFlow;
        Node[j].newLatFlow  = 0.0;
    }
    addExternalInflows(currentDate);
    addDryWeatherInflows(currentDate);
    addWetWeatherInflows(NewRoutingTime);
    addGroundwaterInflows(NewRoutingTime);
    addRdiiInflows(currentDate);
    addIfaceInflows(currentDate);

    // --- check if can skip steady state periods
    if ( SkipSteadyState )
    {
        if ( OldRoutingTime == 0.0
        ||   actionCount > 0
        ||   fabs(stepFlowError) > SysFlowTol
        ||   inflowHasChanged() ) inSteadyState = FALSE;
        else inSteadyState = TRUE;
    }

    // --- find new hydraulic state if system has changed
    if ( inSteadyState == FALSE )
    {
        // --- replace old hydraulic state values with current ones
        for (j = 0; j < Nobjects[LINK]; j++) link_setOldHydState(j);
        for (j = 0; j < Nobjects[NODE]; j++)
        {
            node_setOldHydState(j);
            node_initInflow(j, routingStep);
        }
		for (j = 0; j < Nobjects[SUBCATCH]; j++) subcatch_setOldHydState(j);

        // --- route flow through the drainage network
        if ( Nobjects[LINK] > 0 )
        {
            stepCount = flowrout_execute(SortedLinks, routingModel, routingStep);
        }
    }

    // --- route quality through the drainage network
    if ( Nobjects[POLLUT] > 0 && !IgnoreQuality ) 
    {
        qualrout_execute(routingStep);
    }

    // --- remove evaporation, infiltration & outflows from system
    removeStorageLosses(routingStep);
    removeConduitLosses();
    removeOutflows();
	
    // --- update continuity with new totals
    //     applied over 1/2 of routing step
    massbal_updateRoutingTotals(routingStep/2.);


	// --- 2D routing

	// --- update subcatchment depth
	for (j = 0; j < Nobjects[SUBCATCH]; j++)
	{

		node = Subcatch[j].outNode;

		//Check if connected to drainage system
		if (Subcatch[j].outNode != -1)
		{
			Node[node].M2DDepth = 0.0;

			// --- new node depth
			auxDepth = MAX(Node[node].newDepth - Node[node].fullDepth, 0.0);

			// --- in case of drying reduce momentum
			if (auxDepth < Subcatch[j].oldGlobalDepth)
			{
				Subcatch[j].oldqx = Subcatch[j].oldVx * auxDepth;
				Subcatch[j].oldqy = Subcatch[j].oldVy * auxDepth;
			}

			// --- update depth
			Subcatch[j].oldGlobalDepth = auxDepth;

		}

		// --- reset fluxes
        Subcatch[j].fluxDepth = 0.0;
        Subcatch[j].fluxHUx = 0.0;
        Subcatch[j].fluxHUy = 0.0;

	}

	// --- compute 2D flows
	M2DControl.totalOutflow = 0.0;


	for (j = 0; j < Nobjects[M2DFACE]; j++)
    {

		// --- initial subcatchment ID 
		subcatchLeft  = -1;
		subcatchRight = -1;

		if (M2DFace[j].leftType == POL_STREET || M2DFace[j].rightType == POL_STREET)
		{
	
			//Set left state
			subcatchLeft  = project_findObject(SUBCATCH, M2DFace[j].polygonLeftID);
			leftCentroidZ = Subcatch[subcatchLeft].centroid.z;
			M2DFace[j].hl  = Subcatch[subcatchLeft].oldGlobalDepth;
			M2DFace[j].hul = Subcatch[subcatchLeft].oldqx;
			M2DFace[j].hvl = Subcatch[subcatchLeft].oldqy;

			//Setup variables for solver
			uL[0] = M2DFace[j].hl;
			uL[1] = M2DFace[j].vnx * Subcatch[subcatchLeft].oldVx + M2DFace[j].vny * Subcatch[subcatchLeft].oldVy;
			uL[2] =-M2DFace[j].vny * Subcatch[subcatchLeft].oldVx + M2DFace[j].vnx * Subcatch[subcatchLeft].oldVy;

			//initialize fluxes
			f[0] = 0.;
			f[1] = 0.;
			f[2] = 0.;

			//Check wall
			if (M2DFace[j].rightType == POL_ROOF)
			{

				//Set right as reflecting boundary
				M2DFace[j].hr  = M2DFace[j].hl;
				M2DFace[j].hur =-M2DFace[j].hul;
				M2DFace[j].hvr = M2DFace[j].hvl;

				uR[0] = M2DFace[j].hl;
				uR[1] =-uL[1];
				uR[2] = uL[2];

				if (uL[0] != 0. || uR[0] != 0.)
				riemann_hllc(uL, uR, f, GRAVITY);

			} else if (M2DFace[j].rightType == POL_STREET) {

				//Set right state
				subcatchRight  = project_findObject(SUBCATCH, M2DFace[j].polygonRightID);
				rightCentroidZ = Subcatch[subcatchRight].centroid.z;
				M2DFace[j].hr   = Subcatch[subcatchRight].oldGlobalDepth;
				M2DFace[j].hur  = Subcatch[subcatchRight].oldqx;
				M2DFace[j].hvr  = Subcatch[subcatchRight].oldqy;

				uR[0] = M2DFace[j].hr;
				uR[1] = M2DFace[j].vnx * Subcatch[subcatchRight].oldVx + M2DFace[j].vny * Subcatch[subcatchRight].oldVy;
				uR[2] =-M2DFace[j].vny * Subcatch[subcatchRight].oldVx + M2DFace[j].vnx * Subcatch[subcatchRight].oldVy;

				if (uL[0] > 0. && uR[0] > 0.)
				{
					riemann_hllc(uL, uR, f, GRAVITY);

				} else if (uL[0] > 0.) {

					//Check elevation
					if ( (uL[0] + leftCentroidZ) > rightCentroidZ) riemann_hllc(uL, uR, f, GRAVITY);

				} else if (uR[0] > 0.) {

					//Check elevation
					if ( (uR[0] + rightCentroidZ) > leftCentroidZ) riemann_hllc(uL, uR, f, GRAVITY);

				}
	
			} else {

				//Weir equation
				if (M2DFace[j].hl > 0.0) 
				{
					double discharge = 3.0 * pow(M2DFace[j].hl, 1.5);

					f[0] = discharge;
					f[1] = discharge * discharge / M2DFace[j].hl + GRAVITY * M2DFace[j].hl * M2DFace[j].hl / 2.;
					f[2] = discharge * M2DFace[j].hvl / M2DFace[j].hl;

					//Compute discharge and convert to SI
					M2DControl.totalOutflow += discharge * M2DFace[j].length * M3perFT3;
				}

			}


			// --- update polygon fluxes
			if (subcatchLeft >= 0) {
				Subcatch[subcatchLeft].fluxDepth -= M2DFace[j].length * f[0];
				Subcatch[subcatchLeft].fluxHUx   -= M2DFace[j].length * (M2DFace[j].vnx * f[1] - M2DFace[j].vny * f[2]);
				Subcatch[subcatchLeft].fluxHUy   -= M2DFace[j].length * (M2DFace[j].vny * f[1] + M2DFace[j].vnx * f[2]);
			}

			if (subcatchRight >= 0) {
				Subcatch[subcatchRight].fluxDepth += M2DFace[j].length * f[0];
				Subcatch[subcatchRight].fluxHUx   += M2DFace[j].length * (M2DFace[j].vnx * f[1] - M2DFace[j].vny * f[2]);
				Subcatch[subcatchRight].fluxHUy   += M2DFace[j].length * (M2DFace[j].vny * f[1] + M2DFace[j].vnx * f[2]);
			}

		}

		//***************************
		if (f[0] > 20.0) {
			Subcatch[j].newGlobalDepth = Subcatch[j].newGlobalDepth;
		}




	}

	// --- update subcatchment values and nodes depth
    for (j = 0; j < Nobjects[SUBCATCH]; j++)
    {
		if (Subcatch[j].isStreet)
		{
			// --- update control volume
			dt_area = routingStep / Subcatch[j].area;
			
			Subcatch[j].newGlobalDepth	= Subcatch[j].oldGlobalDepth + dt_area * Subcatch[j].fluxDepth;
			Subcatch[j].newqx			= Subcatch[j].oldqx          + dt_area * Subcatch[j].fluxHUx;
			Subcatch[j].newqy			= Subcatch[j].oldqy          + dt_area * Subcatch[j].fluxHUy;

			//Check negative values
			if (Subcatch[j].newGlobalDepth <= 0.0) {



				//**********************************
				if (Subcatch[j].newGlobalDepth < 0.0) {
					Subcatch[j].newGlobalDepth = Subcatch[j].newGlobalDepth;
				}




				Subcatch[j].newGlobalDepth = 0.0;
				Subcatch[j].newqx          = 0.0;
				Subcatch[j].newqy          = 0.0;
				Subcatch[j].newVx          = 0.0;
				Subcatch[j].newVy          = 0.0;
			} else {


				// --- isolated polygons should not have momentum
				if (Subcatch[j].isIsolated == 1)
				{
					Subcatch[j].newqx = 0.0;
					Subcatch[j].newqy = 0.0;
					Subcatch[j].newVx = 0.0;
					Subcatch[j].newVy = 0.0;
					continue;
				}

				// --- compute source term
				Subcatch[j].shearStress = 0.0;

				// --- constant value along the loop
				fK = GRAVITY * Subcatch[j].newGlobalDepth;

				// --- Manning
				fric2 = pow(Subcatch[j].N,2.);
				fKn = GRAVITY * fric2 / (pow(Subcatch[j].newGlobalDepth,2.3333) + 0.1e-50);

				// --- initial guess
				h  = Subcatch[j].newGlobalDepth;
				qx = Subcatch[j].newqx;
				qy = Subcatch[j].newqy;

				// --- mild slopes
				maxGrav  = 1.0;

				// --- slope
	            GravX = maxGrav * Subcatch[j].Sx;
				GravY = maxGrav * Subcatch[j].Sy;

				// --- first apply the gravity
				Cx = qx - fK * GravX * routingStep;
	            Cy = qy - fK * GravY * routingStep;

				// --- check forces balance
				if (Cx == 0.0 && Cy == 0.0)
				{
		            qx = - fK * GravX;
			        qy = - fK * GravY;
				} else {

					// --- velocity modulus
					res = sqrt( pow(Cx,2.0) + pow(Cy,2.0) ) + 0.1e-50;

					// --- flow direction
					DirFluxX = Cx / res;
					DirFluxY = Cy / res;

					// --- initial guess
					qfin = res;

					// --- Newton-Rapson loop
					for (i = 0; i < 50; i++)
					{
						Fobj = qfin - res + fKn * routingStep * qfin * qfin;

						// --- finished?
						if (fabs(Fobj/qfin) < 1.e-30) break;

						// --- Newton Rapson
	                    dF_dqfin = 1.0 + fKn * routingStep * 2.0 * qfin;

						// --- inc
						dinc = -Fobj/dF_dqfin;

						if( (qfin + dinc) < 0.0) dinc = _copysign(qfin/2.0,dinc);

						// --- second check
						if (fabs(dinc/qfin) < 1.e-10) break;
						
						// --- update and continue!
						qfin = qfin + dinc;

					}

					// --- apply explicitly the friction
					Subcatch[j].shearStress = fKn * 0.025 * pow(res + qfin,2.);

					// --- final vel
					Subcatch[j].newqx = qfin * DirFluxX;
		            Subcatch[j].newqy = qfin * DirFluxY;

					// --- apply density
					Subcatch[j].shearStress *= 62.4;

				}

				// --- update velocities
				Subcatch[j].newVx = Subcatch[j].newqx / Subcatch[j].newGlobalDepth;
				Subcatch[j].newVy = Subcatch[j].newqy / Subcatch[j].newGlobalDepth;

			}

			// --- compute modulus
			Subcatch[j].newVel = sqrt( pow(Subcatch[j].newVx, 2.0) + pow(Subcatch[j].newVy, 2.0) );
			Subcatch[j].new_vh = sqrt( pow(Subcatch[j].newqx, 2.0) + pow(Subcatch[j].newqy, 2.0) );

			// --- update nodes
			if ( (Subcatch[j].newGlobalDepth > 0.0) || (Subcatch[j].oldGlobalDepth > 0.0) )
			{
				node = Subcatch[j].outNode;
				
				if (Subcatch[j].outNode != -1)
					Node[node].M2DDepth += Subcatch[j].newGlobalDepth * (Subcatch[j].area / Node[node].M2DArea);
			}

		}
	}

	for (j = 0; j < Nobjects[NODE]; j++)
    {
		if ( (Node[j].M2DArea > 0.0) && (Node[j].M2DDepth > 0.0) )
		{
			Node[j].newDepth  = Node[j].fullDepth + Node[j].M2DDepth;
			Node[j].newVolume = Node[j].fullVolume + (Node[j].newDepth - Node[j].fullDepth) * Node[j].pondedArea;
		}







		//***************************
		if (Node[j].newDepth > 20.0) {
			Node[j].newDepth = Node[j].newDepth;
		}






     }


    // --- update summary statistics
    if ( RptFlags.flowStats && Nobjects[LINK] > 0 )
    {
        stats_updateFlowStats(routingStep, currentDate, stepCount, inSteadyState);
    }
}

//=============================================================================

void addExternalInflows(DateTime currentDate)
//
//  Input:   currentDate = current date/time
//  Output:  none
//  Purpose: adds direct external inflows to nodes at current date.
//
{
    int     j, p;
    double  q, w;
    TExtInflow* inflow;

    // --- for each node with a defined external inflow
    for (j = 0; j < Nobjects[NODE]; j++)
    {
        inflow = Node[j].extInflow;
        if ( !inflow ) continue;

        // --- get flow inflow
        q = 0.0;
        while ( inflow )
        {
            if ( inflow->type == FLOW_INFLOW )
            {
                q = inflow_getExtInflow(inflow, currentDate);
                break;
            }
            else inflow = inflow->next;
        }
        if ( fabs(q) < FLOW_TOL ) q = 0.0;

        // --- add flow inflow to node's lateral inflow
        Node[j].newLatFlow += q;
        massbal_addInflowFlow(EXTERNAL_INFLOW, q);

        // --- add on any inflow (i.e., reverse flow) through an outfall
        if ( Node[j].type == OUTFALL && Node[j].oldNetInflow < 0.0 ) 
        {
            q = q - Node[j].oldNetInflow;
        }

        // --- get pollutant mass inflows
        inflow = Node[j].extInflow;
        while ( inflow )
        {
            if ( inflow->type != FLOW_INFLOW )
            {
                p = inflow->param;
                w = inflow_getExtInflow(inflow, currentDate);
                if ( inflow->type == CONCEN_INFLOW ) w *= q;
                Node[j].newQual[p] += w;
                massbal_addInflowQual(EXTERNAL_INFLOW, p, w);
            }
            inflow = inflow->next;
        }
    }
}

//=============================================================================

void addDryWeatherInflows(DateTime currentDate)
//
//  Input:   currentDate = current date/time
//  Output:  none
//  Purpose: adds dry weather inflows to nodes at current date.
//
{
    int      j, p;
    int      month, day, hour;
    double   q, w;
    TDwfInflow* inflow;

    // --- get month (zero-based), day-of-week (zero-based),
    //     & hour-of-day for routing date/time
    month = datetime_monthOfYear(currentDate) - 1;
    day   = datetime_dayOfWeek(currentDate) - 1;
    hour  = datetime_hourOfDay(currentDate);

    // --- for each node with a defined dry weather inflow
    for (j = 0; j < Nobjects[NODE]; j++)
    {
        inflow = Node[j].dwfInflow;
        if ( !inflow ) continue;

        // --- get flow inflow (i.e., the inflow whose param code is -1)
        q = 0.0;
        while ( inflow )
        {
            if ( inflow->param < 0 )
            {
                q = inflow_getDwfInflow(inflow, month, day, hour);
                break;
            }
            inflow = inflow->next;
        }
        if ( fabs(q) < FLOW_TOL ) q = 0.0;

        // --- add flow inflow to node's lateral inflow
        Node[j].newLatFlow += q;
        massbal_addInflowFlow(DRY_WEATHER_INFLOW, q);

        // --- add default DWF pollutant inflows
        for ( p = 0; p < Nobjects[POLLUT]; p++)
        {
            if ( Pollut[p].dwfConcen > 0.0 )
            {
                w = q * Pollut[p].dwfConcen;
                Node[j].newQual[p] += w;
                massbal_addInflowQual(DRY_WEATHER_INFLOW, p, w);
            }
        }

        // --- get pollutant mass inflows
        inflow = Node[j].dwfInflow;
        while ( inflow )
        {
            if ( inflow->param >= 0 )
            {
                p = inflow->param;
                w = q * inflow_getDwfInflow(inflow, month, day, hour);
                Node[j].newQual[p] += w;
                massbal_addInflowQual(DRY_WEATHER_INFLOW, p, w);

                // --- subtract off any default inflow
                if ( Pollut[p].dwfConcen > 0.0 )
                {
                    w = q * Pollut[p].dwfConcen;
                    Node[j].newQual[p] -= w;
                    massbal_addInflowQual(DRY_WEATHER_INFLOW, p, -w);
                }
            }
            inflow = inflow->next;
        }
    }
}

//=============================================================================

void addWetWeatherInflows(double routingTime)
//
//  Input:   routingTime = elasped time (millisec)
//  Output:  none
//  Purpose: adds runoff inflows to nodes at current elapsed time.
//
{
    int    i, j, p;
    double q, w;
    double f;

    // --- find where current routing time lies between latest runoff times
    if ( Nobjects[SUBCATCH] == 0 ) return;
    f = (routingTime - OldRunoffTime) / (NewRunoffTime - OldRunoffTime);
    if ( f < 0.0 ) f = 0.0;
    if ( f > 1.0 ) f = 1.0;

    // for each subcatchment outlet node,
    // add interpolated runoff flow & pollutant load to node's inflow
    for (i = 0; i < Nobjects[SUBCATCH]; i++)
    {
        j = Subcatch[i].outNode;
        if ( j >= 0)
        {
            // add runoff flow to lateral inflow
            q = subcatch_getWtdOutflow(i, f);     // current runoff flow
            Node[j].newLatFlow += q;
            massbal_addInflowFlow(WET_WEATHER_INFLOW, q);

            // add pollutant load
            for (p = 0; p < Nobjects[POLLUT]; p++)
            {
                w = subcatch_getWtdWashoff(i, p, f);
                Node[j].newQual[p] += w;
                massbal_addInflowQual(WET_WEATHER_INFLOW, p, w);
            }
        }
    }
}

//=============================================================================

void addGroundwaterInflows(double routingTime)
//
//  Input:   routingTime = elasped time (millisec)
//  Output:  none
//  Purpose: adds groundwater inflows to nodes at current elapsed time.
//
{
    int    i, j, p;
    double q, w;
    double f;
    TGroundwater* gw;

    // --- find where current routing time lies between latest runoff times
    if ( Nobjects[SUBCATCH] == 0 ) return;
    f = (routingTime - OldRunoffTime) / (NewRunoffTime - OldRunoffTime);
    if ( f < 0.0 ) f = 0.0;
    if ( f > 1.0 ) f = 1.0;

    // --- for each subcatchment
    for (i = 0; i < Nobjects[SUBCATCH]; i++)
    {
        // --- see if subcatch contains groundwater
        gw = Subcatch[i].groundwater;
        if ( gw )
        {
            // --- identify node receiving groundwater flow
            j = gw->node;
            if ( j >= 0 )
            {
                // add groundwater flow to lateral inflow
                q = ( (1.0 - f)*(gw->oldFlow) + f*(gw->newFlow) )
                    * Subcatch[i].area;
                if ( fabs(q) < FLOW_TOL ) continue;
                Node[j].newLatFlow += q;
                massbal_addInflowFlow(GROUNDWATER_INFLOW, q);

                // add pollutant load (for positive inflow)
                if ( q > 0.0 )
                {
                    for (p = 0; p < Nobjects[POLLUT]; p++)
                    {
                        w = q * Pollut[p].gwConcen;
                        Node[j].newQual[p] += w;
                        massbal_addInflowQual(GROUNDWATER_INFLOW, p, w);
                    }
                }
            }
        }
    }
}

//=============================================================================

void addRdiiInflows(DateTime currentDate)
//
//  Input:   currentDate = current date/time
//  Output:  none
//  Purpose: adds RDII inflows to nodes at current date.
//
{
    int    i, j, p;
    double q, w;
    int    numRdiiNodes;

    // --- see if any nodes have RDII at current date
    numRdiiNodes = rdii_getNumRdiiFlows(currentDate);

    // --- add RDII flow to each node's lateral inflow
    for (i=0; i<numRdiiNodes; i++)
    {
        rdii_getRdiiFlow(i, &j, &q);
        if ( j < 0 ) continue;
        if ( fabs(q) < FLOW_TOL ) continue;
        Node[j].newLatFlow += q;
        massbal_addInflowFlow(RDII_INFLOW, q);

        // add pollutant load (for positive inflow)
        if ( q > 0.0 )
        {
            for (p = 0; p < Nobjects[POLLUT]; p++)
            {
                w = q * Pollut[p].rdiiConcen;
                Node[j].newQual[p] += w;
                massbal_addInflowQual(RDII_INFLOW, p, w);
            }
        }
    }
}

//=============================================================================

void addIfaceInflows(DateTime currentDate)
//
//  Input:   currentDate = current date/time
//  Output:  none
//  Purpose: adds inflows from routing interface file to nodes at current date.
//
{
    int    i, j, p;
    double q, w;
    int    numIfaceNodes;

    // --- see if any nodes have interface inflows at current date
    if ( Finflows.mode != USE_FILE ) return;
    numIfaceNodes = iface_getNumIfaceNodes(currentDate);

    // --- add interface flow to each node's lateral inflow
    for (i=0; i<numIfaceNodes; i++)
    {
        j = iface_getIfaceNode(i);
        if ( j < 0 ) continue;
        q = iface_getIfaceFlow(i);
        if ( fabs(q) < FLOW_TOL ) continue;
        Node[j].newLatFlow += q;
        massbal_addInflowFlow(EXTERNAL_INFLOW, q);

        // add pollutant load (for positive inflow)
        if ( q > 0.0 )
        {
            for (p = 0; p < Nobjects[POLLUT]; p++)
            {
                w = q * iface_getIfaceQual(i, p);
                Node[j].newQual[p] += w;
                massbal_addInflowQual(EXTERNAL_INFLOW, p, w);
            }
        }
    }
}

//=============================================================================

int  inflowHasChanged()
//
//  Input:   none
//  Output:  returns TRUE if external inflows or outfall flows have changed
//           from the previous time step
//  Purpose: checks if the hydraulic state of the system has changed from
//           the previous time step.
//
{
    int    j;
    double diff, qOld, qNew;

    // --- check if external inflows or outfall flows have changed 
    for (j = 0; j < Nobjects[NODE]; j++)
    {
        qOld = Node[j].oldLatFlow;
        qNew = Node[j].newLatFlow;
        if      ( fabs(qOld) > TINY ) diff = (qNew / qOld) - 1.0;
        else if ( fabs(qNew) > TINY ) diff = 1.0;
        else                    diff = 0.0;
        if ( fabs(diff) > LatFlowTol ) return TRUE;
        if ( Node[j].type == OUTFALL || Node[j].degree == 0 )
        {
            qOld = Node[j].oldFlowInflow;
            qNew = Node[j].inflow;
            if      ( fabs(qOld) > TINY ) diff = (qNew / qOld) - 1.0;
            else if ( fabs(qNew) > TINY ) diff = 1.0;
            else                          diff = 0.0;
            if ( fabs(diff) > LatFlowTol ) return TRUE;
        }
    }
    return FALSE;
}

//=============================================================================

void removeStorageLosses(double tStep)
//
//  Input:   tStep = routing time step (sec)
//  Output:  none
//  Purpose: adds rate of mass lost from all storage nodes due to evaporation
//           & seepage in current time step to overall mass balance.
//
{
    int    i, j, p;
 	double evapLoss = 0.0,
		   seepLoss = 0.0;
    double vRatio;

    // --- check each storage node
    for ( i = 0; i < Nobjects[NODE]; i++ )
    {
        if (Node[i].type == STORAGE)
        {
            // --- update total system storage losses
            evapLoss += Storage[Node[i].subIndex].evapLoss;
            seepLoss += Storage[Node[i].subIndex].seepLoss;
  
            // --- adjust storage concentrations for any evaporation loss
            if ( Nobjects[POLLUT] > 0 && Node[i].newVolume > FUDGE )
            {
                j = Node[i].subIndex;
                vRatio = 1.0 + (Storage[j].evapLoss / Node[i].newVolume);
                for ( p = 0; p < Nobjects[POLLUT]; p++ )
                {
                    Node[i].newQual[p] *= vRatio;
                }
            }
        }
    }

    // --- add loss rates (ft3/sec) to time step's mass balance 
    massbal_addNodeLosses(evapLoss/tStep, seepLoss/tStep);
}

//=============================================================================

void removeConduitLosses()
//
//  Input:   none
//  Output:  none
//  Purpose: adds rate of mass lost from all conduits due to evaporation
//           & seepage over current time step to overall mass balance.
//
{
	int i;
	double evapLoss = 0.0,
		   seepLoss = 0.0;

	for ( i = 0; i < Nobjects[LINK]; i++ )
	{
		if (Link[i].type == CONDUIT)
        {
			// --- update conduit losses
			evapLoss += Conduit[Link[i].subIndex].evapLossRate;
            seepLoss += Conduit[Link[i].subIndex].seepLossRate;
		}
	}
    massbal_addLinkLosses(evapLoss, seepLoss);
}

//=============================================================================

void removeOutflows()
//
//  Input:   none
//  Output:  none
//  Purpose: finds flows that leave the system and adds these to mass
//           balance totals.
//
{
    int    i, p;
    int    isFlooded;
    double q, w;

    for ( i = 0; i < Nobjects[NODE]; i++ )
    {
        // --- determine flows leaving the system
        q = node_getSystemOutflow(i, &isFlooded);
        if ( q != 0.0 )
        {
            massbal_addOutflowFlow(q, isFlooded);
            for ( p = 0; p < Nobjects[POLLUT]; p++ )
            {
                w = q * Node[i].newQual[p];
                massbal_addOutflowQual(p, w, isFlooded);
            }
        }
    }
}

//=============================================================================
