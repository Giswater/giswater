/*	
	Miscellaneous routines
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#ifndef WIN32
#include <sys/time.h>
#include <sys/resource.h>
#else
#include <windows.h>
#include <mmsystem.h>
#endif
#include "paulslib.h"

/*-------------------------------------------------------------------------
	Compute a point along a spline curve
	The knots u[] should have been calculated before using this
	There are n+1 control points
	t is the degree, typically 3 or 4
	v ranges from 0 to n-t+2, start of the curve to the end
*/
void SplinePoint(int *u,int n,int t,double v,XYZ *control,XYZ *output)
{
  int k;
  double b;

  output->x = 0;
  output->y = 0;
  output->z = 0;

  for (k=0;k<=n;k++) {
    b = SplineBlend(k,t,u,v);
    output->x += control[k].x * b;
    output->y += control[k].y * b;
    output->z += control[k].z * b;
  }
}

/*-------------------------------------------------------------------------
   Calculate the blending value for the spline recursively
	If the numerator and denominator are 0 the result is 0
*/
double SplineBlend(int k,int t,int *u,double v)
{
  double value;

  if (t == 1) {
    if ((u[k] <= v) && (v < u[k+1]))
      value = 1;
    else
      value = 0;
  } else {
    if ((u[k+t-1] == u[k]) && (u[k+t] == u[k+1]))
      value = 0;
    else if (u[k+t-1] == u[k])
      value = (u[k+t] - v) / (u[k+t] - u[k+1]) * SplineBlend(k+1,t-1,u,v);
    else if (u[k+t] == u[k+1])
      value = (v - u[k]) / (u[k+t-1] - u[k]) * SplineBlend(k,t-1,u,v);
    else
      value = (v - u[k]) / (u[k+t-1] - u[k]) * SplineBlend(k,t-1,u,v) +
              (u[k+t] - v) / (u[k+t] - u[k+1]) * SplineBlend(k+1,t-1,u,v);
  }

  return(value);
}

/*-------------------------------------------------------------------------
	Create all the points along a spline curve
	Control points "inp", "n" of them.
   Knots "knots", degree "t".
   Ouput curve "outp", "res" of them.
*/
void SplineCurve(XYZ *inp,int nn,int *knots,int t,XYZ *outp,int res)
{
   int i;
   double interval,increment;

   interval = 0;
   increment = (nn - t + 2) / (double)(res - 1);
   for (i=0;i<res-1;i++) {
      SplinePoint(knots,nn,t,interval,inp,&(outp[i]));
      interval += increment;
   }
	outp[res-1] = inp[nn];
}

/*-------------------------------------------------------------------------
   Figure out the knots, u[]
	There are n + t + 1 knots
*/
void SplineKnots(int *u,int n,int t)
{
  int j;

  for (j=0;j<=n+t;j++) {
    if (j < t)
      u[j] = 0;
    else if (j <= n)
      u[j] = j - t + 1;
    else if (j > n)
      u[j] = n - t + 2;
  }
}

/*-------------------------------------------------------------------------
   Three control point Bezier interpolation
	mu ranges from 0 to 1, start to end of the curve
*/
XYZ Bezier3(XYZ p1,XYZ p2,XYZ p3,double mu)
{
   double mum1,mum12,mu2;
   XYZ p;

   mu2 = mu * mu;
   mum1 = 1 - mu;
   mum12 = mum1 * mum1;
   p.x = p1.x * mum12 + 2 * p2.x * mum1 * mu + p3.x * mu2;
   p.y = p1.y * mum12 + 2 * p2.y * mum1 * mu + p3.y * mu2;
   p.z = p1.z * mum12 + 2 * p2.z * mum1 * mu + p3.z * mu2;

   return(p);
}

/*-------------------------------------------------------------------------
   Four control point Bezier interpolation
	mu ranges from 0 to 1, start to end of curve
*/
XYZ Bezier4(XYZ p1,XYZ p2,XYZ p3,XYZ p4,double mu)
{
   double mum1,mum13,mu3;
   XYZ p;

   mum1 = 1 - mu;
   mum13 = mum1 * mum1 * mum1;
   mu3 = mu * mu * mu;

   p.x = mum13*p1.x + 3*mu*mum1*mum1*p2.x + 3*mu*mu*mum1*p3.x + mu3*p4.x;
   p.y = mum13*p1.y + 3*mu*mum1*mum1*p2.y + 3*mu*mu*mum1*p3.y + mu3*p4.y;
   p.z = mum13*p1.z + 3*mu*mum1*mum1*p2.z + 3*mu*mu*mum1*p3.z + mu3*p4.z;

   return(p);
}

/*-------------------------------------------------------------------------
	General Bezier curve
	Number of control points is n+1
	0 <= mu < 1
*/
XYZ Bezier(XYZ *p,int n,double mu)
{
   int k,kn,nn,nkn;
   double blend,muk,munk;
   XYZ b = {0.0,0.0,0.0};

	muk = 1;
	munk = pow(1-mu,(double)n);

   for (k=0;k<=n;k++) {
		nn = n;
		kn = k;
		nkn = n - k;
		blend = muk * munk;
		muk *= mu;
		munk /= (1-mu);
		while (nn >= 1) {
			blend *= nn;
			nn--;
			if (kn > 1) {
				blend /= (double)kn;
				kn--;	
			}
			if (nkn > 1) {
				blend /= (double)nkn;
				nkn--;	
			}
		}
      b.x += p[k].x * blend;
      b.y += p[k].y * blend;
      b.z += p[k].z * blend;
   }

   return(b);
}

/*
   Calculate blending function for Bezier curves/surfaces
*/
double BezierBlend(int k,double mu,int n)
{
   int nn,kn,nkn;
   double blend=1;

   nn = n;
   kn = k;
   nkn = n - k;

   while (nn >= 1) {
      blend *= nn;
      nn--;
      if (kn > 1) {
         blend /= (double)kn;
         kn--;
      }
      if (nkn > 1) {
         blend /= (double)nkn;
         nkn--;
      }
   }
   if (k > 0)
      blend *= pow(mu,(double)k);
   if (n-k > 0)
      blend *= pow(1-mu,(double)(n-k));

   return(blend);
}

/*
	Piecewise cubic bezier curve as defined by Adobe in Postscript
	The two end points are p0 and p3
	Their associated control points are p1 and p2
*/
XYZ CubicBezier(XYZ p0,XYZ p1,XYZ p2,XYZ p3,double mu)
{
	XYZ a,b,c,p;

	c.x = 3 * (p1.x - p0.x);
   c.y = 3 * (p1.y - p0.y);
   c.z = 3 * (p1.z - p0.z);
	b.x = 3 * (p2.x - p1.x) - c.x;
	b.y = 3 * (p2.y - p1.y) - c.y;
	b.z = 3 * (p2.z - p1.z) - c.z;
	a.x = p3.x - p0.x - c.x - b.x;
   a.y = p3.y - p0.y - c.y - b.y;
   a.z = p3.z - p0.z - c.z - b.z;

	p.x = a.x * mu * mu * mu + b.x * mu * mu + c.x * mu + p0.x;
   p.y = a.y * mu * mu * mu + b.y * mu * mu + c.y * mu + p0.y;
   p.z = a.z * mu * mu * mu + b.z * mu * mu + c.z * mu + p0.z;

	return(p);
}

/*-------------------------------------------------------------------------
   Return the angle between two vectors on a plane
   The angle is from vector 1 to vector 2, positive anticlockwise
   The result is between -pi -> pi
*/
double Angle2D(double x1, double y1, double x2, double y2)
{
   double dtheta,theta1,theta2;

   theta1 = atan2(y1,x1);
   theta2 = atan2(y2,x2);
   dtheta = theta2 - theta1;
   while (dtheta > PI)
      dtheta -= TWOPI;
   while (dtheta < -PI)
      dtheta += TWOPI;

   return(dtheta);
}

/*-------------------------------------------------------------------------
	Dot product of two vectors in 3 space p1 dot p2
*/
double DotProduct(XYZ p1,XYZ p2)
{
   return(p1.x*p2.x + p1.y*p2.y + p1.z*p2.z);
}

/*-------------------------------------------------------------------------
   Return the angle in radians between two vectors (0..pi)
*/
double VectorAngle(XYZ p1,XYZ p2)
{
	double m1,m2;
   double costheta;

	m1 = MODULUS(p1);
   m2 = MODULUS(p2);
	if (m1*m2 <= EPS)
		return(0.0);
	else
		costheta = (p1.x*p2.x + p1.y*p2.y + p1.z*p2.z) / (m1*m2);

	if (costheta <= -1)
		return(PI);
	else if (costheta >= 1)
		return(0.0);
	else
		return(acos(costheta));
}

/*-------------------------------------------------------------------------
   Cross product between two vectors p = p1 x p2
*/
XYZ CrossProduct(XYZ p1,XYZ p2)
{
   XYZ p;

   p.x = p1.y * p2.z - p1.z * p2.y;
   p.y = p1.z * p2.x - p1.x * p2.z;
   p.z = p1.x * p2.y - p1.y * p2.x;

   return(p);
}

/*-------------------------------------------------------------------------
	Return the distance between two points
*/
double VectorLength(XYZ p1,XYZ p2)
{
	XYZ d;
	
	d.x = p1.x - p2.x;
	d.y = p1.y - p2.y;
	d.z = p1.z - p2.z;

	return(sqrt(d.x*d.x + d.y*d.y + d.z*d.z));
}

/*-------------------------------------------------------------------------
   Set thelength of a vector
*/
void SetVectorLength(XYZ *p,double len)
{
	Normalise(p);
	p->x *= len;
   p->y *= len;
   p->z *= len;
}

/*-------------------------------------------------------------------------
	Calculate the length of a vector
*/
double Modulus(XYZ p)
{
    return(sqrt(p.x * p.x + p.y * p.y + p.z * p.z));
}

/*-------------------------------------------------------------------------
	Normalise a vector
*/
void Normalise(XYZ *p)
{
   double length;

   length = p->x * p->x + p->y * p->y + p->z * p->z;
   if (length > 0) {
		length = sqrt(length);
      p->x /= length;
      p->y /= length;
      p->z /= length;
   } else {
		p->x = 0;
		p->y = 0;
		p->z = 0;
	}	
}

/*-------------------------------------------------------------------------
	Multiply a vector by a constant
*/
XYZ VectorMul(XYZ p,double c)
{
	XYZ p1;

	p1.x = c * p.x;
	p1.y = c * p.y;
	p1.z = c * p.z;

	return(p1);
}

/*-------------------------------------------------------------------------
   Create a vector
*/
void MakeVector(XYZ *p,double x,double y,double z)
{
	p->x = x;
	p->y = y;
	p->z = z;
}

/*-------------------------------------------------------------------------
   Subtract two vectors p = p2 - p1
*/
XYZ VectorSub(XYZ p1,XYZ p2)
{
   XYZ p;

   p.x = p2.x - p1.x;
   p.y = p2.y - p1.y;
   p.z = p2.z - p1.z;

   return(p);
}

/*-------------------------------------------------------------------------
   Add two vectors p = p2 + p1
*/
XYZ VectorAdd(XYZ p1,XYZ p2)
{
   XYZ p;

   p.x = p2.x + p1.x;
   p.y = p2.y + p1.y;
   p.z = p2.z + p1.z;

	return(p);
}

/*-------------------------------------------------------------------------
  Return TRUE if two vectors are "equal" else FALSE
*/
int VectorEqual(XYZ v1,XYZ v2)
{
  if (ABS(v1.x - v2.x) > EPSILON)
    return(FALSE);
  if (ABS(v1.y - v2.y) > EPSILON)
    return(FALSE);
  if (ABS(v1.z - v2.z) > EPSILON)
    return(FALSE);
  return(TRUE);
}

/*-------------------------------------------------------------------------
  Invert a vector
*/
XYZ VectorInvert(XYZ v)
{
	XYZ vi;

	vi.x = -v.x;
	vi.y = -v.y;
	vi.z = -v.z;

	return(vi);
}

/*-------------------------------------------------------------------------
   Force a vector v2 to be perpendicular to v1
*/
void VectorPerp(XYZ v1,XYZ *v2)
{
	XYZ vr;
	double len;

	len = Modulus(*v2);
   vr = CrossProduct(v1,*v2);
   *v2 = CrossProduct(vr,v1);
   Normalise(v2);
	v2->x *= len;
   v2->y *= len;
   v2->z *= len;
}

/*-------------------------------------------------------------------------
   Rotate a 2D vector clockwise about the origin by theta (radians)
*/
XY Vector2DRotate(XY p,double theta)
{
	XY q;

	q.x =  p.x * cos(theta) + p.y * sin(theta);
	q.y = -p.x * sin(theta) + p.y * cos(theta);

	return(q);
}

/*-------------------------------------------------------------------------
	Rotate vectors around each axis
	Clockwise looking into the origin from along the positive axis
*/
XYZ RotateX(XYZ p,double theta)
{
	XYZ q;

	q.x = p.x;
	q.y = p.y * cos(theta) + p.z * sin(theta);
	q.z = -p.y * sin(theta) + p.z * cos(theta);
	return(q);
}
XYZ RotateY(XYZ p,double theta)
{
   XYZ q;

   q.x = p.x * cos(theta) - p.z * sin(theta);
   q.y = p.y;
   q.z = p.x * sin(theta) + p.z * cos(theta);
   return(q);
}
XYZ RotateZ(XYZ p,double theta)
{
   XYZ q;

   q.x = p.x * cos(theta) + p.y * sin(theta);
   q.y = -p.x * sin(theta) + p.y * cos(theta);
	q.z = p.z;
   return(q);
}

/*-------------------------------------------------------------------------
	Return the midpoint between two vectors
*/
XYZ MidPoint(XYZ p1,XYZ p2)
{
	XYZ p;

	p.x = (p1.x + p2.x) / 2;
	p.y = (p1.y + p2.y) / 2;
	p.z = (p1.z + p2.z) / 2;

	return(p);
}

/*-------------------------------------------------------------------------
	Return the centroid of n points in 3 space
*/
XYZ Centroid(XYZ *p,int n)
{
	int i;
	XYZ sum;

	sum.x = 0;
	sum.y = 0;
	sum.z = 0;
	for (i=0;i<n;i++) {
		sum.x += p[i].x;
		sum.y += p[i].y;
		sum.z += p[i].z;
	}
	sum.x /= n;
	sum.y /= n;
	sum.z /= n;

	return(sum);
}

/*-------------------------------------------------------------------------
   This function is due to Rob Fowler.  Given dy and dx between 2 points
   A and B, we calculate a number in [0.0, 8.0) which is a monotonic
   function of the direction from A to B. 

   (0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0) correspond to
   (  0,  45,  90, 135, 180, 225, 270, 315, 360) degrees, measured
   counter-clockwise from the positive x axis.
*/
double FowlerAngle(double dy,double dx)
{
    double adx, ady;    /* Absolute Values of Dx and Dy */
    int    code;        /* Angular Region Classification Code */

    adx = (dx < 0) ? -dx : dx;  /* Compute the absolute values. */
    ady = (dy < 0) ? -dy : dy;

    code = (adx < ady) ? 1 : 0;
    if (dx < 0)  code += 2;
    if (dy < 0)  code += 4;

    switch (code) {
    case 0: return (dx==0) ? 0 : ady/adx;  /* [  0, 45] */
    case 1: return (2.0 - (adx/ady));      /* ( 45, 90] */
    case 3: return (2.0 + (adx/ady));      /* ( 90,135) */
    case 2: return (4.0 - (ady/adx));      /* [135,180] */
    case 6: return (4.0 + (ady/adx));      /* (180,225] */
    case 7: return (6.0 - (adx/ady));      /* (225,270) */
    case 5: return (6.0 + (adx/ady));      /* [270,315) */
    case 4: return (8.0 - (ady/adx));      /* [315,360) */
    }
	 return(0.0);
}

/*-------------------------------------------------------------------------
	Calculate the unit normal at p given two other points 
	p1,p2 on the surface. The normal points in the direction 
	of p1 crossproduct p2
*/
XYZ CalcNormal(XYZ p,XYZ p1,XYZ p2)
{
	XYZ n,pa,pb;

   pa.x = p1.x - p.x;
   pa.y = p1.y - p.y;
   pa.z = p1.z - p.z;
	pb.x = p2.x - p.x;
	pb.y = p2.y - p.y;
	pb.z = p2.z - p.z;
   n.x = pa.y * pb.z - pa.z * pb.y;
   n.y = pa.z * pb.x - pa.x * pb.z;
   n.z = pa.x * pb.y - pa.y * pb.x;
	Normalise(&n);

	return(n);
}

/*
   Calculate the line segment PaPb that is the shortest route between
   two lines P1P2 and P3P4. Calculate also the values of mua and mub where
      Pa = P1 + mua (P2 - P1)
      Pb = P3 + mub (P4 - P3)
   Return FALSE if no solution exists.
*/
int LineLineIntersect(
   XYZ p1,XYZ p2,XYZ p3,XYZ p4,XYZ *pa,XYZ *pb,
   double *mua, double *mub)
{
   XYZ p13,p43,p21;
   double d1343,d4321,d1321,d4343,d2121;
   double numer,denom;

   p13.x = p1.x - p3.x;
   p13.y = p1.y - p3.y;
   p13.z = p1.z - p3.z;
   p43.x = p4.x - p3.x;
   p43.y = p4.y - p3.y;
   p43.z = p4.z - p3.z;
   if (ABS(p43.x) < EPS && ABS(p43.y) < EPS && ABS(p43.z) < EPS)
      return(FALSE);
   p21.x = p2.x - p1.x;
   p21.y = p2.y - p1.y;
   p21.z = p2.z - p1.z;
   if (ABS(p21.x) < EPS && ABS(p21.y) < EPS && ABS(p21.z) < EPS)
      return(FALSE);

   d1343 = p13.x * p43.x + p13.y * p43.y + p13.z * p43.z;
   d4321 = p43.x * p21.x + p43.y * p21.y + p43.z * p21.z;
   d1321 = p13.x * p21.x + p13.y * p21.y + p13.z * p21.z;
   d4343 = p43.x * p43.x + p43.y * p43.y + p43.z * p43.z;
   d2121 = p21.x * p21.x + p21.y * p21.y + p21.z * p21.z;

   denom = d2121 * d4343 - d4321 * d4321;
   if (ABS(denom) < EPS)
      return(FALSE);
   numer = d1343 * d4321 - d1321 * d4343;

   *mua = numer / denom;
   *mub = (d1343 + d4321 * (*mua)) / d4343;

   pa->x = p1.x + *mua * p21.x;
   pa->y = p1.y + *mua * p21.y;
   pa->z = p1.z + *mua * p21.z;
   pb->x = p3.x + *mub * p43.x;
   pb->y = p3.y + *mub * p43.y;
   pb->z = p3.z + *mub * p43.z;

   return(TRUE);
}

/*-------------------------------------------------------------------------
	Find the closest point in a cloud of points
	Return the index to the closest point, the closest point, the distance
*/
int FindClosest(XYZ p,XYZ *poly,int n,double *dmin,XYZ *pmin)
{
	int k,kmin;
	double d;

   *dmin = 1e32;
   kmin = 0;
   for (k=0;k<n;k++) {
      d = VectorLength(poly[k],p);
      if (d < *dmin) {
         *dmin = d;
         kmin = k;
      }
   }

	*pmin = poly[kmin];
	return(kmin);
}

/*-------------------------------------------------------------------------
   Clip a 3 vertex facet in place
   The 3 point facet is defined by vertices p[0],p[1],p[2], "p[3]"
      There must be a fourth point as a 4 point facet may result
   The normal to the plane is n
   A point on the plane is p0
   The side of the plane containing the normal is clipped away
   Return the number of vertices in the clipped polygon
*/
int ClipFacet(XYZ *p,XYZ n,XYZ p0)
{
   double A,B,C,D;
   double l;
   double side[3];
   XYZ q;

   /*
      Determine the equation of the plane as
      Ax + By + Cz + D = 0
   */
   l = sqrt(n.x*n.x + n.y*n.y + n.z*n.z);
   A = n.x / l;
   B = n.y / l;
   C = n.z / l;
   D = -(n.x*p0.x + n.y*p0.y + n.z*p0.z);

   /*
      Evaluate the equation of the plane for each vertex
      If side < 0 then it is on the side to be retained
      else it is to be clippped
   */
   side[0] = A*p[0].x + B*p[0].y + C*p[0].z + D;
   side[1] = A*p[1].x + B*p[1].y + C*p[1].z + D;
   side[2] = A*p[2].x + B*p[2].y + C*p[2].z + D;

   /* Are all the vertices are on the clipped side */
   if (side[0] >= 0 && side[1] >= 0 && side[2] >= 0)
      return(0);

   /* Are all the vertices on the not-clipped side */
   if (side[0] <= 0 && side[1] <= 0 && side[2] <= 0)
      return(3);

   /* Is p0 the only point on the clipped side */
   if (side[0] > 0 && side[1] < 0 && side[2] < 0) {
      q.x = p[0].x - side[0] * (p[2].x - p[0].x) / (side[2] - side[0]);
      q.y = p[0].y - side[0] * (p[2].y - p[0].y) / (side[2] - side[0]);
      q.z = p[0].z - side[0] * (p[2].z - p[0].z) / (side[2] - side[0]);
      p[3] = q;
      q.x = p[0].x - side[0] * (p[1].x - p[0].x) / (side[1] - side[0]);
      q.y = p[0].y - side[0] * (p[1].y - p[0].y) / (side[1] - side[0]);
      q.z = p[0].z - side[0] * (p[1].z - p[0].z) / (side[1] - side[0]);
      p[0] = q;
      return(4);
   }

   /* Is p1 the only point on the clipped side */
   if (side[1] > 0 && side[0] < 0 && side[2] < 0) {
      p[3] = p[2];
      q.x = p[1].x - side[1] * (p[2].x - p[1].x) / (side[2] - side[1]);
      q.y = p[1].y - side[1] * (p[2].y - p[1].y) / (side[2] - side[1]);
      q.z = p[1].z - side[1] * (p[2].z - p[1].z) / (side[2] - side[1]);
      p[2] = q;
      q.x = p[1].x - side[1] * (p[0].x - p[1].x) / (side[0] - side[1]);
      q.y = p[1].y - side[1] * (p[0].y - p[1].y) / (side[0] - side[1]);
      q.z = p[1].z - side[1] * (p[0].z - p[1].z) / (side[0] - side[1]);
      p[1] = q;
      return(4);
   }

   /* Is p2 the only point on the clipped side */
   if (side[2] > 0 && side[0] < 0 && side[1] < 0) {
      q.x = p[2].x - side[2] * (p[0].x - p[2].x) / (side[0] - side[2]);
      q.y = p[2].y - side[2] * (p[0].y - p[2].y) / (side[0] - side[2]);
      q.z = p[2].z - side[2] * (p[0].z - p[2].z) / (side[0] - side[2]);
      p[3] = q;
      q.x = p[2].x - side[2] * (p[1].x - p[2].x) / (side[1] - side[2]);
      q.y = p[2].y - side[2] * (p[1].y - p[2].y) / (side[1] - side[2]);
      q.z = p[2].z - side[2] * (p[1].z - p[2].z) / (side[1] - side[2]);
      p[2] = q;
      return(4);
   }

   /* Is p0 the only point on the not-clipped side */
   if (side[0] < 0 && side[1] > 0 && side[2] > 0) {
      q.x = p[0].x - side[0] * (p[1].x - p[0].x) / (side[1] - side[0]);
      q.y = p[0].y - side[0] * (p[1].y - p[0].y) / (side[1] - side[0]);
      q.z = p[0].z - side[0] * (p[1].z - p[0].z) / (side[1] - side[0]);
      p[1] = q;
      q.x = p[0].x - side[0] * (p[2].x - p[0].x) / (side[2] - side[0]);
      q.y = p[0].y - side[0] * (p[2].y - p[0].y) / (side[2] - side[0]);
      q.z = p[0].z - side[0] * (p[2].z - p[0].z) / (side[2] - side[0]);
      p[2] = q;
      return(3);
   }

   /* Is p1 the only point on the not-clipped side */
   if (side[1] < 0 && side[0] > 0 && side[2] > 0) {
      q.x = p[1].x - side[1] * (p[0].x - p[1].x) / (side[0] - side[1]);
      q.y = p[1].y - side[1] * (p[0].y - p[1].y) / (side[0] - side[1]);
      q.z = p[1].z - side[1] * (p[0].z - p[1].z) / (side[0] - side[1]);
      p[0] = q;
      q.x = p[1].x - side[1] * (p[2].x - p[1].x) / (side[2] - side[1]);
      q.y = p[1].y - side[1] * (p[2].y - p[1].y) / (side[2] - side[1]);
      q.z = p[1].z - side[1] * (p[2].z - p[1].z) / (side[2] - side[1]);
      p[2] = q;
      return(3);
   }

   /* Is p2 the only point on the not-clipped side */
   if (side[2] < 0 && side[0] > 0 && side[1] > 0) {
      q.x = p[2].x - side[2] * (p[1].x - p[2].x) / (side[1] - side[2]);
      q.y = p[2].y - side[2] * (p[1].y - p[2].y) / (side[1] - side[2]);
      q.z = p[2].z - side[2] * (p[1].z - p[2].z) / (side[1] - side[2]);
      p[1] = q;
      q.x = p[2].x - side[2] * (p[0].x - p[2].x) / (side[0] - side[2]);
      q.y = p[2].y - side[2] * (p[0].y - p[2].y) / (side[0] - side[2]);
      q.z = p[2].z - side[2] * (p[0].z - p[2].z) / (side[0] - side[2]);
      p[0] = q;
      return(3);
   }

   /* Shouldn't get here */
   return(-1);
}

/*-------------------------------------------------------------------------
	Clip a line segment p1,p2 to a bounding box (axis aligned)
	Return FALSE if the line segment isn't included in the box,
	The box is defined by its minimum and maximum corners, bmin and bmax
*/
int ClipLine2Box(XYZ *p1,XYZ *p2,XYZ bmin,XYZ bmax)
{
	double mu;

	/* Clip to bmin.x and bmax.x */
	if (p1->x > p2->x)
		SwapXYZ(p1,p2);
	if (p1->x >= bmax.x || p2->x <= bmin.x)
		return(FALSE);
	if (p1->x < bmin.x && p2->x > bmin.x) {
		mu = (bmin.x - p1->x) / (p2->x - p1->x);
		p1->x = bmin.x;
		p1->y = p1->y + mu * (p2->y - p1->y);
		p1->z = p1->z + mu * (p2->z - p1->z);
	}
	if (p1->x < bmax.x && p2->x > bmax.x) {
		mu = (bmax.x - p1->x) / (p2->x - p1->x);
		p2->x = bmax.x;
		p2->y = p1->y + mu * (p2->y - p1->y);
		p2->z = p1->z + mu * (p2->z - p1->z);
	}
	
   /* Clip to bmin.y and bmax.y */
   if (p1->y > p2->y)
      SwapXYZ(p1,p2);
	if (p1->y >= bmax.y || p2->y <= bmin.y)
		return(FALSE);
   if (p1->y < bmin.y && p2->y > bmin.y) {
      mu = (bmin.y - p1->y) / (p2->y - p1->y);
      p1->x = p1->x + mu * (p2->x - p1->x);
      p1->y = bmin.y;
      p1->z = p1->z + mu * (p2->z - p1->z);
   }
   if (p1->y < bmax.y && p2->y > bmax.y) {
      mu = (bmax.y - p1->y) / (p2->y - p1->y);
      p2->x = p1->x + mu * (p2->x - p1->x);
      p2->y = bmax.y;
      p2->z = p1->z + mu * (p2->z - p1->z);
   } 

   /* Clip to bmin.z and bmax.z */
   if (p1->z > p2->z)
      SwapXYZ(p1,p2);
	if (p1->z >= bmax.z || p2->z <= bmin.z)
		return(FALSE);
   if (p1->z < bmin.z && p2->z > bmin.z) {
      mu = (bmin.z - p1->z) / (p2->z - p1->z);
      p1->x = p1->x + mu * (p2->x - p1->x);
      p1->y = p1->y + mu * (p2->y - p1->y);
      p1->z = bmin.z;
   }
   if (p1->z < bmax.z && p2->z > bmax.z) {
      mu = (bmax.z - p1->z) / (p2->z - p1->z);
      p2->x = p1->x + mu * (p2->x - p1->x);
      p2->y = p1->y + mu * (p2->y - p1->y);
      p2->z = bmax.z;
   }

	if ((p1->x < bmin.x && p2->x < bmin.x) ||
       (p1->y < bmin.y && p2->y < bmin.y) ||
		 (p1->z < bmin.z && p2->z < bmin.z) ||
		 (p1->x > bmax.x && p2->x > bmax.x) ||
		 (p1->y > bmax.y && p2->y > bmax.y) ||
		 (p1->z > bmax.z && p2->z > bmax.z))
		return(FALSE);

	return(TRUE);
}

