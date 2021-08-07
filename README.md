# dtinth’s Tools

**A personal Android enhancement app** that I use on my device.

- [**Morse code notifier**](#morse-code-notifier) vibrates my phone when I receive a notification.

- [**Notification exfiltrator**](#notification-exfiltrator) sends the received notifications into a remote server. It has end-to-end encryption, and aims to be resilient. The primary use case is to integrate with [my personal chatbot, expense tracker, and home automator, “automatron”](https://dt.in.th/automatron.html).

Sounds cool and want something like this? I encourage you to create your own personal Android enhancement app. Feel free to look at my code and reuse parts of it!

## Morse code notifier

Whenever a notification is received, vibrate the device with a morse code pattern of the first 3 initials. For example, if you receive a notification with the title "Hello, world!", the device will vibrate with the pattern ".... .--" (H W).

When receiving a chat message, usually the notification title is the name of the sender. So you can guess who sent the message from the vibration pattern.

To enable: **Settings** &rarr; **Apps** &rarr; **⋮** &rarr; **Special access** &rarr; **Notification access** and enable **dtinth’s Tools**.

## Notification exfiltrator

Sends notifications received on the phone to an external server.

- **End-to-end encryption:** Notification data is [sealed](https://libsodium.gitbook.io/doc/public-key_cryptography/sealed_boxes). The private key corresponding to the configured public key is required to decrypt the data.

- **Resilient:** [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) is used to schedule reliable, asynchronous tasks. Notifications are stored locally, in encrypted form, so that it survives app exit, network disconnection, and device restarts. They will be dispatched to the server when [the device is connected to the internet](https://developer.android.com/reference/androidx/work/NetworkType#CONNECTED). In case of server errors (5xx response), it will be retried with a backoff strategy. This is all handled by WorkManager.

### Integration guide

The following steps assume you have Node.js installed with these dependencies:

```sh
yarn add tweetnacl tweetnacl-sealedbox-js
```

**Key generation:** To generate a keypair, run in Node.js REPL:

```js
fs.writeFileSync('keys.json', JSON.stringify(Object.fromEntries(Object.entries(require('tweetnacl').box.keyPair()).map(x => [x[0], Buffer.from(x[1]).toString('base64')]))))
```

The `keys.json` file will look like this:

```json
{"publicKey":"Uk9KZm56CA4R73EcgvXw7bJe23XMKgVQEC8pE6aytxk="
,"secretKey":"yJ78TckijJSzd0+ZBDzIV+TO0jb722SSc9McM+l99MI="}
```

**Encrypted payload format:** The notification data is sent with `Content-Type: text/plain` through an HTTP POST request to the configured URL. The body of the request is the encrypted notification data, represented as a hex-encoded string:

```
84F19300DDF5CF7C7907E131882D37A1E33FF31142FDEB708DCEE04385A3C468A4FFF17A94B6D54FC99DDB2DBCFC784ABBBED416CB778733375037430345B5E42FEC8443900FD46F345696CC12B9DCE377E2BF5507C779CC66D12CBD50944F3D0792D8B7BA7B24FC49A6D4CA6D96F8F87FC936EC992EF255A4D326F86D97602BB9BBE32D2EA97293630620A383AE6D9FE527EF1B7672936FBEB12B69
```

This script can be used to decrypt the payload:

```js
var sealedbox = require('tweetnacl-sealedbox-js');
const keys = require('./keys.json')

var result = sealedbox.open(
  Buffer.from(process.argv[2], 'hex'),
  Buffer.from(keys.publicKey, 'base64'),
  Buffer.from(keys.secretKey, 'base64')
);

console.log(Buffer.from(result).toString());
```

**Decrypted payload format:** It is a JSON object.

```json
{"packageName":"net.superblock.pushover"
,"title":"test"
,"text":"sawasdee"
,"time":"2021-08-07T08:59:24.909Z"}
```