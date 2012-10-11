**************************************************************
NOTE: it is recomended to read help documents in HTML format.
**************************************************************

About Re-searcher

Re-searcher is an open source system for recurrent psiblast searches developed
in the Institute of Biotechnology, Laboratory of Bioinformatics, Vilnius,
Lithuania. It enables timely detection of new proteins on the protein sequence
databases.
Using Re-searcher you can enter new protein sequences and choose psiblast
parameters for the search. Each user has his own sequence list (or psiblast
queries list) on which psiblst searches are executed periodically.


Quick Installation Guide

Use the following instructions to install the Re-searcher for NCBI searches. If
you encounter problems or you need additional explanation refer to the documentation.txt.

  1. Download and install Java Runtime Environment. In Linux, set your $PATH
     variable to contain the folder of java executable.
  2. Download and extract Re-searcher to desired location on your file system.
  3. To start Researcher in Windows run the researcher/bin/researcher.bat.
     To Start Researcher in Linux use the following command: "researcher/bin/researcher console".
  4. After starting, follow this link to start using Re-searcher http://localhost:8080/Researcher/app.


Configuring Local Psiblast Searches

In order to run searches on a local server you need to do some configuration.
Local server must have psiblast installed and ssh access enabled. To cofigure
Re-searcher's access to the local server:

  1. Login to Re-searcher as administrator (initially, user name and password
     both have value "admin"). Follow "Configuration" link on main menu.
  2. Fill in psiblast server options. For help on psiblast server options click
     the questionmark on Re-searcher or refer to documentation.txt.

Configuring Email Notifications
To enable email notifications:

  1. Login to Re-searcher as administrator (initially, user name and password
     both have value "admin"). Follow "Configuration" link on main menu.
  2. Fill in email notification options. For help on psiblast server options
     click the questionmark on Re-searcher or refer to documentation.txt.