/*-------------------------------------------------------------------------
	Return true if the point p is inside the box with 
	opposite vertices pmin,pmax
*/
int PointInBox(XYZ p,XYZ pmin,XYZ pmax)
{
	if (p.x <= pmin.x || p.x >= pmax.x)
   	return(FALSE);
   if (p.y <= pmin.y || p.y >= pmax.y)
 		return(FALSE);
   if (p.z <= pmin.z || p.z >= pmax.z)
		return(FALSE); 
	return(TRUE);
}

/*-------------------------------------------------------------------------
   Create a contour slice through a 3 vertex facet "p"
   Given the normal of the cutting plane "n" and a point on the plane "p0"
   Return
       0 if the contour plane doesn't cut the facet
       2 if it does cut the facet, the contour line segment is p1->p2
      -1 for an unexpected occurence
   If a vertex touches the contour plane nothing need to be drawn!?
   Note: the following has been written as a "stand alone" piece of
   code that will work but is far from efficient....
*/
int ContourFacet(XYZ *p,XYZ n,XYZ p0,XYZ *p1,XYZ *p2)
{
   double A,B,C,D;
   double l;
   double side[3];

   /*
      Determine the equation of the plane as
      Ax + By + Cz + D = 0
   */
   l = sqrt(n.x*n.x + n.y*n.y + n.z*n.z);
   A = n.x / l;
   B = n.y / l;
   C = n.z / l;
   D = -(n.x*p0.x + n.y*p0.y + n.z*p0.z);

   /*
      Evaluate the equation of the plane for each vertex
      If side < 0 then it is on the side to be retained
      else it is to be clippped
   */
   side[0] = A*p[0].x + B*p[0].y + C*p[0].z + D;
   side[1] = A*p[1].x + B*p[1].y + C*p[1].z + D;
   side[2] = A*p[2].x + B*p[2].y + C*p[2].z + D;

   /* Are all the vertices on one side */
   if (side[0] >= 0 && side[1] >= 0 && side[2] >= 0)
      return(0);
   if (side[0] <= 0 && side[1] <= 0 && side[2] <= 0)
      return(0);

   /* Is p0 the only point on a side by itself */
   if ((SIGN(side[0]) != SIGN(side[1])) && (SIGN(side[0]) != SIGN(side[2]))) {
      p1->x = p[0].x - side[0] * (p[2].x - p[0].x) / (side[2] - side[0]);
      p1->y = p[0].y - side[0] * (p[2].y - p[0].y) / (side[2] - side[0]);
      p1->z = p[0].z - side[0] * (p[2].z - p[0].z) / (side[2] - side[0]);
      p2->x = p[0].x - side[0] * (p[1].x - p[0].x) / (side[1] - side[0]);
      p2->y = p[0].y - side[0] * (p[1].y - p[0].y) / (side[1] - side[0]);
      p2->z = p[0].z - side[0] * (p[1].z - p[0].z) / (side[1] - side[0]);
      return(2);
   }

   /* Is p1 the only point on a side by itself */
   if ((SIGN(side[1]) != SIGN(side[0])) && (SIGN(side[1]) != SIGN(side[2]))) {
      p1->x = p[1].x - side[1] * (p[2].x - p[1].x) / (side[2] - side[1]);
      p1->y = p[1].y - side[1] * (p[2].y - p[1].y) / (side[2] - side[1]);
      p1->z = p[1].z - side[1] * (p[2].z - p[1].z) / (side[2] - side[1]);
      p2->x = p[1].x - side[1] * (p[0].x - p[1].x) / (side[0] - side[1]);
      p2->y = p[1].y - side[1] * (p[0].y - p[1].y) / (side[0] - side[1]);
      p2->z = p[1].z - side[1] * (p[0].z - p[1].z) / (side[0] - side[1]);
      return(2);
   }

   /* Is p2 the only point on a side by itself */
   if ((SIGN(side[2]) != SIGN(side[0])) && (SIGN(side[2]) != SIGN(side[1]))) {
      p1->x = p[2].x - side[2] * (p[0].x - p[2].x) / (side[0] - side[2]);
      p1->y = p[2].y - side[2] * (p[0].y - p[2].y) / (side[0] - side[2]);
      p1->z = p[2].z - side[2] * (p[0].z - p[2].z) / (side[0] - side[2]);
      p2->x = p[2].x - side[2] * (p[1].x - p[2].x) / (side[1] - side[2]);
      p2->y = p[2].y - side[2] * (p[1].y - p[2].y) / (side[1] - side[2]);
      p2->z = p[2].z - side[2] * (p[1].z - p[2].z) / (side[1] - side[2]);
      return(2);
   }

	/* Shouldn't get here */
	return(-1);
}

/*-------------------------------------------------------------------------
   Determine whether or not the line segment p1,p2
   Intersects the 3 vertex facet bounded by pa,pb,pc
   Return true/false and the intersection point p
   The equation of the line is p = p1 + mu (p2 - p1)
   The equation of the plane is a x + b y + c z + d = 0
                                n.x x + n.y y + n.z z + d = 0
*/
int LineFacet(XYZ p1,XYZ p2,XYZ pa,XYZ pb,XYZ pc,XYZ *p)
{
   double d;
   double a1,a2,a3;
   double total,denom,mu;
   XYZ n,pa1,pa2,pa3;

   /* Make sure the arguments are OK */
   if (VectorLength(p1,p2) < EPS)
      return(FALSE);
	if (VectorLength(pa,pb) < EPS || 
		 VectorLength(pb,pc) < EPS || 
		 VectorLength(pc,pa) < EPS)
		return(FALSE);

   /* Calculate the parameters for the plane */
   n.x = (pb.y - pa.y)*(pc.z - pa.z) - (pb.z - pa.z)*(pc.y - pa.y);
   n.y = (pb.z - pa.z)*(pc.x - pa.x) - (pb.x - pa.x)*(pc.z - pa.z);
   n.z = (pb.x - pa.x)*(pc.y - pa.y) - (pb.y - pa.y)*(pc.x - pa.x);
	if (ABS(n.x) < EPS && ABS(n.y) < EPS && ABS(n.z) < EPS)
		return(FALSE);
	Normalise(&n);
   d = - n.x * pa.x - n.y * pa.y - n.z * pa.z;

   /* Calculate the position on the line that intersects the plane */
   denom = n.x * (p2.x - p1.x) + n.y * (p2.y - p1.y) + n.z * (p2.z - p1.z);
   if (ABS(denom) < EPS)         /* Line and plane don't intersect */
      return(FALSE);
   mu = - (d + n.x * p1.x + n.y * p1.y + n.z * p1.z) / denom;
   p->x = p1.x + mu * (p2.x - p1.x);
   p->y = p1.y + mu * (p2.y - p1.y);
   p->z = p1.z + mu * (p2.z - p1.z);
   if (mu < 0 || mu > 1)   /* Intersection not along line segment */
      return(FALSE);

   /* Determine whether or not the intersection point is bounded by pa,pb,pc */
   pa1.x = pa.x - p->x;
   pa1.y = pa.y - p->y;
   pa1.z = pa.z - p->z;
   Normalise(&pa1);
   pa2.x = pb.x - p->x;
   pa2.y = pb.y - p->y;
   pa2.z = pb.z - p->z;
   Normalise(&pa2);
   pa3.x = pc.x - p->x;
   pa3.y = pc.y - p->y;
   pa3.z = pc.z - p->z;
   Normalise(&pa3);
   a1 = pa1.x*pa2.x + pa1.y*pa2.y + pa1.z*pa2.z;
   a2 = pa2.x*pa3.x + pa2.y*pa3.y + pa2.z*pa3.z;
   a3 = pa3.x*pa1.x + pa3.y*pa1.y + pa3.z*pa1.z;
   total = (acos(a1) + acos(a2) + acos(a3)) * RTOD;
   if (ABS(total - 360) > EPS)
      return(FALSE);

   return(TRUE);
}

/*-------------------------------------------------------------------------
	Determine the intersection point of two line segments
	Return FALSE if the lines don't intersect
*/
int LineIntersect(
double x1, double y1, 
double x2, double y2, 
double x3, double y3,
double x4, double y4,
double *x, double *y)
{
	double mua,mub;
	double denom,numera,numerb;

	denom  = (y4-y3) * (x2-x1) - (x4-x3) * (y2-y1);
	numera = (x4-x3) * (y1-y3) - (y4-y3) * (x1-x3);
	numerb = (x2-x1) * (y1-y3) - (y2-y1) * (x1-x3);
	
	/* Are the line coincident? */
	if (ABS(numera) < EPS && ABS(numerb) < EPS && ABS(denom) < EPS) {
		*x = (x1 + x2) / 2;
		*y = (y1 + y2) / 2;
		return(TRUE);
	}

	/* Are the line parallel */
	if (ABS(denom) < EPS) {
		*x = 0;
		*y = 0;
		return(FALSE);
	}

	/* Is the intersection along the the segments */
	mua = numera / denom;
	mub = numerb / denom;
	if (mua < 0 || mua > 1 || mub < 0 || mub > 1) {
		*x = 0;
		*y = 0;
		return(FALSE);
	}
	*x = x1 + mua * (x2 - x1);
	*y = y1 + mua * (y2 - y1);
	return(TRUE);
}

/*-------------------------------------------------------------------------
	Return the distance of a point to a line and the closest point
	The point in question is p3, the line is between p1 and p2.
*/
double PointLine2D(XY p3,XY p1,XY p2,XY *close,double *mu)
{
   double dx,dy,dd;

   dx = p2.x - p1.x;
   dy = p2.y - p1.y;
   dd = dx * dx + dy * dy;

   /* Are the two points p1 and p2 coincident? */
   if (dd < EPS) {
      *mu = 0;
      close->x = p1.x;
      close->y = p1.y;
   } else {
      *mu = ((p3.x - p1.x) * dx + (p3.y - p1.y) * dy) / dd;
      close->x = p1.x + (*mu) * dx;
      close->y = p1.y + (*mu) * dy;
   }
   dx = p3.x - close->x;
   dy = p3.y - close->y;
   return(sqrt(dx * dx + dy * dy));
}
double PointLine3D(XYZ p3,XYZ p1,XYZ p2,XYZ *close,double *mu)
{
   double dx,dy,dz,dd;

   dx = p2.x - p1.x;
   dy = p2.y - p1.y;
	dz = p2.z - p1.z;
   dd = dx * dx + dy * dy + dz * dz;

   /* Are the two points p1 and p2 coincident? */
   if (dd < EPS) {
      *mu = 0;
      close->x = p1.x;
      close->y = p1.y;
		close->z = p1.z;
   } else {
      *mu = ((p3.x - p1.x)*dx + (p3.y - p1.y)*dy + (p3.z - p1.z)*dz) / dd;
      close->x = p1.x + (*mu) * dx;
      close->y = p1.y + (*mu) * dy;
		close->z = p1.z + (*mu) * dz;
   }
   dx = p3.x - close->x;
   dy = p3.y - close->y;
	dz = p3.z - close->z;
   return(sqrt(dx * dx + dy * dy + dz * dz));
}

/*
	Return the clockwise status of a curve, clockwise or anticlockwise
	n vertices making up curve p
          CLOCKWISE == 1
          ANTICLOCKWISE == -1
*/
int ClockWise(XY *p,int n)
{
	int i,j;
	double area=0;

	if (n < 3)
		return(0);

	for (i=0;i<n;i++) {
		j = (i + 1) % n;
		area += (p[i].x*p[j].y - p[j].x*p[i].y);
	}
	if (area < 0)
		return(CLOCKWISE);
	else if (area > 0)
		return(ANTICLOCKWISE);
	else
		return(0);
} 

/*
	Return whether a polygon in 2D is concave or convex
   return 0 for incomputables eg: colinear points
          CLOCKWISE == 1
          ANTICLOCKWISE == -1
   It is assumed that the polygon is simple 
	(does not intersect itself or have holes)
*/
int ConvexPolygon(XY *p,int n)
{
   int i,j,k;
   int flag = 0;
   double z;

   if (n < 3)
      return(0);

   for (i=0;i<n;i++) {
      j = (i + 1) % n;
      k = (i + 2) % n;
      z  = (p[j].x - p[i].x) * (p[k].y - p[j].y);
      z -= (p[j].y - p[i].y) * (p[k].x - p[j].x);
      if (z < 0)
         flag |= 1;
      else if (z > 0)
         flag |= 2;
      if (flag == 3)
	  		return(CONCAVE);
   }
   if (flag != 0)
      return(CONVEX);
   else
      return(0);
}

/*
	Cartesian to polar (degrees) coordinates
*/
void XYZ2Polar(
	double x,double y,double z,
	double *range,double *above,double *about)
{
	double dr;
	
	*range = sqrt(x*x + y*y + z*z);
	if (*range < EPSILON) {
		*above = 0.0;
		*about = 0.0;
	} else {
		*above = asin(z / *range);
		dr = *range * cos(*above);
		if (ABS(dr) < EPSILON) {
			*about = 0.0;
		} else {
			*about = asin(y / dr);
		}
	}
	
	*about *= RTOD;
	*above *= RTOD;
	*about += 90;
}

/*
	Spherical coordinates theta,phi from vector
*/
void InverseSpherical(XYZ p,double *theta,double *phi)
{
   Normalise(&p);
   *theta = PI + atan2(p.y,p.x);
   *phi = atan2(p.z,sqrt(p.x*p.x+p.y*p.y));
}

/*
	Polar (degrees) to cartesian coordinates
*/
void Polar2XYZ(
	double range,double above,double about,
	double *x,double *y,double *z)
{
	about -= 90;
	*x = range * cos(above*DTOR) * cos(about*DTOR);
	*y = range * cos(above*DTOR) * sin(about*DTOR);
	*z = range * sin(above*DTOR);
}

/*
   Triangulation subroutine
   Takes as input NV vertices in array pxyz
   Returned is a list of ntri triangular faces in the array v
   These triangles are arranged in a consistent clockwise order.
	The triangle array 'v' should be malloced to 3 * nv
	The vertex array pxyz must be big enough to hold 3 more points
	The vertex array must be sorted in increasing x values

	qsort(p,nv,sizeof(XYZ),XYZCompare);
      :
	int XYZCompare(void *v1,void *v2)
	{
   	XYZ *p1,*p2;
   	p1 = v1;
   	p2 = v2;
   	if (p1->x < p2->x)
   	   return(-1);
   	else if (p1->x > p2->x)
   	   return(1);
   	else
   	   return(0);
	}
*/
int Triangulate(int nv,XYZ *pxyz,ITRIANGLE *v,int *ntri)
{
   int *complete = NULL;
   IEDGE *edges = NULL;
   int nedge = 0;
	int trimax,emax = 200;
	int status = 0;

   int inside;
   int i,j,k;
   double xp,yp,x1,y1,x2,y2,x3,y3,xc,yc,r;
   double xmin,xmax,ymin,ymax,xmid,ymid;
   double dx,dy,dmax;

   /* Allocate memory for the completeness list, flag for each triangle */
	trimax = 4 * nv;
   if ((complete = (int *)malloc(trimax*sizeof(int))) == NULL) {
		status = 1;
      goto skip;
   }

   /* Allocate memory for the edge list */
   if ((edges = (IEDGE *)malloc(emax*(int)sizeof(EDGE))) == NULL) {
      status = 2;
      goto skip;
   }

   /*
      Find the maximum and minimum vertex bounds.
      This is to allow calculation of the bounding triangle
   */
   xmin = pxyz[0].x;
   ymin = pxyz[0].y;
   xmax = xmin;
   ymax = ymin;
   for (i=1;i<nv;i++) {
      if (pxyz[i].x < xmin) xmin = pxyz[i].x;
      if (pxyz[i].x > xmax) xmax = pxyz[i].x;
      if (pxyz[i].y < ymin) ymin = pxyz[i].y;
      if (pxyz[i].y > ymax) ymax = pxyz[i].y;
   }
   dx = xmax - xmin;
   dy = ymax - ymin;
   dmax = (dx > dy) ? dx : dy;
   xmid = (xmax + xmin) / 2.0;
   ymid = (ymax + ymin) / 2.0;

   /*
      Set up the supertriangle
      This is a triangle which encompasses all the sample points.
      The supertriangle coordinates are added to the end of the
      vertex list. The supertriangle is the first triangle in
      the triangle list.
   */
   pxyz[nv+0].x = xmid - 20 * dmax;
   pxyz[nv+0].y = ymid - dmax;
   pxyz[nv+0].z = 0.0;
   pxyz[nv+1].x = xmid;
   pxyz[nv+1].y = ymid + 20 * dmax;
   pxyz[nv+1].z = 0.0;
   pxyz[nv+2].x = xmid + 20 * dmax;
   pxyz[nv+2].y = ymid - dmax;
   pxyz[nv+2].z = 0.0;
   v[0].p1 = nv;
   v[0].p2 = nv+1;
   v[0].p3 = nv+2;
   complete[0] = FALSE;
   *ntri = 1;

   /*
      Include each point one at a time into the existing mesh
   */
   for (i=0;i<nv;i++) {
		if (i % (nv/100) == 0)
			fprintf(stderr,"Triangulated %d of %d\n",i,nv);

      xp = pxyz[i].x;
      yp = pxyz[i].y;
      nedge = 0;

      /*
         Set up the edge buffer.
         If the point (xp,yp) lies inside the circumcircle then the
         three edges of that triangle are added to the edge buffer
         and that triangle is removed.
      */
      for (j=0;j<(*ntri);j++) {
         if (complete[j]) 
				continue;
         x1 = pxyz[v[j].p1].x;
         y1 = pxyz[v[j].p1].y;
         x2 = pxyz[v[j].p2].x;
         y2 = pxyz[v[j].p2].y;
         x3 = pxyz[v[j].p3].x;
         y3 = pxyz[v[j].p3].y;
         inside = CircumCircle(xp,yp,x1,y1,x2,y2,x3,y3,&xc,&yc,&r);
         // was if (xc + r < xp)
			if (xc < xp && ((xp-xc)*(xp-xc)) > r)
            complete[j] = TRUE;
         if (inside) {
            /* Check that we haven't exceeded the edge list size */
            if (nedge+3 >= emax) {
					emax += 100;
					if ((edges = (IEDGE *)realloc(edges,emax*(int)sizeof(EDGE))) == NULL) {
						status = 3;
						goto skip;
					}
            }
            edges[nedge+0].p1 = v[j].p1;
            edges[nedge+0].p2 = v[j].p2;
            edges[nedge+1].p1 = v[j].p2;
            edges[nedge+1].p2 = v[j].p3;
            edges[nedge+2].p1 = v[j].p3;
            edges[nedge+2].p2 = v[j].p1;
            nedge += 3;
            v[j] = v[(*ntri)-1];
            complete[j] = complete[(*ntri)-1];
            (*ntri)--;
				j--;
         }
		} 

      /*
         Tag multiple edges
         Note: if all triangles are specified anticlockwise then all
               interior edges are opposite pointing in direction.
      */
      for (j=0;j<nedge-1;j++) {
         for (k=j+1;k<nedge;k++) {
            if ((edges[j].p1 == edges[k].p2) && (edges[j].p2 == edges[k].p1)) {
               edges[j].p1 = -1;
               edges[j].p2 = -1;
               edges[k].p1 = -1;
               edges[k].p2 = -1;
            }
            /* Shouldn't need the following, see note above */
            if ((edges[j].p1 == edges[k].p1) && (edges[j].p2 == edges[k].p2)) {
               edges[j].p1 = -1;
               edges[j].p2 = -1;
               edges[k].p1 = -1;
               edges[k].p2 = -1;
            }
         }
      }

      /*
         Form new triangles for the current point
         Skipping over any tagged edges.
         All edges are arranged in clockwise order.
      */
      for (j=0;j<nedge;j++) {
         if (edges[j].p1 < 0 || edges[j].p2 < 0) 
				continue;
         if ((*ntri) >= trimax) {
            status = 4;
				goto skip;
        	}
         v[*ntri].p1 = edges[j].p1;
         v[*ntri].p2 = edges[j].p2;
         v[*ntri].p3 = i;
         complete[*ntri] = FALSE;
			(*ntri)++;
      }
   }

   /*
      Remove triangles with supertriangle vertices
      These are triangles which have a vertex number greater than nv
   */
   for (i=0;i<(*ntri);i++) {
      if (v[i].p1 >= nv || v[i].p2 >= nv || v[i].p3 >= nv) {
         v[i] = v[(*ntri)-1];
         (*ntri)--;
			i--;
      }
   }

skip:
   free(edges);
   free(complete);
	return(status);
}

/*
   Return TRUE if a point (xp,yp) is inside the circumcircle made up
   of the points (x1,y1), (x2,y2), (x3,y3)
   The circumcircle centre is returned in (xc,yc) and the radius r
   NOTE: A point on the edge is inside the circumcircle
*/
int CircumCircle(double xp,double yp,
   double x1,double y1,double x2,double y2,double x3,double y3,
   double *xc,double *yc,double *rsqr)
{
   double m1,m2,mx1,mx2,my1,my2;
   double dx,dy,drsqr;
   double fabsy1y2 = fabs(y1-y2);
   double fabsy2y3 = fabs(y2-y3);

   /* Check for coincident points */
   if (fabsy1y2 < EPSILON && fabsy2y3 < EPSILON)
       return(FALSE);

   if (fabsy1y2 < EPSILON) {
      m2 = - (x3-x2) / (y3-y2);
      mx2 = (x2 + x3) / 2.0;
      my2 = (y2 + y3) / 2.0;
      *xc = (x2 + x1) / 2.0;
      *yc = m2 * (*xc - mx2) + my2;
   } else if (fabsy2y3 < EPSILON) {
      m1 = - (x2-x1) / (y2-y1);
      mx1 = (x1 + x2) / 2.0;
      my1 = (y1 + y2) / 2.0;
      *xc = (x3 + x2) / 2.0;
      *yc = m1 * (*xc - mx1) + my1;
   } else {
      m1 = - (x2-x1) / (y2-y1);
      m2 = - (x3-x2) / (y3-y2);
      mx1 = (x1 + x2) / 2.0;
      mx2 = (x2 + x3) / 2.0;
      my1 = (y1 + y2) / 2.0;
      my2 = (y2 + y3) / 2.0;
      *xc = (m1 * mx1 - m2 * mx2 + my2 - my1) / (m1 - m2);
      if (fabsy1y2 > fabsy2y3) {
         *yc = m1 * (*xc - mx1) + my1;
      }
      else {
         *yc = m2 * (*xc - mx2) + my2;
      }
   }

   dx = x2 - *xc;
   dy = y2 - *yc;
   *rsqr = dx*dx + dy*dy;

   dx = xp - *xc;
   dy = yp - *yc;
   drsqr = dx*dx + dy*dy;

   //return((drsqr <= *rsqr) ? TRUE : FALSE);
	// Proposed by Chuck Morris
 	return((drsqr - *rsqr) <= EPSILON ? TRUE : FALSE);
}

/*
	Rotate a point p by angle theta around an arbitrary normal r
	Return the rotated point.
	Positive angles are anticlockwise looking down the axis towards the origin.
   Assume right hand coordinate system.  
*/
XYZ ArbitraryRotate(XYZ p,double theta,XYZ r)
{
	XYZ q = {0.0,0.0,0.0};
	double costheta,sintheta;

	Normalise(&r);
	costheta = cos(theta);
	sintheta = sin(theta);

	q.x += (costheta + (1 - costheta) * r.x * r.x) * p.x;
	q.x += ((1 - costheta) * r.x * r.y - r.z * sintheta) * p.y;
	q.x += ((1 - costheta) * r.x * r.z + r.y * sintheta) * p.z;

	q.y += ((1 - costheta) * r.x * r.y + r.z * sintheta) * p.x;
	q.y += (costheta + (1 - costheta) * r.y * r.y) * p.y;
	q.y += ((1 - costheta) * r.y * r.z - r.x * sintheta) * p.z;

	q.z += ((1 - costheta) * r.x * r.z - r.y * sintheta) * p.x;
	q.z += ((1 - costheta) * r.y * r.z + r.x * sintheta) * p.y;
	q.z += (costheta + (1 - costheta) * r.z * r.z) * p.z;

	return(q);
}

/*
   Rotate a point p by angle theta around an arbitrary line segment p1-p2
   Return the rotated point.
   Positive angles are anticlockwise looking down the axis
   towards the origin.
   Assume right hand coordinate system.  
*/
XYZ ArbitraryRotate2(XYZ p,double theta,XYZ p1,XYZ p2)
{
   XYZ q = {0.0,0.0,0.0};
   double costheta,sintheta;
	XYZ r;
	
	r.x = p2.x - p1.x;
   r.y = p2.y - p1.y;
   r.z = p2.z - p1.z;
	p.x -= p1.x;
	p.y -= p1.y;
	p.z -= p1.z;
   Normalise(&r);

   costheta = cos(theta);
   sintheta = sin(theta);

   q.x += (costheta + (1 - costheta) * r.x * r.x) * p.x;
   q.x += ((1 - costheta) * r.x * r.y - r.z * sintheta) * p.y;
   q.x += ((1 - costheta) * r.x * r.z + r.y * sintheta) * p.z;

   q.y += ((1 - costheta) * r.x * r.y + r.z * sintheta) * p.x;
   q.y += (costheta + (1 - costheta) * r.y * r.y) * p.y;
   q.y += ((1 - costheta) * r.y * r.z - r.x * sintheta) * p.z;

   q.z += ((1 - costheta) * r.x * r.z - r.y * sintheta) * p.x;
   q.z += ((1 - costheta) * r.y * r.z + r.x * sintheta) * p.y;
   q.z += (costheta + (1 - costheta) * r.z * r.z) * p.z;

	q.x += p1.x;
	q.y += p1.y;
	q.z += p1.z;
   return(q);
}
XYZ ArbitraryRotate3(XYZ p,double theta,XYZ p1,XYZ p2)
{
   XYZ u,q1,q2;
   double d;

   /* Step 1 */
   q1.x = p.x - p1.x;
   q1.y = p.y - p1.y;
   q1.z = p.z - p1.z;

   u.x = p2.x - p1.x;
   u.y = p2.y - p1.y;
   u.z = p2.z - p1.z;
   Normalise(&u);
   d = sqrt(u.y*u.y + u.z*u.z);

   /* Step 2 */
   if (ABS(d) > 0.000001) {
      q2.x = q1.x;
      q2.y = q1.y * u.z / d - q1.z * u.y / d;
      q2.z = q1.y * u.y / d + q1.z * u.z / d;
   } else {
      q2 = q1;
   }

   /* Step 3 */
   q1.x = q2.x * d - q2.z * u.x;
   q1.y = q2.y;
   q1.z = q2.x * u.x + q2.z * d;

   /* Step 4 */
   q2.x = q1.x * cos(theta) - q1.y * sin(theta);
   q2.y = q1.x * sin(theta) + q1.y * cos(theta);
   q2.z = q1.z;

   /* Inverse of step 3 */
   q1.x =   q2.x * d + q2.z * u.x;
   q1.y =   q2.y;
   q1.z = - q2.x * u.x + q2.z * d;

   /* Inverse of step 2 */
   if (ABS(d) > 0.000001) {
      q2.x =   q1.x;
      q2.y =   q1.y * u.z / d + q1.z * u.y / d;
      q2.z = - q1.y * u.y / d + q1.z * u.z / d;
   } else {
      q2 = q1;
   }

   /* Inverse of step 1 */
   q1.x = q2.x + p1.x;
   q1.y = q2.y + p1.y;
   q1.z = q2.z + p1.z;
   return(q1);
}

double LinearInterpolate(double y1,double y2,double mu)
{
   return(y1 * (1 - mu) + y2 * mu);
}

double CosineInterpolate(double y1,double y2,double mu)
{
   double mu2;

   mu2 = (1 - cos(mu*PI)) / 2;
   return(y1 * (1 - mu2) + y2 * mu2);
}

double CubicInterpolate(double y0,double y1,double y2,double y3,double mu)
{
   double a0,a1,a2,a3;

   a0 = y3 - y2 - y0 + y1;
   a1 = y0 - y1 - a0;
   a2 = y2 - y0;
   a3 = y1;

   return(a0*mu*mu*mu + a1*mu*mu + a2*mu + a3);
}

/* 
	Tension: 1 is high, 0 normal, -1 is low
	Bias: 0 is even, positive is towards first segment, 
   negative towards the other 
*/
double HermiteInterpolate(double y0,double y1,double y2,double y3,
   double mu,double tension,double bias)
{
   double m0,m1,mu2,mu3;
   double a0,a1,a2,a3;

	mu2 = mu * mu;
	mu3 = mu2 * mu;
   m0  = (y1 - y0) * (1 + bias) * (1 - tension) / 2;
   m0 += (y2 - y1) * (1 - bias) * (1 - tension) / 2;
   m1  = (y2 - y1) * (1 + bias) * (1 - tension) / 2;
   m1 += (y3 - y2) * (1 - bias) * (1 - tension) / 2;
   a0 =  2*mu3 - 3*mu2 + 1;
   a1 =    mu3 - 2*mu2 + mu;
   a2 =    mu3 -   mu2;
   a3 = -2*mu3 + 3*mu2;

   return(a0*y1 + a1*m0 + a2*m1 + a3*y2);
}

