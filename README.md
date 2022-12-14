# First-Time-Amplify
---

### Goal

1. Get a knowledge how to create an Android application using AWS Amplify.
2. How to connect services in AWS together,
3. Improve understanding AWS Tools and Services: Management console, IAM, Cognito, S3, AWS Budgets.

### Screenshots of application

![Start screen](app/src/main/res/drawable-v24/screenshot_start.jpg)
![Sign up](app/src/main/res/drawable-v24/screenshot_create_account.jpg)
![Add note](app/src/main/res/drawable-v24/screenshot_add_note.jpg)
![Added note view](app/src/main/res/drawable-v24/screenshot_added_notes.jpg)
![Delete note](app/src/main/res/drawable-v24/screenshot_delete_note.jpg)

### Screenshots of AWS Services

![Cognito](app/src/main/res/drawable-v24/screenshot_cognito_user_pool.jpg)

![S3 Bucket](app/src/main/res/drawable-v24/screenshot_s3_bucket.jpg)

![S3 Deployment](app/src/main/res/drawable-v24/screenshots_amplify_deployment.jpg)

### Overview

This is a demo application which has been done based on documentation available here: [Build Android App Amplify](https://aws.amazon.com/getting-started/hands-on/build-android-app-amplify/)

The app includes:
1. Authentication to the app allowing users to sign up, sign in, and manage their account,
2. A scalable GraphQL API configured with an Amazon DynamoDB database allowing users to create and
   delete notes,
3. A file storage using Amazon S3 allowing users to upload images and view them in their app.

It's possible to reuse or to delete the backend created in this project: [Check here - bottom of the page](https://aws.amazon.com/getting-started/hands-on/build-android-app-amplify/module-five/)
