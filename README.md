# pdt2famix
PHP importer for Moose

## Setting up the project 

To run pdt2famix you need first to setup an Eclipse PHP project containing the code. To do this download and configure eclipse PDT:

- download Eclipse PDT ([www.eclipse.org/pdt](https://www.eclipse.org/pdt)); Use at least Eclipse Oxygen;
- install the Symphony plugin ([symfony.dubture.com](http://symfony.dubture.com)). pdt2famix needs the plugin to resolve Doctrine annotations and work with Symfony projects;
- configure the paths to the `php` executable (optional).

Next import the project into workspace. Depending on the type of the project you can import it using different wizards:

- *importing a Symfony project*: select to import a `Symfony Project`. Configure the project name and the path to the source code. For this import to work all dependencies (the `vendor` folder) as well as the container dumper file (`appDevDebugProjectContainer.xml`) need to be present. You can setup this by runing `composed install` (requires `php`) or getting them from somebody who did this, if you do not have `php` installed.
- *create a standard PHP Project from existing sources*: select to create a new `PHP Project`. If the folder containg the code is in the workspace folder then select `Create a new project in workspace`, otherwise `Create project at existing location`. If you select the first option give the project the same name as the folder containing the project. This way Eclipse will create the project using those sources.

After the project is loaded run `Project->Clean`. This refreshes and builds the project and can reveal if there are any missing dependencies. If any dependencies are missing add them to the build path of the project.

## Setting up pdt2famix

Once Eclipse PDT is installed and you imported the project in an workspace you should setup pdt2famix. You can choose to install pdt2famix as a plugin in the Eclipse PDT installation that you previously configured or run it as a standalone application.

### Running pdt2famix as a plugin

Copy the jars for pdt2famix (`com.feenk.pdt2famix_*.jar`) in the plugins folder of the Eclipse PDT installation and restart Eclipse. pdt2famix adds a context menu action `pdt2famix -> Export MSE/ASTs` available in the  `Project Explorer` view when selecting a project.

Once the pdt2famix plugins are installed in Eclipse, they can also be invoked from the command line:

```./eclipse.app/Contents/MacOS/eclipse -nosplash -application com.feenk.pdt2famix.client.application -data <path_to_workspace> -pdt2famixProject <project_name>```

In the previous command:

- `eclipse.app` is the Eclipse app where we installed the plugings;
- `<path_to_workspace>` is the path to the workspace where we loaded the projects;
- `<project_name>` is the name of the project that we imported in the workspace.

The parameter `-consoleLog` can further be used to show the full Eclipse log in the console.

### Running pdt2famix as a standalone application

Download and extract the archive containing the pdt2famix standalone application and run:

```./pdt2famix.app/Contents/MacOS/pdt2famix -data <path_to_workspace> -pdt2famixProject <project_name>```

The parameter `-consoleLog` can further be used to show the full Eclipse log in the console.


## Setting up the development environment for pdt2famix


## Exporting pdt2famix as a product