/*
	Derivation from CONREC
   d               ! matrix of data to contour
   ilb,iub,jlb,jub ! index bounds of data matrix
   x               ! data matrix column coordinates
   y               ! data matrix row coordinates
   nc              ! number of contour levels
   z               ! contour levels in increasing order
*/
void Contour(double **d,int ilb,int iub,int jlb,int jub,
	double *x,double *y,int nc,double *z,
   void (*ConrecLine)(double,double,double,double,double))
{
#define xsect(p1,p2) (h[p2]*xh[p1]-h[p1]*xh[p2])/(h[p2]-h[p1])
#define ysect(p1,p2) (h[p2]*yh[p1]-h[p1]*yh[p2])/(h[p2]-h[p1])

   int m1,m2,m3,case_value;
   double dmin,dmax,x1=0,x2=0,y1=0,y2=0;
   int i,j,k,m;
   double h[5];
   int sh[5];
   double xh[5],yh[5];
   int im[4] = {0,1,1,0},jm[4]={0,0,1,1};
   int castab[3][3][3] = {
     { {0,0,8},{0,2,5},{7,6,9} },
     { {0,3,4},{1,3,1},{4,3,0} },
     { {9,6,7},{5,2,0},{8,0,0} }
   };
	double temp1,temp2;

   for (j=(jub-1);j>=jlb;j--) {
      for (i=ilb;i<=iub-1;i++) {
         temp1 = MIN(d[i][j],d[i][j+1]);
         temp2 = MIN(d[i+1][j],d[i+1][j+1]);
         dmin  = MIN(temp1,temp2);
         temp1 = MAX(d[i][j],d[i][j+1]);
         temp2 = MAX(d[i+1][j],d[i+1][j+1]);
         dmax  = MAX(temp1,temp2);
         if (dmax < z[0] || dmin > z[nc-1]) 
				continue;
         for (k=0;k<nc;k++) {
            if (z[k] < dmin || z[k] > dmax) 
					continue;
            for (m=4;m>=0;m--) {
               if (m > 0) {
                  h[m]  = d[i+im[m-1]][j+jm[m-1]]-z[k];
                  xh[m] = x[i+im[m-1]];
                  yh[m] = y[j+jm[m-1]];
               } else {
                  h[0]  = 0.25 * (h[1]+h[2]+h[3]+h[4]);
                  xh[0] = 0.50 * (x[i]+x[i+1]);
                  yh[0] = 0.50 * (y[j]+y[j+1]);
               }
               if (h[m] > 0.0) 
                  sh[m] = 1;
               else if (h[m] < 0.0) 
                  sh[m] = -1;
               else
                  sh[m] = 0;
            }

			   /*
               Note: at this stage the relative heights of the corners and the
               centre are in the h array, and the corresponding coordinates are
               in the xh and yh arrays. The centre of the box is indexed by 0
               and the 4 corners by 1 to 4 as shown below.
               Each triangle is then indexed by the parameter m, and the 3
               vertices of each triangle are indexed by parameters m1,m2,and m3.
               It is assumed that the centre of the box is always vertex 2
               though this isimportant only when all 3 vertices lie exactly on
               the same contour level, in which case only the side of the box
               is drawn.
                  vertex 4 +-------------------+ vertex 3
                           | \               / |
                           |   \    m-3    /   |
                           |     \       /     |
                           |       \   /       |
                           |  m=2    X   m=2   |       the centre is vertex 0
                           |       /   \       |
                           |     /       \     |
                           |   /    m=1    \   |
                           | /               \ |
                  vertex 1 +-------------------+ vertex 2
            */
            
		   	/* Scan each triangle in the box */
            for (m=1;m<=4;m++) {
               m1 = m;
               m2 = 0;
               if (m != 4)
                  m3 = m + 1;
               else
                  m3 = 1;
               if ((case_value = castab[sh[m1]+1][sh[m2]+1][sh[m3]+1]) == 0)
						continue;
               switch (case_value) {
               case 1: /* Line between vertices 1 and 2 */
                   x1 = xh[m1];
                   y1 = yh[m1];
                   x2 = xh[m2];
                   y2 = yh[m2];
                   break;
               case 2: /* Line between vertices 2 and 3 */
                   x1 = xh[m2];
                   y1 = yh[m2];
                   x2 = xh[m3];
                   y2 = yh[m3];
                   break;
               case 3: /* Line between vertices 3 and 1 */
                   x1 = xh[m3];
                   y1 = yh[m3];
                   x2 = xh[m1];
                   y2 = yh[m1];
                   break;
               case 4: /* Line between vertex 1 and side 2-3 */
                   x1 = xh[m1];
                   y1 = yh[m1];
                   x2 = xsect(m2,m3);
                   y2 = ysect(m2,m3);
                   break;
               case 5: /* Line between vertex 2 and side 3-1 */
                   x1 = xh[m2];
                   y1 = yh[m2];
                   x2 = xsect(m3,m1);
                   y2 = ysect(m3,m1);
                   break;
               case 6: /* Line between vertex 3 and side 1-2 */
                   x1 = xh[m3];
                   y1 = yh[m3];
                   x2 = xsect(m1,m2);
                   y2 = ysect(m1,m2);
                   break;
               case 7: /* Line between sides 1-2 and 2-3 */
                   x1 = xsect(m1,m2);
                   y1 = ysect(m1,m2);
                   x2 = xsect(m2,m3);
                   y2 = ysect(m2,m3);
                   break;
               case 8: /* Line between sides 2-3 and 3-1 */
                   x1 = xsect(m2,m3);
                   y1 = ysect(m2,m3);
                   x2 = xsect(m3,m1);
                   y2 = ysect(m3,m1);
                   break;
               case 9: /* Line between sides 3-1 and 1-2 */
                   x1 = xsect(m3,m1);
                   y1 = ysect(m3,m1);
                   x2 = xsect(m1,m2);
                   y2 = ysect(m1,m2);
                   break;
               default:
                   break;
               }

	  			   /* Finally draw the line */
			      ConrecLine(x1,y1,x2,y2,z[k]); 
            } /* m */
			} /* k - contour */
		} /* i */
	} /* j */
}

/*
   Calculate the intersection of a ray and a sphere
	The line segment is defined from p1 to p2
	The sphere is of radius r and centered at sc
	There are potentially two points of intersection given by
	p = p1 + mu1 (p2 - p1)
	p = p1 + mu2 (p2 - p1)
	Return FALSE if the ray doesn't intersect the sphere.
*/
int RaySphere(XYZ p1,XYZ p2,XYZ sc,double r,double *mu1,double *mu2)
{
   double a,b,c;
   double bb4ac;
   XYZ dp;

   dp.x = p2.x - p1.x;
   dp.y = p2.y - p1.y;
   dp.z = p2.z - p1.z;
   a = dp.x * dp.x + dp.y * dp.y + dp.z * dp.z;
   b = 2 * (dp.x * (p1.x - sc.x) + dp.y * (p1.y - sc.y) + dp.z * (p1.z - sc.z));
   c = sc.x * sc.x + sc.y * sc.y + sc.z * sc.z;
   c += p1.x * p1.x + p1.y * p1.y + p1.z * p1.z;
   c -= 2 * (sc.x * p1.x + sc.y * p1.y + sc.z * p1.z);
   c -= r * r;
   bb4ac = b * b - 4 * a * c;
   if (ABS(a) < EPS || bb4ac < 0) {
		*mu1 = 0;
		*mu2 = 0;
      return(FALSE);
	}

   *mu1 = (-b + sqrt(bb4ac)) / (2 * a);
   *mu2 = (-b - sqrt(bb4ac)) / (2 * a);

   return(TRUE);
}

/*
   Determine the intersection of a line with a plane
   Return false if the line doesn't intersect
*/
int LinePlane(XYZ p1,XYZ p2,PLANE plane,double *mu,XYZ *p)
{
   double numer,denom;
   XYZ dp;

   dp.x = p1.x - p2.x;
   dp.y = p1.y - p2.y;
   dp.z = p1.z - p2.z;
   denom = plane.a * dp.x + plane.b * dp.y + plane.c * dp.z;
   if (ABS(denom) < EPS)
      return(FALSE);
   numer = plane.a * p1.x + plane.b * p1.y + plane.c * p1.z + plane.d;

   *mu = numer / denom;
   p->x = p1.x + (*mu) * (p2.x - p1.x);
   p->y = p1.y + (*mu) * (p2.y - p1.y);
   p->z = p1.z + (*mu) * (p2.z - p1.z);

   return(TRUE);
}
/*
	Same as the above except for a line segment
	Return true if the line segent intersects the plane, return intersection point
*/
int LineSegmentPlane(XYZ p1,XYZ p2,PLANE plane,XYZ *p)
{
   double numer,denom;
   XYZ dp;
  	double mu;

   dp.x = p1.x - p2.x;
   dp.y = p1.y - p2.y;
   dp.z = p1.z - p2.z;
   denom = plane.a * dp.x + plane.b * dp.y + plane.c * dp.z;
   if (ABS(denom) < EPS)
      return(FALSE);
   numer = plane.a * p1.x + plane.b * p1.y + plane.c * p1.z + plane.d;

   mu = numer / denom;
	if (mu < 0 || mu > 1)
		return(FALSE);
   p->x = p1.x + mu * (p2.x - p1.x);
   p->y = p1.y + mu * (p2.y - p1.y); 
   p->z = p1.z + mu * (p2.z - p1.z); 

   return(TRUE);
}

/*
   Linear combination of 3 vectors.
   v = k1 * v1 + k2 * v2 + k3 * v3
*/
XYZ VectorCombination(double k1,XYZ v1,double k2,XYZ v2,double k3,XYZ v3)
{
   XYZ v;

   v.x = k1 * v1.x + k2 * v2.x + k3 * v3.x;
   v.y = k1 * v1.y + k2 * v2.y + k3 * v3.y;
   v.z = k1 * v1.z + k2 * v2.z + k3 * v3.z;

   return(v);
}

/* FILE IO ---------------------------------------------------------*/

/*
   Encode an RLE buffer
   Return the length in the output buffer
*/
int RLECompress(unsigned char *output,unsigned char *input,int length)
{
   int count = 0,index,i;
   unsigned char pixel;
   int out = 0;

   while (count < length) {
      index = count;
      pixel = input[index++];
      while (index < length && index - count < 127 && input[index] == pixel)
         index++;
      if (index - count == 1) {
         /*
            Failed to "replicate" the current pixel. See how many to copy.
            Avoid a replicate run of only 2-pixels after a literal run. There
            is no gain in this, and there is a risK of loss if the run after
            the two identical pixels is another literal run. So search for
            3 identical pixels.
         */
         while (index < length && 
					 index - count < 127 &&
                (input[index] != input[index-1] || 
					  (index > 1 && 
					  input[index] != input[index-2])))
            index++;
         /*
            Check why this run stopped. If it found two identical pixels, reset
            the index so we can add a run. Do this twice: the previous run
            tried to detect a replicate run of at least 3 pixels. So we may be
            able to back up two pixels if such a replicate run was found.
         */
         while (index < length && input[index] == input[index-1])
            index--;
         output[out++] = (unsigned char)(count - index);
         for (i=count;i<index;i++)
            output[out++] = input[i];
      } else {
         output[out++] = (unsigned char)(index - count);
         output[out++] = pixel;
      } /* if */
      count=index;
   } /* while */
   return(out);
}

/*
   Uncompress a RLE buffer
*/
void RLEUncompress(unsigned char *output,unsigned char *input,int length)
{
   signed char count;

   while (length > 0) {
      count = (signed char)*input++;
      if (count > 0) {                      /* replicate run */
         memset(output,*input++,count);
      } else if (count < 0) {               /* literal run */
         count = (signed char)-count;
         memcpy(output,input,count);
         input += count;
      } /* if */
      output += count;
      length -= count;
   } /* while */
}

/*
	Read a line
*/
int ReadLine(FILE *fptr,char *s,int lmax)
{
	int i=0,c;

	s[0] = '\0';
	while ((c = fgetc(fptr)) != '\n' && c != '\r') {
		if (c == EOF)
			return(FALSE);
		s[i] = c;
		i++;
		s[i] = '\0';
		if (i >= lmax)
			break;
	}
	return(TRUE);
}

/*
  Read until a particular character is encountered
*/
int ReadUntil(FILE *fptr,int find,char *s)
{
  int c,i = 0;

  i = 0;
  s[i] = 0;
  while ((c = fgetc(fptr)) != find && c != EOF) {
    s[i] = c;
    i++;
    s[i] = '\0';
  }

  if (c == EOF)
    return(FALSE);
  return(TRUE);
}

/*
	Trim any spaces off the end of a string
*/
void RightTrim(char *s)
{
   int i;

   i = strlen(s) - 1;
   while (i >= 0 && s[i] == ' ')
      s[i--] = '\0';
}

/*
  Read until a particular string is encountered
*/
void ReadToString(FILE *fptr,char *s)
{
  char ss[100];

  while (fscanf(fptr,"%s",ss) == 1) {
    if (strcmp(ss,s) == 0)
      return;
  }
}

/*
   Read a string of n characters
*/
int ReadString(FILE *fptr,char *s,int n)
{
   int i,c;

   s[0] = '\0';
   for (i=0;i<n;i++) {
      c = fgetc(fptr);
      if (c == EOF)
         return(FALSE);
      s[i] = c;
      s[i+1] = '\0';
   }
   return(TRUE);
}

/*
   Read a possibly byte swapped short integer
*/
int ReadShort(FILE *fptr,short int *n,int swap)
{
   unsigned char *cptr,tmp;

   if (fread(n,2,1,fptr) != 1)
      return(FALSE);
	if (swap) {
   	cptr = (unsigned char *)n;
   	tmp = cptr[0];
   	cptr[0] = cptr[1];
   	cptr[1] = tmp;
	}
   return(TRUE);
}

/*
   Write a possibly byte swapped short integer
*/
int WriteShort(FILE *fptr,short n,int swap)
{
   unsigned char *cptr,tmp;

	if (!swap) {
   	if (fwrite(&n,2,1,fptr) != 1)
      	return(FALSE);
   } else {
      cptr = (unsigned char *)(&n);
      tmp = cptr[0];
      cptr[0] = cptr[1];
      cptr[1] = tmp;
      if (fwrite(&n,2,1,fptr) != 1)
         return(FALSE);
   }
   return(TRUE);
}

/*
   Read a possibly byte swapped unsigned short integer
*/
int ReadUShort(FILE *fptr,short unsigned *n,int swap)
{
   unsigned char *cptr,tmp;

   if (fread(n,2,1,fptr) != 1)
      return(FALSE);
	if (swap) {
   	cptr = (unsigned char *)n;
   	tmp = cptr[0];
   	cptr[0] = cptr[1];
   	cptr[1] =tmp;
	}
   return(TRUE);
}

/*
   Write a possibly byte swapped unsigned short integer
*/
int WriteUShort(FILE *fptr,short unsigned n,int swap)
{
   unsigned char *cptr,tmp;

	if (!swap) {
   	if (fwrite(&n,2,1,fptr) != 1)
      	return(FALSE);
   } else {
      cptr = (unsigned char *)(&n);
      tmp = cptr[0];
      cptr[0] = cptr[1];
      cptr[1] =tmp;
      if (fwrite(&n,2,1,fptr) != 1)
         return(FALSE);
   }
   return(TRUE);
}

/*
   Read a possibly byte swapped integer
*/
int ReadInt(FILE *fptr,int *n,int swap)
{
   unsigned char *cptr,tmp;

   if (fread(n,4,1,fptr) != 1)
      return(FALSE);
   if (swap) {
      cptr = (unsigned char *)n;
      tmp = cptr[0];
      cptr[0] = cptr[3];
      cptr[3] = tmp;
      tmp = cptr[1];
      cptr[1] = cptr[2];
      cptr[2] = tmp;
   }
   return(TRUE);
}

/*
   Read a possibly byte swapped unsigned integer
*/
int ReadUInt(FILE *fptr,unsigned int *n,int swap)
{
   unsigned char *cptr,tmp;

   if (fread(n,4,1,fptr) != 1)
      return(FALSE);
   if (swap) {
      cptr = (unsigned char *)n;
      tmp = cptr[0];
      cptr[0] = cptr[3];
      cptr[3] = tmp;
      tmp = cptr[1];
      cptr[1] = cptr[2];
      cptr[2] = tmp;
   }
   return(TRUE);
}

/*
   Write a possibly byte swapped integer
*/
int WriteInt(FILE *fptr,int n,int swap)
{
   unsigned char *cptr,tmp;

	if (!swap) {
   	if (fwrite(&n,4,1,fptr) != 1)
      	return(FALSE);
   } else {
      cptr = (unsigned char *)(&n);
      tmp = cptr[0];
      cptr[0] = cptr[3];
      cptr[3] = tmp;
      tmp = cptr[1];
      cptr[1] = cptr[2];
      cptr[2] = tmp;
      if (fwrite(&n,4,1,fptr) != 1)
         return(FALSE);
   }
   return(TRUE);
}

/*
   Write a possibly byte swapped unsigned integer
*/
int WriteUInt(FILE *fptr,unsigned int n,int swap)
{
   unsigned char *cptr,tmp;

   if (!swap) {
      if (fwrite(&n,4,1,fptr) != 1)
         return(FALSE);
   } else {
      cptr = (unsigned char *)(&n);
      tmp = cptr[0];
      cptr[0] = cptr[3];
      cptr[3] = tmp;
      tmp = cptr[1];
      cptr[1] = cptr[2];
      cptr[2] = tmp;
      if (fwrite(&n,4,1,fptr) != 1)
         return(FALSE);
   }
   return(TRUE);
}

/*
   Read a possibly byte swapped long integer
*/
int ReadLong(FILE *fptr,long *n,int swap)
{
   unsigned char *cptr,tmp;

   if (fread(n,4,1,fptr) != 1)
      return(FALSE);
	if (swap) {
   	cptr = (unsigned char *)n;
   	tmp = cptr[0];
   	cptr[0] = cptr[3];
   	cptr[3] = tmp;
   	tmp = cptr[1];
   	cptr[1] = cptr[2];
   	cptr[2] = tmp;
	}
   return(TRUE);
}

/*
   Write a possibly byte swapped long integer
	arning: machine dependent, this assumes longs are 4 bytes, might be 8!
*/
int WriteLong(FILE *fptr,long n,int swap)
{
   unsigned char *cptr,tmp;

	if (!swap) {
   	if (fwrite(&n,4,1,fptr) != 1)
      	return(FALSE);
   } else {
      cptr = (unsigned char *)(&n);
      tmp = cptr[0];
      cptr[0] = cptr[3];
      cptr[3] = tmp;
      tmp = cptr[1];
      cptr[1] = cptr[2];
      cptr[2] = tmp;
      if (fwrite(&n,4,1,fptr) != 1)
         return(FALSE);
   }
   return(TRUE);
}

/*
   Write a possibly byte swapped unsigned long integer
   Warning: machine dependent, this assumes longs are 4 bytes, might be 8!
*/
int WriteULong(FILE *fptr,unsigned long n,int swap)
{
   unsigned char *cptr,tmp;

   if (!swap) {
      if (fwrite(&n,4,1,fptr) != 1)
         return(FALSE);
   } else {
      cptr = (unsigned char *)(&n);
      tmp = cptr[0];
      cptr[0] = cptr[3];
      cptr[3] = tmp;
      tmp = cptr[1];
      cptr[1] = cptr[2];
      cptr[2] = tmp;
      if (fwrite(&n,4,1,fptr) != 1)
         return(FALSE);
   }
   return(TRUE);
}

/*
   Write a possibly byte swapped double precision number
   Assume IEEE
*/
int WriteDouble(FILE *fptr,double n,int swap)
{
   unsigned char *cptr,tmp;

	if (!swap) {
   	if (fwrite(&n,8,1,fptr) != 1)
      	return(FALSE);
	} else {
   	cptr = (unsigned char *)(&n);
   	tmp = cptr[0];
   	cptr[0] = cptr[7];
   	cptr[7] =tmp;
   	tmp = cptr[1];
   	cptr[1] = cptr[6];
   	cptr[6] = tmp;
   	tmp = cptr[2];
   	cptr[2] = cptr[5];
   	cptr[5] =tmp;
   	tmp = cptr[3];
   	cptr[3] = cptr[4];
   	cptr[4] = tmp;
      if (fwrite(&n,8,1,fptr) != 1)
         return(FALSE);
	}
   return(TRUE);
}

/*
   Read a possibly byte swapped double precision number
   Assume IEEE
*/
int ReadDouble(FILE *fptr,double *n,int swap)
{
   unsigned char *cptr,tmp;

   if (fread(n,8,1,fptr) != 1)
      return(FALSE);
   if (swap) {
      cptr = (unsigned char *)n;
      tmp = cptr[0];
      cptr[0] = cptr[7];
      cptr[7] =tmp;
      tmp = cptr[1];
      cptr[1] = cptr[6];
      cptr[6] = tmp;
      tmp = cptr[2];
      cptr[2] = cptr[5];
      cptr[5] =tmp;
      tmp = cptr[3];
      cptr[3] = cptr[4];
      cptr[4] = tmp;
   }
   return(TRUE);
}

/*
   Read a possibly byte swapped floating point number
   Assume IEEE format
*/
int ReadFloat(FILE *fptr,float *n,int swap)
{
   unsigned char *cptr,tmp;

   if (fread(n,4,1,fptr) != 1)
      return(FALSE);
	if (swap) {
  	 	cptr = (unsigned char *)n;
   	tmp = cptr[0];
   	cptr[0] = cptr[3];
   	cptr[3] =tmp;
   	tmp = cptr[1];
   	cptr[1] = cptr[2];
   	cptr[2] = tmp;
	}
   return(TRUE);
}

/*
   Write a possibly byte swapped float
   Warning: machine dependent, this assumes floats are 4 bytes, might be 8!
*/
int WriteFloat(FILE *fptr,float n,int swap)
{
   unsigned char *cptr,tmp;

   if (!swap) {
      if (fwrite(&n,4,1,fptr) != 1)
         return(FALSE);
   } else {
      cptr = (unsigned char *)(&n);
      tmp = cptr[0];
      cptr[0] = cptr[3];
      cptr[3] = tmp;
      tmp = cptr[1];
      cptr[1] = cptr[2];
      cptr[2] = tmp;
      if (fwrite(&n,4,1,fptr) != 1)
         return(FALSE);
   }
   return(TRUE);
}

/*
   Read past n bytes
*/
int ReadSkip(FILE *fptr,int n)
{
   int i,c;

   for (i=0;i<n;i++) {
      c = fgetc(fptr);
      if (c == EOF)
         return(FALSE);
   }
   return(TRUE);
}

/*
   Read and ignore from a file until some character is reached
*/
void SkipUntil(FILE *fptr,int c)
{
  int cc;

  while ((cc = fgetc(fptr)) != c && cc != EOF)
    ;
}

/*
	Convert a string to lower case
*/
void StrToLower(char *s)
{
  unsigned int i;

  for (i=0;i<strlen(s);i++) {
    if (s[i] >= 'A' && s[i] <= 'Z')
      s[i] += 'a' - 'A';
  }
}

/*
   Convert a string to upper case
*/
void StrToUpper(char *s)
{
  unsigned int i;

  for (i=0;i<strlen(s);i++) {
    if (s[i] >= 'a' && s[i] <= 'z')
      s[i] += 'A' - 'a';
  }
}

/*
	Echo a file, if it exists, to another file
*/
void EchoFile(char *fname,FILE *fp)
{
  int c;
  FILE *fptr;

  if ((fptr = fopen(fname,"rb")) != NULL) {
     while ((c = fgetc(fptr)) != EOF)
        fputc(c,fp);
     fclose(fptr);
   }
}

/*
	Copy parts of a string into another
*/
int IndexCopy(char *sin,char *sout,unsigned int start,unsigned int stop)
{
   unsigned int i,j=0;

   sout[0] = '\0';
   if (strlen(sin) < stop)
      return(FALSE);
   for (i=start;i<=stop;i++) {
      sout[j] = sin[i];
      j++;
      sout[j] = '\0';
   }
   return(TRUE);
}

/* MISC FUNCTIONS ---------------------------------------------------*/

/*
   Clip a double to bounds returning true if necessary
*/
int ClipDouble(double *v,double upper,double lower)
{
	if (*v < lower) {
		*v = lower;
		return(TRUE);		
	}
	if (*v > upper) {
		*v = upper;
		return(TRUE);
	}
	return(FALSE);
}

/*
   Clip a int to bounds returning true if clipping was performed
*/
int ClipInteger(int *v,int lower,int upper)
{
   if (*v < lower) {
      *v = lower;   
      return(TRUE); 
   }
   if (*v > upper) {
      *v = upper;
      return(TRUE);
   }
   return(FALSE);
}

/*
   Swap two doubles
*/
void SwapDouble(double *d1,double *d2)
{
   double tmp;

   tmp = *d1;
   *d1 = *d2;
   *d2 = tmp;
}

/*
	Swap two XYZ points
*/
void SwapXYZ(XYZ *p1,XYZ *p2)
{
	XYZ tmp;

	tmp = *p1;
	*p1 = *p2;
	*p2 = tmp;
}

/*
	Calculate the bounding box of a vector
*/
void MinMaxXYZ(XYZ p,XYZ *min,XYZ *max)
{
   min->x = MIN(min->x,p.x);
   min->y = MIN(min->y,p.y);
   min->z = MIN(min->z,p.z);
   max->x = MAX(max->x,p.x);
   max->y = MAX(max->y,p.y);
   max->z = MAX(max->z,p.z);
}

/*
	Clean a string
*/
void StringClean(char *s)
{
	unsigned int i;
	int c;

	/* Strip non printing charagters from the end */
	while (strlen(s) > 0 && ((c = s[strlen(s)-1]) <= ' ' || c > '~'))
		s[strlen(s)-1] = '\0';

	/* Strip non printing characters from start */
	while (strlen(s) > 0 && (s[0] <= ' ' || s[0] > '~')) {
		for (i=1;i<=strlen(s);i++) 
			s[i-1] = s[i];
	}
}

/* COLOUR STUFF -----------------------------------------------------*/

/*
	Return the colour between c1 and c2 linearly given mu (0..1)
*/
COLOUR RampColour2(double mu,COLOUR c1,COLOUR c2)
{
	COLOUR c;

	if (mu < 0)
		return(c1);
	if (mu > 1)
		return(c2);

	c.r = c1.r + mu * (c2.r - c1.r);
   c.g = c1.g + mu * (c2.g - c1.g);
   c.b = c1.b + mu * (c2.b - c1.b);

	return(c);
}

