# pdt2famix
pdt2famix takes PHP code and produces MSE files that can be imported into Glamorous Toolkit. Based on Eclipse PDT.


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

Extra parameters:

- `-consoleLog`: show the full Eclipse log in the console;
- `-pdt2famixCleanWorkspace`: clean the current workspace including all projects;
- `-pdt2famixBuildWorkspace`: build the current workspace including all projects;
- `-pdt2famixCleanProject`: clean only the given project;
- `-pdt2famixBuildProject`: build only the given project.

### Running pdt2famix as a standalone application

Download and extract the archive containing the pdt2famix standalone application and run:

```./pdt2famix.app/Contents/MacOS/pdt2famix -data <path_to_workspace> -pdt2famixProject <project_name>```
Extra parameters:

- `-consoleLog`: show the full Eclipse log in the console;
- `-pdt2famixCleanWorkspace`: clean the current workspace including all projects;
- `-pdt2famixBuildWorkspace`: build the current workspace including all projects;
- `-pdt2famixCleanProject`: clean only the given project;
- `-pdt2famixBuildProject`: build only the given project.

## Setting up the development environment for pdt2famix

This section provides details for setting up a development environment for pdt2famix. 

First you need to configure eclipse:

- download (Eclipse for RCP and RAP Developers)[https://www.eclipse.org/downloads/eclipse-packages/]. pdt2famix uses Eclipse Oxigen;
- install Eclipse PDT using the update site (Eclipse PDT)[https://www.eclipse.org/pdt/#download]. The version used by pdt2famix is Eclipse PDT 5.2. Install also the Source Code for PDT from the SDK (or the entire SDK);
- install the Symphony plugin ([symfony.dubture.com](http://symfony.dubture.com));
- configure the paths to the `php` executable (optional).

Next clone the pdt2famix repository, and import all the Java projects into the workspace. pdt2famix also contains the project `com.feenk.pdt2famix.exporter.samples` that define tests for the exporter and does not have to be imported into this workspace.

To launch pdt2famix in a new Eclipse installation create a launch configuration for an `Eclipse Application`. pdt2famix will add a context menu to the `Project Explorer` view.

To launch pdt2famix as a product (headless) create a product configuration that runs `com.feenk.pdt2famix.client.product`. You can also create a launch configuration that runs the application `com.feenk.pdt2famix.client.application`.

To run the tests associated with pdt2famix create a new launch configuration for `JUnit Plug-in Test` and select to run the tests from the project `com.feenk.pdt2famix.exporter.test`. The current version of pdt2famix needs to run the tests in a workspace where the project `com.feenk.pdt2famix.exporter.samples` is already loaded. Hence, before running the tests you need to configure an new workspace where this project is loaded and update the `Location` parameter of the launch configuration to point to that workspace. Then make sure the launch configuration only clears the log and not the entire workspace before running the tests, as otherwise the project will be removed before running the tests.

## Releasing pdt2famix

For exporting only the plugins use from the Export wizard (File -> Export...) the wizard `Deployable plug-ins and fragments`. The exported plugins can then be just copied in an Eclipse installation where Eclipse PDT and the Sympfony extension are installed.

For exporting a headless eclipse application use the the wizard `Eclipse product export wizard` from the product `pdt2famixproduct.product` in the project `com.feenk.pdt2famix.client`. On MacOS this wizard does not product a 100% working application. To make it work:
- copy the file `MacOS/pdt2famix` to `pdt2famix.app/Contents/MacOS`. This makes `pdt2famix.app` a valid MacOS application;
- fix the dependency to `org.apache.lucene.core`. For some reason the export wizard creates a dependency to `org.apache.lucene.core_2.9.1.*.jar` instead of `org.apache.lucene.core_6.1.0.*.jar`. To fix this copy the jar `org.apache.lucene.core_6.1.0.*.jar` (for example `org.apache.lucene.core_6.1.0.v20170814-1820.jar`) from the plugins directory of the Eclipse instalation used to develop the plugins, to `plugins` folder of the exported pdt2famix and remove the existing `org.apache.lucene.core_2.9.1.*.jar` plugin (for example `org.apache.lucene.core_2.9.1.v201101211721.jar`). 
- update `configuration/org.eclipse.equinox.simpleconfigurator/bundles.info` with the dependency to `org.apache.lucene.core_2.9.1.*.jar`. Find the line defining the dependency to `org.apache.lucene.core_2.9.1.*.jar` and update it to point to `org.apache.lucene.core_6.1.0.*.jar`. For example: `org.apache.lucene.core,6.1.0.v20170814-1820,plugins/org.apache.lucene.core_6.1.0.v20170814-1820.jar,4,false`

## Creating a product for exporting pdt2famix

Exporting pdt2famix as an Eclipse plugins is done using a product configuration. The easiest way to create a new product configuration is from an exising launch configuration. To create a new product configuration for pdt2famix:

- create a new launch configuration for an `Eclipse Application`
- as `Program to run` select `Run an application` and then use `com.feenk.pdt2famix.client.application` (this is the application provided by pdt2famix for running the exporter);
- in the `Plug-ins` launch the configuration with `plug-ins selected below only` and select `com.feenk.pdt2famix.client` and `com.feenk.pdt2famix.exporter`;
- use `Add required plug-ins` twice to add all dependencies;
- if running `Validate plug-ins` trigger an error search for and add `org.eclipse.equinox.ds` together with all its dependencies;
- add `org.eclipse.dltk.core.index.lucene` together with all it's dependencies
- run and test that the configuration works

Next create a new `Product Configuration` using the previously defined launch configuration. In `Product Definition` use `com.feenk.pdt2famix.client.application` as an application. For `Product` create a new one or use an exiting one. The `Product` referes to extensions for the extension point `com.eclipse.core.runtime.products` in the project `com.feenk.pdt2famix.client`.
