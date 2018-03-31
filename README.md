# Kurakani
A Real Time Chat Application built using Node.js, Express, Mongoose, Socket.io, Passport, crypto with an Android interface built using Android Studio.

# Index
+ [Installation](#installation)
+ [How it Works](#howitworks)




## Installation<a name="installation"></a>
### Running Locally
Make sure you have [Node.js](https://nodejs.org/) and [npm](https://www.npmjs.com/) installed.
+ Install and get started with MongoDB using https://docs.mongodb.com/manual/installation/
1. Clone or Download the repository

	```
	$ git clone https://github.com/Kodery8/NotsApp.git
	$ cd NotsApp
  $ cd ChatApp
	```
2. Install Dependencies

	```
	$ npm install
	```
3. Start the application

  ```
  $ npm start
  ```
Your admin app should now be running on [localhost:3020](http://localhost:3020/).



### Getting Started with the Application
1. Create a super admin in mongo shell
2. Login to the webportal[localhost:3020](http://localhost:3020) with the created Super Admin credentials.
3. Create Departments and assign Admins tothese departments.

### Deploying the Android Application
1. Install the apk from the store.
2. Register yourself(create account) if it is your first time using the application.
3. Login with your credentials if your accoutn already exists.


### How to use the application
1. Login with your credentials.
2. Explore the various features of the application like sessions invites, session accpet/decline.
3. Create broadcasts, view your profile, etc.


### How it works 
1. Uses socket.io for real-time and seamless transmission of texts and files.
2. Uses AES Encryption for messages.
3. Uses SHA512 Hashing techniques for passwords to render anyone unable to ever figure it out.