/*
   Return a colour from one of a number of colour ramps
   type == 1  blue -> cyan -> green -> magenta -> red 
           2  blue -> red 
           3  grey scale 
           4  red -> yellow -> green -> cyan -> blue -> magenta -> red 
           5  green -> yellow 
			  6  green -> magenta
			  7  blue -> green -> red -> green -> blue
			  8  black -> white -> black
			  9  red -> blue -> cyan -> magenta
          10  blue -> cyan -> green -> yellow -> red -> white
			 11  dark brown -> lighter brown (Mars colours, 2 colour transition)
			 12  3 colour transition mars colours
			 13  landscape colours, green -> brown -> yellow
          14  yellow -> red
			 15  blue -> cyan -> green -> yellow -> brown -> white
          16  blue -> green -> red       (Chromadepth for black background)
          17  yellow -> magenta -> cyan  (Chromadepth for white background)
          18  blue -> cyan
          19  blue -> white
          20  landscape colours, modified, green -> brown -> yellow
          21  yellowish to blueish
          22  yellow to blue
			 23  red to white to blue
          24  blue -> yellow -> red
			 25  blue -> cyan -> yellow -> red
			 26  green -> yellow -> green
          27  black -> red -> white
          28  black -> green -> white
          29  black -> blue -> white
			 30  Same as 4 but with less green, more cyan and yellow
			 31  Same as 4 but different weighting between primary and secondary colours
   v should lie between vmin and vmax otherwise it is clipped
   The colour components range from 0 to 1
*/
COLOUR GetColour(double v,double vmin,double vmax,int type)
{
	int iv;
   double dv,vmid,a=0.8;
	COLOUR c = {1.0,1.0,1.0};
	COLOUR c1,c2,c3;
	double ratio,mu;
	HSV hsv1;

	if (vmax < vmin) {
		dv = vmin;
		vmin = vmax;
		vmax = dv;
	}
	if (vmax - vmin < 0.000001) {
		vmin -= 1;
		vmax += 1;
	}
   if (v < vmin)
      v = vmin;
   if (v > vmax)
      v = vmax;
   dv = vmax - vmin;

	switch (type) {
	case 1:
   	if (v < (vmin + 0.25 * dv)) {
      	c.r = 0;
      	c.g = 4 * (v - vmin) / dv;
			c.b = 1;
   	} else if (v < (vmin + 0.5 * dv)) {
      	c.r = 0;
			c.g = 1;
      	c.b = 1 + 4 * (vmin + 0.25 * dv - v) / dv;
   	} else if (v < (vmin + 0.75 * dv)) {
      	c.r = 4 * (v - vmin - 0.5 * dv) / dv;
			c.g = 1;
      	c.b = 0;
   	} else {
			c.r = 1;
      	c.g = 1 + 4 * (vmin + 0.75 * dv - v) / dv;
      	c.b = 0;
   	}
		break;
	case 2:
		c.r = (v - vmin) / dv;
		c.g = 0;
		c.b = (vmax - v) / dv;
		break;
	case 3:
   	c.r = (v - vmin) / dv;
   	c.b = c.r;
   	c.g = c.r;
		break;
	case 4:
      if (v < (vmin + dv / 6.0)) {
         c.r = 1; 
         c.g = 6 * (v - vmin) / dv;
         c.b = 0;
      } else if (v < (vmin + 2.0 * dv / 6.0)) {
         c.r = 1 + 6 * (vmin + dv / 6.0 - v) / dv;
         c.g = 1;
         c.b = 0;
      } else if (v < (vmin + 3.0 * dv / 6.0)) {
         c.r = 0;
         c.g = 1;
         c.b = 6 * (v - vmin - 2.0 * dv / 6.0) / dv;
      } else if (v < (vmin + 4.0 * dv / 6.0)) {
         c.r = 0;
         c.g = 1 + 6 * (vmin + 3.0 * dv / 6.0 - v) / dv;
         c.b = 1;
      } else if (v < (vmin + 5.0 * dv / 6.0)) {
         c.r = 6 * (v - vmin - 4.0 * dv / 6.0) / dv;
         c.g = 0;
         c.b = 1;
      } else {
         c.r = 1;
         c.g = 0;
         c.b = 1 + 6 * (vmin + 5.0 * dv / 6.0 - v) / dv;
      }
		break;
   case 5:
      c.r = (v - vmin) / dv;
      c.g = 1;
      c.b = 0;
		break;
   case 6:
      c.r = (v - vmin) / dv;
      c.g = (vmax - v) / dv;
      c.b = c.r;
		break;
   case 7:
      if (v < (vmin + 0.25 * dv)) {
         c.r = 0;
         c.g = 4 * (v - vmin) / dv;
         c.b = 1 - c.g;
      } else if (v < (vmin + 0.5 * dv)) {
			c.r = 4 * (v - vmin - 0.25 * dv) / dv;
         c.g = 1 - c.r;
         c.b = 0;
      } else if (v < (vmin + 0.75 * dv)) {
         c.g = 4 * (v - vmin - 0.5 * dv) / dv;
			c.r = 1 - c.g;
         c.b = 0;
      } else {
         c.r = 0;
         c.b = 4 * (v - vmin - 0.75 * dv) / dv;
			c.g = 1 - c.b;
      }
      break;
   case 8:
      if (v < (vmin + 0.5 * dv)) {
         c.r = 2 * (v - vmin) / dv;
         c.g = c.r;
         c.b = c.r;
      } else {
         c.r = 1 - 2 * (v - vmin - 0.5 * dv) / dv;
			c.g = c.r;
         c.b = c.r;
      }
      break;
   case 9:
      if (v < (vmin + dv / 3)) {
         c.b = 3 * (v - vmin) / dv;
			c.g = 0;
         c.r = 1 - c.b;
      } else if (v < (vmin + 2 * dv / 3)) {
         c.r = 0;
         c.g = 3 * (v - vmin - dv / 3) / dv;
         c.b = 1;
      } else {
         c.r = 3 * (v - vmin - 2 * dv / 3) / dv;
         c.g = 1 - c.r;
			c.b = 1;
      }
      break;
   case 10:
      if (v < (vmin + 0.2 * dv)) {
         c.r = 0;
         c.g = 5 * (v - vmin) / dv;
         c.b = 1;
      } else if (v < (vmin + 0.4 * dv)) {
         c.r = 0;
         c.g = 1;
         c.b = 1 + 5 * (vmin + 0.2 * dv - v) / dv;
      } else if (v < (vmin + 0.6 * dv)) {
         c.r = 5 * (v - vmin - 0.4 * dv) / dv;
         c.g = 1;
         c.b = 0;
      } else if (v < (vmin + 0.8 * dv)) {
         c.r = 1;
         c.g = 1 - 5 * (v - vmin - 0.6 * dv) / dv;
         c.b = 0;
      } else {
         c.r = 1;
         c.g = 5 * (v - vmin - 0.8 * dv) / dv;
         c.b = 5 * (v - vmin - 0.8 * dv) / dv;
      }
      break;
   case 11:
		c1.r = 200 / 255.0; c1.g =  60 / 255.0; c1.b =   0 / 255.0;
		c2.r = 250 / 255.0; c2.g = 160 / 255.0; c2.b = 110 / 255.0;
      c.r = (c2.r - c1.r) * (v - vmin) / dv + c1.r;
      c.g = (c2.g - c1.g) * (v - vmin) / dv + c1.g;
      c.b = (c2.b - c1.b) * (v - vmin) / dv + c1.b;
      break;
	case 12:
		c1.r =  55 / 255.0; c1.g =  55 / 255.0; c1.b =  45 / 255.0;
      /* c2.r = 200 / 255.0; c2.g =  60 / 255.0; c2.b =   0 / 255.0; */
		c2.r = 235 / 255.0; c2.g =  90 / 255.0; c2.b =  30 / 255.0;
		c3.r = 250 / 255.0; c3.g = 160 / 255.0; c3.b = 110 / 255.0;
		ratio = 0.4;
		vmid = vmin + ratio * dv;
		if (v < vmid) {
      	c.r = (c2.r - c1.r) * (v - vmin) / (ratio*dv) + c1.r;
      	c.g = (c2.g - c1.g) * (v - vmin) / (ratio*dv) + c1.g;
      	c.b = (c2.b - c1.b) * (v - vmin) / (ratio*dv) + c1.b;
		} else {
         c.r = (c3.r - c2.r) * (v - vmid) / ((1-ratio)*dv) + c2.r;
         c.g = (c3.g - c2.g) * (v - vmid) / ((1-ratio)*dv) + c2.g;
         c.b = (c3.b - c2.b) * (v - vmid) / ((1-ratio)*dv) + c2.b;
		}
		break;
	case 13:
      c1.r =   0 / 255.0; c1.g = 255 / 255.0; c1.b =   0 / 255.0;
      c2.r = 255 / 255.0; c2.g = 150 / 255.0; c2.b =   0 / 255.0;
      c3.r = 255 / 255.0; c3.g = 250 / 255.0; c3.b = 240 / 255.0;
      ratio = 0.3;
      vmid = vmin + ratio * dv;
      if (v < vmid) {
         c.r = (c2.r - c1.r) * (v - vmin) / (ratio*dv) + c1.r;
         c.g = (c2.g - c1.g) * (v - vmin) / (ratio*dv) + c1.g;
         c.b = (c2.b - c1.b) * (v - vmin) / (ratio*dv) + c1.b;
      } else {
         c.r = (c3.r - c2.r) * (v - vmid) / ((1-ratio)*dv) + c2.r;
         c.g = (c3.g - c2.g) * (v - vmid) / ((1-ratio)*dv) + c2.g;
         c.b = (c3.b - c2.b) * (v - vmid) / ((1-ratio)*dv) + c2.b;
      }
		break;
   case 14:
      c.r = 1;
      c.g = 1 - (v - vmin) / dv;
      c.b = 0;
      break;
   case 15:
      if (v < (vmin + 0.25 * dv)) {
         c.r = 0;
         c.g = 4 * (v - vmin) / dv;
         c.b = 1;
      } else if (v < (vmin + 0.5 * dv)) {
         c.r = 0;
         c.g = 1;
         c.b = 1 - 4 * (v - vmin - 0.25 * dv) / dv;
      } else if (v < (vmin + 0.75 * dv)) {
			c.r = 4 * (v - vmin - 0.5 * dv) / dv;
         c.g = 1;
         c.b = 0;
      } else {
         c.r = 1;
			c.g = 1;
         c.b = 4 * (v - vmin - 0.75 * dv) / dv;
      }
      break;
   case 16:
      if (v < (vmin + 0.5 * dv)) {
         c.r = 0.0;
         c.g = 2 * (v - vmin) / dv;
         c.b = 1 - 2 * (v - vmin) / dv;
      } else {
         c.r = 2 * (v - vmin - 0.5 * dv) / dv;
         c.g = 1 - 2 * (v - vmin - 0.5 * dv) / dv;
         c.b = 0.0;
      }
      break;
   case 17:
      if (v < (vmin + 0.5 * dv)) {
         c.r = 1.0;
         c.g = 1 - 2 * (v - vmin) / dv;
         c.b = 2 * (v - vmin) / dv;
      } else {
         c.r = 1 - 2 * (v - vmin - 0.5 * dv) / dv;
         c.g = 2 * (v - vmin - 0.5 * dv) / dv;
         c.b = 1.0;
      }
      break;
   case 18:
      c.r = 0;
      c.g = (v - vmin) / dv;
      c.b = 1;
      break;
   case 19:
      c.r = (v - vmin) / dv;
      c.g = c.r;
      c.b = 1;
      break;
   case 20:
      c1.r =   0 / 255.0; c1.g = 160 / 255.0; c1.b =   0 / 255.0;
      c2.r = 180 / 255.0; c2.g = 220 / 255.0; c2.b =   0 / 255.0;
      c3.r = 250 / 255.0; c3.g = 220 / 255.0; c3.b = 170 / 255.0;
      ratio = 0.3;
      vmid = vmin + ratio * dv;
      if (v < vmid) {
         c.r = (c2.r - c1.r) * (v - vmin) / (ratio*dv) + c1.r;
         c.g = (c2.g - c1.g) * (v - vmin) / (ratio*dv) + c1.g;
         c.b = (c2.b - c1.b) * (v - vmin) / (ratio*dv) + c1.b;
      } else {
         c.r = (c3.r - c2.r) * (v - vmid) / ((1-ratio)*dv) + c2.r;
         c.g = (c3.g - c2.g) * (v - vmid) / ((1-ratio)*dv) + c2.g;
         c.b = (c3.b - c2.b) * (v - vmid) / ((1-ratio)*dv) + c2.b;
      }
      break;
   case 21:
      c1.r = 255 / 255.0; c1.g = 255 / 255.0; c1.b = 200 / 255.0;
      c2.r = 150 / 255.0; c2.g = 150 / 255.0; c2.b = 255 / 255.0;
      c.r = (c2.r - c1.r) * (v - vmin) / dv + c1.r;
      c.g = (c2.g - c1.g) * (v - vmin) / dv + c1.g;
      c.b = (c2.b - c1.b) * (v - vmin) / dv + c1.b;
      break;
   case 22:
      c.r = 1 - (v - vmin) / dv;
      c.g = 1 - (v - vmin) / dv;
      c.b = (v - vmin) / dv;
      break;
   case 23:
      if (v < (vmin + 0.5 * dv)) {
         c.r = 1;
         c.g = 2 * (v - vmin) / dv;
         c.b = c.g;
      } else {
         c.r = 1 - 2 * (v - vmin - 0.5 * dv) / dv;
         c.g = c.r;
         c.b = 1;
      }
      break;
   case 24:
      if (v < (vmin + 0.5 * dv)) {
         c.r = 2 * (v - vmin) / dv;
         c.g = c.r;
         c.b = 1 - c.r;
      } else {
         c.r = 1;
         c.g = 1 - 2 * (v - vmin - 0.5 * dv) / dv;
         c.b = 0;
      }
      break;
   case 25:
      if (v < (vmin + dv / 3)) {
         c.r = 0;
         c.g = 3 * (v - vmin) / dv;
         c.b = 1;
      } else if (v < (vmin + 2 * dv / 3)) {
         c.r = 3 * (v - vmin - dv / 3) / dv;
         c.g = 1 - c.r;
         c.b = 1;
      } else {
         c.r = 1;
         c.g = 0;
         c.b = 1 - 3 * (v - vmin - 2 * dv / 3) / dv;
      }
      break;
   case 26:
      if (v < (vmin + 0.5 * dv)) {
         c.r = 2 * (v - vmin) / dv;
         c.g = 1;
         c.b = 0;
      } else {
         c.r = 1 - 2 * (v - vmin - 0.5 * dv) / dv;
         c.g = 1;
         c.b = 0;
      }
      break;
   case 27:
      if (v < (vmin + 0.5 * dv)) {
         c.r = 2 * (v - vmin) / dv;
         c.g = 0;
         c.b = 0;
      } else {
         c.r = 1;
         c.g = 2 * (v - vmin - 0.5 * dv) / dv;
         c.b = c.g;
      }
      break;
   case 28:
      if (v < (vmin + 0.5 * dv)) {
         c.r = 0;
         c.g = 2 * (v - vmin) / dv;
         c.b = 0;
      } else { 
         c.r = 2 * (v - vmin - 0.5 * dv) / dv;
         c.g = 1;
         c.b = c.r;
      }  
      break;
   case 29:
      if (v < (vmin + 0.5 * dv)) {
         c.r = 0;
         c.g = 0;
         c.b = 2 * (v - vmin) / dv;
      } else { 
         c.r = 2 * (v - vmin - 0.5 * dv) / dv;
         c.g = c.r;
         c.b = 1;
      }  
      break;
   case 30:
		hsv1.h = 360 * (v - vmin) / dv;
		hsv1.s = 1;
		hsv1.v = 1;
		c = HSV2RGB(hsv1);
      break;
	case 31:
		v -= vmin;
		v /= dv; // 0 ... 1
		iv = 12 * v;
		mu = 12 * v - iv;
		switch (iv) {
		case 0:
			c.r = 1;
			c.g = mu * a;
			c.b = 0;
			break;
      case 1:
         c.r = 1;
         c.g = a + mu * (1 - a);
         c.b = 0;
         break;
      case 2:
         c.r = 1 + mu * (a - 1);
         c.g = 1;
         c.b = 0;
         break;
      case 3:
         c.r = a + mu * (0 - a);
         c.g = 1;
         c.b = 0;
         break;
      case 4:
         c.r = 0;
         c.g = 1;
         c.b = mu * a;
         break;
      case 5:
         c.r = 0;
         c.g = 1;
         c.b = a + mu * (1 - a);
         break;
      case 6:
         c.r = 0;
         c.g = 1 + mu * (a - 1);
         c.b = 1;
         break;
      case 7:
         c.r = 0;
         c.g = a + mu * (0 - a);
         c.b = 1;
         break;
      case 8:
         c.r = mu * a;
         c.g = 0;
         c.b = 1;
         break;
      case 9:
         c.r = a + mu * (1 - a);
         c.g = 0;
         c.b = 1;
         break;
      case 10:
         c.r = 1;
         c.g = 0;
         c.b = 1 + mu * (a - 1);
         break;
      case 11:
         c.r = 1;
         c.g = 0;
         c.b = a + mu * (0 - a);
         break;
      case 12:
         c.r = 1;
         c.g = 0;
         c.b = 0;
         break;
		}
		break;
	}
	return(c);
}

/*
	Return an ascii character whose density relates to its value
   between two bounds
*/
int AsciiColour(double v,double vmin,double vmax)
{
	int steps = 15;
	int ramp[15] = 
		{' ','.',',',':',';','-','|','I','*','o','O','%','#','@','M'};

   if (v < vmin)
      v = vmin;
   if (v > vmax)
      v = vmax;

	return(ramp[(int)(steps * (v - vmin) / (vmax - vmin))]);
}

/*
	Return the greyscale value of a colour, between 0 (black) and 1 (white)
*/
double AbsColour(COLOUR c)
{
	return(sqrt(c.r*c.r + c.b*c.b + c.g*c.g) / SQRT3);
}

/*
   Return true if two colours are the same
*/
int EqualColour(COLOUR c1,COLOUR c2)
{
	if (ABS(c1.r-c2.r) > EPSILON)
		return(FALSE);
   if (ABS(c1.g-c2.g) > EPSILON)
      return(FALSE);
   if (ABS(c1.b-c2.b) > EPSILON)
      return(FALSE);
   return(TRUE);
}

/*
   Invert a colour
*/
void InvertColour(COLOUR *c)
{
   c->r = 1 - c->r;
	c->g = 1 - c->g;
	c->b = 1 - c->b;
}

/*
   Clip a colour to legal values
*/
void ClipColour(COLOUR *c)
{
   if (c->r < 0) c->r = 0;
	if (c->r > 1) c->r = 1;
   if (c->g < 0) c->g = 0;
   if (c->g > 1) c->g = 1;
   if (c->b < 0) c->b = 0;
   if (c->b > 1) c->b = 1;
}
void ClipRGBA(RGBA *c)
{
   if (c->r < 0) c->r = 0;
   if (c->r > 1) c->r = 1;
   if (c->g < 0) c->g = 0;
   if (c->g > 1) c->g = 1;
   if (c->b < 0) c->b = 0;
   if (c->b > 1) c->b = 1;
   if (c->a < 0) c->a = 0;
   if (c->a > 1) c->a = 1;
}

/*
   Calculate HSL from RGB
   Hue is in degrees
   Lightness is betweeen 0 and 1
   Saturation is between 0 and 1
*/
HSL RGB2HSL(COLOUR c1)
{
   double themin,themax,delta;
   HSL c2;

   themin = MIN(c1.r,MIN(c1.g,c1.b));
   themax = MAX(c1.r,MAX(c1.g,c1.b));
   delta = themax - themin;
   c2.l = (themin + themax) / 2;
   c2.s = 0;
   if (c2.l > 0 && c2.l < 1)
      c2.s = delta / (c2.l < 0.5 ? (2*c2.l) : (2-2*c2.l));
   c2.h = 0;
   if (delta > 0) {
      if (themax == c1.r && themax != c1.g)
         c2.h += (c1.g - c1.b) / delta;
      if (themax == c1.g && themax != c1.b)
         c2.h += (2 + (c1.b - c1.r) / delta);
      if (themax == c1.b && themax != c1.r)
         c2.h += (4 + (c1.r - c1.g) / delta);
      c2.h *= 60;
		if (c2.h < 0)
			c2.h += 360;
   }
   return(c2);
}

/*
   Calculate RGB from HSL, reverse of RGB2HSL()
   Hue is in degrees
   Lightness is between 0 and 1
   Saturation is between 0 and 1
*/
COLOUR HSL2RGB(HSL c1)
{
   COLOUR c2,sat,ctmp;

   while (c1.h < 0)
      c1.h += 360;
   while (c1.h > 360)
      c1.h -= 360;

   if (c1.h < 120) {
      sat.r = (120 - c1.h) / 60.0;
      sat.g = c1.h / 60.0;
      sat.b = 0;
   } else if (c1.h < 240) {
      sat.r = 0;
      sat.g = (240 - c1.h) / 60.0;
      sat.b = (c1.h - 120) / 60.0;
   } else {
      sat.r = (c1.h - 240) / 60.0;
      sat.g = 0;
      sat.b = (360 - c1.h) / 60.0;
   }
   sat.r = MIN(sat.r,1);
   sat.g = MIN(sat.g,1);
   sat.b = MIN(sat.b,1);

   ctmp.r = 2 * c1.s * sat.r + (1 - c1.s);
   ctmp.g = 2 * c1.s * sat.g + (1 - c1.s);
   ctmp.b = 2 * c1.s * sat.b + (1 - c1.s);

   if (c1.l < 0.5) {
      c2.r = c1.l * ctmp.r;
      c2.g = c1.l * ctmp.g;
      c2.b = c1.l * ctmp.b;
   } else {
      c2.r = (1 - c1.l) * ctmp.r + 2 * c1.l - 1;
      c2.g = (1 - c1.l) * ctmp.g + 2 * c1.l - 1;
      c2.b = (1 - c1.l) * ctmp.b + 2 * c1.l - 1;
   }

   return(c2);
}

/*
	Compute YCC from RGB
	Luminance is between 0 and 1
	Both chrominance channels are between -0.5 and 0.5
*/
YCC RGB2YCC(COLOUR c1)
{
	YCC c2;

	c2.y  =  0.2989 * c1.r + 0.5866 * c1.g + 0.1145 * c1.b;
	c2.cr = -0.1687 * c1.r - 0.3312 * c1.g + 0.5000 * c1.b;
	c2.cb =  0.5000 * c1.r - 0.4183 * c1.g - 0.0816 * c1.b;

	return(c2);
}

/*
   Compute RGB from YCC
	Reverse of RGB2YCC()
*/
COLOUR YCC2RGB(YCC c1)
{
	COLOUR c2;

	c2.r = c1.y                  + 1.4022 * c1.cb;
	c2.g = c1.y - 0.3456 * c1.cr - 0.7145 * c1.cb;
	c2.b = c1.y + 1.7710 * c1.cr;
	if (c2.r < 0) c2.r = 0;
   if (c2.g < 0) c2.g = 0;
   if (c2.b < 0) c2.b = 0;
   if (c2.r > 1) c2.r = 1;
   if (c2.g > 1) c2.g = 1;
   if (c2.b > 1) c2.b = 1;

	return(c2);
}

/*
   Calculate RGB from HSV, reverse of RGB2HSV()
   Hue is in degrees
   Lightness is between 0 and 1
   Saturation is between 0 and 1
*/
COLOUR HSV2RGB(HSV c1)
{
   COLOUR c2,sat;

   while (c1.h < 0)
      c1.h += 360;
   while (c1.h > 360)
      c1.h -= 360;

   if (c1.h < 120) {
      sat.r = (120 - c1.h) / 60.0;
      sat.g = c1.h / 60.0;
      sat.b = 0;
   } else if (c1.h < 240) {
      sat.r = 0;
      sat.g = (240 - c1.h) / 60.0;
      sat.b = (c1.h - 120) / 60.0;
   } else {
      sat.r = (c1.h - 240) / 60.0;
      sat.g = 0;
      sat.b = (360 - c1.h) / 60.0;
   }
   sat.r = MIN(sat.r,1);
   sat.g = MIN(sat.g,1);
   sat.b = MIN(sat.b,1);

   c2.r = (1 - c1.s + c1.s * sat.r) * c1.v;
   c2.g = (1 - c1.s + c1.s * sat.g) * c1.v;
   c2.b = (1 - c1.s + c1.s * sat.b) * c1.v;

   return(c2);
}

/*
   Calculate HSV from RGB
   Hue is in degrees
   Lightness is betweeen 0 and 1
   Saturation is between 0 and 1
*/
HSV RGB2HSV(COLOUR c1)
{
   double themin,themax,delta;
   HSV c2;

   themin = MIN(c1.r,MIN(c1.g,c1.b));
   themax = MAX(c1.r,MAX(c1.g,c1.b));
   delta = themax - themin;
   c2.v = themax;
   c2.s = 0;
   if (themax > 0)
      c2.s = delta / themax;
   c2.h = 0;
   if (delta > 0) {
      if (themax == c1.r && themax != c1.g)
         c2.h += (c1.g - c1.b) / delta;
      if (themax == c1.g && themax != c1.b)
         c2.h += (2 + (c1.b - c1.r) / delta);
      if (themax == c1.b && themax != c1.r)
         c2.h += (4 + (c1.r - c1.g) / delta);
      c2.h *= 60;
		if (c2.h < 0)
			c2.h += 360;
   }
   return(c2);
}

/*-------------------------------------------------------------------------
	Return the plane parameters a,b,c from vertices p[]
*/
void PlaneParam(XYZ *p,double *a,double *b,double *c,double *d)
{
   *a = p[0].y * (p[1].z - p[2].z) +
        p[1].y * (p[2].z - p[0].z) +
        p[2].y * (p[0].z - p[1].z);
   *b = p[0].z * (p[1].x - p[2].x) +
        p[1].z * (p[2].x - p[0].x) +
        p[2].z * (p[0].x - p[1].x);
   *c = p[0].x * (p[1].y - p[2].y) +
        p[1].x * (p[2].y - p[0].y) +
        p[2].x * (p[0].y - p[1].y);
   *d = - p[0].x * (p[1].y * p[2].z - p[2].y * p[1].z)
        - p[1].x * (p[2].y * p[0].z - p[0].y * p[2].z)
        - p[2].x * (p[0].y * p[1].z - p[1].y * p[0].z);
}
	
/*
	Given a quad defined by p[0], p[1], p[2], p[3]
	and two ratios mua and mub
	Return the interpolated point

     p[0]    -mux->
     +-----
     |     -----     p[1]
   |  |         -----+
  muy  |             |
   |    |             |
   v    +-----         |
        p[2]  -----     |
                   -----+
                        p[3]
*/
XY PointInPolygon(XY *p,double mux,double muy)
{
	double x1,y1,x2,y2,x3,y3,x4,y4;
	double denom,numer;
	XY q;

   x1 = p[0].x + mux * (p[1].x - p[0].x);
   y1 = p[0].y + mux * (p[1].y - p[0].y);
   x2 = p[2].x + mux * (p[3].x - p[2].x);
   y2 = p[2].y + mux * (p[3].y - p[2].y);
   x3 = p[0].x + muy * (p[2].x - p[0].x);
   y3 = p[0].y + muy * (p[2].y - p[0].y);
   x4 = p[1].x + muy * (p[3].x - p[1].x);
   y4 = p[1].y + muy * (p[3].y - p[1].y);
   numer = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
   denom = ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));
	// mu = numer / denom;
	if (ABS(denom) < EPS) { // Should not happen in proper usage
		q.x = 0;
		q.y = 0;
	} else {
   	q.x = x1 + numer * (x2 - x1) / denom;
   	q.y = y1 + numer * (y2 - y1) / denom;
	}

	return(q);
}

/*-------------------------------------------------------------------------
	Return TRUE if the point "q" lies within the bounded polygon
	p[] with "np" vertices, assume the polygon is convex!
	In any case the distance "dist" is returned
*/
int PointIn3DPolygon(int np,XYZ *p,XYZ q,double *dist)
{
	int i,sign=0;
	double a,b,c,d;
	double dd,denom;
	XYZ n,p1[3];

	/* Determine the plane parameters (the normal is a,b,c) */
	PlaneParam(p,&n.x,&n.y,&n.z,&d);

	*dist = 0;
	denom = sqrt(n.x*n.x + n.y*n.y + n.z*n.z);
	if (ABS(denom) < EPSILON)		/* Degenerate plane */
		return(FALSE);
	*dist = (n.x * q.x + n.y * q.y + n.z * q.z + d) / denom;

	for (i=0;i<np;i++) {
		p1[0] = p[i]; 
		p1[1] = p[(i+1)%np]; 
		p1[2].x = p[i].x + n.x;
   	p1[2].y = p[i].y + n.y;
   	p1[2].z = p[i].z + n.z;
		PlaneParam(p1,&a,&b,&c,&d);
		dd = (a * q.x + b * q.y + c * q.z + d) / sqrt(a*a + b*b + c*c);
		if (i == 0)
			sign = SIGN(dd);
		else if (sign != SIGN(dd))
      	return(FALSE);
	}

	return(TRUE);
}

/*-------------------------------------------------------------------------
   Determines which side of a line the point (x,y) lies.
   The line goes from (x1,y1) to (x2,y2)
   Return codes are -1 for points to the left
                     0 for points on the line
                    +1 for points to the right
*/
int WhichSide(double x,double y,double x1,double y1,double x2,double y2)
{
    double dist;

    dist = (y - y1) * (x2 - x1) - (y2 - y1) * (x - x1);
    if (dist > 0)
       return(-1);
    else if (dist < 0)
       return(1);
    else
       return(0);
}

/*-------------------------------------------------------------------------
   Given a plane through the points p1,p2,p3 determine the intersection 
   of the perpendicular from the point (xp,yp) on the x-y plane.
*/
double PlanePoint(double xp,double yp,XYZ p1,XYZ p2,XYZ p3)
{
   double a,b,c,d;

   a = p1.y * (p2.z - p3.z) + p2.y * (p3.z - p1.z) + p3.y * (p1.z - p2.z);
   b = p1.z * (p2.x - p3.x) + p2.z * (p3.x - p1.x) + p3.z * (p1.x - p2.x);
   c = p1.x * (p2.y - p3.y) + p2.x * (p3.y - p1.y) + p3.x * (p1.y - p2.y);
   d = - p1.x * (p2.y * p3.z - p3.y * p2.z) 
        - p2.x * (p3.y * p1.z - p1.y * p3.z) 
        - p3.x * (p1.y * p2.z - p2.y * p1.z);
        
   if (ABS(c) > EPSILON)
      return(- (a * xp + b * yp + d) / c);
   else
      return(0.0);
}

