# Timesheets

The Timesheets application is a time tracking system developed and in practical use  as a companywide time management tool by [Haulmont] (http://www.haulmont.com/). The application is based on [CUBA platform](https://www.cuba-platform.com/).

There are 2 optional modules within the application:

* Reports
* Charts

The application consists of:

* Data Model
    * 13 entities 
    * 5 enumerations
* Middleware
    * 1 entity listener
    * 3 services
* User Interface
    * 35 screens

This particular showcase application illustrates a 3rd party Vaadin visual component integration.

Based on CUBA Platform 6.4.2

## Data Structure

The application introduces the following entities:

* *Client*; has name, code and several associated Projects.
* *Project*; has name, code, description, status and also links to the parent project (thus projects are hierarchical). *Project* can have several Tasks and several Project Participants.
* The *Task* entity is a high-level description of staff activity. It has name, code, description, status, etc.
* The *Project Participant* entity links a system user with a project. It also represents *Project Role*, which is described below.
* *Project Role* defines the user abilities. The system has 4 pre-defined project roles:
    * *Manager* - manages projects: creates/modifies/removes tasks, adds/removes participants, approves timesheets of participants. Can also submit timesheets.
    * *Approver* - approves participants’ timesheets. Can also submit timesheets.
    * *Worker* - only able to submit timesheets.
    * *Observer* - cannot create/modify any data (including timesheets), but can observe the project and all submitted timesheets.
* *Timesheet* is represented as a set of *Time Entry* entries; each of them is a description of the time spent on a particular task.
* *Task Type* - a task can refer a specific task type. For instance, Development, Testing, Support, etc.
* *Activity Type* - a time entry can refer to some activity type. For instance, Development, Bug fixing, Refactoring, etc.
* *Tag Type* and *Tag* - a time entry can refer to several Tags. Tags are grouped into Tag Types. Tags might be useful if there is a need to add some information to the time entry, but there is not an existing special field for it. As Tag Types are linked with projects, it is possible to make the information project specific.

## Timesheets submission

Users create *Time Entries* for certain tasks.

There are several ways to create time entries:

1.	*Weekly Timesheets* screen - allows creating time entries for the whole week at once.

2.	*Calendar* screen - allows creating time entries for the whole month, or for certain dates.

3.	*My Tasks* screen allows creating time entries for certain tasks.

4.	*My Time Entries* screen allows creating time entries for any date.

    All the listed screens (except the *My Tasks* screen) have the `CommandLine` component, which enables the bulk creation of timesheets using simple text commands.

    For example, **@Platform #Development 4h30m** on the Weekly Timesheets screen will fill the whole week (workdays only) with the time entries for the Development task, with spent time = 4 hours and 30 minutes

## Timesheets approval process

Often timesheets are used for the calculation of wages. In this case, some companies might have a "timesheets approval" process, where a responsible person approves the submitted timesheets.

This application supports such a process out of the box. The *Timesheet Approval* screen allows project managers and approvers, to approve or reject the submitted timesheets.

## Other features

Lori Timesheets inherits all features of [CUBA platform](https://www.cuba-platform.com/en/): reporting, charts, data audit and recovery, scheduling, etc.

## Usage
Open the project in CUBA Studio and execute *Run > Create database*, then *Run > Start application server*. The application will start at `http://localhost:8080/app`. Use *admin* as both login and password to access the application.
