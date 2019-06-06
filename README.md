# Kurakani
A real-time chat application built for the employees of the Government of Sikkim. The app is built using Node.js, Express, Mongoose, Socket.io, Passport, Crypto with an Android Interface built using Android Studio.

# Index
+ [Installation](#installation)
+ [Getting Started with the Application](#getting_started_with_the_application)
+ [Deploying the Android Application](#deploying_the_android_application)
+ [How to use the Application](#how_to_use_the_application)
+ [How it Works](#how_it_works)

## Installation <a name="installation"></a>
### Running Locally
#### Prerequisites 
Installing NodeJs
```
$ sudo apt-get install nodejs
```
Installing MongoDB
```
$ sudo apt install -y mongodb
```
#### Running the code
1. Clone or Download the repository
```
$ git clone https://github.com/kylelobo/NotsApp.git
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
Your admin app should now be running on http://localhost:3020/

### Getting Started with the Application <a name="getting_started_with_the_application"></a>
1. Create a super admin in mongo shell
2. Login to the [webportal](http://localhost:3020) with the created Super Admin credentials.
3. Create Departments and assign Admins to these departments.

### Deploying the Android Application <a name="deploying_the_android_application"></a>
1. Install and get started with Android Studio using https://developer.android.com/studio/index.html
2. Open the application source code in Android Studio.
3. Change the IP address the application will connect to, to the IP address your server will run on.
4. Connect a phone with debugging mode enabled to your system and install the app in the phone using Android Studio.
5. Resgister / login to the app.

### How to use the application <a name="how_to_use_the_application"></a>
1. Login with your credentials.
2. Explore the various features of the application like sessions invites, session accept / decline.
3. Create broadcasts, view your profile, etc.

### How it works <a name="how_it_works"></a>
1. Uses Socket.io for real-time and seamless transmission of texts and files.
2. Uses AES Encryption for messages.
3. Uses SHA512 Hashing techniques for passwords to render anyone unable to ever figure it out.
