Welcome to Krut Computer Recorder.

Some simple instructions on how to run the program can be found in the file "docs.txt". The instructions in that file are enough to run the program in most cases.

To install krut, unpack this zip-archive into a directory of your choice.

You need java JDK 5.0 or JRE 5.0 to run this program, available for download at: 
	http://java.sun.com/j2se/1.5.0/download.jsp

For instructions about how to install the correct version of java on ubuntu systems, see the file "sun-java.txt". These instructions could possibly be of some use on other linux systems as well.

To run krut, you can double click the file krut.bat (only in windows), or you can run KRUT.jar, either by double clicking it, or by running it with java ("Open With" in windows).

Below follows:

1. Advanced installation instructions
2. Comment on audio and video recording
3. Comments on the source code
4. Comments on JMF
5. Conditions of use.



-----------------------------------------

1. Advanced installation instructions

-----------------------------------------

On older versions of Windows, or computers with a faulty installation of java, there might still be problems. A method that always works is:

- Open the command prompt. (In windows you do this by clicking "Start > Run" and typing "cmd", or in some cases "command", and hitting return in the little Run window that pops up.)

- Go to the directory to which you extracted the zip-file you downloaded. (In windows you use the command "cd <directory name>" to move to a directory, and "cd .." to move to the directory above the one you're currently in. To see the files in the directory you're currently in, you type "dir".)

- Once in the directory of Krut, type:

	java -jar KRUT.jar

- If this didn't work, you need to know where your "java.exe" file is located. The path to the "java.exe" file is usually something like "C:\Program Files\jre1.5.X_XX\bin". You have to look around a little (or use the search function to find the file "java.exe"). Then type, from the directory of Krut:

	<your path to java>\java -jar KRUT.jar

Examples for Windows:

	c:\program files\java\jre1.5.0\bin\java -jar KRUT.jar
	c:\jdk1.5.0\bin\java -jar KRUT.jar

If you run krut from the command prompt, you will have access to more output from the program, if you feel you should need it.



-----------------------------------------

2. Comment on audio and video recording

-----------------------------------------

To record audio, you need to enable the "Wave out-mix", or similar, under recording in your sound controls. See video instr_sound.mov for an example in danish XP. See video sound_ubuntu.mov for an example in ubuntu.

To record video from a media player you need to disable video acceleration. See video instr_video.mov for an example in danish XP.



-----------------------------------------

3. Comments on the source code

-----------------------------------------

The source code was compiled in netbeans, and for it to work without changes (changing packages and the directory structure), a similar IDE should be used to compile it.

Source code is availible in the jar file. This program uses JMF, found in the folder jmf in this zip-archive, unless you're using the source package, or if needed, at:

	http://java.sun.com/products/java-media/jmf/2.1.1/download.html

Sources to JMF are also available at the address above.



-----------------------------------------

4. Comments on JMF

-----------------------------------------

If you run krut from somewhere else than the jar-file, you need to have JMF installed, or you need to have jmf.jar in your classpath. The main class is Run_KRUT.

Example Windows: 
	cd <Path to Run_KRUT.class>
	<Path to java\bin>\java -classpath .;<Path to jmf.jar>\jmf.jar Run_KRUT

Example Linux: 
	cd <Path to Run_KRUT.class>
	<Path to java/bin>/java -classpath ./:./<Path to jmf.jar>/jmf.jar Run_KRUT

If you get the error message shown in the file "jmf-error.txt", you most likely have a problem with your installation of the JMF. This should not happend if you're running a KRUT.jar-file, unless you're using krut_compact. See krut-compact-readme for details. If you get another error message, you probably have an incorrect version of java.


-----------------------------------------

5. Conditions of use.

-----------------------------------------

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License distributed with the program, or found here:
	http://opensource.org/licenses/gpl-license.php

This program contains modified versions of Merge.java and JpegImagesToMovie.java. See conditions of use below.


/*
 * @(#)Merge.java	1.2 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */


/*
 * @(#)JpegImagesToMovie.java	1.3 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */
