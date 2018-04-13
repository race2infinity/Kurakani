# Kurakani
A Real Time Chat Application built using Node.js, Express, Mongoose, Socket.io, Passport, crypto with an Android interface built using Android Studio.

# Index
+ [Installation](#installation)
+ [Getting Started with the Application](#gswta)
+ [Deploying the Android Application](#dtaa)
+ [How to use the Application](#htuta)
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



### Getting Started with the Application<a name="gswta"></a>
1. Create a super admin in mongo shell
2. Login to the webportal[localhost:3020](http://localhost:3020) with the created Super Admin credentials.
3. Create Departments and assign Admins to these departments.

### Deploying the Android Application<a name="dtaa"></a>
1. Install and get started with Android Studio using https://developer.android.com/studio/index.html
2. Open the application source code in Android Studio.
3. Change the IP address the application will connect to, to the IP address your server will run on.
4. Connect a phone with debugging mode enabled to your system and install the app in the phone using Android Studio.
5. Resgister/Login to the app.


### How to use the application<a name="htuta"></a>
1. Login with your credentials.
2. Explore the various features of the application like sessions invites, session accept/decline.
3. Create broadcasts, view your profile, etc.


### How it works<a name="howitworks"></a>
1. Uses socket.io for real-time and seamless transmission of texts and files.
2. Uses AES Encryption for messages.
3. Uses SHA512 Hashing techniques for passwords to render anyone unable to ever figure it out.































