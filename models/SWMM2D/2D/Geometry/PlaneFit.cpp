#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <math.h>

// Geometric Tools, Inc.
// http://www.geometrictools.com
// Copyright (c) 1998-2006.  All Rights Reserved
//
// The Wild Magic Library (WM3) source code is supplied under the terms of
// the license agreement
//     http://www.geometrictools.com/License/WildMagic3License.pdf
// and may not be copied or disclosed except in accordance with the terms
// of that agreement.

/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

// Code modification by GISWATER team to adapt to class hierarchy in SWMM2D

#include "headers.h"
#include "PlaneFit.h"


class Eigen
{
public:


  void DecrSortEigenStuff(void)
  {
    Tridiagonal(); //diagonalize the matrix.
    QLAlgorithm(); //
    DecreasingSort();
    GuaranteeRotation();
  }

  void Tridiagonal(void)
  {
    double fM00 = mElement[0][0];
    double fM01 = mElement[0][1];
    double fM02 = mElement[0][2];
    double fM11 = mElement[1][1];
    double fM12 = mElement[1][2];
    double fM22 = mElement[2][2];

    m_afDiag[0] = fM00;
    m_afSubd[2] = 0;
    if (fM02 != (float)0.0)
    {
      double fLength = sqrt(fM01*fM01+fM02*fM02);
      double fInvLength = ((double)1.0)/fLength;
      fM01 *= fInvLength;
      fM02 *= fInvLength;
      double fQ = ((double)2.0)*fM01*fM12+fM02*(fM22-fM11);
      m_afDiag[1] = fM11+fM02*fQ;
      m_afDiag[2] = fM22-fM02*fQ;
      m_afSubd[0] = fLength;
      m_afSubd[1] = fM12-fM01*fQ;
      mElement[0][0] = (double)1.0;
      mElement[0][1] = (double)0.0;
      mElement[0][2] = (float)0.0;
      mElement[1][0] = (float)0.0;
      mElement[1][1] = fM01;
      mElement[1][2] = fM02;
      mElement[2][0] = (double)0.0;
      mElement[2][1] = fM02;
      mElement[2][2] = -fM01;
      m_bIsRotation = false;
    }
    else
    {
      m_afDiag[1] = fM11;
      m_afDiag[2] = fM22;
      m_afSubd[0] = fM01;
      m_afSubd[1] = fM12;
      mElement[0][0] = (float)1.0;
      mElement[0][1] = (float)0.0;
      mElement[0][2] = (float)0.0;
      mElement[1][0] = (float)0.0;
      mElement[1][1] = (float)1.0;
      mElement[1][2] = (float)0.0;
      mElement[2][0] = (float)0.0;
      mElement[2][1] = (float)0.0;
      mElement[2][2] = (float)1.0;
      m_bIsRotation = true;
    }
  }

  bool QLAlgorithm(void)
  {
    const int iMaxIter = 32;

    for (int i0 = 0; i0 <3; i0++)
    {
      int i1;
      for (i1 = 0; i1 < iMaxIter; i1++)
      {
        int i2;
        for (i2 = i0; i2 <= (3-2); i2++)
        {
          double fTmp = fabs(m_afDiag[i2]) + fabs(m_afDiag[i2+1]);
          if ( fabs(m_afSubd[i2]) + fTmp == fTmp )
            break;
        }
        if (i2 == i0)
        {
          break;
        }

        double fG = (m_afDiag[i0+1] - m_afDiag[i0])/(((float)2.0) * m_afSubd[i0]);
        double fR = sqrt(fG*fG+(double)1.0);
        if (fG < (double)0.0)
        {
          fG = m_afDiag[i2]-m_afDiag[i0]+m_afSubd[i0]/(fG-fR);
        }
        else
        {
          fG = m_afDiag[i2]-m_afDiag[i0]+m_afSubd[i0]/(fG+fR);
        }
        double fSin = (double)1.0, fCos = (double)1.0, fP = (double)0.0;
        for (int i3 = i2-1; i3 >= i0; i3--)
        {
          double fF = fSin*m_afSubd[i3];
          double fB = fCos*m_afSubd[i3];
          if (fabs(fF) >= fabs(fG))
          {
            fCos = fG/fF;
            fR = sqrt(fCos*fCos+(double)1.0);
            m_afSubd[i3+1] = fF*fR;
            fSin = ((double)1.0)/fR;
            fCos *= fSin;
          }
          else
          {
            fSin = fF/fG;
            fR = sqrt(fSin*fSin+(double)1.0);
            m_afSubd[i3+1] = fG*fR;
            fCos = ((double)1.0)/fR;
            fSin *= fCos;
          }
          fG = m_afDiag[i3+1]-fP;
          fR = (m_afDiag[i3]-fG)*fSin+((double)2.0)*fB*fCos;
          fP = fSin*fR;
          m_afDiag[i3+1] = fG+fP;
          fG = fCos*fR-fB;
          for (int i4 = 0; i4 < 3; i4++)
          {
            fF = mElement[i4][i3+1];
            mElement[i4][i3+1] = fSin*mElement[i4][i3]+fCos*fF;
            mElement[i4][i3] = fCos*mElement[i4][i3]-fSin*fF;
          }
        }
        m_afDiag[i0] -= fP;
        m_afSubd[i0] = fG;
        m_afSubd[i2] = (double)0.0;
      }
      if (i1 == iMaxIter)
      {
        return false;
      }
    }
    return true;
  }

