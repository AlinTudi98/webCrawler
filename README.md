# webCrawler
</br>

## Table of contents
- [Descriere](#descriere)
- [Utilizare](#utilizare)
- [Realizatori](#realizatori)

## Descriere
Este o aplicatie de tip web crawler care, pornind de la un URL/fisier cu URL-uri, va descarca paginile respective iar apoi, va descarca recursiv paginile catre care exista link-uri.
De asemenea, ofera si posibilitatea procesarii paginilor descarcate prin mecanisme de filtrare dupa tip, cautare dupa cuvinte cheie si crearea de sitemap specific unui site.

## Utilizare
Aplicatia este un utilitar in linie de comanda si pune la dispozitie urmatoarele functionalitati:
- descarcarea recursiva a paginilor web pornind de la un URL specificat sau de la un fisier cu URL-uri
- cautare dupa cuvinte cheie
- filtrare dupa tip/dimensiune
- creare sitemap

### Comenzi posibile:
- **crawl** - parametru folosit pentru pornirea procesului de descarcare a paginilor web 
- **search** - parametru folosit pentru pornirea procesului de cautare
- **sitemap** - parametru folosit pentru pornirea procesului de creare sitemap
- **list** - parametru folosit pentru pornirea procesului de filtrare

### Parametrii posibili:
- **dLink=** specificarea URL-ului pentru pagina de pornire in cadrul procesului de crawl
- **dLinksFile=** specificareaa fisierului de intrare cu URL-uri
- **ignoreRobots** folosit pentru a anunta programul sa ignore fisierele /robots.txt
- **maxDepth=**  adancimea maxima permisa in cadrul procesului de descarcare
- **rootDir=**  directorul radacina unde vor fi salvate paginile descarcate
- **maxSize=**  dimensiunea maxima permisa, dimensiune specificata in KB
- **dTypes=**  specificarea extensiilor permise pentru paginile descarcate
- **config=**  calea catre fisierul de configurare

### Exemplu fisier de configurare:
- **numThreads** = 1
- **delay** = 1
- **rootDir** = dir
- **logLevel** = 1
- **ignoreRobots** = 1
- **logFilename** = log.txt
- **maxDepth** = 1
- **dSizeLimit** = 1024
- **dTypes** = pdf docx doc

Pentru specificarea numarului de thread-uri dorit, trebuie suprascrisa variabila **numThreads** din cadrul fisierului de configurare.


## Realizatori

:man_student: Tudose Alin-Romeo  
<br />
:woman_student: Avram Andreea-Elena  
<br />
:man_student: Brînzea Andrei  
<br />
:man_student: Ciobanu Cosmin-Marian  
<br />
:man_student: Ghiță Andrei-Alexandru  
