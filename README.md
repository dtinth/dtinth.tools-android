# dtinth’s Tools

An personal Android application that I use on my device.

## Morse code notifier

Whenever a notification is received, vibrate the device with a morse code pattern of the first 3 initials. For example, if you receive a notification with the title "Hello, world!", the device will vibrate with the pattern ".... .--" (H W).

When receiving a chat message, usually the notification title is the name of the sender. So you can guess who sent the message from the vibration pattern.

To enable: **Settings** &rarr; **Apps** &rarr; **⋮** &rarr; **Special access** &rarr; **Notification access** and enable **dtinth’s Tools**.

## Notification exfiltrator

Sends notifications received on the phone to an external server. Notification data is [sealed](https://libsodium.gitbook.io/doc/public-key_cryptography/sealed_boxes). The private key corresponding to the configured public key is required to decrypt the data.
