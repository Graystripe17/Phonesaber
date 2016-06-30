# Phonesaber Unlock: Turn your Android into a Phonesaber!

Ad-free, open-sourced, no copyright infringement intended.
Cinnamint and Minty Apps do not claim ownership of audio files in this repository.

-----

Phonesaber Unlock can run on androids as low as API 8. On/Off sound effects are a result of `UpdateService.java`. Since Android discourages third party apps from accessing the intent filters `ACTION_SCREEN_ON` and `ACTION_SCREEN_OFF`, they are kept as a service and declared in the manifest.

High Memory Mode launches a Foreground service, bypassing receiver.java. Low Memory Mode starts a BroadcastReceiver.

Tertiary sound effects from `ACTION_USER_PRESENT` are from a BroadcastReceiver that lives in the MainActivity (lifecycle not guaranteed).


-----

Recently additional resource management has been added to the repository. Phonesaber was responsible for interfering with Media playback in other apps, and this issue has a resolution is currently being tested and will roll out to the Google Play Store soon. Thank you for your patience.

-----

If you would like to contribute, please submit a pull request.
If you have found a bug, please list it under the issue tracker.