  void DecreasingSort(void)
  {
    //sort eigenvalues in decreasing order, e[0] >= ... >= e[iSize-1]
    for (int i0 = 0, i1; i0 <= 3-2; i0++)
    {
      // locate maximum eigenvalue
      i1 = i0;
      double fMax = m_afDiag[i1];
      int i2;
      for (i2 = i0+1; i2 < 3; i2++)
      {
        if (m_afDiag[i2] > fMax)
        {
          i1 = i2;
          fMax = m_afDiag[i1];
        }
      }

      if (i1 != i0)
      {
        // swap eigenvalues
        m_afDiag[i1] = m_afDiag[i0];
        m_afDiag[i0] = fMax;
        // swap eigenvectors
        for (i2 = 0; i2 < 3; i2++)
        {
          double fTmp = mElement[i2][i0];
          mElement[i2][i0] = mElement[i2][i1];
          mElement[i2][i1] = fTmp;
          m_bIsRotation = !m_bIsRotation;
        }
      }
    }
  }


  void GuaranteeRotation(void)
  {
    if (!m_bIsRotation)
    {
      // change sign on the first column
      for (int iRow = 0; iRow <3; iRow++)
      {
        mElement[iRow][0] = -mElement[iRow][0];
      }
    }
  }

  double mElement[3][3];
  double m_afDiag[3];
  double m_afSubd[3];
  bool m_bIsRotation;
};


class Vec3
{
public:
  Vec3(void) { };
  Vec3(double _x,double _y,double _z) { x = _x; y = _y; z = _z; };


  double dot(const Vec3 &v)
  {
    return x*v.x + y*v.y + z*v.z; // the dot product
  }

  double x;
  double y;
  double z;
};




// Function to fit a plane to a set of points
int getBestFitPlane(TPolygon *polygon)
{
  int ret = 0;
  int i;

  //Initialize data
  (*polygon).centroid.x = 0.0f;
  (*polygon).centroid.y = 0.0f;
  (*polygon).centroid.z = 0.0f;

  (*polygon).plane.A = 0.0f;
  (*polygon).plane.B = 0.0f;
  (*polygon).plane.C = 0.0f;
  (*polygon).plane.D = 0.0f;

  //Compute centroid
    for (i=0; i<(*polygon).numVertex; i++)
    {

	  (*polygon).centroid.x += (*polygon).vertex[i].x;
	  (*polygon).centroid.y += (*polygon).vertex[i].y;
	  (*polygon).centroid.z += (*polygon).vertex[i].z;

    }

  double recip = 1.0f / (*polygon).numVertex; // reciprocol of total weighting

  (*polygon).centroid.x *= recip;
  (*polygon).centroid.y *= recip;
  (*polygon).centroid.z *= recip;


  //Variable used in the LSM
  double fSumXX=0;
  double fSumXY=0;
  double fSumXZ=0;

  double fSumYY=0;
  double fSumYZ=0;
  double fSumZZ=0;

    for (i=0; i<(*polygon).numVertex; i++)
    {

      TXYZ kDiff;

      kDiff.x = (*polygon).vertex[i].x - (*polygon).centroid.x;
      kDiff.y = (*polygon).vertex[i].y - (*polygon).centroid.y;
      kDiff.z = (*polygon).vertex[i].z - (*polygon).centroid.z;

      fSumXX+= kDiff.x * kDiff.x; // sume of the squares of the differences.
      fSumXY+= kDiff.x * kDiff.y; // sume of the squares of the differences.
      fSumXZ+= kDiff.x * kDiff.z; // sume of the squares of the differences.

      fSumYY+= kDiff.y * kDiff.y;
      fSumYZ+= kDiff.y * kDiff.z;
      fSumZZ+= kDiff.z * kDiff.z;
    }


  fSumXX *= recip;
  fSumXY *= recip;
  fSumXZ *= recip;
  fSumYY *= recip;
  fSumYZ *= recip;
  fSumZZ *= recip;

  // setup the eigensolver
  Eigen kES;

  kES.mElement[0][0] = fSumXX;
  kES.mElement[0][1] = fSumXY;
  kES.mElement[0][2] = fSumXZ;

  kES.mElement[1][0] = fSumXY;
  kES.mElement[1][1] = fSumYY;
  kES.mElement[1][2] = fSumYZ;

  kES.mElement[2][0] = fSumXZ;
  kES.mElement[2][1] = fSumYZ;
  kES.mElement[2][2] = fSumZZ;

  // compute eigenstuff, smallest eigenvalue is in last position
  kES.DecrSortEigenStuff();

  Vec3 kNormal;
  Vec3 kOrigin((*polygon).centroid.x,(*polygon).centroid.y,(*polygon).centroid.z);


  kNormal.x = kES.mElement[0][2];
  kNormal.y = kES.mElement[1][2];
  kNormal.z = kES.mElement[2][2];

  // the minimum energy
  (*polygon).plane.A = kNormal.x;
  (*polygon).plane.B = kNormal.y;
  (*polygon).plane.C = kNormal.z;


  (*polygon).plane.D = 0 - kNormal.dot(kOrigin);

  return ret;
}
