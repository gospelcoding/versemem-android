versemem-android
================

Yet another Bible verse memorization app for Android

TO-DO List

*NewVerseActivity
  -(done) Simple interface built, needs code to download selected verse
  -translation option
  -option for multiple verse selection

*Database design
  -(Done) Need names of books, numbers of chapters and numbers of verses stored in local db
  -Need to figure out how to set up db on first run


*Alternate checker activities
  -Talk into microphone and check yourself
  -Flashcard style check yourself


*QuizActivity
  -complete interface for after user submits verse (right now only says success or fail)
  -provide retry option after failing a verse


*VerseListActivity
  -make something happen when I click a verse
  -possibly refactor way the list is created (probably don't need to create a Verse object for each verse just to make a list)



*PreferencesActivity
  -create this
  -should include default translation, how many quizes per day and what time, how to be checked, how to be notified of quiz (sound, vibrate or nothing), auto-merge option


*QuizNotificationService
  -(done) create
  -rest of the work for this should concentrate on PreferencesActivity
  -check my work? I know it works, but those Intents and Contexts are confusing and I'm not sure it's 100% right

*Verse-Merge
  -feature to learn long passages in short segments which can be merged together once each is mastered
  -can happen automatically for adjacent verses or manually
  -verses stay merged when status changed to Refreshing, but should be separated if kicked back to Learning
