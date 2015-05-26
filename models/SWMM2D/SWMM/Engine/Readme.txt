INSTRUCTIONS FOR COMPILING SWMM5.EXE USING MICROSOFT VISUAL C++ 2010
=====================================================================

1. Open the file swmm5.c in a text editor and make sure that the
   compiler directives at the top of the file read as follows:
       #define CLE
       //#define SOL
       //#define DLL

2. Create a sub-directory named VC2010_CLE under the directory where
   the SWMM 5 Engine source code files are stored and copy
   VC2010-CLE.VCPROJ to it.

3. Launch Visual C++ 2010 and use the File >> Open command to open
   the VC2010-CLE.VCPROJ file.

4. Issue the Build >> Configuration Manager command and select the
   Release configuration.

5. Issue the Build >> Build VC2010-CLE command to build SWMM5.EXE
   (which will appear in the Release subdirectory underneath the
   VC2010-CLE directory).


