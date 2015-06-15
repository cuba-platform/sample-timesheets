The application is designed for timesheets submission and management.

Based on CUBA Platform 5.5.0

## Data Structure

The application introduces the following entites:

* *Client* has name, code and several associated *Projects*.
* *Project* has name, code, description, status and also link to parent project (so projects are hierarchical). *Project* can have several *Tasks* and several *Project Participants*.
* The *Task* entity is a high-level description of staff activity. It has name, code, description, status, etc.
* The *Project Participant* entity links a system user with a project. It also contains *Project Role*.
* *Project Role* defines the user abilities. The system has 4 pre-defined project roles: 
    * Manager - manages projects: creates/modifies/removes tasks, adds/removes participants, approves timesheets of participants. Also can sumbit timesheets.
    * Approver - approves participants timesheets. Also can sumbit timesheets.
    * Worker - can only submit thimesheets.
    * Observer - can not create/modify any data (including timeseets), but can observe the project and all submitted timesheets.
* Timesheet is represended as a set of *Time Entry* enties, each of them is a description of the time spent for some task.
* *Task Type* - a task can refer to some task type. For instance, Development, Testing, Support, etc.
* *Activity Type* - a time entry can refer to some activity type. For instance, Development, Bug fixing, Refactoring, etc.
* *Tag Type* and *Tag* - a time entry can refer to several *Tags*. *Tags* are grouped into *Tag Types*. *Tags* might be useful if you want to add some information to the time entry, but do not have special field for it. Also, as *Tag Types* are linked with projects, you can make the information project specific. 
For example, we have 2 projects: Internal project and SomeClient project. For each timesheet, SomeClient requires a certain cost code from the pre-defined list. In this case we create *Tag Type* linked with SomeClient project and add the cost codes there (as tags). Fot the internal project we don't need the cost codes so the tags from the tag type are not visible there. 

## Timesheets submission

Users create *Time Entries* for certain tasks. 

There are several ways to create time entries:

1. *Weekly Timesheets* screen - allows to create time entries for the whole week at once.
2. *Calendar* screen - allows to create time entries for the whole month or for certain date.
3. *My Tasks* screen allows to create time entries for certain task.
4. *My Time Entries* screen alows to create time entries for any date.

All the listed screens (except the *My Tasks* screen) have the `CommandLine` component, which enables bulk creation of timesheets using simple text commands.

For example, **@Platform #Development 4h30m** on the Weekly Timesheets screen will fill the whole week (workdays only) with the time entries for Development task with spent time = 4 hours and 30 minutes 

## Timesheets approval process

Often timesheets are used to pay wages for the staff. In this case some companies use the process named "timesheets approval", when a responsible person approves the submitted timesheets.

The application supports the process out of the box. The *Timesheet Approval* screen allows project managers and approvers to approve or reject the timesheets.

## Other features

As any application based on CUBA platform, the Timesheets application supports all platform features: reporting, charts, data audit and recovery, scheduling, etc.

## Usage

Open the project in CUBA Studio and execute *Run > Create database*, then *Run > Start application server*. Login to the application at `http://localhost:8080/app` with *admin* user name and *admin* password.
 