/*-------------------------------------------------------------------------
   Returns TRUE if the point (xp,yp) lies inside the projection of the
   triangle (p1,p2,p3) onto the x-y plane
   A point is in the centre if it is on the same side of all the edges
   or if it lies on one of the edges.
*/
int InTriangle(double xp,double yp,XYZ p1,XYZ p2,XYZ p3)
{
   int side1,side2,side3;

   side1 = WhichSide(xp,yp,p1.x,p1.y,p2.x,p2.y);
   side2 = WhichSide(xp,yp,p2.x,p2.y,p3.x,p3.y);
   side3 = WhichSide(xp,yp,p3.x,p3.y,p1.x,p1.y);

   if (side1 == 0 && side2 == 0)
      return(TRUE);
   if (side2 == 0 && side3 == 0)
      return(TRUE);

   if (side1 == 0 && (side2 == side3))
      return(TRUE);
   if (side2 == 0 && (side1 == side3))
      return(TRUE);
   if (side3 == 0 && (side1 == side2))
      return(TRUE);
        
   if ((side1 == side2) && (side2 == side3))
      return(TRUE);
                
   return(FALSE);
}

/*-------------------------------------------------------------------------
	Calculate the length of a polygon
*/
double PolygonLength(XYZ *p,int np)
{
   int i;
   double length = 0;

   for (i=0;i<np-1;i++)
      length += VectorLength(p[i],p[i+1]);

   return(length);
}

/*-------------------------------------------------------------------------
   Calculate a curvature measure of a polygon
*/
double PolygonCurve(XYZ *p,int np)
{
   int i;
   double c,cmin=1e32,cmax=-1e32;
   XYZ p1,p2;

   if (np < 3)
      return(0.0);

   p1.x = p[1].x - p[0].x;
   p1.y = p[1].y - p[0].y;
   p1.z = p[1].z - p[0].z;

   for (i=2;i<np;i++) {
      p2.x = p[i].x - p[0].x;
      p2.y = p[i].y - p[0].y;
      p2.z = p[i].z - p[0].z;
      c = VectorAngle(p1,p2);
      cmax = MAX(c,cmax);
      cmin = MIN(c,cmin);
   }

   return((cmax - cmin)*RTOD);
}

