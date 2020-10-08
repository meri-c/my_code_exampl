## * This repo holds only code PARTS, to show an example of my skills, so it WILL NOT run on your pc.

------

# These code examples are taken from the inventory project.


### The main problem:

There are lots of vary equipment and techniques in the government structure.
And almost every week you can get some new furniture, computers etc.
Also some people can change their workplaces or
can be transferred to a different district.


### The idea of the solution:

Since all of the information above keep tracking in the separated systems by different departments,
we decided to combine each part of tracking info in one shared app.

### The realization:
main.model.Inventory project has all the information about the equipment, users and people, who are responsible for that.

There is a qr-code on each item so every logged in user in app (android, java) can see the information:
what is it, who is the owner, in which room it should be.

In the same app you can mark an item transferring or the owner changing.
You can run an inventory check, in a format "what should be here, what did we find, report about missing elements".
The excel result of the inventory check saves at the server side and is accessible for a bookkeeping and accounting
department.


#### The frontend part (angular, material bootstrap) is 
for admins to fill the db, check stored items, see any equipment transferring,
 it's history.
To create equipment data and generate qr-code(s) for printing. To control workers and users data.

#### The backend part (java, spring-boot, mybatis, mysql) is
 storing in db, api handling, managing and converting data,
creates documents.

##### All of CI/CD scripts (bitbucket) and the part of a devops work(jenkins, docker, linux server setup) unfortunately can not be shown here.


## All the additional info about the project parts you can read inside the modules. 
## Android app execution you can see in the following folder in pdf file

### Thanks for attention
