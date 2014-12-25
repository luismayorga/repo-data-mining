# nose

Nose is a tool to detect the evolution of bad smells over several versions of a Java SVN repository. It is just a Clojure program that automates the task by running [inFusion](http://www.intooitus.com/products/infusion) over the specified versions of the project and stores the results in a serverless database for its study. The motivation and the results obtained can be found in the [report](report.pdf). 

If you want a quick overview, you can have a look at the [presentation](https://speakerdeck.com/rapsioux/bad-smell-removal-recipes-from-repository-mining) given by [Javier Pérez](https://github.com/javipeg) at the [Belgium and Netherlands software evolution workshop](http://benevol.cwi.nl/2014/).

## Installation

The application can be used with leiningen by checking out this repository or the latest version of a standalone jar can be downloaded from the [tags section](https://github.com/rapsioux/nose/releases) of this repository.

It can be launched by using `java -jar`.


## Usage

The application has three subcommands:

- `db`, which manages the database. At this moment it only accepts another subcommand, `create`, which creates a database called `smell_history.sqlite` in the work directory.

- `analyse`, which runs inFusion for a project and takes three arguments. The first one is the location of the inFusionC executable, which bundles with inFusion. In the OS X application it can be found inside it, in `inFusion.app/Contents/MacOS/inFusionC`.  The second one is the location of a svn repository containing the project to be analysed. The third one is an existing folder where the xml reports will be created.
    
    There are two options available:
    + `-f` or `--first-revision` that specifies the starting revision. The default value is 0.
    + `-l` or `--last-revision` that specifies the last revision. The default value is the max revision of the repository.
    + `-s` or `--step` that specifies how continuos the revisions are. Will iterate over the repository using such increments. The default value is `1`.

- `transform`, which takes a folder that contains the inFusion reports and loads them into the database.

## Result processing
The results obtained in the database can be processed in order to obtain further information. The [paper](report.pdf) presents the study of bad smells removals in four software repositories.

The next datasets show the data extracted:

*    [Flaws detected in each revision](https://www.google.com/fusiontables/DataSource?docid=1e-TtVQK_iGtDWaEehV1qi5chjO22T8vOAfGs_6an)
*    [Last appearance of each bad smell](https://www.google.com/fusiontables/DataSource?docid=1zGlGEZXFygEIaCqL4Xi4j5Job_Y5wbeqcYb4HFuQ)
*    [Bad smells removals identified](https://www.google.com/fusiontables/DataSource?docid=1QA9G-xxezXnZcAogccVnTrKETxjAuVd-iKZslTnF)