/*-------------------------------------------------------------------------
   Given a grid cell and an isolevel, calculate the triangular
   facets requied to represent the isosurface through the cell.
   Return the number of triangular facets, the array "triangles"
   will be loaded up with the vertices at most 5 triangular facets.
   0 will be returned if the grid cell is either totally above
   of totally below the isolevel.
*/
int PolygoniseCube(GRIDCELL g,double iso,TRIANGLE *tri)
{
	int i,ntri = 0;
	int cubeindex;
	XYZ vertlist[12];
/*
	int edgeTable[256].  It corresponds to the 2^8 possible combinations of
	of the eight (n) vertices either existing inside or outside (2^n) of the
	surface.  A vertex is inside of a surface if the value at that vertex is
	less than that of the surface you are scanning for.  The table index is
	constructed bitwise with bit 0 corresponding to vertex 0, bit 1 to vert
	1.. bit 7 to vert 7.  The value in the table tells you which edges of
	the table are intersected by the surface.  Once again bit 0 corresponds
	to edge 0 and so on, up to edge 12. 
	Constructing the table simply consisted of having a program run thru
	the 256 cases and setting the edge bit if the vertices at either end of
	the edge had different values (one is inside while the other is out). 
	The purpose of the table is to speed up the scanning process.  Only the
	edges whose bit's are set contain vertices of the surface.
	Vertex 0 is on the bottom face, back edge, left side.  
	The progression of vertices is clockwise around the bottom face
	and then clockwise around the top face of the cube.  Edge 0 goes from
	vertex 0 to vertex 1, Edge 1 is from 2->3 and so on around clockwise to
	vertex 0 again. Then Edge 4 to 7 make up the top face, 4->5, 5->6, 6->7
	and 7->4.  Edge 8 thru 11 are the vertical edges from vert 0->4, 1->5,
	2->6, and 3->7.
	    4--------5     *---4----*
 	   /|       /|    /|       /|    
 	  / |      / |   7 |      5 |    
 	 /  |     /  |  /  8     /  9    
 	7--------6   | *----6---*   | 
 	|   |    |   | |   |    |   |
 	|   0----|---1 |   *---0|---*  
 	|  /     |  /  11 /     10 /   
 	| /      | /   | 3      | 1
 	|/       |/    |/       |/    
 	3--------2     *---2----*
*/
int edgeTable[256]={
0x0  , 0x109, 0x203, 0x30a, 0x406, 0x50f, 0x605, 0x70c,
0x80c, 0x905, 0xa0f, 0xb06, 0xc0a, 0xd03, 0xe09, 0xf00,
0x190, 0x99 , 0x393, 0x29a, 0x596, 0x49f, 0x795, 0x69c,
0x99c, 0x895, 0xb9f, 0xa96, 0xd9a, 0xc93, 0xf99, 0xe90,
0x230, 0x339, 0x33 , 0x13a, 0x636, 0x73f, 0x435, 0x53c,
0xa3c, 0xb35, 0x83f, 0x936, 0xe3a, 0xf33, 0xc39, 0xd30,
0x3a0, 0x2a9, 0x1a3, 0xaa , 0x7a6, 0x6af, 0x5a5, 0x4ac,
0xbac, 0xaa5, 0x9af, 0x8a6, 0xfaa, 0xea3, 0xda9, 0xca0,
0x460, 0x569, 0x663, 0x76a, 0x66 , 0x16f, 0x265, 0x36c,
0xc6c, 0xd65, 0xe6f, 0xf66, 0x86a, 0x963, 0xa69, 0xb60,
0x5f0, 0x4f9, 0x7f3, 0x6fa, 0x1f6, 0xff , 0x3f5, 0x2fc,
0xdfc, 0xcf5, 0xfff, 0xef6, 0x9fa, 0x8f3, 0xbf9, 0xaf0,
0x650, 0x759, 0x453, 0x55a, 0x256, 0x35f, 0x55 , 0x15c,
0xe5c, 0xf55, 0xc5f, 0xd56, 0xa5a, 0xb53, 0x859, 0x950,
0x7c0, 0x6c9, 0x5c3, 0x4ca, 0x3c6, 0x2cf, 0x1c5, 0xcc ,
0xfcc, 0xec5, 0xdcf, 0xcc6, 0xbca, 0xac3, 0x9c9, 0x8c0,
0x8c0, 0x9c9, 0xac3, 0xbca, 0xcc6, 0xdcf, 0xec5, 0xfcc,
0xcc , 0x1c5, 0x2cf, 0x3c6, 0x4ca, 0x5c3, 0x6c9, 0x7c0,
0x950, 0x859, 0xb53, 0xa5a, 0xd56, 0xc5f, 0xf55, 0xe5c,
0x15c, 0x55 , 0x35f, 0x256, 0x55a, 0x453, 0x759, 0x650,
0xaf0, 0xbf9, 0x8f3, 0x9fa, 0xef6, 0xfff, 0xcf5, 0xdfc,
0x2fc, 0x3f5, 0xff , 0x1f6, 0x6fa, 0x7f3, 0x4f9, 0x5f0,
0xb60, 0xa69, 0x963, 0x86a, 0xf66, 0xe6f, 0xd65, 0xc6c,
0x36c, 0x265, 0x16f, 0x66 , 0x76a, 0x663, 0x569, 0x460,
0xca0, 0xda9, 0xea3, 0xfaa, 0x8a6, 0x9af, 0xaa5, 0xbac,
0x4ac, 0x5a5, 0x6af, 0x7a6, 0xaa , 0x1a3, 0x2a9, 0x3a0,
0xd30, 0xc39, 0xf33, 0xe3a, 0x936, 0x83f, 0xb35, 0xa3c,
0x53c, 0x435, 0x73f, 0x636, 0x13a, 0x33 , 0x339, 0x230,
0xe90, 0xf99, 0xc93, 0xd9a, 0xa96, 0xb9f, 0x895, 0x99c,
0x69c, 0x795, 0x49f, 0x596, 0x29a, 0x393, 0x99 , 0x190,
0xf00, 0xe09, 0xd03, 0xc0a, 0xb06, 0xa0f, 0x905, 0x80c,
0x70c, 0x605, 0x50f, 0x406, 0x30a, 0x203, 0x109, 0x0   };

/*
	int triTable[256][16] also corresponds to the 256 possible combinations
	of vertices.
	The [16] dimension of the table is again the list of edges of the cube
	which are intersected by the surface.  This time however, the edges are
	enumerated in the order of the vertices making up the triangle mesh of
	the surface.  Each edge contains one vertex that is on the surface. 
	Each triple of edges listed in the table contains the vertices of one
	triangle on the mesh.  The are 16 entries because it has been shown that
	there are at most 5 triangles in a cube and each "edge triple" list is
	terminated with the value -1. 
	For example triTable[3] contains 
	{1, 8, 3, 9, 8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}
	This corresponds to the case of a cube whose vertex 0 and 1 are inside
	of the surface and the rest of the verts are outside (00000001 bitwise
	OR'ed with 00000010 makes 00000011 == 3).  Therefore, this cube is
	intersected by the surface roughly in the form of a plane which cuts
	edges 8,9,1 and 3.  This quadrilateral can be constructed from two
	triangles: one which is made of the intersection vertices found on edges
	1,8, and 3; the other is formed from the vertices on edges 9,8, and 1. 
	Remember, each intersected edge contains only one surface vertex.  The
	vertex triples are listed in counter clockwise order for proper facing.
*/
int triTable[256][16] =
{{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{0, 1, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{1, 8, 3, 9, 8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{0, 8, 3, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{9, 2, 10, 0, 2, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{2, 8, 3, 2, 10, 8, 10, 9, 8, -1, -1, -1, -1, -1, -1, -1},
{3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{0, 11, 2, 8, 11, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{1, 9, 0, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{1, 11, 2, 1, 9, 11, 9, 8, 11, -1, -1, -1, -1, -1, -1, -1},
{3, 10, 1, 11, 10, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{0, 10, 1, 0, 8, 10, 8, 11, 10, -1, -1, -1, -1, -1, -1, -1},
{3, 9, 0, 3, 11, 9, 11, 10, 9, -1, -1, -1, -1, -1, -1, -1},
{9, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{4, 3, 0, 7, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{0, 1, 9, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{4, 1, 9, 4, 7, 1, 7, 3, 1, -1, -1, -1, -1, -1, -1, -1},
{1, 2, 10, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{3, 4, 7, 3, 0, 4, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1},
{9, 2, 10, 9, 0, 2, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1},
{2, 10, 9, 2, 9, 7, 2, 7, 3, 7, 9, 4, -1, -1, -1, -1},
{8, 4, 7, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{11, 4, 7, 11, 2, 4, 2, 0, 4, -1, -1, -1, -1, -1, -1, -1},
{9, 0, 1, 8, 4, 7, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
{4, 7, 11, 9, 4, 11, 9, 11, 2, 9, 2, 1, -1, -1, -1, -1},
{3, 10, 1, 3, 11, 10, 7, 8, 4, -1, -1, -1, -1, -1, -1, -1},
{1, 11, 10, 1, 4, 11, 1, 0, 4, 7, 11, 4, -1, -1, -1, -1},
{4, 7, 8, 9, 0, 11, 9, 11, 10, 11, 0, 3, -1, -1, -1, -1},
{4, 7, 11, 4, 11, 9, 9, 11, 10, -1, -1, -1, -1, -1, -1, -1},
{9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{9, 5, 4, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{0, 5, 4, 1, 5, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{8, 5, 4, 8, 3, 5, 3, 1, 5, -1, -1, -1, -1, -1, -1, -1},
{1, 2, 10, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{3, 0, 8, 1, 2, 10, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
{5, 2, 10, 5, 4, 2, 4, 0, 2, -1, -1, -1, -1, -1, -1, -1},
{2, 10, 5, 3, 2, 5, 3, 5, 4, 3, 4, 8, -1, -1, -1, -1},
{9, 5, 4, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{0, 11, 2, 0, 8, 11, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
{0, 5, 4, 0, 1, 5, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
{2, 1, 5, 2, 5, 8, 2, 8, 11, 4, 8, 5, -1, -1, -1, -1},
{10, 3, 11, 10, 1, 3, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1},
{4, 9, 5, 0, 8, 1, 8, 10, 1, 8, 11, 10, -1, -1, -1, -1},
{5, 4, 0, 5, 0, 11, 5, 11, 10, 11, 0, 3, -1, -1, -1, -1},
{5, 4, 8, 5, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1},
{9, 7, 8, 5, 7, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{9, 3, 0, 9, 5, 3, 5, 7, 3, -1, -1, -1, -1, -1, -1, -1},
{0, 7, 8, 0, 1, 7, 1, 5, 7, -1, -1, -1, -1, -1, -1, -1},
{1, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{9, 7, 8, 9, 5, 7, 10, 1, 2, -1, -1, -1, -1, -1, -1, -1},
{10, 1, 2, 9, 5, 0, 5, 3, 0, 5, 7, 3, -1, -1, -1, -1},
{8, 0, 2, 8, 2, 5, 8, 5, 7, 10, 5, 2, -1, -1, -1, -1},
{2, 10, 5, 2, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1},
{7, 9, 5, 7, 8, 9, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1},
{9, 5, 7, 9, 7, 2, 9, 2, 0, 2, 7, 11, -1, -1, -1, -1},
{2, 3, 11, 0, 1, 8, 1, 7, 8, 1, 5, 7, -1, -1, -1, -1},
{11, 2, 1, 11, 1, 7, 7, 1, 5, -1, -1, -1, -1, -1, -1, -1},
{9, 5, 8, 8, 5, 7, 10, 1, 3, 10, 3, 11, -1, -1, -1, -1},
{5, 7, 0, 5, 0, 9, 7, 11, 0, 1, 0, 10, 11, 10, 0, -1},
{11, 10, 0, 11, 0, 3, 10, 5, 0, 8, 0, 7, 5, 7, 0, -1},
{11, 10, 5, 7, 11, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{0, 8, 3, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{9, 0, 1, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{1, 8, 3, 1, 9, 8, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1},
{1, 6, 5, 2, 6, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{1, 6, 5, 1, 2, 6, 3, 0, 8, -1, -1, -1, -1, -1, -1, -1},
{9, 6, 5, 9, 0, 6, 0, 2, 6, -1, -1, -1, -1, -1, -1, -1},
{5, 9, 8, 5, 8, 2, 5, 2, 6, 3, 2, 8, -1, -1, -1, -1},
{2, 3, 11, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{11, 0, 8, 11, 2, 0, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1},
{0, 1, 9, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1},
{5, 10, 6, 1, 9, 2, 9, 11, 2, 9, 8, 11, -1, -1, -1, -1},
{6, 3, 11, 6, 5, 3, 5, 1, 3, -1, -1, -1, -1, -1, -1, -1},
{0, 8, 11, 0, 11, 5, 0, 5, 1, 5, 11, 6, -1, -1, -1, -1},
{3, 11, 6, 0, 3, 6, 0, 6, 5, 0, 5, 9, -1, -1, -1, -1},
{6, 5, 9, 6, 9, 11, 11, 9, 8, -1, -1, -1, -1, -1, -1, -1},
{5, 10, 6, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{4, 3, 0, 4, 7, 3, 6, 5, 10, -1, -1, -1, -1, -1, -1, -1},
{1, 9, 0, 5, 10, 6, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1},
{10, 6, 5, 1, 9, 7, 1, 7, 3, 7, 9, 4, -1, -1, -1, -1},
{6, 1, 2, 6, 5, 1, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1},
{1, 2, 5, 5, 2, 6, 3, 0, 4, 3, 4, 7, -1, -1, -1, -1},
{8, 4, 7, 9, 0, 5, 0, 6, 5, 0, 2, 6, -1, -1, -1, -1},
{7, 3, 9, 7, 9, 4, 3, 2, 9, 5, 9, 6, 2, 6, 9, -1},
{3, 11, 2, 7, 8, 4, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1},
{5, 10, 6, 4, 7, 2, 4, 2, 0, 2, 7, 11, -1, -1, -1, -1},
{0, 1, 9, 4, 7, 8, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1},
{9, 2, 1, 9, 11, 2, 9, 4, 11, 7, 11, 4, 5, 10, 6, -1},
{8, 4, 7, 3, 11, 5, 3, 5, 1, 5, 11, 6, -1, -1, -1, -1},
{5, 1, 11, 5, 11, 6, 1, 0, 11, 7, 11, 4, 0, 4, 11, -1},
{0, 5, 9, 0, 6, 5, 0, 3, 6, 11, 6, 3, 8, 4, 7, -1},
{6, 5, 9, 6, 9, 11, 4, 7, 9, 7, 11, 9, -1, -1, -1, -1},
{10, 4, 9, 6, 4, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{4, 10, 6, 4, 9, 10, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1},
{10, 0, 1, 10, 6, 0, 6, 4, 0, -1, -1, -1, -1, -1, -1, -1},
{8, 3, 1, 8, 1, 6, 8, 6, 4, 6, 1, 10, -1, -1, -1, -1},
{1, 4, 9, 1, 2, 4, 2, 6, 4, -1, -1, -1, -1, -1, -1, -1},
{3, 0, 8, 1, 2, 9, 2, 4, 9, 2, 6, 4, -1, -1, -1, -1},
{0, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{8, 3, 2, 8, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1},
{10, 4, 9, 10, 6, 4, 11, 2, 3, -1, -1, -1, -1, -1, -1, -1},
{0, 8, 2, 2, 8, 11, 4, 9, 10, 4, 10, 6, -1, -1, -1, -1},
{3, 11, 2, 0, 1, 6, 0, 6, 4, 6, 1, 10, -1, -1, -1, -1},
{6, 4, 1, 6, 1, 10, 4, 8, 1, 2, 1, 11, 8, 11, 1, -1},
{9, 6, 4, 9, 3, 6, 9, 1, 3, 11, 6, 3, -1, -1, -1, -1},
{8, 11, 1, 8, 1, 0, 11, 6, 1, 9, 1, 4, 6, 4, 1, -1},
{3, 11, 6, 3, 6, 0, 0, 6, 4, -1, -1, -1, -1, -1, -1, -1},
{6, 4, 8, 11, 6, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{7, 10, 6, 7, 8, 10, 8, 9, 10, -1, -1, -1, -1, -1, -1, -1},
{0, 7, 3, 0, 10, 7, 0, 9, 10, 6, 7, 10, -1, -1, -1, -1},
{10, 6, 7, 1, 10, 7, 1, 7, 8, 1, 8, 0, -1, -1, -1, -1},
{10, 6, 7, 10, 7, 1, 1, 7, 3, -1, -1, -1, -1, -1, -1, -1},
{1, 2, 6, 1, 6, 8, 1, 8, 9, 8, 6, 7, -1, -1, -1, -1},
{2, 6, 9, 2, 9, 1, 6, 7, 9, 0, 9, 3, 7, 3, 9, -1},
{7, 8, 0, 7, 0, 6, 6, 0, 2, -1, -1, -1, -1, -1, -1, -1},
{7, 3, 2, 6, 7, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{2, 3, 11, 10, 6, 8, 10, 8, 9, 8, 6, 7, -1, -1, -1, -1},
{2, 0, 7, 2, 7, 11, 0, 9, 7, 6, 7, 10, 9, 10, 7, -1},
{1, 8, 0, 1, 7, 8, 1, 10, 7, 6, 7, 10, 2, 3, 11, -1},
{11, 2, 1, 11, 1, 7, 10, 6, 1, 6, 7, 1, -1, -1, -1, -1},
{8, 9, 6, 8, 6, 7, 9, 1, 6, 11, 6, 3, 1, 3, 6, -1},
{0, 9, 1, 11, 6, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{7, 8, 0, 7, 0, 6, 3, 11, 0, 11, 6, 0, -1, -1, -1, -1},
{7, 11, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{3, 0, 8, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{0, 1, 9, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{8, 1, 9, 8, 3, 1, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1},
{10, 1, 2, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{1, 2, 10, 3, 0, 8, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1},
{2, 9, 0, 2, 10, 9, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1},
{6, 11, 7, 2, 10, 3, 10, 8, 3, 10, 9, 8, -1, -1, -1, -1},
{7, 2, 3, 6, 2, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{7, 0, 8, 7, 6, 0, 6, 2, 0, -1, -1, -1, -1, -1, -1, -1},
{2, 7, 6, 2, 3, 7, 0, 1, 9, -1, -1, -1, -1, -1, -1, -1},
{1, 6, 2, 1, 8, 6, 1, 9, 8, 8, 7, 6, -1, -1, -1, -1},
{10, 7, 6, 10, 1, 7, 1, 3, 7, -1, -1, -1, -1, -1, -1, -1},
{10, 7, 6, 1, 7, 10, 1, 8, 7, 1, 0, 8, -1, -1, -1, -1},
{0, 3, 7, 0, 7, 10, 0, 10, 9, 6, 10, 7, -1, -1, -1, -1},
{7, 6, 10, 7, 10, 8, 8, 10, 9, -1, -1, -1, -1, -1, -1, -1},
{6, 8, 4, 11, 8, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{3, 6, 11, 3, 0, 6, 0, 4, 6, -1, -1, -1, -1, -1, -1, -1},
{8, 6, 11, 8, 4, 6, 9, 0, 1, -1, -1, -1, -1, -1, -1, -1},
{9, 4, 6, 9, 6, 3, 9, 3, 1, 11, 3, 6, -1, -1, -1, -1},
{6, 8, 4, 6, 11, 8, 2, 10, 1, -1, -1, -1, -1, -1, -1, -1},
{1, 2, 10, 3, 0, 11, 0, 6, 11, 0, 4, 6, -1, -1, -1, -1},
{4, 11, 8, 4, 6, 11, 0, 2, 9, 2, 10, 9, -1, -1, -1, -1},
{10, 9, 3, 10, 3, 2, 9, 4, 3, 11, 3, 6, 4, 6, 3, -1},
{8, 2, 3, 8, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1},
{0, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{1, 9, 0, 2, 3, 4, 2, 4, 6, 4, 3, 8, -1, -1, -1, -1},
{1, 9, 4, 1, 4, 2, 2, 4, 6, -1, -1, -1, -1, -1, -1, -1},
{8, 1, 3, 8, 6, 1, 8, 4, 6, 6, 10, 1, -1, -1, -1, -1},
{10, 1, 0, 10, 0, 6, 6, 0, 4, -1, -1, -1, -1, -1, -1, -1},
{4, 6, 3, 4, 3, 8, 6, 10, 3, 0, 3, 9, 10, 9, 3, -1},
{10, 9, 4, 6, 10, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{4, 9, 5, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{0, 8, 3, 4, 9, 5, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1},
{5, 0, 1, 5, 4, 0, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1},
{11, 7, 6, 8, 3, 4, 3, 5, 4, 3, 1, 5, -1, -1, -1, -1},
{9, 5, 4, 10, 1, 2, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1},
{6, 11, 7, 1, 2, 10, 0, 8, 3, 4, 9, 5, -1, -1, -1, -1},
{7, 6, 11, 5, 4, 10, 4, 2, 10, 4, 0, 2, -1, -1, -1, -1},
{3, 4, 8, 3, 5, 4, 3, 2, 5, 10, 5, 2, 11, 7, 6, -1},
{7, 2, 3, 7, 6, 2, 5, 4, 9, -1, -1, -1, -1, -1, -1, -1},
{9, 5, 4, 0, 8, 6, 0, 6, 2, 6, 8, 7, -1, -1, -1, -1},
{3, 6, 2, 3, 7, 6, 1, 5, 0, 5, 4, 0, -1, -1, -1, -1},
{6, 2, 8, 6, 8, 7, 2, 1, 8, 4, 8, 5, 1, 5, 8, -1},
{9, 5, 4, 10, 1, 6, 1, 7, 6, 1, 3, 7, -1, -1, -1, -1},
{1, 6, 10, 1, 7, 6, 1, 0, 7, 8, 7, 0, 9, 5, 4, -1},
{4, 0, 10, 4, 10, 5, 0, 3, 10, 6, 10, 7, 3, 7, 10, -1},
{7, 6, 10, 7, 10, 8, 5, 4, 10, 4, 8, 10, -1, -1, -1, -1},
{6, 9, 5, 6, 11, 9, 11, 8, 9, -1, -1, -1, -1, -1, -1, -1},
{3, 6, 11, 0, 6, 3, 0, 5, 6, 0, 9, 5, -1, -1, -1, -1},
{0, 11, 8, 0, 5, 11, 0, 1, 5, 5, 6, 11, -1, -1, -1, -1},
{6, 11, 3, 6, 3, 5, 5, 3, 1, -1, -1, -1, -1, -1, -1, -1},
{1, 2, 10, 9, 5, 11, 9, 11, 8, 11, 5, 6, -1, -1, -1, -1},
{0, 11, 3, 0, 6, 11, 0, 9, 6, 5, 6, 9, 1, 2, 10, -1},
{11, 8, 5, 11, 5, 6, 8, 0, 5, 10, 5, 2, 0, 2, 5, -1},
{6, 11, 3, 6, 3, 5, 2, 10, 3, 10, 5, 3, -1, -1, -1, -1},
{5, 8, 9, 5, 2, 8, 5, 6, 2, 3, 8, 2, -1, -1, -1, -1},
{9, 5, 6, 9, 6, 0, 0, 6, 2, -1, -1, -1, -1, -1, -1, -1},
{1, 5, 8, 1, 8, 0, 5, 6, 8, 3, 8, 2, 6, 2, 8, -1},
{1, 5, 6, 2, 1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{1, 3, 6, 1, 6, 10, 3, 8, 6, 5, 6, 9, 8, 9, 6, -1},
{10, 1, 0, 10, 0, 6, 9, 5, 0, 5, 6, 0, -1, -1, -1, -1},
{0, 3, 8, 5, 6, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{10, 5, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{11, 5, 10, 7, 5, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{11, 5, 10, 11, 7, 5, 8, 3, 0, -1, -1, -1, -1, -1, -1, -1},
{5, 11, 7, 5, 10, 11, 1, 9, 0, -1, -1, -1, -1, -1, -1, -1},
{10, 7, 5, 10, 11, 7, 9, 8, 1, 8, 3, 1, -1, -1, -1, -1},
{11, 1, 2, 11, 7, 1, 7, 5, 1, -1, -1, -1, -1, -1, -1, -1},
{0, 8, 3, 1, 2, 7, 1, 7, 5, 7, 2, 11, -1, -1, -1, -1},
{9, 7, 5, 9, 2, 7, 9, 0, 2, 2, 11, 7, -1, -1, -1, -1},
{7, 5, 2, 7, 2, 11, 5, 9, 2, 3, 2, 8, 9, 8, 2, -1},
{2, 5, 10, 2, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1},
{8, 2, 0, 8, 5, 2, 8, 7, 5, 10, 2, 5, -1, -1, -1, -1},
{9, 0, 1, 5, 10, 3, 5, 3, 7, 3, 10, 2, -1, -1, -1, -1},
{9, 8, 2, 9, 2, 1, 8, 7, 2, 10, 2, 5, 7, 5, 2, -1},
{1, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{0, 8, 7, 0, 7, 1, 1, 7, 5, -1, -1, -1, -1, -1, -1, -1},
{9, 0, 3, 9, 3, 5, 5, 3, 7, -1, -1, -1, -1, -1, -1, -1},
{9, 8, 7, 5, 9, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{5, 8, 4, 5, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1},
{5, 0, 4, 5, 11, 0, 5, 10, 11, 11, 3, 0, -1, -1, -1, -1},
{0, 1, 9, 8, 4, 10, 8, 10, 11, 10, 4, 5, -1, -1, -1, -1},
{10, 11, 4, 10, 4, 5, 11, 3, 4, 9, 4, 1, 3, 1, 4, -1},
{2, 5, 1, 2, 8, 5, 2, 11, 8, 4, 5, 8, -1, -1, -1, -1},
{0, 4, 11, 0, 11, 3, 4, 5, 11, 2, 11, 1, 5, 1, 11, -1},
{0, 2, 5, 0, 5, 9, 2, 11, 5, 4, 5, 8, 11, 8, 5, -1},
{9, 4, 5, 2, 11, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{2, 5, 10, 3, 5, 2, 3, 4, 5, 3, 8, 4, -1, -1, -1, -1},
{5, 10, 2, 5, 2, 4, 4, 2, 0, -1, -1, -1, -1, -1, -1, -1},
{3, 10, 2, 3, 5, 10, 3, 8, 5, 4, 5, 8, 0, 1, 9, -1},
{5, 10, 2, 5, 2, 4, 1, 9, 2, 9, 4, 2, -1, -1, -1, -1},
{8, 4, 5, 8, 5, 3, 3, 5, 1, -1, -1, -1, -1, -1, -1, -1},
{0, 4, 5, 1, 0, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{8, 4, 5, 8, 5, 3, 9, 0, 5, 0, 3, 5, -1, -1, -1, -1},
{9, 4, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{4, 11, 7, 4, 9, 11, 9, 10, 11, -1, -1, -1, -1, -1, -1, -1},
{0, 8, 3, 4, 9, 7, 9, 11, 7, 9, 10, 11, -1, -1, -1, -1},
{1, 10, 11, 1, 11, 4, 1, 4, 0, 7, 4, 11, -1, -1, -1, -1},
{3, 1, 4, 3, 4, 8, 1, 10, 4, 7, 4, 11, 10, 11, 4, -1},
{4, 11, 7, 9, 11, 4, 9, 2, 11, 9, 1, 2, -1, -1, -1, -1},
{9, 7, 4, 9, 11, 7, 9, 1, 11, 2, 11, 1, 0, 8, 3, -1},
{11, 7, 4, 11, 4, 2, 2, 4, 0, -1, -1, -1, -1, -1, -1, -1},
{11, 7, 4, 11, 4, 2, 8, 3, 4, 3, 2, 4, -1, -1, -1, -1},
{2, 9, 10, 2, 7, 9, 2, 3, 7, 7, 4, 9, -1, -1, -1, -1},
{9, 10, 7, 9, 7, 4, 10, 2, 7, 8, 7, 0, 2, 0, 7, -1},
{3, 7, 10, 3, 10, 2, 7, 4, 10, 1, 10, 0, 4, 0, 10, -1},
{1, 10, 2, 8, 7, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{4, 9, 1, 4, 1, 7, 7, 1, 3, -1, -1, -1, -1, -1, -1, -1},
{4, 9, 1, 4, 1, 7, 0, 8, 1, 8, 7, 1, -1, -1, -1, -1},
{4, 0, 3, 7, 4, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{4, 8, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{9, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{3, 0, 9, 3, 9, 11, 11, 9, 10, -1, -1, -1, -1, -1, -1, -1},
{0, 1, 10, 0, 10, 8, 8, 10, 11, -1, -1, -1, -1, -1, -1, -1},
{3, 1, 10, 11, 3, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{1, 2, 11, 1, 11, 9, 9, 11, 8, -1, -1, -1, -1, -1, -1, -1},
{3, 0, 9, 3, 9, 11, 1, 2, 9, 2, 11, 9, -1, -1, -1, -1},
{0, 2, 11, 8, 0, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{3, 2, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{2, 3, 8, 2, 8, 10, 10, 8, 9, -1, -1, -1, -1, -1, -1, -1},
{9, 10, 2, 0, 9, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{2, 3, 8, 2, 8, 10, 0, 1, 8, 1, 10, 8, -1, -1, -1, -1},
{1, 10, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{1, 3, 8, 9, 1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{0, 9, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{0, 3, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}};

   /*
      Determine the index into the edge table which
      tells us which vertices are inside of the surface
   */
   cubeindex = 0;
   if (g.val[0] < iso) cubeindex |= 1;
   if (g.val[1] < iso) cubeindex |= 2;
   if (g.val[2] < iso) cubeindex |= 4;
   if (g.val[3] < iso) cubeindex |= 8;
   if (g.val[4] < iso) cubeindex |= 16;
   if (g.val[5] < iso) cubeindex |= 32;
   if (g.val[6] < iso) cubeindex |= 64;
   if (g.val[7] < iso) cubeindex |= 128;

   /* Cube is entirely in/out of the surface */
   if (edgeTable[cubeindex] == 0)
      return(0);

   /* Find the vertices where the surface intersects the cube */
   if (edgeTable[cubeindex] & 1) {
      vertlist[0] = VertexInterp(iso,g.p[0],g.p[1],g.val[0],g.val[1]);
	}
   if (edgeTable[cubeindex] & 2) {
      vertlist[1] = VertexInterp(iso,g.p[1],g.p[2],g.val[1],g.val[2]);
	}
   if (edgeTable[cubeindex] & 4) {
      vertlist[2] = VertexInterp(iso,g.p[2],g.p[3],g.val[2],g.val[3]);
	}
   if (edgeTable[cubeindex] & 8) {
      vertlist[3] = VertexInterp(iso,g.p[3],g.p[0],g.val[3],g.val[0]);
	}
   if (edgeTable[cubeindex] & 16) {
      vertlist[4] = VertexInterp(iso,g.p[4],g.p[5],g.val[4],g.val[5]);
	}
   if (edgeTable[cubeindex] & 32) {
      vertlist[5] = VertexInterp(iso,g.p[5],g.p[6],g.val[5],g.val[6]);
	}
   if (edgeTable[cubeindex] & 64) {
      vertlist[6] = VertexInterp(iso,g.p[6],g.p[7],g.val[6],g.val[7]);
	}
   if (edgeTable[cubeindex] & 128) {
      vertlist[7] = VertexInterp(iso,g.p[7],g.p[4],g.val[7],g.val[4]);
	}
   if (edgeTable[cubeindex] & 256) {
      vertlist[8] = VertexInterp(iso,g.p[0],g.p[4],g.val[0],g.val[4]);
	}
   if (edgeTable[cubeindex] & 512) {
      vertlist[9] = VertexInterp(iso,g.p[1],g.p[5],g.val[1],g.val[5]);
	}
   if (edgeTable[cubeindex] & 1024) {
      vertlist[10] = VertexInterp(iso,g.p[2],g.p[6],g.val[2],g.val[6]);
	}
   if (edgeTable[cubeindex] & 2048) {
      vertlist[11] = VertexInterp(iso,g.p[3],g.p[7],g.val[3],g.val[7]);
	}

   /* Create the triangles */
   for (i=0;triTable[cubeindex][i]!=-1;i+=3) {
      tri[ntri].p[0] = vertlist[triTable[cubeindex][i  ]];
      tri[ntri].p[1] = vertlist[triTable[cubeindex][i+1]];
      tri[ntri].p[2] = vertlist[triTable[cubeindex][i+2]];
      ntri++;
   }

	return(ntri);
}

/*
   Polygonise a tetrahedron given its vertices within a cube
	This is an alternative algorithm to polygonisegrid.
	It results in a smoother surface but more triangular facets.

                      + 0
                     /|\
                    / | \
                   /  |  \
                  /   |   \
                 /    |    \
                /     |     \
               +-------------+ 1
              3 \     |     /
                 \    |    /
                  \   |   /
                   \  |  /
                    \ | /
                     \|/
                      + 2

	It's main purpose is still to polygonise a gridded dataset and
	would normally be called 6 times, one for each tetrahedron making
	up the grid cell.
	Given the grid labelling as in PolygniseGrid one would call
		PolygoniseTri(grid,iso,triangles,0,2,3,7);
		PolygoniseTri(grid,iso,triangles,0,2,6,7);
		PolygoniseTri(grid,iso,triangles,0,4,6,7);
		PolygoniseTri(grid,iso,triangles,0,6,1,2);
		PolygoniseTri(grid,iso,triangles,0,6,1,4);
		PolygoniseTri(grid,iso,triangles,5,6,1,4);
*/
int PolygoniseTri(GRIDCELL g,double iso,
	TRIANGLE *tri,int v0,int v1,int v2,int v3)
{
   int ntri = 0;
   int triindex;
  
   /*
      Determine which of the 16 cases we have given which vertices
      are above or below the isosurface
   */
   triindex = 0;
   if (g.val[v0] < iso) triindex |= 1;
   if (g.val[v1] < iso) triindex |= 2;
   if (g.val[v2] < iso) triindex |= 4;
   if (g.val[v3] < iso) triindex |= 8;

   /* Form the vertices of the triangles for each case */
   switch (triindex) {
   case 0x00:
   case 0x0F:
      break;
   case 0x0E:
   case 0x01:
      tri[0].p[0] = VertexInterp(iso,g.p[v0],g.p[v1],g.val[v0],g.val[v1]);
      tri[0].p[1] = VertexInterp(iso,g.p[v0],g.p[v2],g.val[v0],g.val[v2]);
      tri[0].p[2] = VertexInterp(iso,g.p[v0],g.p[v3],g.val[v0],g.val[v3]);
      ntri++;
      break;
   case 0x0D:
   case 0x02:
      tri[0].p[0] = VertexInterp(iso,g.p[v1],g.p[v0],g.val[v1],g.val[v0]);
      tri[0].p[1] = VertexInterp(iso,g.p[v1],g.p[v3],g.val[v1],g.val[v3]);
      tri[0].p[2] = VertexInterp(iso,g.p[v1],g.p[v2],g.val[v1],g.val[v2]);
      ntri++;
      break;
   case 0x0C:
   case 0x03:
      tri[0].p[0] = VertexInterp(iso,g.p[v0],g.p[v3],g.val[v0],g.val[v3]);
      tri[0].p[1] = VertexInterp(iso,g.p[v0],g.p[v2],g.val[v0],g.val[v2]);
      tri[0].p[2] = VertexInterp(iso,g.p[v1],g.p[v3],g.val[v1],g.val[v3]);
      ntri++;
      tri[1].p[0] = tri[0].p[2];
      tri[1].p[1] = VertexInterp(iso,g.p[v1],g.p[v2],g.val[v1],g.val[v2]);
      tri[1].p[2] = tri[0].p[1];
      ntri++;
      break;
   case 0x0B:
   case 0x04:
      tri[0].p[0] = VertexInterp(iso,g.p[v2],g.p[v0],g.val[v2],g.val[v0]);
      tri[0].p[1] = VertexInterp(iso,g.p[v2],g.p[v1],g.val[v2],g.val[v1]);
      tri[0].p[2] = VertexInterp(iso,g.p[v2],g.p[v3],g.val[v2],g.val[v3]);
      ntri++;
      break;
   case 0x0A:
   case 0x05:
      tri[0].p[0] = VertexInterp(iso,g.p[v0],g.p[v1],g.val[v0],g.val[v1]);
      tri[0].p[1] = VertexInterp(iso,g.p[v2],g.p[v3],g.val[v2],g.val[v3]);
      tri[0].p[2] = VertexInterp(iso,g.p[v0],g.p[v3],g.val[v0],g.val[v3]);
      ntri++;
      tri[1].p[0] = tri[0].p[0];
      tri[1].p[1] = VertexInterp(iso,g.p[v1],g.p[v2],g.val[v1],g.val[v2]);
      tri[1].p[2] = tri[0].p[1];
      ntri++;
      break;
   case 0x09:
   case 0x06:
      tri[0].p[0] = VertexInterp(iso,g.p[v0],g.p[v1],g.val[v0],g.val[v1]);
      tri[0].p[1] = VertexInterp(iso,g.p[v1],g.p[v3],g.val[v1],g.val[v3]);
      tri[0].p[2] = VertexInterp(iso,g.p[v2],g.p[v3],g.val[v2],g.val[v3]);
      ntri++;
      tri[1].p[0] = tri[0].p[0];
      tri[1].p[1] = VertexInterp(iso,g.p[v0],g.p[v2],g.val[v0],g.val[v2]);
      tri[1].p[2] = tri[0].p[2];
      ntri++;
      break;
   case 0x07:
   case 0x08:
      tri[0].p[0] = VertexInterp(iso,g.p[v3],g.p[v0],g.val[v3],g.val[v0]);
      tri[0].p[1] = VertexInterp(iso,g.p[v3],g.p[v2],g.val[v3],g.val[v2]);
      tri[0].p[2] = VertexInterp(iso,g.p[v3],g.p[v1],g.val[v3],g.val[v1]);
      ntri++;
      break;
   }

   return(ntri);
}

/*-------------------------------------------------------------------------
   Return the point between two points in the same ratio as
   isolevel is between valp1 and valp2
*/
XYZ VertexInterp(double isolevel,XYZ p1,XYZ p2,double valp1,double valp2)
{
   double mu;
   XYZ p;

   if (ABS(isolevel-valp1) < 0.00001)
      return(p1);
   if (ABS(isolevel-valp2) < 0.00001)
      return(p2);
   if (ABS(valp1-valp2) < 0.00001)
      return(p1);
   mu = (isolevel - valp1) / (valp2 - valp1);
   p.x = p1.x + mu * (p2.x - p1.x);
   p.y = p1.y + mu * (p2.y - p1.y);
   p.z = p1.z + mu * (p2.z - p1.z);

   return(p);
}

/*
	Compare two 3 or 4 vertex polygons
	Perform the following tests 
	If there is nothing interesting about their relationship return 0
	If the polygons are identical, return 1
*/
int PolygonCompare(POLY34 p1,POLY34 p2) 
{
	
	/* See if the polygons are identical */
	if (p1.np == 3 && p2.np == 3) {
		if (VectorEqual(p1.p[0],p2.p[0]) || 
			 VectorEqual(p1.p[0],p2.p[1]) ||
			 VectorEqual(p1.p[0],p2.p[2])) {
      	if (VectorEqual(p1.p[1],p2.p[0]) ||
          	 VectorEqual(p1.p[1],p2.p[1]) ||
          	 VectorEqual(p1.p[1],p2.p[2])) {
         	if (VectorEqual(p1.p[2],p2.p[0]) ||
             	 VectorEqual(p1.p[2],p2.p[1]) ||
             	 VectorEqual(p1.p[2],p2.p[2])) {
					return(1);
				}
			}
      }
	}
   if (p1.np == 4 && p2.np == 4) {
      if (VectorEqual(p1.p[0],p2.p[0]) ||
          VectorEqual(p1.p[0],p2.p[1]) ||
          VectorEqual(p1.p[0],p2.p[2]) ||
			 VectorEqual(p1.p[0],p2.p[3])) {
         if (VectorEqual(p1.p[1],p2.p[0]) ||
             VectorEqual(p1.p[1],p2.p[1]) ||
             VectorEqual(p1.p[1],p2.p[2]) ||
				 VectorEqual(p1.p[1],p2.p[3])) {
            if (VectorEqual(p1.p[2],p2.p[0]) ||
                VectorEqual(p1.p[2],p2.p[1]) ||
                VectorEqual(p1.p[2],p2.p[2]) ||
					 VectorEqual(p1.p[2],p2.p[3])) {
            	if (VectorEqual(p1.p[3],p2.p[0]) ||
                	 VectorEqual(p1.p[3],p2.p[1]) ||
                	 VectorEqual(p1.p[3],p2.p[2]) ||
						 VectorEqual(p1.p[3],p2.p[3])) {
               	return(1);
					}
            }
         }
      }
   }

	return(0);
}

/*
   Create a triangular facet approximation to a sphere
	Unit radius
   Return the number of facets created.
   The number of facets will be (4^iterations) * 8
*/
int CreateNSphere(TRIFACE *f,int iterations)
{
   int i,it;
   double a;
   XYZ p[6] = {{0,0,1},  {0,0,-1},  {-1,-1,0},  {1,-1,0},  {1,1,0}, {-1,1,0}};
   XYZ pa,pb,pc;
   int nt = 0,ntold;

   /* Create the level 0 object */
   a = 1 / sqrt(2.0);
   for (i=0;i<6;i++) {
      p[i].x *= a;
      p[i].y *= a;
   }
   f[0].p[0] = p[0]; f[0].p[1] = p[3]; f[0].p[2] = p[4];
   f[1].p[0] = p[0]; f[1].p[1] = p[4]; f[1].p[2] = p[5];
   f[2].p[0] = p[0]; f[2].p[1] = p[5]; f[2].p[2] = p[2];
   f[3].p[0] = p[0]; f[3].p[1] = p[2]; f[3].p[2] = p[3];
   f[4].p[0] = p[1]; f[4].p[1] = p[4]; f[4].p[2] = p[3];
   f[5].p[0] = p[1]; f[5].p[1] = p[5]; f[5].p[2] = p[4];
   f[6].p[0] = p[1]; f[6].p[1] = p[2]; f[6].p[2] = p[5];
   f[7].p[0] = p[1]; f[7].p[1] = p[3]; f[7].p[2] = p[2];
   nt = 8;

   if (iterations < 1)
      return(nt);

   /* Bisect each edge and move to the surface of a unit sphere */
   for (it=0;it<iterations;it++) {
      ntold = nt;
      for (i=0;i<ntold;i++) {
			pa = MidPoint(f[i].p[0],f[i].p[1]);
			pb = MidPoint(f[i].p[1],f[i].p[2]);
			pc = MidPoint(f[i].p[2],f[i].p[0]);
         Normalise(&pa);
         Normalise(&pb);
         Normalise(&pc);
         f[nt].p[0] = f[i].p[0]; f[nt].p[1] = pa;        f[nt].p[2] = pc; nt++;
         f[nt].p[0] = pa;        f[nt].p[1] = f[i].p[1]; f[nt].p[2] = pb; nt++;
         f[nt].p[0] = pb;        f[nt].p[1] = f[i].p[2]; f[nt].p[2] = pc; nt++;
         f[i].p[0] = pa;
         f[i].p[1] = pb;
         f[i].p[2] = pc;
      }
   }

   return(nt);
}

/* GEOMETRIC OUTPUT -----------------------------------------------------*/

/*
	Write a Radiance facet 
*/
void WriteRadFacet(FILE *fptr,XYZ *p,int n,char *mat)
{
   int i;

   if (n <= 2)
      return;

    fprintf(fptr,"%s polygon p\n",mat);
    fprintf(fptr,"0\n0\n%d",3*n);
    for (i=0;i<n;i++)
       fprintf(fptr,"\t%g %g %g\n",p[i].x,p[i].y,p[i].z);  
}

/*
   Write a DXF header
*/
void WriteDXFHeader(FILE *fptr,char *s)
{
   fprintf(fptr,"999\nDXF from %s\n",s);
   fprintf(fptr,"  0\nSECTION\n");
   fprintf(fptr,"  2\nHEADER\n");
   fprintf(fptr,"  9\n$ACADVER\n");
   fprintf(fptr,"  1\nAC1006\n");
   fprintf(fptr,"  9\n$INSBASE\n");
   fprintf(fptr,"  10\n0.0\n");
   fprintf(fptr,"  20\n0.0\n");
   fprintf(fptr,"  30\n0.0\n");
   fprintf(fptr,"  9\n$EXTMIN\n");
   fprintf(fptr,"  10\n0.0\n");
   fprintf(fptr,"  20\n0.0\n");
   fprintf(fptr,"  9\n$EXTMAX\n");
   fprintf(fptr,"  10\n1000.0\n");
   fprintf(fptr,"  20\n1000.0\n");
   fprintf(fptr,"  0\nENDSEC\n");

	fprintf(fptr,"  0\nSECTION\n");
	fprintf(fptr,"  2\nTABLES\n");
   fprintf(fptr,"  0\nTABLE\n");
   fprintf(fptr,"  2\nLTYPE\n");
   fprintf(fptr,"  70\n1\n");
   fprintf(fptr,"  0\nLTYPE\n");
   fprintf(fptr,"  2\nCONTINUOUS\n");
   fprintf(fptr,"  70\n64\n");
   fprintf(fptr,"  3\nSolid line\n");
   fprintf(fptr,"  72\n65\n");
   fprintf(fptr,"  73\n0\n");
   fprintf(fptr,"  40\n0.000000\n");
   fprintf(fptr,"  0\nENDTAB\n");
   fprintf(fptr,"  0\nTABLE\n");
   fprintf(fptr,"  2\nLAYER\n");
   fprintf(fptr,"  70\n6\n");
   fprintf(fptr,"  0\nLAYER\n");
   fprintf(fptr,"  2\n1\n");
   fprintf(fptr,"  70\n64\n");
   fprintf(fptr,"  62\n7\n");
   fprintf(fptr,"  6\nCONTINUOUS\n");
   fprintf(fptr,"  0\nLAYER\n");
   fprintf(fptr,"  2\n2\n");
   fprintf(fptr,"  70\n64\n");
   fprintf(fptr,"  62\n7\n");
   fprintf(fptr,"  6\nCONTINUOUS\n");
   fprintf(fptr,"  0\nENDTAB\n");
   fprintf(fptr,"  0\nTABLE\n");
   fprintf(fptr,"  2\nSTYLE\n");
   fprintf(fptr,"  70\n0\n");
   fprintf(fptr,"  0\nENDTAB\n");
	fprintf(fptr,"  0\nENDSEC\n"); 

   fprintf(fptr,"  0\nSECTION\n");
	fprintf(fptr,"  2\nBLOCKS\n");
	fprintf(fptr,"  0\nENDSEC\n");

	fprintf(fptr,"  0\nSECTION\n");
   fprintf(fptr,"  2\nENTITIES\n");
}

/*
   Write a DXF footer
*/
void WriteDXFFooter(FILE *fptr)
{
    fprintf(fptr,"  0\nENDSEC\n");
    fprintf(fptr,"  0\nEOF\n");
}

/*
   Write a DXF entity
	If n is 1 then write a point
	If n is 2 then write a line
	If n is 3 or 4 write a 3dface
	Else do nothing
*/
void WriteDXFFacet(FILE *fptr,XYZ *p,int n)
{
	int i;

	switch (n) {
	case 1:
      fprintf(fptr,"  0\nPOINT\n");
      fprintf(fptr,"  8\nLAYER1\n");
      fprintf(fptr,"%3d\n%g\n",10,p[0].x);
      fprintf(fptr,"%3d\n%g\n",20,p[0].y);
      fprintf(fptr,"%3d\n%g\n",30,p[0].z);
		break;
	case 2:
		fprintf(fptr,"  0\nLINE\n");
		fprintf(fptr,"  8\nLAYER1\n");
		for (i=0;i<n;i++) {
         fprintf(fptr,"%3d\n%g\n",10+i,p[i].x);  
         fprintf(fptr,"%3d\n%g\n",20+i,p[i].y);  
         fprintf(fptr,"%3d\n%g\n",30+i,p[i].z);  
		}
		break;
	case 3:
	case 4:
		fprintf(fptr,"  0\n3DFACE\n");
		fprintf(fptr,"  8\nLAYER1\n");
		for (i=0;i<n;i++) {
			fprintf(fptr,"%3d\n%g\n",10+i,p[i].x);	
         fprintf(fptr,"%3d\n%g\n",20+i,p[i].y);  
         fprintf(fptr,"%3d\n%g\n",30+i,p[i].z);  
		}
		if (n == 3) {
         fprintf(fptr,"%3d\n%g\n",13,p[2].x);  
         fprintf(fptr,"%3d\n%g\n",23,p[2].y);  
         fprintf(fptr,"%3d\n%g\n",33,p[2].z);  
		}
		break;
	default:
		break;
	}
}

/* SYSTEM SPECIFIC  --------------------------------------------------*/

/*
	Time scale at microsecond resolution but returned as seconds
*/
double GetRunTime(void)
{
#ifndef WIN32
	double sec = 0;
	struct timeval tp;

   gettimeofday(&tp,NULL);
   sec = tp.tv_sec + tp.tv_usec / 1000000.0;
#else
	DWORD systime;
	double sec = 0;
	
	systime = timeGetTime();
	sec  = systime/1000.0;
#endif

	return(sec);
}

#ifndef WIN32
/*
	Set keyboard handling to be canonical or not
*/
void SetKeyboardIO(int on)
{
	static struct termios stored_settings;
   struct termios new_settings;

	if (on) {
   	tcgetattr(0,&stored_settings);
   	new_settings = stored_settings;

   	/* Disable canonical mode, and set buffer size to 1 byte */
   	new_settings.c_lflag &= (~ICANON);
   	new_settings.c_cc[VTIME] = 0;
   	new_settings.c_cc[VMIN] = 1;

   	tcsetattr(0,TCSANOW,&new_settings);
	} else {
		tcsetattr(0,TCSANOW,&stored_settings);
	}
}
#endif

/*
   Calculate the logarith with an arbitrary base
*/
double Logarithm(double n,double base)
{
   return(log(n) / log(base));
}

/*
   Return n1 to the power of n2
*/
long PowerInt(long n1,long n2)
{
   long i;
   long answer = 1;

   for (i=1;i<=n2;i++)
      answer *= n1;
   return(answer);
}

/*
   Integer square root
*/
long IntSqrt(long l)
{
  long i,j=0,k;

  for (i=0x4000;i!=0;i>>=1) {
    k = (j + i) * (j + i);
    if (k <= l)
      j += i;
    if (k == l)
      break;
  }

  return(j);
}

/*
   Iterative definition of factorial
*/
double Factorial(int n)
{
   int i;
   double factor = 1;

   for (i=2;i<=n;i++)
      factor *= i;

   return(factor);
}

/*
   Series expansion for the moddified Bessel function of the 0 order
   This is correct to 16 decimal places
*/
double BesselI0(double x)
{
   double sum,xx,x2;

   sum = 1;
   x2 = x * x;
   xx = x2;

   sum += xx / 4.0;
   xx  *= x2;
   sum += xx / 64.0;
   xx *= x2;
   sum += xx / 2304.0;
   xx *= x2;
   sum += xx / 147456.0;
   xx *= x2;
   sum += xx / 14745600.0;
   xx *= x2;
   sum += xx / 2123366400.0;
   xx *= x2;
   sum += xx / 416179814400.0;
   xx *= x2;
   sum += xx / 106542032486400.0;

   return(sum);
}

double Bessel(double x,int n)
{
   double v,theta,dtheta,sum = 0;

   dtheta = PI / 10000;
   for (theta=0;theta<PI;theta+=dtheta) {
      v = cos(x * sin(theta) - n*theta);
      sum += v * dtheta;
   }

   return(sum/PI);
}

/* 
	n'th zero of the m'th bessel function 
*/
double BesselZero(int n,int m)
{
   int sign1,sign2;
   double x=0,y,ncount=0;
/*
double zeros[9][11] = {
   {-1.0,2.406, 5.520, 8.655,11.792,14.932,18.071,21.213,24.352,27.495,30.634},
   {-1.0,0.000, 3.832, 7.016,10.174,13.324,16.471,19.616,22.761,25.904,29.047},
   {-1.0,0.000, 5.136, 8.417,11.621,14.796,17.961,21.117,24.271,27.420,30.570},
   {-1.0,0.000, 6.381, 9.762,13.016,16.224,19.410,22.583,25.749,28.909,32.065},
   {-1.0,0.000, 7.589,11.065,14.374,17.616,20.828,24.019,27.200,30.371,33.538},
   {-1.0,0.000, 8.772,12.339,15.701,18.981,22.218,25.431,28.627,31.812,34.989},
   {-1.0,0.000, 9.937,13.589,17.005,20.321,23.587,26.820,30.035,33.233,36.423},
   {-1.0,0.000,11.087,14.822,18.288,21.642,24.935,28.192,31.423,34.638,37.839},
   {-1.0,0.000,12.226,16.038,19.556,22.945,26.268,29.545,32.797,36.025,39.242},
}; */

   if (n <= 0)
      return(0.0);

   y = Bessel(x,m);
   if (ABS(y) < 0.001) {
      if (n == 1)
         return(0.0);
      ncount++;
      x += 0.001;
      sign1 = SIGN(Bessel(x,m));
   } else {
      sign1 = SIGN(x);
   }
   x += 0.001;
   for (;x<100;x+=0.001) {
      y = SIGN(Bessel(x,m));
      sign2 = SIGN(y);
      if (sign2 != sign1) {
         sign1 = sign2;
         ncount++;
         if (ncount >= n)
            return(x);
      }
   }
   return(-1.0);
}

/*
	Natural log of the gamma function
	Derived from "Numerical Receipes in C"
	xx > 0
*/
double LnGamma(double xx)
{
	int j;
   double x,y,tmp,ser;
   double cof[6] = {
		76.18009172947146,    -86.50532032941677,
      24.01409824083091,    -1.231739572450155,
      0.1208650973866179e-2,-0.5395239384953e-5
	};

   y = x = xx;
   tmp = x + 5.5 - (x + 0.5) * log(x + 5.5);
   ser = 1.000000000190015;
   for (j=0;j<=5;j++) 
		ser += (cof[j] / ++y);
   return(log(2.5066282746310005 * ser / x) - tmp);
}

/* 2 x 2 matrices ----------------------------------------------- */

/*
	Normal mathematical convention is a_row_column
   | a1  b1 |      | a_0_0  a_0_1 |
   |        |  or  |              |
   | a2  b2 |      | a_1_0  a_1_1 |
*/
double Determinant22(double a[2][2])
{
	return(a[0][0]*a[1][1] - a[0][1]*a[1][0]);
}

double Det2x2(double a1,double b1,double a2,double b2)
{
  return(a1*b2 - b1*a2);
}

int Inverse22(double m[2][2],double i[2][2])
{
   double det;

   det = Determinant22(m);
   if (ABS(det) <= EPSILON)
      return(FALSE);

   i[0][0] =   m[1][1] / det;
   i[0][1] = - m[0][1] / det;
   i[1][0] = - m[1][0] / det;
   i[1][1] =   m[0][0] / det;
   return(TRUE);
}

/* 3 x 3 matrices ----------------------------------------------- */

/*
	| a1  b1  c1 |      | a_0_0  a_0_1  a_0_2 |
	|            |      |                     |
	| a2  b2  c2 |  or  | a_1_0  a_1_1  a_1_2 |
	|            |      |                     |
	| a3  b3  c3 |      | a_2_0  a_2_1  a_2_2 |
*/
double Determinant33(double a[3][3])
{
	return(a[0][0]*(a[1][1]*a[2][2] - a[1][2]*a[2][1]) +
          a[0][1]*(a[1][0]*a[2][2] - a[1][2]*a[2][0]) +
          a[0][2]*(a[1][0]*a[2][1] - a[2][0]*a[1][1]));
}

double Det3x3(
	double a1,double a2,double a3,
	double b1,double b2,double b3,
	double c1,double c2,double c3)
{
  return( a1 * Det2x2(b2,b3,c2,c3)
        - b1 * Det2x2(a2,a3,c2,c3)
        + c1 * Det2x2(a2,a3,b2,b3));
}

int Inverse33(double a[3][3],double b[3][3])
{
   double det;

   det = Determinant33(a);
   if (ABS(det) <= EPSILON)
      return(FALSE);

   b[0][0] =   (a[1][1]*a[2][2] - a[2][1]*a[1][2]) / det;
   b[0][1] = - (a[0][1]*a[2][2] - a[2][1]*a[0][2]) / det;
   b[0][2] =   (a[0][1]*a[1][2] - a[1][1]*a[0][2]) / det;

   b[1][0] = - (a[1][0]*a[2][2] - a[2][0]*a[1][2]) / det;
   b[1][1] =   (a[0][0]*a[2][2] - a[2][0]*a[0][2]) / det;
   b[1][2] = - (a[0][0]*a[1][2] - a[1][0]*a[0][2]) / det;

   b[2][0] =   (a[1][0]*a[2][1] - a[2][0]*a[1][1]) / det;
   b[2][1] = - (a[0][0]*a[2][1] - a[2][0]*a[0][1]) / det;
   b[2][2] =   (a[0][0]*a[1][1] - a[1][0]*a[0][1]) / det;
	
   return(TRUE);
}

/* 4 x 4 matrices ----------------------------------------------- */ 

/*
   | a1  b1  c1  d1 |      | a_0_0  a_0_1  a_0_2  a_0_3 |
   |                |      |                            |
   | a2  b2  c2  d2 |  or  | a_1_0  a_1_1  a_1_2  a_1_3 |
   |                |      |                            |
   | a3  b3  c3  d3 |      | a_2_0  a_2_1  a_2_2  a_2_3 |
   |                |      |                            |
   | a4  b4  c4  d4 |      | a_3_0  a_3_1  a_3_2  a_3_3 |
*/
double Determinant44(double a[4][4])
{
   double a1,a2,a3,a4,b1,b2,b3,b4,c1,c2,c3,c4,d1,d2,d3,d4;

   a1 = a[0][0]; b1 = a[0][1]; c1 = a[0][2]; d1 = a[0][3];
   a2 = a[1][0]; b2 = a[1][1]; c2 = a[1][2]; d2 = a[1][3];
   a3 = a[2][0]; b3 = a[2][1]; c3 = a[2][2]; d3 = a[2][3];
   a4 = a[3][0]; b4 = a[3][1]; c4 = a[3][2]; d4 = a[3][3];

   return(a1 * Det3x3(b2,b3,b4,c2,c3,c4,d2,d3,d4)
        - b1 * Det3x3(a2,a3,a4,c2,c3,c4,d2,d3,d4)
        + c1 * Det3x3(a2,a3,a4,b2,b3,b4,d2,d3,d4)
        - d1 * Det3x3(a2,a3,a4,b2,b3,b4,c2,c3,c4));
}

/*
   Calculate the adjoint of a 4x4 matrix
   Let  a_i_j  denote the minor determinant of matrix A obtained by
   deleting the ith row and jth column from A.
   Let b_i_j = (-1)^(i+j) a_j_i
   The matrix B = (b_i_j) is the adjoint of A
*/
void Adjoint44(double a[4][4],double b[4][4])
{
  double a1,a2,a3,a4,b1,b2,b3,b4;
  double c1,c2,c3,c4,d1,d2,d3,d4;

  a1 = a[0][0]; b1 = a[0][1]; c1 = a[0][2]; d1 = a[0][3];
  a2 = a[1][0]; b2 = a[1][1]; c2 = a[1][2]; d2 = a[1][3];
  a3 = a[2][0]; b3 = a[2][1]; c3 = a[2][2]; d3 = a[2][3];
  a4 = a[3][0]; b4 = a[3][1]; c4 = a[3][2]; d4 = a[3][3];

  b[0][0] =   Det3x3(b2,b3,b4,c2,c3,c4,d2,d3,d4);
  b[1][0] = - Det3x3(a2,a3,a4,c2,c3,c4,d2,d3,d4);
  b[2][0] =   Det3x3(a2,a3,a4,b2,b3,b4,d2,d3,d4);
  b[3][0] = - Det3x3(a2,a3,a4,b2,b3,b4,c2,c3,c4);

  b[0][1] = - Det3x3(b1,b3,b4,c1,c3,c4,d1,d3,d4);
  b[1][1] =   Det3x3(a1,a3,a4,c1,c3,c4,d1,d3,d4);
  b[2][1] = - Det3x3(a1,a3,a4,b1,b3,b4,d1,d3,d4);
  b[3][1] =   Det3x3(a1,a3,a4,b1,b3,b4,c1,c3,c4);

  b[0][2] =   Det3x3(b1,b2,b4,c1,c2,c4,d1,d2,d4);
  b[1][2] = - Det3x3(a1,a2,a4,c1,c2,c4,d1,d2,d4);
  b[2][2] =   Det3x3(a1,a2,a4,b1,b2,b4,d1,d2,d4);
  b[3][2] = - Det3x3(a1,a2,a4,b1,b2,b4,c1,c2,c4);

  b[0][3] = - Det3x3(b1,b2,b3,c1,c2,c3,d1,d2,d3);
  b[1][3] =   Det3x3(a1,a2,a3,c1,c2,c3,d1,d2,d3);
  b[2][3] = - Det3x3(a1,a2,a3,b1,b2,b3,d1,d2,d3);
  b[3][3] =   Det3x3(a1,a2,a3,b1,b2,b3,c1,c2,c3);
}

/*
	Calculate the inverse of a 4x4 matrix
    -1     1
   A  = ------- adjoint A
         det A
*/
int Inverse44(double a[4][4],double b[4][4])
{
   int i,j;
   double det;

   Adjoint44(a,b);
   det = Determinant44(a);

   if (ABS(det) < EPSILON) /* Singular */
		return(FALSE);

   for (i=0;i<4;i++)
      for (j=0;j<4;j++)
         b[i][j] = b[i][j] / det;

   return(TRUE);
}

void Identity44(double a[4][4])
{
   int i,j;

   for (i=0;i<4;i++)
      for (j=0;j<4;j++)
         a[i][j] = (double) (i == j);
}

/*
	c = a * b
*/
void Multiply44(double a[4][4],double b[4][4], double c[4][4])
{
	int i,j,k;

	for (i=0;i<4;i++) {
		for (j=0;j<4;j++) {
			c[i][j] = 0;
			for (k=0;k<4;k++) {
				c[i][j] += a[i][k] * b[k][j];
			}
		}
	}
}

/*
   Recursive definition of determinate using expansion by minors.
*/
double Determinant(double **a,int n)
{
   int i,j,j1,j2;
   double det = 0;
   double **m = NULL;

   if (n < 1) { /* Error */

   } else if (n == 1) { /* Shouldn't get used */
      det = a[0][0];
   } else if (n == 2) {
      det = a[0][0] * a[1][1] - a[1][0] * a[0][1];
   } else {
      det = 0;
      for (j1=0;j1<n;j1++) {
         m = (double **)malloc((n-1)*sizeof(double *));
         for (i=0;i<n-1;i++)
            m[i] = (double *)malloc((n-1)*sizeof(double));
         for (i=1;i<n;i++) {
            j2 = 0;
            for (j=0;j<n;j++) {
               if (j == j1)
                  continue;
               m[i-1][j2] = a[i][j];
               j2++;
            }
         }
         det += pow(-1.0,j1+2.0) * a[0][j1] * Determinant(m,n-1);
         for (i=0;i<n-1;i++)
            free(m[i]);
         free(m);
      }
   }
   return(det);
}

/*
   Find the cofactor matrix of a square matrix
*/
void CoFactor(double **a,int n,double **b)
{
   int i,j,ii,jj,i1,j1;
   double det;
   double **c;

   c = (double **)malloc((n-1)*sizeof(double *));
   for (i=0;i<n-1;i++)
     c[i] = (double *)malloc((n-1)*sizeof(double));

   for (j=0;j<n;j++) {
      for (i=0;i<n;i++) {

         /* Form the adjoint a_ij */
         i1 = 0;
         for (ii=0;ii<n;ii++) {
            if (ii == i)
               continue;
            j1 = 0;
            for (jj=0;jj<n;jj++) {
               if (jj == j)
                  continue;
               c[i1][j1] = a[ii][jj];
               j1++;
            }
            i1++;
         }

         /* Calculate the determinate */
         det = Determinant(c,n-1);

         /* Fill in the elements of the cofactor */
         b[i][j] = pow(-1.0,i+j+2.0) * det;
      }
   }

   for (i=0;i<n-1;i++)
      free(c[i]);
   free(c);
}

/*
   Transpose of a square matrix, do it in place
*/
void Transpose(double **a,int n)
{
   int i,j;
   double tmp;

   for (i=1;i<n;i++) {
      for (j=0;j<i;j++) {
         tmp = a[i][j];
         a[i][j] = a[j][i];
         a[j][i] = tmp;
      }
   }
}

/*
   Multiply two square matrices
*/
void MatrixMul(double **a,double **b,double **c,int n)
{
   int i,j,k;

   for (i=0;i<n;i++) {
      for (j=0;j<n;j++) {
         c[i][j] = 0;
         for (k=0;k<n;k++) {
            c[i][j] += a[k][j] * b[i][k];
         }
      }
   }
}

void WriteMatrix(FILE *fptr,int format,double **a,int n)
{
   int i,j;

   switch (format) {
   case 0: /* Plain text */
      for (i=0;i<n;i++) {
         for (j=0;j<n;j++) {
            fprintf(fptr,"%10.4f",a[i][j]);
         }
         fprintf(fptr,"\n");
      }
      break;
   case 1: /* HTML */
      fprintf(fptr,"<table cellpadding=0 cellspacing=0 border=0><tr>\n");
      fprintf(fptr,"<td valign=\"center\">A = </td>\n");
      fprintf(fptr,"<td valign=\"center\">");
      fprintf(fptr,"<img src=\"lbracket.gif\" width=3 height=%d></td>\n",n*13);
      fprintf(fptr,"<td valign=\"center\">\n");
      fprintf(fptr,"<table cellpadding=0 cellspacing=0 border=0>\n");
      for (i=0;i<n;i++) {
         fprintf(fptr,"<tr>\n");
         for (j=0;j<n;j++) {
            fprintf(fptr,"<td height=10><center>");
            fprintf(fptr,"&nbsp;&nbsp;%g&nbsp;&nbsp;",a[i][j]);
            fprintf(fptr,"</center></td>\n");
         }
         fprintf(fptr,"</tr>\n");
      }
      fprintf(fptr,"</table></td>\n");
      fprintf(fptr,"<td valign=\"center\">");
      fprintf(fptr,"<img src=\"rbracket.gif\" width=3 height=%d></td>\n",n*15);
      fprintf(fptr,"</tr></table>\n");
      break;
   }
}

/*
   This Random Number Generator is based on the algorithm in a FORTRAN
   version published by George Marsaglia and Arif Zaman, Florida State
   University; ref.: see original comments below.
   At the fhw (Fachhochschule Wiesbaden, W.Germany), Dept. of Computer
   Science, we have written sources in further languages (C, Modula-2
   Turbo-Pascal(3.0, 5.0), Basic and Ada) to get exactly the same test
   results compared with the original FORTRAN version.
   April 1989
   Karl-L. Noell <NOELL@DWIFH1.BITNET>
      and  Helmut  Weber <WEBER@DWIFH1.BITNET>

   This random number generator originally appeared in "Toward a Universal
   Random Number Generator" by George Marsaglia and Arif Zaman.
   Florida State University Report: FSU-SCRI-87-50 (1987)
   It was later modified by F. James and published in "A Review of Pseudo-
   random Number Generators"
   THIS IS THE BEST KNOWN RANDOM NUMBER GENERATOR AVAILABLE.
   (However, a newly discovered technique can yield
   a period of 10^600. But that is still in the development stage.)
   It passes ALL of the tests for random number generators and has a period
   of 2^144, is completely portable (gives bit identical results on all
   machines with at least 24-bit mantissas in the floating point
   representation).
   The algorithm is a combination of a Fibonacci sequence (with lags of 97
   and 33, and operation "subtraction plus one, modulo one") and an
   "arithmetic sequence" (using subtraction).

   Use IJ = 1802 & KL = 9373 to test the random number generator. The
   subroutine RANMAR should be used to generate 20000 random numbers.
   Then display the next six random numbers generated multiplied by 4096*4096
   If the random number generator is working properly, the random numbers
   should be:
           6533892.0  14220222.0  7275067.0
           6172232.0  8354498.0   10633180.0
*/

/* Globals */
double u[97],c,cd,cm;
int i97,j97;
int test = FALSE;

/*
   This is the initialization routine for the random number generator.
   NOTE: The seed variables can have values between:    0 <= IJ <= 31328
                                                        0 <= KL <= 30081
   The random number sequences created by these two seeds are of sufficient
   length to complete an entire calculation with. For example, if sveral
   different groups are working on different parts of the same calculation,
   each group could be assigned its own IJ seed. This would leave each group
   with 30000 choices for the second seed. That is to say, this random
   number generator can create 900 million different subsequences -- with
   each subsequence having a length of approximately 10^30.
*/
void RandomInitialise(int ij,int kl)
{
   double s,t;
   int ii,i,j,k,l,jj,m;

   /*
      Handle the seed range errors
         First random number seed must be between 0 and 31328
         Second seed must have a value between 0 and 30081
   */
   if (ij < 0 || ij > 31328 || kl < 0 || kl > 30081) {
		ij = 1802;
		kl = 9373;
   }

   i = (ij / 177) % 177 + 2;
   j = (ij % 177)       + 2;
   k = (kl / 169) % 178 + 1;
   l = (kl % 169);

   for (ii=0; ii<97; ii++) {
      s = 0.0;
      t = 0.5;
      for (jj=0; jj<24; jj++) {
         m = (((i * j) % 179) * k) % 179;
         i = j;
         j = k;
         k = m;
         l = (53 * l + 1) % 169;
         if (((l * m % 64)) >= 32)
            s += t;
         t *= 0.5;
      }
      u[ii] = s;
   }

   c    = 362436.0 / 16777216.0;
   cd   = 7654321.0 / 16777216.0;
   cm   = 16777213.0 / 16777216.0;
   i97  = 97;
   j97  = 33;
   test = TRUE;
}

/* 
   This is the random number generator proposed by George Marsaglia in
   Florida State University Report: FSU-SCRI-87-50
*/
double RandomUniform(void)
{
   double uni;

   /* Make sure the initialisation routine has been called */
   if (!test) 
   	RandomInitialise(1802,9373);

   uni = u[i97-1] - u[j97-1];
   if (uni <= 0.0)
      uni++;
   u[i97-1] = uni;
   i97--;
   if (i97 == 0)
      i97 = 97;
   j97--;
   if (j97 == 0)
      j97 = 97;
   c -= cd;
   if (c < 0.0)
      c += cm;
   uni -= c;
   if (uni < 0.0)
      uni++;

   return(uni);
}

/*
  ALGORITHM 712, COLLECTED ALGORITHMS FROM ACM.
  THIS WORK PUBLISHED IN TRANSACTIONS ON MATHEMATICAL SOFTWARE,
  VOL. 18, NO. 4, DECEMBER, 1992, PP. 434-435.
  The function returns a normally distributed pseudo-random number
  with a given mean and standard devaiation.  Calls are made to a
  function subprogram which must return independent random
  numbers uniform in the interval (0,1).
  The algorithm uses the ratio of uniforms method of A.J. Kinderman
  and J.F. Monahan augmented with quadratic bounding curves.
*/
double RandomGaussian(double mean,double stddev)
{
   double  q,u,v,x,y;

	/*  
		Generate P = (u,v) uniform in rect. enclosing acceptance region 
      Make sure that any random numbers <= 0 are rejected, since
      gaussian() requires uniforms > 0, but RandomUniform() delivers >= 0.
	*/
   do {
      u = RandomUniform();
      v = RandomUniform();
   	if (u <= 0.0 || v <= 0.0) {
       	u = 1.0;
       	v = 1.0;
   	}
      v = 1.7156 * (v - 0.5);

      /*  Evaluate the quadratic form */
      x = u - 0.449871;
   	y = ABS(v) + 0.386595;
      q = x * x + y * (0.19600 * y - 0.25472 * x);

      /* Accept P if inside inner ellipse */
      if (q < 0.27597)
			break;

      /*  Reject P if outside outer ellipse, or outside acceptance region */
    } while ((q > 0.27846) || (v * v > -4.0 * log(u) * u * u));

    /*  Return ratio of P's coordinates as the normal deviate */
    return (mean + stddev * v / u);
}

/*
   Return random integer within a range, lower -> upper INCLUSIVE
*/
int RandomInt(int lower,int upper)
{
   return((int)(RandomUniform() * (upper - lower + 1)) + lower);
}

/*
   Return random float within a range, lower -> upper
*/
double RandomDouble(double lower,double upper)
{
   return((upper - lower) * RandomUniform() + lower);
}

/*
	Crapy little random number generator
	Returns a floating point random number on -1 to 1, given an x
*/
double PrimeNoise1(int x)
{
	int i,xx;
	double y;

	xx = x;
	x = (xx << 13);
	for (i=0;i<xx;i++)
		x *= xx;
	y = 1 - ((x*(x*x*15731+789221)+1376312589) & 0x7fffffff) / 1073741824.0;
	return(y);
}

/*
   ForwardRandomUniform()
   Skip ahead in the random sequence.
   by Stan Reckard
*/
void ForwardRandomUniform(long forward)
{
   double uni;

   /* Make sure the initialisation routine has been called */
   if (!test)
      RandomInitialise(1802,9373);

   while (forward--) {
       uni = u[i97-1] - u[j97-1];
       if (uni <= 0.0)
          uni++;
       u[i97-1] = uni;
       i97--;
       if (i97 == 0)
          i97 = 97;
       j97--;
       if (j97 == 0)
          j97 = 97;
       c -= cd;
       if (c < 0.0)
          c += cm;
   }
}

/*
   BackupRandomUniform()
   Backup in the random sequence 'backup' times.
   by Stan Reckard
*/
void BackupRandomUniform(long backup) 
{
   double uni, uniAlt, prev;

   while (backup--) {
      if (c >= cm)
          c -= cm;

      c += cd;

      if (j97 == 97)
         j97 = 0;
      j97++;

      if (i97 == 97)
         i97 = 0;
      i97++;

      uni = u[i97-1];
      uniAlt = uni - 1;

      prev = uni + u[j97-1];
      if ((prev > 0.0F) && (prev < 1.0F))
         u[i97-1] = prev;
      else
         u[i97-1] = uniAlt + u[j97-1];;
   }
}

/*
   UnRandomUniform()
   Backup in the random sequence.
   by Stan Reckard
*/
double UnRandomUniform(void) 
{
   double uni, uniAlt, prev;
   double cTmp;

   if (c >= cm)
      c -= cm;

   c += cd;

   if (j97 == 97)
      j97 = 0;
   j97++;

   if (i97 == 97)
      i97 = 0;
   i97++;

   uni = u[i97-1];
   uniAlt = uni - 1;

   prev = uni + u[j97-1];
   if ((prev > 0.0F) && (prev < 1.0F))
      u[i97-1] = prev;
   else {
      u[i97-1] = uniAlt + u[j97-1];
   }
   /* RandomUniform() has been completely undone at this point.  */

   /*
      Now get the random# that was last retrieved to
                return without altering the random sequence.
      uni holds old value of u[i97-1]
   */
   cTmp = c;
   cTmp -= cd;
   if (cTmp < 0.0F)
      cTmp += cm;
   uni -= cTmp;
   if (uni < 0.0F)  uni++;

   return uni;  /* prev random# returned */
}


/* rand24()    24-bit precision   */
unsigned int rand24(void) 
{
   return (unsigned int)(RandomUniform() * 4096 * 4096);
}

/* unRand24()    24-bit precision   */
unsigned int unRand24(void) 
{
   return (unsigned int)(UnRandomUniform() * 4096 * 4096);
}

QUATERNION Qadd(QUATERNION q1,QUATERNION q2)
{
	QUATERNION qans = {0,0,0,0};

	qans.r = q1.r + q2.r;
   qans.a = q1.a + q2.a;
   qans.b = q1.b + q2.b;
   qans.c = q1.c + q2.c;

	return(qans);
}

QUATERNION Qsub(QUATERNION q1,QUATERNION q2)
{
   QUATERNION qans = {0,0,0,0};

   qans.r = q1.r - q2.r;
   qans.a = q1.a - q2.a;
   qans.b = q1.b - q2.b;
   qans.c = q1.c - q2.c;

   return(qans);
}

QUATERNION Qmult(QUATERNION q1,QUATERNION q2)
{
   QUATERNION qans = {0,0,0,0};

   qans.r = q1.r*q2.r - q1.a*q2.a - q1.b*q2.b - q1.c*q2.c;
   qans.a = q1.r*q2.a + q1.a*q2.r + q1.b*q2.c - q1.c*q2.b;
   qans.b = q1.r*q2.b + q1.b*q2.r + q1.c*q2.a - q1.a*q2.c;
   qans.c = q1.r*q2.c + q1.c*q2.r + q1.a*q2.b - q1.b*q2.a;

   return(qans);
}

QUATERNION Qexp(QUATERNION q)
{
   QUATERNION qans = {0,0,0,0};
	double m,er,sm;
	
	m = sqrt(q.a*q.a + q.b*q.b + q.c*q.c);
	er = exp(q.r);

	if (m == 0) {
		qans.r = er;
		return(qans);
	}

	sm = sin(m);
	qans.r = er * cos(m);
	qans.a = er * q.a * sm / m;
	qans.b = er * q.b * sm / m;
	qans.c = er * q.c * sm / m;

   return(qans);
}

QUATERNION Qdiv(QUATERNION q1,QUATERNION q2)
{
   QUATERNION qans = {0,0,0,0},q3;
	double len2;

   len2 = q2.r*q2.r + q2.a*q2.a + q2.b*q2.b + q2.c*q2.c;
   if (len2 > 0) {
		q3 = Qmult(q1,q2);
   	qans.r = 2 * q2.r * q1.r - q3.r;
   	qans.a = 2 * q2.r * q1.a - q3.a;  
   	qans.b = 2 * q2.r * q1.b - q3.b;  
   	qans.c = 2 * q2.r * q1.c - q3.c;  
		qans.r /= len2;
      qans.a /= len2;
      qans.b /= len2;
      qans.c /= len2;
	}

   return(qans);
}

QUATERNION Qinv(QUATERNION q)
{
	QUATERNION qans = {0,0,0,0};
	double len2;

	len2 = q.r*q.r + q.a*q.a + q.b*q.b + q.c*q.c;
	if (len2 > 0) {
		qans.r =  q.r / len2;
   	qans.a = -q.a / len2;
   	qans.b = -q.b / len2;
   	qans.c = -q.c / len2;
	}

	return(qans);
}

QUATERNION Qconj(QUATERNION q)
{
	QUATERNION qans = {0,0,0,0};

	qans.r =  q.r;
	qans.a = -q.a;
	qans.b = -q.b;
	qans.c = -q.c;

	return(qans);
}

double Qmod(QUATERNION q)
{
	return(sqrt(q.r*q.r + q.a*q.a + q.b*q.b + q.c*q.c));
}

/*-------------------------------------------------------------------------
   This computes an in-place complex-to-complex FFT
   x and y are the real and imaginary arrays of 2^m points.
   dir =  1 gives forward transform
   dir = -1 gives reverse transform

     Formula: forward
                  N-1
                  ---
              1   \          - j k 2 pi n / N
      X(n) = ---   >   x(k) e                    = forward transform
              N   /                                n=0..N-1
                  ---
                  k=0

      Formula: reverse
                  N-1
                  ---
                  \          j k 2 pi n / N
      X(n) =       >   x(k) e                    = forward transform
                  /                                n=0..N-1
                  ---
                  k=0
*/
int FFT(int dir,int m,double *x,double *y)
{
   long nn,i,i1,j,k,i2,l,l1,l2;
   double c1,c2,tx,ty,t1,t2,u1,u2,z;

   /* Calculate the number of points 
   nn = 1;
   for (i=0;i<m;i++)
      nn *= 2;
	*/
	nn = 1 << m;

   /* Do the bit reversal */
   i2 = nn >> 1;
   j = 0;
   for (i=0;i<nn-1;i++) {
      if (i < j) {
         tx = x[i];
         ty = y[i];
         x[i] = x[j];
         y[i] = y[j];
         x[j] = tx;
         y[j] = ty;
      }
      k = i2;
      while (k <= j) {
         j -= k;
         k >>= 1;
      }
      j += k;
   }

   /* Compute the FFT */
   c1 = -1.0;
   c2 = 0.0;
   l2 = 1;
   for (l=0;l<m;l++) {
      l1 = l2;
      l2 <<= 1;
      u1 = 1.0;
      u2 = 0.0;
      for (j=0;j<l1;j++) {
         for (i=j;i<nn;i+=l2) {
            i1 = i + l1;
            t1 = u1 * x[i1] - u2 * y[i1];
            t2 = u1 * y[i1] + u2 * x[i1];
            x[i1] = x[i] - t1;
            y[i1] = y[i] - t2;
            x[i] += t1;
            y[i] += t2;
         }
         z =  u1 * c1 - u2 * c2;
         u2 = u1 * c2 + u2 * c1;
         u1 = z;
      }
      c2 = sqrt((1.0 - c1) / 2.0);
      if (dir == 1)
         c2 = -c2;
      c1 = sqrt((1.0 + c1) / 2.0);
   }

   /* Scaling for forward transform */
   if (dir == 1) {
      for (i=0;i<nn;i++) {
         x[i] /= (double)nn;
         y[i] /= (double)nn;
      }
   }

   return(TRUE);
}

/*-------------------------------------------------------------------------
   Perform a 2D FFT inplace given a complex 2D array
   The direction dir, 1 for forward, -1 for reverse
   The size of the array (nx,ny)
   Return false if there are memory problems or
      the dimensions are not powers of 2
*/
int FFT2D(COMPLEX **c,int nx,int ny,int dir)
{
   int i,j;
   int m,twopm;
   double *real,*imag;

   /* Transform the rows */
   real = (double *)malloc(nx * sizeof(double));
   imag = (double *)malloc(nx * sizeof(double));
   if (real == NULL || imag == NULL)
      return(FALSE);
   if (!Powerof2(nx,&m,&twopm) || twopm != nx)
      return(FALSE);
   for (j=0;j<ny;j++) {
      for (i=0;i<nx;i++) {
         real[i] = c[i][j].real;
         imag[i] = c[i][j].imag;
      }
      FFT(dir,m,real,imag);
      for (i=0;i<nx;i++) {
         c[i][j].real = real[i];
         c[i][j].imag = imag[i];
      }
   }
   free(real);
   free(imag);

   /* Transform the columns */
   real = (double *)malloc(ny * sizeof(double));
   imag = (double *)malloc(ny * sizeof(double));
   if (real == NULL || imag == NULL)
      return(FALSE);
   if (!Powerof2(ny,&m,&twopm) || twopm != ny)
      return(FALSE);
   for (i=0;i<nx;i++) {
      for (j=0;j<ny;j++) {
         real[j] = c[i][j].real;
         imag[j] = c[i][j].imag;
      }
      FFT(dir,m,real,imag);
      for (j=0;j<ny;j++) {
         c[i][j].real = real[j];
         c[i][j].imag = imag[j];
      }
   }
   free(real);
   free(imag);

   return(TRUE);
}

/*-------------------------------------------------------------------------
        Direct fourier transform
*/
int DFT(int dir,int m,double *x1,double *y1)
{
   long i,k;
   double arg;
   double cosarg,sinarg;
	double *x2=NULL,*y2=NULL;

	x2 = (double *)malloc(m*sizeof(double));
   y2 = (double *)malloc(m*sizeof(double));
	if (x2 == NULL || y2 == NULL)
		return(FALSE);

   for (i=0;i<m;i++) {
      x2[i] = 0;
      y2[i] = 0;
      arg = - dir * 2.0 * 3.141592654 * (double)i / (double)m;
      for (k=0;k<m;k++) {
         cosarg = cos(k * arg);
         sinarg = sin(k * arg);
         x2[i] += (x1[k] * cosarg - y1[k] * sinarg);
         y2[i] += (x1[k] * sinarg + y1[k] * cosarg);
      }
   }

   /* Copy the data back */
   if (dir == 1) {
      for (i=0;i<m;i++) {
         x1[i] = x2[i] / (double)m;
         y1[i] = y2[i] / (double)m;
      }
   } else {
      for (i=0;i<m;i++) {
         x1[i] = x2[i];
         y1[i] = y2[i];
      }
   }

	free(x2);
	free(y2);
   return(TRUE);
}

/*-------------------------------------------------------------------------
	Calculate the closest but lower power of two of a number
	twopm = 2**m <= n
	Return TRUE if 2**m == n
*/
int Powerof2(int n,int *m,int *twopm)
{
	if (n <= 1) {
		*m = 0;
		*twopm = 1;
		return(FALSE);
	}

   *m = 1;
   *twopm = 2;
   do {
      (*m)++;
      (*twopm) *= 2;
   } while (2*(*twopm) <= n);

   if (*twopm != n) 
		return(FALSE);
	else
		return(TRUE);
}

/*-------------------------------------------------------------------------
	Calculate the Pearsons cross correlation series for all delays
	Input arrays are s1 and s2
	Number of points in each array is n
	The output correlation sequence at delays of -n/2 to n/2 is sout
	The zero lag correlation coeficient is at index n/2
	The series is assumed to be 0 for indexes below 0 and above n-1
*/
void Correlate(double *s1,double *s2,int n,double *sout)
{
	int i,j,delay;
	double ms1=0,ms2=0,ss1=0,ss2=0,denom,ss1s2;

	/* Calculate the means */
	for (i=0;i<n;i++) {
		ms1 += s1[i];
		ms2 += s2[i];
	}
	ms1 /= n;
	ms2 /= n;

	/* Calculate the variances */
	for (i=0;i<n;i++) {
		ss1 += (s1[i] - ms1) * (s1[i] - ms1);
		ss2 += (s2[i] - ms2) * (s2[i] - ms2);
	}
	denom = sqrt(ss1 * ss2);

	for (delay=-n/2;delay<n/2;delay++) {
		ss1s2 = 0;
		for (i=0;i<n;i++) {
			j = i + delay;
			if (j < 0 || j >= n)
            continue;
			else
				ss1s2 += (s1[i] - ms1) * (s2[j] - ms2);
			/* Or should it be (?)
         if (j < 0 || j >= n)
            ss1s2 += (s1[i] - ms1) * (-ms2);
         else
            ss1s2 += (s1[i] - ms1) * (s2[j] - ms2);
			*/
		}
		sout[delay+n/2] = ss1s2 / denom;
	}
}

/*-------------------------------------------------------------------------
   Solve a system of n equations in n unknowns using Gaussian Elimination
   Solve an equation in matrix form Ax = b
   The 2D array a is the matrix A with an additional column b.
   This is often written (A:b)

   A0,0    A1,0    A2,0    ....  An-1,0     b0
   A0,1    A1,1    A2,1    ....  An-1,1     b1
   A0,2    A1,2    A2,2    ....  An-1,2     b2
   :       :       :             :          :
   :       :       :             :          :
   A0,n-1  A1,n-1  A2,n-1  ....  An-1,n-1   bn-1

   The result is returned in x, otherwise the function returns FALSE
   if the system of equations is singular.
*/
int GSolve(double **a,int n,double *x)
{
   int i,j,k,maxrow;
   double tmp;
  
   for (i=0;i<n;i++) {

      /* Find the row with the largest first value */
      maxrow = i;
      for (j=i+1;j<n;j++) {
         if (ABS(a[i][j]) > ABS(a[i][maxrow]))
            maxrow = j;
      }

      /* Swap the maxrow and ith row */
      for (k=i;k<n+1;k++) {
         tmp = a[k][i];
         a[k][i] = a[k][maxrow];
         a[k][maxrow] = tmp;
      }

      /* Singular matrix? */
      if (ABS(a[i][i]) < EPS)
         return(FALSE);

      /* Eliminate the ith element of the jth row */
      for (j=i+1;j<n;j++) {
         for (k=n;k>=i;k--) {
            a[k][j] -= a[k][i] * a[i][j] / a[i][i];
         }
      }
   }

   /* Do the back substitution */
   for (j=n-1;j>=0;j--) {
      tmp = 0;
      for (k=j+1;k<n;k++)
         tmp += a[k][j] * x[k];
      x[j] = (a[n][j] - tmp) / a[j][j];
   }

   return(TRUE);
}

/*-------------------------------------------------------------------------
	Compute the circular autocorrelation (the long way without fft)
	Series in array x of length N, calculate autocorrelation at lag delay
	NOT normalised!
*/
double AutoCorr(double *x,int n,int delay)
{
	int i,k;
	double sum=0,mean=0;

	for (i=0;i<n;i++)
		mean += x[i];
	mean /= n;

	for (i=0;i<n;i++) {
		k = (i + delay) % n;
		sum += (x[i] - mean) * (x[k] - mean);
	}

	return(sum);
}

/*-------------------------------------------------------------------------
   Compute the circular crosscorrelation (the long way without fft)
   Series in array x and y of same length N, 
	Calculate crosscorrelation at lag delay
	NOT normalised!
*/
double CrossCorr(double *x,double *y,int n,int delay)
{
   int i,k;
   double sum=0,mean1=0,mean2=0;

   for (i=0;i<n;i++) {
      mean1 += x[i];
		mean2 += y[i];
	}
   mean1 /= n;
	mean2 /= n;

   for (i=0;i<n;i++) {
      k = (i + delay) % n;
      sum += (x[i] - mean1) * (y[k] - mean2);
   }

   return(sum);
}

/*
	Write an AIFF sound file
	Only do one channel, only support 16 bit.
	Supports sample frequencies of 11, 22, 44KHz (default).
	Little/big endian independent!
*/
void Write_AIFF(FILE *fptr,double *samples,long nsamples,int nfreq)
{
	unsigned short v;
	int i;
	unsigned long totalsize; 
	double themin,themax,scale,themid;
 
   /* Write the form chunk */
   fprintf(fptr,"FORM");
   totalsize = 4 + 8 + 18 + 8 + 2 * nsamples + 8;
   fputc((totalsize & 0xff000000) >> 24,fptr);
   fputc((totalsize & 0x00ff0000) >> 16,fptr);
   fputc((totalsize & 0x0000ff00) >> 8,fptr);
   fputc((totalsize & 0x000000ff),fptr);
   fprintf(fptr,"AIFF");

   /* Write the common chunk */
   fprintf(fptr,"COMM");
   fputc(0,fptr);                               /* Size */
   fputc(0,fptr);
   fputc(0,fptr);
   fputc(18,fptr);
   fputc(0,fptr);                               /* Channels = 1 */
   fputc(1,fptr);
   fputc((nsamples & 0xff000000) >> 24,fptr);   /* Samples */
   fputc((nsamples & 0x00ff0000) >> 16,fptr);
   fputc((nsamples & 0x0000ff00) >> 8,fptr);
   fputc((nsamples & 0x000000ff),fptr);
   fputc(0,fptr);                               /* Size = 16 */
   fputc(16,fptr);
	fputc(0x40,fptr);										/* 10 byte sample rate */
	if (nfreq == 11025) 
      fputc(0x0c,fptr);
	else if (nfreq == 22050) 
   	fputc(0x0d,fptr);
	else 
      fputc(0x0e,fptr);
	fputc(0xac,fptr);
   fputc(0x44,fptr);
   fputc(0,fptr); 
   fputc(0,fptr);
   fputc(0,fptr);
   fputc(0,fptr);
   fputc(0,fptr);
   fputc(0,fptr);

   /* Write the sound data chunk */
   fprintf(fptr,"SSND");
   fputc(((2*nsamples+8) & 0xff000000) >> 24,fptr);/* Size      */
   fputc(((2*nsamples+8) & 0x00ff0000) >> 16,fptr);
   fputc(((2*nsamples+8) & 0x0000ff00) >> 8,fptr);
   fputc(((2*nsamples+8) & 0x000000ff),fptr);
   fputc(0,fptr);                                /* Offset    */
   fputc(0,fptr);
   fputc(0,fptr);
   fputc(0,fptr);
   fputc(0,fptr);                                /* Block     */
   fputc(0,fptr);
   fputc(0,fptr);
   fputc(0,fptr);

	/* Find the range */
	themin = samples[0];
	themax = themin;
	for (i=1;i<nsamples;i++) {
		if (samples[i] > themax)
			themax = samples[i];
		if (samples[i] < themin)
			themin = samples[i];
	}
	if (themin >= themax) {
		themin -= 1;
		themax += 1;
	}
	themid = (themin + themax) / 2;
	themin -= themid;
	themax -= themid;
	if (ABS(themin) > ABS(themax))
		themax = ABS(themin);
	scale = 32760 / (themax);

	/* Write the data */
   for (i=0;i< nsamples;i++) {
		v = (unsigned short)(scale * (samples[i] - themid));
      fputc((v & 0xff00) >> 8,fptr);
      fputc((v & 0x00ff),fptr);
   }
}

/*
   Write an AU (Sun) sound file
   Only do one channel, only support 16 bit.
   Supports any (reasonable) sample frequency
   Little/big endian independent!
*/
void Write_AU(FILE *fptr,double *samples,long nsamples,int nfreq)
{
   unsigned short v;
   int i;
   unsigned long totalsize;
   double themin,themax,scale,themid;

   /* Write the form chunk */
   fprintf(fptr,".snd");
   fputc(0,fptr);                               /* Data offset */
   fputc(0,fptr);
   fputc(0,fptr);
   fputc(24,fptr);
   totalsize = 2 * nsamples;
   fputc((totalsize & 0xff000000) >> 24,fptr);  /* Data size */
   fputc((totalsize & 0x00ff0000) >> 16,fptr);
   fputc((totalsize & 0x0000ff00) >> 8,fptr);
   fputc((totalsize & 0x000000ff),fptr);
   fputc(0,fptr);                               /* Encoding, 16 PCM */
   fputc(0,fptr);
   fputc(0,fptr);
   fputc(3,fptr);
   fputc((nfreq & 0xff000000) >> 24,fptr);      /* Sample frequency (Hz) */
   fputc((nfreq & 0x00ff0000) >> 16,fptr);
   fputc((nfreq & 0x0000ff00) >> 8,fptr);
   fputc((nfreq & 0x000000ff),fptr);
   fputc(0,fptr);                               /* Channels */
   fputc(0,fptr);
   fputc(0,fptr);
   fputc(1,fptr);

   /* Find the range */
   themin = samples[0];
   themax = themin;
   for (i=1;i<nsamples;i++) {
      if (samples[i] > themax)
         themax = samples[i];
      if (samples[i] < themin)
         themin = samples[i];
   }
   if (themin >= themax) {
      themin -= 1;
      themax += 1;
   }
   themid = (themin + themax) / 2;
   themin -= themid;
   themax -= themid;
   if (ABS(themin) > ABS(themax))
      themax = ABS(themin);
   scale = 32760 / (themax);

   /* Write the data */
   for (i=0;i<nsamples;i++) {
      v = (unsigned short)(scale * (samples[i] - themid));
      fputc((v & 0xff00) >> 8,fptr);
      fputc((v & 0x00ff),fptr);
   }
}

/*
   Write an WAVE (MicroSloth) sound file
   Only do one channel, only support 16 bit.
   Supports any (reasonable) sample frequency
   Little/big endian independent!
	Assume samples are in the rage of -1 to 1
*/
void Write_WAVE(FILE *fptr,float *samples,int nsamples,int nfreq)
{
   short v;
   int i;
	float f;
   unsigned int totalsize,bytespersec;

   /* Write the form chunk */
   fprintf(fptr,"RIFF");
   totalsize = 2 * nsamples + 36;
	fputc((totalsize & 0x000000ff),fptr);        /* File size */
   fputc((totalsize & 0x0000ff00) >> 8,fptr);
	fputc((totalsize & 0x00ff0000) >> 16,fptr);
   fputc((totalsize & 0xff000000) >> 24,fptr);
	fprintf(fptr,"WAVE");
	fprintf(fptr,"fmt ");                        /* fmt_ chunk */
   fputc(16,fptr);                              /* Chunk size */
   fputc(0,fptr);
   fputc(0,fptr);
   fputc(0,fptr);
	fputc(1,fptr);                               /* Format tag - uncompressed */
   fputc(0,fptr);
   fputc(1,fptr);                               /* Channels */
   fputc(0,fptr);
	fputc((nfreq & 0x000000ff),fptr);            /* Sample frequency (Hz) */
   fputc((nfreq & 0x0000ff00) >> 8,fptr);
	fputc((nfreq & 0x00ff0000) >> 16,fptr);
	fputc((nfreq & 0xff000000) >> 24,fptr);
	bytespersec = 2 * nfreq;
   fputc((bytespersec & 0x000000ff),fptr);      /* Average bytes per second */
   fputc((bytespersec & 0x0000ff00) >> 8,fptr);
	fputc((bytespersec & 0x00ff0000) >> 16,fptr);
   fputc((bytespersec & 0xff000000) >> 24,fptr);
   fputc(2,fptr);                               /* Block alignment */
   fputc(0,fptr);
   fputc(16,fptr);                              /* Bits per sample */
   fputc(0,fptr);
   fprintf(fptr,"data");  
	totalsize = 2 * nsamples;
	fputc((totalsize & 0x000000ff),fptr);        /* Data size */
	fputc((totalsize & 0x0000ff00) >> 8,fptr);
	fputc((totalsize & 0x00ff0000) >> 16,fptr);
   fputc((totalsize & 0xff000000) >> 24,fptr); 

   /* Write the data */
   for (i=0;i<nsamples;i++) {
      f = samples[i];
		if (f < -1) f = -1;
		if (f > 1) f = 1;
		v = f * 32760;
		fputc((v & 0x00ff),fptr);
      fputc((v & 0xff00) >> 8,fptr);
   }
}

/*-------------------------------------------------------------------------
   Linear Regression
   y(x) = a x + b, for n samples
   The following assumes the standard deviations are unknown for x and y
   Return a, b and r the regression coefficient
*/
int LinRegress(double *x,double *y,int n,double *a,double *b,double *r)
{
   int i;
   double sumx=0,sumy=0,sumx2=0,sumy2=0,sumxy=0;
   double sxx,syy,sxy;

   *a = 0;
   *b = 0;
   *r = 0;
   if (n < 2)
      return(FALSE);

   for (i=0;i<n;i++) {
      sumx += x[i];
      sumy += y[i];
      sumx2 += (x[i] * x[i]);
      sumy2 += (y[i] * y[i]);
      sumxy += (x[i] * y[i]);
   }
   sxx = sumx2 - sumx * sumx / n;
   syy = sumy2 - sumy * sumy / n;
   sxy = sumxy - sumx * sumy / n;

   /*
      There is no x variation
      Infinite slope (b), non existant intercept (a)
   */
   if (ABS(sxx) == 0)
      return(FALSE);

   *b = sxy / sxx;
   *a = sumy / n - (*b) * sumx / n;

   if (ABS(syy) == 0)
      *r = 1;
   else
      *r = sxy / sqrt(sxx * syy);

   return(TRUE);
}

/*
   Cadd(z1,z2) = z1 + z2
*/
COMPLEX Cadd(COMPLEX z1,COMPLEX z2)
{
    COMPLEX ztmp;

    ztmp.real = z1.real + z2.real;
    ztmp.imag = z1.imag + z2.imag;
    return(ztmp);
}

/*
   Csub(z1,z2) = z1 - z2
*/
COMPLEX Csub(COMPLEX z1,COMPLEX z2)
{
    COMPLEX ztmp;

    ztmp.real = z1.real - z2.real;
    ztmp.imag = z1.imag - z2.imag;
    return(ztmp);
}

/*
   Cmult(z1,z2) = z1 * z2
*/
COMPLEX Cmult(COMPLEX z1,COMPLEX z2)
{
    COMPLEX ztmp;

    ztmp.real = z1.real * z2.real - z1.imag * z2.imag;
    ztmp.imag = z1.real * z2.imag + z2.real * z1.imag;
    return(ztmp);
}

/*
   Cmultd(z,d) = z * d
*/
COMPLEX Cmultd(COMPLEX z,double d)
{
    COMPLEX ztmp;

    ztmp.real = z.real * d;
    ztmp.imag = z.imag * d;
    return(ztmp);
}

/*
   Csqrt(z) 
   Sqrt of the root and half the angle.
   where u = sqrt(0.5*(root+x)) and v = sqrt(0.5*(root-x))
   and root is the magnitude of z
   the sign is the same as that of the imaginary part of z
*/
COMPLEX Csqrt(COMPLEX z)
{
   COMPLEX ztmp;
   double r,theta;

   r = sqrt(z.real*z.real + z.imag*z.imag);
   theta = atan2(z.imag,z.real);

   r = sqrt(r);
   ztmp.real = r * cos(theta/2);
   ztmp.imag = r * sin(theta/2);

   return(ztmp);
}

/*
   Natural logarithm of a complex number
*/
COMPLEX Clog(COMPLEX z)
{
    COMPLEX ztmp;

    if (z.imag == 0.0 && z.real > 0.0) {
        ztmp.real = log(z.real);
        ztmp.imag = 0.0;
    } else if (z.real == 0.0) {
        if (z.imag > 0.0) {
            ztmp.real = log(z.imag);
            ztmp.imag = PID2;
        } else {
            ztmp.real = log(-(z.imag));
            ztmp.imag = - PID2;
        }
    } else {
        ztmp.real = log(sqrt(z.real*z.real + z.imag*z.imag));
        ztmp.imag = atan2(z.imag,z.real);
    }
    return(ztmp);
}

/*
   Cexp(z) = exp(real) cos(imag) + j( exp(real) sin(imag) )
   where z = real + j imag
*/
COMPLEX Cexp(COMPLEX z)
{
    double r;
    COMPLEX ztmp;

    r = exp(z.real);
    ztmp.real = r * cos(z.imag);
    ztmp.imag = r * sin(z.imag);
    return(ztmp);
}

/*
  Csin(z) = sin(real) cosh(imag) + j cos(real) sinh(imag)
*/
COMPLEX Csin(COMPLEX z)
{
    COMPLEX ztmp;

    if (z.imag == 0.0) {
        ztmp.real = sin(z.real);
        ztmp.imag = 0.0;
    } else {
        ztmp.real = sin(z.real) * cosh(z.imag);
        ztmp.imag = cos(z.real) * sinh(z.imag);
    }
    return(ztmp);
}

/*
   Ccos(z) = cos(real) cosh(imag) - j sin(real) sinh(imag)
*/
COMPLEX Ccos(COMPLEX z)
{
    COMPLEX ztmp;

    if (z.imag == 0.0) {
        ztmp.real = cos(z.real);
        ztmp.imag = 0.0;
    } else {
        ztmp.real =   cos(z.real) * cosh(z.imag);
        ztmp.imag = - sin(z.real) * sinh(z.imag);
    }
    return(ztmp);
}

/*
  Ctan(z) = ( sin(2*real) + jsinh(2*imag) )
            -------------------------------
            ( cos(2*real) + cosh(2*imag) )
*/
COMPLEX Ctan(COMPLEX z)
{
    COMPLEX ztmp;
    double denom,real2,imag2;

    if (z.imag == 0.0) {
        ztmp.real = tan(z.real);
        ztmp.imag = 0.0;
    } else {
        real2 = 2.0 * z.real;
        imag2 = 2.0 * z.imag;
        denom = cos(real2) + cosh(imag2);
        ztmp.real = sin(real2) / denom;
        ztmp.imag = sinh(imag2) / denom;
    }
    return(ztmp);
}

/*
   Casin(z) = k*pi + (-1)^k asin(b)
                   + j (-1)^k log(a + sqrt(a^2 - 1))
   where a = 0.5 sqrt((x+1)^2 + y^2) + 0.5 sqrt((x-1)^2 + y^2)
   and   b = 0.5 sqrt((x+1)^2 + y^2) - 0.5 sqrt((x-1)^2 + y^2)
   and z = x + jy, k an integer
*/
COMPLEX Casin(COMPLEX z)
{
    COMPLEX ztmp;
    double a,b;
    double xm1,xp1,x2,y2;
    double part1,part2;

    if (z.imag == 0.0) {
        ztmp.real = asin(z.real);
        ztmp.imag = 0.0;
    } else {
        x2 = z.real * z.real;
        y2 = z.imag * z.imag;
        xp1 = x2 + 2.0 * z.real + 1.0;
        xm1 = x2 - 2.0 * z.real + 1.0;
        part1 = 0.5 * sqrt(xp1 + y2);
        part2 = 0.5 * sqrt(xm1 + y2);
        a = part1 + part2;
        b = part1 - part2;
        ztmp.real = asin(b);
        ztmp.imag = log(a + sqrt(a * a - 1.0) );
    }
    return(ztmp);
}

/*
   Cacos(z) = 2*k*pi (+-) [ acos(b)
                       - j log(a + sqrt(a^2 - 1))
   where a = 0.5 sqrt((x+1)^2 + y^2) + 0.5 sqrt((x-1)^2 + y^2)
   and   b = 0.5 sqrt((x+1)^2 + y^2) - 0.5 sqrt((x-1)^2 + y^2)
   and   z = x + jy, K an integer.
*/
COMPLEX Cacos(COMPLEX z)
{
    COMPLEX ztmp;
    double a,b;
    double xm1,xp1,x2,y2;
    double part1,part2;

    if (z.imag == 0.0) {
        ztmp.real = acos(z.real);
        ztmp.imag = 0.0;
    } else {
        x2 = z.real * z.real;
        y2 = z.imag * z.imag;
        xp1 = x2 + 2.0 * z.real + 1.0;
        xm1 = x2 - 2.0 * z.real + 1.0;
        part1 = 0.5 * sqrt(xp1 + y2);
        part2 = 0.5 * sqrt(xm1 + y2);
        a = part1 + part2;
        b = part1 - part2;
        ztmp.real = acos(b);
        ztmp.imag = - log( a + sqrt(a*a - 1.0) );
    }
    return(ztmp);
}

/*
   Catan(z) = k*pi + 0.5 * atan(2x/(1-x^2-y^2))
                   + j/4 * log (( x^2+(y+1)^2) / ( x^2+(y-1)^2))
*/
COMPLEX Catan(COMPLEX z)
{
    COMPLEX ztmp;
    double ym1,yp1,x2,y2,denom;

    if (z.imag == 0.0) {
        ztmp.real = atan(z.real);
        ztmp.imag = 0.0;
    } else {
        x2 = z.real * z.real;
        y2 = z.imag * z.imag;
        denom = 1.0 - x2 - y2;
        yp1 = x2 + y2 + 2.0 * z.imag + 1.0;
        ym1 = x2 + y2 - 2.0 * z.imag + 1.0;
        ztmp.real = 0.5 * atan( 2.0 * z.real / denom );
        ztmp.imag = 0.25 * log( yp1 / ym1 );
    }
    return(ztmp);
}

/*
   Csinh(z) = 0.5 ( Cexp(z) - Cexp(-z) )
*/
COMPLEX Csinh(COMPLEX z)
{
    COMPLEX ztmp;
    COMPLEX mz,zt1,zt2;

    mz.real = - z.real;
    mz.imag = - z.imag;
    zt1 = Cexp(z);
    zt2 = Cexp(mz);
    ztmp.real = 0.5 * (zt1.real - zt2.real );
    ztmp.imag = 0.5 * (zt1.imag - zt2.imag );
    return(ztmp);
}

/*
   Ccosh(z) = 0.5 ( Cexp(z) + Cexp(-z) )
*/
COMPLEX Ccosh(COMPLEX z)
{
    COMPLEX ztmp;
    COMPLEX mz,zt1,zt2;

    mz.real = - z.real;
    mz.imag = - z.imag;
    zt1 = Cexp(z);
    zt2 = Cexp(mz);
    ztmp.real = 0.5 * ( zt1.real + zt2.real );
    ztmp.imag = 0.5 * ( zt1.imag + zt2.imag );
    return(ztmp);
}

/*
   Ctanh(z) = ( 1 - Cexp(-2z) ) / ( 1 + Cexp(-2z) )
*/
COMPLEX Ctanh(COMPLEX z)
{
    COMPLEX ztmp;
    COMPLEX zt1,zt2,num,denom;

    if (z.imag == 0.0) {
        ztmp.real = tanh(z.real);
        ztmp.imag = 0.0;
    } else {
        zt1.real = -2.0 * z.real;
        zt1.imag = -2.0 * z.imag;
        zt2 = Cexp(zt1);
        num.real = 1.0 - zt2.real;
        num.imag = - zt2.imag;
        denom.real = 1.0 + zt2.real;
        denom.imag = zt2.imag;
        ztmp = Cdiv(num,denom);
    }
    return(ztmp);
}

/*
   Casinh(z) = Clog( z + Csqrt( z^2 + 1 ))
*/
COMPLEX Casinh(COMPLEX z)
{
    COMPLEX ztmp;
    COMPLEX zt1,zt2;

    zt1.real = z.real * z.real - z.imag * z.imag + 1.0;
    zt1.imag = 2.0 * z.real * z.imag;
    zt2 = Csqrt(zt1);
    zt2.real += z.real;
    zt2.real += z.imag;
    ztmp = Clog(zt2);
    return(ztmp);
}

/*
   Cacosh(z) = Clog ( z + Csqrt(z^2 - 1) )
*/
COMPLEX Cacosh(COMPLEX z)
{
    COMPLEX ztmp;
    COMPLEX zt1,zt2;

    zt1.real = z.real * z.real - z.imag * z.imag - 1.0;
    zt1.imag = 2.0 * z.real * z.imag;
    zt2 = Csqrt(zt1);
    zt2.real += z.real;
    zt2.imag += z.imag;
    ztmp = Clog(zt2);
    return(ztmp);
}

/*
   Catanh(z) = 0.5 * Clog( (1+z) / (1-z) )
*/
COMPLEX Catanh(COMPLEX z)
{
    COMPLEX ztmp;
    COMPLEX zp1,zm1,zt1;

    zp1.real = 1.0 + z.real;
    zp1.imag = z.imag;
    zm1.real = 1.0 - z.real;
    zm1.imag = - (z.imag);
    zt1 = Clog(Cdiv(zp1,zm1));
    ztmp.real = zt1.real * 0.5;
    ztmp.imag = zt1.imag * 0.5;
    return(ztmp);
}

/*
   Cdiv(z1,z2) = z1 / z2
*/
COMPLEX Cdiv(COMPLEX z1,COMPLEX z2)
{
    COMPLEX ztmp;
    double den,r;
    double absr,absi;

    absr = (z2.real >= 0 ? z2.real : -z2.real);
    absi = (z2.imag >= 0 ? z2.imag : -z2.imag);

    if (z1.real == 0.0 && z1.imag == 0.0) {
        ztmp.real = 0.0;
        ztmp.imag = 0.0;
    } else if (z2.real == 0.0 && z2.imag == 0.0) {
        ztmp.real = 0.0;
        ztmp.imag = 0.0;
    } else if (absr >= absi) {
        r = z2.imag / z2.real;
        den = z2.real + r * z2.imag;
        ztmp.real = (z1.real + z1.imag * r) / den;
        ztmp.imag = (z1.imag - z1.real * r) / den;
    } else {
        r = z2.real / z2.imag;
        den = z2.imag + r * z2.real;
        ztmp.real = (z1.real * r + z1.imag) / den;
        ztmp.imag = (z1.imag * r - z1.real) / den;
    }
    return(ztmp);
}

/*
	Cinv(z) = 1 / z
*/
COMPLEX Cinv(COMPLEX z)
{
	COMPLEX zone = {1.0,0.0};
	
	return(Cdiv(zone,z));
}

/*
   Cdivd(z1,d) = z / d
*/
COMPLEX Cdivd(COMPLEX z,double d)
{
    COMPLEX ztmp;

    if (d == 0.0) {
        ztmp.real = 0.0;
        ztmp.imag = 0.0;
    } else if (z.real == 0.0 && z.imag == 0.0) {
        ztmp.real = 0.0;
        ztmp.imag = 0.0;
    } else {
        ztmp.real = z.real / d;
        ztmp.imag = z.imag / d;
    }
    return(ztmp);
}

/*
   Cpowd(z,d) = z ^ d
	De Moivre's theorem
	Multiply angle by power and raise the modulus to the power
*/
COMPLEX Cpowd(COMPLEX z,double d)
{
   COMPLEX ztmp;
   double phi=0,r;

   r = z.real * z.real + z.imag * z.imag;
   r = exp(d * log(r) / 2);
   phi = d * atan2(z.imag,z.real);
   ztmp.real = r * cos(phi);
   ztmp.imag = r * sin(phi);

   return(ztmp);
}

/*
   Cabs(z) = sqrt( real^2 + imag^2 )
   where z = real + j imag
*/
double Cabs(COMPLEX z)
{
   double absr,absi,sqrr,sqri;

   if ((absr = z.real) < 0)
      absr = -z.real;
   if ((absi = z.imag) < 0)
      absi = -z.imag;

   if (absr == 0.0) {
      return(absi);
   } else if (absi == 0.0) {
      return(absr);
   } else if (absr > absi) {
      sqrr = absr * absr;
      sqri = absi * absi;
      return(absr * sqrt(1 + sqri/sqrr));
   } else {
      sqrr = absr * absr;
      sqri = absi * absi;
      return(absi * sqrt(1 + sqrr/sqri));
   }
}


