# Developer Guide 

* [Setting Up](#setting-up)
* [Design](#design)
* [Implementation](#implementation)
* [Testing](#testing)
* [Dev Ops](#dev-ops)
* [Appendix A: User Stories](#appendix-a--user-stories)
* [Appendix B: Use Cases](#appendix-b--use-cases)
* [Appendix C: Non Functional Requirements](#appendix-c--non-functional-requirements)
* [Appendix D: Glossary](#appendix-d--glossary)
* [Appendix E : Product Survey](#appendix-e--product-survey)

[//]: # "@@author A0147944U"
## Setting up

#### Prerequisites

1. **JDK `1.8.0_111`**  or later - [Official Download](http://www.oracle.com/technetwork/java/javase/downloads/index.html) <br>

    > Having any Java 8 version is not enough. <br>
    This app will not work with earlier versions of Java 8.
    
2. **Eclipse** IDE - [Official Download](https://eclipse.org/) <br>
3. **e(fx)clipse** plugin for Eclipse (Do the steps 2 onwards given in [this page](http://www.eclipse.org/efxclipse/install.html#for-the-ambitious)) <br>
4. **Buildship Gradle Integration** plugin from the Eclipse Marketplace
5. **EclEmma** plugin from the Eclipse Marketplace


#### Importing the project into Eclipse

0. Fork this repo, and clone the fork to your computer
1. Open Eclipse (Note: Ensure you have installed the **e(fx)clipse** and **buildship** plugins as given 
   in the prerequisites above)
2. Click `File` > `Import`
3. Click `Gradle` > `Gradle Project` > `Next` > `Next`
4. Click `Browse`, then locate the project's directory
5. Click > `Next` > `Next`
6. Click `Finish`

  > * If you are asked whether to 'keep' or 'overwrite' config files, choose to 'keep'.
  > * If unable to successfully import (i.e. errors showing up), reimport it and select 'overwrite'
  > * If Eclipse auto-changed any settings files during the import process, you may discard those changes. Remember not to push these changed files if settings are local.
  > * Depending on your connection speed and server load, it can even take up to 30 minutes for the set up to finish. (This is because Gradle downloads library files from servers during the project set up process) You may check on its progress via the progress bar at the bottom right corner of Eclipse.
[//]: # "@@author"

## Design

### Architecture

<img src="images/architecture.png" width="600"><br>
The **_Architecture Diagram_** given above explains the high-level design of the App.
Given below is a quick overview of each component.

`Main` has only one class called [`MainApp`](../src/main/java/seedu/task/MainApp.java). It is responsible for,
* At app launch: Initializes the components in the correct sequence, and connect them up with each other.
* At shut down: Shuts down the components and invoke cleanup method where necessary.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.
Two of those classes play important roles at the architecture level.
* `EventsCentre` : This class (written using [Google's Event Bus library](https://github.com/google/guava/wiki/EventBusExplained))
  is used by components to communicate with other components using events (i.e. a form of _Event Driven_ design)
* `LogsCenter` : Used by many classes to write log messages to the App's log file.

The rest of the App consists four components.
* [**`UI`**](#ui-component) : The UI of tha App.
* [**`Logic`**](#logic-component) : The command executor.
* [**`Model`**](#model-component) : Holds the data of the App in-memory.
* [**`Storage`**](#storage-component) : Reads data from, and writes data to, the hard disk.

Each of the four components
* Defines its _API_ in an `interface` with the same name as the Component.
* Exposes its functionality using a `{Component Name}Manager` class.

For example, the `Logic` component (see the class diagram given below) defines it's API in the `Logic.java`
interface and exposes its functionality using the `LogicManager.java` class.<br>
<img src="images/LogicClassDiagram.png" width="800"><br>

The _Sequence Diagram_ below shows how the components interact for the scenario where the user issues the
command `delete 3`.
[//]: # "@@author A0152958R"
<img src="images/build.png" width="800">
[//]: # "@@author"

>Note how the `Model` simply raises a `TaskManagerChangedEvent` when the TaskManager data are changed,
 instead of asking the `Storage` to save the updates to the hard disk.

The diagram below shows how the `EventsCenter` reacts to that event, which eventually results in the updates
being saved to the hard disk and the status bar of the UI being updated to reflect the 'Last Updated' time. <br>
[//]: # "@@author A0152958R"
<img src="images\storage.png" width="800">
[//]: # "@@author"

> Note how the event is propagated through the `EventsCenter` to the `Storage` and `UI` without `Model` having
  to be coupled to either of them. This is an example of how this Event Driven approach helps us reduce direct 
  coupling between components.

The sections below give more details of each component.

### UI component
[//]: # "@@author A0152958R"
<img src="images/Ui.png" width="800"><br>
[//]: # "@@author"

**API** : [`Ui.java`](../src/main/java/seedu/task/ui/Ui.java)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `TaskListPanel`,
`StatusBarFooter`, `BrowserPanel` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class
and they can be loaded using the `UiPartLoader`.

The `UI` component uses JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files
 that are in the `src/main/resources/view` folder.<br>
 For example, the layout of the [`MainWindow`](../src/main/java/seedu/task/ui/MainWindow.java) is specified in
 [`MainWindow.fxml`](../src/main/resources/view/MainWindow.fxml)

The `UI` component,
* Executes user commands using the `Logic` component.
* Binds itself to some data in the `Model` so that the UI can auto-update when data in the `Model` change.
* Responds to events raised from various parts of the App and updates the UI accordingly.

### Logic component 

<img src="images/LogicClassDiagram.png" width="800"><br>

**API** : [`Logic.java`](../src/main/java/seedu/task/logic/Logic.java)

1. `Logic` uses the `Parser` class to parse the user command.
2. This results in a `Command` object which is executed by the `LogicManager`.
3. The command execution can affect the `Model` (e.g. adding a task) and/or raise events.
4. The result of the command execution is encapsulated as a `CommandResult` object which is passed back to the `Ui`.

Given below is the Sequence Diagram for interactions within the `Logic` component for the `execute("delete 1")`
 API call.<br>
 [//]: # "@@ A0152958R"
<img src="images/logic.png" width="800"><br>
[//]: # "@@author"

[//]: # "@@author A0147335E"
**Undo Command** : [`UndoCommand.java`](../src/main/java/seedu/task/logic/commands/UndoCommand.java)

1. `UndoCommand` uses the `HistoryManager` class to extract the previous commands that was successfully inputted by user.
2. As the user wants to undo previous command, the command that is actually executed is the reverse action for previous command.
3. As undo command is most useful for delete, add, clear, done, undone, edit commands as it uses the storage component.
4. The result of the undo command execution is encapsulated as a `CommandResult` object which is passed back to the `Ui`.
5. User can also undo multiple times and it is done by calling undo command with the number of times that the user set
[//]: # "@@author"

### Model component
[//]: # "@@author A0152958R"
<img src="images/model.png" width="800"><br>
[//]: # "@@author"

**API** : [`Model.java`](../src/main/java/seedu/task/model/Model.java)

The `Model`,
* stores a `UserPref` object that represents the user's preferences.
* stores the TaskManager data.
* exposes a `UnmodifiableObservableList<ReadOnlyTaskManager>` that can be 'observed' e.g. the UI can be bound to this list
  so that the UI automatically updates when the data in the list change.
* does not depend on any of the other three components.

### Storage component
[//]: # "@@author A0152958R"
<img src="images/storageClass.png" width="800"><br>
[//]: # "@@author"

**API** : [`Storage.java`](../src/main/java/seedu/task/storage/Storage.java)

The `Storage` component,
* can save `UserPref` objects in json format and read it back.
* can save the TaskManager data in xml format and read it back.

### Common classes

Classes used by multiple components are in the `seedu.task.commons` package.

## Implementation

### Logging

We are using `java.util.logging` package for logging. The `LogsCenter` class is used to manage the logging levels
and logging destinations.

* The logging level can be controlled using the `logLevel` setting in the configuration file
  (See [Configuration](#configuration))
* The `Logger` for a class can be obtained using `LogsCenter.getLogger(Class)` which will log messages according to
  the specified logging level
* Currently log messages are output through: `Console` and to a `.log` file.

**Logging Levels**

* `SEVERE` : Critical problem detected which may possibly cause the termination of the application
* `WARNING` : Can continue, but with caution
* `INFO` : Information showing the noteworthy actions by the App
* `FINE` : Details that is not usually noteworthy but may be useful in debugging
  e.g. print the actual list instead of just its size

### Configuration

Certain properties of the application can be controlled (e.g App name, logging level) through the configuration file 
(default: `config.json`):

## Testing

Tests can be found in the `./src/test/java` folder.

**In Eclipse**:
> If you are not using a recent Eclipse version (i.e. _Neon_ or later), enable assertions in JUnit tests
  as described [here](http://stackoverflow.com/questions/2522897/eclipse-junit-ea-vm-option).

* To run all tests, right-click on the `src/test/java` folder and choose
  `Run as` > `JUnit Test`
* To run a subset of tests, you can right-click on a test package, test class, or a test and choose
  to run as a JUnit test.
  [//]: # "@@author A0147944U"
* To check for code coverage by JUnit tests or manually using the TaskManager, right-click on the `src/test/java` folder and choose
  `Coverage as` > either `JUnit Test` or `Java Application`, depending on which is needed
  [//]: # "@@author"

**Using Gradle**:
* See [UsingGradle.md](UsingGradle.md) for how to run tests using Gradle.

We have two types of tests:

1. **GUI Tests** - These are _System Tests_ that test the entire App by simulating user actions on the GUI. 
   These are in the `guitests` package.
  
2. **Non-GUI Tests** - These are tests not involving the GUI. They include,
   1. _Unit tests_ targeting the lowest level methods/classes. <br>
      e.g. `seedu.task.commons.UrlUtilTest`
   2. _Integration tests_ that are checking the integration of multiple code units 
     (those code units are assumed to be working).<br>
      e.g. `seedu.task.storage.StorageManagerTest`
   3. Hybrids of unit and integration tests. These test are checking multiple code units as well as 
      how the are connected together.<br>
      e.g. `seedu.task.logic.LogicManagerTest`
  
**Headless GUI Testing** :
Thanks to the [TestFX](https://github.com/TestFX/TestFX) library we use,
 our GUI tests can be run in the _headless_ mode. 
 In the headless mode, GUI tests do not show up on the screen.
 That means the developer can do other things on the Computer while the tests are running.<br>
 See [UsingGradle.md](UsingGradle.md#running-tests) to learn how to run tests in headless mode.
  
## Dev Ops

### Build Automation

See [UsingGradle.md](UsingGradle.md) to learn how to use Gradle for build automation.

### Continuous Integration

We use [Travis CI](https://travis-ci.org/) to perform _Continuous Integration_ on our projects.
See [UsingTravis.md](UsingTravis.md) for more details.

[//]: # "@@author A0147944U"
### Review code quality

We use [Codacy](https://www.codacy.com/) to perform code review of common security concerns, code style violations, best practices, code coverage and other metrics to Codacy on our projects.

### Automated coverage reports

We use [Coveralls](https://coveralls.io/) on top of EclEmma to generate JUnit tests coverage reports as the development progresses.

### Making a Release

Here are the steps to create a new release.
 
 1. Generate a JAR file [using Gradle](UsingGradle.md#creating-the-jar-file). (simplified by [provided batch file](#generate-jar-file) on windows)
 2. Tag the repo with the version number. e.g. `v0.1`
 2. [Crete a new release using GitHub](https://help.github.com/articles/creating-releases/) 
    and upload the JAR file your created.

#### Generate .jar file

Run `Generate Jar build.bat` located in root folder. Alternatively, run `gradlew shadowJar` via terminal in the project root folder. More info on [using Gradle](UsingGradle.md#creating-the-jar-file). TaskManager.jar generated will be found in `build/jar`.

#### Prepare for gitpush/Clean files

Run`Clean_and_prepare_for_gitpush.bat` located in root folder. This will clean up files created by running the application via Eclipse, collate files using [Collate-TUI.jar](https://github.com/collate/collate) and then generating a new .jar file.
[//]: # "@@author"

### Managing Dependencies

A project often depends on third-party libraries. For example, TaskManager depends on the
[Jackson library](http://wiki.fasterxml.com/JacksonHome) for XML parsing. Managing these _dependencies_
can be automated using Gradle. For example, Gradle can download the dependencies automatically, which
is better than these alternatives.<br>
a. Include those libraries in the repo (this bloats the repo size)<br>
b. Require developers to download those libraries manually (this creates extra work for developers)<br>

[//]: # "@@author A0147944U"
## Appendix A : User Stories

Priorities: High (must have) - `* * *`, Medium (nice to have)  - `* *`,  Low (unlikely to have) - `*`


Priority | As a ... | I want to ... | So that I can...
-------- | :-------- | :--------- | :-----------
`* * *` | new user | see usage instructions | refer to instructions when I forget how to use the App
`* * *` | user | add a new task |
`* * *` | user | delete a task | remove task that I have done
`* * *` | user | edit a task | make changes to task parameters
`* * *` | user | list all tasks | view all tasks in the TaskManager
`* * *` | user | find a task by name | filter out tasks without having to go manually through the entire list
`* *` | new user | not follow a strict format for inputs | freely call commands without having to memorize all the formats
`* *` | user | set a task to be recurring | automate task creation process for tasks that I will constantly add
`* *` | user | undo last action | remove changes made by the last command
`* *` | user | be flexible in command inputs | easily key in commands without remembering specific formats
`* *` | user | backup my data elsewhere | save the current data into another data file for future use
`* *` | power user | access another data file | have mutiple users/profiles or access an older backup file
`* *` | user with many tasks in the task manager | sort tasks by a given parameter | view tasks in a way more benefical for planning
`*` | user | seperate completed and incomplete tasks | Keep track of which items are done and which are yet to be done
`*` | user | have auto-fill | easily key in commands without remembering the formats

## Appendix B : Use Cases

(For all use cases below, the **System** is the `TaskManager` and the **Actor** is the `user`, unless specified otherwise)

### Use case: Show help page

**MSS**

1. User requests to view help page (via help command, <kbd>F1</kbd> key, or menu option) 
2. Task Manager opens help page <br>
Use case ends


### Use case: Add task

**MSS**

1. User requests to add a task with given parameters
2. Task Manager successfully adds task, showing parsed parameters in the message panel and added task in task list panel<br>
Use case ends

**Extensions**

1a. Task of same parameters have been added before

> 1a1. Task Manager shows an error message stating that the task already exists <br>
  Use case ends

1b. Parameters given are invalid

> 1b1. Task Manager shows an error message <br>
  Use case ends


### Use case: Edit task

**MSS**

1. User requests to edit task with given parameters
2. Task Manager successfully updates task, showing affected task in the message panel and updates accordingly in task list panel <br>
Use case ends

**Extensions**

1a. The given index is invalid

> 1a1. Task Manager shows an error message stating provided index is invalid <br>
  Use case ends

1b. Parameters given are invalid

> 1b1. Task Manager shows an error message <br>
  Use case ends
  
  
### Use case: Delete task

**MSS**

1. User requests to delete a specific task in the list
2. Task Manager deletes the task and updates accordingly in task list panel <br>
Use case ends

**Extensions**

1a. The list is empty

> Use case ends

1b. The given index is invalid

> 1b1. Task Manager shows an error message stating provided index is invalid <br>
  Use case ends


### Use case: Find Task(s)

**MSS**

1. User requests to list tasks containing a KEYWORD
2. Task Manager shows tasks containing the KEYWORD <br>
Use case ends

### Use case: List tasks

**MSS**

1. User requests to list tasks
2. Task Manager successfully list tasks, sorted based on how it was last sorted <br>
Use case ends


### Use case: Sort tasks

**MSS**

1. User requests to sort tasks by default or given parameter
2. Task Manager successfully sorts based on user's input <br>
Use case ends

**Extensions**

1a. The given parameter is invalid

> 1a1. Task Manager shows an error message stating that the parameter is invalid and provides the list of valid parameters <br>
  Use case ends

### Use case: Undo last command

**MSS**

1. User requests to undo last command or multiple undo command
2. Task Manager successfully reverts to previous state, showing command undone in message panel <br>
Use case ends

**Extensions**

1a. No more commands to undo

> 1a1. Task Manager shows an error message stating that there are no more commands to undo <br>
  Use case ends


### Use case: Mark task as done

**MSS**

1. User requests to mark a task as done
2. Task Manager successfully updates task, showing affected task in the message panel and highlighting affected task in green in task list panel <br>
Use case ends

**Extensions**

1a. Task is already marked as done

> 1a1. Task Manager shows an error message stating that the task is already marked as done <br>
  Use case ends

1b. The given index is invalid

> 1b1. Task Manager shows an error message stating provided index is invalid <br>
  Use case ends


### Use case: Mark task as not done

**MSS**

1. User requests to mark a task as not done
2. Task Manager successfully updates task, showing affected task in the message panel and green highlight in task list panel disappears <br>
Use case ends

**Extensions**

1a. Task is already marked as not done

> 1a1. Task Manager shows an error message stating that the task is already marked as not done <br>
  Use case ends

1b. The given index is invalid

> 1b1. Task Manager shows an error message stating provided index is invalid <br>
  Use case ends

[//]: # "@@author A0147335E"
### Use case: Mark task as favorite

**MSS**

1. User requests to mark a task as favorite
2. Task Manager successfully updates task, showing affected task in the message panel and highlighting affected task in yellow in task list panel <br>
Use case ends

**Extensions**

1a. Task is already marked as favorite

> 1a1. Task Manager shows an error message stating that the task is already marked as favorite <br>
  Use case ends

1b. The given index is invalid

> 1b1. Task Manager shows an error message stating provided index is invalid <br>
  Use case ends

[//]: # "@@author A0147335E"
### Use case: Mark task as not favorite

**MSS**

1. User requests to mark a task as not favorite
2. Task Manager successfully updates task, showing affected task in the message panel and yellow highlight in task list panel disappears <br>
Use case ends

[//]: # "@@author"
**Extensions**

1a. Task is already marked as not favorite

> 1a1. Task Manager shows an error message stating that the task is already marked as not favorite <br>
  Use case ends

1b. The given index is invalid

> 1b1. Task Manager shows an error message stating provided index is invalid <br>
  Use case ends
  
### Use case: Set a task to repeat automatically

**MSS**

1. User requests to set a task to repeat automatically
2. Task Manager successfully updates task, showing affected task in the message panel <br>
Use case ends

**Extensions**

1a. The given interval is invalid

> 1a1. Task Manager shows an error message stating provided interval is invalid, listing valid intervals <br>
  Use case ends

1b. The given index is invalid

> 1b1. Task Manager shows an error message stating provided index is invalid <br>
  Use case ends

### Use case: Backup Data

**MSS**

1. User requests to backup current data at specified location
2. Task Manager saves data at specified location
3. Task Manager shows location where data is saved at in message panel <br>
Use case ends

**Extensions**

1a. The user don't enter a filepath

> 1a1. User is provided with a filepicker <br>
> 1a2. User selects filepath <br>
  Use case resumes at step 2

> > 1a2a1. User exits filepicker <br>
> > 1a2a2. Task Manager shows a message informing the backup command has been aborted <br>
    Use case ends

1b. The user don't enter a valid file path

> 1b1. Task Manager shows an error message <br>
  Use case ends

 2a. Specified location is inaccessible by TaskManager or does not exist
 
 > 2a1. Task Manager shows an error message <br>
  Use case ends


### Use case: Access Another Data File

**MSS**

1. User requests to access data at specified location
2. Task Manager loads data at specified location.
3. Task Manager restarts<br>
Use case ends

**Extensions**

1a. The user don't enter a filepath

> 1a1. User is provided with a filepicker <br>
> 1a2. User selects filepath <br>
  Use case resumes at step 2

> > 1a2a1. User exits filepicker <br>
> > 1a2a2. Task Manager shows a message informing the directory command has been aborted <br>
    Use case ends

1b. The user don't enter a valid file path

> 1b1. Task Manager shows an error message <br>
  Use case ends

 2a. Specified file is inaccessible by TaskManager or does not exist
 
 > 2a1. Task Manager shows an error message <br>
  Use case ends


### Use case: Clear tasks

**MSS**

1. User requests to clear tasks
2. Task Manager successfully clear tasks, showing success message in the message panel and the updated task list in the task list panel <br>
Use case ends


### Use case: Refresh tasks

**MSS**

1. User requests to refresh tasks
2. Task Manager successfully refreshes tasks, showing success message in the message panel and the updated task list in the task list panel <br>
Use case ends


### Use case: Exit TaskManager

**MSS**

1. User requests to exit TaskManager
2. Task Manager shuts down <br>
Use case ends


## Appendix C : Non Functional Requirements

1. Should work on any [mainstream OS](#mainstream-os) as long as it has Java `1.8.0_60` or higher installed.
2. Should be able to hold at least to 1000 tasks.
3. Should come with automated unit tests and open source code.
4. Should function fully offline.
5. Should process a command within 2 seconds on a modern computer.

## Appendix D : Glossary

##### Mainstream OS

> Windows, OS-X.
[//]: # "@@author"

##### Private task detail

> A task detail that is not meant to be shared with others

[//]: # "@@author A0147335E"
## Appendix E : Product Survey

   | Todoist| Any.Do | Wunderlist
-------- | :-------- | :--------- | :-----------
`Platforms` | All platforms | iOS, Android, Google Chrome | iPhone, iPad, Android, Windows, Kindle, Web
`Price` | Free / Premium ($28.99/year) | Free / Premium ($45/year or $5 per month) | Free / Pro ($59.88/year)
`Pros` | Good user interface<br>Fastest task manager app Works offline<br>Can set priorities by choosing from 1 to 4. <br>Can drag and drop tasks <br>Can create a Project with emojis in its name  Supports text formatting| Minimalistic design Can Star a task to mark it as high priority Runs at high speed Has in-built speech recognition <br>Can drag and drop tasks <br>
Can swipe a task to the right to mark it as complete<br> Can swipe a completed task to the left to add it again to the list. <br>Can attach files from your Dropbox and Google Drive  | Simple-to-use <br>Can Star a task to set a priority to a task Hashtags feature<br> Can add comments and attachments Supports email reminder Powerful search function Can restore deleted lists. <br>Can manually create backups and import data from your backups.<br> Can attach files from your Dropbox.`Cons` | The key features are all part of premium plan <br>Not compatible with Windows Phone Search function is limited in free plan <br>Doesn't have a backup option for free users | Not intuitive to use Doesn't hide a task when it's marked as complete.<br> Doesn't support email reminders Doesn't have a Windows app.<br>  Doesn't have a backup option. Doesn't support emojis in your list names. Doesn't support markdown.| Slow to load <br>Doesn't show last synced time <br>Doesn't support text formatting
[//]: # "@@author"
