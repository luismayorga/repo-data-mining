# nose

App to detect the evolution of bad smells over several versions of a Java SVN repository.

## Installation

Download the latest version of the jar from the [tags section](https://github.com/rapsioux/nose/releases) of this repository.


## Usage

The application has three subcommands:

- `db`, which manages the database. At this moment it only accepts another subcommand, `create`, that takes as an argument the name of the database to be created.

- `analyse`, which runs inFusion for a project and takes two arguments. The first one is the location of the inFusionC executable and the second one is the location of a working copy to be analysed.
    
    There are two options available:
    + `-f` or `--first-revision` that specifies the starting revision. The default value is 0.
    + `-l` or `--last-revision` that specifies the last revision. The default value is the max revision of the repository.

- `transform`, which takes a folder that contains the inFusion reports and the database and loads them into it.

## Result processing
The results obtained in the database can be processed in order to obtain further information. The paper [TBD](http://) presents study of the bad smells removal within four software repositories.

The next datasets show the data extracted:

*    [Flaws detected in each revision](https://www.google.com/fusiontables/DataSource?docid=1e-TtVQK_iGtDWaEehV1qi5chjO22T8vOAfGs_6an)
*    [Last appearance of each bad smell](https://www.google.com/fusiontables/DataSource?docid=1zGlGEZXFygEIaCqL4Xi4j5Job_Y5wbeqcYb4HFuQ)
*    [Bad smells removals identified](https://www.google.com/fusiontables/DataSource?docid=1QA9G-xxezXnZcAogccVnTrKETxjAuVd-iKZslTnF)
